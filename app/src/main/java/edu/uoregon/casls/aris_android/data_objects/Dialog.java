package edu.uoregon.casls.aris_android.data_objects;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 8/19/15.
 */
public class Dialog {
	public int    dialog_id              = 0;
	public String name                   = "Dialog";
	public String desc                   = "";
	public int    icon_media_id          = 0;
	public int    intro_dialog_script_id = 0;
	public int    back_button_enabled    = 1;

	public Dialog copy() {
		Dialog c = new Dialog();
		c.dialog_id = this.dialog_id;
		c.name = this.name;
		c.desc = this.desc;
		c.icon_media_id = this.icon_media_id;
		c.intro_dialog_script_id = this.intro_dialog_script_id;
		c.back_button_enabled = this.back_button_enabled;
		return c;
	}

	public boolean compareTo(Dialog ob) {
		return (ob.dialog_id == this.dialog_id);
	}

	public String description() {
		return "Dialog- Id:" + this.dialog_id + "\tName:" + this.name + "\t";
	}

	public int icon_media_id() {
		if (icon_media_id == 0) return Media.DEFAULT_DIALOG_ICON_MEDIA_ID;
		return icon_media_id;
	}

}
