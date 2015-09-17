package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;

/**
 * Created by smorison on 8/20/15.
 */
public class NotesModel {

	public Map<Long, Note> notes = new LinkedHashMap<>();
	public Map<Long, NoteComment> noteComments = new LinkedHashMap<>();

	public void clearGameData() {

	}

	public void requestNotes() {
	}

	public void requestNoteComments() {
	}
}
