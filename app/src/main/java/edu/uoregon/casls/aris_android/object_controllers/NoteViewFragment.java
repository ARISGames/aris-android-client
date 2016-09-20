package edu.uoregon.casls.aris_android.object_controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;


public class NoteViewFragment extends Fragment {

//	private OnFragmentInteractionListener mListener;

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
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_note_view, container, false);
		return rootView;
	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		((GamePlayActivity) activity).onSectionAttached(
//				getArguments().getInt(ARG_SECTION_NUMBER));
////		try {
////			mListener = (OnFragmentInteractionListener) activity;
////		} catch (ClassCastException e) {
////			throw new ClassCastException(activity.toString()
////					+ " must implement OnFragmentInteractionListener");
////		}
//	}

	@Override
	public void onDetach() {
		super.onDetach();
//		mListener = null;
	}

	public void initWithInstance(Instance i) {

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
//	public interface OnFragmentInteractionListener {
//		// TODO: Update argument type and name
//		public void onFragmentInteraction(Uri uri);
//	}

}
