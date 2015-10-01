package edu.uoregon.casls.aris_android.Utilities;

/**
 * Created by smorison on 9/15/15.
 */
public class Calls {

	// Server calls
	// Game initialization
	public static final String HTTP_GET_SCENES_4_GAME 		= "v2.scenes.getScenesForGame/";		// Game Piece Avail
	public static final String HTTP_TOUCH_SCENE_4_PLAYER 	= "v2.client.touchSceneForPlayer/";		// Game Piece Avail
	public static final String HTTP_GET_GROUPS_4_GAME 		= "v2.plaques.getGroupsForGame/";		// Game Piece Avail
	public static final String HTTP_GET_PLAQUES_4_GAME 		= "v2.plaques.getPlaquesForGame/";		// Game Piece Avail
	public static final String HTTP_GET_ITEMS_4_GAME 		= "v2.items.getItemsForGame/";			// Game Piece Avail
	public static final String HTTP_TOUCH_ITEMS_4_PLAYER 	= "v2.client.touchItemsForPlayer/";		// Game Piece Avail
	public static final String HTTP_GET_DIALOGS_4_GAME 		= "v2.dialogs.getDialogsForGame/";		// Game Piece Avail
	public static final String HTTP_GET_DIALOG_CHARS_4_GAME = "v2.dialogs.getDialogCharactersForGame/";// Game Piece Avail
	public static final String HTTP_GET_DIALOG_SCRIPTS_4_GAME = "v2.dialogs.getDialogScriptsForGame/";// Game Piece Avail
	public static final String HTTP_GET_DIALOG_OPTNS_4_GAME = "v2.dialogs.getDialogOptionsForGame/";// Game Piece Avail
	public static final String HTTP_GET_WEB_PAGES_4_GAME 	= "v2.web_pages.getWebPagesForGame/";	// Game Piece Avail
	public static final String HTTP_GET_NOTES_4_GAME 		= "v2.notes.getNotesForGame/";			// Game Piece Avail
	public static final String HTTP_GET_NOTE_COMMNTS_4_GAME = "v2.note_comments.getNoteCommentsForGame/";// Game Piece Avail
	public static final String HTTP_GET_TAGS_4_GAME 		= "v2.tags.getTagsForGame/";			// Game Piece Avail
	public static final String HTTP_GET_OBJ_TAGS_4_GAME 	= "v2.tags.getObjectTagsForGame/";		// Game Piece Avail
	public static final String HTTP_GET_EVENTS_4_GAME 		= "v2.events.getEventsForGame/";		// Game Piece Avail
	public static final String HTTP_GET_QUESTS_4_GAME 		= "v2.quests.getQuestsForGame/";		// Game Piece Avail
	public static final String HTTP_GET_TRIGGERS_4_GAME 	= "v2.triggers.getTriggersForGame/";	// Game Piece Avail
	public static final String HTTP_GET_FACTORIES_4_GAME 	= "v2.factories.getFactoriesForGame/";	// Game Piece Avail
	public static final String HTTP_GET_OVERLAYS_4_GAME 	= "v2.overlays.getOverlaysForGame/";	// Game Piece Avail
	public static final String HTTP_GET_INSTANCES_4_GAME 	= "v2.instances.getInstancesForGame/";	// Game Piece Avail
	public static final String HTTP_GET_TABS_4_GAME 		= "v2.tabs.getTabsForGame/";			// Game Piece Avail
	public static final String HTTP_GET_MEDIA_4_GAME 		= "v2.media.getMediaForGame/";			// Game Piece Avail
	public static final String HTTP_GET_USERS_4_GAME 		= "v2.users.getUsersForGame/";			// Game Piece Avail
	// Game cyclical update calls
	public static final String HTTP_GET_SCENE_4_PLAYER 		= "v2.client.getSceneForPlayer/";
	public static final String HTTP_GET_INSTANCES_4_PLAYER 	= "v2.client.getInstancesForPlayer/"; // Needs game_id, owner_id
	public static final String HTTP_GET_TRIGGERS_4_PLAYER 	= "v2.client.getTriggersForPlayer/"; // "tick_factories","game_id"
	public static final String HTTP_GET_OVERLAYS_4_PLAYER 	= "v2.client.getOverlaysForPlayer/";
	public static final String HTTP_GET_QUESTS_4_PLAYER 	= "v2.client.getQuestsForPlayer/";
	public static final String HTTP_GET_TABS_4_PLAYER 		= "v2.client.getTabsForPlayer/";
	public static final String HTTP_GET_LOGS_4_PLAYER 		= "v2.client.getLogsForPlayer/";
	// Client behaviour initiated calls:
	public static final String HTTP_GET_LOG_PLAYER_SET_SCENE = "v2.client.logPlayerSetScene/"; // "game_id", "item_id"
	public static final String HTTP_SET_PLAYER_SCENE 		= "v2.client.setPlayerScene/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_BEGAN_GAME 	= "v2.client.logPlayerBeganGame/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_MOVED 		= "v2.client.logPlayerMoved/"; // "game_id","scene_id"


	public static final String HTTP_GET_NOTE = "v2.notes.getNote/"; // todo check this call syntax and server prefix paths
}
