package edu.uoregon.casls.aris_android.object_controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.User;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;
import edu.uoregon.casls.aris_android.models.NotesModel;


public class NoteViewFragment extends Fragment {

	private OnFragmentInteractionListener mListener;

	public Note                  note;
	public NotesModel            notesModel;
	public NoteComment           noteComment;
	public Instance              instance;
	public View                  fragView;
	public ARISMediaViewFragment mediaViewFrag;

	public transient GamePlayActivity mGamePlayAct;

	public static NoteViewFragment newInstance(int sectionNumber) {
		NoteViewFragment fragment = new NoteViewFragment();
		Bundle args = new Bundle();
//		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//		fragment.setArguments(args);
		return fragment;
	}

	public static NoteViewFragment newInstance(String sectionName) {
		NoteViewFragment fragment = new NoteViewFragment();
		Bundle args = new Bundle();
//		args.putString(ARG_SECTION_NUMBER, sectionName);
//		fragment.setArguments(args);

		return fragment;
	}

	public NoteViewFragment() {
		// Required empty public constructor
		mGamePlayAct = (GamePlayActivity) getActivity();
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct;
	}

	public void initWithInstance(Instance i) {
		instance = i;
		notesModel = mGamePlayAct.mGame.notesModel; // convenience ref
		note = notesModel.noteForId(i.object_id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGamePlayAct = (GamePlayActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		fragView = inflater.inflate(R.layout.fragment_note_view, container, false);
		// Init media fragment
		mediaViewFrag = new ARISMediaViewFragment();
		mediaViewFrag.initContext(mGamePlayAct);
		// add media  frag inside this frag.
		getChildFragmentManager().beginTransaction().add(R.id.fl_note_media_view_container, mediaViewFrag).commit();
		getChildFragmentManager().executePendingTransactions();

		// back button
		ImageButton ibNoteBack = (ImageButton) fragView.findViewById(R.id.ib_note_go_back);
		ibNoteBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSelf();
			}
		});

		return fragView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.loadViewElements();
	}

	private void dismissSelf() {
		if (mListener != null) {
			mListener.fragmentNoteDismiss();
		}
	}


	private void loadViewElements() {
		// set note title
		User user = mGamePlayAct.mUsersModel.userForId(note.user_id);
		if (user != null) {
			note.user = user;
		}
		String noteTitle = note.user.display_name + " " + note.getCreatedMMDDYY();
		TextView tvNoteTitle = (TextView) fragView.findViewById(R.id.tv_note_title);
		tvNoteTitle.setText(noteTitle);

		// set note media
		if (note.media_id != 0) {
			Media media = mGamePlayAct.mMediaModel.mediaForId(note.media_id);
			mediaViewFrag.setMedia(media);
			// todo: add onclick to dismiss keyboard for comments.
		}

		// set note description
		TextView tvDesc = (TextView) fragView.findViewById(R.id.tv_note_desc);
		tvDesc.setText(note.description);

		// set note comments
		final Button btnPost = (Button)fragView.findViewById(R.id.btn_note_comment_post);
		final EditText etComment = (EditText) fragView.findViewById(R.id.et_note_comment);
//		etComment.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				btnPost.setVisibility(View.VISIBLE);
//			}
//		});
		// set button visibility when comment edittext is in/out of focus.
		etComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					btnPost.setVisibility(View.VISIBLE);
				}
				else {
					btnPost.setVisibility(View.INVISIBLE);
					hideKeyboard();
				}
			}
		});

		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { // in iOS this is all in NoteViewController.commentConfirmed
				NoteComment noteComment = new NoteComment();
				noteComment.note_id = note.note_id;
				noteComment.user_id = Long.decode(mGamePlayAct.mPlayer.user_id);
				noteComment.description = etComment.getText().toString();
				notesModel.createNoteComment(noteComment);
				List<NoteComment> newComments = notesModel.noteCommentsForNoteId(note.note_id);
				newComments.add(noteComment);
				refreshComments(newComments);
				hideKeyboard();
			}
		});
	}

	private void refreshComments(List<NoteComment> newComments) {
		// todo: draw/redraw list of comments
	}

	private void hideKeyboard() {
		// todo: drop the soft keyboard when not desired. Possibly automatic in Android?
	}

	@Override
	public void onDetach() {
		super.onDetach();
//		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void fragmentNoteDismiss();
		void gamePlayTabBarViewControllerRequestsNav();
	}

}
