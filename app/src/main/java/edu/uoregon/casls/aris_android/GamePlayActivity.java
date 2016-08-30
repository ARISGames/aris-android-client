package edu.uoregon.casls.aris_android;

import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Dispatcher;
import edu.uoregon.casls.aris_android.Utilities.ResponseHandler;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.Factory;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.InstantiableProtocol;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.Plaque;
import edu.uoregon.casls.aris_android.data_objects.Scene;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.data_objects.User;
import edu.uoregon.casls.aris_android.data_objects.WebPage;
import edu.uoregon.casls.aris_android.media.ARISMediaViewFragment;
import edu.uoregon.casls.aris_android.models.MediaModel;
import edu.uoregon.casls.aris_android.models.UsersModel;
import edu.uoregon.casls.aris_android.object_controllers.DialogViewFragment;
import edu.uoregon.casls.aris_android.object_controllers.ItemViewFragment;
import edu.uoregon.casls.aris_android.object_controllers.NoteViewFragment;
import edu.uoregon.casls.aris_android.object_controllers.PlaqueViewFragment;
import edu.uoregon.casls.aris_android.object_controllers.WebPageViewFragment;
import edu.uoregon.casls.aris_android.services.AppServices;
import edu.uoregon.casls.aris_android.tab_controllers.AttributesViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.DecoderViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.InventoryViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.MapViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.QuestsViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.ScannerViewFragment;

