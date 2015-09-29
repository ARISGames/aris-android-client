package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Log;

/**
 * Created by smorison on 8/20/15.
 */
public class LogsModel extends ARISModel {

	public Map<Long, Log> logs = new LinkedHashMap<>();

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
}
