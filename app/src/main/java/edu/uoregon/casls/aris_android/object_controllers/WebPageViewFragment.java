package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.WebPage;
import edu.uoregon.casls.aris_android.models.InstancesModel;


public class WebPageViewFragment extends Fragment {

	public  Instance         instance;
	public  WebPage          mWebPage;
	public  InstancesModel   instancesModel;
	public  Tab              tab;
	private WebView          wvMainWebView;
	public  GamePlayActivity mGamePlayActivity;
	View mWebPageView;

	private boolean hasAppeared;

	private OnFragmentInteractionListener mListener;

	public WebPageViewFragment() {
		mGamePlayActivity = (GamePlayActivity) getActivity();
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayActivity = gamePlayAct;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGamePlayActivity = (GamePlayActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mWebPageView = inflater.inflate(R.layout.fragment_web_page_view, container, false);

		ImageButton ibBack = (ImageButton) mWebPageView.findViewById(R.id.ib_back_button);
		// on page back button listener.
		ibBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSelf();
			}
		});

		if (!hasAppeared)
			this.viewWillAppearFirstTime();
		return mWebPageView;
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
	public void onResume() {
		super.onResume();
		// capture Android's back button.
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
					// handle back button's click listener
					dismissSelf();
					return true;
				}
				return false;
			}
		});
	}

	public void initWithInstance(Instance i) {
		mGamePlayActivity = (GamePlayActivity) getActivity();
		instance = i;
//		mWebPage = mGamePlayActivity.mGame.webPagesModel.webPageForId(i.object_id);
		hasAppeared = false;
	}

	public void viewWillAppearFirstTime() {
		hasAppeared = true;
		mWebPage = mGamePlayActivity.mGame.webPagesModel.webPageForId(instance.object_id);
		wvMainWebView = (WebView) mWebPageView.findViewById(R.id.wv_page_viewer);
		wvMainWebView.setWebViewClient(new WebViewClient());
		wvMainWebView.getSettings().setJavaScriptEnabled(true);
		wvMainWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		wvMainWebView.getSettings().setLoadWithOverviewMode(true);
		wvMainWebView.loadUrl(mWebPage.url);

//		if(!webPage || webPage.back_button_enabled)
//			self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
//
//		if(tab)
//		{
//			UIButton *threeLineNavButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 27, 27)];
//			[threeLineNavButton setImage:[UIImage imageNamed:@"threelines"] forState:UIControlStateNormal];
//			[threeLineNavButton addTarget:self action:@selector(dismissSelf) forControlEvents:UIControlEventTouchUpInside];
//			threeLineNavButton.accessibilityLabel = @"In-Game Menu";
//			self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:threeLineNavButton];
//		}
	}

	private void dismissSelf() {
		if (tab != null) {
			this.showNav();
		} else {
			wvMainWebView.destroy();
			if (mListener != null) {
				mListener.fragmentWebPageViewDismiss();
			}
		}
	}

	private void showNav() {
		mListener.gamePlayTabBarViewControllerRequestsNav();
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
		void fragmentWebPageViewDismiss();
		void onFragmentInteraction(Uri uri);
		void gamePlayTabBarViewControllerRequestsNav();
	}
}
