package edu.uoregon.casls.aris_android.models;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
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
	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;
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

		for (Trigger newTrigger : newTriggers) {
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
			mGamePlayAct.mDispatch.model_triggers_invalidated(invalidatedTriggers); //("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}

		n_game_data_received++;
		mGamePlayAct.mDispatch.model_triggers_available(); //_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.game_piece_available();//_ARIS_NOTIF_SEND_("GAME_PIECE_AVAILABLE", null, null);
	}

	public List<Trigger> conformTriggersListToFlyweight(List<Trigger> newTriggers) {
		List<Trigger> conformingTriggers = new ArrayList<>();
		List<Trigger> invalidatedTriggers = new ArrayList<>();
		int size = newTriggers.size();
		for (Trigger newt : newTriggers) {

			Trigger exist = this.triggerForId(newt.trigger_id); // iOS: exists = "LOCATION" trigger

			if (exist != null) {
				if (!exist.mergeDataFromTrigger(newt)) { // iOS: exist.mergeDataFromTrigger(newt) returns True (YES)
					invalidatedTriggers.add(exist);        // iOS: should NOT get here
				}
				conformingTriggers.add(exist);              // get's here regardless.
			}
			else {
				triggers.put(newt.trigger_id, newt);
				conformingTriggers.add(newt);
			}
		}
		if (!invalidatedTriggers.isEmpty()) {
			Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " model_triggers_invalidated() ");
			mGamePlayAct.mDispatch.model_triggers_invalidated(invalidatedTriggers); //_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_INVALIDATED", null, @{@"invalidated_triggers":invalidatedTriggers});
		}
		return conformingTriggers;
	}

	public void playerTriggersReceived(List<Trigger> newTriggers) {
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " About to call updatePlayerTriggers(). newTriggers size = " + newTriggers.size());
		this.updatePlayerTriggers(this.conformTriggersListToFlyweight(newTriggers));
	}

	public void updatePlayerTriggers(List<Trigger> newTriggers) {
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting updatePlayerTriggers(). newTriggers size = " + newTriggers.size());
		if (newTriggers.isEmpty() || newTriggers.size() < 1)
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " updateTriggers entered with EMPTY LIST of newTriggers. Size = " + newTriggers.size());
		List<Trigger> addedTriggers = new ArrayList<>();
		List<Trigger> removedTriggers = new ArrayList<>();
		//
		//find added
		//
		// redundant flag initialization since we'll always enter this loop and
		// isNew will get set, but here just for clarity, but we cannot assume we'll
		// enter the 'find removed' loop since playerTriggers might be empty.
		boolean isNew = false;
		for (Trigger newTrigger : newTriggers) {
			isNew = true;
			for (Trigger oldTrigger : playerTriggers) {
				if (newTrigger.trigger_id == oldTrigger.trigger_id)
					isNew = false;
			}
			if (isNew) {
				addedTriggers.add(newTrigger);
			}
		}

		//find removed
		boolean removed = false;
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
			Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " model_triggers_less_available() ");
			mGamePlayAct.mDispatch.model_triggers_new_available(addedTriggers);//_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_NEW_AVAILABLE", null, @{@"added":addedTriggers});
		}
		if (!removedTriggers.isEmpty()) {
			Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " model_triggers_less_available() ");
			mGamePlayAct.mDispatch.model_triggers_less_available(removedTriggers);//_ARIS_NOTIF_SEND_("MODEL_TRIGGERS_LESS_AVAILABLE", null, @{@"removed":removedTriggers});
		}
		mGamePlayAct.mDispatch.model_player_triggers_available(); //_ARIS_NOTIF_SEND_("MODEL_PLAYER_TRIGGERS_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.player_piece_available();//_ARIS_NOTIF_SEND_("PLAYER_PIECE_AVAILABLE", null, null);
	}

	public void requestTriggers() {
		mGamePlayAct.mAppServices.fetchTriggers();
	}
	public void requestTrigger(long t) {
		mGamePlayAct.mAppServices.fetchTriggerById(t);
	}

	public void requestPlayerTriggers() {
		if (this.playerDataReceived() && !mGame.network_level.equals("REMOTE")) {
			List<Instance> rejected = new ArrayList<>();
			List<Trigger> ptrigs = new ArrayList<>();
			List<Trigger> ts = new ArrayList<>(triggers.values());
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting requestPlayerTriggers(). ts size = " + ts.size());

			for (Trigger t : ts) {
				if (t.scene_id != mGame.scenesModel.playerScene.scene_id  || !mGame.requirementsModel.evaluateRequirementRoot(t.requirement_root_package_id)) {
					Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting requestPlayerTriggers(). Stopping Loop at pt 1. t.requirement_root_package_id = " + t.requirement_root_package_id);
					continue;
				}
				Instance i = mGame.instancesModel.instanceForId(t.instance_id);

				if (i == null) {
					Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting requestPlayerTriggers(). Stopping Loop at pt 2 ");
					continue;
				}
				if (i.factory_id != 0) {
					Factory f = mGame.factoriesModel.factoryForId(i.factory_id);
					if (f == null) {
						Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting requestPlayerTriggers(). Stopping Loop at pt 3 ");
						continue;
					}
					Date now = new Date(); // should be now.
					int time = now.compareTo(i.getCreated()); //[[NSDate date] timeIntervalSinceDate:i.created];
//					NSLog(@"%d",time);
					if (time > f.produce_expiration_time) {
						rejected.add(i);
						Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Starting requestPlayerTriggers(). Stopping Loop at pt 4 ");
						continue;
					}
				}
				ptrigs.add(t);
			}
			Log.i(AppConfig.LOGTAG, getClass().getSimpleName() + "Accepted: " + ptrigs.size() + ", Rejected: " + rejected.size());
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Finishing requestPlayerTriggers(). ptrigs size = " + ptrigs.size());
			mGamePlayAct.mDispatch.services_player_triggers_received(ptrigs); //("SERVICES_PLAYER_TRIGGERS_RECEIVED", null, @{@"triggers":ptrigs});
		} // for now treat every game as HYBRID, since that's really all we need to worry about in v1.0 of Android
		if (!this.playerDataReceived() || mGame.network_level.equals("HYBRID") || mGame.network_level.equals("REMOTE")) {
			mGamePlayAct.mAppServices.fetchTriggersForPlayer();
		}
	}

	// null trigger (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Trigger triggerForId(long trigger_id) {
		if (trigger_id == 0) {
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
		Collection<Trigger> trigList = triggers.values(); // convert to Collection for iterating
		for (Trigger t : trigList) {
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
