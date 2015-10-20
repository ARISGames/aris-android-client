package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Overlay;

/**
 * Created by smorison on 8/20/15.
 */
public class OverlaysModel extends ARISModel {

	public Map<Long, Overlay> overlays = new LinkedHashMap<>();
	public List<Overlay> playerOverlays = new ArrayList<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void requestOverlays() {
	}

	public void requestPlayerOverlays() {

	}

	public long nGameDataToReceive () {
		return 1;
	}


	public void requestPlayerData()
	{
		this.requestPlayerOverlays();
	}

	public void clearPlayerData()
	{
		playerOverlays.clear();
		n_player_data_received = 0;
	}

	public void requestGameData()
	{
		this.requestOverlays();
	}
	public void clearGameData()
	{
		this.clearPlayerData();
		overlays.clear();// = [[NSMutableDictionary alloc] init];
		n_game_data_received = 0;
	}

	public void overlaysReceived(List<Overlay> newOverlays)
	{
		this.updateOverlays(newOverlays);
	}

	public void updateOverlays(List<Overlay> newOverlays)
	{
//		Overlay *newOverlay;
		Long newOverlayId;
		for (Overlay newOverlay : newOverlays)
		{
			newOverlayId = newOverlay.overlay_id;
			if(!overlays.containsKey(newOverlayId))
			overlays.put(newOverlayId, newOverlay);// setObject:newOverlay forKey:newOverlayId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_overlays_available(overlays); //_ARIS_NOTIF_SEND_(@"MODEL_OVERLAYS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available();//_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	- (NSArray *) conformOverlaysListToFlyweight:(NSArray *)newOverlays
	{
		NSMutableArray *conformingOverlays = [[NSMutableArray alloc] init];
		Overlay *o;
		for(long i = 0; i < newOverlays.count; i++)
		{
			if((o = this.overlayForId:((Overlay *)newOverlays[i]).overlay_id]))
			[conformingOverlays addObject:o];
		}

		return conformingOverlays;
	}

	public void playerOverlaysReceived:(NSNotification *)notif
	{
		this.updatePlayerOverlays:this.conformOverlaysListToFlyweight:notif.userInfo[@"overlays"]]];
	}

	public void updatePlayerOverlays:(NSArray *)newOverlays
	{
		NSMutableArray *addedOverlays = [[NSMutableArray alloc] init];
		NSMutableArray *removedOverlays = [[NSMutableArray alloc] init];

		//placeholders for comparison
		Overlay *newOverlay;
		Overlay *oldOverlay;

		//find added
		BOOL new;
		for(long i = 0; i < newOverlays.count; i++)
		{
			new = YES;
			newOverlay = newOverlays[i];
			for(long j = 0; j < playerOverlays.count; j++)
			{
				oldOverlay = playerOverlays[j];
				if(newOverlay.overlay_id == oldOverlay.overlay_id) new = NO;
			}
			if(new) [addedOverlays addObject:newOverlays[i]];
		}

		//find removed
		BOOL removed;
		for(long i = 0; i < playerOverlays.count; i++)
		{
			removed = YES;
			oldOverlay = playerOverlays[i];
			for(long j = 0; j < newOverlays.count; j++)
			{
				newOverlay = newOverlays[j];
				if(newOverlay.overlay_id == oldOverlay.overlay_id) removed = NO;
			}
			if(removed) [removedOverlays addObject:playerOverlays[i]];
		}

		playerOverlays = newOverlays;
		n_player_data_received++;
		if(addedOverlays.count > 0)   _ARIS_NOTIF_SEND_(@"MODEL_OVERLAYS_NEW_AVAILABLE",nil,@{@"added":addedOverlays});
		if(removedOverlays.count > 0) _ARIS_NOTIF_SEND_(@"MODEL_OVERLAYS_LESS_AVAILABLE",nil,@{@"removed":removedOverlays});
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	public void requestOverlays       { [_SERVICES_ fetchOverlays];   }
	public void requestPlayerOverlays
	{
		if(this.playerDataReceived] &&
		![_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		{
			NSMutableArray *ptrigs = [[NSMutableArray alloc] init];
    /*
     // copied impl from triggers- haven't yet actually designed overlays...
    NSArray *os = [overlays allValues];
    for(int i = 0; i < os.count; i++)
    {
      Overlay *o = os[i];
      if([_MODEL_REQUIREMENTS_ evaluateRequirementRoot:o.requirement_root_package_id])
        [ptrigs addObject:o];
    }
     */
			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_OVERLAYS_RECEIVED",nil,@{@"triggers":ptrigs});
		}
		if(!this.playerDataReceived] ||
		[_MODEL_GAME_.network_level isEqualToString:@"HYBRID"] ||
		[_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
		[_SERVICES_ fetchOverlaysForPlayer];
	}

	- (Overlay *) overlayForId:(long)overlay_id
	{
		return [overlays objectForKey:[NSNumber numberWithLong:overlay_id]];
	}

	- (NSArray *) playerOverlays
	{
		return playerOverlays;
	}

}
