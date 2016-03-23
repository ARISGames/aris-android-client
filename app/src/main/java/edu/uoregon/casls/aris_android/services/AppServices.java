package edu.uoregon.casls.aris_android.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Calls;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.Tag;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 10/6/15.
 *
 * Centralized location for all game model HTTP server calls. Sends requests to server
 * asynchronously. Responses are fielded by ResponseHandler which must be instantiated
 * in GamePlayActivity and referenced herein.
 */
public class AppServices {


	public transient GamePlayActivity mGamePlayAct;
	public ARISMediaLoader mMediaLoader; // transient?
//	public transient Game mGame;
//	public User mPlayer;

	public AppServices(GamePlayActivity gamePlayActivity) {
		initContext(gamePlayActivity);
	}

	public AppServices() {
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		// set up media loader
		if (mMediaLoader == null)
			mMediaLoader = new ARISMediaLoader(mGamePlayAct);
		//convenience references:
//		mGame = mGamePlayAct.mGame;
//		mPlayer = mGamePlayAct.mPlayer;
	}

	public void fetchInstances() {
		pollServer(Calls.HTTP_GET_INSTANCES_4_GAME, jsonGameId()); //could leave out ownerId and get same result, but would rather be explicit -iOS comment
	}

	public void fetchInstanceById(long i) {

	}


