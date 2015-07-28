package edu.uoregon.casls.aris_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class GamesList extends ActionBarActivity {

	private static final String TIME_TAB_DAILY = "Daily";
	private static final String TIME_TAB_WEEKLY = "Weekly";
	private static final String TIME_TAB_MONTHLY = "Monthly";
	private static final String HTTP_GET_NEARBY_GAMES_REQ_API = "v2.client.getNearbyGamesForPlayer";
	private static final String HTTP_GET_POPULAR_GAMES_REQ_API = "v2.client.getPopularGamesForPlayer";
	private static final String HTTP_GET_RECENT_GAMES_REQ_API = "v2.client.getRecentGamesForPlayer";
	private static final String HTTP_GET_SEARCH_GAMES_REQ_API = "v2.client.getSearchGamesForPlayer";
	private static final String HTTP_GET_PLAYER_GAMES_REQ_API = "v2.client.getPlayerGamesForPlayer";
	private final static String TAG_SERVER_SUCCESS = "success";
	private String mUser_name;
	private String mPassword;
	private String mUser_Id;
	private String mDisplay_name;
	private String mMedia_id;
	private String mRead_write_key;
	private View mProgressView;
	private LinearLayout mLlTimeTabBar;
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
		setContentView(R.layout.activity_games_list);
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
		mProgressView = findViewById(R.id.network_req_progress);

		// get time tab view elements.
		mLlTimeTabBar = (LinearLayout) findViewById(R.id.ll_time_tab_bar);
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
		//hide time tab bar
		mLlTimeTabBar.setVisibility(View.GONE);
		// get nearby games from server
	}

	public void onClickPopularBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickPopularBtn");
		mLlTimeTabBar.setVisibility(View.VISIBLE);

	}

	public void onClickRecentBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickRecentBtn");
		mLlTimeTabBar.setVisibility(View.GONE);

	}

	public void onClickSearchBtn(View v) {
		mLlTimeTabBar.setVisibility(View.GONE);

	}

	public void onClickMineBtn(View v) {
		mLlTimeTabBar.setVisibility(View.GONE);

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
		// White = #FFFCFCFC Blue = FF0F3C7C blackish = FF242424
		switch (tabSelected) {
			case (TIME_TAB_DAILY):
				mFlTimeTabDaily.setBackgroundResource(R.drawable.btn_selected_radius_lft_corners);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FFFCFCFC"));
				mFlTimeTabWeekly.setBackgroundResource(0);
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FF242424"));
				mTvTimeTabWeekly.setBackgroundColor(Color.parseColor("#FFFCFCFC"));
				mFlTimeTabMonthly.setBackgroundResource(0);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabMonthly.setPadding(1, 2, 0, 0);
				break;
			case (TIME_TAB_WEEKLY):
				mFlTimeTabDaily.setBackgroundResource(0);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabWeekly.setBackgroundColor(Color.TRANSPARENT);
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FFFCFCFC"));
				mTvTimeTabWeekly.setBackgroundColor(Color.parseColor("#FF0F3C7C"));
				mFlTimeTabMonthly.setBackgroundResource(0);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FF242424"));
				break;
			case (TIME_TAB_MONTHLY):
				mFlTimeTabDaily.setBackgroundResource(0);
				mTvTimeTabDaily.setTextColor(Color.parseColor("#FF242424"));
				mFlTimeTabDaily.setPadding(0, 3, 1, 0);
				mFlTimeTabWeekly.setBackgroundResource(0);
				mTvTimeTabWeekly.setTextColor(Color.parseColor("#FF242424"));
				mTvTimeTabWeekly.setBackgroundColor(Color.parseColor("#FFFCFCFC"));
				mFlTimeTabMonthly.setBackgroundResource(R.drawable.btn_selected_radius_rt_corners);
				mTvTimeTabMonthly.setTextColor(Color.parseColor("#FFFCFCFC"));
				break;
			default:
				throw new IllegalArgumentException("Invalid Time Range Selected: " + tabSelected);		}
	}

	private void pollServer(final String request_api) {
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = AppUtils.SERVER_URL_MOBILE + request_api;

		rqParams.put("request", request_api);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		try {

			jsonMain.put("user_id", mUser_Id);
			jsonMain.put("page", 0); // todo: determine proper value for page. 0 is just a stand-in value.

			switch (request_api) {
				case (HTTP_GET_NEARBY_GAMES_REQ_API):
					jsonMain.put("longitude", mUser_Id); // todo: get current lon and lat.
					jsonMain.put("latitude", mUser_Id);
					break;
				case (HTTP_GET_POPULAR_GAMES_REQ_API):
					break;
				case (HTTP_GET_RECENT_GAMES_REQ_API):
					break;
				case (HTTP_GET_SEARCH_GAMES_REQ_API):
					break;
				case (HTTP_GET_PLAYER_GAMES_REQ_API):
					break;
			}

			// set up "auth":{...} json child object
			JSONObject jsonAuth = jsonMain.getJSONObject("auth");
			jsonAuth.put("user_name", mUser_name);
			jsonAuth.put("password", mPassword);
			jsonAuth.put("user_id", mUser_Id);
			jsonAuth.put("key", mRead_write_key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(AppUtils.LOGTAG, "Json string Req to server: " + jsonMain);

		try {
			entity = new StringEntity(jsonMain.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/*
		client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
		 */
		// post data should look like this: {"password":"123123","permission":"read_write","user_name":"scott"}
		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();

			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
					showProgress(false);
					processJsonHttpResponse(request_api, TAG_SERVER_SUCCESS, jsonReturn);

				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppUtils.LOGTAG, "AsyncHttpClient failed server call. ", throwable);
					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(), "You are not connected to the internet currently. Please try again later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}
	}

	private void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) throws JSONException {
		Log.i(AppUtils.LOGTAG, "Return status to server Req: " + jsonReturn.toString());
		if (callingReq == HTTP_GET_NEARBY_GAMES_REQ_API) {
			Log.i(AppUtils.LOGTAG, "Landed successfully in colling Req: " + HTTP_GET_NEARBY_GAMES_REQ_API);
			try {
				// process incoming json data
				if (jsonReturn.has("data")) {
//					int returnCode = (jsonReturn.has("returnCode")) ? jsonReturn.getInt("returnCode") : null; // what do I do?
//					String returnCodeDescription = (jsonReturn.has("returnCode")) ? jsonReturn.getString("returnCodeDescription") : ""; // For what?
					JSONArray jsonGamesList = jsonReturn.getJSONArray("data");
					JSONObject game = new JSONObject();
					if (jsonGamesList.length() > 0) { // get games
						for (jsonGamesList :  game) {
							//put into an array for the games list view.
							game.getString("game_id");
							game.getString("name");
							game.getString("description");
							game.getString("tick_script");
							game.getString("tick_delay");
							game.getString("icon_media_id");
							game.getString("media_id");
							game.getString("map_type");
							game.getString("map_latitude");
							game.getString("map_longitude");
							game.getString("map_zoom_level");
							game.getString("map_show_player");
							game.getString("map_show_players");
							game.getString("map_offsite_mode");
							game.getString("notebook_allow_comments");
							game.getString("notebook_allow_likes");
							game.getString("notebook_trigger_scene_id");
							game.getString("notebook_trigger_requirement_root_package_id");
							game.getString("notebook_trigger_title");
							game.getString("notebook_trigger_icon_media_id");
							game.getString("notebook_trigger_distance");
							game.getString("notebook_trigger_infinite_distance");
							game.getString("notebook_trigger_wiggle");
							game.getString("notebook_trigger_show_title");
							game.getString("notebook_trigger_hidden");
							game.getString("notebook_trigger_on_enter");
							game.getString("inventory_weight_cap");
							game.getString("is_siftr");
							game.getString("siftr_url");
							game.getString("published");
							game.getString("type");
							game.getString("intro_scene_id");
							game.getString("moderated");

						}

					}
					else { // no data in return set
						Toast t = Toast.makeText(getApplicationContext(), "No games found.",
								Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER, 0, 0);
						t.show();
					}
				}
			} catch (JSONException e) {
				Log.e(AppUtils.LOGTAG, "Failed while parsing returning JSON from request:" + HTTP_CLIENT_LOGIN_REQ_API + " Error reported was: " + e.getCause());
				e.printStackTrace();
			}
		}
		else { // unknown callinRequest
			Log.e(AppUtils.LOGTAG, "AsyncHttpClient returned unknown server callingReq: " + callingReq);
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
	}


	public void populateAllViews() {
		// called after any data has been refreshed, usually after network return.
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		}
		else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}


}



/*
HTTP calls as generated on iOS with corresponding footer button click:

Entering this page:

(Nearby call returning nothing found):
Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getNearbyGamesForPlayer/	(1.356220)
2015-07-28 15:19:47.270 ARIS[241:18902] Fin async data:
{"data":[],"returnCode":0,"returnCodeDescription":null}

Nearby button:
Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getNearbyGamesForPlayer/
2015-07-28 15:19:45.911 ARIS[241:18902] Req async data: {"longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"}}
2015-07-28 15:19:46.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:19:46.939 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.users.updateUser/	(1.340365)
2015-07-28 15:19:46.940 ARIS[241:18902] Fin async data:
{"faultCode":"AMFPHP_RUNTIME_ERROR","faultDetail":"\/Users\/smorison\/git\/server\/services\/v2\/media.php on line 59","faultString":"fopen(Users\/smorison\/git\/server\/gamedata\/v2\/players\/aris1dd6b5f9601e766c57b9de300e663bcc.jpg): failed to open stream: No such file or directory"}

Popular button:
2015-07-28 15:23:21.399 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getPopularGamesForPlayer/
2015-07-28 15:23:21.400 ARIS[241:18902] Req async data: {"interval":"WEEK","longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"}}
2015-07-28 15:23:22.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:23:23.135 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getPopularGamesForPlayer/	(1.732973)
2015-07-28 15:23:23.136 ARIS[241:18902] Fin async data:
{"data":[],"returnCode":0,"returnCodeDescription":null}

Recent Button:
2015-07-28 15:24:05.296 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getRecentGamesForPlayer/
2015-07-28 15:24:05.297 ARIS[241:18902] Req async data: {"longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"}}
2015-07-28 15:24:05.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:24:05.783 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getRecentGamesForPlayer/	(0.484475)
2015-07-28 15:24:05.783 ARIS[241:18902] Fin async data:
{"data":[{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null}],"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:24:05.788 ARIS[241:18902] NSNotification: SERVICES_RECENT_GAMES_RECEIVED
2015-07-28 15:24:05.790 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:24:05.790 ARIS[241:18902] NSNotification: MODEL_RECENT_GAMES_AVAILABLE
2015-07-28 15:24:05.875 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:24:05.876 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:24:06.036 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.158770)
2015-07-28 15:24:06.037 ARIS[241:18902] Fin async data:
{"data":{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:24:06.044 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:24:06.045 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE

Search Button: initial click
2015-07-28 15:25:15.938 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/
2015-07-28 15:25:15.939 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"longitude":"-89.409260","user_id":"1","latitude":"43.073128","text":"","page":0}
2015-07-28 15:25:16.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:25:16.699 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/	(0.758612)
2015-07-28 15:25:16.700 ARIS[241:18902] Fin async data:
{"data":[],"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:25:16.701 ARIS[241:18902] NSNotification: SERVICES_SEARCH_GAMES_RECEIVED
2015-07-28 15:25:16.702 ARIS[241:18902] NSNotification: MODEL_SEARCH_GAMES_AVAILABLE

Search (with search term "Kids"):
2015-07-28 15:26:09.309 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/
2015-07-28 15:26:09.310 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"longitude":"-89.409260","user_id":"1","latitude":"43.073128","text":"Kids","page":0}
2015-07-28 15:26:09.346 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:26:09.848 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/	(0.537020)
2015-07-28 15:26:09.849 ARIS[241:18902] Fin async data:
{"data":[],"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:26:09.850 ARIS[241:18902] NSNotification: SERVICES_SEARCH_GAMES_RECEIVED
2015-07-28 15:26:09.851 ARIS[241:18902] NSNotification: MODEL_SEARCH_GAMES_AVAILABLE

Search (with search term "Scott"):
2015-07-28 15:27:19.685 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/
2015-07-28 15:27:19.686 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"longitude":"-89.409260","user_id":"1","latitude":"43.073128","text":"Scott","page":0}
2015-07-28 15:27:20.296 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getSearchGamesForPlayer/	(0.608814)
2015-07-28 15:27:20.297 ARIS[241:18902] Fin async data:
{"data":[{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null}],"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:27:20.301 ARIS[241:18902] NSNotification: SERVICES_SEARCH_GAMES_RECEIVED
2015-07-28 15:27:20.302 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:27:20.305 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:27:20.305 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:27:20.317 ARIS[241:18902] NSNotification: MODEL_SEARCH_GAMES_AVAILABLE
2015-07-28 15:27:20.336 ARIS[241:18902] Dup req abort : http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:27:20.337 ARIS[241:18902] Dup req data  : {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:27:20.370 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:27:20.440 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.133297)
2015-07-28 15:27:20.441 ARIS[241:18902] Fin async data:
{"data":{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:27:20.448 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:27:20.449 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE

More Button:
2015-07-28 15:28:11.296 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.client.getPlayerGamesForPlayer/
2015-07-28 15:28:11.297 ARIS[241:18902] Req async data: {"longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"}}
2015-07-28 15:28:11.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:28:12.322 ARIS[241:18902] NSNotification: CONNECTION_LAG
2015-07-28 15:28:12.623 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.client.getPlayerGamesForPlayer/	(1.324679)
2015-07-28 15:28:12.624 ARIS[241:18902] Fin async data:
{"data":[{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null},{"game_id":"5","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"3","moderated":null},{"game_id":"6","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"0","moderated":null}],"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:12.635 ARIS[241:18902] NSNotification: SERVICES_MINE_GAMES_RECEIVED
2015-07-28 15:28:12.637 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:12.639 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:28:12.640 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:28:12.653 ARIS[241:18902] Dup req abort : http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:28:12.654 ARIS[241:18902] Dup req data  : {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:28:12.665 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:12.666 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:12.668 ARIS[241:18902] NSNotification: MODEL_MINE_GAMES_AVAILABLE
2015-07-28 15:28:12.688 ARIS[241:18902] Dup req abort : http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:28:12.689 ARIS[241:18902] Dup req data  : {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":1}
2015-07-28 15:28:12.717 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:28:12.717 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":5}
2015-07-28 15:28:12.744 ARIS[241:18902] Req asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/
2015-07-28 15:28:12.745 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":6}
2015-07-28 15:28:12.786 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.145088)
2015-07-28 15:28:12.787 ARIS[241:18902] Fin async data:
{"data":{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:12.795 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:12.796 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:12.916 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.196895)
2015-07-28 15:28:12.918 ARIS[241:18902] Fin async data:
{"data":{"game_id":"5","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"3","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:12.927 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:12.932 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:13.027 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.280197)
2015-07-28 15:28:13.028 ARIS[241:18902] Fin async data:
{"data":{"game_id":"6","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"0","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:13.036 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:13.038 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE


 */