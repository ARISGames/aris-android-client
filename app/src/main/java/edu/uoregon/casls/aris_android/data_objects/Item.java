package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Item {
	public long item_id;
	public String name;
	public String desc;
	public long icon_media_id;
	public long media_id;
	public Boolean droppable;
	public Boolean destroyable;
	public long max_qty_in_inventory;
	public long weight;
	public String url;
	public String type; //NORMAL, ATRIB, HIDDEN, URL
	public Boolean delta_notification;

}
