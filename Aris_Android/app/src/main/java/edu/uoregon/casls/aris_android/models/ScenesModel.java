package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Scene;

/**
 * Created by smorison on 8/20/15.
 */
public class ScenesModel extends ARISModel {

	public Scene playerScene;
	public GamePlayActivity mGamePlayAct;

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

	public void setPlayerScene(Scene s) {
		playerScene = s;
//		_ARIS_NOTIF_SEND_(@"MODEL_SCENES_PLAYER_SCENE_AVAILABLE",nil,nil); // for reference; iOS messages
	}

	public void clearPlayerData() {
	}

	public void requestScenes() {

	}

	public void scenesReceived() { // method here to conform with iOS version of this class
		this.updateScenes();
	}

	private void updateScenes() {
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_SCENES_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void sceneTouched() {
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_SCENE_TOUCHED",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void touchPlayerScene() {

	}

	public void requestPlayerScene() {

	}

	public long nGameDataToReceive ()
	{
		return 2;
	}


	public Scene sceneForId(long object_id) {
		return scenes.get(object_id);
	}
}
