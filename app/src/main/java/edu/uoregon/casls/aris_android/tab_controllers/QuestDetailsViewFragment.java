package edu.uoregon.casls.aris_android.tab_controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.uoregon.casls.aris_android.ARISWebView;
import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Quest;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;

/**
 * Created by mtolly on 10/25/17.
 */

public class QuestDetailsViewFragment extends Fragment {
    public Quest mQuest;
    public String mMode;
    public        ARISMediaViewFragment mediaViewFrag;
    public static View                  mQuestDetailsView;
    public ARISWebView mAwvPlaqueDesc;

    public transient GamePlayActivity mGamePlayActivity;

    private OnFragmentInteractionListener mListener;

    public QuestDetailsViewFragment() {}

    public void initContext(GamePlayActivity gamePlayAct) {
        mGamePlayActivity = gamePlayAct;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGamePlayActivity = (GamePlayActivity) getActivity();
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mQuestDetailsView = inflater.inflate(R.layout.fragment_quest_details_view, container, false);
//		FragmentTransaction ft = mGamePlayActivity.getSupportFragmentManager().beginTransaction();
        // Init fragment
        mediaViewFrag = new ARISMediaViewFragment();
        mediaViewFrag.initContext(mGamePlayActivity);
        // add a frag inside another (this) frag.
        getChildFragmentManager().beginTransaction().add(R.id.fl_quest_media_view_container, mediaViewFrag).commit();
        getChildFragmentManager().executePendingTransactions();

        return mQuestDetailsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // presumably the media fragment has now loaded and passed initial lifecycle calls so we can
        // tell it to load stuff.
        Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, "QuestDetailsViewFragment.onResume; ");
        this.loadView();
    }

    // todo: call from onCreateView?
    public void loadView() {
        String function = mMode.equals("ACTIVE") ? mQuest.active_function : mQuest.complete_function;
        if (function.equals("NONE")) {
            RelativeLayout continueFooter = (RelativeLayout) mQuestDetailsView.findViewById(R.id.rl_quest_footer);
            continueFooter.setVisibility(View.INVISIBLE);
        }
        else {
            ImageView ivContinueArrow = (ImageView) mQuestDetailsView.findViewById(R.id.iv_quest_footer_right_arrow);
            ivContinueArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    continueButtonTouched(v);
                }
            });
            ivContinueArrow.bringToFront();
            ivContinueArrow.setClickable(true);
        }

        ImageButton ibGoBack = (ImageButton) mQuestDetailsView.findViewById(R.id.ib_quest_go_back);
        ibGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSelf();
            }
        });

        this.loadQuest();
    }

    public void loadQuest() {
        if (!mQuest.name.isEmpty()) {
            TextView tvQuestTitle = (TextView) mQuestDetailsView.findViewById(R.id.tv_note_title);
            tvQuestTitle.setText(mQuest.name);
        }
        String description = mMode.equals("ACTIVE") ? mQuest.active_desc : mQuest.complete_desc;
        if (!description.contentEquals("")) { // load the description webview
            mAwvPlaqueDesc = (ARISWebView) mQuestDetailsView.findViewById(R.id.awv_plaque_desc);
            mAwvPlaqueDesc.initContextAndInjectJavaScript(mGamePlayActivity);
            mAwvPlaqueDesc.loadHTMLString(description);
        }
        long media_id = mMode.equals("ACTIVE") ? mQuest.active_media_id : mQuest.complete_media_id;
        Media media = mGamePlayActivity.mMediaModel.mediaForId(media_id);
        if (media != null) {
            mediaViewFrag.setMedia(media);
//			[mediaView setMedia:media];
        }
    }

    public void continueButtonTouched(View v) {
        String function = mMode.equals("ACTIVE") ? mQuest.active_function : mQuest.complete_function;

        if (function.equals("JAVASCRIPT")) {} // [webView hookWithParams:@""];
        else if (function.equals("NONE")) return;
        else if (function.equals("PICKGAME")) {} // [_MODEL_ leaveGame];
        else mGamePlayActivity.displayTab(mGamePlayActivity.mGame.tabsModel.tabForType(function), false);
    }

    private void dismissSelf() {
        if (mListener != null) {
            mListener.fragmentQuestDismiss();
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
        void fragmentQuestDismiss();

        void onFragmentInteraction(Uri uri);

        void gamePlayTabBarViewControllerRequestsNav();
    }
}
