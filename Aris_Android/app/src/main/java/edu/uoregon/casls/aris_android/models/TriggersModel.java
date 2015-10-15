package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class TriggersModel extends ARISModel {

	public Map<Long, Trigger> triggers = new LinkedHashMap<>();
	public List<Trigger> playerTriggers = new ArrayList<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		triggers.clear();
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void requestTriggers() {
	}

	public void requestPlayerTriggers() {

	}

	public long nGameDataToReceive () {
		return 1;
	}


	public void requestPlayerData
	{
		this.requestPlayerTriggers];
	}
	public void clearPlayerData
	{
		playerTriggers = [[NSArray alloc] init];
		n_player_data_received = 0;
	}
	- (long) nPlayerDataToReceive
	{
		return 1;
	}

	public void requestGameData
	{
		this.requestTriggers];
	}
	public void clearGameData
	{
		this.clearPlayerData];
		triggers  = [[NSMutableDictionary alloc] init];
		blacklist = [[NSMutableDictionary alloc] init];
		n_game_data_received = 0;
	}
	- (long) nGameDataToReceive
	{
		return 1;
	}

	public void triggersReceived(List<Trigger> newTriggers)
	{
		this.updateTriggers(newTriggers);
	}

	public void triggerReceived:(NSNotification *)notif
	{
		this.updateTriggers:@[notif.userInfo[@"trigger"]]];
	}

	public void updateTriggers(List<Trigger> newTriggers)
	{
		Trigger *newTrigger;
		NSNumber *newTriggerId;
		NSMutableArray *invalidatedTriggers = [[NSMutableArray alloc] init];
		for(long i = 0; i < newTriggers.count; i++)
		{
			newTrigger = [newTriggers objectAtIndex:i];
			newTriggerId = [NSNumber numberWithLong:newTrigger.trigger_id];
			if(![triggers objectForKey:newTriggerId])
			{
				[triggers setObject:newTrigger forKey:newTriggerId];
				[blacklist removeObjectForKey:[NSNumber numberWithLong:newTriggerId]];
			}
			else
			if(![[triggers objectForKey:newTriggerId] mergeDataFromTrigger:newTrigger])
			[invalidatedTriggers addObject:[triggers objectForKey:newTriggerId]];
		}
		if(invalidatedTriggers.count) _ARIS_NOTIF_SEND_(@"MODEL_TRIGGERS_INVALIDATED",nil,@{@"invalidated_triggers":invalidatedTriggers});

		n_game_data_received++;
		_ARIS_NOTIF_SEND_(@"MODEL_TRIGGERS_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	- (NSArray *) conformTriggersListToFlyweight:(NSArray *)newTriggers
	{
		NSMutableArray *conformingTriggers = [[NSMutableArray alloc] init];
		NSMutableArray *invalidatedTriggers = [[NSMutableArray alloc] init];
		for(long i = 0; i < newTriggers.count; i++)
		{
			Trigger *newt = newTriggers[i];
			Trigger *exist = this.triggerForId:newt.trigger_id];

			if(exist)
			{
				if(![exist mergeDataFromTrigger:newt]) [invalidatedTriggers addObject:exist];
				[conformingTriggers addObject:exist];
			}
			else
			{
				[triggers setObject:newt forKey:[NSNumber numberWithLong:newt.trigger_id]];
				[conformingTriggers addObject:newt];
			}
		}
		if(invalidatedTriggers.count) _ARIS_NOTIF_SEND_(@"MODEL_TRIGGERS_INVALIDATED",nil,@{@"invalidated_triggers":invalidatedTriggers});
		return conformingTriggers;
	}

	public void playerTriggersReceived:(NSNotification *)notif
	{
		this.updatePlayerTriggers:this.conformTriggersListToFlyweight:notif.userInfo[@"triggers"]]];
	}

	public void updatePlayerTriggers:(NSArray *)newTriggers
	{
		NSMutableArray *addedTriggers = [[NSMutableArray alloc] init];
		NSMutableArray *removedTriggers = [[NSMutableArray alloc] init];

		//placeholders for comparison
		Trigger *newTrigger;
		Trigger *oldTrigger;

		//find added
		BOOL new;
		for(long i = 0; i < newTriggers.count; i++)
		{
			new = YES;
			newTrigger = newTriggers[i];
			for(long j = 0; j < playerTriggers.count; j++)
			{
				oldTrigger = playerTriggers[j];
				if(newTrigger.trigger_id == oldTrigger.trigger_id) new = NO;
			}
			if(new) [addedTriggers addObject:newTriggers[i]];
		}

		//find removed
		BOOL removed;
		for(long i = 0; i < playerTriggers.count; i++)
		{
			removed = YES;
			oldTrigger = playerTriggers[i];
			for(long j = 0; j < newTriggers.count; j++)
			{
				newTrigger = newTriggers[j];
				if(newTrigger.trigger_id == oldTrigger.trigger_id) removed = NO;
			}
			if(removed) [removedTriggers addObject:playerTriggers[i]];
		}

		playerTriggers = newTriggers;
		n_player_data_received++;

		if(addedTriggers.count > 0)   _ARIS_NOTIF_SEND_(@"MODEL_TRIGGERS_NEW_AVAILABLE",nil,@{@"added":addedTriggers});
		if(removedTriggers.count > 0) _ARIS_NOTIF_SEND_(@"MODEL_TRIGGERS_LESS_AVAILABLE",nil,@{@"removed":removedTriggers});
		_ARIS_NOTIF_SEND_(@"MODEL_PLAYER_TRIGGERS_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	public void requestTriggers { [_SERVICES_ fetchTriggers]; }
	public void requestTrigger:(long)t { [_SERVICES_ fetchTriggerById:t]; }
	public void requestPlayerTriggers
	{
		if(this.playerDataReceived] &&
		![_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		{
			NSMutableArray *rejected = [[NSMutableArray alloc] init];
			NSMutableArray *ptrigs = [[NSMutableArray alloc] init];
			NSArray *ts = [triggers allValues];
			for(int i = 0; i < ts.count; i++)
			{
				Trigger *t = ts[i];
				if(t.scene_id != _MODEL_SCENES_.playerScene.scene_id  || ![_MODEL_REQUIREMENTS_ evaluateRequirementRoot:t.requirement_root_package_id]) continue;

				Instance *i = [_MODEL_INSTANCES_ instanceForId:t.instance_id];
				if(!i) continue;

				if(i.factory_id)
				{
					Factory *f = [_MODEL_FACTORIES_ factoryForId:i.factory_id];
					if(!f) continue;
					int time = [[NSDate date] timeIntervalSinceDate:i.created];
					NSLog(@"%d",time);
					if(time > f.produce_expiration_time)
					{
						[rejected addObject:i];
						continue;
					}
				}
				[ptrigs addObject:t];
			}
			NSLog(@"Accepted: %lu, Rejected: %lu",(unsigned long)ptrigs.count,(unsigned long)rejected.count);
			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_TRIGGERS_RECEIVED",nil,@{@"triggers":ptrigs});
		}
		if(!this.playerDataReceived] ||
		[_MODEL_GAME_.network_level isEqualToString:@"HYBRID"] ||
		[_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		[_SERVICES_ fetchTriggersForPlayer];
	}

// null trigger (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	- (Trigger *) triggerForId:(long)trigger_id
	{
		if(!trigger_id) return [[Trigger alloc] init];
		Trigger *t = [triggers objectForKey:[NSNumber numberWithLong:trigger_id]];
		if(!t)
		{
			[blacklist setObject:@"true" forKey:[NSNumber numberWithLong:trigger_id]];
			this.requestTrigger:trigger_id];
			return [[Trigger alloc] init];
		}
		return t;
	}

	- (NSArray *) triggersForInstanceId:(long)instance_id
	{
		NSMutableArray *a = [[NSMutableArray alloc] init];
		for(long i = 0; i < triggers.count; i++)
		{
			Trigger *t = [triggers allValues][i];
			if(t.instance_id == instance_id)
			[a addObject:t];
		}
		return a;
	}

	- (Trigger *) triggerForQRCode:(NSString *)code
	{
		Trigger *t;
		for(long i = 0; i < playerTriggers.count; i++)
		{
			t = playerTriggers[i];
			if([t.type isEqualToString:@"QR"] && [t.qr_code isEqualToString:code]) return t;
		}
		return nil;
	}

	- (NSArray *) playerTriggers
	{
		return playerTriggers;
	}

	public void expireTriggersForInstanceId:(long)instance_id
	{
		NSMutableArray *newTriggers = [[NSMutableArray alloc] init];
		for(long i = 0; i < playerTriggers.count; i++)
		{
			if(((Trigger *)playerTriggers[i]).instance_id != instance_id)
			[newTriggers addObject:playerTriggers[i]];
		}
		this.updatePlayerTriggers:newTriggers];
	}

}
