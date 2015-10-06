package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Group;

/**
 * Created by smorison on 9/29/15.
 */
public class GroupsModel extends ARISModel {

	public Map<Long, Group> groups = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;
	public Group playerGroup;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

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
