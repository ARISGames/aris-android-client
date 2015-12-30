package edu.uoregon.casls.aris_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;

public class GameCoverPageActivity extends AppCompatActivity {
	private static final String HTTP_GET_PLAYER_PLAYED_GAME_REQ_API = "v2.client.getPlayerPlayedGame/";
	private static final String HTTP_LOG_PLAYER_RESET_GAME = "v2.client.logPlayerResetGame/";
	private final static String TAG_SERVER_SUCCESS = "success";
	public Bundle mTransitionAnimationBndl;
	public User mUser;
	protected Game mGame;
	private View mProgressView;
	private LinearLayout mLlFooter;
	private FrameLayout mFlReset, mFlResume, mFlNewGame;
	public JSONObject mJsonAuth;
	private boolean mHasPlayed;
	private ImageView ivGameLogo;
	private TextView tvGameName;
	private WebView wvGameDesc;
//	private TextView tvGameDesc;
	private WebView wvGamePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_cover_page);
		Gson gson = new Gson();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mUser = new User(extras.getString("user"));
			//GSON (Slow in debug mode. Ok in regular run mode)
			mGame = gson.fromJson(extras.getString("game"), Game.class);

			try {
				mJsonAuth = new JSONObject(extras.getString("json_auth"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

//		ImageView ivGameIcon = (ImageView) findViewById(R.id.iv_game_icon);
		ivGameLogo = (ImageView) findViewById(R.id.iv_game_designer_logo);
		tvGameName = (TextView) findViewById(R.id.tv_game_cover_name);
		wvGameDesc = (WebView) findViewById(R.id.tv_game_desc);
//		tvGameDesc = (TextView) findViewById(R.id.tv_game_desc);
		mProgressView = findViewById(R.id.network_req_progress);
		mLlFooter = (LinearLayout) findViewById(R.id.ll_game_cover_pg_footer);
		mFlNewGame = (FrameLayout) findViewById(R.id.fl_newgame_btnbox);
		mFlResume = (FrameLayout) findViewById(R.id.fl_resume_btnbox);
		mFlReset = (FrameLayout) findViewById(R.id.fl_reset_btnbox);


		// stub in graphics todo: replace with custom icon/logo from game settings
//		ivGameIcon.setImageResource(R.drawable.logo_full_tiny);

		pollServer(HTTP_GET_PLAYER_PLAYED_GAME_REQ_API);
	}

	/*
	 Server Request for this game screen:
	URL: http://10.223.178.105/server/json.php/v2.client.getPlayerPlayedGame/
	Req data: {"auth":{"user_id":1,"key":"F7rwZn5LwfH0gf4gQdBSZ6My1gZlWIhrGzOvMJ79PEZVJU2qXt9MpLagS0rFyzX4"},"game_id":"6"}
	 */
	private void pollServer(final String request_api) {
		showProgress(true);
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = AppConfig.SERVER_URL_MOBILE + request_api;

		rqParams.put("request", request_api);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		try {
			jsonMain.put("game_id", String.valueOf(mGame.game_id));
			// embed Auth json into main json block
			jsonMain.put("auth", mJsonAuth);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "Json string Req to server: " + jsonMain);

		try {
			entity = new StringEntity(jsonMain.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// post data should look like this: {"auth":{"user_id":1,"key":"F7...yzX4"},"game_id":"6"}
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
					Log.w(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
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
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "Return status to server Req: " + jsonReturn.toString());
		if (callingReq.contentEquals(HTTP_GET_PLAYER_PLAYED_GAME_REQ_API) ) { //
			// Response looks like this: {"data":{"game_id":"1","has_played":false},"returnCode":0,"returnCodeDescription":null}
			try {
				// process incoming json data
				if (jsonReturn.has("data")) {
					if (jsonReturn.has("returnCode") && jsonReturn.getLong("returnCode") == 0) {
						JSONObject jsonDataBlock = new JSONObject(jsonReturn.getString("data"));
						mHasPlayed = jsonDataBlock.getBoolean("has_played");
						mGame.begin_fresh = !mHasPlayed; // set Game begin_fresh property to inverse of HasPlayed.
						mGame.know_if_begin_fresh = true;
						updateAllViews();
					}

				}
			} catch (JSONException e) {
				Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + HTTP_GET_PLAYER_PLAYED_GAME_REQ_API + " Error reported was: " + e.getCause());
				e.printStackTrace();
			}
		}
		else if (callingReq.equals(HTTP_LOG_PLAYER_RESET_GAME)) {
			if (jsonReturn.has("returnCode") && jsonReturn.getLong("returnCode") == 0) {
				// reset game play buttons.
				mHasPlayed = false;
				updateAllViews();
			}
			else {
				Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "Attempt to reset game from GameCoverPageActivity failed; server returned code: "  + jsonReturn.getLong("returnCode"));
			}
		}
		else { // unknown callinRequest
			Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient returned successfully but with unhandled server callingReq: " + callingReq);
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
	}

	private void updateAllViews() {
		tvGameName.setText(mGame.name);
		wvGameDesc.getSettings().setJavaScriptEnabled(true);
		wvGameDesc.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		wvGameDesc.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
		wvGameDesc.getSettings().setUseWideViewPort(true); // constrain the image horizontally
		wvGameDesc.loadData(mGame.desc, "text/html", null); // was a text view and would not handle html
//		tvGameDesc.setText(mGame.desc);
		wvGamePic = (WebView) findViewById(R.id.wv_game_pic);
		if (mGame.media.media_id == 0) { // 0 = no custom icon
			wvGamePic.setBackgroundColor(0x00000000);
			wvGamePic.setBackgroundResource(R.drawable.logo_icon); // set to static aris icon
			ViewGroup.LayoutParams layoutParams = wvGamePic.getLayoutParams(); // force the webview to have a size since it cannot adjust w/o actual content.
			layoutParams.height = 400;
			layoutParams.width = 400;
			wvGamePic.setLayoutParams(layoutParams);
			ivGameLogo.setImageResource(R.drawable.logo_text_nav);
		}
		else {
			wvGamePic.getSettings().setJavaScriptEnabled(true);
			wvGamePic.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
//			wvGamePic.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
			wvGamePic.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
			wvGamePic.getSettings().setUseWideViewPort(true); // constrain the image horizontally
//			wvGamePic
			wvGamePic.loadUrl(mGame.media.remoteURL.toString());
		}

		if (mHasPlayed) {
			mFlReset.setVisibility(View.VISIBLE);
			mFlResume.setVisibility(View.VISIBLE);
			mFlNewGame.setVisibility(View.GONE);
		}
		else {
			mFlReset.setVisibility(View.GONE);
			mFlResume.setVisibility(View.GONE);
			mFlNewGame.setVisibility(View.VISIBLE);
		}
		mLlFooter.setVisibility(View.VISIBLE);
	}

	public void onClickResetGame (View v) {
/* iOS reference code for your convenience
		[_MODEL_GAMES_ playerResetGame:game.game_id];
		game.begin_fresh = YES;
		[self refreshFromGame];
*/
		pollServer(HTTP_LOG_PLAYER_RESET_GAME); // covers the actions of [_MODEL_GAMES_ playerResetGame:game.game_id];
		mGame.begin_fresh = true;
		//[self refreshFromGame]; in Android this happens after server return call in rcv'd via updateAllViews()

	}

	public void onClickResumeGame (View v) {
		// start game play activity.
		startGamePlay();
	}
	public void onClickNewGame (View v) {
		// start game play activity.
		startGamePlay();
	}

	private void startGamePlay() {
		Intent i = new Intent(GameCoverPageActivity.this, GamePlayActivity.class);
		i.putExtra("json_auth", mJsonAuth.toString());
		i.putExtra("user", 		mUser.toJsonStr());
//		i.putExtra("game_id", 	mGame.game_id); //todo: do I need the entire game object or just the game id. ei. does it just reload everything in gameplay?
		Gson gson = new Gson();
		i.putExtra("game", gson.toJson(mGame));
 		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i, mTransitionAnimationBndl);
		finish();

	}

	public void onBackButtonClick(View v) {
		// kill activity - return to login
		super.onBackPressed();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_game_cover_page, menu);
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

	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_right, R.animator.slide_in_from_left);
	}
}

