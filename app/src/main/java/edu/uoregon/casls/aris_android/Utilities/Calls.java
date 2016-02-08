package edu.uoregon.casls.aris_android.Utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by smorison on 9/15/15.
 */
public class Calls {

	// Server calls

	// delete things
	public static final String HTTP_DELETE_NOTE = "v2.notes.deleteNote/";
	public static final String HTTP_DELETE_NOTE_COMMENT = "v2.note_comments.deleteNoteComment/";

	// drop it
	public static final String HTTP_DROP_ITEM = "v2.client.dropItem/";

	// create things
	public static final String HTTP_CREATE_NOTE = "v2.notes.createNote/";
	public static final String HTTP_CREATE_NOTE_COMMENT = "v2.note_comments.createNoteComment/";


	// Game initialization
	public static final String HTTP_GET_DIALOG                   = "v2.dialogs.getDialog/";
	public static final String HTTP_GET_DIALOG_CHARACTER         = "v2.dialogs.getDialogCharacter/";
	public static final String HTTP_GET_DIALOG_OPTION            = "v2.dialogs.getDialogOption/";
	public static final String HTTP_GET_DIALOG_SCRIPT            = "v2.dialogs.getDialogScript/";
	public static final String HTTP_GET_DIALOG_CHARS_4_GAME      = "v2.dialogs.getDialogCharactersForGame/";
	public static final String HTTP_GET_DIALOG_SCRIPTS_4_GAME    = "v2.dialogs.getDialogScriptsForGame/";
	public static final String HTTP_GET_DIALOG_OPTNS_4_GAME      = "v2.dialogs.getDialogOptionsForGame/";
	public static final String HTTP_GET_DIALOGS_4_GAME           = "v2.dialogs.getDialogsForGame/";
	public static final String HTTP_GET_EVENTS_4_GAME            = "v2.events.getEventsForGame/";
	public static final String HTTP_GET_FACTORIES_4_GAME         = "v2.factories.getFactoriesForGame/";
	public static final String HTTP_GET_GROUPS_4_GAME            = "v2.groups.getGroupsForGame/";
	public static final String HTTP_TOUCH_GROUP_4_PLAYER         = "v2.client.touchGroupForPlayer/";
	public static final String HTTP_GET_INSTANCES_4_GAME         = "v2.instances.getInstancesForGame/";
	public static final String HTTP_GET_ITEMS_4_GAME             = "v2.items.getItemsForGame/";
	public static final String HTTP_TOUCH_ITEMS_4_PLAYER         = "v2.client.touchItemsForPlayer/";
	public static final String HTTP_TOUCH_ITEMS_4_GAME           = "v2.client.touchItemsForGame/";
	public static final String HTTP_TOUCH_ITEMS_4_GROUPS         = "v2.client.touchItemsForGroups/";
	public static final String HTTP_GET_NOTES_4_GAME             = "v2.notes.getNotesForGame/";
	public static final String HTTP_GET_NOTE_COMMNTS_4_GAME      = "v2.note_comments.getNoteCommentsForGame/";
	public static final String HTTP_GET_OBJ_TAGS_4_GAME          = "v2.tags.getObjectTagsForGame/";
	public static final String HTTP_GET_PLAQUES_4_GAME           = "v2.plaques.getPlaquesForGame/";
	public static final String HTTP_GET_QUESTS_4_GAME            = "v2.quests.getQuestsForGame/";
	public static final String HTTP_GET_SCENES_4_GAME            = "v2.scenes.getScenesForGame/";
	public static final String HTTP_TOUCH_SCENE_4_PLAYER         = "v2.client.touchSceneForPlayer/";
	public static final String HTTP_GET_TABS_4_GAME              = "v2.tabs.getTabsForGame/";
	public static final String HTTP_GET_TAGS_4_GAME              = "v2.tags.getTagsForGame/";
	public static final String HTTP_GET_TRIGGERS_4_GAME          = "v2.triggers.getTriggersForGame/";
	public static final String HTTP_GET_OVERLAYS_4_GAME          = "v2.overlays.getOverlaysForGame/";
	public static final String HTTP_GET_MEDIA_4_GAME             = "v2.media.getMediaForGame/";
	public static final String HTTP_GET_USERS_4_GAME             = "v2.users.getUsersForGame/";
	public static final String HTTP_GET_REQ_ROOT_PKGS_4_GAME     = "v2.requirements.getRequirementRootPackagesForGame/";
	public static final String HTTP_GET_REQ_AND_PKGS_4_GAME      = "v2.requirements.getRequirementAndPackagesForGame/";
	public static final String HTTP_GET_REQ_ATOMS_4_GAME         = "v2.requirements.getRequirementAtomsForGame/";
	public static final String HTTP_GET_WEB_PAGES_4_GAME         = "v2.web_pages.getWebPagesForGame/";
	// Game cyclical update calls
	public static final String HTTP_GET_INSTANCES_4_PLAYER       = "v2.client.getInstancesForPlayer/"; // Needs game_id, owner_id
	public static final String HTTP_GET_GROUP_4_PLAYER           = "v2.client.getGroupForPlayer/";
	public static final String HTTP_GET_LOGS_4_PLAYER            = "v2.client.getLogsForPlayer/";
	public static final String HTTP_GET_OVERLAYS_4_PLAYER        = "v2.client.getOverlaysForPlayer/";
	public static final String HTTP_GET_QUESTS_4_PLAYER          = "v2.client.getQuestsForPlayer/";
	public static final String HTTP_GET_SCENE_4_PLAYER           = "v2.client.getSceneForPlayer/";
	public static final String HTTP_GET_TABS_4_PLAYER            = "v2.client.getTabsForPlayer/";
	public static final String HTTP_GET_TRIGGERS_4_PLAYER        = "v2.client.getTriggersForPlayer/"; // "tick_factories","game_id"
	// log Game events
	public static final String HTTP_LOG_GAME_RECEIVED_ITEM       = "v2.client.logGameReceivedItem/";
	public static final String HTTP_LOG_GAME_LOST_ITEM           = "v2.client.logGameLostItem/";
	// log Group events
	public static final String HTTP_LOG_GROUP_RECEIVED_ITEM      = "v2.client.logGroupReceivedItem/";
	public static final String HTTP_LOG_GROUP_LOST_ITEM          = "v2.client.logGroupLostItem/";
	// Client player behaviour initiated calls:
	public static final String HTTP_LOG_PLAYER_BEGAN_GAME        = "v2.client.logPlayerBeganGame/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_COMPLATED_QUEST   = "v2.client.logPlayerCompletedQuest/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_JOINED_GROUP      = "v2.client.logPlayerJoinedGroup/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_LOST_ITEM         = "v2.client.logPlayerLostItem/";
	public static final String HTTP_LOG_PLAYER_MOVED             = "v2.client.logPlayerMoved/"; // "game_id","scene_id"
	public static final String HTTP_LOG_PLAYER_RAN_EVENT_PKG     = "v2.client.logPlayerRanEventPackage/";
	public static final String HTTP_LOG_PLAYER_RECEIVED_ITEM     = "v2.client.logPlayerReceivedItem/";
	public static final String HTTP_LOG_PLAYER_RESET_GAME        = "v2.client.logPlayerResetGame/";
	public static final String HTTP_LOG_PLAYER_SET_SCENE         = "v2.client.logPlayerSetScene/"; // "game_id", "item_id"
	public static final String HTTP_LOG_PLAYER_TRIGGERED_TRIGGER = "v2.client.logPlayerTriggeredTrigger/";
	public static final String HTTP_LOG_PLAYER_VIEWED_CONTENT    = "v2.client.logPlayerViewedContent/";
	public static final String HTTP_LOG_PLAYER_VIEWED_INSTANCE   = "v2.client.logPlayerViewedInstance/";
	public static final String HTTP_LOG_PLAYER_VIEWED_TAB        = "v2.client.logPlayerViewedTab/";

