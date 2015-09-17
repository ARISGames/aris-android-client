package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Event;

/**
 * Created by smorison on 8/20/15.
 */
public class EventsModel {

	public Map<Long, Event> events = new LinkedHashMap<>();

	public void clearGameData() {
	}

	public void requestEvents() {

	}
}
