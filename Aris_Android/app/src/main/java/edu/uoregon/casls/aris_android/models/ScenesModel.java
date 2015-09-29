package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Scene;

/**
 * Created by smorison on 8/20/15.
 */
public class ScenesModel extends ARISModel {

	public Scene playerScene;

	// array of scenes by scene_id (long)
	public Map<Long, Scene> scenes = new LinkedHashMap<>();

	public void ScenesModel() {
		// same as init() in iOS
		// may need to separate inti an actual init() method if called separately.

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

	public void touchPlayerScene() {

	}

	public void requestPlayerScene() {

	}

	public long nGameDataToReceive ()
	{
		return 2;
	}
}
