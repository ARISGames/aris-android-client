package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Media;

/**
 * Created by smorison on 8/20/15.
 */
public class MediaModel {

	public Map<Long, Media> medias = new LinkedHashMap<>();

	public void clearGameData() {
		medias.clear();
	}

	public void clearPlayerData() {

	}

	public void requestTabs() {
	}

	public void requestPlayerTabs() {

	}
}
