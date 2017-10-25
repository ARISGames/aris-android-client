package edu.uoregon.casls.aris_android.tab_controllers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Quest;


public class QuestsViewFragment extends Fragment {
	/**
	 *  The fragment argument representing the section number for this
	* fragment.
	*/

	private static final String ARG_SECTION_NUMBER = "section_number";
	private transient GamePlayActivity mGamePlayAct;
	public View mThisFragsView;
	FrameLayout mFlActiveQuestsTab;
	FrameLayout mFlCompletedQuestsTab;
	TextView mTvActiveQuestsTab;
	TextView mTvCompletedQuestsTab;

	public enum WhichList {
		ACTIVE, COMPLETED
	}

	WhichList mCurrentQuestList = WhichList.ACTIVE; // default

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static QuestsViewFragment newInstance(int sectionNumber) {
		QuestsViewFragment fragment = new QuestsViewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public static QuestsViewFragment newInstance(String sectionName) {
		QuestsViewFragment fragment = new QuestsViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SECTION_NUMBER, sectionName);
		fragment.setArguments(args);
		return fragment;
	}

	// save the fragment's state vars. See: http://stackoverflow.com/a/17135346/1680968
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		if (savedInstanceState != null) {
//			//Restore the fragment's state here
//		}
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//
//	}

	public QuestsViewFragment() {
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mThisFragsView = inflater.inflate(R.layout.fragment_quests_view, container, false);
		if (mGamePlayAct == null)
			mGamePlayAct = (GamePlayActivity) getActivity();

		// onclick listeners
		 mFlActiveQuestsTab = (FrameLayout) mThisFragsView.findViewById(R.id.fl_active_quests_tab);
		mFlActiveQuestsTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentQuestList = WhichList.ACTIVE;
				updateQuestsList();
				setTabHighlighting();
			}
		});
		 mFlCompletedQuestsTab = (FrameLayout) mThisFragsView.findViewById(R.id.fl_completed_quests_tab);
		mFlCompletedQuestsTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentQuestList = WhichList.COMPLETED;
				updateQuestsList();
				setTabHighlighting();
			}
		});
		mTvActiveQuestsTab = (TextView) mThisFragsView.findViewById(R.id.tv_active_quests_tab);
		mTvActiveQuestsTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentQuestList = WhichList.ACTIVE;
				updateQuestsList();
				setTabHighlighting();
			}
		});
		mTvCompletedQuestsTab = (TextView) mThisFragsView.findViewById(R.id.tv_completed_quests_tab);
		mTvCompletedQuestsTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentQuestList = WhichList.COMPLETED;
				updateQuestsList();
				setTabHighlighting();
			}
		});
		updateQuestsList();
		mGamePlayAct.showNavBar();
		return mThisFragsView;
	}

	public void updateQuestsList() {

		LinearLayout llQuestsListLayout = (LinearLayout) mThisFragsView.findViewById(R.id.ll_quests_list);
		llQuestsListLayout.removeAllViews(); // refresh visible views so they don't accumulate

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0, -1, 0, -1);

		switch (mCurrentQuestList) {
			case ACTIVE :
				// get current list of active quests
				List<Quest> activeQuests = mGamePlayAct.mGame.questsModel.visibleActiveQuests();
				// if list is empty set text to read "No Active Quests"
				if (activeQuests == null || activeQuests.size() < 1) {
					TextView tvNoItemsMessage = new TextView(mGamePlayAct);
					tvNoItemsMessage.setText("No Active Quests");
					tvNoItemsMessage.setTextSize(getResources().getDimension(R.dimen.textsize_small));
					tvNoItemsMessage.setGravity(Gravity.CENTER_HORIZONTAL);
					tvNoItemsMessage.setPadding(0, 15, 0, 0);
					tvNoItemsMessage.setLayoutParams(layoutParams);
					llQuestsListLayout.addView(tvNoItemsMessage);
				}
				// populate with active quests.
				else {
					for (final Quest q : activeQuests) {
						LayoutInflater inflater = (LayoutInflater) mGamePlayAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						final View itemView = inflater.inflate(R.layout.quests_list_item, null);
						TextView tvItemName = (TextView) itemView.findViewById(R.id.tv_quest_item_name);
						tvItemName.setText(q.name);
						itemView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mGamePlayAct.displayQuest(q, "ACTIVE");
							}
						});
						itemView.setClickable(true);
						llQuestsListLayout.addView(itemView);
					}
				}
					break;
			case COMPLETED:
				// populate with completed quests
				// get current list of completed quests
				List<Quest> completedQuests = mGamePlayAct.mGame.questsModel.visibleCompleteQuests();
				// if list is empty set text to read "No Active Quests"
				if (completedQuests == null || completedQuests.size() < 1) {
					TextView tvNoItemsMessage = new TextView(mGamePlayAct);
					tvNoItemsMessage.setText("No Completed Quests");
					tvNoItemsMessage.setTextSize(getResources().getDimension(R.dimen.textsize_small));
					tvNoItemsMessage.setGravity(Gravity.CENTER_HORIZONTAL);
					tvNoItemsMessage.setPadding(0, 15, 0, 0);
					tvNoItemsMessage.setLayoutParams(layoutParams);
					llQuestsListLayout.addView(tvNoItemsMessage);
				}
				// populate with completed quests.
				else {
					for (final Quest q : completedQuests) {
						LayoutInflater inflater = (LayoutInflater) mGamePlayAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						final View itemView = inflater.inflate(R.layout.quests_list_item, null);
						TextView tvItemName = (TextView) itemView.findViewById(R.id.tv_quest_item_name);
						tvItemName.setText(q.name);
						itemView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mGamePlayAct.displayQuest(q, "COMPLETE");
							}
						});
						itemView.setClickable(true);
						llQuestsListLayout.addView(itemView);
					}
				}
				break;
			default:
				//error state
				break;
		}
	}

	private void setTabHighlighting() {
		// White = #FFFCFCFC Blue = FF0F3C7C Blackish = FF242424
		switch (mCurrentQuestList) {
			case ACTIVE:
				mTvActiveQuestsTab.setBackgroundColor(Color.parseColor("#FF0F3C7C")); // blue
				mTvCompletedQuestsTab.setBackgroundColor(Color.parseColor("#a3a3a3")); // grey
				break;
			case COMPLETED:
				mTvActiveQuestsTab.setBackgroundColor(Color.parseColor("#a3a3a3")); // grey
				mTvCompletedQuestsTab.setBackgroundColor(Color.parseColor("#FF0F3C7C")); // blue
				break;
			default:
				throw new IllegalArgumentException("Invalid Tab Selected in QuestsViewFragment: " + mCurrentQuestList);		}
	}

	public void onClickTabActiveQuests(View v) {
//		"see udateAllViews() in GamesListActivity for previously working code for list building"
		mCurrentQuestList = WhichList.ACTIVE;
		updateQuestsList();
	}

	public void onClickTabCompletedQuests(View v) {
		mCurrentQuestList = WhichList.COMPLETED;
		updateQuestsList();
	}
}