	public void fetchInstancesForPlayer() {
		JSONObject jsonArgs = jsonGameId();
		try {
			jsonArgs.put("owner_id", mGamePlayAct.mPlayer.user_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_GET_INSTANCES_4_PLAYER, jsonArgs);
	}

	public void fetchPlayerPlayedGame() {
		pollServer(Calls.HTTP_GET_PLAYER_PLAYED_GAME, jsonGameId());
	}

	public void setQtyForInstanceId(long instance_id, long qty) {

	}

	public void fetchOptionsForPlayerForDialog(long dialog_id, long dialog_script_id) {

	}

	public void fetchRequirementAtoms() {
		pollServer(Calls.HTTP_GET_REQMNT_ATOMS_4_GAME, jsonGameId());
	}

	public void fetchRequirementAnds() {
		pollServer(Calls.HTTP_GET_REQMNT_AND_PKGS_4_GAME, jsonGameId());
	}

	public void fetchRequirementRoots() {
		pollServer(Calls.HTTP_GET_REQMNT_ROOT_PKGS_4_GAME, jsonGameId());
	}

	public void touchItemsForGame() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_GAME, jsonGameId());
	}

	public void touchItemsForPlayer() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_PLAYER, jsonGameId());
	}

	public void touchItemsForGroups() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_GROUPS, jsonGameId());
	}

	public void fetchItems() {
		pollServer(Calls.HTTP_GET_ITEMS_4_GAME, jsonGameId());
	}

	public void dropItem(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
			jsonArgs.put("latitude", mGamePlayAct.mPlayer.latitude);
			jsonArgs.put("longitude", mGamePlayAct.mPlayer.longitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_DROP_ITEM, jsonArgs);
	}

	public void fetchLogsForPlayer() {
		pollServer(Calls.HTTP_GET_LOGS_4_PLAYER, jsonGameId());
	}

	public void logPlayerEnteredGame() {
		pollServer(Calls.HTTP_LOG_PLAYER_BEGAN_GAME, jsonGameId());
	}

	public void logPlayerResetGame() { // handled in GameCoverPageActivity() in Android; just here for consistency
		pollServer(Calls.HTTP_LOG_PLAYER_RESET_GAME, jsonGameId());
	}

	public void logPlayerMoved() {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("latitude", mGamePlayAct.mPlayer.latitude);
			jsonArgs.put("longitude", mGamePlayAct.mPlayer.longitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_MOVED, jsonArgs);
	}

	public void logPlayerViewedTabId(long tab_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("tab_id", tab_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_MOVED, jsonArgs);
	}

	public void logPlayerViewedPlaqueId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "PLAQUE");  //    @"content_type":@"PLAQUE",
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedItemId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "ITEM");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedDialogId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "DIALOG");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedDialogScriptId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "DIALOG_SCRIPT");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedWebPageId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "WEB_PAGE");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedNoteId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("content_type", "NOTE");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedSceneId(long content_id) {
		JSONObject jsonArgs = jsonGameId();
		// add the scene ID to params
		try {
			jsonArgs.put("content_type", "SCENE");  //
			jsonArgs.put("content_id", content_id); // @"content_id":[NSNumber numberWithLong:plaque_id]
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_CONTENT, jsonArgs);
	}

	public void logPlayerViewedInstanceId(long instance_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("instance_id", instance_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_VIEWED_INSTANCE, jsonArgs);
	}

	public void logPlayerTriggeredTriggerId(long trigger_id) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("trigger_id", trigger_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_TRIGGERED_TRIGGER, jsonArgs);
	}

	public void logPlayerReceivedItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_RECEIVED_ITEM, jsonArgs);
	}

	public void logPlayerLostItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_LOST_ITEM, jsonArgs);
	}

	public void logGameReceivedItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_GAME_RECEIVED_ITEM, jsonArgs);
	}

	public void logGameLostItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_GAME_LOST_ITEM, jsonArgs);
	}

	public void logGroupReceivedItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_GROUP_RECEIVED_ITEM, jsonArgs);
	}

	public void logGroupLostItemId(long item_id, long qty) {
		JSONObject jsonArgs = jsonGameId();
		// add to params
		try {
			jsonArgs.put("item_id", item_id);
			jsonArgs.put("qty", qty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_GROUP_LOST_ITEM, jsonArgs);
	}

	public void logPlayerSetSceneId(long scene_id) {
		JSONObject jsonArgs = jsonGameId();
		// add the scene ID to params
		try {
			jsonArgs.put("scene_id", scene_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_LOG_PLAYER_SET_SCENE, jsonArgs);
	}

	public void logPlayerJoinedGroupId(long group_id) { // todo: this
	}

	public void logPlayerRanEventPackageId(long event_package_id) { // todo:
	}

	public void fetchDialog(long dialogId) { // unused call
		pollServer(Calls.HTTP_GET_DIALOG, longToJSONReqParam("dialog_id", dialogId));
	}

	public void fetchDialogCharacter(long dialogCharacterId) { // unused call
		pollServer(Calls.HTTP_GET_DIALOG_CHARACTER, longToJSONReqParam("dialog_character_id", dialogCharacterId));
	}

	public void fetchDialogOption(long dialogOptionId) { // unused call
		pollServer(Calls.HTTP_GET_DIALOG_OPTION, longToJSONReqParam("dialog_option_id", dialogOptionId));
	}

	public void fetchDialogScript(long dialogScriptId) { // unused call
		pollServer(Calls.HTTP_GET_DIALOG_SCRIPT, longToJSONReqParam("dialog_script_id", dialogScriptId));
	}

	public void fetchDialogs() {
		pollServer(Calls.HTTP_GET_DIALOGS_4_GAME, jsonGameId());
	}

	public void fetchDialogCharacters() {
		pollServer(Calls.HTTP_GET_DIALOG_CHARS_4_GAME, jsonGameId());
	}

	public void fetchDialogScripts() {
		pollServer(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME, jsonGameId());
	}

	public void fetchDialogOptions() {
		pollServer(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME, jsonGameId());
	}

	public void fetchQuestsForPlayer() {
		pollServer(Calls.HTTP_GET_QUESTS_4_PLAYER, jsonGameId());
	}

	public void fetchQuests() {
		pollServer(Calls.HTTP_GET_QUESTS_4_GAME, jsonGameId());
	}

	public void fetchTags() {
		pollServer(Calls.HTTP_GET_TAGS_4_GAME, jsonGameId());
	}

	public void fetchObjectTags() {
		pollServer(Calls.HTTP_GET_OBJ_TAGS_4_GAME, jsonGameId());
	}

	public void fetchMedias() {
		pollServer(Calls.HTTP_GET_MEDIA_4_GAME, jsonGameId());
	}

	public void fetchMediaById(long media_id) {
		pollServer(Calls.HTTP_GET_MEDIA, longToJSONReqParam("media_id", media_id));
	}

	public void createNote(Note n, Tag t, Media m, Trigger tr) {
		JSONObject jsonArgs = jsonGameId();
		try {
			jsonArgs.put("user_id", mGamePlayAct.mPlayer.user_id);
			jsonArgs.put("name", n.name);
			jsonArgs.put("description", n.desc);
			if (m != null) {
				// convert to base64 string
				// Note may crash some devices. If so perhaps consider converting the local media file
				// as suggested here: http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				m.data.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byte[] byteArray = byteArrayOutputStream .toByteArray();
				JSONObject media = new JSONObject();
				media.put("game_id", mGamePlayAct.mGame.game_id);
				media.put("file_name", m.localURL.toString());
				media.put("data", Base64.encodeToString(byteArray, Base64.DEFAULT));
				jsonArgs.put("media", media);
			}
			if (t != null) {
				jsonArgs.put("tag_id", t.tag_id);
			}
			if (tr != null) {
				JSONObject trigger = new JSONObject();
				trigger.put("game_id", mGamePlayAct.mGame.game_id);
				trigger.put("latitude", tr.latitude);
				trigger.put("longitude", tr.location);
				jsonArgs.put("trigger", trigger);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		pollServer(Calls.HTTP_CREATE_NOTE, jsonArgs);

	}

	public void updateNote(Note n, Tag t, Media m, Trigger tr) {
		JSONObject jsonArgs = jsonGameId();
		try {
			jsonArgs.put("note_id", n.note_id);
			jsonArgs.put("user_id", mGamePlayAct.mPlayer.user_id);
			jsonArgs.put("name", n.name);
			jsonArgs.put("description", n.desc);
			if (m != null) {
				// convert to base64 string
				// Note may crash some devices. If so perhaps consider converting the local media file
				// as suggested here: http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				m.data.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byte[] byteArray = byteArrayOutputStream .toByteArray();
				JSONObject media = new JSONObject();
				media.put("game_id", mGamePlayAct.mGame.game_id);
				media.put("file_name", m.localURL.toString());
				media.put("data", Base64.encodeToString(byteArray, Base64.DEFAULT));
				jsonArgs.put("media", media);
			}
			if (t != null)
				jsonArgs.put("tag_id", t.tag_id);
			else
				jsonArgs.put("tag_id", 0);
			if (tr != null) {
				JSONObject trigger = new JSONObject();
				trigger.put("game_id", mGamePlayAct.mGame.game_id);
				trigger.put("latitude", tr.latitude);
				trigger.put("longitude", tr.location);
				jsonArgs.put("trigger", trigger);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		pollServer(Calls.HTTP_UPDATE_NOTE, jsonArgs);
	}

	public void deleteNoteId(long note_id) {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("note_id", note_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_DELETE_NOTE, jsonParams);
	}

	public void fetchNoteById(long note_id) {
		pollServer(Calls.HTTP_GET_NOTE, longToJSONReqParam  ("note_id", note_id));
	}

	public void fetchNotes() {
		pollServer(Calls.HTTP_GET_NOTES_4_GAME, jsonGameId());
	}

	public void fetchNoteComments() {
		pollServer(Calls.HTTP_GET_NOTE_COMMENTS_4_GAME, jsonGameId());
	}

	public void createNoteComment(NoteComment noteComment) {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("user_id", mGamePlayAct.mPlayer.user_id);
			jsonParams.put("note_id", noteComment.note_id);
			jsonParams.put("name", noteComment.name);
			jsonParams.put("description", noteComment.description);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_CREATE_NOTE_COMMENT, jsonParams);
	}

	public void updateNoteComment(NoteComment noteComment) {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("user_id", mGamePlayAct.mPlayer.user_id);
			jsonParams.put("note_id", noteComment.note_id);
			jsonParams.put("note_comment_id", noteComment.note_comment_id);
			jsonParams.put("name", noteComment.name);
			jsonParams.put("description", noteComment.description);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_UPDATE_NOTE_COMMENT, jsonParams);
	}

	public void deleteNoteCommentId(long note_comment_id) {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("note_comment_id", note_comment_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_DELETE_NOTE_COMMENT, jsonParams);
	}

	public void fetchEvents() {
		pollServer(Calls.HTTP_GET_EVENTS_4_GAME, jsonGameId());
	}

	public void fetchFactories() {
		pollServer(Calls.HTTP_GET_FACTORIES_4_GAME, jsonGameId());
	}

	public void fetchGroups() {
		pollServer(Calls.HTTP_GET_GROUPS_4_GAME, jsonGameId());
	}

	public void touchGroupForPlayer() {
		pollServer(Calls.HTTP_TOUCH_GROUP_4_PLAYER, jsonGameId());
	}

	public void fetchGroupForPlayer() {
		pollServer(Calls.HTTP_GET_GROUP_4_PLAYER, jsonGameId());
	}

	public void setPlayerGroupId(long group_id) {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("group_id", group_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_SET_PLAYER_GROUP, jsonParams);
	}

	public void fetchTriggers() {
		pollServer(Calls.HTTP_GET_TRIGGERS_4_GAME, jsonGameId());
	}

	public void fetchTriggerById(long trigger_id) {
		pollServer(Calls.HTTP_GET_TRIGGER, longToJSONReqParam("trigger_id", trigger_id));
	}

	public void fetchTriggersForPlayer() {
		JSONObject jsonParams = jsonGameId();
		try {
			jsonParams.put("tick_factories", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_GET_TRIGGERS_4_PLAYER, jsonParams);
	}

	public void fetchOverlays() {
		pollServer(Calls.HTTP_GET_OVERLAYS_4_GAME, jsonGameId());

	}

	public void fetchOverlaysForPlayer() {
		pollServer(Calls.HTTP_GET_OVERLAYS_4_PLAYER, jsonGameId());
	}

	public void fetchTabs() {
		pollServer(Calls.HTTP_GET_TABS_4_GAME, jsonGameId());
	}

	public void fetchTabsForPlayer() {
		pollServer(Calls.HTTP_GET_TABS_4_PLAYER, jsonGameId());
	}

	public void fetchWebPages() {
		pollServer(Calls.HTTP_GET_WEB_PAGES_4_GAME, jsonGameId());
	}

	public void fetchUsers() {
		pollServer(Calls.HTTP_GET_USERS_4_GAME, jsonGameId());
	}

	public void fetchUserById(long user_id) {
		pollServer(Calls.HTTP_GET_USER, longToJSONReqParam("user_id", user_id));
	}

	public void fetchPlaques() {
		pollServer(Calls.HTTP_GET_PLAQUES_4_GAME, jsonGameId());
	}

	public void fetchScenes() {
		pollServer(Calls.HTTP_GET_SCENES_4_GAME, jsonGameId());
	}

	public void touchSceneForPlayer() {
		pollServer(Calls.HTTP_TOUCH_SCENE_4_PLAYER, jsonGameId());
	}

	public void fetchSceneForPlayer() {
		pollServer(Calls.HTTP_GET_SCENE_4_PLAYER, jsonGameId());
	}

	public void setPlayerSceneId(long scene_id) {
		JSONObject jsonArgs = jsonGameId();
		// add the scene ID to params
		try {
			jsonArgs.put("scene_id", scene_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(Calls.HTTP_SET_PLAYER_SCENE, jsonArgs);
	}
	public JSONObject mJsonAuth;

	public JSONObject jsonGameId() {
		JSONObject jsonGameID = new JSONObject();
		try {
			jsonGameID.put("game_id", mGamePlayAct.mGame.game_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonGameID;
	}

	public void pollServer(final String requestApi, JSONObject jsonMain) {
//		showProgress(true);
//		JSONObject jsonMain = new JSONObject();

		RequestParams rqParams = new RequestParams();

		final Context context = mGamePlayAct;
		String request_url = AppConfig.SERVER_URL_MOBILE + requestApi;

		mGamePlayAct.mPlayer.location = AppUtils.getGeoLocation(context);

		rqParams.put("request", requestApi);
		StringEntity entity;
		entity = null;

		try {
			// place the auth block.
			jsonMain.put("auth", mGamePlayAct.mJsonAuth);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			entity = new StringEntity(jsonMain.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Post the request
		// 	post data should look like this: {"auth":{"user_id":1,"key":"F7...yzX4"},"game_id":"6"}
		// fixme: OutOfMemoryError here occasionally during activiy loop. Some way to check and defer or cancel this Rq if memory too low?
		if (AppUtils.isNetworkAvailable(mGamePlayAct.getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();
//			static String reqCall
//			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + "AsyncHttpClient Sending Req: " + request_url );
			Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D1, getClass().getSimpleName() + "AsyncHttpClient Sending Req: " + request_url + "Params: " + jsonMain.toString());
//			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient Sending Req: " + request_url + "Params: " + jsonMain.toString());
			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
//					showProgress(false);
					try {
						mGamePlayAct.mResposeHandler.processJsonHttpResponse(requestApi, AppConfig.TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.w(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
//					showProgress(false);
					Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
				@Override
				public void onProgress(int remaining, int total) {
//					Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient Progress for Req: " + requestApi + ". Progress: " + remaining + "/" + total);
					// todo: set up progress bars of some sort for each request.
				}
			});
		}
		else {
			Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "You are not connected to the internet currently. Please try again later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}

	}

	public JSONObject longToJSONReqParam(String key, long l) {
		JSONObject jsonReqParam = new JSONObject();
		try {
			jsonReqParam.put(key, l);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonReqParam;
	}


}
