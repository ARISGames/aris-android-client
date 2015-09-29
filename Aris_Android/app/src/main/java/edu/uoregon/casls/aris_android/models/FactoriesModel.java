package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Factory;

/**
 * Created by smorison on 8/20/15.
 */
public class FactoriesModel extends ARISModel {

	public Map<Long, Factory> factories = new LinkedHashMap<>();

	public void clearGameData() {
		factories.clear();
		n_game_data_received = 0;
	}

	public void requestFactories() {

	}

	public long nGameDataToReceive () {
		return 1;
	}

}
