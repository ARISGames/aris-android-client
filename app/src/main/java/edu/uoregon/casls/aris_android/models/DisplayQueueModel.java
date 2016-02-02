package edu.uoregon.casls.aris_android.models;

import java.util.LinkedList;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.InstantiableProtocol;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class DisplayQueueModel extends ARISModel {
	public List<Trigger> displayQueue = new LinkedList<>(); //NSMutableArray *displayQueue;

	//blacklist triggered triggers from auto-enqueue until they become unavailable for at least one refresh
	//(prevents constant triggering if someone has bad requirements)
	public List<Trigger> displayBlacklist = new LinkedList<>(); //NSMutableArray *displayBlacklist;
//	NSTimer *timerPoller;

	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

/*
	- (id) init
	{
		if(self = [super init])
		{
			timerPoller = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(tickAndEnqueueAvailableTimers) userInfo:nil repeats:true];
			this.clearPlayerData];
			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_NEW_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_LESS_AVAILABLE",self,@selector(reevaluateAutoTriggers),nil);
			_ARIS_NOTIF_LISTEN_(@"MODEL_TRIGGERS_INVALIDATED",self,@selector(reevaluateAutoTriggers),nil);
			_ARIS_NOTIF_LISTEN_(@"USER_MOVED",self,@selector(reevaluateAutoTriggers),nil);
		}
		return self;
	}
*/

	public void clearPlayerData()
	{
		displayQueue = null;
		displayBlacklist = null;
	}

	public void inject(Object i)
	{
		 //[displayQueue removeObject(i);
		 //[displayQueue insertObject:i atIndex:0];
		mGamePlayAct.mDispatch.model_display_new_enqueued(); //_ARIS_NOTIF_SEND_(@"MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
	}

	public void enqueue(Object i)
	{
		if(!this.displayInQueue(i))
			displayQueue.add((Trigger) i); // addObject(i);
		mGamePlayAct.mDispatch.model_display_new_enqueued(); //_ARIS_NOTIF_SEND_(@"MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
	}

	public void enqueueTrigger(Trigger t)                       { this.enqueue(t); }
	public void injectTrigger(Trigger t)                       { this.inject(t);  }
	public void enqueueInstance(Instance i)                     { this.enqueue(i); }
	public void injectInstance(Instance i)                    { this.inject(i);  }
	public void enqueueObject(InstantiableProtocol o) { this.enqueue(o); }
	public void injectObject(InstantiableProtocol o) { this.inject(o);  }
	public void enqueueTab(Tab t)                               { this.enqueue(t);  }
	public void injectTab(Tab t)                                { this.inject(t);  }

	public Object dequeue()
	{
		this.purgeInvalidFromQueue();
		Object o = null;
		if(displayQueue.size() > 0)
		{
			o = displayQueue.get(0);
			displayQueue.remove(o); //removeObject(o);

			if(o.getClass().isInstance(Trigger.class)  && ((Trigger) o).trigger_id != 0) displayBlacklist.add((Trigger) o);// addObject(o);
		}
		return o;
	}

	public boolean displayInQueue(Object d)
	{
		for(int i = 0; i < displayQueue.size(); i++)
			if(d == displayQueue.get(i)) return true;
		return false;
	}

	public boolean displayBlacklisted(Object d)
	{
		for(int i = 0; i < displayBlacklist.size(); i++)
			if(d == displayBlacklist.get(i)) return true;
		return false;
	}

	public void reevaluateAutoTriggers()
	{
		this.purgeInvalidFromQueue();
		//this.tickAndEnqueueAvailableTimers]; //will be called by poller
		this.enqueueNewImmediates();
	}

	public void purgeInvalidFromQueue()
	{
		List<Trigger> pt = mGamePlayAct.mGame.triggersModel.playerTriggers(); // _MODEL_TRIGGERS_.playerTriggers;
		Trigger t;

		//if trigger in queue no longer available, remove from queue
		for(int i = 0; i < displayQueue.size(); i++)
		{
			boolean valid = false;
			if(!displayQueue.get(i).getClass().isInstance(Trigger.class)) continue; // isKindOfClass:[Trigger class]]) continue; //only triggers are blacklisted
			t = displayQueue.get(i);
			for(int j = 0; j < pt.size(); j++)
				if(t.trigger_id == 0 || t.trigger_id == ((Trigger)pt.get(j)).trigger_id) valid = true; //allow artificial triggers to stay in queue
			if(!valid) displayQueue.remove(t);
		}

		//if trigger in blacklist no longer available/within range, remove from blacklist
		for(int i = 0; i < displayBlacklist.size(); i++)
		{
			boolean valid = false;
			if(displayBlacklist.get(i).getClass().isInstance(Trigger.class)) //only triggers are blacklisted
			{
				t = displayBlacklist.get(i);
				for(int j = 0; j < pt.size(); j++)
				{
					if(
							t == pt.get(j) &&
									(
											t.type.contentEquals("IMMEDIATE") ||
					(
							t.type.contentEquals("LOCATION") &&
					t.trigger_on_enter &&
							(
									t.infinite_distance == 1 ||
									t.location. distanceFromLocation:_MODEL_PLAYER_.location] < t.distance
					)
					)
					)
					)
					valid = true;
				}
			}
			if(!valid) displayBlacklist.remove(t);// removeObject(t);
		}
	}

/*	public void tickAndEnqueueAvailableTimers()
	{
		List<Trigger> pt = _MODEL_TRIGGERS_.playerTriggers;
		Trigger t;
		for(long i = 0; i < pt.size(); i++)
		{
			t = pt[i];

			if([t.type isEqualToString:@"TIMER"])
			{
				boolean inQueue = false;
				for(long i = 0; i < displayQueue.size(); i++)
				{
					if(displayQueue.get(i) == t) inQueue = true;
				}
				if(!inQueue && t.time_left > 0)
					t.time_left--;

				if(t.time_left <= 0 && t.seconds > 0)
				{
					t.time_left = t.seconds;
					this.enqueueTrigger(t); //will auto verify not already in queue
				}
			}
		}
	}

	public void enqueueNewImmediates()
	{
		List<Trigger> pt = _MODEL_TRIGGERS_.playerTriggers;
		Trigger t;
		for(long i = 0; i < pt.size(); i++)
		{
			t = pt[i];
			if(
					(
							[t.type isEqualToString:@"IMMEDIATE"] ||
			(
					[t.type isEqualToString:@"LOCATION"] &&
			t.trigger_on_enter &&
					(
							t.infinite_distance ||
							[t.location distanceFromLocation:_MODEL_PLAYER_.location] < t.distance
			)
			)
			) &&
			!this.displayBlacklisted(t)
			)
			{
				this.enqueueTrigger(t); //will auto verify not already in queue
			}
		}
	}

	public void endPlay()
	{
		[timerPoller invalidate];
		_ARIS_NOTIF_IGNORE_ALL_(self);
	}

*/
}
