package edu.uoregon.casls.aris_android.Utilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Event;
import edu.uoregon.casls.aris_android.data_objects.Factory;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Group;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.ObjectTag;
import edu.uoregon.casls.aris_android.data_objects.Overlay;
import edu.uoregon.casls.aris_android.data_objects.Quest;
import edu.uoregon.casls.aris_android.data_objects.Scene;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.Tag;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.data_objects.User;
import edu.uoregon.casls.aris_android.data_objects.WebPage;
import edu.uoregon.casls.aris_android.models.MediaModel;

/**
 * Created by smorison on 10/6/15.
 */
public class Dispatcher {
	// Cannot be instantiated outside of the context of GamePlayActivity

	public transient GamePlayActivity mGamePlayAct;
//	public transient Game mGame;
//	public User mPlayer;
//	public Object mMediaModel;

	public Dispatcher(GamePlayActivity gamePlayActivity) {
		this.initContext(gamePlayActivity);
	}

	public Dispatcher() {

	}

	public void initContext(GamePlayActivity gamePlayActivity) {

		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		//convenience references:
//		mGame = mGamePlayAct.mGame;
//		mPlayer = mGamePlayAct.mPlayer;
//		mMediaModel = mGamePlayAct.mMediaModel; cyclic ref
	}

	// replicate the _ARIS_NOTIF_ mechanism from iOS with a rudimentary method calls

	//	CONNECTION_LAG",nil,@{@"laggers":laggers});
//	DEFAULTS_CLEAR",nil,nil);
//	DEFAULTS_UPDATED",nil,nil);
//	DEVICE_MOVED", nil, @{@"location":lastKnownLocation}); // will be handled in GamePlayActivity.
//	GAME_PLAY_DISPLAYED_INSTANCE",nil,@{@"instance":i}); // sent by GamePLayViewController; no listeners - sem
//	GAME_PLAY_DISPLAYED_TRIGGER",nil,@{@"trigger":t}); // sent by GamePLayViewController; no listeners - sem
//	LowMemoryWarning",nil,nil);
//	MODEL_ANYWHERE_GAMES_AVAILABLE",nil,nil); } // sent by GamesModel; listened for by GamePickerAnywhereViewController(); I think we're doing this already in Android GamesListActivity
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

	//	MODEL_DISPLAY_NEW_ENQUEUED", nil, nil); // Will be handled in GamePlayActivity -sem
//	MODEL_DOWNLOADED_GAMES_AVAILABLE",nil,nil); } // Handled in GamesListActivity - sem
//	MODEL_EVENTS_AVAILABLE",nil,nil);
	public void model_events_available() {
		// no listners
	}

	//	MODEL_FACTORIES_AVAILABLE",nil,nil);
	public void model_factories_available() {
		// no listeners
	}

	//	MODEL_GAME_AVAILABLE",nil,@{@"game":[self gameForId:g.game_id]});// Will be handled in GamePlayActivity -sem
//	MODEL_GAME_BEGAN",nil,nil); // Will be handled in GamePlayActivity -sem
//	MODEL_GAME_CHOSEN",nil,nil); // Will be handled in GamePlayActivity -sem

//	MODEL_GAME_DATA_LOADED", nil, nil);
	public void model_game_data_loaded() {
		// in iOS would call LoadingViewController.gameDataLoaded(), which then calls Game.requestPlayerData(); I'll call it directly.
		mGamePlayAct.mGame.requestPlayerData();
	}

	//	MODEL_GAME_INSTANCES_AVAILABLE",nil,nil);
	public void model_game_instances_available() {
		// not listened for;
	}

	//	MODEL_GAME_INSTANCES_TOUCHED",nil,nil);
	public void model_game_instance_touched() {
		// no action assigned to this yet.
	}

	//	MODEL_GAME_LEFT",nil,nil); // Will be handled in GamePlayActivity -sem
//	MODEL_GAME_PERCENT_LOADED", nil, @{@"percent":percentReceived});
	public void model_game_percent_loaded(float percentReceived) {
		// todo: LoadingViewController.percentLoaded();
	}

