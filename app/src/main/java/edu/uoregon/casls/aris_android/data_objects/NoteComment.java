package edu.uoregon.casls.aris_android.data_objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 8/19/15.
 */
public class NoteComment implements Comparable<NoteComment> {
	public  long   note_comment_id   = 0;
	public  long   note_id           = 0;
	public  long   user_id           = 0;
	public  String name              = "";
	public  String desc              = "";
	public  String user_display_name = "";
	//	public Date created = new Date();
	private String created           = new SimpleDateFormat(AppConfig.GAME_DATE_FORMAT).format(new Date());

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
			return new SimpleDateFormat(AppConfig.GAME_DATE_FORMAT).parse(created);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; // satisfy default return obligation.
	}
}
