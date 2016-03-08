package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Plaque implements InstantiableProtocol {
	public long   plaque_id           = 0;
	public String name                = "Plaque";
	public String description         = "Text";
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
		return "Plaque- Id:" + this.plaque_id + "\tName:" + this.name + "\tDesc:" + this.description + "\t";
	}

}

/*[
    {
        "back_button_enabled": "1",
        "continue_function": "EXIT",
        "description": "This Plaque should show up as the very first thing in the game.",
        "event_package_id": "71433",
        "game_id": "15907",
        "icon_media_id": "0",
        "media_id": "0",
        "name": "The Main Plaque",
        "plaque_id": "61879"
    }
]
*/