package edu.uoregon.casls.aris_android.models;


import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.Config;
import edu.uoregon.casls.aris_android.data_objects.Factory;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class TriggersModel extends ARISModel {

	public Map<Long, Trigger> triggers = new HashMap<>();
	public List<Trigger> playerTriggers = new ArrayList<>();
	public GamePlayActivity mGamePlayAct;
	public Game mGame;
	public Map<Long, String> blacklist = new HashMap<>(); //list of ids attempting / attempted and failed to load

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public void clearGameData() {
		this.clearPlayerData();
		triggers.clear();
		blacklist.clear();
		n_game_data_received = 0;
	}

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

	public void triggerReceived(Trigger newTrigger) {

		List<Trigger> newTrigs = new ArrayList<>();
		newTrigs.add(newTrigger);
		this.updateTriggers(newTrigs);
	}

	public void updateTriggers(List<Trigger> newTriggers) {
		Long newTriggerId;
		List<Trigger> invalidatedTriggers = new ArrayList<>();

		for (Trigger newTrigger : newTriggers)
		{
			newTriggerId = newTrigger.trigger_id;

			if (!triggers.containsKey(newTriggerId)) {
				triggers.put(newTriggerId, newTrigger);
				blacklist.remove(newTriggerId);
			}
			else if (!triggers.get(newTriggerId).mergeDataFromTrigger(newTrigger)) {

				invalidatedTriggers.add(triggers.get(newTriggerId));
			}
		}
		if (!invalidatedTriggers.isEmpty()) {
			mGamePlayAct.mDispatch.model_triggers_available(); //("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}

		n_game_data_received++;
		mGamePlayAct.mDispatch.model_triggers_available(); //_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.model_game_piece_available();//_ARIS_NOTIF_SEND_("MODEL_GAME_PIECE_AVAILABLE", null, null);
	}

	public List<Trigger> conformTriggersListToFlyweight(List<Trigger> newTriggers) {
		List<Trigger> conformingTriggers = new ArrayList<>();
		List<Trigger> invalidatedTriggers = new ArrayList<>();
		int size = newTriggers.size();
		for (Trigger newt : newTriggers) {

			Trigger exist = this.triggerForId(newt.trigger_id);

			if (exist != null) {
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
		if (!invalidatedTriggers.isEmpty()) {
			mGamePlayAct.mDispatch.model_triggers_invalidated(invalidatedTriggers); //_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}
		return conformingTriggers;
	}

	public void playerTriggersReceived(List<Trigger> newTriggers) {

		this.updatePlayerTriggers(this.conformTriggersListToFlyweight(newTriggers));
	}

	public void updatePlayerTriggers(List<Trigger> newTriggers) {
		List<Trigger> addedTriggers = new ArrayList<>();
		List<Trigger> removedTriggers = new ArrayList<>();

		//find added
		boolean flag;
		int isize = newTriggers.size();
		int jsize = playerTriggers.size();

		for (Trigger newTrigger : newTriggers) {
			flag = true;
			for (Trigger oldTrigger : playerTriggers)
			for (int j = 0; j < jsize; j++) {
				if (newTrigger.trigger_id == oldTrigger.trigger_id) {
					flag = false;
				}
			}
			if (flag) {
				addedTriggers.add(newTrigger);
			}
		}

		//find removed
		boolean removed;
		for (Trigger oldTrigger : playerTriggers) {
			removed = true;

			for (Trigger newTrigger : newTriggers) {
				if (newTrigger.trigger_id == oldTrigger.trigger_id) {
					removed = false;
				}
			}
			if (removed) {
				removedTriggers.add(oldTrigger);
			}
		}

		playerTriggers = newTriggers;
		n_player_data_received++;

		if (!addedTriggers.isEmpty()) {
			mGamePlayAct.mDispatch.model_triggers_new_available(addedTriggers);//_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_NEW_AVAILABLE", null, @{@"added":addedTriggers});
		}
		if (!removedTriggers.isEmpty()) {
			mGamePlayAct.mDispatch.model_triggers_less_available(removedTriggers);//_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_LESS_AVAILABLE", null, @{@"removed":removedTriggers});
		}
		mGamePlayAct.mDispatch.model_player_triggers_available(); //_ARIS_NOTIF_SEND_("MODEL_PLAYER_TRIGGERS_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.model_game_player_piece_available();//_ARIS_NOTIF_SEND_("MODEL_GAME_PLAYER_PIECE_AVAILABLE", null, null);
	}

	public void requestTriggers() {
		mGamePlayAct.mServices.fetchTriggers();
	}
	public void requestTrigger(long t) {
		mGamePlayAct.mServices.fetchTriggerById(t);
	}

	public void requestPlayerTriggers() {
		if (this.playerDataReceived() && !mGame.network_level.equals("REMOTE")) {
			List<Instance> rejected = new ArrayList<>();
			List<Trigger> ptrigs = new ArrayList<>();
			List<Trigger> ts = new ArrayList<>(triggers.values());

			for (Trigger t : ts) {
				if (t.scene_id != mGame.scenesModel.playerScene.scene_id  || !mGame.requirementsModel.evaluateRequirementRoot(t.requirement_root_package_id)) {
					continue;
				}
				Instance i = mGame.instancesModel.instanceForId(t.instance_id);

				if (i == null) {
					continue;
				}
				if (i.factory_id != 0) {
					Factory f = mGame.factoriesModel.factoryForId(i.factory_id);
					if (f == null) {
						continue;
					}
					Date now = new Date(); // should be now.
					int time = now.compareTo(i.created); //[[NSDate date] timeIntervalSinceDate:i.created];
//					NSLog(@"%d",time);
					if (time > f.produce_expiration_time) {
						rejected.add(i);
						continue;
					}
				}
				ptrigs.add(t);
			}
			//TODO unsure of logging
			Log.i(Config.LOGTAG, getClass().getSimpleName() + "Accepted: " + ptrigs.size() + ", Rejected: " + rejected.size());
			mGamePlayAct.mDispatch.services_player_trigger_received(ptrigs); //("SERVICES_PLAYER_TRIGGERS_RECEIVED", null, @{@"triggers":ptrigs});
		}
		if (!this.playerDataReceived() || mGame.network_level.equals("HYBRID") || mGame.network_level.equals("REMOTE")) {
			mGamePlayAct.mServices.fetchTriggersForPlayer();
		}
	}

	// null trigger (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Trigger triggerForId(long trigger_id) {
		if (trigger_id != 0) {
			return new Trigger();
		}
		Trigger t = triggers.get(trigger_id);
		if (t == null) {
			blacklist.put(trigger_id, "true");
			this.requestTrigger(trigger_id);
			return new Trigger();
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
		for (Trigger t : playerTriggers) {
			if (t.type.contentEquals("QR") && t.qr_code.contentEquals(code)) {
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
		for (Trigger t : playerTriggers) {
			if (t.instance_id != instance_id) {
				newTriggers.add(t);
			}
		}
		this.updatePlayerTriggers(newTriggers);
	}
}
