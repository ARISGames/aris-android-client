package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Plaque;

/**
 * Created by smorison on 8/20/15.
 */
public class PlaquesModel extends ARISModel {

	public Map<Long, Plaque> plaques = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		plaques.clear();
		n_game_data_received = 0;
	}

	public void plaquesReceived() { // method here to conform with iOS version of this class
		this.updatePlaques();
	}

	private void updatePlaques() {
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_scenes_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_SCENES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestPlaques() {

	}

	public long nGameDataToReceive () {
		return 1;
	}

	public Plaque plaqueForId(long object_id) {
		return plaques.get(object_id);
	}
}
