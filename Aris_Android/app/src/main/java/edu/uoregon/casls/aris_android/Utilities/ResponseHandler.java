package edu.uoregon.casls.aris_android.Utilities;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Event;
import edu.uoregon.casls.aris_android.data_objects.Factory;
import edu.uoregon.casls.aris_android.data_objects.Group;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
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

/**
 * Created by smorison on 11/3/15.
 */
public class ResponseHandler { // for now only handles responses with respect to GamePlayActivity.
	
	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
	}
	
	public void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) throws JSONException {
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
						List<Scene> scenes = new ArrayList<>();
						for (int i = 0; i < jsonScenes.length(); i++) {
							String jsonSceneStr = jsonScenes.getJSONObject(i).toString();
							Scene scene = gson.fromJson(jsonSceneStr, Scene.class);
							//populate hashmap as <scene_id, Scene Obj>
//							mGamePlayAct.mGame.scenesModel.scenes.put(scene.scene_id, scene); // in iOS the object is added in the class itself
							scenes.add(scene);
							// tell the game class that we got one of the 27 required pieces.
							// serving the function that the iOS "MODEL_GAME_PLAYER_PIECE_AVAILABLE" message would have.
//							if (!mGamePlayAct.mGame.playerDataReceived) mGamePlayAct.mGame.gamePlayerPieceReceived();
						}
						mGamePlayAct.mGame.scenesModel.scenesReceived(scenes);
					}
				} catch (JSONException e) {
					Log.e(Config.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + callingReq + " Error reported was: " + e.getCause());
					e.printStackTrace();
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_SCENE_4_PLAYER)) {
				if (jsonReturn.has("data")) {
					JSONObject jsonData = jsonReturn.getJSONObject("data"); // is there any data?
					mGamePlayAct.mGame.scenesModel.sceneTouched();
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_PLAQUES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Plaque> plaques = new ArrayList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Plaque plaque = gson.fromJson(dataStr, Plaque.class);
						//populate hashmap as <plaque_id, Plaque Obj>
						plaques.add(plaque);
						mGamePlayAct.mGame.plaquesModel.plaques.put(plaque.plaque_id, plaque);
					}
					mGamePlayAct.mGame.plaquesModel.plaquesReceived(plaques);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_GROUPS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Group> groups = new ArrayList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Group group = gson.fromJson(dataStr, Group.class);
						groups.add(group);
						//populate hashmap as <plaque_id, Plaque Obj>
//						mGamePlayAct.mGame.groupsModel.groups.put(group.group_id, group);
					}
					mGamePlayAct.mGame.groupsModel.groupsReceived(groups);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_ITEMS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Item> newItems = new ArrayList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Item item = gson.fromJson(dataStr, Item.class);
						newItems.add(item);
						//populate hashmap as <plaque_id, Plaque Obj>
