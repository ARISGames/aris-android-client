package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.uoregon.casls.aris_android.ARISWebView;
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
	public  GamePlayActivity              mGamePlayActivity;

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
		mGamePlayActivity = (GamePlayActivity) getActivity();
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

		refreshTitle();

		if (tab != null) {
			ImageButton ibToggleTab = (ImageButton) mItemFragView.findViewById(R.id.ib_item_go_back);
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
			ImageButton ibGoBack = (ImageButton) mItemFragView.findViewById(R.id.ib_item_go_back);
			ibGoBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissSelf();
				}
			});
		}
		LinearLayout llPickUpClickArea = (LinearLayout) mItemFragView.findViewById(R.id.ll_item_pick_up_footer);
		updateViewButtons();
		return mItemFragView;
	}

	public void refreshTitle() { // todo: Necessary in android?
		TextView tvItemName = (TextView) mItemFragView.findViewById(R.id.tv_item_name);
		if (instance.qty < 2 || instance.infinite_qty == 1)
			tvItemName.setText(mItem.name);
		else
			tvItemName.setText(mItem.name + " x" + instance.qty);
	}

	private void updateViewButtons() {
		TextView tvDestroy = (TextView) mItemFragView.findViewById(R.id.tv_item_destroy);
		TextView tvDrop = (TextView) mItemFragView.findViewById(R.id.tv_item_drop);
		TextView tvPickUp = (TextView) mItemFragView.findViewById(R.id.tv_item_pick_up);

		if (instance.owner_id == Long.decode(mGamePlayActivity.mPlayer.user_id) && mItem.destroyable == 1) { // show destroy button?
			tvDestroy.setVisibility(View.VISIBLE);
			tvDestroy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					destroyItemClicked();
				}
			});
		}
		else tvDestroy.setVisibility(View.GONE);

		if (instance.owner_id == Long.decode(mGamePlayActivity.mPlayer.user_id) && mItem.droppable == 1) { // show drop button?
			tvDrop.setVisibility(View.VISIBLE);
			tvDrop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					dropItemClicked();
				}
			});
		}
		else tvDrop.setVisibility(View.GONE);

		if (instance.owner_id == 0 && (instance.qty > 0 || instance.infinite_qty == 1)) { // Pick up button visible?
			tvPickUp.setVisibility(View.VISIBLE);
			tvPickUp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Pick up item
					pickUpItemClicked();
				}
			});
		}
		else tvPickUp.setVisibility(View.GONE);

	}

	private void destroyItemClicked() {
		long amtCanDestroy = mGamePlayActivity.mGame.playerInstancesModel.qtyOwnedForItem(mItem.item_id);

		if (amtCanDestroy > 1) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(mGamePlayActivity);
			final EditText etQty = new EditText(mGamePlayActivity);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(10, 0, 10, 0);
			etQty.setLayoutParams(lp);
			etQty.setHint("Qty?");
			etQty.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			alertDialog.setView(etQty);
			alertDialog.setTitle("Destroy How Many?");
			alertDialog.setMessage("Enter quantity to destroy");
			alertDialog.setPositiveButton("Destroy", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton) {
					long qty = Long.decode(etQty.getText().toString().isEmpty() ? "1" : etQty.getText().toString());
					destroyItemQty(qty);
				}
			} );
			alertDialog.show();
		}
		else if (amtCanDestroy > 0)
			dropItemQty(1);
	}

	private void destroyItemQty(long qty) {
		if (mGamePlayActivity.mGame.playerInstancesModel.takeItemFromPlayer(mItem.item_id, qty) == 0) {
			this.dismissSelf();
		}
		else {
			this.updateViewButtons();
			this.refreshTitle();
		}
	}

