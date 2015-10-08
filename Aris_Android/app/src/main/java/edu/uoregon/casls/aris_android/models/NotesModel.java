package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
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
	public GamePlayActivity mGamePlayAct;
	public Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

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

	public void requestGameData
	{
		this.requestNotes];
		this.requestNoteComments];
	}
	public void clearGameData
	{
		this.invalidateCaches];
		notes         = [[NSMutableDictionary alloc] init];
		note_comments = [[NSMutableDictionary alloc] init];
		n_game_data_received = 0;
	}

	public void createNote(Note n, Tag t, Media m, Trigger tr)
	{
		mGamePlayAct.mServices.createNote(n, t, m, tr); //just forward to services
	}
	public void saveNote(Note n, Tag t, Media m, Trigger tr)
	{
		mGamePlayAct.mServices.updateNote(n, t, m, tr); //just forward to services
	}
	public void deleteNoteId(long)note_id
	{
		mGamePlayAct.mServices.deleteNoteId:note_id]; //just forward to services
		[notes removeObjectForKey:[NSNumber numberWithLong:note_id]];
		this.invalidateCaches];
	}

	public void createNoteComment:(NoteComment *)n
	{
		mGamePlayAct.mServices.createNoteComment:n]; //just forward to services
	}
	public void saveNoteComment:(NoteComment *)n
	{
		mGamePlayAct.mServices.updateNoteComment:n]; //just forward to services
	}
	public void deleteNoteCommentId:(long)note_comment_id
	{
		mGamePlayAct.mServices.deleteNoteCommentId:note_comment_id]; //just forward to services
		[note_comments removeObjectForKey:[NSNumber numberWithLong:note_comment_id]];
	}

	public void invalidateCaches()
	{
		playerNotes = nil;
		listNotes = nil;
		notesMatchingTag = nil;
	}

	public void notesReceived:(NSNotification *)notif
	{
		this.updateNotes:notif.userInfo[@"notes"]];
	}

	public void noteReceived:(NSNotification *)notif
	{
		this.updateNotes:@[notif.userInfo[@"note"]]];
	}

	public void updateNotes:(NSArray *)newNotes
	{
		this.invalidateCaches];
		Note *newNote;
		NSNumber *newNoteId;
		for(long i = 0; i < newNotes.count; i++)
		{
			newNote = [newNotes objectAtIndex:i];
			newNoteId = [NSNumber numberWithLong:newNote.note_id];
			if(!notes[newNoteId]) [notes setObject:newNote forKey:newNoteId];
		}
		n_game_data_received++;
		_ARIS_NOTIF_SEND_(@"MODEL_NOTES_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestNotes
	{
		mGamePlayAct.mServices.fetchNotes];
	}

	public void noteCommentsReceived:(NSNotification *)notif
	{
		this.updateNoteComments:notif.userInfo[@"note_comments"]];
	}

	public void noteCommentReceived:(NSNotification *)notif
	{
		this.updateNoteComments:@[notif.userInfo[@"note_comment"]]];
	}

	public void updateNoteComments:(NSArray *)newNoteComments
	{
		NoteComment *newComment;
		NSNumber *newCommentId;
		for(long i = 0; i < newNoteComments.count; i++)
		{
			newComment = [newNoteComments objectAtIndex:i];
			newCommentId = [NSNumber numberWithLong:newComment.note_comment_id];
			if(!note_comments[newCommentId]) [note_comments setObject:newComment forKey:newCommentId];
		}
		n_game_data_received++;
		_ARIS_NOTIF_SEND_(@"MODEL_NOTE_COMMENTS_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestNoteComments
	{
		mGamePlayAct.mServices.fetchNoteComments];
	}

// null note (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	- (Note *) noteForId:(long)note_id
	{
		if(!note_id) return [[Note alloc] init];
		return notes[[NSNumber numberWithLong:note_id]];
	}

	- (NSArray *) notes
	{
		return [notes allValues];
	}

	- (NSArray *) playerNotes
	{
		if(playerNotes) return playerNotes;
		playerNotes = [[NSMutableArray alloc] init];
		NSArray *ns = [notes allValues];
		for(long i = 0; i < ns.count; i++)
			if(((Note *)ns[i]).user_id == _MODEL_PLAYER_.user_id) [playerNotes addObject:ns[i]];
		return playerNotes;
	}

	- (NSArray *) listNotes
	{
		if(listNotes) return listNotes;
		listNotes = [[NSMutableArray alloc] init];

		for(long i = 0; i < _MODEL_TRIGGERS_.playerTriggers.count; i++)
		{
			Trigger  *trigger  = _MODEL_TRIGGERS_.playerTriggers[i];
			Instance *instance = [_MODEL_INSTANCES_ instanceForId:trigger.instance_id];

			if([instance.object_type isEqualToString:@"NOTE"])
			{
				Note *note = (Note *)instance.object;
				if(note) [listNotes addObject:note];
			}
		}

		return listNotes;
	}

	- (NSArray *) notesMatchingTag:(Tag*)tag
	{
		if(notesMatchingTag) return notesMatchingTag;
		notesMatchingTag = [[NSMutableArray alloc] init];
		NSArray *ns = [notes allValues];
		for(long i = 0; i < ns.count; i++)
		{
			NSArray *tags = [_MODEL_TAGS_ tagsForObjectType:@"NOTE" id:((Note *)ns[i]).note_id];
			for(long j = 0; j < tags.count; j++)
				if(tag == tags[j]) [notesMatchingTag addObject:ns[i]];
		}
		return notesMatchingTag;
	}

// null note (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	- (NoteComment *) noteCommentForId:(long)note_comment_id
	{
		if(!note_comment_id) return [[NoteComment alloc] init];
		return note_comments[[NSNumber numberWithLong:note_comment_id]];
	}

	- (NSArray *) noteComments
	{
		return [note_comments allValues];
	}

	- (NSArray *) noteCommentsForNoteId:(long)note_id
	{
		NSMutableArray *noteCommentsMatchingNote = [[NSMutableArray alloc] init];
		NSArray *ncs = [note_comments allValues];
		for(long i = 0; i < ncs.count; i++)
			if(((NoteComment *)ncs[i]).note_id == note_id) [noteCommentsMatchingNote addObject:ncs[i]];
		return _ARIS_ARRAY_SORTED_ON_(noteCommentsMatchingNote,@"created");
	}

	public Note noteForId(long object_id) {
		return notes.get(object_id);
	}
}
