package edu.uoregon.casls.aris_android.data_objects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.models.ARISModel;
import edu.uoregon.casls.aris_android.models.DialogsModel;
import edu.uoregon.casls.aris_android.models.EventsModel;
import edu.uoregon.casls.aris_android.models.FactoriesModel;
import edu.uoregon.casls.aris_android.models.GameInstancesModel;
import edu.uoregon.casls.aris_android.models.GroupInstancesModel;
import edu.uoregon.casls.aris_android.models.GroupsModel;
import edu.uoregon.casls.aris_android.models.InstancesModel;
import edu.uoregon.casls.aris_android.models.ItemsModel;
import edu.uoregon.casls.aris_android.models.LogsModel;
import edu.uoregon.casls.aris_android.models.NotesModel;
import edu.uoregon.casls.aris_android.models.OverlaysModel;
import edu.uoregon.casls.aris_android.models.PlaquesModel;
import edu.uoregon.casls.aris_android.models.PlayerInstancesModel;
import edu.uoregon.casls.aris_android.models.QuestsModel;
import edu.uoregon.casls.aris_android.models.RequirementsModel;
import edu.uoregon.casls.aris_android.models.ScenesModel;
import edu.uoregon.casls.aris_android.models.TabsModel;
import edu.uoregon.casls.aris_android.models.TagsModel;
import edu.uoregon.casls.aris_android.models.TriggersModel;
import edu.uoregon.casls.aris_android.models.WebPagesModel;
import edu.uoregon.casls.aris_android.services.PollTimerService;

/*
  Created by smorison on 7/28/15.
 */
public class Game {

	public long n_game_data_to_receive        = 0;
	public long n_game_data_received          = 0;
	public long n_maintenance_data_to_receive = 0;
	public long n_maintenance_data_received   = 0;
	public long n_player_data_to_receive      = 0;
	public long n_player_data_received        = 0;
	public long n_media_data_to_receive       = 0;
	public long n_media_data_received         = 0;

	// booleans as ints to conform to json from ARIS responses
	public int listen_player_piece_available      = 1;
	public int listen_game_piece_available        = 1;
	public int listen_maintenance_piece_available = 1;
	public int listen_media_piece_available       = 1;

	private static final String HTTP_GET_FULL_GAME_REQ_API = "v2.games.getFullGame/";
	public long game_id;
	public String name = "";
	public String desc = "";
	public int published; // boolean as int
	public String   type     = "";
	public Location location = new Location("0"); // from iOS; not used?
	public long player_count;

	public Media icon_media;
	public long  icon_media_id;
	public Media media;
	public long  media_id;

	public long intro_scene_id;

	public List<User>        authors  = new ArrayList<>();
	public List<GameComment> comments = new ArrayList<GameComment>();

	public String   map_type     = "";
	public String   map_focus    = "";
	public Location map_location = new Location("0");
	public double map_zoom_level;

	// booleans as ints to conform to json from ARIS server
	public int map_show_player;
	public int map_show_players;
	public int map_offsite_mode;

	public int notebook_allow_comments;
	public int notebook_allow_likes;
	public int notebook_allow_player_tags;

	public long   inventory_weight_cap;
	//	public String network_level = "";
	public String network_level;
	public int    allow_download; // boolean as int
	public int    preload_media; // boolean as int
	public long   version;

	public List<ARISModel> models = new ArrayList<>(); // List of all the models below for iteration convenience

	// Game subcomponent classes
	public ScenesModel          scenesModel;
	public GroupsModel          groupsModel;
	public PlaquesModel         plaquesModel;
	public ItemsModel           itemsModel;
	public DialogsModel         dialogsModel;
	public WebPagesModel        webPagesModel;
	public NotesModel           notesModel;
	public TagsModel            tagsModel;
	public EventsModel          eventsModel;
	public RequirementsModel    requirementsModel;
	public TriggersModel        triggersModel;
	public FactoriesModel       factoriesModel;
	public OverlaysModel        overlaysModel;
	public InstancesModel       instancesModel;
	public PlayerInstancesModel playerInstancesModel;         //todo: is this where gameUsers go? Players == Users??
	public GameInstancesModel   gameInstancesModel;
	public GroupInstancesModel  groupInstancesModel;
	public TabsModel            tabsModel;
	public LogsModel            logsModel;
	public QuestsModel          questsModel;
//	public DisplayQueueModel 	displayQueueModel; // iOS only for now
	// medias (in GamePlayAct 		

