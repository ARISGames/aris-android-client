package edu.uoregon.casls.aris_android;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class GamesListNearby extends ActionBarActivity {

	private static final String TIME_TAB_DAILY = "Daily";
	private static final String TIME_TAB_WEEKLY = "Weekly";
	private static final String TIME_TAB_MONTHLY = "Monthly";
	private String mUser_name;
	private String mPassword;
	private String mUser_Id;
	private String mDisplay_name;
	private String mMedia_id;
	private String mRead_write_key;
	private FrameLayout mFlTimeTabDaily;
	private FrameLayout mFlTimeTabWeekly;
	private FrameLayout mFlTimeTabMonthly;
	private TextView mTvTimeTabDaily;
	private TextView mTvTimeTabWeekly;
	private TextView mTvTimeTabMonthly;
	private String mTimeTabSelected = TIME_TAB_DAILY; // default starting tab

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games_list_nearby);
		if (mUser_name == null || mPassword == null || mUser_Id == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				mUser_name = extras.getString("user_name");
				mPassword = extras.getString("password");
				mUser_Id = extras.getString("user_id");
				mDisplay_name = extras.getString("display_name");
				mMedia_id = extras.getString("media_id");
				mRead_write_key = extras.getString("read_write_key");
			}
			else
				Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": extras) was NULL");
		}

		// get time tab view elements.
		mFlTimeTabDaily = (FrameLayout) findViewById(R.id.fl_time_tab_daily);
		mFlTimeTabWeekly = (FrameLayout) findViewById(R.id.fl_time_tab_weekly);
		mFlTimeTabMonthly = (FrameLayout) findViewById(R.id.fl_time_tab_monthly);
		mTvTimeTabDaily = (TextView) findViewById(R.id.tv_time_tab_txt_daily);
		mTvTimeTabWeekly = (TextView) findViewById(R.id.tv_time_tab_txt_weekly);
		mTvTimeTabMonthly = (TextView) findViewById(R.id.tv_time_tab_txt_monthly);

	}

	// handle profile button click
	public void profileButtonClick(View v) {

	}

	public void onClickNearbyBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickNearbyBtn");

	}

	public void onClickPopularBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickPopularBtn");

	}

	public void onClickRecentBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickRecentBtn");

	}

	public void onClickSearchBtn(View v) {

	}

	public void onClickMineBtn(View v) {

	}

	public void onClickTabDaily(View v) {
		selectTimeRangeTab(TIME_TAB_DAILY);
	}

	public void onClickTabWeekly(View v) {
		selectTimeRangeTab(TIME_TAB_WEEKLY);

	}

	public void onClickTabMonthly(View v) {
		selectTimeRangeTab(TIME_TAB_MONTHLY);

	}

	private void selectTimeRangeTab(String tabSelected) {
		mTimeTabSelected = tabSelected;
		switch (tabSelected) {
			case (TIME_TAB_DAILY):
				mFlTimeTabDaily.setBackgroundResource(R.drawable.btn_selected_radius_rt_corners);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FFFCFCFC"));
				mFlTimeTabWeekly.setBackgroundResource(0);
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabMonthly.setBackgroundResource(0);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FF242424"));
				break;
			case (TIME_TAB_WEEKLY):
				mFlTimeTabDaily.setBackgroundResource(0);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabWeekly.setBackgroundColor(Color.parseColor("#FF0F3C7C"));
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FFFCFCFC"));
				mFlTimeTabMonthly.setBackgroundResource(0);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FF242424"));
				break;
			case (TIME_TAB_MONTHLY):
				mFlTimeTabDaily.setBackgroundResource(0);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabWeekly.setBackgroundResource(0);
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabMonthly.setBackgroundResource(R.drawable.btn_selected_radius_lft_corners);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FFFCFCFC"));
				break;
			default:
				throw new IllegalArgumentException("Invalid Time Range Selected: " + tabSelected);		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_games_list_nearby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