	//	MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	public void model_game_piece_available() {
		if (mGamePlayAct.mGame.listen_model_game_piece_available) mGamePlayAct.mGame.gamePieceReceived();
	}

	//	MODEL_GAME_PLAYER_DATA_LOADED", nil, nil);
	public void model_game_player_data_loaded() {
		// todo: LoadingViewController.playerDataLoaded();
	}

	//	MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	public void model_game_player_piece_available() {
		if (mGamePlayAct.mGame.listen_model_game_player_piece_available) mGamePlayAct.mGame.gamePlayerPieceReceived();
	}

	//	MODEL_GROUP_INSTANCES_AVAILABLE",nil,nil);
	public void model_group_instances_available() {
		// no listeners
	}

	//	MODEL_GROUP_INSTANCES_TOUCHED",nil,nil);
	public void model_group_instances_touched() {
		// no listeners currently
	}

	//	MODEL_GROUP_TOUCHED",nil,nil);
	public void model_group_touched() {
		// no listeners
	}

	//	MODEL_GROUPS_AVAILABLE",nil,nil);
	public void model_groups_available() {
		// no listeners
	}

	//	MODEL_GROUPS_PLAYER_GROUP_AVAILABLE",nil,nil);
	public void model_groups_player_group_available() {
		// no listeners
	}

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
	public void model_items_available() {
		// todo: listeners?
	}

	//	MODEL_LOGGED_IN",nil,nil); // Handled in LoginActivity -sem
//	MODEL_LOGGED_OUT",nil,nil); // Handled in LoginActivity -sem
//	MODEL_LOGIN_FAILED",nil,nil); } // Handled in LoginActivity -sem
//	MODEL_LOGS_AVAILABLE",nil,nil);
	public void model_logs_available() {
//	broadcast only; no receivers
	}

	//	MODEL_MEDIA_AVAILABLE",nil,nil);
	public void model_media_available() {
		// todo: iOS call = ARISMediaLoader.retryLoadingAllMedia()
	}

	//	MODEL_MEDIA_DATA_COMPLETE",nil,nil);
	public void model_media_data_complete() {
		// todo: iOS Call = LoadingViewController.mediaDataComplete() (calls beginGame!)
	}

	//	MODEL_MEDIA_DATA_LOADED",nil,nil);
	public void model_media_data_loaded() {
		// todo: iOS Call = LoadingViewController.mediaDataLoaded()
	}

	//	MODEL_MINE_GAMES_AVAILABLE",nil,nil); } // Handled in GamesListActivity - sem
//	MODEL_NEARBY_GAMES_AVAILABLE",nil,nil); } // Handled in GamesListActivity - sem
//	MODEL_NOTE_COMMENTS_AVAILABLE",nil,nil);
	public void note_comments_available() {
		// no listeners
	}

	//	MODEL_NOTES_AVAILABLE",nil,nil);
	public void model_notes_avaialble() {
		// no Listeners
	}

	//	MODEL_OBJECT_TAGS_AVAILABLE",nil,nil);
	public void model_object_tags_available() {
		// no listeners
	}

	//	MODEL_OVERLAYS_AVAILABLE",nil,nil);
	public void model_overlays_available(Map<Long, Overlay> overlays) {
		// no listeners
	}

	//	MODEL_OVERLAYS_LESS_AVAILABLE",nil,@{@"removed":removedOverlays});
	public void model_overlays_less_available(List<Overlay> removedOverlays) {
		// todo: MapViewController.refreshViewFromModel()
	}

	//	MODEL_OVERLAYS_NEW_AVAILABLE",nil,@{@"added":addedOverlays});
	public void model_overlays_new_available(List<Overlay> addedOverlays) {
		// todo: MapViewController.refreshViewFromModel()
	}

	//	MODEL_PLAQUES_AVAILABLE",nil,nil); // from plaquesModel; not listened to. -sem

//	MODEL_PLAYER_INSTANCES_AVAILABLE",nil,nil);
	public void model_player_instances_available() {
		// todo: find listners
	}

	//	MODEL_PLAYER_INSTANCES_TOUCHED",nil,nil);
	public void model_player_instances_touched() {
		// no listeners
	}