	//local stuff
	public long downloadedVersion   = 0;
	public int  know_if_begin_fresh = 0; // boolean as int
	public int  begin_fresh         = 0; // boolean as int

	//	PollTimer vars
	public  Boolean isPollTimerRunning = false;
	private Intent  pollTimerSvcIntent = null;

	// FYI transient indicates "do not serialize"; gson will die a recursive death if it did.
	public transient GamePlayActivity mGamePlayAct; // For reference to GamePlayActivity; do not instantiate (new) object or circular references will ensue.
	public transient Context          mContext;

	// Empty Constructor
	public Game() {
		this.initialize(); // almost certainly redundant in Android since all vars are init'd when declared todo: remove if so.
	}

	// Basic Constructor with json game block
	public Game(JSONObject jsonGame) throws JSONException {
		this.initialize();
		initWithJson(jsonGame);
	}

	public Game(JSONObject jsonGame, Context context) throws JSONException {
		this.initContext(context);
		this.initialize();
		initWithJson(jsonGame);
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
	}

	public void initContext(Context context) {
		mContext = context; // used for pregameplay activities e.g. GamesListActivity
	}

	private void initialize() {
		n_game_data_received = 0;
		n_player_data_received = 0;

		authors.clear(); // redundant at this point, but left in to keep in sync with iOS code
		comments.clear();

		downloadedVersion = 0;

		know_if_begin_fresh = 0;
		begin_fresh = 0;
	}

	// function name in keeping with iOS, but just look up stored game file and set downloadedVersion attribute.
	public void initWithDictionary() {
		// todo: check for game saved as file from prior play load if found iOS:
		// todo: perhaps use the Game stored in GamePlayAct.bundle; ultimately needs to be placed in
		// todo: appPrefs or a file if it's too big for appPrefs. Looks like in iOS they're saving
		// todo: multiple game files as "[path=game_id]/game.json". That would seem to indicate that we really
		// todo: need to be following suit by using individual files, not appPrefs or Bundle.

		// this is insane to deserialize the entire game and all descendant classes just for one field. I'm going to use appPrefs.?
		//   check for game saved as file from prior play; load if found.
		// fixme: pick only one of the following two blocks below. The second would be vastly more efficient, if the whole game obj isn't needed.
		if (mGamePlayAct != null && false) { // fixme: temporairly block this cond. while playing with appPrefs
			File gameFile = AppUtils.gameStorageFile(mContext, this.game_id);
			Boolean letMePass = false; // something I can use to trick my way past the condition below in the debugger.
			if (gameFile.exists() || letMePass) {
				Gson gson = new Gson();
				String jsonStoredGame = AppUtils.readFromFileStream(mContext, gameFile); // read raw json from stored game file
				Game storedGame = gson.fromJson(jsonStoredGame, Game.class); // deserialize json into Game

				this.downloadedVersion = storedGame.version;
//			GameDataLite storedGameLite = gson.fromJson(jsonStoredGame, GameDataLite.class); // deserialize json into Game
//			this.downloadedVersion = storedGameLite.version;

			}
		}
		// getDownloaded version via appPrefs
		if (mGamePlayAct != null) { //fixMe: need to get downloadedVersion set correctly. Media load condition depends on it.
			String gameStorageFileName = gameStorageFile(mGamePlayAct).getName();
			if (mGamePlayAct.appPrefs.contains(gameStorageFileName + ".downloadedVersion")) {
				this.downloadedVersion = mGamePlayAct.appPrefs.getLong(gameStorageFileName + ".downloadedVersion", 0);
			}
		}
	}


