package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Tab;

/**
 * Created by smorison on 8/20/15.
 */
public class TabsModel extends ARISModel {

	public Map<Long, Tab> tabs = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		tabs.clear();
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void requestTabs() {
	}

	public void requestPlayerTabs() {

	}

	public long nGameDataToReceive () {
		return 1;
	}


	public void requestPlayerData
	{
		this.requestPlayerTabs];
	}
	public void clearPlayerData()
	{
		playerTabs.clear();
		n_player_data_received = 0;
	}

	public void requestGameData
	{
		this.requestTabs];
	}
	public void clearGameData
	{
		this.clearPlayerData];
		tabs = [[NSMutableDictionary alloc] init];
		n_game_data_received = 0;
	}


	public void tabsReceived(List<Tab> newTabs)
	{
		this.updateTabs(newTabs);
	}

	public void updateTabs(List<Tab> newTabs)
	{
		Tab *newTab;
		NSNumber *newTabId;
		for(long i = 0; i < newTabs.count; i++)
		{
			newTab = [newTabs objectAtIndex:i];
			newTabId = [NSNumber numberWithLong:newTab.tab_id];
			if(![tabs objectForKey:newTabId]) [tabs setObject:newTab forKey:newTabId];
		}
		n_game_data_received++;
		_ARIS_NOTIF_SEND_(@"MODEL_TABS_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestTabs       { [_SERVICES_ fetchTabs]; }

	public void requestPlayerTabs
	{
		if(this.playerDataReceived] &&
		![_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		{
			NSMutableArray *ptabs = [[NSMutableArray alloc] init];
			NSArray *ts = [tabs allValues];
			for(int i = 0; i < ts.count; i++)
			{
				Tab *t = ts[i];
				if([_MODEL_REQUIREMENTS_ evaluateRequirementRoot:t.requirement_root_package_id])
				[ptabs addObject:t];
			}
			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_TABS_RECEIVED",nil,@{@"tabs":ptabs});
		}
		if(!this.playerDataReceived] ||
		[_MODEL_GAME_.network_level isEqualToString:@"HYBRID"] ||
		[_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		[_SERVICES_ fetchTabsForPlayer];
	}

//admittedly a bit silly, but a great way to rid any risk of deviation from flyweight by catching it at the beginning
	- (NSArray *) conformTabListToFlyweight:(NSArray *)newTabs
	{
		NSMutableArray *conformingTabs = [[NSMutableArray alloc] init];
		Tab *t;
		for(long i = 0; i < newTabs.count; i++)
		{
			if((t = this.tabForId:((Tab *)newTabs[i]).tab_id]))
			[conformingTabs addObject:t];
		}

		return conformingTabs;
	}

	public void playerTabsReceived:(NSNotification *)notification
	{
		this.updatePlayerTabs:this.conformTabListToFlyweight:[notification.userInfo objectForKey:@"tabs"]]];
	}

	public void updatePlayerTabs:(NSArray *)newTabs
	{
		NSDictionary *deltas = this.findDeltasInNew:newTabs fromOld:playerTabs];
		playerTabs = newTabs; //assumes already conforms to flyweight
		n_player_data_received++;
		if(((NSArray *)deltas[@"added"]).count > 0)
		_ARIS_NOTIF_SEND_(@"MODEL_TABS_NEW_AVAILABLE",nil,deltas);
		if(((NSArray *)deltas[@"removed"]).count > 0)
		_ARIS_NOTIF_SEND_(@"MODEL_TABS_LESS_AVAILABLE",nil,deltas);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	- (NSDictionary *) findDeltasInNew:(NSArray *)newTabs fromOld:(NSArray *)oldTabs
	{
		NSDictionary *qDeltas = @{ @"added":[[NSMutableArray alloc] init], @"removed":[[NSMutableArray alloc] init] };

		//placeholders for comparison
		Tab *newTab;
		Tab *oldTab;

		//find added
		BOOL new;
		for(long i = 0; i < newTabs.count; i++)
		{
			new = YES;
			newTab = newTabs[i];
			for(long j = 0; j < oldTabs.count; j++)
			{
				oldTab = oldTabs[j];
				if(newTab.tab_id == oldTab.tab_id) new = NO;
			}
			if(new) [qDeltas[@"added"] addObject:newTabs[i]];
		}

		//find removed
		BOOL removed;
		for(long i = 0; i < oldTabs.count; i++)
		{
			removed = YES;
			oldTab = oldTabs[i];
			for(long j = 0; j < newTabs.count; j++)
			{
				newTab = newTabs[j];
				if(newTab.tab_id == oldTab.tab_id) removed = NO;
			}
			if(removed) [qDeltas[@"removed"] addObject:oldTabs[i]];
		}

		return qDeltas;
	}

	- (Tab *) tabForType:(NSString *)t
	{
		Tab *tab;

		//first, search player tabs
		for(long i = 0; i < playerTabs.count; i++)
		{
			if([((Tab *)playerTabs[i]).type isEqualToString:t])
			tab = playerTabs[i];
		}
		if(tab) return tab;

		//if not found, try to get any game tab
		NSArray *gameTabs = [tabs allValues];
		for(long i = 0; i < gameTabs.count; i++)
		{
			if([((Tab *)gameTabs[i]).type isEqualToString:t])
			tab = gameTabs[i];
		}
		return tab;
	}

	- (Tab *) tabForId:(long)tab_id
	{
		if(!tab_id) return [[Tab alloc] init];
		return [tabs objectForKey:[NSNumber numberWithLong:tab_id]];
	}

	- (NSArray *) playerTabs
	{
		return playerTabs;
	}

}
