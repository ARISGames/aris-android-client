package edu.uoregon.casls.aris_android.Utilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 10/6/15.
 */
public class Dispatcher {
	// Cannot be instantiated outside of the context of GamePlayActivity

	public GamePlayActivity mGamePlayAct;
	public Game mGame;
	public User mPlayer;

	public Dispatcher(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		//convenience references:
		mGame = mGamePlayAct.mGame;
		mPlayer = mGamePlayAct.mPlayer;
	}

	// replicate the _ARIS_NOTIF_ mechanism from iOS with a rudimentary method calls

//	CONNECTION_LAG",nil,@{@"laggers":laggers});
//	DEFAULTS_CLEAR",nil,nil);
//	DEFAULTS_UPDATED",nil,nil);
//	DEVICE_MOVED", nil, @{@"location":lastKnownLocation});
//	GAME_PLAY_DISPLAYED_INSTANCE",nil,@{@"instance":i});
//	GAME_PLAY_DISPLAYED_TRIGGER",nil,@{@"trigger":t});
//	LowMemoryWarning",nil,nil);
//	MODEL_ANYWHERE_GAMES_AVAILABLE",nil,nil); }
//	MODEL_DIALOG_CHARACTERS_AVAILABLE",nil,nil);
public void model_dialog_characters_available() {
	// no one to field this one. Oh well. Die in space.
}
//	MODEL_DIALOG_OPTIONS_AVAILABLE",nil,nil);
public void model_dialog_options_available() {
	// nope. me neither.
}
//	MODEL_DIALOG_SCRIPTS_AVAILABLE",nil,nil);
public void model_dialog_scripts_available() {
	// no one for this call either.
}
//	MODEL_DIALOGS_AVAILABLE",nil,nil);
public void model_dialogs_available() {
	// no listeners in this version?
}
//	MODEL_DISPLAY_NEW_ENQUEUED", nil, nil);
//	MODEL_DOWNLOADED_GAMES_AVAILABLE",nil,nil); }
//	MODEL_EVENTS_AVAILABLE",nil,nil);
//	MODEL_FACTORIES_AVAILABLE",nil,nil);
//	MODEL_GAME_AVAILABLE",nil,@{@"game":[self gameForId:g.game_id]});
//	MODEL_GAME_BEGAN",nil,nil);
//	MODEL_GAME_CHOSEN",nil,nil);
//	MODEL_GAME_DATA_LOADED", nil, nil);
public void model_game_data_loaded() {
	// in iOS would call LoadingViewController.gameDataLoaded(), which then calls Game.requestPlayerData(); I'll call it directly.
	mGame.requestPlayerData();
}
//	MODEL_GAME_INSTANCES_AVAILABLE",nil,nil);
//	MODEL_GAME_INSTANCES_TOUCHED",nil,nil);
//	MODEL_GAME_LEFT",nil,nil);
//	MODEL_GAME_PERCENT_LOADED", nil, @{@"percent":percentReceived});
//	MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	public void model_game_piece_available() {
		mGame.gamePieceReceived();
	}
//	MODEL_GAME_PLAYER_DATA_LOADED", nil, nil);
//	MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	public void model_game_player_piece_available() {
		mGame.gamePlayerPieceReceived();
	}
//	MODEL_GROUP_INSTANCES_AVAILABLE",nil,nil);
//	MODEL_GROUP_INSTANCES_TOUCHED",nil,nil);
//	MODEL_GROUP_TOUCHED",nil,nil);
//	MODEL_GROUPS_AVAILABLE",nil,nil);
//	MODEL_GROUPS_PLAYER_GROUP_AVAILABLE",nil,nil);
//	MODEL_INSTANCES_AVAILABLE",nil,gameDeltas);
	public void model_instances_available(Map<String, Map<String, Object>> gameDeltas) {
	}
//	MODEL_INSTANCES_GAINED",nil,gameDeltas);
	public void model_instances_gained(Map<String, Map<String, Object>> gameDeltas) {
	}
//	MODEL_INSTANCES_LOST",  nil,gameDeltas);
	public void model_instances_lost(Map<String, Map<String, Object>> gameDeltas) {
	}