	// only to be called from an instatiation of GamePlayActivity.
	public static File appStorageDir(GamePlayActivity gamePlayAct) {
		File appDir = new File(gamePlayAct.getFilesDir().getPath());
		File gameDir = gamePlayAct.getDir(appDir + String.valueOf(gamePlayAct.mGame.game_id), Context.MODE_PRIVATE); //Creating an internal dir;
		return gameDir;
	}

	public static File gameStorageFile(GamePlayActivity gamePlayAct) {
		String gameFileName = String.valueOf(gamePlayAct.mGame.game_id) + "_game.json";
		File appDir = new File(gamePlayAct.getFilesDir().getPath());
		File gameFile = new File(appDir, gameFileName);
		return gameFile;
	}

//	public static boolean streamFileExists(Context context, File file) {
//
//	}

	private void initWithJson(JSONObject jsonGame) throws JSONException {
		if (jsonGame.has("game_id") && !jsonGame.getString("game_id").equals("null"))
			game_id = Long.parseLong(jsonGame.getString("game_id"));
		if (jsonGame.has("name"))
			name = jsonGame.getString("name");
		if (jsonGame.has("allow_download"))
			allow_download = Integer.parseInt(jsonGame.getString("allow_download"));
		if (jsonGame.has("description"))
			desc = jsonGame.getString("description");
//		if (jsonGame.has("tick_script"))
//			tick_script = jsonGame.getString("tick_script");
//		if (jsonGame.has("tick_delay") && !jsonGame.getString("tick_delay").equals("null"))
//			tick_delay = Long.parseLong(jsonGame.getString("tick_delay"));
		if (jsonGame.has("intro_scene_id") && !jsonGame.getString("intro_scene_id").equals("null"))
			intro_scene_id = Long.parseLong(jsonGame.getString("intro_scene_id"));
		if (jsonGame.has("icon_media_id") && !jsonGame.getString("icon_media_id").equals("null"))
			icon_media_id = Long.parseLong(jsonGame.getString("icon_media_id"));
		if (jsonGame.has("media_id") && !jsonGame.getString("media_id").equals("null"))
			media_id = Long.parseLong(jsonGame.getString("media_id"));
		if (jsonGame.has("latitude") && !jsonGame.getString("latitude").equals("null"))
			location.setLatitude(Double.parseDouble(jsonGame.getString("latitude")));
		if (jsonGame.has("longitude") && !jsonGame.getString("longitude").equals("null"))
			location.setLongitude(Double.parseDouble(jsonGame.getString("longitude")));
		if (jsonGame.has("map_focus") && !jsonGame.getString("map_focus").equals("null"))
			map_focus = jsonGame.getString("map_focus");
		if (jsonGame.has("map_type") && !jsonGame.getString("map_type").equals("null"))
			map_type = jsonGame.getString("map_type");
		if (jsonGame.has("map_latitude") && !jsonGame.getString("map_latitude").equals("null"))
			map_location.setLatitude(Double.parseDouble(jsonGame.getString("map_latitude")));
		if (jsonGame.has("map_longitude") && !jsonGame.getString("map_longitude").equals("null"))
			map_location.setLongitude(Double.parseDouble(jsonGame.getString("map_longitude")));
		if (jsonGame.has("map_zoom_level") && !jsonGame.getString("map_zoom_level").equals("null"))
			map_zoom_level = Double.parseDouble(jsonGame.getString("map_zoom_level"));
		if (jsonGame.has("map_show_player") && !jsonGame.getString("map_show_player").equals("null"))
			map_show_player = Integer.parseInt(jsonGame.getString("map_show_player"));
		if (jsonGame.has("map_show_players") && !jsonGame.getString("map_show_players").equals("null"))
			map_show_players = Integer.parseInt(jsonGame.getString("map_show_players"));
		if (jsonGame.has("map_offsite_mode") && !jsonGame.getString("map_offsite_mode").equals("null"))
			map_offsite_mode = Integer.parseInt(jsonGame.getString("map_offsite_mode"));
		if (jsonGame.has("network_level") && !jsonGame.getString("network_level").equals("null"))
			network_level = jsonGame.getString("network_level");
		if (jsonGame.has("notebook_allow_comments") && !jsonGame.getString("notebook_allow_comments").equals("null"))
			notebook_allow_comments = Integer.parseInt(jsonGame.getString("notebook_allow_comments"));
		if (jsonGame.has("notebook_allow_likes") && !jsonGame.getString("notebook_allow_likes").equals("null"))
			notebook_allow_likes = Integer.parseInt(jsonGame.getString("notebook_allow_likes"));
		if (jsonGame.has("notebook_allow_player_tags") && !jsonGame.getString("notebook_allow_player_tags").equals("null"))
			notebook_allow_player_tags = Integer.parseInt(jsonGame.getString("notebook_allow_player_tags"));
		if (jsonGame.has("published") && !jsonGame.getString("published").equals("null"))
			published = Integer.parseInt(jsonGame.getString("published"));
		if (jsonGame.has("preload_media") && !jsonGame.getString("preload_media").equals("null"))
			preload_media = Integer.parseInt(jsonGame.getString("preload_media"));
		if (jsonGame.has("type") && !jsonGame.getString("type").equals("null"))
			type = jsonGame.getString("type");
		if (jsonGame.has("intro_scene_id") && !jsonGame.getString("intro_scene_id").equals("null"))
			intro_scene_id = Long.parseLong(jsonGame.getString("intro_scene_id"));
		if (jsonGame.has("player_count") && !jsonGame.getString("player_count").equals("null"))
			player_count = Long.parseLong(jsonGame.getString("player_count"));
		if (jsonGame.has("inventory_weight_cap") && !jsonGame.getString("inventory_weight_cap").equals("null"))
			inventory_weight_cap = Long.parseLong(jsonGame.getString("inventory_weight_cap"));
		if (jsonGame.has("version") && !jsonGame.getString("version").equals("null"))
			version = Long.parseLong(jsonGame.getString("version"));

		//not found in basic game data apparently, at least not here in Game() see full game init
//		jsonGame.getString("inventory_weight_cap");
//		jsonGame.getString("is_siftr");
//		jsonGame.getString("siftr_url");
//		jsonGame.getString("moderated");
//		jsonGame.getString("notebook_trigger_scene_id");
//		jsonGame.getString("notebook_trigger_requirement_root_package_id");
//		jsonGame.getString("notebook_trigger_title");
//		jsonGame.getString("notebook_trigger_icon_media_id");
//		jsonGame.getString("notebook_trigger_distance");
//		jsonGame.getString("notebook_trigger_infinite_distance");
//		jsonGame.getString("notebook_trigger_wiggle");
//		jsonGame.getString("notebook_trigger_show_title");
//		jsonGame.getString("notebook_trigger_hidden");
//		jsonGame.getString("notebook_trigger_on_enter");

		// run through full game init, in the event that there are comments present in the basic game json block
//		initFullGameDetailsWithJson(jsonGame);

	}

