package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Event;

/**
 * Created by smorison on 8/20/15.
 */
public class EventsModel extends ARISModel {

	public Map<Long, Event> events = new LinkedHashMap<>();

	public void clearGameData() {
		events.clear();
		n_game_data_received = 0;
	}

	public void requestEvents() {

	}

	public long nGameDataToReceive () {
		return 1;
	}

}