public class GamePlayActivity extends AppCompatActivity // <-- was ActionBarActivity
		implements
			GamePlayNavDrawerFragment.NavigationDrawerCallbacks,
			MapViewFragment.OnFragmentInteractionListener,
			PlaqueViewFragment.OnFragmentInteractionListener,
			DialogViewFragment.OnFragmentInteractionListener,
			ItemViewFragment.OnFragmentInteractionListener,
			WebPageViewFragment.OnFragmentInteractionListener,
			ARISMediaViewFragment.OnFragmentInteractionListener {


	private final static String TAG_SERVER_SUCCESS      = "success";
	private static final String FRAGMENT_VISIBILITY_MAP = "FRAGMENT_VISIBILITY_MAP";
	public static SharedPreferences appPrefs;

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
	public GamePlayTabSelectorViewController mGamePlayTabSelectorViewController;

	// fragment views for game. Acting in place of DisplayViewController classes in iOS.
	// (may want to centralize these in a Navigation Controller)
	public GamePlayPlayerFragment playerViewFragment;
	// tab_controllers
	public AttributesViewFragment attributesViewController;
	public DecoderViewFragment    decoderViewFragment;
	public InventoryViewFragment  inventoryViewFragment;
	public MapViewFragment        mapViewFragment;
	public QuestsViewFragment     questsViewFragment;
	public ScannerViewFragment    scannerViewFragment;
	// object_controllers
	public DialogViewFragment     dialogViewFragment;
	public ItemViewFragment       itemViewFragment;
	public NoteViewFragment       noteViewFragment;
	public PlaqueViewFragment     plaqueViewFragment;
	public WebPageViewFragment    webPageViewFragment;

	public HashMap<String, Boolean> fragVisible = new HashMap<>();
	public String mCurrentFragVisible;
	public String currentFragVisibl2e;

	public boolean    viewingInstantiableObject = false;
	public List<Long> local_inst_queue          = new ArrayList<>();

	public Handler performSelector = new Handler(); // used for time deferred method invocation similar to iOS "performSelector"
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private GamePlayNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle = "ARIS"; // defualt.
	private long      preferred_game_id;
	public  ActionBar mActionBar;
	public boolean leave_game_enabled  = true; // todo: this should get set somewhere in the login return data mashup. For now hardwire ON.
	public boolean triggerQueueWaiting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		mNavigationDrawerFragment = (GamePlayNavDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		appPrefs = getSharedPreferences(AppConfig.APP_PREFS_FILE_NAME, MODE_PRIVATE);

		Gson gson = new Gson();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mPlayer = new User(extras.getString("user")); // we're now a "Player", BTW.
			//GSON (Super slow in debug mode. Ok in regular run mode)
			mGame = gson.fromJson(extras.getString("game"), Game.class);
			mGame.initContext(this); // to allow upward visibility to activities various game/player objects
			mGame.initWithDictionary(); // misleading name in Android. Checks for and loads version number of saved game file.

			try {
				mJsonAuth = new JSONObject(extras.getString("json_auth"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// check for savedInstanceState here instead of overiding onRestoreInstance because it won't
		// get called until after onStart (too late)
		if (savedInstanceState != null) { // there was a saved instance. we must be reawakening from being stopped by OS
			restoreFromSavedInstance(savedInstanceState);
		}

		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

		mDispatch = new Dispatcher(); // Centralized place for object to object messaging
		mAppServices = new AppServices(); // Centralized place for server calls.
		mResposeHandler = new ResponseHandler(); // Where calls to server return for landing.
		mMediaModel = new MediaModel(this);
		mUsersModel = new UsersModel(this);
		mGamePlayTabSelectorViewController = new GamePlayTabSelectorViewController();

		viewingInstantiableObject = false;

		// Having arrived here in this activity is tantamount to the
		//   "LoadingViewController.gameChosen->RootViewController.startLoading" call hierarchy as in iOS
		//   the game has implicitly been "Chosen" so we can "startLoading" straight away
		mGame.getReadyToPlay();
		// Start barrage of game related server requests
	}

	private void restoreFromSavedInstance(Bundle savedInstanceState) {
		if (fragVisible == null || fragVisible.isEmpty())
			fragVisible = (HashMap<String, Boolean>) savedInstanceState.getSerializable(FRAGMENT_VISIBILITY_MAP);
		// thaw out frozen fragments.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (Map.Entry<String, Boolean> fragEntry : fragVisible.entrySet()) {
			String fragTag = fragEntry.getKey();
			Log.i("TAG", "Looking through fragVisible Hash, fragTag: " + fragTag);
			Log.i("TAG", "Its visibility is " + fragEntry.getValue().toString());
			// rebirth fragment
			android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragTag);
			// sanity check. Find a way to fail gracefully or just restart activity perhaps.
			if (fragment == null)
				Log.i("TAG", "Trying to restore this fragment failed fragment is null");
			else
				Log.i("TAG", "Fragment was recreated using tag.");
			//@formatter:off
			// reconstitute as specific fragment class objects.
			if      (fragment instanceof DecoderViewFragment) {
				decoderViewFragment = (DecoderViewFragment) fragment;
			}
			else if (fragment instanceof DialogViewFragment) {
				dialogViewFragment = (DialogViewFragment) fragment;
			}
			else if (fragment instanceof InventoryViewFragment) {
				inventoryViewFragment = (InventoryViewFragment) fragment;
			}
			else if (fragment instanceof MapViewFragment) {
				mapViewFragment = (MapViewFragment) fragment;
			}
			else if (fragment instanceof NoteViewFragment) {
				noteViewFragment = (NoteViewFragment) fragment;
			}
			else if (fragment instanceof GamePlayPlayerFragment) {
				playerViewFragment = (GamePlayPlayerFragment) fragment;
			}
			else if (fragment instanceof QuestsViewFragment) {
				questsViewFragment = (QuestsViewFragment) fragment;
			}
			else if (fragment instanceof ScannerViewFragment) {
				scannerViewFragment = (ScannerViewFragment) fragment;
			}
			else if (fragment instanceof WebPageViewFragment) {
				webPageViewFragment = (WebPageViewFragment) fragment;
			}
			//@formatter:on
			// hide them all to start
			ft.hide(fragment);
			// find the visible fragment from previous life cycle
			if (fragEntry.getValue()) mCurrentFragVisible = fragTag;
		}
		ft.commit();
		getSupportFragmentManager().executePendingTransactions(); // flush its queue before attempting to show fragments
		showInstantiableFragment(mCurrentFragVisible, null);

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
		mNavigationDrawerFragment.initContext(this);

		if (!mGame.hasLatestDownload() || mGame.network_level.contentEquals("REMOTE")) // loadingViewController.startLoading equivalent in iOS
			this.requestGameData(); // load all game data
		else {
			// todo:   Basically we'll sub in Android life cycle state save and restore (which means this needs to go in the onResume or onStart method
			//[_MODEL_ restoreGameData); // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restoreGameData
			this.gameDataLoaded(); //
		}

	}

	public void requestGameData() {
		// todo: progress bar
		mGame.requestGameData(); // load all game data
	}

	public void gameDataLoaded() {
		// decide if game needs to be loaded from server or if it is already stored on device from previous load. // todo: perform the device save of game
		if (!mGame.hasLatestDownload() || !mGame.begin_fresh() || !mGame.network_level.contentEquals("LOCAL")) //if !local, need to perform maintenance on server so it doesn't keep conflicting with local data
			this.requestMaintenanceData();
		else {
			//skip maintenance step
			//_MODEL_ restorePlayerData); // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restorePlayerData
			this.playerDataLoaded();
		}

		// new logic in iOS as of 8/16 update: (mostly the same as above with added conditions and the if/else logic inverted.)
/*
		if(
				(_MODEL_GAME_.downloadedVersion && [_DELEGATE_.reachability currentReachabilityStatus] == NotReachable) ||//offline but playable...
		([_MODEL_GAME_ hasLatestDownload] && [_MODEL_GAME_.network_level isEqualToString:@"LOCAL"]) //if !local, need to perform maintenance on server so it doesn't keep conflicting with local data
		)
		{
			[_MODEL_ restorePlayerData];
			[self playerDataLoaded];
		}
		else
		[self requestMaintenanceData];
*/

	}

	// todo: implement Android version of these iOS methods:
/*
	public void gameFetchFailed { [self.view addSubview:gameRetryLoadButton); }
	public void retryGameFetch
	{
		[gameRetryLoadButton removeFromSuperview);
		this.requestGameData);
	}
*/

	public void requestMaintenanceData() {
		// todo: show progress bar.
		mGame.requestMaintenanceData();
	}

	// will be called from Game.maintenancePieceReceived() when Game.allMaintenanceDataLoaded() is satisfied.
	public void maintenanceDataLoaded() {
		// prior to 8/16 iOS update:
//		if (!mGame.hasLatestDownload() || !mGame.begin_fresh()) // fixme: ensure begin_game condition is being set meaningfully from server call, getPlayerPlayedGame
//			this.requestPlayerData();
//		else {
//			//_MODEL_ restorePlayerData); // todo: code in the "restoreGameData" process. See iOS LoadingViewController.startLoading -> AppModel.restorePlayerData
//			this.playerDataLoaded();
//		}
		// after 8/16 iOS update:
		this.requestPlayerData();
	}

	// todo: implement Android version of these iOS methods:
/*
	public void maintenancePercentLoaded:(NSNotification *)notif { maintenanceProgressBar.progress = [notif.userInfo["percent") floatValue); }
	public void maintenanceFetchFailed { [self.view addSubview:maintenanceRetryLoadButton); }
	public void retryMaintenanceFetch
	{
		[maintenanceRetryLoadButton removeFromSuperview);
		this.requestMaintenanceData);
	}
*/


	//Player Data
	public void requestPlayerData() {
//		[self.view addSubview:playerProgressLabel); [self.view addSubview:playerProgressBar); // todo progress bar
		mGame.requestPlayerData();
	}

	public void playerDataLoaded() { // gets called only after all game, player and maint data loaded
		if (!mGame.hasLatestDownload()) {
			if (mGame.preload_media())
				this.requestMediaData(); // won't load until maintDataLoaded <-
			else
				this.beginGame(); //[_MODEL_ beginGame);
		}
		else
			this.beginGame();    //[_MODEL_ beginGame);
	}

	// todo: implement Android version of these iOS methods:
/*
	public void playerPercentLoaded:(NSNotification *)notif { playerProgressBar.progress = [notif.userInfo["percent") floatValue); }
	public void playerFetchFailed { [self.view addSubview:playerRetryLoadButton); }
	public void retryPlayerFetch
	{
		[playerRetryLoadButton removeFromSuperview);
		this.requestPlayerData);
	}
*/

	//Media Data
	public void requestMediaData() {
		//[self.view addSubview:mediaProgressLabel); [self.view addSubview:mediaProgressBar); // todo progress bar
		mGame.requestMediaData();
	}

	public void mediaDataLoaded() {
		this.beginGame();
	}

	public void mediaDataComplete() {}

	// todo: implement Android version of these iOS methods:
/*
	public void mediaPercentLoaded:(NSNotification *)notif { mediaProgressBar.progress = [notif.userInfo["percent") floatValue); }
	public void mediaFetchFailed { [self.view addSubview:mediaRetryLoadButton); }
	public void retryMediaFetch {
		[mediaRetryLoadButton removeFromSuperview);
		this.requestMediaData);
	}
*/

	// Stubs from iOS RootViewController. May be unnecessary in Android vers. but included while developing App just in case they become useful.
	public void gameBegan() { // stub for potential use later to duplicate RootViewController behaviours as exist in iOS vs.
		// in iOS, initializes View Controller. Not much else.
		// todo: except of course loading the tabViewController (load tab fragment first so that any
		// todo: exiting fragment popped off the stack will wind up back here.)

//		gamePlayViewController = new GamePlayViewController alloc] initWithDelegate:self); // this initializer happens to be where the tabs get presented
		// (from GamePlayViewController.initWithDeligate)
//		gamePlayTabSelectorController = [[GamePlayTabSelectorViewController alloc] initWithDelegate:self];
		// set up tab appropriate tab fragment()
//		mGamePlayTabSelectorViewController.initContext(this); // also serves many of the functions of iOS GamePlayTabSelectorViewControllerinitWithDelegate
//		mGamePlayTabSelectorViewController.setupDefaultTab();

//		this.displayContentController:gamePlayViewController);
	}

	public void gameChosen() { // stub for potential use later to duplicate RootViewController behaviours as exist in iOS vs.
		// in iOS, starts the game loading sequence.
	}

	public void gameLeft() {
//		mGame.displayQueueModel.endPlay(); // tell displayQueue we're leaving called via Game.gameLeft()
		// in iOS RootViewController, nulls all values kills current gameplayview and returns view to GamesList.
		// pretty much default behaviour in Android Activity stack "back" action. Not needed here;
	}

	private void beginGame() {
		this.preferred_game_id = 0; //assume the preference was met
		if (mGame.begin_fresh())
			this.storeGame(); //we loaded fresh, so can store player data

		// Game data should now be loaded. Populate the NavDrawer tabs.
		mNavigationDrawerFragment.addItems(mGame.tabsModel.playerTabTypes());
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// set up background tab (see View Layer sermon below):
		mGamePlayTabSelectorViewController.initContext(this); // also serves many of the functions of iOS GamePlayTabSelectorViewControllerinitWithDelegate
		mGamePlayTabSelectorViewController.setupDefaultTab();
		// set Title of action bar
		mTitle = AppUtils.prettyName(mGame.tabsModel.playerTabs.get(0).type);
		mActionBar.setTitle(mTitle);

		mGame.displayQueueModel.listen_model_triggers_new_available = 1;
		if (triggerQueueWaiting) {
			// turn on triggerQueueWaiting flag and call again;
			triggerQueueWaiting = false; // background tab fragment should now be in place, so allow Triggers to be evaluated
			mGame.displayQueueModel.reevaluateAutoTriggers();
		}
/*      Gospel of ARIS View Layers from Phil D.:
		The long story short is that, when in a game, the view hierarchy is topped by
		"GamePlayViewController". It manages 3 layers  of views: At the back is the
		"TabViewController" (or something along those lines). It contains one of the tabs as its
		front viewcontroller (like "MapTab" or "QuestsTab" or whatever).

		!!! At any time, something
		might tell GamePlayViewController to display an "instantiable", in which case GPVC will
		display said instantiable (a plaque, an item, a dialog, a quest, etc...) on top of whatever
		tab view controller is behind it.

		This can be dismissed/managed regardless of the
		TabViewController behind it (when you dismiss the "plaque", for example, all that will
		remain behind it is whatever tab viewcontroller was there before the plaque was
		displayed in front of it).

		Another implication of this is that there is a max of one
		instantiable in the view heirarchy at a time (ditto for tabs). The final layer is the
		"notification" layer- at any time a notification could pop up, and it will be displayed on
		top of both the tab and the instantiable view controller (if there is no instantiable view
		controller behind it, that's fine! it just gets displayed at the highest layer).
*/

//		mNavigationDrawerFragment.setMenuVisibility(false); // no workie
//		mNavigationDrawerFragment.setHasOptionsMenu(false);

		boolean debugThis = true; // todo: dev debugging
		if (debugThis)
			checkGameFile(); // todo: dev debugging delete or disable after code is working.

		mGame.logsModel.playerEnteredGame(); //		mGame.logsModel.playerEnteredGame);
		mDispatch.model_game_began(); // calls mGame.gameBegan() and mGamePlayAct.gameBegan()
//		this.showNavBar(); // make sure it's there to start and hide if there's an instantiable displayed on top.// fixme: temp dsabled
	}

	private void showInstantiableFragment(String fragTag, Instance i) {
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Entering showInstantiableFragment instanceType: " + i.object_type);
		if (fragTag == null) return; // todo: temporary fix
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Entering showInstantiableFragment loading fragment for view ");
		// settle any outstanding fragment tasks
		getSupportFragmentManager().executePendingTransactions();
		// if there is no currently visible fragment, set incoming one to current.
		if (mCurrentFragVisible == null || mCurrentFragVisible.isEmpty())
			mCurrentFragVisible = fragTag;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		// get specific fragments involved in transition
		Fragment currentVisibleFrag = fm.findFragmentByTag(mCurrentFragVisible);
		Fragment fragToDisplay = fm.findFragmentByTag(fragTag);
		// set transition
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		if (!fragToDisplay.isAdded())
			ft.addToBackStack(fragTag);
		ft.replace(R.id.fragment_view_container, fragToDisplay);
		ft.commit();

		hideNavBar();
		setAsFrontmostFragment(fragTag);
	}

//	private void showInstantiableFragment(String fragTag, Instance i) {
//		// if somehow we tried to transition to the fragment already showing, bail.
//		if (fragTag.contentEquals(mCurrentFragVisible)) return;
//		// settle any outstanding fragment tasks
//		getSupportFragmentManager().executePendingTransactions();
//		// if there is no currently visible fragment, set incoming one to current.
//		if (mCurrentFragVisible == null || mCurrentFragVisible.isEmpty())
//			mCurrentFragVisible = fragTag;
//		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		// get specific fragments involved in transition
//		Fragment currentVisibleFrag = fm.findFragmentByTag(mCurrentFragVisible);
//		Fragment fragToDisplay = fm.findFragmentByTag(fragTag);
//		// set transition
//		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//		ft.hide(currentVisibleFrag); // hide old fragment view
//		ft.show(fragToDisplay); // show new fragment
//		ft.commit();
//
//		setAsFrontmostFragment(fragTag);
//	}

	public void setAsFrontmostFragment(String fragTag) {
		// set visibility tracking vars
		fragVisible.put(mCurrentFragVisible, false);
		fragVisible.put(fragTag, true);
		mCurrentFragVisible = fragTag;
	}

	public void dismissFragment() {

	}

	private void storeGame() {

		// serialize entire game to an app internal local file
		Gson gson = new Gson();
		String jsonGame = gson.toJson(mGame); // data = mGame.serialize] dataUsingEncoding:NSUTF8StringEncoding);

		File gameStorageFile = AppUtils.gameStorageFile(this, mGame.game_id);
		AppUtils.writeToFileStream(this, gameStorageFile, jsonGame);

		// just store the game downloadedVersion field on App prefs
		SharedPreferences.Editor prefsEd = appPrefs.edit();
		prefsEd.putLong(AppUtils.gameStorageFile(this, mGame.game_id).getName() + ".downloadedVersion", mGame.downloadedVersion);
		prefsEd.commit();

/* save the whole ARISModel (iOS) done by gson in Android along with the Game object. */
//		ARISModel *m;
//		for(long i = 0; i < _MODEL_GAME_.models.count; i++)
//		{
//			m = _MODEL_GAME_.modelsi);
//			data = new m serializeGameData] dataUsingEncoding:NSUTF8StringEncoding);
//			file = [folder stringByAppendingPathComponent:[NSString stringWithFormat:"%@_game.json",m.serializedName]);
//			[data writeToFile:file atomically:YES);
//			new NSURL fileURLWithPath:file] setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error);
//		}
//		for(long i = 0; i < _MODEL_GAME_.models.count; i++)
//		{
//			m = _MODEL_GAME_.modelsi);
//			data = new m serializePlayerData] dataUsingEncoding:NSUTF8StringEncoding);
//			file = [folder stringByAppendingPathComponent:[NSString stringWithFormat:"%@_player.json",m.serializedName]);
//			[data writeToFile:file atomically:YES);
//			new NSURL fileURLWithPath:file] setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:&error);
//		}
		mGame.downloadedVersion = mGame.version;
	}

	public void checkGameFile() { // for debugging; open, or attempt to open the game file and deserialize its contents
		//get directory listing of the "FilesDir"
		File appDir = new File(this.getFilesDir().getPath());
		File[] directoryContent = appDir.listFiles();
		int numFiles = directoryContent.length;

		File gameFile = AppUtils.gameStorageFile(this, mGame.game_id);
		boolean existsAndIsFile = gameFile.exists() && gameFile.isFile();
		if (gameFile.isFile() && gameFile.getName().endsWith("_game.json")) {
			String jsonStoredGame = AppUtils.readFromFileStream(this, gameFile); // read raw json from stored game file
			Gson gson = new Gson();
			Game g = gson.fromJson(jsonStoredGame, Game.class); // deserialize json into Game
			String temp = g.name;
		}

	}

	private void deleteStoredGame() {
		// todo: should there not be some house cleaning so game files don't accumulate on device?
		// todo: A single game file can take over 350kb. 20 or 30 files might start to become an issue.
		// todo: question is, when to call this?
		this.deleteFile(AppUtils.gameStorageFile(this, mGame.game_id).getName());
	}

	@Override
	public void onStop() {
//		mGame.pauseGame(); // not sure if this might be needed for android lifecycle control of game.
		super.onStop();
	}

	@Override
	public void onDestroy() {
		this.gameLeft();
		mGame.gameLeft();
		super.onDestroy();
	}

	public void dropItem(long item_id, long qty) {

	}

	/*
	*
	* "You should consider what you plan to do with the fragment to decide which path to follow.
	* If you use a FragmentTransaction to hide the fragment, then it can still be in the running
	* state of its lifecycle, but its UI has been detached from the window so it's no longer
	* visible. So you could technically still interact with the fragment and reattach its UI later
	* you need to. If you replace the fragment, the you are actually pulling it out of the container
	* and it will go through all of the teardown events in the lifecycle (onPause, onStop, etc) and
	* if for some reason you need that fragment again you would have to insert it back into the
	* container and let it run through all of its initialization again.
	*
	* If there is a high probability that you will need that fragment again, then just hide it
	* because it's a less expensive operation to redraw it's layout than to completely reinitialize
	* it.
	* */

//	@Override
//	public void onNavigationDrawerItemSelected(int position) {
//		// update the main content by replacing fragments
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		switch (position) {
//			case 0:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayQuestsFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 1:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayMapFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 2:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayInventoryFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 3:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayScannerFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 4:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayDecoderFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 5:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayPlayerFragment.newInstance(position + 1))
//						.commit();
//				break;
//			case 6:
//				fragmentManager.beginTransaction()
//						.replace(R.id.fragment_view_container, GamePlayNoteFragment.newInstance(position + 1))
//						.commit();
//				break;
//		}
//	}

	@Override
	public void onNavigationDrawerItemSelected(String itemName) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		if (itemName.equals("Quests")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, QuestsViewFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Map")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, MapViewFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Inventory")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, new InventoryViewFragment())
					.commit();
		}
		else if (itemName.equals("Scanner")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, ScannerViewFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Decoder")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, DecoderViewFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Player")) { // todo: GamePlayPlayerFragment? What is this? Does it need to exists?
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, GamePlayPlayerFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Notebook")) {
			fragmentManager.beginTransaction()
					.addToBackStack(itemName)
					.replace(R.id.fragment_view_container, NoteViewFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Leave Game")) {
			fragmentManager.popBackStack(); // leave this fragment
			onBackPressed(); // leave GamePlayActivity

		}

//		setActionBarTitle(itemName);
		onSectionAttached(itemName);
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

	public void onSectionAttached(String name) {
		mTitle = name;
	}

	public void restoreActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setTitle(mTitle);
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

	public void instantiableViewControllerRequestsDismissal(Instance ivc) {
		// [((ARISViewController *)ivc).navigationController dismissViewControllerAnimated:NO completion:nil]; <-- I think this just the delegate. ?
		viewingInstantiableObject = false;
		// [self reSetOverlayControllersInVC:self atYDelta:-20]; <-- Sets up a gameNotificationView.  Not sure what a gameNotificationView is.
		// [_MODEL_LOGS_ playerViewedContent:ivc.instance.object_type id:ivc.instance.object_id];
		mGame.logsModel.playerViewedContent(ivc.object_type, ivc.object_id);
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " instantiableViewControllerRequestsDismissal() called. about to call tryDequeue ");
		// [self performSelector:@selector(tryDequeue) withObject:nil afterDelay:1];

//		tryDequeue(); // jumping straight to tryDequeue will result in previous item in queue to not get dequeued.

		this.performSelector.postDelayed(new Runnable() {
			@Override
			public void run() {
				tryDequeue();
			}
		}, 1000); //:@selector(tryDequeue) withObject:nil afterDelay:1);

//		if (!this.doDequeue && instantiableViewController.viewControllers[0] == ivc) {
//			[self displayContentController:gamePlayRevealController];
//			instantiableViewController = nil;

		}

	@Override
	public void fragmentPlaqueDismiss() {

		// Android implementation of iOS GamePlayViewController.instantiableViewControllerRequestsDismissal:
		// todo: make plaque fragment disband (stop) somewhere in here, but not before we're done referring to it.
		// todo perhaps just hide it at this point and let it get replaced when the next view is enqueued. ?
		// this happens in [*]ViewController.dismissSelf() in iOS; in Android we stuff them all in this method.
		this.instantiableViewControllerRequestsDismissal(plaqueViewFragment.mInstance);
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " fragmentPlaqueDismiss(). looking at plaqueViewFragment.tab: ");
		// now tell this fragment to die
		if (plaqueViewFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.popBackStack();
//			if (!viewingInstantiableObject) {  // todo: temporary in leu of showNav() call in fragment dismissSelf()
//				this.showNavBar();
//			}
			this.hideNavBar();
		}
	}

	@Override
	public void fragmentWebPageViewDismiss() {
		this.instantiableViewControllerRequestsDismissal(webPageViewFragment.instance);

		if (webPageViewFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.popBackStackImmediate(); // immediate?
		}
	}

	@Override
	public void fragmentItemViewDismiss() {
		this.instantiableViewControllerRequestsDismissal(itemViewFragment.instance);

		if (itemViewFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.popBackStackImmediate(); // immediate?
		}
	}

	@Override
	public void fragmentDialogDismiss() {
		this.instantiableViewControllerRequestsDismissal(dialogViewFragment.instance);
		// now tell this fragment to die
		if (dialogViewFragment != null) {
			FragmentManager fm = getSupportFragmentManager();
			fm.popBackStack(); // immediate?
//			if (!viewingInstantiableObject) { // todo: temporary in leu of showNav() call in fragment dismissSelf()
//				this.showNavBar();
//			}
		}
	}

	@Override
	public void gamePlayTabBarViewControllerRequestsNav() {
		// Display the nav drawer at this point, until/unless the next UI view is triggered.
		this.showNav();
	}

	private void showNav() {
		this.showNavBar();
		this.openNavDrawer();
	}

	public void onClickMapOpenDrawer(View v) {
		this.openNavDrawer();
	}


	/*
	*
	*  GamePlayViewController Section (Separate class in iOS)
	*
	*/

	public void tryDequeue() { // combines tryDequeue and doDequeue from iOS
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Try Dequeue: viewingInstantiableObject = " + viewingInstantiableObject);
		//Doesn't currently have the view-heirarchy authority to display.
		//if(!(self.isViewLoaded && self.view.window)) //should work but apple's timing is terrible
		if (viewingInstantiableObject)
			return;
		Object o;
		o = mGame.displayQueueModel.dequeue();
		if (o != null) {
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " in tryDeQueue(): object is class " + o.getClass().getSimpleName());
			if (o instanceof Trigger)
				this.displayTrigger((Trigger) o);
			else if (o instanceof Instance)
				this.displayInstance((Instance) o);
			else if (o instanceof Tab)
				this.displayTab((Tab) o);
			else if (InstantiableProtocol.class.isInstance(o))
				this.displayObject(o);
		}
	}

	public void flushBufferQueuedInstances() {
		for (int i = 0; i < local_inst_queue.size(); i++) {
			Instance inst = mGame.instancesModel.instanceForId(local_inst_queue.get(i)); //_MODEL_INSTANCES_ instanceForId:((NSNumber *)local_inst_queuei]).longValue);
			if (inst.instance_id > 0) {
				mGame.displayQueueModel.enqueueInstance(inst);
				local_inst_queue.remove(i); // removeObjectAtIndex(i);
				i--;
			}
		}
	}

	public void displayTrigger(Trigger t) {
		Instance i = mGame.instancesModel.instanceForId(t.instance_id);
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Entering displayTrigger triggerid: " + t.trigger_id + ", instanceid: " + i.instance_id);
		if (i.instance_id < 1) {
			//this is bad and points to a need for a non-global service architecture.
			//see notes by 'local_inst_queue'
			local_inst_queue.add(t.instance_id); // addObject:[NSNumber numberWithLong:t.instance_id]);
		}
		else {
			mDispatch.game_play_display_triggered(t); //_ARIS_NOTIF_SEND_("GAME_PLAY_DISPLAYED_TRIGGER",nil,@{"trigger":t});
			this.displayInstance(i);
			mGame.logsModel.playerTriggeredTriggerId(t.trigger_id);
		}
	}

	public void displayInstance(Instance i) {
		Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Entering displayInstance instanceType: " + i.object_type);
		String tag = "";
		String fragViewToDisplay = "";
//		ARISViewController *vc;
		if (i.object_type.contentEquals("PLAQUE")) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			if (plaqueViewFragment == null) {
				plaqueViewFragment = new PlaqueViewFragment();
				plaqueViewFragment.initContext(this);
				plaqueViewFragment.initWithInstance(i);
				tag = plaqueViewFragment.toString();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.addToBackStack(tag);
				ft.replace(R.id.fragment_view_container, plaqueViewFragment, tag); //set tag.
				Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Fragment added ");
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null)
				if (plaqueViewFragment.isVisible() && plaqueViewFragment.getTag().contentEquals(mCurrentFragVisible))
					return;

			fragViewToDisplay = plaqueViewFragment.getTag(); // same end result as vc var in iOS
//		vc = new PlaqueViewController(i delegate:self);
		}
		else if (i.object_type.contentEquals("ITEM")) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			if (itemViewFragment == null) {
				itemViewFragment = new ItemViewFragment();
				itemViewFragment.initContext(this);
				itemViewFragment.initWithInstance(i);
				tag = itemViewFragment.toString();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_view_container, itemViewFragment, tag); //set tag.
				ft.addToBackStack(tag);
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null) if (itemViewFragment.isVisible()
					&& itemViewFragment.getTag().contentEquals(mCurrentFragVisible))
				return;

			fragViewToDisplay = itemViewFragment.getTag(); // same end result as vc var in iOS
