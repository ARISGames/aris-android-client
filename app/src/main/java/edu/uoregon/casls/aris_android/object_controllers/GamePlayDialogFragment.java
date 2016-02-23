package edu.uoregon.casls.aris_android.object_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.models.DialogsModel;
import edu.uoregon.casls.aris_android.models.MediaModel;

/**

 */
public class GamePlayDialogFragment extends Fragment {

	public Dialog dialog;
	public DialogsModel dialogsModel;
	public DialogScript dialogScript;
	public DialogCharacter dialogCharacter;
	public Media dialogCharMedia;

	public Instance instance;
	public Tab tab;
	public GamePlayActivity mGamePlayActivity;

	ScrollView slideUpDialogScriptAndOptionsPanel;
	View       fragView;

//	private OnFragmentInteractionListener mListener;

	public GamePlayDialogFragment() {
		// local convenience reference to Parent activity
		initContext();
	}

	public void initWithInstance(Instance i) {
		instance = i;
		dialog = mGamePlayActivity.mGame.dialogsModel.dialogForId(i.object_id);
		dialogsModel = mGamePlayActivity.mGame.dialogsModel; // convenience ref
//		delegate = d;
		loadViewElements();
	}

	public void initWithTab(Tab tab) {
		instance = mGamePlayActivity.mGame.instancesModel.instanceForId(0);
		instance.object_type = tab.type;
		instance.object_id = tab.content_id;
		dialogsModel = mGamePlayActivity.mGame.dialogsModel; // convenience ref
		dialog = mGamePlayActivity.mGame.dialogsModel.dialogForId(instance.object_id);
//		delegate = d;
		loadViewElements();
	}

	public void initContext() {
		mGamePlayActivity = (GamePlayActivity) getActivity();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGamePlayActivity = (GamePlayActivity)getActivity();
		if (getArguments() != null) {
//			mParam1 = getArguments().getString(ARG_PARAM1);
//			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.fragment_dialog_view, container, false);
		return fragView;
	}


	private void loadViewElements() {
		dialogScript = dialogsModel.scriptForId(dialog.intro_dialog_script_id); // get Script
		dialogCharacter = dialogsModel.characterForId(dialogScript.dialog_character_id); // get character
		// get mediaCD (from database)
		// todo: the media might already be loaded on the device. if it is, use it instead of downloading from remote URL
		MediaModel mm = new MediaModel(mGamePlayActivity);
		dialogCharMedia = mm.mediaForId(dialogCharacter.media_id);

		// set title
		TextView tvDialogTitle = (TextView) fragView.findViewById(R.id.tv_dialog_title);
		tvDialogTitle.setText(dialogCharacter.title);

		// set dialog character image
		WebView wvCharImage = (WebView) fragView.findViewById(R.id.wv_character_image);
		wvCharImage.getSettings().setJavaScriptEnabled(true);
		wvCharImage.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		wvCharImage.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
		wvCharImage.getSettings().setUseWideViewPort(true); // constrain the image horizontally
		// attempt to fit image to whole screen width
		DisplayMetrics displaymetrics = new DisplayMetrics();
		mGamePlayActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenWidth = displaymetrics.widthPixels;
		int screenHeight = displaymetrics.heightPixels;
		// from: http://stackoverflow.com/a/10395972
		String data="<html><head><body><center><img width="+screenWidth+" src=\""+dialogCharMedia.mediaCD.remoteURL.toString()+"\" /></center></body></html>";
		wvCharImage.loadData(data, "text/html", null);

		slideUpDialogScriptAndOptionsPanel = (ScrollView) fragView.findViewById(R.id.scrlv_dialog_text_w_options);
		slideUpDialogScriptAndOptionsPanel.setVisibility(View.INVISIBLE); // hide to start.
		ImageButton backBtn = (ImageButton) fragView.findViewById(R.id.ib_dialog_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackButtonClick(fragView);
			}
		});

		// set Dialog Script Text:
		TextView tvScriptText = (TextView) fragView.findViewById(R.id.tv_dialog_script_text);
		tvScriptText.setText(dialogScript.text);

		// build Dialog Options List
		populateDialogOptionsList(fragView);
		toggleSlidingDialogPanel();

	}

	private void populateDialogOptionsList(View fragView) {
		LinearLayout llDialogOptionsListLayout = (LinearLayout) fragView.findViewById(R.id.ll_dialog_options_list);
		llDialogOptionsListLayout.removeAllViews(); // refresh visible views so they don't accumulate

		String[] tempText = {"This is the first dialog option, but it is a really long one <i>with some tags also</i>. This will help me align the web layout so long stuff <b>fits properly</b>.",
				"This is the <b>second</b> diolog option",
				"<font color=\"red\">This is the third diolog option</font>"};
		Collection<DialogOption> dialogOptions =  mGamePlayActivity.mGame.dialogsModel.dialogOptions.values();
		// loop through set of all dialog options for this dialog; add them to list.

		int listPosition = 0;
		for (DialogOption dialogOption : dialogOptions) {
			if (dialogOption.parent_dialog_script_id == dialogScript.dialog_script_id) {
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View dialogOptionItemView = inflater.inflate(R.layout.dialog_option_list_item, null);

				// set webview to display dialog option prompt
				WebView wvDialogOptionPrompt = (WebView) dialogOptionItemView.findViewById(R.id.wv_dialog_option_prompt);
				wvDialogOptionPrompt.getSettings().setJavaScriptEnabled(true);
				wvDialogOptionPrompt.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
				wvDialogOptionPrompt.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
//		    	wvDialogOptionPrompt.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				String dialogOptionHtml = "<html><body>" + dialogOption.prompt + "</body></html>";
				wvDialogOptionPrompt.loadData(dialogOptionHtml, "text/html", null);

				dialogOptionItemView.setId((int)dialogOption.dialog_option_id);
				dialogOptionItemView.setTag(dialogOption.dialog_option_id);

				// forward button:
				ImageView dialogFwdButton = (ImageView) dialogOptionItemView.findViewById(R.id.iv_game_item_right_arrow);
				final int ii = (int)dialogOption.dialog_option_id;
				dialogFwdButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast t = Toast.makeText(getActivity(), "You selected item #" + ii,
								Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER, 0, 0);
						t.show();
					}
				});
				// add this dialog option item to the list layout
				llDialogOptionsListLayout.addView(dialogOptionItemView, listPosition++);

			}
		}
	}

	public void onBackButtonClick(View v) {
		// todo: finish this
	}

	public void toggleSlidingDialogPanel() {
		if (slideUpDialogScriptAndOptionsPanel.getVisibility() == View.VISIBLE) {
			// hide it
			Animation slideAnim = AnimationUtils.loadAnimation(getContext(),
					R.anim.bottom_down);
			slideUpDialogScriptAndOptionsPanel.startAnimation(slideAnim);
			slideUpDialogScriptAndOptionsPanel.setVisibility(View.INVISIBLE);
		}
		else {
			// show it
			Animation slideAnim = AnimationUtils.loadAnimation(getContext(),
					R.anim.bottom_up);
			slideUpDialogScriptAndOptionsPanel.startAnimation(slideAnim);
			slideUpDialogScriptAndOptionsPanel.setVisibility(View.VISIBLE);
		}
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
//		if (context instanceof OnFragmentInteractionListener) {
//			mListener = (OnFragmentInteractionListener) context;
//		}
//		else {
//			throw new RuntimeException(context.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
	}

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
//	public interface OnFragmentInteractionListener {
//		// TODO: Update argument type and name
//		void onFragmentInteraction(Uri uri);
//	}
}
