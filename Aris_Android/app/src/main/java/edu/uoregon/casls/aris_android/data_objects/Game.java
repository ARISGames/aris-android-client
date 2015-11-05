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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.PollTimerService;
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

/*
  Created by smorison on 7/28/15.
 */
public class Game {

	public long n_game_data_to_receive = 0;
	public long n_game_data_received = 0;
	public long n_maintenance_data_to_receive = 0;
	public long n_maintenance_data_received = 0;
	public long n_player_data_to_receive = 0;
	public long n_player_data_received = 0;
	public long n_media_data_to_receive = 0;
	public long n_media_data_received = 0;

	public boolean listen_player_piece_available = true;
	public boolean listen_game_piece_available = true;
	public boolean listen_maintenance_piece_available = true;
	public boolean listen_media_piece_available = true;
//	public NSTimer *poller; todo: android equivalent
	// todo: this will not serialize (crashes gson.toJson()) so I need to locate it in the gameplay activity itself.

	private static final String HTTP_GET_FULL_GAME_REQ_API = "v2.games.getFullGame/";
	public long game_id;
	public String name = "";
	public String desc = "";
	public boolean published;
	public String type = "";
	public Location location = new Location("0"); // from iOS; not used?
	public long player_count;

	public Media icon_media;
	public long icon_media_id;
	public Media media;
	public long media_id;

	public long intro_scene_id;

	public List<User> authors = new ArrayList<>();
	public List<GameComment> comments = new ArrayList<GameComment>();

	public String map_type = "";
	public String map_focus = "";
	public Location map_location = new Location("0");
	public double map_zoom_level;
	public boolean map_show_player;
	public boolean map_show_players;
	public boolean map_offsite_mode;

	public boolean notebook_allow_comments;
	public boolean notebook_allow_likes;
	public boolean notebook_allow_player_tags;

	public long inventory_weight_cap;
	public String network_level = "";
	public boolean allow_download;
	public boolean preload_media;
	public long version;

	public List<ARISModel> models = new ArrayList<>(); // List of all the models below for iteration convenience

	// Game subcomponent classes
	public ScenesModel 			scenesModel;		// Game Piece
	public GroupsModel 			groupsModel;		// Game Piece
	public PlaquesModel 		plaquesModel;		// Game Piece
	public ItemsModel 			itemsModel;		// Game Piece
	public DialogsModel 		dialogsModel;		// Game Piece
	public WebPagesModel 		webPagesModel;
	public NotesModel 			notesModel;		// Game Piece
	public TagsModel 			tagsModel;
	public EventsModel 			eventsModel;			// Game Piece
	public RequirementsModel 	requirementsModel;			// Game Piece
	public TriggersModel 		triggersModel;
	public FactoriesModel 		factoriesModel;		// Game Piece
	public OverlaysModel 		overlaysModel;		// Game Piece
	public InstancesModel 		instancesModel;		// Game Piece
	public PlayerInstancesModel playerInstancesModel;		// Game Piece todo: is this where gameUsers go? Players == Users??
	public GameInstancesModel 	gameInstancesModel;
	public GroupInstancesModel 	groupInstancesModel;
	public TabsModel 			tabsModel;
	public LogsModel 			logsModel;
	public QuestsModel			questsModel;		// Game Piece
//	public DisplayQueueModel 	displayQueueModel; // iOS only for now
	// medias (in GamePlayAct 		// Game Piece

	//local stuff
	long downloadedVersion = 0;
	public boolean know_if_begin_fresh = false;
	public boolean begin_fresh = false;

//	PollTimer vars
	public Boolean isPollTImerRunning = false;
	private Intent pollTimerSvcIntent = null;


	// FYI transient indicates "do not serialize"; gson will die a recursive death if it did.
	public transient GamePlayActivity mGamePlayAct; // For reference to GamePlayActivity; do not instantiate (new) object or circular references will ensue.
	// Empty Constructor
	public Game() {
		this.initialize();
	}

	// Basic Constructor with json game block
	public Game(JSONObject jsonGame) throws JSONException {
		this.initialize();
		initWithJson(jsonGame);
	}

	private void initialize() {
		n_game_data_received = 0;
		n_player_data_received = 0;

		authors.clear(); // redundant at this point, but left in to keep in sync with iOS code
		comments.clear();

		downloadedVersion = 0;
		know_if_begin_fresh = false;
		begin_fresh = false;

	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
	}

