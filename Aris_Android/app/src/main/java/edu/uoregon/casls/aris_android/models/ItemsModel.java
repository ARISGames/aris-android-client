package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class ItemsModel {

	public Map<Long, Item> items = new LinkedHashMap<>();

	public void clearGameData() {
		items.clear();
	}

	public void requestItems() {

	}
}
