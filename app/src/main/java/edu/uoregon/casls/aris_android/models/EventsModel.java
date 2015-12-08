package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Event;
import edu.uoregon.casls.aris_android.data_objects.EventPackage;
import edu.uoregon.casls.aris_android.data_objects.Game;

/**
 * Created by smorison on 8/20/15.
 */
public class EventsModel extends ARISModel {

	public Map<Long, Event> events = new HashMap<>();
	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	// todo: Create Android equivalent functionality of this:
//	ARISWebView *runner; //only running one at once


	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public void clearGameData() {
		events.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void requestGameData() {
		this.requestEvents();
	}

	public void eventsReceived(List<Event> newEvents) {
		this.updateEvents(newEvents);
	}

	public void updateEvents(List<Event> newEvents) {
		long newEventId;
		for (Event newEvent : newEvents) {
			newEventId = newEvent.event_id;
			if (!events.containsKey(newEventId)) events.put(newEventId, newEvent);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.events_available(); // _ARIS_NOTIF_SEND_(@"MODEL_EVENTS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void runEventPackageId(long event_package_id) {
		List<Event> es = this.eventsForEventPackageId(event_package_id);
		for (Event e : es) {
			//legacy
			if (e.event.contentEquals("TAKE_ITEM"))
				mGame.playerInstancesModel.takeItemFromPlayer(e.content_id, e.qty);
			if (e.event.contentEquals("GIVE_ITEM"))
				mGame.playerInstancesModel.giveItemToPlayer(e.content_id, e.qty);

			if (e.event == null || e.event.contentEquals("NONE"))
				return;

			if (e.event.contentEquals("TAKE_ITEM_PLAYER"))
				mGame.playerInstancesModel.takeItemFromPlayer(e.content_id, e.qty);
			if (e.event.contentEquals("GIVE_ITEM_PLAYER"))
				mGame.playerInstancesModel.giveItemToPlayer(e.content_id, e.qty);
			if (e.event.contentEquals("SET_ITEM_PLAYER"))
				mGame.playerInstancesModel.setItemsForPlayer(e.content_id, e.qty);

			if (e.event.contentEquals("TAKE_ITEM_GAME"))
				mGame.gameInstancesModel.takeItemFromGame(e.content_id, e.qty);
			if (e.event.contentEquals("GIVE_ITEM_GAME"))
				mGame.gameInstancesModel.giveItemToGame(e.content_id, e.qty);
			if (e.event.contentEquals("SET_ITEM_GAME"))
				mGame.gameInstancesModel.setItemsForGame(e.content_id, e.qty);

			if (e.event.contentEquals("TAKE_ITEM_GROUP"))
				mGame.groupInstancesModel.takeItemFromGroup(e.content_id, e.qty);
			if (e.event.contentEquals("GIVE_ITEM_GROUP"))
				mGame.groupInstancesModel.giveItemToGroup(e.content_id, e.qty);
			if (e.event.contentEquals("SET_ITEM_GROUP"))
				mGame.groupInstancesModel.setItemsForGroup(e.content_id, e.qty);

			if (e.event.contentEquals("SET_SCENE"))
				mGame.scenesModel.setPlayerScene(mGame.scenesModel.sceneForId(e.content_id));

			if (e.event.contentEquals("SET_GROUP"))
				mGame.groupsModel.setPlayerGroup(mGame.groupsModel.groupForId(e.content_id));

			if (e.event.contentEquals("RUN_SCRIPT")) {
				// todo: Android version of this:
//				runner = [[ARISWebView alloc) initWithDelegate:self);
//				runner.userInteractionEnabled = NO;
//				[runner loadHTMLString:[NSString stringWithFormat:[ARISTemplate ARISHtmlTemplate], e.script] baseURL:nil];
			}
		}
		mGame.logsModel.playerRanEventPackageId(event_package_id);
	}

	public void requestEvents() {
		mGamePlayAct.mAppServices.fetchEvents();
	}

	public List<Event> eventsForEventPackageId(long event_package_id) {
		List<Event> package_events = new LinkedList<>();
		Collection<Event> allEvents = events.values();
		for (Event e : allEvents) {
			if (e.event_package_id == event_package_id)
				package_events.add(e);
		}
		return package_events;
	}

	public List<Event> events() {
		return new ArrayList(events.values()); // convert Collection to List for return
	}

	// null event (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Event eventForId(long event_id) {
		if (event_id == 0) return new Event();
		return events.get(event_id); // objectForKey:[NSNumber numberWithLong:event_id]];
	}

	// NOT flyweight!!! (because joke objects)
	public EventPackage eventPackageForId(long event_package_id) {
		EventPackage ep = new EventPackage();
		ep.event_package_id = event_package_id;
		return ep;
	}

}
