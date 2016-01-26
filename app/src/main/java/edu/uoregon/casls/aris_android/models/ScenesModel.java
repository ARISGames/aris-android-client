package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Scene;

/**
 * Created by smorison on 8/20/15.
 */
public class ScenesModel extends ARISModel {

	public Scene playerScene;
	public transient GamePlayActivity mGamePlayAct;

	// array of scenes by scene_id (long)
	public Map<Long, Scene> scenes = new LinkedHashMap<>();

	public void ScenesModel() {
		// same as init() in iOS
		// may need to separate inti an actual init() method if called separately.

	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		scenes.clear();
		n_game_data_received = 0;
	}

	public void requestGameData() {
		this.requestScenes();
	}

	public void setPlayerScene(Scene s) {
		playerScene = s;
		mGamePlayAct.mGame.logsModel.playerChangedSceneId(s.scene_id); // [_MODEL_LOGS_ playerChangedSceneId:s.scene_id];
		if (!mGamePlayAct.mGame.network_level.contentEquals("LOCAL")) //if(![_MODEL_GAME_.network_level isEqualToString:@"LOCAL"])
			mGamePlayAct.mAppServices.setPlayerSceneId(s.scene_id); //[_SERVICES_ setPlayerSceneId:s.scene_id];
		mGamePlayAct.mDispatch.model_scenes_player_scene_available(); // _ARIS_NOTIF_SEND_(@"MODEL_SCENES_PLAYER_SCENE_AVAILABLE",nil,nil); // for reference; iOS messages
	}

	public void clearPlayerData() {
		playerScene = null;
		n_player_data_received = 0;
	}

	public void requestPlayerData() {
		this.requestPlayerScene();
	}

	public long nPlayerDataToReceive() {
		return 1;
	}

	public void clearMaintenanceData() {
		n_maintenance_data_received = 0;
	}

	public long nMaintenanceDataToReceive() {
		return 1;
	}

	public void requestMaintenanceData() {
		this.touchPlayerScene();
	}

	public void scenesReceived(List<Scene> newScenes) { // method here to conform with iOS version of this class
		this.updateScenes(newScenes);
	}

	public void playerSceneReceived(Scene newScene) {
		//A bit of hack verification to ensure valid scene. Ideally shouldn't be needed...
		boolean overridden = false;
		Scene s = this.sceneForId(newScene.scene_id);
		if (s.scene_id == 0) {
			overridden = true;
			s = this.sceneForId(mGamePlayAct.mGame.intro_scene_id); //received scene not valid
		}
		if (s.scene_id == 0 && scenes.size() > 0) { //received scene not valid, intro_scene not valid
			overridden = true;
			s = scenes.get(0); // allValues][0]; //choose arbitrary scene to ensure valid state
		}

		if (overridden) this.setPlayerScene(s);
		this.updatePlayerScene(s);
	}

	private void updatePlayerScene(Scene newScene) {
		playerScene = newScene;
		n_player_data_received++;
		mGamePlayAct.mDispatch.model_scenes_player_scene_available(); // _ARIS_NOTIF_SEND_(@"MODEL_SCENES_PLAYER_SCENE_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.player_piece_available(); // _ARIS_NOTIF_SEND_(@"PLAYER_PIECE_AVAILABLE",nil,nil);

	}

	private void updateScenes(List<Scene> newScenes) {
		long newSceneId;
		for (Scene newScene : newScenes) {
			newSceneId = newScene.scene_id;
			if (!scenes.containsKey(newSceneId)) scenes.put(newSceneId, newScene);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_scenes_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_SCENES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //		_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void sceneTouched() {
		n_maintenance_data_received++;
		mGamePlayAct.mDispatch.model_scene_touched(); //		_ARIS_NOTIF_SEND_(@"MODEL_SCENE_TOUCHED",nil,nil);
		mGamePlayAct.mDispatch.maintenance_piece_available(); //_ARIS_NOTIF_SEND_(@"MAINTENANCE_PIECE_AVAILABLE",nil,nil);
	}

	public void requestScenes() {
		mGamePlayAct.mAppServices.fetchScenes();
	}

	public void touchPlayerScene() {
		mGamePlayAct.mAppServices.touchSceneForPlayer();
	}

	public void requestPlayerScene() {
//		Game lGame = mGamePlayAct.mGame;
		boolean pdr = this.playerDataReceived();
		String nl = mGamePlayAct.mGame.network_level;
		if (this.playerDataReceived() && !mGamePlayAct.mGame.network_level.contentEquals("REMOTE")) {
			mGamePlayAct.mDispatch.services_player_scene_received(playerScene); //_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_SCENE_RECEIVED",nil,@{@"scene":playerScene}); //just return current
		}
		if (!this.playerDataReceived() ||
				mGamePlayAct.mGame.network_level.contentEquals("HYBRID") ||
				mGamePlayAct.mGame.network_level.contentEquals("REMOTE"))
			mGamePlayAct.mAppServices.fetchSceneForPlayer();

	}

	public long nGameDataToReceive() {
		return 1;
	}


	public Scene sceneForId(long object_id) {
		return scenes.get(object_id);
	}
}
