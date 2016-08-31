package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Tab;
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

	public Item                  mItem;
	public Tab                   tab;
	public Instance              instance;
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

	public void initWithTab(Tab t) {
		tab = t;
		instance = mGamePlayActivity.mGame.instancesModel.instanceForId(0); //get null inst
		instance.object_type = tab.type;
		instance.object_id = tab.content_id;
		mItem = mGamePlayActivity.mGame.itemsModel.itemForId(instance.object_id);
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

		if (tab != null) {
			ImageButton ibToggleTab = (ImageButton) mItemFragView.findViewById(R.id.ib_item_tabmenu_or_go_back);
			ibToggleTab.setImageResource(R.drawable.threelines);
			ibToggleTab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// toggle to tab.
					mGamePlayActivity.onClickMapOpenDrawer(mItemFragView); // todo: untested
				}
			});

		}
		else {
			ImageButton ibGoBack = (ImageButton) mItemFragView.findViewById(R.id.ib_item_tabmenu_or_go_back);
			ibGoBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissSelf();
				}
			});
		}
		LinearLayout llPickUpClickArea = (LinearLayout) mItemFragView.findViewById(R.id.ll_item_pick_up_footer);
		setUpButtons();
		return mItemFragView;
	}

	private void setUpButtons() {
		if (true) { // todo: logic for buttons
			TextView tvDestroy = (TextView) mItemFragView.findViewById(R.id.tv_item_destroy);
			tvDestroy.setVisibility(View.VISIBLE);
			tvDestroy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					destroyItem();
				}
			});
		}
		if (true) { // todo: logic for buttons
			TextView tvPickUp = (TextView) mItemFragView.findViewById(R.id.tv_item_destroy);
			tvPickUp.setVisibility(View.VISIBLE);
			tvPickUp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					pickUpItem();
				}
			});
		}
		if (true) { // todo: logic for buttons
			TextView tvDrop = (TextView) mItemFragView.findViewById(R.id.tv_item_destroy);
			tvDrop.setVisibility(View.VISIBLE);
			tvDrop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					dropItem();
				}
			});
		}
	}

	private void destroyItem() {

	}

	public void pickUpItem() {

	}

	private void dropItem() {

	}

	@Override
	public void onResume() {
		super.onResume();
		// presumably the media fragment has now loaded and passed initial lifecycle calls so we can
		// tell it to load stuff.
		if (mItem.type.equalsIgnoreCase("URL") && mItem.url != null && !mItem.url.isEmpty() & !mItem.url.contentEquals("0"))
			loadItemInWebView();
		else
			this.loadItemInMediaView();
		this.loadItemDescriptionWebView();
	}

	private void loadItemInWebView() {
		WebView wvItemAsURL = (WebView) mItemFragView.findViewById(R.id.wv_item_as_url);
		wvItemAsURL.setVisibility(View.VISIBLE);
		wvItemAsURL.getSettings().setJavaScriptEnabled(true);
		wvItemAsURL.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		wvItemAsURL.getSettings().setLoadWithOverviewMode(true);
		wvItemAsURL.loadUrl(mItem.url);
//		String htmlBlock = "<html><body>" + mItem.url + "</body></html>";
//		wvItemAsURL.loadData(htmlBlock, "text/html", null);
	}

	private void loadItemInMediaView() {
		// Init Media View Child Fragment
		mediaViewFrag = new ARISMediaViewFragment();
		mediaViewFrag.initContext(mGamePlayActivity);
		// add a frag inside another (this) frag.
		getChildFragmentManager().beginTransaction().add(R.id.fl_item_media_view_container, mediaViewFrag).commit();
		getChildFragmentManager().executePendingTransactions();

		Media media = mGamePlayActivity.mMediaModel.mediaForId(mItem.media_id);
		if (media != null) {
			Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, "ItemViewFragment. setting webview with Item media; ");
			mediaViewFrag.setMedia(media);
//			[mediaView setMedia:media];
		}
		else {
			// todo: show default icon
		}

	}

	private void loadItemDescriptionWebView() {
		if (!mItem.description.contentEquals("")) {
			WebView wvItemDescription = (WebView) mItemFragView.findViewById(R.id.wv_item_desc);
			wvItemDescription.getSettings().setJavaScriptEnabled(true);
			wvItemDescription.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
			wvItemDescription.getSettings().setLoadWithOverviewMode(true);
			String htmlBlock = "<html><body>" + mItem.description + "</body></html>";
			wvItemDescription.loadData(htmlBlock, "text/html", null);
		}
	}

	private void dismissSelf() {
//		if (tab != null) // todo
//			this.showNav();
		if (mListener != null) {
			mListener.fragmentItemViewDismiss();
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

	public interface OnFragmentInteractionListener {
		void fragmentItemViewDismiss();
		void onFragmentInteraction(Uri uri);
	}
}
