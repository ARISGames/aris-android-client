package edu.uoregon.casls.aris_android;

import android.app.ActivityOptions;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Calls;
import edu.uoregon.casls.aris_android.Utilities.Config;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Event;
import edu.uoregon.casls.aris_android.data_objects.Factory;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Group;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Note;
import edu.uoregon.casls.aris_android.data_objects.NoteComment;
import edu.uoregon.casls.aris_android.data_objects.ObjectTag;
import edu.uoregon.casls.aris_android.data_objects.Overlay;
import edu.uoregon.casls.aris_android.data_objects.Plaque;
import edu.uoregon.casls.aris_android.data_objects.Quest;
import edu.uoregon.casls.aris_android.data_objects.Scene;
import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.data_objects.Tag;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.data_objects.User;
import edu.uoregon.casls.aris_android.data_objects.WebPage;

public class GamePlayActivity extends ActionBarActivity
		implements GamePlayNavDrawerFragment.NavigationDrawerCallbacks, GamePlayMapFragment.OnFragmentInteractionListener {

// Todo 9.29.15: Need to see what happens now when the game tries to load, and then set about setting up the cyclic app status calls

	private final static String TAG_SERVER_SUCCESS = "success";
	public Bundle mTransitionAnimationBndl;
	public User mPlayer; // Sanity note: Now that the game is "playing" we will refer to the logged in User as "Player"
	public Game mGame;
	private View mProgressView; // todo: install a progress spinner for server delays
	public JSONObject mJsonAuth;
	public Map<Long, Media> mGameMedia = new LinkedHashMap<>();
	public Map<String, User> mGameUsers = new LinkedHashMap<>();

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private GamePlayNavDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		mNavigationDrawerFragment = (GamePlayNavDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		Gson gson = new Gson();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mPlayer = new User(extras.getString("user")); // we're now a "Player", BTW.
			//GSON (Slow in debug mode. Ok in regular run mode)
			mGame = gson.fromJson(extras.getString("game"), Game.class);
			mGame.setContext(this); // to allow upward visibility to activities various game/player objects

			try {
				mJsonAuth = new JSONObject(extras.getString("json_auth"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();

		// initialize game object's inner classes and variables.
		mGame.getReadyToPlay();
		// Start barrage of game related server requests
		getGameDataFromServer();
		// Set up the drawer. todo: move this to processServerResponse() for call getTabsForPlayer
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	private void getGameDataFromServer() {
		// here are all the calls made from iOS on starting or resuming a game:
		JSONObject jsonGameID = new JSONObject();
		JSONObject jsonAddlData = new JSONObject();
		try {
			jsonGameID.put("game_id", mGame.game_id);
			jsonAddlData.put("game_id", mGame.game_id);
			jsonAddlData.put("owner_id", 0); // todo: is this always zero for getInstanceForGame?
		} catch (JSONException e) {
			e.printStackTrace();
		}

		pollServer(Calls.HTTP_GET_SCENES_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_TOUCH_SCENE_4_PLAYER, jsonGameID); 
		pollServer(Calls.HTTP_GET_PLAQUES_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_GROUPS_4_GAME, jsonGameID);
		pollServer(Calls.HTTP_GET_ITEMS_4_GAME, jsonGameID);
		pollServer(Calls.HTTP_TOUCH_ITEMS_4_PLAYER, jsonGameID); 
		pollServer(Calls.HTTP_GET_DIALOGS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_DIALOG_CHARS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_WEB_PAGES_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_NOTES_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_NOTE_COMMNTS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_TAGS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_OBJ_TAGS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_EVENTS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_QUESTS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_TRIGGERS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_FACTORIES_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_OVERLAYS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_INSTANCES_4_GAME, jsonAddlData); 
		pollServer(Calls.HTTP_GET_TABS_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_MEDIA_4_GAME, jsonGameID); 
		pollServer(Calls.HTTP_GET_USERS_4_GAME, jsonGameID); 
	}

	public void dropItem(long item_id, long qty) {

	}

	private void pollServer(final String requestApi, JSONObject jsonMain) {
//		showProgress(true);
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = Config.SERVER_URL_MOBILE + requestApi;

		mPlayer.location = AppUtils.getGeoLocation(context);

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

		// Post the request
		// 	post data should look like this: {"auth":{"user_id":1,"key":"F7...yzX4"},"game_id":"6"}
		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();

			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
//					showProgress(false);
					try {
						processJsonHttpResponse(requestApi, TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.w(Config.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
//					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(), "You are not connected to the internet currently. Please try again later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}

	}

	private void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) throws JSONException {
		Log.d(Config.LOGTAG, getClass().getSimpleName() + " Server response to Req: " + callingReq + "; data: " + jsonReturn.toString());
		if (jsonReturn.has("returnCode") && jsonReturn.getLong("returnCode") == 0) {
			if (callingReq.contentEquals(Calls.HTTP_GET_SCENES_4_GAME)) { // parse array of returns scenes
				// Response looks like this:
				// {"data":[{"scene_id":"98","game_id":"78","name":"James J Hill","description":"","editor_x":"0","editor_y":"0"}],"returnCode":0,"returnCodeDescription":null}
				Log.d(Config.LOGTAG, getClass().getSimpleName() + "Landed successfully in colling Req: " + callingReq);
				try {
					// process incoming json data
					if (jsonReturn.has("data")) {
						JSONArray jsonScenes = jsonReturn.getJSONArray("data");
						Gson gson = new Gson();
						for (int i = 0; i < jsonScenes.length(); i++) {
							String jsonSceneStr = jsonScenes.getJSONObject(i).toString();
							Scene scene = gson.fromJson(jsonSceneStr, Scene.class);
							//populate hashmap as <scene_id, Scene Obj>

							mGame.scenesModel.scenes.put(scene.scene_id, scene); // in iOS the object is added in the class itself
							// tell the game class that we got one of the 27 required pieces.
							// serving the function that the iOS "MODEL_GAME_PLAYER_PIECE_AVAILABLE" message would have.
//							if (!mGame.playerDataReceived) mGame.gamePlayerPieceReceived();
						}
						mGame.scenesModel.scenesReceived();
					}
				} catch (JSONException e) {
					Log.e(Config.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + callingReq + " Error reported was: " + e.getCause());
					e.printStackTrace();
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_SCENE_4_PLAYER)) {
				if (jsonReturn.has("data")) {
					JSONObject jsonData = jsonReturn.getJSONObject("data"); // is there any data?
					mGame.scenesModel.sceneTouched();
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_PLAQUES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Plaque plaque = gson.fromJson(dataStr, Plaque.class);
						//populate hashmap as <plaque_id, Plaque Obj>
						mGame.plaquesModel.plaques.put(plaque.plaque_id, plaque);
					}
					mGame.plaquesModel.plaquesReceived();
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_GROUPS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Group group = gson.fromJson(dataStr, Group.class);
						//populate hashmap as <plaque_id, Plaque Obj>
						mGame.groupsModel.groups.put(group.group_id, group);
					}
					mGame.plaquesModel.plaquesReceived();
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_ITEMS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Item item = gson.fromJson(dataStr, Item.class);
						//populate hashmap as <plaque_id, Plaque Obj>
						mGame.itemsModel.items.put(item.item_id, item);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_ITEMS_4_PLAYER)) {
				// is there any return data for this, or just acknowlegment?

				// call PlayerInstancesModel.playerInstancesTouched()?
//				mGame.playerInstancesModel.touchPlayerInstances(); // in iOS, this winds up calling Game.gamePieceReceived(); we'll do that directly:
				if (!mGame.gameDataReceived) mGame.gamePieceReceived(); // Oddity: calls gamePieceReceived rather than gamePlayerPieceReceived "but IS a game-level fetch"
				if (jsonReturn.has("data")) {
//					JSONObject jsonData = jsonReturn.getJSONObject("data"); //do nothing; no data.
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Dialog dialog = gson.fromJson(dataStr, Dialog.class);
						//populate hashmap as dialog_id, Dialog Obj>
						mGame.dialogsModel.dialogs.put(dialog.dialog_id, dialog);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_CHARS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogCharacter dialogChar = gson.fromJson(dataStr, DialogCharacter.class);
						//populate hashmap as dialogChars_id, DialogCharacter Obj>
						mGame.dialogsModel.dialogCharacters.put(dialogChar.dialog_character_id, dialogChar);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogScript dialogScript = gson.fromJson(dataStr, DialogScript.class);
						//populate hashmap as dialogScript_id, DialogScript Obj>
						mGame.dialogsModel.dialogScripts.put(dialogScript.dialog_character_id, dialogScript);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogOption dialogOption = gson.fromJson(dataStr, DialogOption.class);
						//populate hashmap as dialog_option_id, DialogOption Obj>
						mGame.dialogsModel.dialogOptions.put(dialogOption.dialog_option_id, dialogOption);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_WEB_PAGES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						WebPage webpage = gson.fromJson(dataStr, WebPage.class);
						//populate hashmap as dialog_id, Dialog Obj>
						mGame.webPagesModel.webpages.put(webpage.web_page_id, webpage);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_SCENE_4_PLAYER)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Note note = gson.fromJson(dataStr, Note.class);
						//populate hashmap as note_id, Note Obj>
						mGame.notesModel.notes.put(note.note_id, note);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_NOTE_COMMNTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						NoteComment noteCmnt = gson.fromJson(dataStr, NoteComment.class);
						//populate hashmap as Note_comment_id, NoteComment Obj>
						mGame.notesModel.noteComments.put(noteCmnt.note_comment_id, noteCmnt); // todo: are these indexed by note id or note_comment_id?
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TAGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Tag tag = gson.fromJson(dataStr, Tag.class);
						//populate hashmap as tag_id, Tag Obj>
						mGame.tagsModel.tags.put(tag.tag_id, tag);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_OBJ_TAGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						ObjectTag objTag = gson.fromJson(dataStr, ObjectTag.class);
						//populate hashmap as object_tag_id, ObjectTag Obj>
						mGame.tagsModel.objectTags.put(objTag.object_tag_id, objTag);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_EVENTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Event event = gson.fromJson(dataStr, Event.class);
						//populate hashmap as event_id, Event Obj>
						mGame.eventsModel.events.put(event.event_id, event);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_QUESTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Quest quest = gson.fromJson(dataStr, Quest.class);
						//populate hashmap as quest_id, Quest Obj>
						mGame.questsModel.quests.put(quest.quest_id, quest);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TRIGGERS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Trigger trigger = gson.fromJson(dataStr, Trigger.class);
						//populate hashmap as trigger_id, Quest Obj>
						mGame.triggersModel.triggers.put(trigger.trigger_id, trigger);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_FACTORIES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Factory factory = gson.fromJson(dataStr, Factory.class);
						//populate hashmap as factory_id, Factory Obj>
						mGame.factoriesModel.factories.put(factory.factory_id, factory);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_OVERLAYS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Overlay overlay = gson.fromJson(dataStr, Overlay.class);
						//populate hashmap as overlayr_id, Overlay Obj>
						mGame.overlaysModel.overlays.put(overlay.overlay_id, overlay);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_INSTANCES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Instance instance = gson.fromJson(dataStr, Instance.class);
						//populate hashmap as instances_id, Instance Obj>
						mGame.instancesModel.instances.put(instance.instance_id, instance);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TABS_4_GAME)) { // returns array of teh items for the game mode drawer
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Tab tab = gson.fromJson(dataStr, Tab.class);
						//populate hashmap as tab_id, Tab Obj>
						mGame.tabsModel.tabs.put(tab.tab_id, tab);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_MEDIA_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					// Note: this model works differently, at least in iOS: it broadcasts the receipt event to the actual Media model.
					// See MediaModel.m (in iOS). Follows this method flow from listener: initWithContext->mediasReceived->ipdateMedias

					// Not sure at the point of this coding why this differs, and as there is not "mediasModel: class in iOS,
					// I'm going to just populate the media objects in as a class var (array of model obj) of this GamePlayActivity.
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Media media = gson.fromJson(dataStr, Media.class);
						//populate hashmap as media_id, Media Obj>
						mGameMedia.put(media.media_id, media); // may wish to move this array into Game.class; don't see a particularly good reason to store it locally
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_USERS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						User user = gson.fromJson(dataStr, User.class);
						//populate hashmap as user_id, User Obj>
						mGameUsers.put(user.user_id, user);
						if (!mGame.gameDataReceived) mGame.gamePieceReceived();
					}
				}
			}
			else if (callingReq.equals("")) { // stub
				if (jsonReturn.has("data")) {
					JSONObject jsonData = jsonReturn.getJSONObject("data");
				}
			}
			else { // unknown callinRequest
				Log.e(Config.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient returned successfully but with unhandled server callingReq: " + callingReq);
				Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
						Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();

			}
		}
		else { // server denial. Probably need to alert user (?)
			Log.e(Config.LOGTAG, getClass().getSimpleName() + "Server request " + callingReq + " failed; server returned code: " + jsonReturn.getLong("returnCode")
					+ "\nPlayer Id: " + mPlayer.user_id
					+ "\nGame Id: " + mGame.game_id);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {
			case 0:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayQuestsFragment.newInstance(position + 1))
						.commit();
				break;
			case 1:
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayMapFragment.newInstance(position + 1))
						.commit();
				break;
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(String itemName) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		if (itemName.equals("Quests")) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, GamePlayQuestsFragment.newInstance(itemName))
					.commit();
		}
		else if (itemName.equals("Map")) {
				fragmentManager.beginTransaction()
						.replace(R.id.container, GamePlayMapFragment.newInstance(itemName))
						.commit();
		}
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

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
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

	@Override
	public void onFragmentInteraction(Uri uri) {
		Uri u = uri;
	}

	@Override
	public void onSecondFragButtonClick(String message) {
		String gotit = message;
	}

	public void fetchNoteById(long object_id) {
		JSONObject jsonAddlData = new JSONObject();
		try {
			jsonAddlData.put("note_id", mGame.game_id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		pollServer(Calls.HTTP_GET_NOTE, jsonAddlData);
	}

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
