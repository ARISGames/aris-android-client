package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.Tag;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 8/20/15.
 */
public class NotesModel extends ARISModel {

	public Map<Long, Note> notes = new LinkedHashMap<>();
	public Map<Long, NoteComment> noteComments = new LinkedHashMap<>();
	public List<Note> playerNotes = new LinkedList<>();
	public List<Note> listNotes = new LinkedList<>();
	public List<Note> notesMatchingTag = new LinkedList<>();
	public GamePlayActivity mGamePlayAct;
	public Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public void clearGameData() {
		this.invalidateCaches();
		noteComments.clear();
		notes.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 2;
	}

	public void requestGameData() {
		this.requestNotes();
		this.requestNoteComments();
	}

	public void createNote(Note n, Tag t, Media m, Trigger tr) {
		mGamePlayAct.mServices.createNote(n, t, m, tr); //just forward to services
	}

	public void saveNote(Note n, Tag t, Media m, Trigger tr) {
		mGamePlayAct.mServices.updateNote(n, t, m, tr); //just forward to services
	}

	public void deleteNoteId(long note_id) {
		mGamePlayAct.mServices.deleteNoteId(note_id); //just forward to services
		notes.remove(note_id);
		this.invalidateCaches();
	}

	public void createNoteComment(NoteComment n) {
		mGamePlayAct.mServices.createNoteComment(n); //just forward to services
	}

	public void saveNoteComment(NoteComment n) {
		mGamePlayAct.mServices.updateNoteComment(n); //just forward to services
	}

	public void deleteNoteCommentId(long note_comment_id) {
		mGamePlayAct.mServices.deleteNoteCommentId(note_comment_id); //just forward to services
		noteComments.remove(note_comment_id);// removeObjectForKey:[NSNumber numberWithLong:note_comment_id]];
	}

	public void invalidateCaches() {
		playerNotes.clear();
		listNotes.clear();
		notesMatchingTag.clear();
	}

	public void notesReceived(List<Note> notes) {
		this.updateNotes(notes);
	}

	public void noteReceived(List<Note> notes) {
		this.updateNotes(notes);
	}

	public void updateNotes(List<Note> newNotes) {
		this.invalidateCaches();
		long newNoteId;
		for (Note newNote : newNotes) {
			newNoteId = newNote.note_id;
			if (!notes.containsKey(newNoteId)) notes.put(newNoteId, newNote);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_notes_avaialble(); //_ARIS_NOTIF_SEND_(@"MODEL_NOTES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestNotes() {
		mGamePlayAct.mServices.fetchNotes();
	}

	public void noteCommentsReceived(List<NoteComment> newNoteComments) {
		this.updateNoteComments(newNoteComments);
	}

	public void noteCommentReceived(List<NoteComment> newNoteComments) {
		this.updateNoteComments(newNoteComments);
	}

	public void updateNoteComments(List<NoteComment> newNoteComments) {
		long newCommentId;
		for (NoteComment newComment : newNoteComments) {
			newCommentId = newComment.note_comment_id;
			if (!noteComments.containsKey(newCommentId)) noteComments.put(newCommentId, newComment);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.note_comments_available(); //_ARIS_NOTIF_SEND_(@"MODEL_NOTE_COMMENTS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestNoteComments() {
		mGamePlayAct.mServices.fetchNoteComments();
	}

	// null note (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Note noteForId(long note_id) {
		if (note_id == 0) return new Note();
		return notes.get(note_id);
	}

	public List<Note> notes() {
		return new ArrayList(notes.values());// allValues];
	}

	public List<Note> playerNotes() {
		if (!playerNotes.isEmpty()) return playerNotes;
		Collection<Note> ns = notes.values();
		for (Note n : ns)
			if (n.user_id == Long.getLong(mGamePlayAct.mPlayer.user_id)) playerNotes.add(n);
		return playerNotes;
	}

	public List<Note> listNotes() {
		if (!listNotes.isEmpty()) return listNotes;
		for (int i = 0; i < mGame.triggersModel.playerTriggers.size(); i++) {
			Trigger trigger = mGame.triggersModel.playerTriggers.get(i);
			Instance instance = mGame.instancesModel.instanceForId(trigger.instance_id);
			if (instance.object_type.contentEquals("NOTE")) {
				Note note = (Note) instance.object();
				if (note != null) listNotes.add(note); //[listNotes addObject:note];
			}
		}

		return listNotes;
	}

	public List<Note> notesMatchingTag(Tag tag) {
		if (!notesMatchingTag.isEmpty()) return notesMatchingTag;
		Collection<Note> ns = notes.values();
		for (Note n : ns) {
			List<Tag> tags = mGame.tagsModel.tagsForObjectType("NOTE", n.note_id);
			for (Tag t : tags)
				if (tag == t) notesMatchingTag.add(n);// addObject:ns[i]];
		}
		return notesMatchingTag;
	}

	// null note (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public NoteComment noteCommentForId(long note_comment_id) {
		if (note_comment_id == 0) return new NoteComment();
		return noteComments.get(note_comment_id);
	}

	public List<NoteComment> noteComments() {
		return new ArrayList<>(noteComments.values());// allValues];
	}

	public List<NoteComment> noteCommentsForNoteId(long note_id) {
		List<NoteComment> noteCommentsMatchingNote = new ArrayList<>();
		Collection<NoteComment> ncs = noteComments.values();// allValues];
		for (NoteComment nc : ncs)
			if (nc.note_id == note_id) noteCommentsMatchingNote.add(nc);

		// sort by created field and return
//		return _ARIS_ARRAY_SORTED_ON_(noteCommentsMatchingNote,@"created");
		Collections.sort(noteCommentsMatchingNote);
		return noteCommentsMatchingNote;
	}

}
