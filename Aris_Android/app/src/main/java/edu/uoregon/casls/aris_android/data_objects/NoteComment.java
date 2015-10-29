package edu.uoregon.casls.aris_android.data_objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
//	public Date created = new Date();
	private String created;

	public NoteComment() {
		setCreated(new Date());
	}

	public int compareTo(NoteComment ncToCompare) { // just compares create
		return this.getCreated().compareTo(ncToCompare.getCreated());
	}

	public void setCreated(Date created) {
		this.created = created.toString();
	}

	public Date getCreated() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(created);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; // satisfy default return obligation.
	}
}
