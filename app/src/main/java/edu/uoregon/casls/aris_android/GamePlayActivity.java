package edu.uoregon.casls.aris_android;

import android.app.ActivityOptions;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Dispatcher;
import edu.uoregon.casls.aris_android.Utilities.ResponseHandler;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.User;
import edu.uoregon.casls.aris_android.models.GamesModel;
import edu.uoregon.casls.aris_android.models.MediaModel;
import edu.uoregon.casls.aris_android.models.UsersModel;
import edu.uoregon.casls.aris_android.services.AppServices;

public class GamePlayActivity extends AppCompatActivity // <-- was ActionBarActivity
		implements GamePlayNavDrawerFragment.NavigationDrawerCallbacks, GamePlayMapFragment.OnFragmentInteractionListener {

// Todo 9.29.15: Need to see what happens now when the game tries to load, and then set about setting up the cyclic app status calls

	private final static String TAG_SERVER_SUCCESS = "success";
	public  Bundle          mTransitionAnimationBndl;
	public  User            mPlayer; // Sanity note: Now that the game is "playing" we will refer to the logged in User as "Player"
	public  Game            mGame;
	public  Dispatcher      mDispatch;
	public  AppServices     mAppServices;
	public  ResponseHandler mResposeHandler;
	public  MediaModel      mMediaModel;
	public  UsersModel      mUsersModel;
//	public  GamesModel      mGamesModel; // needed to store multiple games on device for future retrieval.
	private View            mProgressView; // todo: install a progress spinner for server delays
	public  JSONObject      mJsonAuth;
	public Map<Long, Media>  mGameMedia = new LinkedHashMap<>();
	public Map<String, User> mGameUsers = new LinkedHashMap<>();

	public Handler performSelector = new Handler(); // used for time deferred method invocation similar to iOS "performSelector"
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private GamePlayNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private long         preferred_game_id;

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
			mPlayer = new User(extras.getString("user")); // we're now a "Player", BTW.
			//GSON (Slow in debug mode. Ok in regular run mode)
			mGame = gson.fromJson(extras.getString("game"), Game.class);
			mGame.initContext(this); // to allow upward visibility to activities various game/player objects


			try {
				mJsonAuth = new JSONObject(extras.getString("json_auth"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

		mDispatch = new Dispatcher(); // Centralized place for object to object messaging
		mAppServices = new AppServices(); // Centralized place for server calls.
		mResposeHandler = new ResponseHandler(); // Where calls to server return for landing.
		mMediaModel = new MediaModel(this);
		mUsersModel = new UsersModel(this);

		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// Having arrived here in this activity is tantamount to the
		//   "LoadingViewController.gameChosen->RootViewController.startLoading" call hierarchy as in iOS
		//   the game has implicitly been "Chosen" so we can "startLoading" straight away
		mGame.getReadyToPlay();
		// Start barrage of game related server requests
	}

	@Override
	public void onStart() {
		super.onStart();
		// todo: restore saved Game object if it was stashed for app sleep.
		// reinit contexts to be safe after a resume.
		mDispatch.initContext(this); // initialize contexts
		mAppServices.initContext(this);
		mGame.initContext(this);
		mResposeHandler.initContext(this);

		if (!mGame.hasLatestDownload() || mGame.network_level.contentEquals("REMOTE")) // loadingViewController.startLoading equivalent in iOS
			this.requestGameData(); // load all game data
		else {
			// todo:   Basically we'll sub in Android life cycle state save and restore (which means this needs to go in the onResume or onStart method
			//[_MODEL_ restoreGameData]; // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restoreGameData
			this.gameDataLoaded(); //
		}
	}

	public void requestGameData() {
		// todo: progress bar
		mGame.requestGameData(); // load all game data
	}

	public void gameDataLoaded() {
		// decide if game needs to be loaded from server or if it is already stored on device from previous load. // todo: perform the device save of game
		if (!mGame.hasLatestDownload() || !mGame.begin_fresh || !mGame.network_level.contentEquals("LOCAL")) //if !local, need to perform maintenance on server so it doesn't keep conflicting with local data
			this.requestMaintenanceData();
		else {
			//skip maintenance step
			//_MODEL_ restorePlayerData]; // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restorePlayerData
			this.playerDataLoaded();
		}

	}

	// todo: implement Android version of these iOS methods:
/*
	public void gameFetchFailed { [self.view addSubview:gameRetryLoadButton]; }
	public void retryGameFetch
	{
		[gameRetryLoadButton removeFromSuperview];
		this.requestGameData];
	}
*/

	public void requestMaintenanceData() {
		// todo: show progress bar.
		mGame.requestMaintenanceData();
	}

	// will be called from Game.maintenancePieceReceived() when Game.allMaintenanceDataLoaded() is satisfied.
	public void maintenanceDataLoaded() {
		if (!mGame.hasLatestDownload() || !mGame.begin_fresh)
			this.requestPlayerData();
		else {
			//_MODEL_ restorePlayerData]; // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restorePlayerData
			this.playerDataLoaded();//fixme: not ever getting here
		}
	}

	// todo: implement Android version of these iOS methods:
/*
	public void maintenancePercentLoaded:(NSNotification *)notif { maintenanceProgressBar.progress = [notif.userInfo[@"percent"] floatValue]; }
	public void maintenanceFetchFailed { [self.view addSubview:maintenanceRetryLoadButton]; }
	public void retryMaintenanceFetch
	{
		[maintenanceRetryLoadButton removeFromSuperview];
		this.requestMaintenanceData];
	}
*/


//Player Data
	public void requestPlayerData() {
//		[self.view addSubview:playerProgressLabel]; [self.view addSubview:playerProgressBar]; // todo progress bar
		mGame.requestPlayerData();
	}

	public void playerDataLoaded() { // gets called only after all game, player and maint data loaded
		if (!mGame.hasLatestDownload()) {
			if (mGame.preload_media)
				this.requestMediaData(); // won't load until maintDataLoaded <-
			else
				this.beginGame(); //[_MODEL_ beginGame];
		}
		else
			this.beginGame();	//[_MODEL_ beginGame];
	}

	// todo: implement Android version of these iOS methods:
/*
	public void playerPercentLoaded:(NSNotification *)notif { playerProgressBar.progress = [notif.userInfo[@"percent"] floatValue]; }
	public void playerFetchFailed { [self.view addSubview:playerRetryLoadButton]; }
	public void retryPlayerFetch
	{
		[playerRetryLoadButton removeFromSuperview];
		this.requestPlayerData];
	}
*/

//Media Data
	public void requestMediaData() {
		//[self.view addSubview:mediaProgressLabel]; [self.view addSubview:mediaProgressBar]; // todo progress bar
		mGame.requestMediaData();
	}

	public void mediaDataLoaded() {
		this.beginGame();
	}

	public void mediaDataComplete() {}

	// todo: implement Android version of these iOS methods:
/*
	public void mediaPercentLoaded:(NSNotification *)notif { mediaProgressBar.progress = [notif.userInfo[@"percent"] floatValue]; }
	public void mediaFetchFailed { [self.view addSubview:mediaRetryLoadButton]; }
	public void retryMediaFetch {
		[mediaRetryLoadButton removeFromSuperview];
		this.requestMediaData];
	}
*/

	// Stubs from iOS RootViewController. May be unnecessary in Android vers. but included while developing App just in case they become useful.
	public void gameBegan() { // stub for potential use later to duplicate RootViewController behaviours as exist in iOS vs.
		// in iOS, initializes View Controller. Not much else.
	}
	public void gameChosen() { // stub for potential use later to duplicate RootViewController behaviours as exist in iOS vs.
		// in iOS, starts the game loading sequence.
	}
	public void gameLeft() {
		// in iOS RootViewController, nulls all values kills current gameplayview and returns view to GamesList.
		// pretty much default behaviour in Android Activity stack "back" action. Not needed here;
	}

	private void beginGame() {
		this.preferred_game_id = 0; //assume the preference was met
		if (mGame.begin_fresh)
			this.storeGame(); //we loaded fresh, so can store player data

		mGame.logsModel.playerEnteredGame(); //		[_MODEL_LOGS_ playerEnteredGame];
		mDispatch.game_began(); // calls mGame.gameBegan() and mGamePlayAct.gameBegan()
	}

	private void storeGame() {

		Gson gson = new Gson();
		String jsonGame = gson.toJson(mGame); // data = mGame.serialize] dataUsingEncoding:NSUTF8StringEncoding];

		File gameStorageFile = AppUtils.gameStorageFile(this);
		AppUtils.writeToFileStream(this, gameStorageFile, jsonGame);

//		File appDir = new File(getFilesDir().getPath());
//		File gameDir = getDir(appDir + String.valueOf(mGame.game_id), Context.MODE_PRIVATE); //Creating an internal dir;
//		File gameFile = new File(gameDir, "game.json"); //Getting a file within the dir.
//		AppUtils.writeToFileStream(this, gameFile, jsonGame);
//
//		NSString *folder = [[self applicationDocumentsDirectory] stringByAppendingPathComponent:[NSString stringWithFormat:@"%ld",_MODEL_GAME_.game_id]];
//		[[NSFileManager defaultManager] createDirectoryAtPath:folder withIntermediateDirectories:YES attributes:nil error:&error];

		// sem: store this game model (set of games)
//		file = [folder stringByAppendingPathComponent:@"game.json"];
//		[data writeToFile:file atomically:YES];
//		[[NSURL fileURLWithPath:file] setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error];

/* save the whole ARISModel (iOS) done by gson in Android along with the Game object. */
//		ARISModel *m;
//		for(long i = 0; i < _MODEL_GAME_.models.count; i++)
//		{
//			m = _MODEL_GAME_.models[i];
//			data = [[m serializeGameData] dataUsingEncoding:NSUTF8StringEncoding];
//			file = [folder stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_game.json",m.serializedName]];
//			[data writeToFile:file atomically:YES];
//			[[NSURL fileURLWithPath:file] setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error];
//		}
//		for(long i = 0; i < _MODEL_GAME_.models.count; i++)
//		{
//			m = _MODEL_GAME_.models[i];
//			data = [[m serializePlayerData] dataUsingEncoding:NSUTF8StringEncoding];
//			file = [folder stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_player.json",m.serializedName]];
//			[data writeToFile:file atomically:YES];
//			[[NSURL fileURLWithPath:file] setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error];
//		}
		mGame.downloadedVersion = mGame.version;
	}

	private void deleteStoredGame() {
		// todo: should there not be some house cleaning so game files don't accumulate on device?
		// todo: question is, when to call this?
		this.deleteFile(AppUtils.gameStorageFile(this).getName());
	}

	@Override
	public void onStop() {
//		mGame.pauseGame(); // not sure if this might be needed for android lifecycle control of game.
		super.onStop();
	}

	@Override
	public void onDestroy() {
		mGame.gameLeft();
		super.onDestroy();
	}

	public void dropItem(long item_id, long qty) {

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
			case 2:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayInventoryFragment.newInstance(position + 1))
						.commit();
				break;
			case 3:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayScannerFragment.newInstance(position + 1))
						.commit();
				break;
			case 4:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayDecoderFragment.newInstance(position + 1))
						.commit();
				break;
			case 5:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayPlayerFragment.newInstance(position + 1))
						.commit();
				break;
			case 6:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayNotebookFragment.newInstance(position + 1))
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
		else if (itemName.equals("Inventory")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayInventoryFragment.newInstance(itemName))
						.commit();
		}
		else if (itemName.equals("Scanner")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayScannerFragment.newInstance(itemName))
						.commit();
		}
		else if (itemName.equals("Decoder")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayDecoderFragment.newInstance(itemName))
						.commit();
		}
		else if (itemName.equals("Player")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayPlayerFragment.newInstance(itemName))
						.commit();
		}
		else if (itemName.equals("Notebook")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayNotebookFragment.newInstance(itemName))
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

	public void fetchNoteById(long object_id) { // why is this here?? todo: get this out of here.
		JSONObject jsonAddlData = new JSONObject();
		try {
			jsonAddlData.put("note_id", mGame.game_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

//		mServices.pollServer(Calls.HTTP_GET_NOTE, jsonAddlData);
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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		Gson gson = new Gson();
		String jsonGame = gson.toJson(mGame);
		savedInstanceState.putString("mGame", jsonGame);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		Gson gson = new Gson();

		mGame = gson.fromJson(savedInstanceState.getString("mGame"), Game.class); // restore game from stored json in savedInstanceState
		mGame.initContext(this); // reset context
		mGame.initModelContexts(); // re-initialize all the embedded objects' references to the Activity context.
	}

}