	public void initWithMap(HashMap<String, String> hmapGame) { // stub for future possible use.
	}

	// fill in the fields not present in the constructor parameters, such authors and inventory_weight_cap
	public void initFullGameDetailsWithJson(JSONObject jsonFullGame) throws JSONException {
		Gson gson = new Gson();
		JSONObject jsonGameData = jsonFullGame.getJSONObject("data");
		if (jsonGameData.has("inventory_weight_cap") && !jsonGameData.getString("inventory_weight_cap").equals("null"))
			inventory_weight_cap = Long.parseLong(jsonGameData.getString("inventory_weight_cap"));
		// get authors from full game block
		if (jsonGameData.has("authors")) {
			JSONArray jsonAuthorsList = jsonGameData.getJSONArray("authors");
			for (int i = 0; i < jsonAuthorsList.length(); i++) {
				// Ex Author Json: "authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}]
				JSONObject jsonAuthor = jsonAuthorsList.getJSONObject(i);
				authors.add(new User(jsonAuthor));
			}
		}
		//todo: update this media loading process to conform to the newer iOS form.
		if (jsonGameData.has("media")) {
			// get media block
			JSONObject jsonMedia = jsonGameData.getJSONObject("media");
			media = gson.fromJson(jsonMedia.toString(), Media.class);
//			android.util.Log.d(Config.LOGTAG, getClass().getSimpleName() + "Debug break to examine object media");
			media.initUrlDuctTape(); // tie server media data into new class var names
		}
		if (jsonGameData.has("icon_media")) {
			// get icon_media block
			JSONObject jsonMedia = jsonGameData.getJSONObject("icon_media");
			icon_media = gson.fromJson(jsonMedia.toString(), Media.class);
			icon_media.initUrlDuctTape();// tie server media data into new class var names
		}

		// none of these are utilized, or even mentioned in, in the iOS code leaving in for future potential
//		jsonFullGame.getString("is_siftr");
//		jsonFullGame.getString("siftr_url");
//		jsonFullGame.getString("moderated");
//		jsonFullGame.getString("notebook_trigger_scene_id");
//		jsonFullGame.getString("notebook_trigger_requirement_root_package_id");
//		jsonFullGame.getString("notebook_trigger_title");
//		jsonFullGame.getString("notebook_trigger_icon_media_id");
//		jsonFullGame.getString("notebook_trigger_distance");
//		jsonFullGame.getString("notebook_trigger_infinite_distance");
//		jsonFullGame.getString("notebook_trigger_wiggle");
//		jsonFullGame.getString("notebook_trigger_show_title");
//		jsonFullGame.getString("notebook_trigger_hidden");
//		jsonFullGame.getString("notebook_trigger_on_enter");

		// stub-in for when/if comments seem to become a part of the Game data.
//		if (jsonFullGame.has("comments")) {
//			JSONArray jsonCommentsList = jsonFullGame.getJSONArray("comments");
//			for (int i=0; i < jsonCommentsList.length(); i++) {
//				// Ex Comments Json: "comments":[{?????}] // awaiting ...
//				JSONObject jsonComment = jsonCommentsList.getJSONObject(i);
//				comments.add(new Comment(jsonComment));
//			}
//		}

	}

