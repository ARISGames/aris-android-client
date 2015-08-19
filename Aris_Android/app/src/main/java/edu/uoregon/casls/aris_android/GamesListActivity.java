package edu.uoregon.casls.aris_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;


public class GamesListActivity extends ActionBarActivity {

	private static final String TIME_TAB_DAILY = "Daily";
	private static final String TIME_TAB_WEEKLY = "Weekly";
	private static final String TIME_TAB_MONTHLY = "Monthly";
	private static final String HTTP_GET_NEARBY_GAMES_REQ_API = "v2.client.getNearbyGamesForPlayer/";
	private static final String HTTP_GET_POPULAR_GAMES_REQ_API = "v2.client.getPopularGamesForPlayer/";
	private static final String HTTP_GET_RECENT_GAMES_REQ_API = "v2.client.getRecentGamesForPlayer/";
	private static final String HTTP_GET_SEARCH_GAMES_REQ_API = "v2.client.getSearchGamesForPlayer/";
	private static final String HTTP_GET_PLAYER_GAMES_REQ_API = "v2.client.getPlayerGamesForPlayer/";
	private static final String HTTP_GET_FULL_GAME_REQ_API = "v2.games.getFullGame/";
	private final static String TAG_SERVER_SUCCESS = "success";
	private User user;
	private View mProgressView;
	private LinearLayout mLlSearchBar;
	private LinearLayout mLlTimeTabBar;
	private FrameLayout mFlTimeTabDaily;
	private FrameLayout mFlTimeTabWeekly;
	private FrameLayout mFlTimeTabMonthly;
	private TextView mTvTimeTabDaily;
	private TextView mTvTimeTabWeekly;
	private TextView mTvTimeTabMonthly;
	private EditText mEtSearchTxt;
	private String mTimeTabSelected = TIME_TAB_WEEKLY; // default starting tab

	public List<Game> mListedGames = new ArrayList<Game>();
	public Map<String, Game> mListedGamesMap = new LinkedHashMap<String, Game>();
	public JSONObject mJsonAuth = new JSONObject();
	public int mTotalGamesCount = 0;
	public int mFullGamesUpdated = 0;
	public Bundle mTransitionAnimationBndl;
	private JSONArray mJsonGamesList;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games_list);

		user = new User();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user.user_name = 		extras.getString("user_name");
			user.password = 		extras.getString("password");
			user.user_id = 			extras.getString("user_id");
			user.display_name = 	extras.getString("display_name");
			user.media_id = 		extras.getString("media_id");
			user.read_write_key = 	extras.getString("read_write_key");
			// Location info not used at this point.(?)
//			user.location.setLatitude(0);
//			user.location.setLongitude(0);
		}
		else
			Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": extras) was NULL");

		mProgressView = findViewById(R.id.network_req_progress);

		// get time tab view elements.
		mLlTimeTabBar = (LinearLayout) findViewById(R.id.ll_time_tab_bar);
		mLlSearchBar = (LinearLayout) findViewById(R.id.incl_search_bar);
		mFlTimeTabDaily = (FrameLayout) findViewById(R.id.fl_time_tab_daily);
		mFlTimeTabWeekly = (FrameLayout) findViewById(R.id.fl_time_tab_weekly);
		mFlTimeTabMonthly = (FrameLayout) findViewById(R.id.fl_time_tab_monthly);
		mTvTimeTabDaily = (TextView) findViewById(R.id.tv_time_tab_txt_daily);
		mTvTimeTabWeekly = (TextView) findViewById(R.id.tv_time_tab_txt_weekly);
		mTvTimeTabMonthly = (TextView) findViewById(R.id.tv_time_tab_txt_monthly);

		// make profile button visible
		ImageButton ibProfile = (ImageButton) findViewById(R.id.imgBtn_profile);
		ibProfile.setVisibility(View.VISIBLE);

		Button btnCancelGameSearch = (Button) findViewById(R.id.btn_cancel_game_search);
		btnCancelGameSearch.setVisibility(View.GONE); // hide cancel button for now unti we know what it's for.

		mEtSearchTxt = (EditText) findViewById(R.id.et_search_txt);
//		mEtSearchTxt.setImeActionLabel("Go", EditorInfo.IME_ACTION_DONE); // doesn't seem to work
		final InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
		mEtSearchTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					imm.hideSoftInputFromWindow(mEtSearchTxt.getWindowToken(), 0);
				}
				else
					imm.showSoftInput(mEtSearchTxt, 0);
