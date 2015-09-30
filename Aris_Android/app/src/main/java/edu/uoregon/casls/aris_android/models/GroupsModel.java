package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Group;

/**
 * Created by smorison on 9/29/15.
 */
public class GroupsModel extends ARISModel {

	public Map<Long, Group> groups = new LinkedHashMap<>();

	public void clearGameData() {
		groups.clear();
		n_game_data_received = 0;
	}

	public void groupsReceived() { // method here to conform with iOS version of this class
		this.updateGroups();
	}

	private void updateGroups() {
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_GROUPS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public long nGameDataToReceive () {
		return 1;
	}

}
