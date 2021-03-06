package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class ItemsModel extends ARISModel {

	public Map<Long, Item> items = new LinkedHashMap<>();
	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		items.clear();
		n_game_data_received = 0;
	}

	public void itemsReceived(List<Item> newItems) { // method here to conform with iOS version of this class
		this.updateItems(newItems);
	}

	private void updateItems(List<Item> newItems) {
		long newItemId;
		for (Item newItem : newItems) {
			newItemId = newItem.item_id;
			if(!items.containsKey(newItemId)) items.put(newItemId, newItem);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_items_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_ITEMS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //		_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestGameData() {
		this.requestItems();
	}

	public void requestItems() {
		mGamePlayAct.mAppServices.fetchItems();
	}

	public Map<Long, Item> items() {
		return items;
	}

// null item (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Item itemForId(long item_id) {
		if (item_id == 0) return new Item();
		return items.get(item_id);
	}

	public long nGameDataToReceive() {
		return 1;
	}
}
