package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Plaque;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.models.InstancesModel;

public class PlaqueViewFragment extends Fragment {

	public Plaque plaque;
	public Instance instance;
	public InstancesModel instancesModel;
	public Tab tab;

	public GamePlayActivity mGamePlayActivity;

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	public PlaqueViewFragment() {
		// Required empty public constructor
		initContext();
	}

	public void initContext() {
		mGamePlayActivity = (GamePlayActivity) getActivity();
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
		return inflater.inflate(R.layout.fragment_plaque_view, container, false);
	}


	public void initWithInstance(Instance i) {
//		delegate = d; // Android app eschews the delegates (for now, anyway)
		instance = i;
		plaque = mGamePlayActivity.mGame.plaquesModel.plaqueForId(instance.object_id);
		if (plaque.event_package_id > 0)
			mGamePlayActivity.mGame.eventsModel.runEventPackageId(plaque.event_package_id);
//		this.title = this.tabTitle; // iOS IU stuff.
	}

	public void initWithTab(Tab t) {
//		delegate = d;
		tab = t;
		instance = mGamePlayActivity.mGame.instancesModel.instanceForId(0); //get null inst
		instance.object_type = tab.type;
		instance.object_id = tab.content_id;
		plaque = mGamePlayActivity.mGame.plaquesModel.plaqueForId(instance.object_id);
//		self.title = plaque.name; // iOS
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		}
		else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
		void onFragmentInteraction(Uri uri);
	}
}
