package edu.uoregon.casls.aris_android.Utilities;

import android.content.Context;
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

import java.io.UnsupportedEncodingException;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.Tag;
import edu.uoregon.casls.aris_android.data_objects.Trigger;

/**
 * Created by smorison on 10/6/15.
 */
public class Services {


	public transient GamePlayActivity mGamePlayAct;
//	public transient Game mGame;
//	public User mPlayer;

	public Services(GamePlayActivity gamePlayActivity) {
		initContext(gamePlayActivity);
	}

	public Services() {
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		//convenience references:
//		mGame = mGamePlayAct.mGame;
//		mPlayer = mGamePlayAct.mPlayer;
	}

	public void fetchInstances() {
		pollServer(Calls.HTTP_GET_INSTANCES_4_GAME, getJsonGameId()); //could leave out ownerId and get same result, but would rather be explicit -iOS comment
	}

	public void fetchInstanceById(long i) {

	}


	public void fetchInstancesForPlayer() {

	}

	public void setQtyForInstanceId(long instance_id, long qty) {

	}

	public void fetchOptionsForPlayerForDialog(long dialog_id, long dialog_script_id) {

	}

	public void fetchRequirementAtoms() {
		pollServer(Calls.HTTP_GET_REQ_ATOMS_4_GAME, getJsonGameId()); // todo needs Response condition
	}

	public void fetchRequirementAnds() {
		pollServer(Calls.HTTP_GET_REQ_AND_PKGS_4_GAME, getJsonGameId()); // todo needs Response condition
	}

	public void fetchRequirementRoots() {
		pollServer(Calls.HTTP_GET_REQ_ROOT_PKGS_4_GAME, getJsonGameId()); // todo needs Response condition
	}

