package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Log;

/**
 * Created by smorison on 8/20/15.
 */
public class LogsModel extends ARISModel {

	public Map<Long, Log> logs = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		this.clearPlayerData();
		n_game_data_received = 0;
	}

	public void clearPlayerData() {
		logs.clear();
	}

	public void requestPlayerLogs() {
	}

	public long nGameDataToReceive () {
		return 1;
	}

	public void playerReceivedItemId(long item_id, long qty) {
	}

	public void playerLostItemId(long item_id, long qty) {
	}

	public long[] objectIdsOfType(String item, long tag_id) {
		return new long[0]; // todo stub temp
	}
}
