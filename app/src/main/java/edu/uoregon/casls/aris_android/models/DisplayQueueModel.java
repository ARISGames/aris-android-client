package edu.uoregon.casls.aris_android.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
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

	public int listen_model_triggers_new_available  = 0; // was 1. trying to delay getting this called before tab fragemtn is in place. - sem
	public int listen_model_triggers_less_available = 1;
	public int listen_model_triggers_invalidated    = 1;
	public int listen_user_moved                    = 1;

	public List<Object> displayQueue = new ArrayList<>(); //NSMutableArray *displayQueue;
	//blacklist triggered triggers from auto-enqueue until they become unavailable for at least one refresh
	//(prevents constant triggering if someone has bad requirements)
	public List<Trigger> displayBlacklist = new ArrayList<>(); //NSMutableArray *displayBlacklist;
//	NSTimer *timerPoller;

	public transient GamePlayActivity mGamePlayAct;
	private Intent  pollTriggerTimerSvcIntent = null;
	private boolean isTriggerPollerRunning    = false;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public DisplayQueueModel(GamePlayActivity gamePlayAct) {
//		timerPoller = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(tickAndEnqueueAvailableTimers) userInfo:nil repeats:true];
		this.initContext(gamePlayAct); // must be called immediately to enable the poller to start.
		startTriggerPoller();
		this.clearPlayerData();
		displayBlacklist = new ArrayList<>();
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_NEW_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_LESS_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_INVALIDATED",self,@selector(reevaluateAutoTriggers),nil);
//			_ARIS_NOTIF_LISTEN_(@"USER_MOVED",self,@selector(reevaluateAutoTriggers),nil);
	}

	private void startTriggerPoller() {
		Context c = mGamePlayAct;
		BroadcastReceiver bcr = mMessageReceiver;
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
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Enqueue the following: " + i.getClass().getName());
		if (!this.displayInQueue(i)) { // if this (Instance) is not already in displayQueue, add it.
			if (displayQueue == null) displayQueue = new ArrayList<>();
			displayQueue.add(i); // addObject(i);
		}
		mGamePlayAct.mDispatch.model_display_new_enqueued(); //_ARIS_NOTIF_SEND_(@"MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
	}

	public void enqueueTrigger(Trigger t) {
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " enqueueTrigger() ");
		this.enqueue(t);
	}

	/* injectTrigger is never called */
	public void injectTrigger(Trigger t) { this.inject(t); }

	public void enqueueInstance(Instance i) {
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " enqueueInstance() ");
		this.enqueue(i);
	}

	/* injectInstance is never called */
	public void injectInstance(Instance i) { this.inject(i); }

	public void enqueueObject(InstantiableProtocol o) {
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " enqueueObject() ");
		this.enqueue(o);
	}

	public void injectObject(InstantiableProtocol o) { this.inject(o); }

	public void enqueueTab(Tab t) { this.enqueue(t); }

	public void injectTab(Tab t) { this.inject(t); }

	public Object dequeue() { // problem: after arriving at a (plaque in this case, the queue should be empty. It's not. It has the Plaque in it.
		this.purgeInvalidFromQueue();// displayQueue should be size = 0 after continue in plaquetest game
		Object o = null;
		if (!displayQueue.isEmpty()) {
			o = displayQueue.get(0);
			displayQueue.remove(o); //removeObject(o);

			if (o instanceof Trigger && ((Trigger)o).trigger_id != 0)
				displayBlacklist.add((Trigger)o);// addObject(o);
		}
//		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Dequeue the following: " + o.getClass().getName());
		return o;
	}

	public boolean displayInQueue(Object d) {
		if (displayQueue != null)
			for (Object o : displayQueue)
				if (d == o)
					return true; // == tests to see that d and t are references to the SAME object
		return false;
	}

	public boolean displayBlacklisted(Object d) {
		if (displayBlacklist != null)
			for (Trigger t : displayBlacklist)
				if (d == t) return true;
		return false;
	}

	public void reevaluateAutoTriggers() {
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " reevaluateAutoTriggers() ");

		this.purgeInvalidFromQueue();
		//this.tickAndEnqueueAvailableTimers]; //will be called by poller
		this.enqueueNewImmediates();
	}

	public void purgeInvalidFromQueue() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers(); // _MODEL_TRIGGERS_.playerTriggers;
		Trigger t = null;

		//if trigger in queue no longer available, remove from queue
//		for (int i = 0; i < displayQueue.size(); i++) {
		if (displayQueue != null) { // hold off until there's something in the List. NPE otherwise.
//			for (Object o : displayQueue) {
			for (Iterator<Object> iter = displayQueue.iterator(); iter.hasNext(); ) {// for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
				Object o = iter.next(); //String value = iterator.next();
				boolean valid = false;
				if (!(o instanceof Trigger)) // (!o instanceof Trigger) <- is this more efficient/appropriate/logically correct?
					continue; // isKindOfClass:[Trigger class]]) continue; //only triggers are blacklisted
				t = (Trigger) o; // cast to Trigger
				for (Trigger jt : pt)
					if (t.trigger_id == 0 || t.trigger_id == ((Trigger) jt).trigger_id)
						valid = true; //allow artificial triggers to stay in queue
//				if (!valid) displayQueue.remove(o);
				if (!valid) iter.remove();
			}
		}

		//if trigger in blacklist no longer available/within range, remove from blacklist
//		for (int i = 0; i < displayBlacklist.size(); i++) {
		if (displayBlacklist != null) {
//			for (Object o : displayBlacklist) { // could probably just be cast to Trigger instead of Object here - sem
//			for (Iterator<Trigger> iter = displayBlacklist.iterator(); iter.hasNext(); ) { // could probably just be cast to Trigger instead of Object here - sem
			for (int i = displayBlacklist.size() - 1; i > -1; i--) { // could probably just be cast to Trigger instead of Object here - sem
				boolean valid = false;
//				if (o instanceof Trigger) { //only triggers are blacklisted - sem: so why are we testing here?
//					t = (Trigger)o;
//					t = iter.next();
				t = displayBlacklist.get(i);
				//@formatter:off
				for (Trigger jt : pt) {
					if (t == jt
						&& (t.type.contentEquals("IMMEDIATE")
							|| (t.type.contentEquals("LOCATION")
								&& t.trigger_on_enter == 1
								&& (t.infinite_distance == 1
									|| t.location.distanceTo(mGamePlayAct.mPlayer.location) < t.distance
								    )
							)
						)
					)
					valid = true;
				}
				//@formatter:on
				if (!valid) displayBlacklist.remove(t);// removeObject(t);
			}
		}
	}

	public void tickAndEnqueueAvailableTimers() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers;

		for (Trigger t : pt) {
			if (t.type.contentEquals("TIMER")) {
				boolean inQueue = false;
				for (Object o : displayQueue) {
					if (o == t) inQueue = true;
				}
				if (!inQueue && t.time_left > 0)
					t.time_left--;

				if (t.time_left <= 0 && t.seconds > 0) {
					t.time_left = t.seconds;
					this.enqueueTrigger(t); //will auto verify not already in queue
				}
			}
		}
	}

	public void enqueueNewImmediates() {
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers;
		if (pt != null)
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
					Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " enqueueNewImmediates() ");
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
