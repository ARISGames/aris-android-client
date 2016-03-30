package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Plaque;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;
import edu.uoregon.casls.aris_android.models.InstancesModel;

public class PlaqueViewFragment extends Fragment {

	public Plaque plaque;
	public Instance instance;
	public InstancesModel instancesModel;
	public Tab tab;
	public ARISMediaViewFragment mediaViewFrag;
	public View                  mPlaqueView;

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

	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayActivity = gamePlayAct;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mGamePlayActivity = (GamePlayActivity) getActivity(); //??
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
		mPlaqueView = inflater.inflate(R.layout.fragment_plaque_view, container, false);
		FragmentTransaction ft = mGamePlayActivity.getSupportFragmentManager().beginTransaction();
		// Init fragment
		mediaViewFrag = new ARISMediaViewFragment();
		mediaViewFrag.initContext(mGamePlayActivity);
		// add a frag inside another (this) frag.
		getChildFragmentManager().beginTransaction().add(R.id.fl_plaque_media_view_container, mediaViewFrag).commit();
		getChildFragmentManager().executePendingTransactions();

		return mPlaqueView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// presumably the media fragment has now loaded and passed initial lifecycle calls so we can
		// tell it to load stuff.
		this.loadView();
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

	// todo: call from onCreateView?
	public void loadView()
	{

//		mediaView = [[ARISMediaView alloc] initWithDelegate:self];
//		[mediaView setDisplayMode:ARISMediaDisplayModeTopAlignAspectFitWidthAutoResizeHeight];

//		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//		transaction.add(R.id.ll_plaque_media_view_container, mediaViewFrag).commit();

		// add a frag inside another (this) frag.
//		getChildFragmentManager().beginTransaction().add(R.id.fl_plaque_media_view_container, mediaViewFrag).commit();
//		getChildFragmentManager().executePendingTransactions();

		// Show Continue text and forward button if continue_function != NONE
		// In Android: Hide these features if continue_function == NONE
		if (plaque.continue_function.contentEquals("NONE")) {
			RelativeLayout continueFooter = (RelativeLayout) mPlaqueView.findViewById(R.id.rl_plaque_footer);
			continueFooter.setVisibility(View.INVISIBLE);
		}
		else {
			ImageView ivContinueArrow = (ImageView) mPlaqueView.findViewById(R.id.iv_plaque_footer_right_arrow);
			ivContinueArrow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					continueButtonTouched(v);
				}
			});
			ivContinueArrow.bringToFront();
			ivContinueArrow.setClickable(true);
		}

		this.loadPlaque();
	}

	public void loadPlaque()
	{
		if (!plaque.name.isEmpty()) { // set plaqueue title
			TextView tvPlaqueueTitle = (TextView) mPlaqueView.findViewById(R.id.tv_plaque_title);
			tvPlaqueueTitle.setText(plaque.name);
		}
		if (!plaque.description.contentEquals("")) { // load the description webview
//			[scrollView addSubview:webView];
//			webView.frame = CGRectMake(0, 0, self.view.bounds.size.width, 10);//Needs correct width to calc height
//			[webView loadHTMLString:[NSString stringWithFormat:[ARISTemplate ARISHtmlTemplate], plaque.description] baseURL:nil];
			WebView wvDialogOptionPrompt = (WebView) mPlaqueView.findViewById(R.id.wv_plaque_desc);
			wvDialogOptionPrompt.getSettings().setJavaScriptEnabled(true);
			wvDialogOptionPrompt.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
			wvDialogOptionPrompt.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
//		    	wvDialogOptionPrompt.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			String dialogOptionHtml = "<html><body>" + plaque.description + "</body></html>";
			wvDialogOptionPrompt.loadData(dialogOptionHtml, "text/html", null);
		}

		// load associated media into media fragment todo: may just want to put fragment into includable view with ordinary class?
		Media media = mGamePlayActivity.mMediaModel.mediaForId(plaque.media_id);
		if (media != null) {
			mediaViewFrag.setMedia(media);
//			[mediaView setMedia:media];
		}
	}

	public void continueButtonTouched(View v) {
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " continueButtonTouched. plaque.continue_function = " + plaque.continue_function);

		if (plaque.continue_function.contentEquals("JAVASCRIPT")) {
			// todo: [webView hookWithParams:@""];
		} else if (plaque.continue_function.contentEquals("EXIT")) {
			this.dismissSelf();
		}
	}

	private void dismissSelf() {
		if (tab != null)
			this.showNav();
		if (mListener != null) {
			mListener.fragmentPlaqueDismiss();
		}
		// the following iOS logic wil happen in GamePlayActivity.fragmentPlaqueExit();
	}

	private void showNav() {
		mListener.gamePlayTabBarViewControllerRequestsNav();
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
	public void onDestroyView() {
		super.onDestroyView();
		mGamePlayActivity.viewingInstantiableObject = false;
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
		void fragmentPlaqueDismiss();
		void onFragmentInteraction(Uri uri);
		void gamePlayTabBarViewControllerRequestsNav();
	}
}
