package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;

public class ItemViewFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	public GamePlayActivity mGamePlayActivity;

	public Item mItem;
	public Instance instance;
	public ARISMediaViewFragment mediaViewFrag;
	public View                  mItemFragView;

	public ItemViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mGamePlayActivity = (GamePlayActivity)getActivity();
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	public void initWithInstance(Instance i) {
		instance = i;
		mItem = mGamePlayActivity.mGame.itemsModel.itemForId(i.object_id);
		
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayActivity = gamePlayActivity;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mItemFragView = inflater.inflate(R.layout.fragment_item_view, container, false);

		TextView tvItemName = (TextView) mItemFragView.findViewById(R.id.tv_item_name);
		tvItemName.setText(mItem.name);
		mediaViewFrag = new ARISMediaViewFragment();
		mediaViewFrag.initContext(mGamePlayActivity);
		// add a frag inside another (this) frag.
		getChildFragmentManager().beginTransaction().add(R.id.fl_item_media_view_container, mediaViewFrag).commit();
		getChildFragmentManager().executePendingTransactions();

		ImageButton ibGoBack = (ImageButton) mItemFragView.findViewById(R.id.ib_item_go_back);
		ibGoBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSelf();
			}
		});
		RelativeLayout rlPickUpClickArea = (RelativeLayout) mItemFragView.findViewById(R.id.rl_item_pick_up_footer);
		rlPickUpClickArea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Pick up item
			}
		});

		loadItemMediaView();
		
		return mItemFragView;
	}

	private void loadItemMediaView() {
		Media media = mGamePlayActivity.mMediaModel.mediaForId(mItem.media_id);
		if (media != null) {
			Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, "PlaqueViewFragment. setting webview with Plaque media; ");
			mediaViewFrag.setMedia(media);
//			[mediaView setMedia:media];
		}

	}

	private void dismissSelf() {
//		if (tab != null) // todo
//			this.showNav();
		if (mListener != null) {
			mListener.fragmentItemViewDismiss();
		}
		// the following iOS logic wil happen in GamePlayActivity.fragmentPlaqueExit();
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

	public interface OnFragmentInteractionListener {
		void fragmentItemViewDismiss();
		void onFragmentInteraction(Uri uri);
	}
}