	public void getReadyToPlay() {
		listen_player_piece_available = 1;        //_ARIS_NOTIF_LISTEN_(@"PLAYER_PIECE_AVAILABLE",self,@selector(gamePlayerPieceReceived),null);
		listen_maintenance_piece_available = 1;    // _ARIS_NOTIF_LISTEN_(@"MAINTENANCE_PIECE_AVAILABLE",self,@selector(maintenancePieceReceived),nil);
		listen_game_piece_available = 1;        // _ARIS_NOTIF_LISTEN_(@"GAME_PIECE_AVAILABLE",self,@selector(gamePieceReceived),null);
		listen_media_piece_available = 1;        // _ARIS_NOTIF_LISTEN_(@"MEDIA_PIECE_AVAILABLE",self,@selector(mediaPieceReceived),nil);

		n_game_data_received = 0;
		n_player_data_received = 0;
		//@formatter:off
		scenesModel = new ScenesModel();                   models.add(scenesModel);
		groupsModel = new GroupsModel();                   models.add(groupsModel);
		plaquesModel = new PlaquesModel();                 models.add(plaquesModel);
		itemsModel = new ItemsModel();                     models.add(itemsModel);
		dialogsModel = new DialogsModel();                 models.add(dialogsModel);
		webPagesModel = new WebPagesModel();               models.add(webPagesModel);
		notesModel = new NotesModel();                     models.add(notesModel);
		tagsModel = new TagsModel();                       models.add(tagsModel);
		eventsModel = new EventsModel();                   models.add(eventsModel);
		requirementsModel = new RequirementsModel();       models.add(requirementsModel);
		triggersModel = new TriggersModel();               models.add(triggersModel);
		factoriesModel = new FactoriesModel();             models.add(factoriesModel);
		overlaysModel = new OverlaysModel();               models.add(overlaysModel);
		instancesModel = new InstancesModel();             models.add(instancesModel);
		playerInstancesModel = new PlayerInstancesModel(); models.add(playerInstancesModel);
		gameInstancesModel = new GameInstancesModel();     models.add(gameInstancesModel);
		groupInstancesModel = new GroupInstancesModel();   models.add(groupInstancesModel);
		tabsModel = new TabsModel();                       models.add(tabsModel);
		logsModel = new LogsModel();                       models.add(logsModel);
		questsModel = new QuestsModel();                   models.add(questsModel);
		//@formatter:on
//		displayQueueModel    = new DisplayQueueModel();	 	models.add(displayQueueModel   ); // iOS only for now
		models.add(mGamePlayAct.mUsersModel); //		[models addObject:_MODEL_USERS_];
		models.add(mGamePlayAct.mMediaModel); //		[models addObject:_MODEL_MEDIA_];

		n_game_data_to_receive = 0;
		n_maintenance_data_to_receive = 0;
		n_player_data_to_receive = 0;
		n_media_data_to_receive = 0;
		for (ARISModel model : models) {
			n_game_data_to_receive += model.nGameDataToReceive();
			n_maintenance_data_to_receive += model.nMaintenanceDataToReceive();
			n_player_data_to_receive += model.nPlayerDataToReceive();
		}
		n_media_data_to_receive = mGamePlayAct.mMediaModel.numMediaTryingToLoad(); //must be recalculated once game info gotten

		initModelContexts();  // android only
	}