//		vc = new ItemViewController(i delegate:self);
		}
		else if (i.object_type.contentEquals("DIALOG")) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			if (dialogViewFragment == null) {
				dialogViewFragment = new DialogViewFragment();
				dialogViewFragment.initContext(this);
				dialogViewFragment.initWithInstance(i);
				tag = dialogViewFragment.toString();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_view_container, dialogViewFragment, tag); //set tag. // was .add()
				ft.addToBackStack(tag);
//					ft.attach(dialogViewFragment); // was .show()
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null) if (dialogViewFragment.isVisible()
					&& dialogViewFragment.getTag().contentEquals(mCurrentFragVisible))
				return;

			fragViewToDisplay = dialogViewFragment.getTag(); // same end result as vc var in iOS
//		    vc = new DialogViewController(i delegate:self);
		}
		else if (i.object_type.contentEquals("WEB_PAGE")) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			if (webPageViewFragment == null) {
				webPageViewFragment = new WebPageViewFragment();
				webPageViewFragment.initWithInstance(i);
				tag = webPageViewFragment.toString();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_view_container, webPageViewFragment, tag); //set tag.
				ft.addToBackStack(tag);
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null) if (webPageViewFragment.isVisible()
					&& webPageViewFragment.getTag().contentEquals(mCurrentFragVisible))
				return;

			fragViewToDisplay = webPageViewFragment.getTag(); // same end result as vc var in iOS