//	MODEL_INSTANCES_PLAYER_AVAILABLE",nil,playerDeltas);
	public void model_instances_player_available(Map<String, Map<String, Object>> playerDeltas) {
	}
//	MODEL_INSTANCES_PLAYER_GAINED",nil,playerDeltas);
	public void model_instances_player_gained(Map<String, Map<String, Object>> playerDeltas) {
	}
//	MODEL_INSTANCES_PLAYER_LOST",  nil,playerDeltas);
	public void model_instances_player_lost(Map<String, Map<String, Object>> playerDeltas) {
		// todo: see UI change behavour that this causes in (iOS) GameNotificationViewController.parseLostInstancesIntoNotifications()
	}

//	MODEL_ITEMS_AVAILABLE",nil,nil);
//	MODEL_LOGGED_IN",nil,nil);
//	MODEL_LOGGED_OUT",nil,nil);
//	MODEL_LOGIN_FAILED",nil,nil); }
//	MODEL_LOGS_AVAILABLE",nil,nil);
//	MODEL_MEDIA_AVAILABLE",nil,nil);
//	MODEL_MEDIA_DATA_COMPLETE",nil,nil);
//	MODEL_MEDIA_DATA_LOADED",nil,nil);
//	MODEL_MINE_GAMES_AVAILABLE",nil,nil); }
//	MODEL_NEARBY_GAMES_AVAILABLE",nil,nil); }
//	MODEL_NOTE_COMMENTS_AVAILABLE",nil,nil);
//	MODEL_NOTES_AVAILABLE",nil,nil);
//	MODEL_OBJECT_TAGS_AVAILABLE",nil,nil);
//	MODEL_OVERLAYS_AVAILABLE",nil,nil);
//	MODEL_OVERLAYS_LESS_AVAILABLE",nil,@{@"removed":removedOverlays});
//	MODEL_OVERLAYS_NEW_AVAILABLE",nil,@{@"added":addedOverlays});
//	MODEL_PLAQUES_AVAILABLE",nil,nil);
//	MODEL_PLAYER_INSTANCES_AVAILABLE",nil,nil);
//	MODEL_PLAYER_INSTANCES_TOUCHED",nil,nil);
//	MODEL_PLAYER_PLAYED_GAME_AVAILABLE",nil,notif.userInfo);
//	MODEL_PLAYER_SCRIPT_OPTIONS_AVAILABLE",nil,uInfo);
//	MODEL_PLAYER_TRIGGERS_AVAILABLE",nil,nil);
//	MODEL_POPULAR_GAMES_AVAILABLE",nil,nil); }
//	MODEL_QUESTS_ACTIVE_LESS_AVAILABLE",nil,deltas);
//	MODEL_QUESTS_ACTIVE_NEW_AVAILABLE",nil,deltas);
//	MODEL_QUESTS_AVAILABLE",nil,nil);
//	MODEL_QUESTS_COMPLETE_LESS_AVAILABLE",nil,deltas);
//	MODEL_QUESTS_COMPLETE_NEW_AVAILABLE",nil,deltas);
//	MODEL_RECENT_GAMES_AVAILABLE",nil,nil); }
//	MODEL_REQUIREMENT_AND_PACKAGES_AVAILABLE",nil,nil);
//	MODEL_REQUIREMENT_ATOMS_AVAILABLE",nil,nil);
//	MODEL_REQUIREMENT_ROOT_PACKAGES_AVAILABLE",nil,nil);
public void model_requirement_root_packages_available() {
	// nada.
}
//	MODEL_SCENE_TOUCHED",nil,nil);
//	MODEL_SCENES_AVAILABLE",nil,nil);
//	MODEL_SCENES_PLAYER_SCENE_AVAILABLE",nil,nil);
//	MODEL_SEARCH_GAMES_AVAILABLE",nil,nil); }
//	MODEL_TABS_AVAILABLE",nil,nil);
//	MODEL_TABS_LESS_AVAILABLE",nil,deltas);
//	MODEL_TABS_NEW_AVAILABLE",nil,deltas);
//	MODEL_TAGS_AVAILABLE",nil,nil);
//	MODEL_TRIGGERS_AVAILABLE",nil,nil);
//	MODEL_TRIGGERS_INVALIDATED",nil,@{@"invalidated_triggers":invalidatedTriggers});
//	MODEL_TRIGGERS_LESS_AVAILABLE",nil,@{@"removed":removedTriggers});
//	MODEL_TRIGGERS_NEW_AVAILABLE",nil,@{@"added":addedTriggers});
//	MODEL_USERS_AVAILABLE",nil,nil);
//	MODEL_WEB_PAGES_AVAILABLE",nil,nil);
//	PusherGameEventReceived",event,nil);
//	PusherGroupEventReceived",event,nil);
//	PusherPlayerEventReceived",event,nil);
//	PusherWebPageEventReceived",event,nil);
//	SERVICES_ANYWHERE_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_DIALOG_CHARACTERS_RECEIVED", nil, @{@"dialogCharacters":dialogCharacters});
public void services_dialog_characters_received(List<DialogCharacter> dialogCharacters) {
	mGame.dialogsModel.dialogCharactersReceived(dialogCharacters);
}
//	SERVICES_DIALOG_OPTIONS_RECEIVED", nil, @{@"dialogOptions":dialogOptions});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_character":dialogCharacter});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_option":dialogOption});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_script":dialogScript});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog":dialog});
//	SERVICES_DIALOG_SCRIPTS_RECEIVED", nil, @{@"dialogScripts":dialogScripts});
public void services_dialog_scipts_received(List<DialogScript> dialogScripts) {
	mGame.dialogsModel.dialogScriptsReceived(dialogScripts);
}
//	SERVICES_DIALOGS_RECEIVED", nil, @{@"dialogs":dialogs});
public void services_dialog_received(List<Dialog> dialogs) {
	mGame.dialogsModel.dialogsReceived(dialogs);
}
//	SERVICES_DOWNLOADED_GAMES_RECEIVED", nil, @{@"games":d_games});
//	SERVICES_EVENT_RECEIVED", nil, @{@"event":event});
//	SERVICES_EVENTS_RECEIVED", nil, @{@"events":events});
//	SERVICES_FACTORIES_RECEIVED", nil, @{@"factories":factories});
//	SERVICES_FACTORY_RECEIVED", nil, @{@"factory":factory});
//	SERVICES_GAME_FETCH_FAILED", nil, nil); }
//	SERVICES_GAME_INSTANCES_TOUCHED", nil, nil);
//	SERVICES_GAME_RECEIVED", nil, @{@"game":[[Game alloc] initWithDictionary:(NSDictionary *)result.resultData]});
//	SERVICES_GROUP_INSTANCES_TOUCHED", nil, nil);
//	SERVICES_GROUP_RECEIVED", nil, @{@"group":group});
//	SERVICES_GROUP_TOUCHED", nil, nil);
//	SERVICES_GROUPS_RECEIVED", nil, @{@"groups":groups});
//	SERVICES_INSTANCE_RECEIVED", nil, @{@"instance":instance});
//	SERVICES_INSTANCES_RECEIVED", nil, @{@"instances":instances});
//	SERVICES_ITEM_RECEIVED", nil, @{@"item":item});
//	SERVICES_ITEMS_RECEIVED", nil, @{@"items":items});
//	SERVICES_LOGIN_FAILED",nil,nil); return; }
//	SERVICES_LOGIN_RECEIVED",nil,@{@"user":user});
//	SERVICES_MEDIA_RECEIVED", nil, @{@"media":mediaDict}); // fakes an entire list and does same as fetching all media
//	SERVICES_MEDIAS_RECEIVED", nil, @{@"medias":mediaDicts});
//	SERVICES_MINE_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_NEARBY_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_NOTE_COMMENT_RECEIVED", nil, @{@"note_comment":noteComment});
//	SERVICES_NOTE_COMMENTS_RECEIVED", nil, @{@"note_comments":noteComments});
//	SERVICES_NOTE_RECEIVED", nil, @{@"note":note});
//	SERVICES_NOTES_RECEIVED", nil, @{@"notes":notes});
//	SERVICES_OBJECT_TAGS_RECEIVED", nil, @{@"object_tags":@[newObjectTag]});
//	SERVICES_OBJECT_TAGS_RECEIVED", nil, @{@"object_tags":objectTags});
//	SERVICES_OVERLAY_RECEIVED", nil, @{@"overlay":overlay});
//	SERVICES_OVERLAYS_RECEIVED", nil, @{@"overlays":overlays});
//	SERVICES_PLAQUE_RECEIVED", nil, @{@"plaque":plaque});
//	SERVICES_PLAQUES_RECEIVED", nil, @{@"plaques":plaques});
//	SERVICES_PLAYER_GROUP_RECEIVED",nil,@{@"group":playerGroup}); //just return current
//	SERVICES_PLAYER_INSTANCES_RECEIVED",nil,@{@"instances":pinsts});
public void services_player_instances_rceived(Collection<Instance> insts) {
}
//	SERVICES_PLAYER_INSTANCES_TOUCHED", nil, nil);
public void services_player_instances_touched() {
	mGame.playerInstancesModel.playerInstancesTouched();
}



