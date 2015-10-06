package edu.uoregon.casls.aris_android.data_objects;

import java.util.Date;

/**
 * Created by smorison on 8/19/15.
 */
public class Note {
	public long note_id;
	public long user_id;
	public String name;
	public String desc;
	public String user_display_name;
	public long media_id;
	public long tag_id;
	public long object_tag_id;
	public Date created = new Date();

	public long icon_media_id; // irrelevant?
}