//	MODEL_PLAYER_PLAYED_GAME_AVAILABLE",nil,notif.userInfo); // May be unnecessary in Android.
//	MODEL_PLAYER_SCRIPT_OPTIONS_AVAILABLE",nil,uInfo); // handled as internal method redirect in DialogsModel - sem
//	MODEL_PLAYER_TRIGGERS_AVAILABLE",nil,nil);
	public void model_player_triggers_available() {
		// todo: MapViewController.refreshViewFromModel()
		// todo: NotebookNotesViewController.newNoteListAvailable()
		mGamePlayAct.mGame.notesModel.invalidateCaches();
	}

	//	MODEL_POPULAR_GAMES_AVAILABLE",nil,nil); } // Handled in GamesListActivity - sem
//	MODEL_QUESTS_ACTIVE_LESS_AVAILABLE",nil,deltas);
	public void model_quests_active_less_available(Map<String, List<Quest>> deltas) {
		// todo: duplicate (UI) behaviour of IconQuestsViewController.refreshViewFromModel()
		// todo: duplicate UI of QuestsViewController.refreshViewFromModel()
	}

	//	MODEL_QUESTS_ACTIVE_NEW_AVAILABLE",nil,deltas);
	public void model_quests_active_new_available(Map<String, List<Quest>> deltas) {
		// todo: duplicate UI of GameNotificationViewController.parseActiveQuestsIntoNotifications(deltas)
		// todo: duplicate UI of IconQuestsViewController.refreshViewFromModel()
		// todo: duplicate UI of QuestsViewController.refreshViewFromModel()
	}

	//	MODEL_QUESTS_AVAILABLE",nil,nil);
	public void model_quests_available() {
		// no listeners
	}

	//	MODEL_QUESTS_COMPLETE_LESS_AVAILABLE",nil,deltas);
	public void model_quests_complete_less_available(Map<String, List<Quest>> deltas) {
		// todo: duplicate (UI) behaviour of IconQuestsViewController.refreshViewFromModel()
		// todo: duplicate UI of QuestsViewController.refreshViewFromModel()
	}

	//	MODEL_QUESTS_COMPLETE_NEW_AVAILABLE",nil,deltas);
	public void model_quests_complete_new_available(Map<String, List<Quest>> deltas) {
		// todo: duplicate UI of GameNotificationViewController.parseCompleteQuestsIntoNotifications(deltas)
		// todo: duplicate UI of IconQuestsViewController.refreshViewFromModel()
		// todo: duplicate UI of QuestsViewController.refreshViewFromModel()
	}

	//	MODEL_RECENT_GAMES_AVAILABLE",nil,nil); }
//	MODEL_REQUIREMENT_AND_PACKAGES_AVAILABLE",nil,nil);
	public void model_requirement_and_packages_available() {
		// No recipients
	}

	//	MODEL_REQUIREMENT_ATOMS_AVAILABLE",nil,nil);
	public void model_requirement_atoms_packages_available() {
		// Adrift at sea with no destination
	}

	//	MODEL_REQUIREMENT_ROOT_PACKAGES_AVAILABLE",nil,nil);
	public void model_requirement_root_packages_available() {
		// nada.
	}

	//	MODEL_SCENE_TOUCHED",nil,nil); // sent from ScenesModel but not listened to -sem
	public void model_scene_touched() {
		// no listeners
	}
//	MODEL_SCENES_AVAILABLE",nil,nil);
	public void model_scenes_available() {
		// todo: find listeners
	}

	//	MODEL_SCENES_PLAYER_SCENE_AVAILABLE",nil,nil);
	public void model_scenes_player_scene_available() {
		// sent from ScenesModel (in two places) but not listened to by anyone.
	}
//	MODEL_SEARCH_GAMES_AVAILABLE",nil,nil); } // Handled in GamesListActivity - sem
//	MODEL_TABS_AVAILABLE",nil,nil);
	public void model_tabs_available() {
		// no listeners
	}