//		vc = new WebPageViewController(i delegate:self);
		}
		else if (i.object_type.contentEquals("NOTE")) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			if (noteViewFragment == null) {
				noteViewFragment = new NoteViewFragment();
				noteViewFragment.initWithInstance(i);
				tag = noteViewFragment.toString();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_view_container, noteViewFragment, tag); //set tag.
				ft.addToBackStack(tag);
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null) if (noteViewFragment.isVisible()
					&& noteViewFragment.getTag().contentEquals(mCurrentFragVisible))
				return;

			fragViewToDisplay = noteViewFragment.getTag(); // same end result as vc var in iOS
//		vc = new NoteViewController(i delegate:self);
		}

		// Special Cases which do not "actually display anything"
		if (i.object_type.contentEquals("EVENT_PACKAGE")) { //Special case (don't actually display anything)
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			mGame.eventsModel.runEventPackageId(i.object_id); //will take care of log
			//Hack 'dequeue' as simulation for normally inevitable request dismissal of VC we didn't put up...
			this.performSelector.postDelayed(new Runnable() {
				@Override
				public void run() {
					tryDequeue();
				}
			}, 1000); //:@selector(tryDequeue) withObject:nil afterDelay:1);
			return;
		}
		if (i.object_type.contentEquals("SCENE")) { //Special case (don't actually display anything)
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			mGame.scenesModel.setPlayerScene((Scene) i.object());
			mGame.logsModel.playerViewedInstanceId(i.instance_id);
			//Hack 'dequeue' as simulation for normally inevitable request dismissal of VC we didn't put up...
			this.performSelector.postDelayed(new Runnable() {
				@Override
				public void run() {
					tryDequeue();
				}
			}, 1000); //:@selector(tryDequeue) withObject:nil afterDelay:1);
			return;
		}
		if (i.object_type.contentEquals("EVENT_PACKAGE")) { //Special case (don't actually display anything)
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			mGame.eventsModel.runEventPackageId(i.object_id);
			mGame.logsModel.playerViewedInstanceId(i.instance_id);
			//Hack 'dequeue' as simulation for normally inevitable request dismissal of VC we didn't put up...
			this.performSelector.postDelayed(new Runnable() {
				@Override
				public void run() {
					tryDequeue();
				}
			}, 1000); //:@selector(tryDequeue) withObject:nil afterDelay:1);
			return;
		}
		if (i.object_type.contentEquals("FACTORY")) { //Special case (don't actually display anything)
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Instance Type found to be: " + i.object_type);
			//Hack 'dequeue' as simulation for normally inevitable request dismissal of VC we didn't put up...
			this.performSelector.postDelayed(new Runnable() {
				@Override
				public void run() {
					tryDequeue();
				}
			}, 1000); //:@selector(tryDequeue) withObject:nil afterDelay:1);
			return;
		}
		mGame.logsModel.playerViewedInstanceId(i.instance_id);
		mDispatch.game_play_displayed_instance(i); //_ARIS_NOTIF_SEND_("GAME_PLAY_DISPLAYED_INSTANCE",nil,@{"instance"(i});
		if (i.factory_id > 0) {
			Factory f = mGame.factoriesModel.factoryForId(i.factory_id);
			if (f.produce_expire_on_view == 1)
				mGame.triggersModel.expireTriggersForInstanceId(i.instance_id);
		}
		showInstantiableFragment(fragViewToDisplay, i);
