package edu.uoregon.casls.aris_android.Utilities;

/**
 * Created by smorison on 9/15/15.
 */
public class Calls {

	// Server calls
	// Game initialization
	public static final String HTTP_GET_SCENES_4_GAME         = "v2.scenes.getScenesForGame/";
	public static final String HTTP_TOUCH_SCENE_4_PLAYER      = "v2.client.touchSceneForPlayer/";
	public static final String HTTP_GET_GROUPS_4_GAME         = "v2.plaques.getGroupsForGame/";
	public static final String HTTP_TOUCH_GROUP_4_PLAYER      = "v2.client.touchGroupForPlayer/";
	public static final String HTTP_GET_PLAQUES_4_GAME        = "v2.plaques.getPlaquesForGame/";
	public static final String HTTP_GET_ITEMS_4_GAME          = "v2.items.getItemsForGame/";
	public static final String HTTP_TOUCH_ITEMS_4_PLAYER      = "v2.client.touchItemsForPlayer/";
	public static final String HTTP_TOUCH_ITEMS_4_GAME        = "v2.client.touchItemsForGame/";
	public static final String HTTP_TOUCH_ITEMS_4_GROUPS      = "v2.client.touchItemsForGroups/";
	public static final String HTTP_GET_DIALOGS_4_GAME        = "v2.dialogs.getDialogsForGame/";
	public static final String HTTP_GET_DIALOG_CHARS_4_GAME   = "v2.dialogs.getDialogCharactersForGame/";
	public static final String HTTP_GET_DIALOG_SCRIPTS_4_GAME = "v2.dialogs.getDialogScriptsForGame/";
	public static final String HTTP_GET_DIALOG_OPTNS_4_GAME   = "v2.dialogs.getDialogOptionsForGame/";
	public static final String HTTP_GET_WEB_PAGES_4_GAME      = "v2.web_pages.getWebPagesForGame/";
	public static final String HTTP_GET_NOTES_4_GAME          = "v2.notes.getNotesForGame/";
	public static final String HTTP_GET_NOTE_COMMNTS_4_GAME   = "v2.note_comments.getNoteCommentsForGame/";
	public static final String HTTP_GET_TAGS_4_GAME           = "v2.tags.getTagsForGame/";
	public static final String HTTP_GET_OBJ_TAGS_4_GAME       = "v2.tags.getObjectTagsForGame/";
	public static final String HTTP_GET_EVENTS_4_GAME         = "v2.events.getEventsForGame/";
	public static final String HTTP_GET_QUESTS_4_GAME         = "v2.quests.getQuestsForGame/";
	public static final String HTTP_GET_TRIGGERS_4_GAME       = "v2.triggers.getTriggersForGame/";
	public static final String HTTP_GET_FACTORIES_4_GAME      = "v2.factories.getFactoriesForGame/";
	public static final String HTTP_GET_OVERLAYS_4_GAME       = "v2.overlays.getOverlaysForGame/";
	public static final String HTTP_GET_INSTANCES_4_GAME      = "v2.instances.getInstancesForGame/";
	public static final String HTTP_GET_TABS_4_GAME           = "v2.tabs.getTabsForGame/";
	public static final String HTTP_GET_MEDIA_4_GAME          = "v2.media.getMediaForGame/";
	public static final String HTTP_GET_USERS_4_GAME          = "v2.users.getUsersForGame/";
	public static final String HTTP_GET_REQ_ROOT_PKGS_4_GAME  = "v2.requirements.getRequirementRootPackagesForGame/";
	public static final String HTTP_GET_REQ_AND_PKGS_4_GAME   = "v2.requirements.getRequirementAndPackagesForGame/";
	public static final String HTTP_GET_REQ_ATOMS_4_GAME      = "v2.requirements.getRequirementAtomsForGame/";
	// Game cyclical update calls
	public static final String HTTP_GET_SCENE_4_PLAYER        = "v2.client.getSceneForPlayer/";
	public static final String HTTP_GET_INSTANCES_4_PLAYER    = "v2.client.getInstancesForPlayer/"; // Needs game_id, owner_id
	public static final String HTTP_GET_GROUP_4_PLAYER        = "v2.client.getGroupForPlayer/";
	public static final String HTTP_GET_TRIGGERS_4_PLAYER     = "v2.client.getTriggersForPlayer/"; // "tick_factories","game_id"
	public static final String HTTP_GET_OVERLAYS_4_PLAYER     = "v2.client.getOverlaysForPlayer/";
	public static final String HTTP_GET_QUESTS_4_PLAYER       = "v2.client.getQuestsForPlayer/";
	public static final String HTTP_GET_TABS_4_PLAYER         = "v2.client.getTabsForPlayer/";
	public static final String HTTP_GET_LOGS_4_PLAYER         = "v2.client.getLogsForPlayer/";
	// Client behaviour initiated calls:
	public static final String HTTP_GET_LOG_PLAYER_SET_SCENE  = "v2.client.logPlayerSetScene/"; // "game_id", "item_id"
	public static final String HTTP_SET_PLAYER_SCENE          = "v2.client.setPlayerScene/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_BEGAN_GAME     = "v2.client.logPlayerBeganGame/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_MOVED          = "v2.client.logPlayerMoved/"; // "game_id","scene_id"
	// Client utility calls
	public static final String HTTP_USER_LOGIN_REQ_API        = "v2.users.logIn/";
	public static final String HTTP_USER_REQ_FORGOT_PASSWD    = "v2.users.requestForgotPasswordEmail/";

	public static final String HTTP_GET_NOTE  = "v2.notes.getNote/"; // todo check this call syntax and server prefix paths
	public static final String HTTP_GET_MEDIA = "v2.media.getMedia/";

}
