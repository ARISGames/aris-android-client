package edu.uoregon.casls.aris_android;

import android.app.ActivityOptions;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Calls;
import edu.uoregon.casls.aris_android.Utilities.Config;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;

public class GamePlayActivity extends ActionBarActivity
		implements GamePlayNavDrawerFragment.NavigationDrawerCallbacks, GamePlayMapFragment.OnFragmentInteractionListener {


	private final static String TAG_SERVER_SUCCESS = "success";
	public Bundle mTransitionAnimationBndl;
	public User mUser;
	protected Game mGame;
	private View mProgressView;
	public JSONObject mJsonAuth;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private GamePlayNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		mNavigationDrawerFragment = (GamePlayNavDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

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

		// Start barrage of game related server requests
		getGameDataFromServer();
		// Set up the drawer. todo: move this to processServerResponse() for call getTabsForPlayer
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	private void getGameDataFromServer() {
		// here are all the calls made from iOS on starting or resuming a game:
		pollServer(Calls.HTTP_GET_SCENES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_TOUCH_SCENE_4_PLAYER, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_PLAQUES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_ITEMS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_PLAYER, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_DIALOGS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_DIALOG_CHARS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_WEB_PAGES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_NOTES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_NOTE_COMMNTS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_TAGS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_OBJ_TAGS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_EVENTS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_QUESTS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_TRIGGERS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_FACTORIES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_OVERLAYS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_INSTANCES_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_TABS_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_MEDIA_4_GAME, ""); // user auth json reqired
		pollServer(Calls.HTTP_GET_USERS_4_GAME, ""); // user auth json reqired
	}

	private void pollServer(String requestApi, String auxData) {
//		showProgress(true);
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = Config.SERVER_URL_MOBILE + requestApi;

		mUser.location = AppUtils.getGeoLocation(context);

		rqParams.put("request", requestApi);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		JSONObject jsonAuth = new JSONObject();

		try {
			// place the auth block.
			jsonMain.put("auth", mJsonAuth);
			//place additional required params:
			switch (requestApi) {
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

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {
			case 0:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayQuestsFragment.newInstance(position + 1))
						.commit();
				break;
			case 1:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayMapFragment.newInstance(position + 1))
						.commit();
				break;
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(String itemName) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		if (itemName.equals("Quests")) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, GamePlayQuestsFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Map")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayMapFragment.newInstance(itemName))
						.commit();
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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

	@Override
	public void onFragmentInteraction(Uri uri) {
		Uri u = uri;
	}

	@Override
	public void onSecondFragButtonClick(String message) {
		String gotit = message;
	}

//	@Override
//	public void onFragmentInteraction(Uri uri) {
//		Uri u = uri;
//	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_game_play);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_game_play, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//
//		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public void onNavigationDrawerItemSelected(int position) {
//
//	}
}
