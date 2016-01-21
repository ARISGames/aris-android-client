package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Item {
	public long   item_id              = 0;
	public String name                 = "Item";
	public String desc                 = "";
	public long   icon_media_id        = 0;
	public long   media_id             = 0;
	public long   droppable            = 0; // boolean as long
	public long   destroyable          = 0; // boolean as long
	public long   max_qty_in_inventory = 99999999;
	public long   weight               = 0;
	public String url                  = "";
	public String type                 = "NORMAL"; //NORMAL, ATRIB, HIDDEN, URL
	public long   delta_notification   = 1; // boolean as long
}
