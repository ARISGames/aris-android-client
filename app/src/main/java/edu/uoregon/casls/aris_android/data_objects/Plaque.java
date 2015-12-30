package edu.uoregon.casls.aris_android.data_objects;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 8/19/15.
 */
public class Plaque {
	public int plaque_id;
	public String name = "Plaque";
	public String desc = "Text";
	public int icon_media_id = 0;
	public int media_id = 0;
	public int event_package_id = 0;
	public int back_button_enabled = 1;
	public String continue_function = "EXIT";

	public int icon_media_id() {
		if (icon_media_id == 0) return Media.DEFAULT_PLAQUE_ICON_MEDIA_ID;
		return icon_media_id;
	}

	public String description()
	{
		return "Plaque- Id:" + this.plaque_id + "\tName:" + this.name +"\tDesc:" + this.desc + "\t";
	}

}
