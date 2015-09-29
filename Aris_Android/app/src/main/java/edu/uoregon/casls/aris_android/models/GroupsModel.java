package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Groups;

/**
 * Created by smorison on 9/29/15.
 */
public class GroupsModel extends ARISModel {

	public Map<Long, Groups> groups = new LinkedHashMap<>();

	public void clearGameData() {
		groups.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 1;
	}

}
