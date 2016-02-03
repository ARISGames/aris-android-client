package edu.uoregon.casls.aris_android.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.LinkedList;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.InstantiableProtocol;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.services.PollTriggerService;

/**
 * Created by smorison on 8/20/15.
 */
public class DisplayQueueModel extends ARISModel {
	public List<Trigger> displayQueue = new LinkedList<>(); //NSMutableArray *displayQueue;

	public int listen_model_triggers_new_available  = 1;
	public int listen_model_triggers_less_available = 1;
	public int listen_model_triggers_invalidated    = 1;
	public int listen_user_moved                    = 1;

	//blacklist triggered triggers from auto-enqueue until they become unavailable for at least one refresh
	//(prevents constant triggering if someone has bad requirements)
	public List<Trigger> displayBlacklist = new LinkedList<>(); //NSMutableArray *displayBlacklist;
//	NSTimer *timerPoller;

	public transient GamePlayActivity mGamePlayAct;
	private Intent pollTriggerTimerSvcIntent = null;
	private boolean isTriggerPollerRunning = false;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public DisplayQueueModel() {
//		timerPoller = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(tickAndEnqueueAvailableTimers) userInfo:nil repeats:true];
		startTriggerPoller();
		this.clearPlayerData();
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_NEW_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_LESS_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_INVALIDATED",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"USER_MOVED",self,@selector(reevaluateAutoTriggers),nil);
	}

