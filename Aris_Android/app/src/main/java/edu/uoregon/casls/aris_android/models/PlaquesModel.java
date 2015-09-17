package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Plaque;

/**
 * Created by smorison on 8/20/15.
 */
public class PlaquesModel {

	public Map<Long, Plaque> plaques = new LinkedHashMap<>();

	public void clearGameData() {
		plaques.clear();
	}

	public void requestPlaques() {

	}
}