//	SERVICES_PLAYER_LOGS_RECEIVED", nil, @{@"logs":logs});
//	SERVICES_PLAYER_OVERLAYS_RECEIVED", nil, @{@"overlays":overlays});
//	SERVICES_PLAYER_OVERLAYS_RECEIVED",nil,@{@"triggers":ptrigs});
//	SERVICES_PLAYER_PLAYED_GAME_RECEIVED", nil, (NSDictionary *)result.resultData);
//	SERVICES_PLAYER_QUESTS_RECEIVED", nil, quests);
//	SERVICES_PLAYER_SCENE_RECEIVED", nil, @{@"scene":s});
//	SERVICES_PLAYER_SCENE_RECEIVED",nil,@{@"scene":playerScene}); //just return current
//	SERVICES_PLAYER_SCRIPT_OPTIONS_RECEIVED", nil, uInfo);
//	SERVICES_PLAYER_TABS_RECEIVED", nil, @{@"tabs":tabs});
//	SERVICES_PLAYER_TABS_RECEIVED",nil,@{@"tabs":ptabs});
//	SERVICES_PLAYER_TRIGGERS_RECEIVED", nil, @{@"triggers":triggers});
//	SERVICES_PLAYER_TRIGGERS_RECEIVED",nil,@{@"triggers":ptrigs});
//	SERVICES_POPULAR_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_QUEST_RECEIVED", nil, @{@"quest":quest});
//	SERVICES_QUESTS_RECEIVED", nil, @{@"quests":quests});
//	SERVICES_RECENT_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_REQUIREMENT_AND_PACKAGES_RECEIVED", nil, @{@"requirement_and_packages":raps});
//	SERVICES_REQUIREMENT_ATOMS_RECEIVED", nil, @{@"requirement_atoms":as});
//	SERVICES_REQUIREMENT_ROOT_PACKAGES_RECEIVED", nil, @{@"requirement_root_packages":rrps});
//	SERVICES_SCENE_RECEIVED", nil, @{@"scene":scene});
//	SERVICES_SCENE_TOUCHED", nil, nil);
//	SERVICES_SCENES_RECEIVED", nil, @{@"scenes":scenes});
//	SERVICES_SEARCH_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_TAB_RECEIVED", nil, @{@"tab":tab});
//	SERVICES_TABS_RECEIVED", nil, @{@"tabs":tabs});
//	SERVICES_TAG_RECEIVED", nil, @{@"tag":tag});
//	SERVICES_TAGS_RECEIVED", nil, @{@"tags":tags});
//	SERVICES_TRIGGER_RECEIVED", nil, @{@"trigger":trigger});
//	SERVICES_TRIGGERS_RECEIVED", nil, @{@"triggers":triggers});
//	SERVICES_UPDATE_USER_FAILED",nil,nil); return; }
//	SERVICES_UPDATE_USER_RECEIVED",nil,@{@"user":user});
//	SERVICES_USER_RECEIVED", nil, @{@"user":user});
//	SERVICES_USERS_RECEIVED", nil, @{@"users":users});
//	SERVICES_WEB_PAGE_RECEIVED", nil, @{@"web_page":webPage});
//	SERVICES_WEB_PAGES_RECEIVED", nil, @{@"webPages":webPages});
//	USER_MOVED",nil,nil);
//	WIFI_CONNECTED",self,nil); break;

}