//						mGamePlayAct.mGame.itemsModel.items.put(item.item_id, item);
					}
					mGamePlayAct.mGame.itemsModel.itemsReceived(newItems);
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_ITEMS_4_PLAYER)) {
				// is there any return data for this, or just acknowlegment?

				// call PlayerInstancesModel.playerInstancesTouched()?
				mGamePlayAct.mDispatch.services_player_instances_touched(); //_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_INSTANCES_TOUCHED", nil, nil);
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Dialog> dialogs = new ArrayList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Dialog dialog = gson.fromJson(dataStr, Dialog.class);
						//populate hashmap as dialog_id, Dialog Obj>
//						mGamePlayAct.mGame.dialogsModel.dialogs.put(dialog.dialog_id, dialog);
						dialogs.add(dialog);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_dialog_received(dialogs); //_ARIS_NOTIF_SEND_(@"SERVICES_DIALOGS_RECEIVED", nil, @{@"dialogs":dialogs});
//					mGamePlayAct.mGame.dialogsModel.dialogsReceived(); // called directly rather than through Dispatch?
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_CHARS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<DialogCharacter> dialogCharacters = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogCharacter dialogChar = gson.fromJson(dataStr, DialogCharacter.class);
						//populate hashmap as dialogChars_id, DialogCharacter Obj>
//						mGamePlayAct.mGame.dialogsModel.dialogCharacters.put(dialogChar.dialog_character_id, dialogChar);
						dialogCharacters.add(dialogChar);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
//					mGamePlayAct.mGame.dialogsModel.dialogsReceived(dialogs);
					mGamePlayAct.mDispatch.services_dialog_characters_received(dialogCharacters); // _ARIS_NOTIF_SEND_(@"SERVICES_DIALOG_CHARACTERS_RECEIVED", nil, @{@"dialogCharacters":dialogCharacters});
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_SCRIPTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<DialogScript> dialogScripts = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogScript dialogScript = gson.fromJson(dataStr, DialogScript.class);
						//populate hashmap as dialogScript_id, DialogScript Obj>
//						mGamePlayAct.mGame.dialogsModel.dialogScripts.put(dialogScript.dialog_character_id, dialogScript);
						dialogScripts.add(dialogScript);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
//					mGamePlayAct.mGame.dialogsModel.dialogsReceived(dialogs);
					mGamePlayAct.mDispatch.services_dialog_scipts_received(dialogScripts); // _ARIS_NOTIF_SEND_(@"SERVICES_DIALOG_SCRIPTS_RECEIVED", nil, @{@"dialogScripts":dialogScripts});
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_DIALOG_OPTNS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<DialogOption> dialogOptions = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						DialogOption dialogOption = gson.fromJson(dataStr, DialogOption.class);
						//populate hashmap as dialog_option_id, DialogOption Obj>
						dialogOptions.add(dialogOption);
//						mGamePlayAct.mGame.dialogsModel.dialogOptions.put(dialogOption.dialog_option_id, dialogOption);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_dialog_options_received(dialogOptions);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_WEB_PAGES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<WebPage> webPages = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						WebPage webpage = gson.fromJson(dataStr, WebPage.class);
						//populate hashmap as dialog_id, Dialog Obj>
						webPages.add(webpage);
//						mGamePlayAct.mGame.webPagesModel.webpages.put(webpage.web_page_id, webpage);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_web_pages_received(webPages);
				}
			}
			else if (callingReq.equals(Calls.HTTP_TOUCH_SCENE_4_PLAYER)) {
				mGamePlayAct.mDispatch.services_scene_touched(); //_ARIS_NOTIF_SEND_(@"SERVICES_SCENE_TOUCHED", nil, nil);
			}
			else if (callingReq.equals(Calls.HTTP_GET_NOTE_COMMNTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<NoteComment> noteComments = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						NoteComment noteCmnt = gson.fromJson(dataStr, NoteComment.class);
						//populate hashmap as Note_comment_id, NoteComment Obj>
						noteComments.add(noteCmnt);
//						mGamePlayAct.mGame.notesModel.noteComments.put(noteCmnt.note_comment_id, noteCmnt); // todo: are these indexed by note id or note_comment_id?
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_note_comments_received(noteComments);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TAGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Tag> tags = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Tag tag = gson.fromJson(dataStr, Tag.class);
						//populate hashmap as tag_id, Tag Obj>
						tags.add(tag);
//						mGamePlayAct.mGame.tagsModel.tags.put(tag.tag_id, tag);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_tags_received(tags);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_OBJ_TAGS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<ObjectTag> objectTags = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						ObjectTag objTag = gson.fromJson(dataStr, ObjectTag.class);
						//populate hashmap as object_tag_id, ObjectTag Obj>
						objectTags.add(objTag);
//						mGamePlayAct.mGame.tagsModel.objectTags.put(objTag.object_tag_id, objTag);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_object_tags_received(objectTags);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_EVENTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Event> events = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Event event = gson.fromJson(dataStr, Event.class);
						//populate hashmap as event_id, Event Obj>
						events.add(event);
//						mGamePlayAct.mGame.eventsModel.events.put(event.event_id, event);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_events_received(events);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_QUESTS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Quest> quests = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Quest quest = gson.fromJson(dataStr, Quest.class);
						//populate hashmap as quest_id, Quest Obj>
						quests.add(quest);
//						mGamePlayAct.mGame.questsModel.quests.put(quest.quest_id, quest);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_quests_received(quests);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TRIGGERS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Trigger> triggers = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Trigger trigger = gson.fromJson(dataStr, Trigger.class);
						//populate hashmap as trigger_id, Quest Obj>
						triggers.add(trigger);
//						mGamePlayAct.mGame.triggersModel.triggers.put(trigger.trigger_id, trigger);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_triggers_received(triggers);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_FACTORIES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Factory> factories = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Factory factory = gson.fromJson(dataStr, Factory.class);
						//populate hashmap as factory_id, Factory Obj>
						factories.add(factory);
//						mGamePlayAct.mGame.factoriesModel.factories.put(factory.factory_id, factory);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_factories_received(factories);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_OVERLAYS_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Overlay> overlays = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Overlay overlay = gson.fromJson(dataStr, Overlay.class);
						//populate hashmap as overlayr_id, Overlay Obj>
						overlays.add(overlay);
//						mGamePlayAct.mGame.overlaysModel.overlays.put(overlay.overlay_id, overlay);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_overlays_received(overlays);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_INSTANCES_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
//					Gson gson = new Gson();
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
					List<Instance> instances = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Instance instance = gson.fromJson(dataStr, Instance.class);
						//populate hashmap as instances_id, Instance Obj>
						instances.add(instance);
//						mGamePlayAct.mGame.instancesModel.instances.put(instance.instance_id, instance);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_instances_received(instances);
				}
			}
			else if (callingReq.equals(Calls.HTTP_GET_TABS_4_GAME)) { // returns array of teh items for the game mode drawer
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
					List<Tab> tabs = new LinkedList<>();
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Tab tab = gson.fromJson(dataStr, Tab.class);
						//populate hashmap as tab_id, Tab Obj>
						tabs.add(tab);
//						mGamePlayAct.mGame.tabsModel.tabs.put(tab.tab_id, tab);
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
					}
					mGamePlayAct.mDispatch.services_tabs_received(tabs);
				}
			}
