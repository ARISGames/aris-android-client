package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Group;

/**
 * Created by smorison on 9/29/15.
 */
public class GroupsModel extends ARISModel {

	public Map<Long, Group> groups = new LinkedHashMap<>();
	public transient GamePlayActivity mGamePlayAct;
	public Group playerGroup;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		groups.clear();
		n_game_data_received = 0;
	}

	public void requestGameData() {
		this.requestGroups();
		this.touchPlayerGroup();
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void requestPlayerData() {
		this.requestPlayerGroup();
	}

	public void clearPlayerData() {
		playerGroup = null;
		n_player_data_received = 0;
	}

	public void clearMaintenanceData() {
		n_maintenance_data_received = 0;
	}

	public long nMaintenanceDataToReceive() {
		return 1;
	}

	public void requestMaintenanceData() {
		this.touchGroupInstances();
	}

	public long nPlayerDataToReceive() {
		return 1;
	}

	public void groupsReceived(List<Group> newGroups) {
		this.updateGroups(newGroups);
	}

	public void touchGroupInstances() {
		mGamePlayAct.mAppServices.touchItemsForGroups();
	}

	public void playerGroupReceived(Group newGroup) {
//		Group s = this.groupForId(((Group)notif.userInfo["group"]).group_id); //todo: figure out if this is needed for now I'm shorting the circuit
//		this.updatePlayerGroup(s);
		this.updatePlayerGroup(newGroup);
	}

	public void groupTouched() {
		n_maintenance_data_received++;
		mGamePlayAct.mDispatch.model_group_touched(); //_ARIS_NOTIF_SEND_("MODEL_GROUP_TOUCHED", null, null);
		mGamePlayAct.mDispatch.maintenance_piece_available(); //_ARIS_NOTIF_SEND_(@"MAINTENANCE_PIECE_AVAILABLE",nil,nil);
	}

	public void updateGroups(List<Group> newGroups) {
		long newGroupId;
		for (Group newGroup : newGroups) {
			newGroupId = newGroup.group_id;
			if (!groups.containsKey(newGroupId)) {
				groups.put(newGroupId, newGroup); //setObject:newGroup forKey:newGroupId];
			}
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_groups_available(); //_ARIS_NOTIF_SEND_("MODEL_GROUPS_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_("GAME_PIECE_AVAILABLE", null, null);
	}

	public void updatePlayerGroup(Group newGroup) {
		playerGroup = newGroup;
		n_player_data_received++;
		mGamePlayAct.mDispatch.model_groups_player_group_available(); //_ARIS_NOTIF_SEND_("MODEL_GROUPS_PLAYER_GROUP_AVAILABLE", null, null);
		mGamePlayAct.mDispatch.game_player_piece_available(); //_ARIS_NOTIF_SEND_("PLAYER_PIECE_AVAILABLE", null, null);
	}

	public void requestGroups() {
		mGamePlayAct.mAppServices.fetchGroups();
	}

	public void touchPlayerGroup() {
		mGamePlayAct.mAppServices.touchGroupForPlayer();
	}

	public void requestPlayerGroup() {
		if (this.playerDataReceived() && !mGamePlayAct.mGame.network_level.equals("REMOTE")) {
			mGamePlayAct.mDispatch.services_player_group_received(playerGroup); //_ARIS_NOTIF_SEND_("SERVICES_PLAYER_GROUP_RECEIVED", null, @{@"group":playerGroup}); //just return current
		}
		if (!this.playerDataReceived() || mGamePlayAct.mGame.network_level.equals("HYBRID") || mGamePlayAct.mGame.network_level.equals("REMOTE")) {
			mGamePlayAct.mAppServices.fetchGroupForPlayer();
		}
	}

	public Group playerGroup() {
		return playerGroup;
	}

	public void setPlayerGroup(Group g) {
		playerGroup = g;
		mGamePlayAct.mGame.logsModel.playerChangedGroupId(g.group_id);
		if (!mGamePlayAct.mGame.network_level.equals("LOCAL")) {
			mGamePlayAct.mAppServices.setPlayerGroupId(g.group_id);
		}
		mGamePlayAct.mDispatch.model_groups_player_group_available(); //_ARIS_NOTIF_SEND_("MODEL_GROUPS_PLAYER_GROUP_AVAILABLE", null, null);
	}

	//null group (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Group groupForId(long group_id) {
		if (group_id == 0) {
			return new Group();
		}
		return groups.get(group_id);
	}

}