	private void startTriggerPoller() {
		// register receiver
		LocalBroadcastManager.getInstance(mGamePlayAct).registerReceiver(mMessageReceiver, new IntentFilter(AppConfig.TRIGGER_POLLER_SVC_ACTION));
		if (!isTriggerPollerRunning) {
			pollTriggerTimerSvcIntent = new Intent(mGamePlayAct, PollTriggerService.class);
			mGamePlayAct.startService(pollTriggerTimerSvcIntent);
			isTriggerPollerRunning = true;
		}

	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Trigger Timer has Cycled  - - - - - - - - TRIGG!");
			handleTriggerPollerMessage(intent);
		}
	};

	private void handleTriggerPollerMessage(Intent msg) {
		if (!isTriggerPollerRunning) return; // ignore any calls after timer was killed
		Bundle data = msg.getExtras();
		switch (data.getInt(AppConfig.COMMAND, 0)) {
			case AppConfig.POLLTIMER_CYCLE_PASS:
//				int progress = data.getInt(AppConfig.DATA, 0); // not used.
				this.tickAndEnqueueAvailableTimers();
				break;
			case AppConfig.POLLTIMER_RESULT: // sent when finished cycling stub. not used.
//				String res = data.getString(AppConfig.DATA);
				break;
			default:
				break;
		}
	}

	public void clearPlayerData() {
		displayQueue = null;
		displayBlacklist = null;
	}

	public void inject(Object i) {
		//[displayQueue removeObject(i);
		//[displayQueue insertObject:i atIndex:0];
		mGamePlayAct.mDispatch.model_display_new_enqueued(); //_ARIS_NOTIF_SEND_(@"MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
	}

	public void enqueue(Object i) {
		if (!this.displayInQueue(i))
			displayQueue.add((Trigger) i); // addObject(i);
		mGamePlayAct.mDispatch.model_display_new_enqueued(); //_ARIS_NOTIF_SEND_(@"MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
	}

	public void enqueueTrigger(Trigger t) { this.enqueue(t); }

	public void injectTrigger(Trigger t) { this.inject(t); }

	public void enqueueInstance(Instance i) { this.enqueue(i); }

	public void injectInstance(Instance i) { this.inject(i); }

	public void enqueueObject(InstantiableProtocol o) { this.enqueue(o); }

	public void injectObject(InstantiableProtocol o) { this.inject(o); }

	public void enqueueTab(Tab t) { this.enqueue(t); }

	public void injectTab(Tab t) { this.inject(t); }

	public Object dequeue() {
		this.purgeInvalidFromQueue();
		Object o = null;
		if (!displayQueue.isEmpty()) {
			o = displayQueue.get(0);
			displayQueue.remove(o); //removeObject(o);

			if (o.getClass().isInstance(Trigger.class) && ((Trigger) o).trigger_id != 0)
				displayBlacklist.add((Trigger) o);// addObject(o);
		}
		return o;
	}

	public boolean displayInQueue(Object d) {
		for (Trigger t : displayQueue)
			if (d == t) return true; // == tests to see that d and t are references to the SAME object
		return false;
	}

	public boolean displayBlacklisted(Object d) {
		for (Trigger t : displayBlacklist)
			if (d == t) return true;
		return false;
	}

	public void reevaluateAutoTriggers() {
		this.purgeInvalidFromQueue();
		//this.tickAndEnqueueAvailableTimers]; //will be called by poller
		this.enqueueNewImmediates();
	}

	public void purgeInvalidFromQueue() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers(); // _MODEL_TRIGGERS_.playerTriggers;
		Trigger t = null;

		//if trigger in queue no longer available, remove from queue
		for (int i = 0; i < displayQueue.size(); i++) {
			boolean valid = false;
			if (!displayQueue.get(i).getClass().isInstance(Trigger.class))
				continue; // isKindOfClass:[Trigger class]]) continue; //only triggers are blacklisted
			t = displayQueue.get(i);
//			for (int j = 0; j < pt.size(); j++)
			for (Trigger  jt : pt)
				if (t.trigger_id == 0 || t.trigger_id == ((Trigger) jt).trigger_id)
					valid = true; //allow artificial triggers to stay in queue
			if (!valid) displayQueue.remove(t);
		}

		//if trigger in blacklist no longer available/within range, remove from blacklist
		for (int i = 0; i < displayBlacklist.size(); i++) {
			boolean valid = false;
			if (displayBlacklist.get(i).getClass().isInstance(Trigger.class)) //only triggers are blacklisted
			{
				//@formatter:off
				t = displayBlacklist.get(i);
//				for (int j = 0; j < pt.size(); j++)
				for (Trigger  jt : pt) {
					if ( t == jt
						 && (t.type.contentEquals("IMMEDIATE")
							 || ( t.type.contentEquals("LOCATION")
								  && t.trigger_on_enter == 1
								  && ( t.infinite_distance == 1
									   || t.location.distanceTo(mGamePlayAct.mPlayer.location) < t.distance
								)
							)
						)
					)
					valid = true;
				}
				//@formatter:on
			}
			if (!valid) displayBlacklist.remove(t);// removeObject(t);
		}
	}

	public void tickAndEnqueueAvailableTimers() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers;

		for (Trigger it : pt) {
			if (it.type.contentEquals("TIMER")) {
				boolean inQueue = false;
				for (Trigger jt : displayQueue) {
					if (jt == it) inQueue = true;
				}
				if (!inQueue && it.time_left > 0)
					it.time_left--;

				if (it.time_left <= 0 && it.seconds > 0) {
					it.time_left = it.seconds;
					this.enqueueTrigger(it); //will auto verify not already in queue
				}
			}
		}
	}

	public void enqueueNewImmediates() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers;
		for (Trigger t : pt) {
			//@formatter:off
			if (
					( t.type.contentEquals("IMMEDIATE")
					  || ( t.type.contentEquals("LOCATION")
						   && t.trigger_on_enter == 1
						   && ( t.infinite_distance == 1
							    || t.location.distanceTo(mGamePlayAct.mPlayer.location) < t.distance
							  )
						 )
					)
					&& !this.displayBlacklisted(t)
			)
			{
				this.enqueueTrigger(t); //will auto verify not already in queue
			}
			//@formatter:on
		}
	}

	public void endPlay() {
		if (isTriggerPollerRunning) {
			mGamePlayAct.stopService(pollTriggerTimerSvcIntent);
			isTriggerPollerRunning = false;
		}

		listen_model_triggers_new_available = 0;
		listen_model_triggers_less_available = 0;
		listen_model_triggers_invalidated = 0;
		listen_user_moved = 0;
	}


}
