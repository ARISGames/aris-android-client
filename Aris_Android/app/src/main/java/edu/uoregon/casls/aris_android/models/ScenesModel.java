package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Scene;

/**
 * Created by smorison on 8/20/15.
 */
public class ScenesModel {

	private Map<String, Scene> scenes = new LinkedHashMap<String, Scene>();

	public void ScenesModel() {
		// same as init() in iOS
		// may need to separate inti an actual init() method if called separately.
	}

	public void clearGameData() {
		scenes.clear();
	}

	public void clearPlayerData() {
	}

	public void requestScenes() {

	}

	public void touchPlayerScene() {

	}

	public void requestPlayerScene() {

	}
}