//				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			}
		});
		mEtSearchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				if (result == EditorInfo.IME_ACTION_SEARCH) {
//					doSearch();
					pollServer(HTTP_GET_SEARCH_GAMES_REQ_API, mEtSearchTxt.getText().toString());
					return true;
				}
				else {
					return false;
				}
			}
		});

		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

		pollServer(HTTP_GET_NEARBY_GAMES_REQ_API, "");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("user", 		user.toJsonStr());
//		savedInstanceState.putString("user_name", 		user.user_name);
//		savedInstanceState.putString("password", 		user.password);
//		savedInstanceState.putString("user_id", 		user.user_id);
//		savedInstanceState.putString("display_name", user.display_name);
//		savedInstanceState.putString("media_id", 		user.media_id);
//		savedInstanceState.putString("read_write_key", 	user.read_write_key);
		savedInstanceState.putString("games_map", mJsonGamesList.toString());
		savedInstanceState.putInt("total_games_count", mTotalGamesCount);
		savedInstanceState.putInt("total_games_updated", mFullGamesUpdated);

	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		user = new User(savedInstanceState.getString("user"));

//		user.user_name = 	savedInstanceState.getString("user_name");
//		user.password = 	savedInstanceState.getString("password");
//		user.user_id = 		savedInstanceState.getString("user_id");
//		user.display_name = savedInstanceState.getString("display_name");
//		user.media_id = 	savedInstanceState.getString("media_id");
//		user.read_write_key = savedInstanceState.getString("read_write_key");
		try {
			JSONObject jsonObject = new JSONObject(savedInstanceState.getString("games_map"));
			mJsonGamesList = jsonObject.getJSONArray("data");
			mListedGamesMap = parseGamesToMap(mJsonGamesList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mTotalGamesCount = 		savedInstanceState.getInt("total_games_count");
		mFullGamesUpdated = 	savedInstanceState.getInt("total_games_updated");

	}

	@Override
	public void onResume() {
		super.onResume();
//		updateAllViews();
	}

	// handle profile button click
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onClickProfileButton(View v) {
		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		Bundle transitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_left, R.animator.slide_out_to_right).toBundle();

		//activity version:
		Intent i = new Intent(GamesListActivity.this, ProfileActivity.class);
		i.putExtra("user", user.toJsonStr());
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i, transitionAnimationBndl);
	}

	public void onClickNearbyBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickNearbyBtn");
		//hide time tab bar
		mLlTimeTabBar.setVisibility(View.GONE);
		mLlSearchBar.setVisibility(View.GONE);
		// get nearby games from server
		clearGamesList();
		pollServer(HTTP_GET_NEARBY_GAMES_REQ_API, "");
	}

	public void onClickPopularBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickPopularBtn");
		mLlTimeTabBar.setVisibility(View.VISIBLE);
		mLlSearchBar.setVisibility(View.GONE);
		// get popular games from server
		clearGamesList();
		pollServer(HTTP_GET_POPULAR_GAMES_REQ_API, "WEEK");
	}

	public void onClickRecentBtn(View v) {
		Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": onClickRecentBtn");
		clearGamesList();
		mLlTimeTabBar.setVisibility(View.GONE);
		mLlSearchBar.setVisibility(View.GONE);
		// get recent games from server
		pollServer(HTTP_GET_RECENT_GAMES_REQ_API, "");
	}

	public void onClickSearchBtn(View v) {
		mLlTimeTabBar.setVisibility(View.GONE);
		mLlSearchBar.setVisibility(View.VISIBLE);
		clearGamesList();
		mEtSearchTxt.setText("");
		mEtSearchTxt.clearFocus();
		mEtSearchTxt.requestFocus();
	}

	public void onClickMineBtn(View v) {
		mLlTimeTabBar.setVisibility(View.GONE);
		mLlSearchBar.setVisibility(View.GONE);
		clearGamesList();
		// get player's games from server
		pollServer(HTTP_GET_PLAYER_GAMES_REQ_API, "");
	}

	public void onClickTabDaily(View v) {
		selectTimeRangeTab(TIME_TAB_DAILY);
		clearGamesList();
		pollServer(HTTP_GET_POPULAR_GAMES_REQ_API, "DAY");
	}

	public void onClickTabWeekly(View v) {
		selectTimeRangeTab(TIME_TAB_WEEKLY);
		clearGamesList();
		pollServer(HTTP_GET_POPULAR_GAMES_REQ_API, "WEEK");
	}

	public void onClickTabMonthly(View v) {
		selectTimeRangeTab(TIME_TAB_MONTHLY);
		clearGamesList();
		pollServer(HTTP_GET_POPULAR_GAMES_REQ_API, "MONTH");
	}

	public void onClickClearSearchTxt(View v) {
		mEtSearchTxt.setText("");
	}

	public void onClickCancelSearch(View v) {
		// todo: just what is the cancel button for, anyway? for now do this: (nothing!)

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Quit Aris?")
				.setMessage("Are you sure you want to quit Aris?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("No", null)
				.show();

	}

	private void selectTimeRangeTab(String tabSelected) {
		mTimeTabSelected = tabSelected;
		// White = #FFFCFCFC Blue = FF0F3C7C Blackish = FF242424
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

	private void pollServer(final String request_api, String auxData) {
		showProgress(true);
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = AppUtils.SERVER_URL_MOBILE + request_api;

		user.location = AppUtils.getGeoLocation(context);

		rqParams.put("request", request_api);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonAuth = new JSONObject();
		try {

			jsonMain.put("user_id", user.user_id);
			jsonMain.put("page", 0); // todo: determine proper value for page. 0 is just a stand-in value.

			switch (request_api) {
				case (HTTP_GET_NEARBY_GAMES_REQ_API):
					jsonMain.put("latitude", user.location.getLatitude());
					jsonMain.put("longitude", user.location.getLongitude());
					jsonAuth.put("user_name", user.user_name);
					jsonAuth.put("password", user.password);
					break;
				case (HTTP_GET_POPULAR_GAMES_REQ_API):
					//sample: {"interval":"WEEK","longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7...X4"}}
					jsonMain.put("longitude", user.location.getLongitude());
					jsonMain.put("interval", auxData);
					jsonMain.put("latitude", user.location.getLatitude());
					jsonAuth.put("user_name", user.user_name);
					jsonAuth.put("password", user.password);
					break;
				case (HTTP_GET_PLAYER_GAMES_REQ_API):
				case (HTTP_GET_RECENT_GAMES_REQ_API): // get player and get recent use the same Req param set.
					//sample: {"longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7...yzX4"}}
					jsonMain.put("latitude", user.location.getLatitude());
					jsonMain.put("longitude", user.location.getLongitude());
					jsonAuth.put("user_name", user.user_name);
					jsonAuth.put("password", user.password);
					break;
				case (HTTP_GET_SEARCH_GAMES_REQ_API):
					//sample: {"auth":{"user_id":1,"key":"F7...zX4"},"longitude":"-89.409260","user_id":"1","latitude":"43.073128","text":"","page":0}
					jsonMain.put("latitude", user.location.getLatitude());
					jsonMain.put("longitude", user.location.getLongitude());
					jsonMain.put("text", auxData);
					break;
				case (HTTP_GET_FULL_GAME_REQ_API):
					jsonMain.put("game_id", Long.parseLong(auxData));
					jsonAuth.put("user_name", user.user_name);
					jsonAuth.put("password", user.password);
					break;
				default:
					break;
			}

			// set up "auth":{...} json child object
			jsonAuth.put("user_id", Long.parseLong(user.user_id));
			jsonAuth.put("key", user.read_write_key);
			mJsonAuth = jsonAuth; // copy to global
			// embed Auth json into main json block
			jsonMain.put("auth", jsonAuth);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(AppUtils.LOGTAG, "Json string Req to server: " + jsonMain);

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
					try {
						processJsonHttpResponse(request_api, TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}

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
		Log.d(AppUtils.LOGTAG, "Return status to server Req: " + jsonReturn.toString());
		if (callingReq.matches(HTTP_GET_NEARBY_GAMES_REQ_API
				+ "|" +  HTTP_GET_POPULAR_GAMES_REQ_API
				+ "|" +  HTTP_GET_RECENT_GAMES_REQ_API
				+ "|" +  HTTP_GET_SEARCH_GAMES_REQ_API
				+ "|" +  HTTP_GET_PLAYER_GAMES_REQ_API) ) { // true = debug temp hall pass for all
			Log.i(AppUtils.LOGTAG, "Landed successfully in colling Req: " + callingReq);
			mFullGamesUpdated = 0; // reset found game count
			try {
				// process incoming json data
				if (jsonReturn.has("data")) {
//					int returnCode = (jsonReturn.has("returnCode")) ? jsonReturn.getInt("returnCode") : null; // what do I do?
//					String returnCodeDescription = (jsonReturn.has("returnCode")) ? jsonReturn.getString("returnCodeDescription") : ""; // For what?
					JSONArray jsonGamesList = jsonReturn.getJSONArray("data");
					mJsonGamesList = jsonGamesList;
					mTotalGamesCount = jsonGamesList.length();
//					JSONObject game = new JSONObject();
					if (jsonGamesList.length() > 0) { // get games - Parse through here or send entire array to special method?
						// send JSON gamesList object to parser method

						mListedGamesMap = parseGamesToMap(jsonGamesList);
						// do we get the full game now and add the extra bits to the Games List? I guess so.
						// After all game basic blocks have loaded, poll for the additional (full) game data.
						getFullGames();
					}
					else { // no data in return set
						mListedGamesMap.clear(); // empty the games list map
						updateAllViews();
						Toast t = Toast.makeText(getApplicationContext(), "No games found.",
								Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER, 0, 0);
						t.show();
					}
				}
			} catch (JSONException e) {
				Log.e(AppUtils.LOGTAG, "Failed while parsing returning JSON from request:" + HTTP_GET_NEARBY_GAMES_REQ_API + " Error reported was: " + e.getCause());
				e.printStackTrace();
			}
		}
		else if (callingReq.contentEquals(HTTP_GET_FULL_GAME_REQ_API)) {
			// Fill in full game data
			fillInFullGameData(jsonReturn);
			mFullGamesUpdated++;
			if (mFullGamesUpdated == mTotalGamesCount) {
				updateAllViews();
			}
		}
		else { // unknown callinRequest
			Log.e(AppUtils.LOGTAG, "AsyncHttpClient returned successfully but with unhandled server callingReq: " + callingReq);
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
	}

	private void clearGamesList() {
		LinearLayout llGamesListLayout = (LinearLayout) findViewById(R.id.ll_games_list);
		llGamesListLayout.removeAllViews(); // refresh visible views so they don't accumulate
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void updateAllViews() {
		// called after any data has been refreshed, usually after network return.
		LinearLayout llGamesListLayout = (LinearLayout) findViewById(R.id.ll_games_list);
		llGamesListLayout.removeAllViews(); // refresh visible views so they don't accumulate

		if (mListedGamesMap == null || mListedGamesMap.size() < 1) {
			//nothing in games list
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 9, 0, 0);

			TextView tvNoEvidenceMessage = new TextView(this);
			tvNoEvidenceMessage.setText("No Games Found.\nYou should make one at arisgames.org.");
			tvNoEvidenceMessage.setTextSize(getResources().getDimension(R.dimen.textsize_small));
			tvNoEvidenceMessage.setGravity(Gravity.CENTER_HORIZONTAL);
			tvNoEvidenceMessage.setPadding(0, 15, 0, 0);
			tvNoEvidenceMessage.setLayoutParams(layoutParams);
			llGamesListLayout.addView(tvNoEvidenceMessage);
		}
		else {
			// populate games list.
			int i = 0;
			for (final String game_id_key: mListedGamesMap.keySet()) {
				final Game gameItem = mListedGamesMap.get(game_id_key);

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View gameItemView = inflater.inflate(R.layout.games_list_item, null);

				gameItemView.setId(Integer.parseInt(game_id_key));
				gameItemView.setTag(gameItem.name);

				//Set game item textviews and rating bar:
				TextView tvGameName = (TextView) gameItemView.findViewById(R.id.tv_game_name);
				TextView tvGameAuthors = (TextView) gameItemView.findViewById(R.id.tv_author_names);
				TextView tvNmbrOfReviews = (TextView) gameItemView.findViewById(R.id.tv_nmbr_of_reviews);
				RatingBar rateBarGameRating = (RatingBar) gameItemView.findViewById(R.id.ratingBar_game_rating);
				tvGameName.setText(gameItem.name);
				List<User> authorsList = new ArrayList<>();
				authorsList = gameItem.authors;
				String authorNames = "";
				// iterate through Authors list and create one string of names
				for (int j = 0; j < authorsList.size(); j++) {
					User author = authorsList.get(j);
					if (j == 0)
						authorNames = author.display_name;
					else
						authorNames += ", " + author.display_name;
				}
				tvGameAuthors.setText(authorNames);
				tvGameName.setText(gameItem.name);
				rateBarGameRating.setRating(Float.parseFloat("0")); // todo: set rating to proper value (from where? games-model, I presume.)

				final Bundle transitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
						R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

				// set onClick listener for this game item listing:
				gameItemView.setOnClickListener(new View.OnClickListener() {
					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					@Override
					public void onClick(View v) {
						// start game cover page  activity
						Intent i = new Intent(GamesListActivity.this, GameCoverPageActivity.class);
						Gson gson = new Gson();
						String jsonGame = gson.toJson(gameItem);
						i.putExtra("user", user.toJsonStr());
						i.putExtra("game", jsonGame);
						i.putExtra("json_auth", mJsonAuth.toString());
						startActivity(i, transitionAnimationBndl);

					}
				});

				llGamesListLayout.addView(gameItemView, i++);
			}
		}

	}

	private void fillInFullGameData(JSONObject jsonFullGameHTTPReturnSet) throws JSONException {
		//get "data" block
		JSONObject jsonFullGameData = jsonFullGameHTTPReturnSet.getJSONObject("data");
		String game_id = jsonFullGameData.getString("game_id"); // get game id from json block
		Game game = mListedGamesMap.get(game_id); // get game instance
		game.initFullGameDetailsWithJson(jsonFullGameHTTPReturnSet);
	}

	private void getFullGames() {
		// iterate through map list and request full game data for each.
		for (Map.Entry game : mListedGamesMap.entrySet()) {
			pollServer(HTTP_GET_FULL_GAME_REQ_API, game.getKey().toString()); // key is game_id
		}
	}

//	public void populateAllViews() {
//		// called after any data has been refreshed, usually after network return.
//	}

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

	// convert json list of games into Array (List) of Game() objects.
//	public List<Game> parseGamesToList (JSONArray gamesList) throws JSONException {
//
//		List<Game> games = new ArrayList<Game>();
//
//		for (int i = 0; i < gamesList.length(); i++) {
//			JSONObject jsonGame = gamesList.getJSONObject(i);
//			games.add(new Game(this, mJsonAuth, jsonGame)); // add to simple list (array)
//
//		}
//		return games;
//	}

	// convert json list of games into Game() objects.
	public Map<String, Game> parseGamesToMap (JSONArray gamesList) throws JSONException {

		Map<String, Game> games = new LinkedHashMap<String, Game>();

		for (int i = 0; i < gamesList.length(); i++) {
			JSONObject jsonGame = gamesList.getJSONObject(i);
			//populate hashmap as <game_id, Game Obj>
			games.put(jsonGame.getString("game_id"), new Game(jsonGame)); // add to hashmap

		}
		return games;
	}
/* iOS:
	- (NSArray *) parseGames:(NSArray *)gamesDicts
	{
		NSMutableArray *games= [[NSMutableArray alloc] init];

		for(long i = 0; i < gamesDicts.count; i++)
		[games addObject:[[Game alloc] initWithDictionary:gamesDicts[i]]];

		return games;
	}
*/

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

Mine Button:
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
2015-07-28 15:28:12.745 ARIS[241:18902] Req async data: {"auth":{"user_id":1,"key":"F7rw...zX4"},"game_id":6}
2015-07-28 15:28:12.786 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.145088)
2015-07-28 15:28:12.787 ARIS[241:18902] Fin async data:
{"data":{"game_id":"1","name":"scott game 1","description":"game1","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"1","type":"LOCATION","intro_scene_id":"1","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:12.795 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:12.796 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:12.916 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.196895)
2015-07-28 15:28:12.918 ARIS[241:18902] Fin async data:
{"data":{"game_id":"5","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wigglne":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"3","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:12.927 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:12.932 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE
2015-07-28 15:28:13.027 ARIS[241:18902] Fin asynch URL: http://10.223.178.105/server/json.php/v2.games.getFullGame/	(0.280197)
2015-07-28 15:28:13.028 ARIS[241:18902] Fin async data:
{"data":{"game_id":"6","name":"test game 2","description":"testing some concepts in aris","tick_script":null,"tick_delay":null,"icon_media_id":"0","media_id":"0","map_type":"STREET","map_latitude":"0","map_longitude":"0","map_zoom_level":"0","map_show_player":"1","map_show_players":"1","map_offsite_mode":"0","notebook_allow_comments":"1","notebook_allow_likes":"1","notebook_trigger_scene_id":"0","notebook_trigger_requirement_root_package_id":"0","notebook_trigger_title":"","notebook_trigger_icon_media_id":"0","notebook_trigger_distance":"0","notebook_trigger_infinite_distance":"0","notebook_trigger_wiggle":"0","notebook_trigger_show_title":"1","notebook_trigger_hidden":"0","notebook_trigger_on_enter":"0","inventory_weight_cap":"0","is_siftr":null,"siftr_url":null,"published":"0","type":"LOCATION","intro_scene_id":"0","moderated":null,"authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}],"media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"},"icon_media":{"media_id":"0","game_id":0,"name":"Default NPC","file_name":"npc.png","url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc.png","thumb_url":"http:\/\/aris.localhost\/server\/gamedata\/v2\/0\/npc_128.png"}},"returnCode":0,"returnCodeDescription":null}
2015-07-28 15:28:13.036 ARIS[241:18902] NSNotification: SERVICES_GAME_RECEIVED
2015-07-28 15:28:13.038 ARIS[241:18902] NSNotification: MODEL_GAME_AVAILABLE


 */