	private void initWithJson(JSONObject jsonGame) throws JSONException {
		if (jsonGame.has("game_id") && !jsonGame.getString("game_id").equals("null"))
			game_id = Long.parseLong(jsonGame.getString("game_id"));
		if (jsonGame.has("name"))
			name = jsonGame.getString("name");
		if (jsonGame.has("allow_download"))
			allow_download = Boolean.parseBoolean(jsonGame.getString("allow_download"));
		if (jsonGame.has("description"))
			desc = jsonGame.getString("description");
//		if (jsonGame.has("tick_script"))
//			tick_script = jsonGame.getString("tick_script");
//		if (jsonGame.has("tick_delay") && !jsonGame.getString("tick_delay").equals("null"))
//			tick_delay = Long.parseLong(jsonGame.getString("tick_delay"));
		if (jsonGame.has("intro_scene_id") && !jsonGame.getString("intro_scene_id").equals("null"))
			icon_media_id = Long.parseLong(jsonGame.getString("intro_scene_id"));
		if (jsonGame.has("icon_media_id") && !jsonGame.getString("icon_media_id").equals("null"))
			icon_media_id = Long.parseLong(jsonGame.getString("icon_media_id"));
		if (jsonGame.has("media_id") && !jsonGame.getString("media_id").equals("null"))
			media_id = Long.parseLong(jsonGame.getString("media_id"));
		if (jsonGame.has("latitude") && !jsonGame.getString("latitude").equals("null"))
			location.setLatitude(Double.parseDouble(jsonGame.getString("latitude")));
		if (jsonGame.has("longitude") && !jsonGame.getString("longitude").equals("null"))
			location.setLongitude(Double.parseDouble(jsonGame.getString("longitude")));
		if (jsonGame.has("map_focus"))
			map_type = jsonGame.getString("map_focus");
		if (jsonGame.has("map_type"))
			map_type = jsonGame.getString("map_type");
		if (jsonGame.has("map_latitude") && !jsonGame.getString("map_latitude").equals("null"))
			map_location.setLatitude(Double.parseDouble(jsonGame.getString("map_latitude")));
		if (jsonGame.has("map_longitude") && !jsonGame.getString("map_longitude").equals("null"))
			map_location.setLongitude(Double.parseDouble(jsonGame.getString("map_longitude")));
		if (jsonGame.has("map_zoom_level") && !jsonGame.getString("map_zoom_level").equals("null"))
			map_zoom_level = Double.parseDouble(jsonGame.getString("map_zoom_level"));
		if (jsonGame.has("map_show_player") && !jsonGame.getString("map_show_player").equals("null"))
			map_show_player = Boolean.parseBoolean(jsonGame.getString("map_show_player"));
		if (jsonGame.has("map_show_players") && !jsonGame.getString("map_show_players").equals("null"))
			map_show_players = Boolean.parseBoolean(jsonGame.getString("map_show_players"));
		if (jsonGame.has("map_offsite_mode") && !jsonGame.getString("map_offsite_mode").equals("null"))
			map_offsite_mode = Boolean.parseBoolean(jsonGame.getString("map_offsite_mode"));
		if (jsonGame.has("network_level") && !jsonGame.getString("network_level").equals("null"))
			notebook_allow_comments = Boolean.parseBoolean(jsonGame.getString("network_level"));
		if (jsonGame.has("notebook_allow_comments") && !jsonGame.getString("notebook_allow_comments").equals("null"))
			notebook_allow_comments = Boolean.parseBoolean(jsonGame.getString("notebook_allow_comments"));
		if (jsonGame.has("notebook_allow_likes") && !jsonGame.getString("notebook_allow_likes").equals("null"))
			notebook_allow_likes = Boolean.parseBoolean(jsonGame.getString("notebook_allow_likes"));
		if (jsonGame.has("notebook_allow_player_tags") && !jsonGame.getString("notebook_allow_player_tags").equals("null"))
			notebook_allow_player_tags = Boolean.parseBoolean(jsonGame.getString("notebook_allow_player_tags"));
		if (jsonGame.has("published") && !jsonGame.getString("published").equals("null"))
			published = Boolean.parseBoolean(jsonGame.getString("published"));
		if (jsonGame.has("preload_media") && !jsonGame.getString("preload_media").equals("null"))
			published = Boolean.parseBoolean(jsonGame.getString("preload_media"));
		if (jsonGame.has("type"))
			type = jsonGame.getString("type");
		if (jsonGame.has("intro_scene_id") && !jsonGame.getString("intro_scene_id").equals("null"))
			intro_scene_id = Long.parseLong(jsonGame.getString("intro_scene_id"));
		if (jsonGame.has("player_count") && !jsonGame.getString("player_count").equals("null"))
			player_count = Long.parseLong(jsonGame.getString("player_count"));
		if (jsonGame.has("inventory_weight_cap") && !jsonGame.getString("inventory_weight_cap").equals("null"))
			player_count = Long.parseLong(jsonGame.getString("inventory_weight_cap"));
		if (jsonGame.has("version") && !jsonGame.getString("version").equals("null"))
			player_count = Long.parseLong(jsonGame.getString("version"));

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

		// defer this to full game init
//		if (jsonGame.has("authors")) {
//			JSONArray jsonAuthorsList = jsonGame.getJSONArray("authors");
//			// in iOS they parse an authors array and include a member array of Author objs
//			// todo: add authors parse code
//		}

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
			for (int i=0; i < jsonAuthorsList.length(); i++) {
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

		}
		if (jsonGameData.has("icon_media")) {
			// get icon_media block
			JSONObject jsonMedia = jsonGameData.getJSONObject("icon_media");
			icon_media = gson.fromJson(jsonMedia.toString(), Media.class);
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
		listen_player_piece_available = true; 		//_ARIS_NOTIF_LISTEN_(@"PLAYER_PIECE_AVAILABLE",self,@selector(gamePlayerPieceReceived),null);
		listen_maintenance_piece_available = true; 	// _ARIS_NOTIF_LISTEN_(@"MAINTENANCE_PIECE_AVAILABLE",self,@selector(maintenancePieceReceived),nil);
		listen_game_piece_available = true; 		// _ARIS_NOTIF_LISTEN_(@"GAME_PIECE_AVAILABLE",self,@selector(gamePieceReceived),null);
		listen_media_piece_available = true; 		// _ARIS_NOTIF_LISTEN_(@"MEDIA_PIECE_AVAILABLE",self,@selector(mediaPieceReceived),nil);

		scenesModel          = new ScenesModel(); 			models.add(scenesModel         );
		plaquesModel         = new PlaquesModel(); 			models.add(plaquesModel        );
		itemsModel           = new ItemsModel(); 			models.add(itemsModel          );
		dialogsModel         = new DialogsModel(); 			models.add(dialogsModel        );
		webPagesModel        = new WebPagesModel(); 		models.add(webPagesModel       );
		notesModel           = new NotesModel(); 			models.add(notesModel          );
		tagsModel            = new TagsModel(); 			models.add(tagsModel           );
		eventsModel          = new EventsModel(); 			models.add(eventsModel         );
		requirementsModel    = new RequirementsModel(); 	models.add(requirementsModel   );
		triggersModel        = new TriggersModel(); 		models.add(triggersModel       );
		factoriesModel       = new FactoriesModel(); 		models.add(factoriesModel      );
		overlaysModel        = new OverlaysModel(); 		models.add(overlaysModel       );
		instancesModel       = new InstancesModel(); 		models.add(instancesModel      );
		playerInstancesModel = new PlayerInstancesModel();	models.add(playerInstancesModel);
		gameInstancesModel   = new GameInstancesModel();	models.add(gameInstancesModel  );
		groupInstancesModel  = new GroupInstancesModel();	models.add(groupInstancesModel );
		tabsModel            = new TabsModel(); 			models.add(tabsModel           );
		logsModel            = new LogsModel(); 			models.add(logsModel           );
		questsModel          = new QuestsModel(); 			models.add(questsModel         );
//		displayQueueModel    = new DisplayQueueModel();	 	models.add(displayQueueModel   ); // iOS only for now
		// todo: user Model and media model added??
//		[models addObject:_MODEL_USERS_];
//		[models addObject:_MODEL_MEDIA_];

		n_game_data_to_receive = 0;
		n_maintenance_data_to_receive = 0;
		n_player_data_to_receive = 0;
		n_media_data_to_receive = 0;
		for (ARISModel model : models) {
			n_game_data_to_receive   += model.nGameDataToReceive();
			n_maintenance_data_to_receive += model.nMaintenanceDataToReceive();
			n_player_data_to_receive += model.nPlayerDataToReceive();
		}
		// todo: waiting for previous todo to solve this
//		n_media_data_to_receive = mediaModel.numMediaTryingToLoad(); //must be recalculated once game info gotten

		initModelContexts();
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
		n_player_data_to_receive = 0;
		n_player_data_received = 0;

		models = null;

		scenesModel          = null;
		groupsModel          = null;
		plaquesModel         = null;
		itemsModel           = null;
		dialogsModel         = null;
		webPagesModel        = null;
		notesModel           = null;
		tagsModel            = null;
		eventsModel          = null;
		requirementsModel    = null;
		triggersModel        = null;
		factoriesModel       = null;
		overlaysModel        = null;
		instancesModel       = null;
		playerInstancesModel = null;
		gameInstancesModel   = null;
		groupInstancesModel  = null;
		tabsModel            = null;
		questsModel          = null;
		logsModel            = null;
//		displayQueueModel    = null; // iOS
	}

	public void requestGameData() {
		n_game_data_received = 0;
		// loop through all models and call requestGameData()
		for (ARISModel model : models) {
			model.requestGameData();
		}

// // original game data loading sequence
//		scenesModel.requestScenes();
//		scenesModel.touchPlayerScene(); //  touch is originating from somewhere else; find it and make it work.
//		plaquesModel.requestPlaques();
//		itemsModel.requestItems();
//		playerInstancesModel.touchPlayerInstances();
//		dialogsModel.requestDialogs(); //makes 4 "game data received" notifs (dialogs, characters, scripts, options)
//		webPagesModel.requestWebPages();
//		notesModel.requestNotes();
//		notesModel.requestNoteComments();
//		tagsModel.requestTags();
//		eventsModel.requestEvents();
//		questsModel.requestQuests();
//		triggersModel.requestTriggers();
//		factoriesModel.requestFactories();
//		overlaysModel.requestOverlays();
//		instancesModel.requestInstances();
//		tabsModel.requestTabs();

//		//the requests not 'owned' by Game. Also, not 100% necessary
//		//(has ability to load on an individual basis)
//		_MODEL_MEDIA_ requestMedia();
//		_MODEL_USERS_ requestUsers();
	}

	/**
	 *
	 */
	public void requestPlayerData() {
		n_player_data_received = 0;
		for (ARISModel model : models) {
			model.requestPlayerData();
		}

//		scenesModel.requestPlayerScene();
//		instancesModel.requestPlayerInstances();
//		triggersModel.requestPlayerTriggers();
//		overlaysModel.requestPlayerOverlays();
//		questsModel.requestPlayerQuests();
//		tabsModel.requestPlayerTabs();
//		logsModel.requestPlayerLogs();
	}

	public void requestMaintenanceData() {
		n_player_data_received = 0;
		for (ARISModel model : models) {
			model.requestPlayerData();
		}
	}

	public void gamePieceReceived() {
		n_game_data_received++;
		if (this.allGameDataReceived()) {
			n_game_data_received = n_game_data_to_receive; //should already be exactly this...
			mGamePlayAct.mDispatch.model_game_data_loaded(); // _ARIS_NOTIF_SEND_(@"DATA_LOADED", nil, nil); // will call requestPlayerData()
		}
		percentLoadedChanged();
	}

	public void gamePlayerPieceReceived() {
		n_player_data_received++;
		if (n_player_data_received >= n_player_data_to_receive) {
			mGamePlayAct.mDispatch.model_game_player_data_loaded(); // _ARIS_NOTIF_SEND_(@"PLAYER_DATA_LOADED", null, null); // broadcast to any listeners that game data is ready
		}
		percentLoadedChanged();
	}

	public boolean allGameDataReceived() {

		for (int i = 0; i < models.size(); i++) {
//		for (ARISModel model : models) {    // iterate through all models
			ARISModel model = models.get(i);
			boolean gdr = model.gameDataReceived();
			if (!gdr) { // stop if one reports it's not received all its data.
				return false;
			}
		}
		return true;
	}

	public boolean  hasLatestDownload() {
		return (this.downloadedVersion != 0 && this.version == this.downloadedVersion);
	}

	public void percentLoadedChanged() {
		float percentReceived = (n_game_data_received + n_player_data_received)/(n_game_data_to_receive + n_player_data_to_receive);
		mGamePlayAct.mDispatch.model_game_percent_loaded(percentReceived); // _ARIS_NOTIF_SEND_(@"PERCENT_LOADED", null, @{@"percent":percentReceived});
	}

	public void gameBegan() { // todo: bring up to date with iOS

		listen_game_piece_available = false; 		// _ARIS_NOTIF_IGNORE_(@"GAME_PIECE_AVAILABLE", self, null);
		listen_player_piece_available = false; 		// _ARIS_NOTIF_IGNORE_(@"PLAYER_PIECE_AVAILABLE", self, null);
		listen_maintenance_piece_available = false;	// _ARIS_NOTIF_IGNORE_(@"MAINTENANCE_PIECE_AVAILABLE", self, nil);
		listen_media_piece_available = false; 		// _ARIS_NOTIF_IGNORE_(@"MEDIA_PIECE_AVAILABLE", self, nil);
//		todo: build poller!
//		poller = [NSTimer scheduledTimerWithTimeInterval:10.0 target:self selector:@selector(requestPlayerData) userInfo:null repeats:YES];
		this.startPollTimer();
	}

	private void startPollTimer() {
		// register receiver
		LocalBroadcastManager.getInstance(mGamePlayAct).registerReceiver(mMessageReceiver, new IntentFilter(AppConfig.POLLTIMER_SVC_ACTION));

		if (!isPollTImerRunning) {
			pollTimerSvcIntent = new Intent(mGamePlayAct, PollTimerService.class);
			mGamePlayAct.startService(pollTimerSvcIntent);
			isPollTImerRunning = true;
		}

	}

	public void gameLeft() {
//		poller invalidate();
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " PollTimer has Cycled  - - - - - - - - DING!");
			handleMessage(intent);
		}
	};

	private void handleMessage(Intent msg)
	{
		Bundle data = msg.getExtras();
		switch (data.getInt(AppConfig.COMMAND, 0))
		{
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


//	private Runnable runnable = new Runnable() {
//		@Override
//		public void run() {
//      /* do what you need to do */
//			foobar();
//			]\
//      /* and here comes the "trick" */
//			poller.postDelayed(this, 100);
//		}
//	};


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

	public long rating() {
		if(comments.isEmpty()) return 0;
		long rating = 0;
		for(GameComment comment : comments)
			rating += comment.rating;
		return rating/comments.size();
	}

	public String description()
	{
		return (String) "Game- Id:" + game_id + "\tName:" + name;
	}

}

/*
Example of getFullGame reult JSON:
http://arisgames.org/server/json.php/v2.games.getFullGame/	(1.126998)
2015-08-13 15:57:52.920 ARIS[717:244847] Fin async data:
{"data":{
"game_id":"91",
"name":"Dinner Search",
"description":"Help Mr. and Mrs. Hill get ready for... Mr. Hill.",
"icon_media_id":"2891",
"media_id":"2891",
"map_type":"STREET",
"map_latitude":"0",
"map_longitude":"0",
"map_zoom_level":"0",
"map_show_player":"1",
"map_show_players":"1",
"map_offsite_mode":"0",
"notebook_allow_comments":"1",
"notebook_allow_likes":"1",
"notebook_trigger_scene_id":"0",
"notebook_trigger_requirement_root_package_id":"0",
"notebook_trigger_title":"",
"notebook_trigger_icon_media_id":"0",
"notebook_trigger_distance":"0",
"notebook_trigger_infinite_distance":"0",
"notebook_trigger_wiggle":"0",
"notebook_trigger_show_title":"0",
"notebook_trigger_hidden":"0",
"notebook_trigger_on_enter":"0",
"inventory_weight_cap":"0",
"is_siftr":"0",
"siftr_url":null,
"published":"1",
"type":"QR",
"intro_scene_id":"1",
"moderated":"0",
"authors":[{"user_id":"34","user_name":"erica.white","display_name":"","media_id":"0"}],
"media":{
	"media_id":"2891",
	"game_id":"91",
	"name":"Dinner",
	"file_name":"aris18ae30099ba6d75e05d2c445f4eecafd.jpg",
	"url":"http:\/\/arisgames.org\/server\/gamedatav2\/91\/aris18ae30099ba6d75e05d2c445f4eecafd.jpg",
	"thumb_url":"http:\/\/arisgames.org\/server\/gamedatav2\/91\/aris18ae30099ba6d75e05d2c445f4eecafd_128.jpg"},
"icon_media":{
	"media_id":"2891",
	"game_id":"91",
	"name":"Dinner",
	"file_name":"aris18ae30099ba6d75e05d2c445f4eecafd.jpg",
	"url":"http:\/\/arisgames.org\/server\/gamedatav2\/91\/aris18ae30099ba6d75e05d2c445f4eecafd.jpg",
	"thumb_url":"http:\/\/arisgames.org\/server\/gamedatav2\/91\/aris18ae30099ba6d75e05d2c445f4eecafd_128.jpg"
	}
},"returnCode":0,"returnCodeDescription":null}

 */