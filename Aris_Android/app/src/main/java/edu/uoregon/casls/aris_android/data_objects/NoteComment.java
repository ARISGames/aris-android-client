package edu.uoregon.casls.aris_android.data_objects;

import java.util.Date;

/**
 * Created by smorison on 8/19/15.
 */
public class NoteComment implements Comparable<NoteComment> {
	public long note_comment_id;
	public long note_id;
	public long user_id;
	public String name;
	public String desc;
	public String user_display_name;
	public Date created = new Date();

	public int compareTo(NoteComment ncToCompare) { // just compares create
		return created.compareTo(ncToCompare.created);
	}
}