//		ARISNavigationController *nav = new ARISNavigationController alloc] initWithRootViewController:vc);
//		this.presentDisplay(nav);
		viewingInstantiableObject = true; // iOS happens in presentDisplay
	}


	public void displayObject(Object o) // - (void) displayObject:(NSObject <InstantiableProtocol>*)o
	{
//		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Entering displayObject instanceType: " + o.getClass().getName());
		Log.d(AppConfig.LOGTAG+AppConfig.LOGTAG_D1, getClass().getSimpleName() + " Entering displayObject instanceType: " + o.getClass().getName());
		String tag = "";
		String fragViewToDisplay = "";
//		ARISViewController *vc;
		Instance i = mGame.instancesModel.instanceForId(0);

//		if(Plaque.class.isInstance(o)) // <- better, worse, same, different?
		if (o instanceof Plaque) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " displayObject() instance of Plaque was found ");
			Plaque p = (Plaque) o;
			i.object_type = "PLAQUE";
			i.object_id = p.plaque_id;
			if (plaqueViewFragment == null || plaqueViewFragment.getTag() == null) {
				plaqueViewFragment = new PlaqueViewFragment();
				tag = plaqueViewFragment.toString();
				plaqueViewFragment.initContext(this);
				plaqueViewFragment.initWithInstance(i);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); // no change
				ft.replace(R.id.fragment_view_container, plaqueViewFragment, tag); //set tag.
				ft.addToBackStack(tag);
				ft.commit();
				getSupportFragmentManager().executePendingTransactions();
				setAsFrontmostFragment(tag);
			}
			// if it's already visible and the frontmost fragment... bail, no further action here
			else if (mCurrentFragVisible != null) {
				if (plaqueViewFragment.isVisible() && plaqueViewFragment.getTag().contentEquals(mCurrentFragVisible)) {
					return;
				}
			}

			fragViewToDisplay = plaqueViewFragment.getTag(); // same end result as vc var in iOS