//	MODEL_TABS_LESS_AVAILABLE",nil,deltas);

	public void model_tabs_less_available(Map<String, List<Tab>> deltas) {
		// todo: GamePlayTabSelectorViewController.refreshFromModel()
	}

	//	MODEL_TABS_NEW_AVAILABLE",nil,deltas);
	public void model_tabs_new_available(Map<String, List<Tab>> deltas) {
		// todo: GamePlayTabSelectorViewController.refreshFromModel()
	}

	//	MODEL_TAGS_AVAILABLE",nil,nil);
	public void model_tags_available() {
		// no listeners
	}

	//	MODEL_TRIGGERS_AVAILABLE",nil,nil);
	public void model_triggers_available() {
		// no listeners
	}

	//	MODEL_TRIGGERS_INVALIDATED",nil,@{@"invalidated_triggers":invalidatedTriggers});
	public void model_triggers_invalidated(List<Trigger> invalidatedTriggers) {
		// todo: DisplayQueueModel.reevaluateAutoTriggers(invalidatedTriggers)
		// todo: MapViewController.triggersInvalidated(invalidatedTriggers)
	}

	//	MODEL_TRIGGERS_LESS_AVAILABLE",nil,@{@"removed":removedTriggers});
	public void model_triggers_less_available(List<Trigger> removedTriggers) {
		// todo: DisplayQueueModel.reevaluateAutoTriggers(removedTriggers)
	}

	//	MODEL_TRIGGERS_NEW_AVAILABLE",nil,@{@"added":addedTriggers});
	public void model_triggers_new_available(List<Trigger> addedTriggers) {
		// todo: DisplayQueueModel.reevaluateAutoTriggers(addedTriggers)
	}

	//	MODEL_USERS_AVAILABLE",nil,nil);
	public void model_users_available() {
		//no listeners
	}

	//	MODEL_WEB_PAGES_AVAILABLE",nil,nil);
	public void model_web_pages_available() {
		// no listeners
	}

	//	PusherGameEventReceived",event,nil);
//	PusherGroupEventReceived",event,nil);
//	PusherPlayerEventReceived",event,nil);
//	PusherWebPageEventReceived",event,nil);
//	SERVICES_ANYWHERE_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_DIALOG_CHARACTERS_RECEIVED", nil, @{@"dialogCharacters":dialogCharacters});
	public void services_dialog_characters_received(List<DialogCharacter> dialogCharacters) {
		mGamePlayAct.mGame.dialogsModel.dialogCharactersReceived(dialogCharacters);
	}

	//	SERVICES_DIALOG_OPTIONS_RECEIVED", nil, @{@"dialogOptions":dialogOptions});
	public void services_dialog_options_received(List<DialogOption> dialogOptions) {
		mGamePlayAct.mGame.dialogsModel.dialogOptionsReceived(dialogOptions);
	}

	//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_character":dialogCharacter});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_option":dialogOption});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog_script":dialogScript});
//	SERVICES_DIALOG_RECEIVED", nil, @{@"dialog":dialog});
//	SERVICES_DIALOG_SCRIPTS_RECEIVED", nil, @{@"dialogScripts":dialogScripts});
	public void services_dialog_scipts_received(List<DialogScript> dialogScripts) {
		mGamePlayAct.mGame.dialogsModel.dialogScriptsReceived(dialogScripts);
	}

	//	SERVICES_DIALOGS_RECEIVED", nil, @{@"dialogs":dialogs});
	public void services_dialog_received(List<Dialog> dialogs) {
		mGamePlayAct.mGame.dialogsModel.dialogsReceived(dialogs);
	}

	//	SERVICES_DOWNLOADED_GAMES_RECEIVED", nil, @{@"games":d_games});
//	SERVICES_EVENT_RECEIVED", nil, @{@"event":event});
//	SERVICES_EVENTS_RECEIVED", nil, @{@"events":events});
	public void services_events_received(List<Event> events) {
		mGamePlayAct.mGame.eventsModel.eventsReceived(events);
	}

	//	SERVICES_FACTORIES_RECEIVED", nil, @{@"factories":factories});
	public void services_factories_received(List<Factory> factories) {
		mGamePlayAct.mGame.factoriesModel.factoriesReceived(factories);
	}

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
	public void services_instances_received(List<Instance> instances) {
		mGamePlayAct.mGame.instancesModel.gameInstancesReceived(instances);
	}

	//	SERVICES_ITEM_RECEIVED", nil, @{@"item":item});