//			else if (callingReq.equals(Calls.HTTP_GET_MEDIA_4_GAME)) {
//				if (jsonReturn.has("data")) {
//					JSONArray jsonData = jsonReturn.getJSONArray("data");
//					// Note: this model works differently, at least in iOS: it broadcasts the receipt event to the actual Media model.
//					// See MediaModel.m (in iOS). Follows this method flow from listener: initWithContext->mediasReceived->ipdateMedias
//
//					// Not sure at the point of this coding why this differs, and as there is not "mediasModel: class in iOS,
//					// I'm going to just populate the media objects in as a class var (array of model obj) of this GamePlayActivity.
//					Gson gson = new Gson();
//					for (int i = 0; i < jsonData.length(); i++) {
//						String dataStr = jsonData.getJSONObject(i).toString();
//						Media media = gson.fromJson(dataStr, Media.class);
//						//populate hashmap as media_id, Media Obj>
//						mGameMedia.put(media.media_id, media); // may wish to move this array into Game.class; don't see a particularly good reason to store it locally
//						if (!mGamePlayAct.mGame.gameDataReceived) mGamePlayAct.mGame.gamePieceReceived();
//					}
//					//note that this intentionally only sends the dictionaries, not fully populated Media objects
//					// _ARIS_NOTIF_SEND_(@"SERVICES_MEDIA_RECEIVED", nil, @{@"media":mediaDict}); // fakes an entire list and does same as fetching all media
//				}
//			}
			else if (callingReq.equals(Calls.HTTP_GET_MEDIA_4_GAME)) {
				if (jsonReturn.has("data")) {
					JSONArray jsonData = jsonReturn.getJSONArray("data");
					Gson gson = new Gson();
//					List<Map<String, Object>> rawMediaArr = new LinkedList<>(); // orig.
					List<Map<String, String>> rawMediaArr = new LinkedList<>();
					Log.d(Config.LOGTAG, getClass().getSimpleName() + "Received data from Call, " + Calls.HTTP_GET_MEDIA_4_GAME + "; Return set size = " + rawMediaArr.size());
					for (int i = 0; i < jsonData.length(); i++) {
						String dataStr = jsonData.getJSONObject(i).toString();
						Map<String, String> aMediaRec = gson.fromJson(dataStr, new TypeToken<HashMap<String, Object>>() {}.getType());
						//populate List with this Key->Val pair set. (Map)
						rawMediaArr.add(aMediaRec);
					}
					//note that this intentionally only sends the dictionaries, not fully populated Media objects
					mGamePlayAct.mDispatch.services_medias_received(rawMediaArr); // _ARIS_NOTIF_SEND_(@"SERVICES_MEDIAS_RECEIVED", nil, @{@"medias":mediaDicts}); // fakes an entire list and does same as fetching all media
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
						mGamePlayAct.mGameUsers.put(user.user_id, user);
					}
					mGamePlayAct.mDispatch.services_users_received(mGamePlayAct.mGameUsers); // _ARIS_NOTIF_SEND_(@"SERVICES_USERS_RECEIVED", nil, @{@"users":users});

				}
			}
			else if (callingReq.equals("")) { // stub
				if (jsonReturn.has("data")) {
					JSONObject jsonData = jsonReturn.getJSONObject("data");
				}
			}
			else { // unknown callinRequest
				Log.e(Config.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient returned successfully but with unhandled server callingReq: " + callingReq);
				Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
						Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();

			}
		}
		else { // server denial. Probably need to alert user (?)
			Log.e(Config.LOGTAG, getClass().getSimpleName() + "Server request " + callingReq + " failed; server returned code: " + jsonReturn.getLong("returnCode")
					+ "\nPlayer Id: " + mGamePlayAct.mPlayer.user_id
					+ "\nGame Id: " + mGamePlayAct.mGame.game_id);
		}
	}

}
