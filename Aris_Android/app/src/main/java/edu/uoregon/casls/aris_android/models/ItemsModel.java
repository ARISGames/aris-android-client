package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class ItemsModel extends ARISModel {

	public Map<Long, Item> items = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		items.clear();
		n_game_data_received = 0;
	}

	public void itemsReceived() { // method here to conform with iOS version of this class
		this.updateItems();
	}

	private void updateItems() {
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_items_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_ITEMS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestItems() {

	}

	public Map<Long, Item> items() {
		return items;
	}

// null item (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Item itemForId(long item_id) {
		if (item_id == 0) return new Item();
		return items.get(item_id);
	}


	public long nGameDataToReceive ()
	{
		return 1;
	}
}
