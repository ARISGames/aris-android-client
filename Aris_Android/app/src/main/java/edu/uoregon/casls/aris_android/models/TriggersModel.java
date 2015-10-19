//package edu.uoregon.casls.aris_android.models;
package groupsmodel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class TriggersModel extends ARISModel {

	public Map<Long, Trigger> triggers = new HashMap<>();
	public List<Trigger> playerTriggers = new ArrayList<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		triggers.clear();
		n_game_data_received = 0;
	}

//	public void clearGameData() {
//		this.clearPlayerData();
//		triggers  = [[NSMutableDictionary alloc] init];
//		blacklist = [[NSMutableDictionary alloc] init];
//		n_game_data_received = 0;
//	}

	public void clearPlayerData() {

		playerTriggers.clear();
		n_player_data_received = 0;
	}

	public void requestPlayerData() {

		this.requestPlayerTriggers();
	}

	public long nPlayerDataToReceive() {

		return 1;
	}

	public void requestGameData() {

		this.requestTriggers();
	}


	public long nGameDataToReceive() {

		return 1;
	}

	public void triggersReceived(List<Trigger> newTriggers) {

		this.updateTriggers(newTriggers);
	}

	public void triggerReceived(NSNotification notif) {

		//TODO unsure of noticications
		this.updateTriggers:@[notif.userInfo[@"trigger"]]];
	}

	public void updateTriggers(List<Trigger> newTriggers) {

		Trigger newTrigger;
		//TODO unsure of NSNumber
		NSNumber *newTriggerId;
		List<Trigger> invalidatedTriggers = new ArrayList<>();

		int size = newTriggers.size();
		for (int i = 0; i < size; i++)
		{
			newTrigger = newTriggers[i];
			newTriggerId = newTrigger.trigger_id;

			if (!triggers.get(newTriggerId)) {

				triggers.put(newTriggerId, newTrigger);
				blacklist.remove(newTriggerId);
			}
			//TODO the following line may be incorrect
			else if (!triggers.get(newTriggerId).mergeDataFromTrigger(newTrigger)) {

				invalidatedTriggers.add(triggers.get(newTriggerId));
			}
		}
		if (invalidatedTriggers.size()) {
			//TODO unsure of final argument, name for all such funtions
			_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}

		n_game_data_received++;
		_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_AVAILABLE", null, null);
		_ARIS_NOTIF_SEND_("MODEL_GAME_PIECE_AVAILABLE", null, null);
	}

	public List<Trigger> conformTriggersListToFlyweight(List<Trigger> newTriggers) {

		List<Trigger> conformingTriggers = new ArrayList<>();
		List<Trigger> invalidatedTriggers = new ArrayList<>();

		int size = newTriggers.size();
		for (int i = 0; i < size; i++) {

			Trigger newt = newTriggers[i];
			Trigger exist = this.triggerForId(newt.trigger_id);

			if (exist) {
				if (!exist.mergeDataFromTrigger(newt)) {
					invalidatedTriggers.add(exist);
				}
				conformingTriggers.add(exist);
			}
			else {
				triggers.put(newt.trigger_id, newt);
				conformingTriggers.add(newt);
			}
		}
		if (invalidatedTriggers.size()) {
			_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}
		return conformingTriggers;
	}

	public void playerTriggersReceived(NSNotification notif) {

		this.updatePlayerTriggers(this.conformTriggersListToFlyweight(notif.userInfo[@"triggers"]));
	}

	public void updatePlayerTriggers(List<Trigger> newTriggers) {

		List<Object> addedTriggers = new ArrayList<>();
		List<Object> removedTriggers = new ArrayList<>();

		//placeholders for comparison
		Trigger newTrigger;
		Trigger oldTrigger;

		//find added
		boolean flag;
		int isize = newTriggers.size();
		int jsize = playerTriggers.size();

		for (int i = 0; i < isize; i++) {

			flag = true;
			newTrigger = newTriggers[i];

			for (int j = 0; j < jsize; j++) {
				oldTrigger = playerTriggers[j];
				if (newTrigger.trigger_id == oldTrigger.trigger_id) {
					flag = false;
				}
			}
			if (flag) {
				addedTriggers.add(newTriggers[i]);
			}
		}

		//find removed
		boolean removed;
		for (int i = 0; i < jsize; i++) {

			removed = true;
			oldTrigger = playerTriggers[i];

			for (int j = 0; j < isize; j++) {

				newTrigger = newTriggers[j];

				if (newTrigger.trigger_id == oldTrigger.trigger_id) {
					removed = false;
				}
			}
			if (removed) {
				removedTriggers.add(playerTriggers[i]);
			}
		}

		playerTriggers = newTriggers;
		n_player_data_received++;

		if (addedTriggers.size() > 0) {
			_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_NEW_AVAILABLE", null, @{@"added":addedTriggers});
		}
		if (removedTriggers.size() > 0) {
			_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_LESS_AVAILABLE", null, @{@"removed":removedTriggers});
		}
		_ARIS_NOTIF_SEND_("MODEL_PLAYER_TRIGGERS_AVAILABLE", null, null);
		_ARIS_NOTIF_SEND_("MODEL_GAME_PLAYER_PIECE_AVAILABLE", null, null);
	}

	public void requestTriggers() {
		_SERVICES_.fetchTriggers();
	}
	public void requestTrigger(long t) {
		_SERVICES_.fetchTriggerById(t);
	}

	public void requestPlayerTriggers() {

		if (this.playerDataReceived() && !_MODEL_GAME_.network_level.equals("REMOTE")) {

			List<Trigger> rejected = new ArrayList<>();
			List<Trigger> ptrigs = new ArrayList<>();
			List<Trigger> ts = new ArrayList<>(triggers.values());

			int size = ts.size();
			for (int i = 0; i < size; i++) {
				Trigger t = ts[i];
				if (t.scene_id != _MODEL_SCENES_.playerScene.scene_id  || ![_MODEL_REQUIREMENTS_.evaluateRequirementRoot(t.requirement_root_package_id)) {
					continue;
				}
				//TODO unsure of instances
				Instance *i = [_MODEL_INSTANCES_ instanceForId:t.instance_id];

				if (!i) {
					continue;
				}
				if (i.factory_id) {
					Factory f = _MODEL_FACTORIES_.factoryForId(i.factory_id);
					if (!f) {
						continue;
					}
					//TODO unsure of time
					int time = [[NSDate date] timeIntervalSinceDate:i.created];
					NSLog(@"%d",time);
					if (time > f.produce_expiration_time) {
						rejected.add(i);
						continue;
					}
				}
				ptrigs.add(t);
			}
			//TODO unsure of logging
			NSLog(@"Accepted: %lu, Rejected: %lu",(unsigned long)ptrigs.count,(unsigned long)rejected.count);
			_ARIS_NOTIF_SEND_("SERVICES_PLAYER_TRIGGERS_RECEIVED", null, @{@"triggers":ptrigs});
		}
		if (!this.playerDataReceived() || _MODEL_GAME_.network_level.equals("HYBRID") || _MODEL_GAME_.network_level.equals("REMOTE")) {
			_SERVICES_.fetchTriggersForPlayer();
		}
	}

	// null trigger (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Trigger triggerForId(long trigger_id) {

		if (!trigger_id) {
			//TODO unsure of in situ alloc
			return [[Trigger alloc] init];
		}

		Trigger t = triggers.get(trigger_id);

		if (!t) {
			blacklist.put(trigger_id, "true");
			this.requestTrigger(trigger_id);
			//TODO unsure of in situ alloc
			return [[Trigger alloc] init];
		}
		return t;
	}

	public List<Trigger> triggersForInstanceId(long instance_id) {

		List<Trigger> a = new ArrayList<>();

		int size = triggers.size();
		for (long i = 0; i < size; i++) {

			Trigger t = triggers.get(i);

			if (t.instance_id == instance_id) {
				a.add(t);
			}
		}
		return a;
	}

	public Trigger triggerForQRCode(String code) {

		Trigger t;

		int size = playerTriggers.size();
		for (long i = 0; i < size; i++) {

			t = playerTriggers[i];
			if (t.type.equals("QR") && t.qr_code.equals(code)) {
				return t;
			}
		}
		return null;
	}

	public List<Trigger> playerTriggers() {
		return playerTriggers;
	}

	public void expireTriggersForInstanceId(long instance_id) {

		List<Trigger> newTriggers = new ArrayList<>();

		int size = playerTriggers.size();
		for (long i = 0; i < size; i++) {

			if (((Trigger )playerTriggers[i]).instance_id != instance_id) {

				newTriggers.add(playerTriggers[i]);
			}
		}
		this.updatePlayerTriggers(newTriggers);
	}

}
