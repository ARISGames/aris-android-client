package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Tab;

/**
 * Created by smorison on 8/20/15.
 */
public class TabsModel extends ARISModel {

	public Map<Long, Tab> tabs = new HashMap<>();
	public List<Tab> playerTabs = new ArrayList<>();
	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void requestPlayerData() {
		this.requestPlayerTabs();
	}

	public void clearPlayerData() {
		playerTabs.clear();
		n_player_data_received = 0;
	}

	public void requestGameData() {
		this.requestTabs();
	}

	public void clearGameData() {
		this.clearPlayerData();
		tabs.clear();
		n_game_data_received = 0;
	}

	public void tabsReceived(List<Tab> newTabs) {
		this.updateTabs(newTabs);
	}

	public void updateTabs(List<Tab> newTabs) {
		long newTabId;
		for (Tab newTab : newTabs) {
			newTabId = newTab.tab_id;
			if (!tabs.containsKey(newTabId))
				tabs.put(newTabId, newTab); // setObject:newTab forKey:newTabId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_tabs_available(); //_ARIS_falseTIF_SEND_(@"MODEL_TABS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_falseTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestTabs() {
		mGamePlayAct.mServices.fetchTabs();
	}

	public void requestPlayerTabs() {
		if (this.playerDataReceived() && !mGame.network_level.contentEquals("REMOTE")) {
			List<Tab> ptabs = new ArrayList<>();
			Collection<Tab> ts = tabs.values();// allValues];
			for (Tab t : ts) {
				if (mGame.requirementsModel.evaluateRequirementRoot(t.requirement_root_package_id))
					ptabs.add(t); // addObject:t];
			}
			mGamePlayAct.mDispatch.services_player_tabs_received(ptabs); //_ARIS_falseTIF_SEND_(@"SERVICES_PLAYER_TABS_RECEIVED",nil,@{@"tabs":ptabs});
		}
		if (!this.playerDataReceived() ||
				mGame.network_level.contentEquals("HYBRID") ||
				mGame.network_level.contentEquals("REMOTE"))
			mGamePlayAct.mServices.fetchTabsForPlayer();
	}

	//admittedly a bit silly, but a great way to rid any risk of deviation from flyweight by catching it at the beginning
	public List<Tab> conformTabListToFlyweight(List<Tab> newTabs) {
		List<Tab> conformingTabs = new ArrayList<>();
		for (Tab t : newTabs) {
			if ((t = this.tabForId(t.tab_id)) != null)
				conformingTabs.add(t); // addObject:t];
		}

		return conformingTabs;
	}

	public void playerTabsReceived(List<Tab> newTabs) {
		this.updatePlayerTabs(this.conformTabListToFlyweight(newTabs));
	}

	public void updatePlayerTabs(List<Tab> newTabs) {
		Map<String, List<Tab>> deltas = this.findDeltasInNew(newTabs, playerTabs);
		playerTabs = newTabs; //assumes already conforms to flyweight
		n_player_data_received++;
		if (deltas.containsKey("added"))
			mGamePlayAct.mDispatch.model_tabs_new_available(deltas); //_ARIS_falseTIF_SEND_(@"MODEL_TABS_NEW_AVAILABLE",nil,deltas);
		if (deltas.containsKey("removed"))
			mGamePlayAct.mDispatch.model_tabs_less_available(deltas); //_ARIS_falseTIF_SEND_(@"MODEL_TABS_LESS_AVAILABLE",nil,deltas);
		mGamePlayAct.mDispatch.model_game_player_piece_available(); //_ARIS_falseTIF_SEND_(@"MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	Map<String, List<Tab>> findDeltasInNew(List<Tab> newTabs, List<Tab> oldTabs) {
		Map<String, List<Tab>> qDeltas = new HashMap<>(); //@{ @"added":[[NSMutableArray alloc] init], @"removed":[[NSMutableArray alloc] init] };
		qDeltas.put("added", new ArrayList<Tab>());
		qDeltas.put("removed", new ArrayList<Tab>());

		//find added
		boolean isNew;
		for (Tab newTab : newTabs) {
			isNew = true;
			for (Tab oldTab : oldTabs) {
				if (newTab.tab_id == oldTab.tab_id) isNew = false;
			}
			if (isNew) qDeltas.get("added").add(newTab);
		}

		//find removed
		boolean removed;
		for (Tab oldTab : oldTabs) {
			removed = true;
			for (Tab newTab : newTabs) {
				if (newTab.tab_id == oldTab.tab_id) removed = false;
			}
			if (removed) qDeltas.get("removed").add(oldTab);
		}

		return qDeltas;
	}

	public Tab tabForType(String t) {

		//first, search player tabs
		for (Tab tab : playerTabs) {
			if (tab.type.contentEquals(t))
				return tab;
		}

		//if not found, try to get any game tab
		Collection<Tab> gameTabs = tabs.values();
		for (Tab tab : gameTabs) {
			if (tab.type.contentEquals(t))
				return tab;
		}

		// default:
		return new Tab();
	}

	public Tab tabForId(long tab_id) {
		if (tab_id == 0) return new Tab();
		return tabs.get(tab_id);// objectForKey:[NSNumber numberWithLong:tab_id]];
	}

	public List<Tab> playerTabs() {
		return playerTabs;
	}

}