//	SERVICES_ITEMS_RECEIVED", nil, @{@"items":items});

//	SERVICES_LOGIN_FAILED",nil,nil); return; }
//	SERVICES_LOGIN_RECEIVED",nil,@{@"user":user});
//	SERVICES_MEDIA_RECEIVED", nil, @{@"media":mediaDict}); // fakes an entire list and does same as fetching all media
//	SERVICES_MEDIAS_RECEIVED", nil, @{@"medias":mediaDicts});
	public void services_medias_received(List<Map<String, String>> rawMediaArr) {

		mGamePlayAct.mMediaModel.mediasReceived(rawMediaArr);
	}

	//	SERVICES_MINE_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_NEARBY_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_NOTE_COMMENT_RECEIVED", nil, @{@"note_comment":noteComment});
//	SERVICES_NOTE_COMMENTS_RECEIVED", nil, @{@"note_comments":noteComments});
	public void services_note_comments_received(List<NoteComment> noteComments) {
		mGamePlayAct.mGame.notesModel.noteCommentsReceived(noteComments);
	}

	//	SERVICES_NOTE_RECEIVED", nil, @{@"note":note});
//	SERVICES_NOTES_RECEIVED", nil, @{@"notes":notes});
//	SERVICES_OBJECT_TAGS_RECEIVED", nil, @{@"object_tags":objectTags});
	public void services_object_tags_received(List<ObjectTag> objectTags) {
		mGamePlayAct.mGame.tagsModel.objectTagsReceived(objectTags);
	}

	//	SERVICES_OVERLAY_RECEIVED", nil, @{@"overlay":overlay});
//	SERVICES_OVERLAYS_RECEIVED", nil, @{@"overlays":overlays});
	public void services_overlays_received(List<Overlay> overlays) {
		mGamePlayAct.mGame.overlaysModel.overlaysReceived(overlays);
	}

	//	SERVICES_PLAQUE_RECEIVED", nil, @{@"plaque":plaque});
//	SERVICES_PLAQUES_RECEIVED", nil, @{@"plaques":plaques});
//	SERVICES_PLAYER_GROUP_RECEIVED",nil,@{@"group":playerGroup}); //just return current
	public void services_player_group_received(Group playerGroup) {
		mGamePlayAct.mGame.groupsModel.playerGroupReceived(playerGroup);
	}

	//	SERVICES_PLAYER_INSTANCES_RECEIVED",nil,@{@"instances":pinsts});
	public void services_player_instances_received(Collection<Instance> insts) {
		// todo: something here?
	}

	//	SERVICES_PLAYER_INSTANCES_TOUCHED", nil, nil);
	public void services_player_instances_touched() {
		mGamePlayAct.mGame.playerInstancesModel.playerInstancesTouched();
	}

	//	SERVICES_PLAYER_LOGS_RECEIVED", nil, @{@"logs":logs});
//	SERVICES_PLAYER_OVERLAYS_RECEIVED", nil, @{@"overlays":overlays});
//	SERVICES_PLAYER_OVERLAYS_RECEIVED",nil,@{@"triggers":ptrigs});
	public void services_player_overlays_received(List<Overlay> overlays) {
		mGamePlayAct.mGame.overlaysModel.playerOverlaysReceived(overlays);
	}

	//	SERVICES_PLAYER_PLAYED_GAME_RECEIVED", nil, (NSDictionary *)result.resultData);
//	SERVICES_PLAYER_QUESTS_RECEIVED", nil, quests);
	public void services_player_quests_received(Map<String, List<Quest>> pquests) {
		mGamePlayAct.mGame.questsModel.playerQuestsReceived(pquests);
	}

	//	SERVICES_PLAYER_SCENE_RECEIVED", nil, @{@"scene":s});
//	SERVICES_PLAYER_SCENE_RECEIVED",nil,@{@"scene":playerScene}); //just return current
	public void services_player_scene_received(Scene playerScene) {
		mGamePlayAct.mGame.scenesModel.playerSceneReceived(playerScene);
	}
