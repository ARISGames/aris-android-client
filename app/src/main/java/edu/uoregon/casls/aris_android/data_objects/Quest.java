package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Quest {
	public long   quest_id = 0;
	public String name     = "";
	public String description = "";

	public long   active_icon_media_id     = 0;
	public long   active_media_id          = 0;
	public String active_description = "";
	public String active_notification_type = "NONE";
	public String active_function          = "NONE";
	public long   active_event_package_id  = 0;

	public long   complete_icon_media_id     = 0;
	public long   complete_media_id          = 0;
	public String complete_description = "";
	public String complete_notification_type = "NONE";
	public String complete_function          = "NONE";
	public long   complete_event_package_id  = 0;

	public long sort_index = 0;

	public long active_requirement_root_package_id   = 0;
	public long complete_requirement_root_package_id = 0;
}