	public void initModelContexts() { // pass on the context for upward visibility in object instantiation tree
		for (ARISModel model : models) {
			model.initContext(mGamePlayAct);
		}
	}

	//to remove models while retaining the game stub for lists and such
	public void endPlay() {
		n_game_data_to_receive = 0;
		n_game_data_received = 0;
		n_maintenance_data_to_receive = 0;
		n_maintenance_data_received = 0;
		n_player_data_to_receive = 0;
		n_player_data_received = 0;

		models = null;

		scenesModel = null;
		groupsModel = null;
		plaquesModel = null;
		itemsModel = null;
		dialogsModel = null;
		webPagesModel = null;
		notesModel = null;
		tagsModel = null;
		eventsModel = null;
		requirementsModel = null;
		triggersModel = null;
		factoriesModel = null;
		overlaysModel = null;
		instancesModel = null;
		playerInstancesModel = null;
		gameInstancesModel = null;
		groupInstancesModel = null;
		tabsModel = null;
		questsModel = null;
		logsModel = null;
//		displayQueueModel    = null; // iOS
	}

	public void requestGameData() {
		n_game_data_received = 0;
		// loop through all models and call requestGameData()
		for (ARISModel model : models) {
			model.requestGameData();
		}
	}

	public void requestMaintenanceData() {
		n_maintenance_data_received = 0; //
		for (ARISModel model : models) {
			model.requestMaintenanceData();
		}
	}


	public void requestPlayerData() {
		n_player_data_received = 0;
		for (ARISModel model : models) {
			model.requestPlayerData();
		}
	}

	public void requestMediaData() {
		n_media_data_received = 0;
		n_media_data_to_receive = mGamePlayAct.mMediaModel.requestMediaData(); //[_MODEL_MEDIA_ requestMediaData];

		if (n_media_data_to_receive == 0) {
			//hack to quickly check if already done
			n_media_data_received--;
			this.mediaPieceReceived();
		}
	}

