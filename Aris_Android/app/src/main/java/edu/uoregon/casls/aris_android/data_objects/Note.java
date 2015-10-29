package edu.uoregon.casls.aris_android.data_objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
//	public Date created = new Date();
	private String created; // use set/get

	public long icon_media_id; // irrelevant?

	public Note() {
		setCreated(new Date());
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
