package edu.uoregon.casls.aris_android.data_objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 8/19/15.
 */
public class Note implements InstantiableProtocol {
	public  long   note_id           = 0;
	public  long   user_id           = 0;
	public  String name              = "";
	public  String desc              = "";
	public  String user_display_name = "";
	public  long   media_id          = 0;
	public  long   tag_id            = 0;
	public  long   object_tag_id     = 0;
	//	public Date created = new Date();

	private String created = new SimpleDateFormat(AppConfig.GAME_DATE_FORMAT).format(new Date()); // use set/get

	public long icon_media_id = 0; // irrelevant?

	public Note() {
		setCreated(new Date());
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

	public long icon_media_id(Game game) {
		if (game.tagsModel.tagsForObjectType("NOTE", note_id).size() > 0) { //[_MODEL_TAGS_ tagsForObjectType:@"NOTE" id:note_id].count)
			Tag tag = game.tagsModel.tagsForObjectType("NOTE", note_id).get(0);
			if (tag.media_id != 0) {
				return tag.media_id;
			}
		}

		return Media.DEFAULT_NOTE_ICON_MEDIA_ID;
	}
}
