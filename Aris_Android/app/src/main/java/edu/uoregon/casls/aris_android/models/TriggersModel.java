package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class TriggersModel extends ARISModel {

	public Map<Long, Trigger> triggers = new LinkedHashMap<>();

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
}