//			vc = new PlaqueViewController(i delegate:self);
		}
		else if (o instanceof Item) {
			Item it = (Item) o;
			i.object_type = "ITEM";
			i.object_id = it.item_id;
			itemViewFragment.initWithInstance(i);
			fragViewToDisplay = itemViewFragment.getTag(); // same end result as vc var in iOS
//			vc = new ItemViewController(i delegate:self);
		}
		else if (o instanceof Dialog) {
			Dialog d = (Dialog) o;
			i.object_type = "DIALOG";
			i.object_id = d.dialog_id;
			dialogViewFragment.initWithInstance(i);
			fragViewToDisplay = dialogViewFragment.getTag(); // same end result as vc var in iOS
//			vc = new DialogViewController(i delegate:self);
		}
		else if (o instanceof WebPage) {
			WebPage w = (WebPage) o;
			//todo: realize difference in the two condition here
			if (w.web_page_id == 0) { //assume ad hoc (created from some webview href maybe?)
				webPageViewFragment.initWithInstance(i);
				fragViewToDisplay = webPageViewFragment.getTag(); // same end result as vc var in iOS
//				vc = new WebPageViewController alloc] initWithWebPage:w delegate:self);
			}
			else {
				i.object_type = "WEB_PAGE";
				i.object_id = w.web_page_id;
				webPageViewFragment.initWithInstance(i);
				fragViewToDisplay = webPageViewFragment.getTag(); // same end result as vc var in iOS
//				vc = new WebPageViewController(i delegate:self);
			}
		}
		else if (o instanceof Note) {
			Note n = (Note) o;
			i.object_type = "NOTE";
			i.object_id = n.note_id;
			noteViewFragment.initWithInstance(i);
			fragViewToDisplay = noteViewFragment.getTag(); // same end result as vc var in iOS
//			vc = new NoteViewController(i delegate:self);
		}

		showInstantiableFragment(fragViewToDisplay, i);