//	public long qty;
	private void dropItemClicked() {
		long amtCanDrop = mGamePlayActivity.mGame.playerInstancesModel.qtyOwnedForItem(mItem.item_id);

		if (amtCanDrop > 1) {
//			long qty;
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(mGamePlayActivity);
			final EditText etQty = new EditText(mGamePlayActivity);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(10, 0, 10, 0);
			etQty.setLayoutParams(lp);
			etQty.setHint("Qty?");
			etQty.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			alertDialog.setView(etQty);
			alertDialog.setTitle("Drop How Many?");
			alertDialog.setMessage("Enter quantity to drop");
			alertDialog.setPositiveButton("Drop", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton) {
					long qty = Long.decode(etQty.getText().toString().isEmpty() ? "1" : etQty.getText().toString());
					dropItemQty(qty);
				}
			} );
			alertDialog.show();

		}
		else if (amtCanDrop > 0)
			dropItemQty(1);
	}

	private void dropItemQty(long qty) {
		if (mGamePlayActivity.mGame.playerInstancesModel.dropItemFromPlayer(mItem.item_id, qty) == 0) {
			this.dismissSelf();
		}
		else {
			this.updateViewButtons();
			this.refreshTitle();
		}

	}

	public void pickUpItemClicked() {
//		lastbuttontouched = 2;
		long amtMoreCanHold = mGamePlayActivity.mGame.playerInstancesModel.qtyAllowedToGiveForItem(mItem.item_id); //[_MODEL_PLAYER_INSTANCES_ qtyAllowedToGiveForItem:item.item_id];
		long allowablePickupAmt = instance.infinite_qty == 1 ? 99999999 : instance.qty;
		if (amtMoreCanHold < allowablePickupAmt) allowablePickupAmt = amtMoreCanHold;

		if (allowablePickupAmt == 0) {
//			[[ARISAlertHandler sharedAlertHandler] showAlertWithTitle:@"Unable to Pick Up" message:@"Max qty already owned."];
			Toast t = Toast.makeText(mGamePlayActivity, "Unable to Pick Up this item. You already have as many as you are allowed.",
					Toast.LENGTH_LONG);
			t.setGravity(Gravity.TOP, 0, 0);
			t.show();

			return;
		}
		else if (allowablePickupAmt > 1 && instance.infinite_qty == 0) {
//			ItemActionViewController *itemActionVC = [[ItemActionViewController alloc] initWithPrompt:NSLocalizedString(@"ItemPickupKey", @"") positive:YES maxqty:allowablePickupAmt delegate:self];
			final AlertDialog alertDialog = new AlertDialog.Builder(mGamePlayActivity).create();
			final EditText etQty = new EditText(mGamePlayActivity);
			etQty.setHint("Qty?");
			etQty.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			alertDialog.setTitle("Pick Up How Many?");
			alertDialog.setMessage("Enter quantity");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Pick Up", Message.obtain());
			alertDialog.setView(etQty);
			alertDialog.show();

			long qty = Long.decode(etQty.getText().toString().isEmpty() ? "1" : etQty.getText().toString());
			pickupItemQty(qty);
		}
		else this.pickupItemQty(1);
	}

	private void pickupItemQty(long q) {
//		mGamePlayActivity.mGame.playerInstancesModel.giveItemToPlayer(mItem.item_id, q); //	[_MODEL_PLAYER_INSTANCES_ giveItemToPlayer:item.item_id qtyToAdd:q];
		// (MT) There used to be a Toast notification here; moved to Dispatcher.model_instances_player_{gained,lost}
		mGamePlayActivity.mGame.playerInstancesModel.giveItemToPlayer(mItem.item_id, q);

		long nq = instance.qty - q;
		mGamePlayActivity.mGame.instancesModel.setQtyForInstanceId(mItem.item_id, nq); //[_MODEL_INSTANCES_ setQtyForInstanceId:instance.instance_id qty:nq];
		instance.qty = nq; //should get set in above call- but if bogus instance, can't hurt to force it
		this.updateViewButtons(); //[self updateViewButtons];
		this.refreshTitle(); // [self refreshTitle];

		this.dismissSelf();
	}

	@Override
	public void onResume() {
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
					// handle back button's click listener
					dismissSelf();
					return true;
				}
				return false;
			}
		});

		// presumably the media fragment has now loaded and passed initial lifecycle calls so we can
		// tell it to load stuff.
		if (mItem.type.equalsIgnoreCase("URL") && mItem.url != null && !mItem.url.isEmpty() & !mItem.url.contentEquals("0"))
			loadItemInWebView();
		else
			this.loadItemInMediaView();
		this.loadItemDescriptionWebView();
	}

	private void loadItemInWebView() {
		ARISWebView wvItemAsURL = (ARISWebView) mItemFragView.findViewById(R.id.wv_item_as_url);
		wvItemAsURL.setVisibility(View.VISIBLE);
		wvItemAsURL.initContextAndInjectJavaScript(mGamePlayActivity);
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
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, "ItemViewFragment. setting webview with Item media; ");
			mediaViewFrag.setMedia(media);
//			[mediaView setMedia:media];
		}
//		else {
//			// show default icon
//		}
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
		if (tab != null) {
			this.showNav();
		} else if (mListener != null) {
			mListener.fragmentItemViewDismiss();
		}
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
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void fragmentItemViewDismiss();

		void onFragmentInteraction(Uri uri);

		void gamePlayTabBarViewControllerRequestsNav();
	}
}
