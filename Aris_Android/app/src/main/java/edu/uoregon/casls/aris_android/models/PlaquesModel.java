package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Plaque;

/**
 * Created by smorison on 8/20/15.
 */
public class PlaquesModel extends ARISModel {

	public Map<Long, Plaque> plaques = new LinkedHashMap<>();

	public void clearGameData() {
		plaques.clear();
		n_game_data_received = 0;
	}

	public void plaquesReceived() { // method here to conform with iOS version of this class
		this.updatePlaques();
	}

	private void updatePlaques() {
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_SCENES_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestPlaques() {

	}

	public long nGameDataToReceive () {
		return 1;
	}
}