	public void gamePieceReceived() { // called as listener to todo: GAME_PIECE_AVAILABLE?
		n_game_data_received++;
		mGamePlayAct.mDispatch.game_percent_loaded((float) n_game_data_received / (float) n_game_data_to_receive);
		if (this.allGameDataReceived()) {
			n_game_data_received = n_game_data_to_receive; //should already be exactly this...
			mGamePlayAct.mDispatch.game_data_loaded(); // _ARIS_NOTIF_SEND_(@"DATA_LOADED", nil, nil); // will call requestPlayerData()
		}
	}

	//fixme: watch here and see why we're never passing the condition below.
	public void maintenancePieceReceived() { // called as listener to [GameInstances|GroupInstance|Groups|PlayerInstance|Scenes]Touched() via Dispater.maintenance_piece_available()
		n_maintenance_data_received++;
		mGamePlayAct.mDispatch.maintenance_percent_loaded((float) n_maintenance_data_received / (float) n_maintenance_data_to_receive); //_ARIS_NOTIF_SEND_(@"MAINTENANCE_PERCENT_LOADED", nil,
		// @{@"percent":[NSNumber numberWithFloat:(float)n_maintenance_data_received/(float)n_maintenance_data_to_receive]});
		if (this.allMaintenanceDataReceived()) {
//			downloadedVersion = version; // just used for dev testing. don't leave this in final code.
			n_maintenance_data_received = n_maintenance_data_to_receive; //should already be exactly this...
			mGamePlayAct.mDispatch.maintenance_data_loaded(); //_ARIS_NOTIF_SEND_(@"MAINTENANCE_DATA_LOADED", nil, nil);
		}
	}

	public void playerPieceReceived() {
		n_player_data_received++;
		mGamePlayAct.mDispatch.player_percent_loaded((float) n_player_data_received / (float) n_player_data_to_receive); //_ARIS_NOTIF_SEND_(@"PLAYER_PERCENT_LOADED", nil,
		// @{@"percent":[NSNumber numberWithFloat:(float)n_player_data_received/(float)n_player_data_to_receive]});
		if (this.allPlayerDataReceived()) {
			n_player_data_received = n_player_data_to_receive; //should already be exactly this...
			mGamePlayAct.mDispatch.model_player_data_loaded(); //_ARIS_NOTIF_SEND_(@"PLAYER_DATA_LOADED", nil, nil);
		}
	}

	public void mediaPieceReceived() {
		n_media_data_received++;
		mGamePlayAct.mDispatch.media_percent_loaded((float) n_media_data_received / (float) n_media_data_to_receive); //_ARIS_NOTIF_SEND_(@"MEDIA_PERCENT_LOADED", nil,
		// @{@"percent":[NSNumber numberWithFloat:(float)n_media_data_received/(float)n_media_data_to_receive]});
		if (this.allMediaDataReceived()) {
			n_media_data_received = n_media_data_to_receive; //should already be exactly this...
			mGamePlayAct.mDispatch.model_media_data_loaded(); //_ARIS_NOTIF_SEND_(@"MEDIA_DATA_LOADED", nil, nil);
		}
	}

	public void percentLoadedChanged() {
		float percentReceived = (n_game_data_received + n_player_data_received) / (n_game_data_to_receive + n_player_data_to_receive);
		mGamePlayAct.mDispatch.game_percent_loaded(percentReceived); // _ARIS_NOTIF_SEND_(@"PERCENT_LOADED", null, @{@"percent":percentReceived});
	}

	public void gameBegan() {

		listen_game_piece_available = 0;        // _ARIS_NOTIF_IGNORE_(@"GAME_PIECE_AVAILABLE", self, null);
		listen_player_piece_available = 0;        // _ARIS_NOTIF_IGNORE_(@"PLAYER_PIECE_AVAILABLE", self, null);
		listen_maintenance_piece_available = 0;    // _ARIS_NOTIF_IGNORE_(@"MAINTENANCE_PIECE_AVAILABLE", self, nil);
		listen_media_piece_available = 0;        // _ARIS_NOTIF_IGNORE_(@"MEDIA_PIECE_AVAILABLE", self, nil);
//		todo: build poller!
//		poller = [NSTimer scheduledTimerWithTimeInterval:10.0 target:self selector:@selector(requestPlayerData) userInfo:null repeats:true];
		this.startPollTimer();
	}

