package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class ItemsModel extends ARISModel {

	public Map<Long, Item> items = new LinkedHashMap<>();

	public void clearGameData() {
		items.clear();
		n_game_data_received = 0;
	}

	public void itemsReceived() { // method here to conform with iOS version of this class
		this.updateItems();
	}

	private void updateItems() {
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_ITEMS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
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
