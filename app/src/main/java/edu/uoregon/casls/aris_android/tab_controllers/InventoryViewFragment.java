package edu.uoregon.casls.aris_android.tab_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;
import edu.uoregon.casls.aris_android.models.PlayerInstancesModel;


public class InventoryViewFragment extends Fragment {

	private transient GamePlayActivity mGamePlayAct;
	public static     View             mThisFragsView;
	public Collection<Instance> mInstances = new ArrayList();
	public Tab                   tab;
//	public ARISMediaViewFragment mediaViewFrag;

	private OnFragmentInteractionListener mListener;

	public InventoryViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {//1
		// check for preexisting view; remove it if one is found. Otherwise get Duplicate Fragment error.
		if (mThisFragsView != null) {
			ViewGroup parent = (ViewGroup) mThisFragsView.getParent();
			if (parent != null)
				parent.removeView(mThisFragsView);
		}

		try {
			// Inflate the layout for this fragment
			mThisFragsView = inflater.inflate(R.layout.fragment_inventory_view, container, false);
		} catch (InflateException ie) {
			// do nothing; just go on.
		}


//		if (mGamePlayAct == null)
		mGamePlayAct = (GamePlayActivity) getActivity();
		mGamePlayAct.showNavBar();

//		this.updateList(); // maybe move to onResume?
		mInstances = new ArrayList<>();
		return mThisFragsView;
	}

	@Override
	public void onResume() {
		super.onResume();//4
		this.updateList(); // maybe move to onResume?
	}

	@Override
	public void onStart() {
		super.onStart();//3
	}