	public void gameLeft() {
		if (isPollTimerRunning) { // todo: clear/cancel all outstanding service requests for poll timer
			mGamePlayAct.stopService(pollTimerSvcIntent);
			isPollTimerRunning = false;
		}
	}

	public boolean allGameDataReceived() {
		for (ARISModel model : models)
			if (!model.gameDataReceived()) return false;
		return true;
	}

	public boolean allMaintenanceDataReceived() {
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " MAINTCYCLE  - - - - - - - - allMaintenanceDataReceived()");

		for (ARISModel model : models)
			if (!model.maintenanceDataReceived()) return false;
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " MAINTCYCLE  - - - - - - - - allMaintenanceDataReceived() PASSED PASSED PASSED");
		return true;
	}

	public boolean allPlayerDataReceived() {
		for (ARISModel model : models)
			if (!model.playerDataReceived()) return false;
		return true;
	}

	public boolean allMediaDataReceived() {
		return n_media_data_received >= n_media_data_to_receive; //a bit more fragile than others'...
	}

	public boolean hasLatestDownload() { // todo: follow the version values below and ensure they're getting set correctly; I think they're not.
		return (this.downloadedVersion != 0 && this.version == this.downloadedVersion);
	}

	public long rating() {
		if (comments == null || comments.isEmpty()) return 0;
		long rating = 0;
		for (GameComment comment : comments)
			rating += comment.rating;
		return rating / comments.size();
	}

	private void startPollTimer() {
		// register receiver
		LocalBroadcastManager.getInstance(mGamePlayAct).registerReceiver(mMessageReceiver, new IntentFilter(AppConfig.POLLTIMER_SVC_ACTION));

		if (!isPollTimerRunning) {
			pollTimerSvcIntent = new Intent(mGamePlayAct, PollTimerService.class);
			mGamePlayAct.startService(pollTimerSvcIntent);
			isPollTimerRunning = true;
		}

	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " PollTimer has Cycled  - - - - - - - - DING!");
			handleMessage(intent);
		}
	};

	private void handleMessage(Intent msg) {
		if (!isPollTimerRunning) return; // ignore any calls after timer was killed
		Bundle data = msg.getExtras();
		switch (data.getInt(AppConfig.COMMAND, 0)) {
			case AppConfig.POLLTIMER_CYCLE_PASS:
//				int progress = data.getInt(AppConfig.DATA, 0); // not used.
				this.requestPlayerData();
				break;
			case AppConfig.POLLTIMER_RESULT: // sent when finished cycling stub. not used.
//				String res = data.getString(AppConfig.DATA);
				break;
			default:
				break;
		}
	}

	public void clearModels() {
		n_game_data_received = 0;
		n_player_data_received = 0;

		for (ARISModel model : models) {
			model.clearPlayerData();
		}
		for (ARISModel model : models) {
			model.clearGameData();
		}

//		displayQueueModel.clear();
	}

	public String description() {
		return (String) "Game- Id:" + game_id + "\tName:" + name;
	}

	public void mergeDataFromGame(Game g) {

	}

	public boolean begin_fresh() {
		return begin_fresh == 1;
	}

	public boolean preload_media() {
		return preload_media == 1;
	}

	public boolean listen_game_piece_available() {
		return listen_game_piece_available == 1;
	}

	public boolean listen_maintenance_piece_available() {
		return listen_maintenance_piece_available == 1;
	}

	public boolean listen_media_piece_available() {
		return listen_media_piece_available == 1;
	}

	public boolean listen_player_piece_available() {
		return listen_player_piece_available == 1;
	}
}
