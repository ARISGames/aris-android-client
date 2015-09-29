package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;

/**
 * Created by smorison on 8/20/15.
 */
public class NotesModel extends ARISModel {

	public Map<Long, Note> notes = new LinkedHashMap<>();
	public Map<Long, NoteComment> noteComments = new LinkedHashMap<>();

	public void clearGameData() {
		noteComments.clear();
		notes.clear();
		n_game_data_received = 0;
	}

	public void requestNotes() {
	}

	public void requestNoteComments() {
	}

	public long nGameDataToReceive () {
		return 2;
	}
}