//	SERVICES_PLAYER_SCRIPT_OPTIONS_RECEIVED", nil, uInfo);
	public void services_player_script_options_received(Map<String, Object> uInfo) {
		// todo: find listeners for this
	}

	//	SERVICES_PLAYER_TABS_RECEIVED",nil,@{@"tabs":ptabs});
	public void services_player_tabs_received(List<Tab> tabs) {
		mGamePlayAct.mGame.tabsModel.playerTabsReceived(tabs);
	}

	//	SERVICES_PLAYER_TRIGGERS_RECEIVED",nil,@{@"triggers":ptrigs});
	public void services_player_trigger_received(List<Trigger> ptrigs) {
		// todo: MapViewController.refreshViewFromModel(ptrigs)
		// todo: NotebookNotesViewController.newNoteListAvailable()
		mGamePlayAct.mGame.notesModel.invalidateCaches();
	}

	//	SERVICES_POPULAR_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_QUEST_RECEIVED", nil, @{@"quest":quest});
//	SERVICES_QUESTS_RECEIVED", nil, @{@"quests":quests});
	public void services_quests_received(List<Quest> quests) {
		mGamePlayAct.mGame.questsModel.questsReceived(quests);
	}

	//	SERVICES_RECENT_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_REQUIREMENT_AND_PACKAGES_RECEIVED", nil, @{@"requirement_and_packages":raps});
//	SERVICES_REQUIREMENT_ATOMS_RECEIVED", nil, @{@"requirement_atoms":as});
//	SERVICES_REQUIREMENT_ROOT_PACKAGES_RECEIVED", nil, @{@"requirement_root_packages":rrps});
//	SERVICES_SCENE_RECEIVED", nil, @{@"scene":scene});
//	SERVICES_SCENE_TOUCHED", nil, nil);
	public void services_scene_touched() {
		mGamePlayAct.mGame.scenesModel.sceneTouched();
	}

	//	SERVICES_SCENES_RECEIVED", nil, @{@"scenes":scenes});
//	SERVICES_SEARCH_GAMES_RECEIVED", nil, @{@"games":[self parseGames:(NSArray *)result.resultData]});
//	SERVICES_TAB_RECEIVED", nil, @{@"tab":tab});
//	SERVICES_TABS_RECEIVED", nil, @{@"tabs":tabs});
	public void services_tabs_received(List<Tab> tabs) {
		mGamePlayAct.mGame.tabsModel.tabsReceived(tabs);
	}

	//	SERVICES_TAG_RECEIVED", nil, @{@"tag":tag});
//	SERVICES_TAGS_RECEIVED", nil, @{@"tags":tags});
	public void services_tags_received(List<Tag> tags) {
		mGamePlayAct.mGame.tagsModel.tagsReceived(tags);
	}

	//	SERVICES_TRIGGER_RECEIVED", nil, @{@"trigger":trigger});
//	SERVICES_TRIGGERS_RECEIVED", nil, @{@"triggers":triggers});
	public void services_triggers_received(List<Trigger> triggers) {
		mGamePlayAct.mGame.triggersModel.triggersReceived(triggers);
	}

	//	SERVICES_UPDATE_USER_FAILED",nil,nil); return; }
//	SERVICES_UPDATE_USER_RECEIVED",nil,@{@"user":user});
//	SERVICES_USER_RECEIVED", nil, @{@"user":user});
//	SERVICES_USERS_RECEIVED", nil, @{@"users":users});
	public void services_users_received(Map<String, User> mGameUsers) {
		mGamePlayAct.mUsersModel.usersReceived(mGameUsers);
	}

	//	SERVICES_WEB_PAGE_RECEIVED", nil, @{@"web_page":webPage});
//	SERVICES_WEB_PAGES_RECEIVED", nil, @{@"webPages":webPages});
	public void services_web_pages_received(List<WebPage> webPages) {
		mGamePlayAct.mGame.webPagesModel.webPagesReceived(webPages);
	}

	//	USER_MOVED",nil,nil);
	public void user_moved() {
		// todo: display Queue Model reevaluateAutoTriggers() - update UI locations e.g. map
		// todo: also MapViewController.playerMoved()

	}



//	WIFI_CONNECTED",self,nil); break;

}
