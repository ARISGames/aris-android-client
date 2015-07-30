package edu.uoregon.casls.aris_android.data_objects;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uoregon.casls.aris_android.AppUtils;
import edu.uoregon.casls.aris_android.GamesList;

/*
  Created by smorison on 7/28/15.
 */
public class Game {

	private static final String HTTP_GET_FULL_GAME_REQ_API = "v2.games.getFullGame/";
	long game_id;
	String name;
	String desc;
	String tick_script;
	long tick_delay;
	boolean published;
	String type;
	Location location = new Location("0");
	long player_count;

	long icon_media_id;
	long media_id;

	long intro_scene_id;

	List<User> authors = new ArrayList<User>();
//	List<User> Comment = new ArrayList<Comment>();

	String map_type;
	//		Location map_location; // from iOS; not used
	double map_zoom_level;
	boolean map_show_player;
	boolean map_show_players;
	boolean map_offsite_mode;

	boolean notebook_allow_comments;
	boolean notebook_allow_likes;
	boolean notebook_allow_player_tags;

	long inventory_weight_cap;

	Context mContext;

	// Supporting classes
//		ScenesModel     scenesModel;
//		PlaquesModel    plaquesModel;
//		ItemsModel      itemsModel;
//		DialogsModel    dialogsModel;
//		WebPagesModel   webPagesModel;
//		NotesModel      notesModel;
//		TagsModel       tagsModel;
//		EventsModel     eventsModel;
//		TriggersModel   triggersModel;
//		FactoriesModel  factoriesModel;
//		OverlaysModel   overlaysModel;
//		InstancesModel  instancesModel;
//		PlayerInstancesModel  playerInstancesModel;
//		TabsModel       tabsModel;
//		LogsModel       logsModel;
//		QuestsModel     questsModel;
//		DisplayQueueModel displayQueueModel;

	// Basic Constructor
	public Game(JSONObject mJsonAuth, JSONObject jsonGame) throws JSONException {
		initWithJson(jsonGame);
	}

	// Constructor for use with internal HTTP calls which need the context
	public Game(final Context context, final JSONObject jsonAuth, JSONObject jsonGame) throws JSONException {

		mContext = context;
		initWithJson(jsonGame);
		// get full game // Turned out to be problematic - ASYNC calls would return after object was returned
//		pollServer(context, jsonAuth, HTTP_GET_FULL_GAME_REQ_API); // moving FullGame call to GamesList
	}

	// stub for future possible use.
	public Game(final Context context, final HashMap<String, String> hmapAuth, HashMap<String, String> hmapGame) {
//		initWithMap(hmapGame);
		// get full game
//		pollServer(context, hmapAuth, HTTP_GET_FULL_GAME_REQ_API);
	}

