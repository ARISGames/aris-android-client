package edu.uoregon.casls.aris_android.tab_controllers;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	private GamePlayActivity mGamePlayAct   ;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment GamePlayMapFragment.
	 */
	// TODO: Rename and change types and number of parameters
//	public static GamePlayMapFragment newInstance(String param1, String param2) {
//		GamePlayMapFragment fragment = new GamePlayMapFragment();
//		Bundle args = new Bundle();
//		args.putString(ARG_PARAM1, param1);
//		args.putString(ARG_PARAM2, param2);
//		fragment.setArguments(args);
//		return fragment;
//	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static MapViewFragment newInstance(int sectionNumber) {
		MapViewFragment fragment = new MapViewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public static MapViewFragment newInstance(String sectionName) {
		MapViewFragment fragment = new MapViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SECTION_NUMBER, sectionName);
		fragment.setArguments(args);
		return fragment;
	}

	public MapViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View v = inflater.inflate(R.layout.fragment_map_view, container, false);

		Button b = (Button) v.findViewById(R.id.button);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onSecondFragButtonClick("This is the message sent when I clicked on the button in Second Fragment");

			}
		});
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct;
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
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
		public void onSecondFragButtonClick(String message);
	}

}
