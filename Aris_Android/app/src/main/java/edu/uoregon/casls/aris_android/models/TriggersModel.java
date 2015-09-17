package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class TriggersModel {

	public Map<Long, Trigger> triggers = new LinkedHashMap<>();

	public void clearGameData() {
		
	}

	public void clearPlayerData() {

	}

	public void requestTriggers() {
	}

	public void requestPlayerTriggers() {

	}
}