	private void initWithJson(JSONObject jsonGame) throws JSONException {
		if (jsonGame.has("game_id") && !jsonGame.getString("game_id").contentEquals("null"))
			game_id = Long.parseLong(jsonGame.getString("game_id"));
		if (jsonGame.has("name"))
			name = jsonGame.getString("name");
		if (jsonGame.has("description"))
			desc = jsonGame.getString("description");
		if (jsonGame.has("tick_script"))
			tick_script = jsonGame.getString("tick_script");
		if (jsonGame.has("tick_delay") && !jsonGame.getString("tick_delay").contentEquals("null"))
			tick_delay = Long.parseLong(jsonGame.getString("tick_delay"));
		if (jsonGame.has("icon_media_id") && !jsonGame.getString("icon_media_id").contentEquals("null"))
			icon_media_id = Long.parseLong(jsonGame.getString("icon_media_id"));
		if (jsonGame.has("media_id") && !jsonGame.getString("media_id").contentEquals("null"))
			media_id = Long.parseLong(jsonGame.getString("media_id"));
		if (jsonGame.has("map_type"))
			map_type = jsonGame.getString("map_type");
		if (jsonGame.has("map_latitude") && !jsonGame.getString("map_latitude").contentEquals("null"))
			location.setLatitude(Double.parseDouble(jsonGame.getString("map_latitude")));
		if (jsonGame.has("map_longitude") && !jsonGame.getString("map_longitude").contentEquals("null"))
			location.setLongitude(Double.parseDouble(jsonGame.getString("map_longitude")));
		if (jsonGame.has("map_zoom_level") && !jsonGame.getString("map_zoom_level").contentEquals("null"))
			map_zoom_level = Double.parseDouble(jsonGame.getString("map_zoom_level"));
		if (jsonGame.has("map_show_player") && !jsonGame.getString("map_show_player").contentEquals("null"))
			map_show_player = Boolean.parseBoolean(jsonGame.getString("map_show_player"));
		if (jsonGame.has("map_show_players") && !jsonGame.getString("map_show_players").contentEquals("null"))
			map_show_players = Boolean.parseBoolean(jsonGame.getString("map_show_players"));
		if (jsonGame.has("map_offsite_mode") && !jsonGame.getString("map_offsite_mode").contentEquals("null"))
			map_offsite_mode = Boolean.parseBoolean(jsonGame.getString("map_offsite_mode"));
		if (jsonGame.has("notebook_allow_comments") && !jsonGame.getString("notebook_allow_comments").contentEquals("null"))
			notebook_allow_comments = Boolean.parseBoolean(jsonGame.getString("notebook_allow_comments"));
		if (jsonGame.has("notebook_allow_likes") && !jsonGame.getString("notebook_allow_likes").contentEquals("null"))
			notebook_allow_likes = Boolean.parseBoolean(jsonGame.getString("notebook_allow_likes"));
		if (jsonGame.has("notebook_allow_player_tags") && !jsonGame.getString("notebook_allow_player_tags").contentEquals("null"))
			notebook_allow_player_tags = Boolean.parseBoolean(jsonGame.getString("notebook_allow_player_tags"));
		if (jsonGame.has("published") && !jsonGame.getString("published").contentEquals("null"))
			published = Boolean.parseBoolean(jsonGame.getString("published"));
		if (jsonGame.has("type"))
			type = jsonGame.getString("type");
		if (jsonGame.has("intro_scene_id") && !jsonGame.getString("intro_scene_id").contentEquals("null"))
			intro_scene_id = Long.parseLong(jsonGame.getString("intro_scene_id"));
		if (jsonGame.has("player_count") && !jsonGame.getString("player_count").contentEquals("null"))
			player_count = Long.parseLong(jsonGame.getString("player_count"));

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
		JSONObject jsonGameData = jsonFullGame.getJSONObject("data");
		if (jsonGameData.has("inventory_weight_cap") && !jsonGameData.getString("inventory_weight_cap").contentEquals("null"))
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

		// where do all these come into play???
		// upon search, none of these are utilized, or even mentioned in, in the iOS code
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

	private void pollServer(final Context context, JSONObject jsonAuth, final String request_api) {
		RequestParams rqParams = new RequestParams();

//		final Context context = this;
		String request_url = AppUtils.SERVER_URL_MOBILE + request_api;

		rqParams.put("request", request_api);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		try {

			jsonMain.put("game_id", game_id);
			jsonMain.put("auth", jsonAuth);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(AppUtils.LOGTAG, "Json string Req to server: " + jsonMain);

		try {
			entity = new StringEntity(jsonMain.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/*
		client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
		 */
		// post data should look like this: {"password":"123123","permission":"read_write","user_name":"scott"}
		if (AppUtils.isNetworkAvailable(context)) { // force logic. assuming that netwk is currently availbl.
			AsyncHttpClient client = new AsyncHttpClient();

			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
//					showProgress(false);
					try {
						processJsonHttpResponse(request_api, AppUtils.TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppUtils.LOGTAG, "AsyncHttpClient failed server call. ", throwable);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		else {
			// log it.
		}
	}

	private void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) throws JSONException {
		Log.i(AppUtils.LOGTAG, "Landed successfully in colling Req: " + callingReq);
		try {
			// process incoming json data
			if (jsonReturn.has("data")) {
				initFullGameDetailsWithJson(jsonReturn);
			}
		} catch (JSONException e) {
			Log.e(AppUtils.LOGTAG, "Failed while parsing returning JSON from request:" + callingReq + " Error reported was: " + e.getCause());
			e.printStackTrace();
		}
	}
}