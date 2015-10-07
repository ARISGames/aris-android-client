package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Event;
import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class EventsModel extends ARISModel {

	public Map<Long, Event> events = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public void clearGameData() {
		events.clear();
		n_game_data_received = 0;
	}

	public void requestEvents() {

	}

	public long nGameDataToReceive () {
		return 1;
	}

	public Event eventPackageForId(long object_id) {
		return events.get(object_id);
	}

	public void runEventPackageId(long active_event_package_id) {
	}
}
