package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Overlay;

/**
 * Created by smorison on 8/20/15.
 */
public class OverlaysModel extends ARISModel {

	public Map<Long, Overlay> overlays = new LinkedHashMap<>();
	public List<Overlay> playerOverlays = new ArrayList<>();
	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame; // convenience ref
	}

	public long nGameDataToReceive() {
		return 1;
	}


	public void requestPlayerData() {
		this.requestPlayerOverlays();
	}

	public void clearPlayerData() {
		playerOverlays.clear();
		n_player_data_received = 0;
	}

	public void requestGameData() {
		this.requestOverlays();
	}

	public void clearGameData() {
		this.clearPlayerData();
		overlays.clear();// = [[NSMutableDictionary alloc] init];
		n_game_data_received = 0;
	}

	public void overlaysReceived(List<Overlay> newOverlays) {
		this.updateOverlays(newOverlays);
	}

	public void updateOverlays(List<Overlay> newOverlays) {
//		Overlay *newOverlay;
		Long newOverlayId;
		for (Overlay newOverlay : newOverlays) {
			newOverlayId = newOverlay.overlay_id;
			if (!overlays.containsKey(newOverlayId))
				overlays.put(newOverlayId, newOverlay);// setObject:newOverlay forKey:newOverlayId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.overlays_available(overlays); //_ARIS_NOTIF_SEND_(@"MODEL_OVERLAYS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available();//_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public List<Overlay> conformOverlaysListToFlyweight(List<Overlay> newOverlays) {
		List<Overlay> conformingOverlays = new ArrayList<>();
		Overlay o;
		for (Overlay newOverlay : newOverlays) {
			if ((o = this.overlayForId(newOverlay.overlay_id)) != null)
				conformingOverlays.add(o); // addObject:o];
		}

		return conformingOverlays;
	}

	public void playerOverlaysReceived(List<Overlay> newOverlays) {
		this.updatePlayerOverlays(this.conformOverlaysListToFlyweight(newOverlays));
	}

	public void updatePlayerOverlays(List<Overlay> newOverlays) {
		List<Overlay> addedOverlays = new ArrayList<>();
		List<Overlay> removedOverlays = new ArrayList<>();

		//find added
		boolean isNew;
		for (Overlay newOverlay : newOverlays) {
			isNew = true;
			for (Overlay oldOverlay : playerOverlays) {
				if (newOverlay.overlay_id == oldOverlay.overlay_id) isNew = false;
			}
			if (isNew) addedOverlays.add(newOverlay); // addObject:newOverlays[i]];
		}

		//find removed
		boolean removed;
		for (Overlay oldOverlay : playerOverlays) {
			removed = true;
			for (Overlay newOverlay : newOverlays) {
				if (newOverlay.overlay_id == oldOverlay.overlay_id) removed = false;
			}
			if (removed) removedOverlays.add(oldOverlay); // addObject:playerOverlays[i]];
		}

		playerOverlays = newOverlays;
		n_player_data_received++;
		if (addedOverlays.size() > 0)
			mGamePlayAct.mDispatch.overlays_new_available(addedOverlays); //@"MODEL_OVERLAYS_NEW_AVAILABLE",nil,@{@"added":addedOverlays});
		if (removedOverlays.size() > 0)
			mGamePlayAct.mDispatch.overlays_less_available(removedOverlays); //_ARIS_NOTIF_SEND_(@"MODEL_OVERLAYS_LESS_AVAILABLE",nil,@{@"removed":removedOverlays});
		mGamePlayAct.mDispatch.game_player_piece_available();  //_ARIS_NOTIF_SEND_(@"PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	public void requestOverlays() {
		mGamePlayAct.mServices.fetchOverlays();
	}

	public void requestPlayerOverlays() {
		if (this.playerDataReceived() && !mGame.network_level.contentEquals("REMOTE")) {
			List<Overlay> overlays = new ArrayList<>();
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
			// not so sure what the intention is here; code under construction? -sem
			mGamePlayAct.mDispatch.services_player_overlays_received(overlays); //_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_OVERLAYS_RECEIVED",nil,@{@"triggers":ptrigs});
		}
		if (!this.playerDataReceived() ||
				mGame.network_level.contentEquals("HYBRID") ||
				mGame.network_level.contentEquals("REMOTE"))
			mGamePlayAct.mServices.fetchOverlaysForPlayer();
	}

	public Overlay overlayForId(long overlay_id) {
		return overlays.get(overlay_id); //objectForKey:[NSNumber numberWithLong:overlay_id]];
	}

	public List<Overlay> playerOverlays() {
		return playerOverlays;
	}

}