//		ARISNavigationController *nav = new ARISNavigationController alloc] initWithRootViewController:vc);
//		this.presentDisplay:nav);
		viewingInstantiableObject = true; // iOS happens in presentDisplay

	}

	// This (I'm guessing) is handling a selection in what we are calling the Nav Drawer
	public void displayTab(Tab t) {
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Entering displayTab tabid: " + t.tab_id);
//todo:		gamePlayTabSelectorController.requestDisplayTab(t);
		this.tryDequeue(); //no 'closing event' for tab
	}

	public void displayScannerWithPrompt(String p) {
//todo:		gamePlayTabSelectorController.requestDisplayScannerWithPrompt(p);
	}

	// dialog option was selected; determine desired course of action
	@Override
	public void onOtherDialogOptionSelected(long dialogOptionId) {
		DialogOption op = mGame.dialogsModel.dialogOptions.get(dialogOptionId);
		FragmentManager fragmentManager = getSupportFragmentManager();
		// Handled in Dialog Fragment
//		if (op.link_type.contentEquals("DIALOG_SCRIPT")) {
////		[delegate dialogScriptChosen:[_MODEL_DIALOGS_ scriptForId:op.link_id]];
//		}
		// also handled directly in Dialog Fragment
//		else if (op.link_type.contentEquals("EXIT_TO_DIALOG")) {
//			// Optimized: reuse the same controllers, just switch it to a new dialog
////			[delegate dialogScriptChosen:[_MODEL_DIALOGS_ scriptForId:[_MODEL_DIALOGS_ dialogForId:op.link_id].intro_dialog_script_id]];
//		}
		if (op.link_type.contentEquals("EXIT")) {
//			[delegate exitRequested];
			// todo: same as finish();lets try it:
			this.finish();
		}
		else if (op.link_type.contentEquals("EXIT_TO_PLAQUE")) {
//			[_MODEL_DISPLAY_QUEUE_ enqueueObject:[_MODEL_PLAQUES_ plaqueForId:op.link_id]];    [delegate exitRequested];
			mGame.displayQueueModel.enqueueObject(mGame.plaquesModel.plaqueForId(op.link_id));
			// todo: kill dialog fragment now?
//			fragmentManager.beginTransaction()
//					.replace(R.id.fragment_view_container, new GamePlayPlaqueFragment())
//					.commit();

		}
		else if (op.link_type.contentEquals("EXIT_TO_ITEM")) {
//				[_MODEL_DISPLAY_QUEUE_ enqueueObject:[_MODEL_ITEMS_ itemForId:op.link_id]];
//				[delegate exitRequested];
		}
		else if (op.link_type.contentEquals("EXIT_TO_WEB_PAGE")) {
//				[_MODEL_DISPLAY_QUEUE_ enqueueObject:[_MODEL_WEB_PAGES_ webPageForId:op.link_id]]; [delegate exitRequested];
		}
		else if (op.link_type.contentEquals("EXIT_TO_TAB")) {
//			[_MODEL_DISPLAY_QUEUE_ enqueueTab:[_MODEL_TABS_ tabForId:op.link_id]];             [delegate exitRequested];

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		android.app.FragmentManager fm = getFragmentManager();
		if (fm.getBackStackEntryCount() == 0) { // if this is zero, we should have completely left the game.
//			this.leaveGame(); // if this hasn't already been called, calling it here would be very bad.
			this.finish();
		}
	}

	public void showNavBar() {
		mNavigationDrawerFragment.getActionBar().show();
	}

	public void hideNavBar() {
		mNavigationDrawerFragment.getActionBar().hide();
	}

	public void openNavDrawer() {
		mNavigationDrawerFragment.mDrawerLayout.openDrawer(mNavigationDrawerFragment.mFragmentContainerView);
	}

	public void closeNavDrawer() {
		mNavigationDrawerFragment.mDrawerLayout.closeDrawer(mNavigationDrawerFragment.mFragmentContainerView);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		Gson gson = new Gson();
		String jsonGame = gson.toJson(mGame);
		savedInstanceState.putString("mGame", jsonGame);
		savedInstanceState.putSerializable(FRAGMENT_VISIBILITY_MAP, fragVisible);
		super.onSaveInstanceState(savedInstanceState);
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

	@Override
	public void onFragmentInteraction(Uri uri) {
		Uri u = uri;
	}

	@Override
	public void onSecondFragButtonClick(String message) {
		String gotit = message;
	}

	public void leaveGame() {
		mGame.endPlay();
		mGame = null;
		this.leave_game_enabled = true;
//		[_DEFAULTS_ saveUserDefaults]; // <-- todo
		mDispatch.model_game_left();
	}

//	public void presentDisplay(UIViewController vc)
//	{
//		this.presentViewController:vc animated:NO completion:nil);
//		viewingInstantiableObject = YES;
//
//		this.reSetOverlayControllersInVC:vc atYDelta:20);
//	}


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