	public void touchItemsForGame() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_GAME, getJsonGameId());
	}

	public void touchItemsForPlayer() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_PLAYER, getJsonGameId());
	}

	public void touchItemsForGroups() {
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_GROUPS, getJsonGameId());
	}

	public void fetchItems() {
		pollServer(Calls.HTTP_GET_ITEMS_4_GAME, getJsonGameId());
	}

	public void dropItem(long item_id, long qty) {

	}

	public void fetchLogsForPlayer() {
		pollServer(Calls.HTTP_GET_LOGS_4_PLAYER, getJsonGameId());
	}

	public void logPlayerEnteredGame() {

	}

	public void logPlayerMoved() {

	}

	public void logPlayerViewedTabId(long tab_id) {

	}

	public void logPlayerViewedPlaqueId(long content_id) {
	}

	public void logPlayerViewedItemId(long content_id) {
	}

	public void logPlayerViewedDialogId(long content_id) {
	}

	public void logPlayerViewedDialogScriptId(long content_id) {
	}

	public void logPlayerViewedWebPageId(long content_id) {
	}

	public void logPlayerViewedNoteId(long content_id) {
	}

	public void logPlayerViewedSceneId(long content_id) {
	}

	public void logPlayerViewedInstanceId(long instance_id) {
	}

	public void logPlayerTriggeredTriggerId(long trigger_id) {
	}

	public void logPlayerReceivedItemId(long item_id, long qty) {
	}

	public void logPlayerLostItemId(long item_id, long qty) {
	}

	public void logGameReceivedItemId(long item_id, long qty) {
	}

	public void logGameLostItemId(long item_id, long qty) {
	}

	public void logGroupReceivedItemId(long item_id, long qty) {
	}

	public void logGroupLostItemId(long item_id, long qty) {
	}

	public void logPlayerSetSceneId(long scene_id) {
	}

	public void logPlayerJoinedGroupId(long group_id) {
	}

	public void logPlayerRanEventPackageId(long event_package_id) {
	}

	public void fetchDialogs() {
		pollServer(Calls.HTTP_GET_DIALOGS_4_GAME, getJsonGameId());
	}

	public void fetchDialogCharacters() {
		pollServer(Calls.HTTP_GET_DIALOG_CHARS_4_GAME, getJsonGameId());
	}

	public void fetchDialogScripts() {
		pollServer(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME, getJsonGameId());
	}

	public void fetchDialogOptions() {
		pollServer(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME, getJsonGameId());
	}

	public void fetchQuestsForPlayer() {
		pollServer(Calls.HTTP_GET_QUESTS_4_PLAYER, getJsonGameId());
	}

	public void fetchQuests() {
		pollServer(Calls.HTTP_GET_QUESTS_4_GAME, getJsonGameId());
	}

	public void fetchTags() {
		pollServer(Calls.HTTP_GET_TAGS_4_GAME, getJsonGameId());
	}

	public void fetchObjectTags() {
		pollServer(Calls.HTTP_GET_OBJ_TAGS_4_GAME, getJsonGameId());
	}

	public void fetchMedias() {
	}

	public void createNote(Note n, Tag t, Media m, Trigger tr) {
	}

	public void updateNote(Note n, Tag t, Media m, Trigger tr) {
	}

	public void deleteNoteId(long note_id) {
	}

	public void fetchNotes() {
		pollServer(Calls.HTTP_GET_NOTES_4_GAME, getJsonGameId());
	}

	public void fetchNoteComments() {
		pollServer(Calls.HTTP_GET_NOTE_COMMNTS_4_GAME, getJsonGameId());
	}

	public void createNoteComment(NoteComment n) {
	}

	public void updateNoteComment(NoteComment n) {
	}

	public void deleteNoteCommentId(long note_comment_id) {
	}

	public void fetchEvents() {
		pollServer(Calls.HTTP_GET_EVENTS_4_GAME, getJsonGameId());
	}

	public void fetchFactories() {
		pollServer(Calls.HTTP_GET_FACTORIES_4_GAME, getJsonGameId());
	}

	public void fetchGroups() {
		pollServer(Calls.HTTP_GET_GROUPS_4_GAME, getJsonGameId());
	}

	public void touchGroupForPlayer() {
		pollServer(Calls.HTTP_TOUCH_GROUP_4_PLAYER, getJsonGameId());
	}

	public void fetchGroupForPlayer() {
	}

	public void setPlayerGroupId(long group_id) {

	}

	public void fetchTriggers() {
		pollServer(Calls.HTTP_GET_TRIGGERS_4_GAME, getJsonGameId());
	}

	public void fetchTriggerById(long t) {
	}

	public void fetchTriggersForPlayer() {
		pollServer(Calls.HTTP_GET_TRIGGERS_4_PLAYER, getJsonGameId());
	}

	public void fetchOverlays() {
		pollServer(Calls.HTTP_GET_OVERLAYS_4_GAME, getJsonGameId());

	}

	public void fetchOverlaysForPlayer() {
		pollServer(Calls.HTTP_GET_OVERLAYS_4_PLAYER, getJsonGameId());
	}

	public void fetchTabs() {
		pollServer(Calls.HTTP_GET_TABS_4_GAME, getJsonGameId());
	}

	public void fetchTabsForPlayer() {
		pollServer(Calls.HTTP_GET_TABS_4_PLAYER, getJsonGameId());
	}

	public void fetchWebPages() {
		pollServer(Calls.HTTP_GET_WEB_PAGES_4_GAME, getJsonGameId());
	}

	public void fetchUsers() {
	}

	public void fetchUserById(long t) {
	}

	public void fetchPlaques() {
		pollServer(Calls.HTTP_GET_PLAQUES_4_GAME, getJsonGameId());
	}

	public void fetchScenes() {
		pollServer(Calls.HTTP_GET_SCENES_4_GAME, getJsonGameId());
	}

	public void touchSceneForPlayer() {
	}

	public void fetchSceneForPlayer() {
		pollServer(Calls.HTTP_GET_SCENE_4_PLAYER, getJsonGameId());
	}


	private final static String TAG_SERVER_SUCCESS = "success";
	public JSONObject mJsonAuth;

	public JSONObject getJsonGameId() {
		JSONObject jsonGameID = new JSONObject();
		try {
			jsonGameID.put("game_id", mGamePlayAct.mGame.game_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonGameID;
	}

	public JSONObject getJsonGameIDAndOwnerId() {
		JSONObject jsonAddlData = new JSONObject();
		try {
			jsonAddlData.put("game_id", mGamePlayAct.mGame.game_id);
			jsonAddlData.put("owner_id", 0); // todo: is this always zero for getInstanceForGame?
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonAddlData;
	}

	public void pollServer(final String requestApi, JSONObject jsonMain) {
//		showProgress(true);
//		JSONObject jsonMain = new JSONObject();

		RequestParams rqParams = new RequestParams();

		final Context context = mGamePlayAct;
		String request_url = Config.SERVER_URL_MOBILE + requestApi;

		mGamePlayAct.mPlayer.location = AppUtils.getGeoLocation(context);

		rqParams.put("request", requestApi);
		StringEntity entity;
		entity = null;
		JSONObject jsonAuth = new JSONObject();

		try {
			// place the auth block.
			jsonMain.put("auth", mJsonAuth);
			//place additional required params
//			switch (requestApi) {
//				case (HTTP_GET_NEARBY_GAMES_REQ_API):
//					break;
//				case (HTTP_GET_POPULAR_GAMES_REQ_API):
//					//sample: {"interval":"WEEK","longitude":"-89.409260","user_id":"1","latitude":"43.073128","page":0,"auth":{"user_id":1,"key":"F7...X4"}}
//					break;
//				case (HTTP_GET_PLAYER_GAMES_REQ_API):
//				case (HTTP_GET_RECENT_GAMES_REQ_API): // get player and get recent use the same Req param set.
//					break;
//				case (HTTP_GET_SEARCH_GAMES_REQ_API):
//					break;
//				case (HTTP_GET_FULL_GAME_REQ_API):
//					break;
//				default:
//					break;
//			}

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
		if (AppUtils.isNetworkAvailable(mGamePlayAct.getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();

			Log.d(Config.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient Sending Req: " + request_url);
			Log.d(Config.LOGTAG,  getClass().getSimpleName() + "AsyncHttpClient Params for Req: " + jsonMain.toString());
			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
//					showProgress(false);
					try {
//						processJsonHttpResponse(requestApi, TAG_SERVER_SUCCESS, jsonReturn);
						mGamePlayAct.mResposeHandler.processJsonHttpResponse(requestApi, TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.w(Config.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
//					showProgress(false);
					Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, responseString, throwable);
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
}
