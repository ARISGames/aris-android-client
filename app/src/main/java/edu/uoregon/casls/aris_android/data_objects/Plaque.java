package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Plaque {
	public long   plaque_id           = 0;
	public String name                = "Plaque";
	public String desc                = "Text";
	public long   icon_media_id       = 0;
	public long   media_id            = 0;
	public long   event_package_id    = 0;
	public long   back_button_enabled = 1; // Boolean as long
	public String continue_function   = "EXIT";

	public long icon_media_id() {
		if (icon_media_id == 0) return Media.DEFAULT_PLAQUE_ICON_MEDIA_ID;
		return icon_media_id;
	}

	public String description() {
		return "Plaque- Id:" + this.plaque_id + "\tName:" + this.name + "\tDesc:" + this.desc + "\t";
	}

}