	@Override
	public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
		super.onInflate(context, attrs, savedInstanceState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);//2
	}

	public void updateList() { // aka refreshViews in iOS InventoryViewController
		LinearLayout llInventoryList = (LinearLayout) mThisFragsView.findViewById(R.id.ll_inventory_list);
		llInventoryList.removeAllViews(); // refresh visible views so they don't accumulate
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0, -1, 0, -1);

		Collection<Instance> instances = mGamePlayAct.mGame.playerInstancesModel.inventory().values();//playerInstances.values();

		// todo: need to examine code to make sure we're looking at the correct instances and to reinstate the qty == 0 filter loop below
		for (Instance instance : instances) { //60832
			if (instance.qty == 0)
				continue;
			mInstances.add(instance);
		}
		if (mInstances == null || mInstances.size() < 1) {
			TextView tvNoItemsMessage = new TextView(mGamePlayAct);
			tvNoItemsMessage.setText("No Items in Inventory");
			tvNoItemsMessage.setTextSize(getResources().getDimension(R.dimen.textsize_small));
			tvNoItemsMessage.setGravity(Gravity.CENTER_HORIZONTAL);
			tvNoItemsMessage.setPadding(0, 15, 0, 0);
			tvNoItemsMessage.setLayoutParams(layoutParams);
			llInventoryList.addView(tvNoItemsMessage);
		}
		// populate with inventory items.
		else {
			for (final Instance itemInstance : mInstances) {
//				Item item = itemInstance.mGamePlayAct.mGame.instancesModel.
				LayoutInflater inflater = (LayoutInflater) mGamePlayAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View itemView = inflater.inflate(R.layout.inventory_list_item, null);
				// icon/graphic
//				FrameLayout flIconMediaView = (FrameLayout) itemView.findViewById(R.id.fl_inventory_item_icon_media_view_container);
				WebView wvItemIcon = (WebView) itemView.findViewById(R.id.wv_inventory_item_icon);
				if (itemInstance.icon_media_id() == 0) {
					wvItemIcon.setBackgroundColor(0x00000000);
					wvItemIcon.setBackgroundResource(R.drawable.logo_icon); //todo: default item icon here.
				}
				else {
//				ARISMediaViewFragment mediaViewFrag = new ARISMediaViewFragment();
//				mediaViewFrag.initContext(mGamePlayAct);
//				// add media  frag inside this frag.
//				getChildFragmentManager().beginTransaction().add(flIconMediaView.getId(), mediaViewFrag).commit();
//				getChildFragmentManager().executePendingTransactions();
//
//				Media iconMedia;
////						iconMedia = mGamePlayAct.mMediaModel.mediaForId(itemInstance.icon_media_id())
//					if (itemInstance.icon_media_id() != 0)
//						iconMedia = mGamePlayAct.mMediaModel.mediaForId(itemInstance.icon_media_id());
//					else
//						iconMedia = mGamePlayAct.mMediaModel.mediaForId(Media.DEFAULT_ITEM_ICON_MEDIA_ID);
////						else if(itemInstance.media_id != 0) iconMedia = [_MODEL_MEDIA_ mediaForId:item.media_id];
//
//					if (iconMedia != null && iconMedia.type().contentEquals("IMAGE")) {
////						[iconCache setObject:iconMedia forKey:[NSNumber numberWithLong:item.item_id]];
//						mediaViewFrag.setMedia(iconMedia);
//					}
//					else if (iconMedia != null) {
//						if (iconMedia.type().contentEquals("AUDIO"))
//							mediaViewFrag.setImageFromDrawableRes("defaultAudioIcon.png");
//						if (iconMedia.type().contentEquals("VIDEO"))
//							mediaViewFrag.setImageFromDrawableRes("defaultVideoIcon.png");
//					}

//					mediaViewFrag.setMedia(media);

					wvItemIcon.getSettings().setJavaScriptEnabled(false);
					wvItemIcon.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
					wvItemIcon.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
					wvItemIcon.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//					Media itemIconMedia = mGamePlayAct.mGameMedia.get(item.icon_media_id());
//					Media media = mGamePlayActivity.mMediaModel.mediaForId(mPlaque.media_id);

					Media itemIconMedia = mGamePlayAct.mMediaModel.mediaForId(itemInstance.icon_media_id());
					String item_icon_URL;
					if (itemIconMedia.mediaCD.remoteURL != null) {
						item_icon_URL = itemIconMedia.mediaCD.remoteURL;
						String iconAsHtmlImg = "<html><body style=\"margin: 0; padding: 0\"><img src=\"" + item_icon_URL + "\" width=\"100%\" height=\"100%\"/></body></html>";
						wvItemIcon.loadData(iconAsHtmlImg, "text/html", null);
					}
					else if (itemIconMedia.mediaCD.localURL != null) {
						item_icon_URL = itemIconMedia.mediaCD.localURL;
						wvItemIcon.loadUrl(item_icon_URL);
					}
					else {
						item_icon_URL = "file:///android_res/drawable/logo_icon.png"; // when all else fails
						wvItemIcon.loadUrl(item_icon_URL);
					}

//					String iconAsHtmlImg = "<html><body style=\"margin: 0; padding: 0\"><img src=\"" + item_icon_URL + "\" width=\"100%\" height=\"100%\"/></body></html>";
//					wvItemIcon.loadData(iconAsHtmlImg, "text/html", null);
				}
				TextView tvItemName = (TextView) itemView.findViewById(R.id.tv_inventory_item_name);
				tvItemName.setText(itemInstance.name());
				TextView tvItemDesc = (TextView) itemView.findViewById(R.id.tv_inventory_item_desc);
				Item i = (Item) itemInstance.object();
				tvItemDesc.setText(i.description);
				TextView tvItemQty = (TextView) itemView.findViewById(R.id.tv_inventory_item_qty);
				tvItemQty.setText(String.valueOf(itemInstance.qty));
				llInventoryList.addView(itemView);
				itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mGamePlayAct.mGame.displayQueueModel.enqueueInstance(itemInstance); // can I do this, this way, or should I just do a fragment replace here?
					}
				});
			}
		}
	}


//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		((GamePlayActivity) activity).onSectionAttached(
//				getArguments().getString(ARG_SECTION_NAME));
//		try {
//			mListener = (OnFragmentInteractionListener) activity;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}

	@Override
	public void onDetach() {
		super.onDetach();
//		mListener = null;
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

		void gamePlayTabBarViewControllerRequestsNav();

		void fragmentInventoryDismiss();
	}
//
//	private void dismissSelf() {
//		if (tab != null)
//			this.showNav();
//		if (mListener != null) {
//			mListener.fragmentInventoryDismiss();
//		}
//		// the following iOS logic wil happen in GamePlayActivity.fragmentPlaqueExit();
//	}
//
//	private void showNav() {
//		mListener.gamePlayTabBarViewControllerRequestsNav();
//	}
//
//	@Override
//	public void onAttach(Context context) {
//		super.onAttach(context);
//		if (context instanceof OnFragmentInteractionListener) {
//			mListener = (OnFragmentInteractionListener) context;
//		}
//		else {
//			throw new RuntimeException(context.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}

}