	public static final String HTTP_SET_PLAYER_GROUP = "v2.client.setPlayerGroup/";
	public static final String HTTP_SET_PLAYER_SCENE = "v2.client.setPlayerScene/"; // "game_id","scene_id"

	// update things
	public static final String HTTP_UPDATE_NOTE = "v2.notes.updateNote/";
	public static final String HTTP_UPDATE_NOTE_COMMENT = "v2.note_comments.updateNoteComment/";

	// Client utility calls
	public static final String HTTP_USER_LOGIN_REQ_API     = "v2.users.logIn/";
	public static final String HTTP_USER_REQ_FORGOT_PASSWD = "v2.users.requestForgotPasswordEmail/";

	public static final String HTTP_GET_MEDIA   = "v2.media.getMedia/";
	public static final String HTTP_GET_NOTE    = "v2.notes.getNote/"; // todo check this call syntax and server prefix paths
	public static final String HTTP_GET_PLAYER_PLAYED_GAME = "v2.client.getPlayerPlayedGame/";
	public static final String HTTP_GET_TRIGGER = "v2.triggers.getTrigger/";
	public static final String HTTP_GET_USER    = "v2.users.getUser/";

	// calls who's responses can be ignored.
	public static final Set<String> FIRE_AND_FORGET_CALLS = new HashSet<String>(Arrays.asList(
			new String[]{
					HTTP_DELETE_NOTE,
					HTTP_DELETE_NOTE_COMMENT,
					HTTP_DROP_ITEM,
					HTTP_LOG_PLAYER_BEGAN_GAME,
					HTTP_LOG_PLAYER_RESET_GAME,
					HTTP_LOG_PLAYER_MOVED,
					HTTP_LOG_PLAYER_VIEWED_TAB,
					HTTP_LOG_PLAYER_VIEWED_CONTENT,
					HTTP_LOG_PLAYER_RECEIVED_ITEM,
					HTTP_LOG_PLAYER_LOST_ITEM,
					HTTP_LOG_GAME_RECEIVED_ITEM,
					HTTP_LOG_GAME_LOST_ITEM,
					HTTP_LOG_GROUP_RECEIVED_ITEM,
					HTTP_LOG_GROUP_LOST_ITEM,
					HTTP_LOG_PLAYER_SET_SCENE,
					HTTP_LOG_PLAYER_JOINED_GROUP,
					HTTP_LOG_PLAYER_RAN_EVENT_PKG,
					HTTP_LOG_PLAYER_COMPLATED_QUEST
			}
	));
}
