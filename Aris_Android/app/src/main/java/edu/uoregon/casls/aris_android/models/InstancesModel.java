package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Instance;

/**
 * Created by smorison on 8/20/15.
 */
public class InstancesModel extends ARISModel {

	public Map<Long, Instance> instances = new LinkedHashMap<>();

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void requestInstances() {
	}

	public void requestPlayerInstances() {

	}

	public long nGameDataToReceive () {
		return 1;
	}
}
