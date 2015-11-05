package edu.uoregon.casls.aris_android.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.Config;
import edu.uoregon.casls.aris_android.Utilities.DBDealer;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.cd_data_objects.MediaCD;

/**
 * Created by smorison on 8/20/15.
 */
public class MediaModel extends ARISModel {

	public Map<Long, Media> medias = new LinkedHashMap<>();//light cache on mediaCD wrappers ('Media' objects)
	public List<Integer> mediaIDsToLoad = new LinkedList<>();
	public transient GamePlayActivity mGamePlayAct;
	//	NSManagedObjectContext *context; // for CoreData reference // todo: Android relevant?
//	NSMutableArray *mediaDataLoadDelegateHandles; // todo: Android relevant?
//	NSMutableArray *mediaDataLoadMedia; // todo: Android relevant?
	int mediaDataLoaded;

	private SQLiteDatabase db;
	private DBDealer dbDealer;


	public MediaModel(GamePlayActivity gamePlayActivity) {
		super();
		initContext(gamePlayActivity);
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		if (dbDealer == null)
			dbDealer = new DBDealer(gamePlayAct); // only instantiate once
//		else
//			dbDealer.initContext(mGamePlayAct); // todo: maybe invalidate and recreate with updated context. Or do nothing?
	}

	public void clearGameData() {
		medias.clear();
		mediaIDsToLoad.clear();
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void requestTabs() {
	}

	public void requestPlayerTabs() {

	}

	public long nGameDataToReceive() {
		return 1;
	}

// todo: figure out how to encorporate this iOS voodoo into the android app
	// Essentially this is a database call to a (device) persistent database.
	// The predicate is the SQL to be executed
	// the context (NSManagedObjectContext) is the database
	// The EntityDescription is the table and database to be queried.
	// The FetchRequest encapsulates the whole statement with both the source (ie table) and the predicate (ie SQL expression)
	// The call to executeFetchRequest() will perfom the request and return any data or errors if there are any
/*	- (NSArray *) mediaForPredicate:(NSPredicate *)predicate // return an array of raw Media K/V pair sets
	{
		NSError *error;
		NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
		NSEntityDescription *entity = [NSEntityDescription entityForName:@"MediaCD" inManagedObjectContext:context];
		[fetchRequest setEntity:entity];
		[fetchRequest setPredicate:predicate];
		NSArray *cachedMediaArray = [context executeFetchRequest:fetchRequest error:&error] ;

		return cachedMediaArray; // return array (list) of MediaCD objects
	}*/

	/**
	 * mediaForPredicate(where)
	 *
	 * @param whereClause SQL where string w/o "where"; e.g. "name = 'Bob' AND (this = 1 OR that >= 33)"
	 * @return Map of resulting MediaCD objs indexed by mediaID.
	 */

	public Map<Integer, MediaCD> mediaForPredicate(String whereClause) {
		Map<Integer, MediaCD> listOfMedia = new HashMap<>();

		Cursor cursor = dbDealer.getMedias(whereClause);
		cursor.moveToFirst();
		// convert cursor to Map of mediaCDs
		while (!cursor.isAfterLast()) {
			MediaCD mediaCDItem = parseMedia(cursor);
			listOfMedia.put(mediaCDItem.media_id, mediaCDItem);
			cursor.moveToNext();
		}

		cursor.close();

		return listOfMedia;
	}

	private MediaCD parseMedia(Cursor cursor) {
		MediaCD mediaCD = new MediaCD();
		mediaCD.media_id = cursor.getInt(cursor.getColumnIndexOrThrow(DBDealer.MEDIA_ID));
		mediaCD.game_id = cursor.getInt(cursor.getColumnIndexOrThrow(DBDealer.GAME_ID));
		mediaCD.user_id = cursor.getInt(cursor.getColumnIndexOrThrow(DBDealer.USER_ID));
		mediaCD.localURL = cursor.getString(cursor.getColumnIndexOrThrow(DBDealer.LOCAL_URL));
		mediaCD.remoteURL = cursor.getString(cursor.getColumnIndexOrThrow(DBDealer.REMOTE_URL));
		return mediaCD;
	}

//	public void commitContext()
//	{
//		NSError *error;
//		if(![context save:&error])
//		_ARIS_LOG_(@"Error saving media context - error:%@",error);
//	}

	public void clearCache() // delete all stored rows in media table.
	{
		dbDealer.deleteAllMedias();
		Log.i(Config.LOGTAG, getClass().getSimpleName() + "All Media deleted from Local DB."); //this is really only useful because this potentially takes a while, and this shows that its not frozen
	}

	public void requestGameData() {
		this.requestMedia();
	}

	public void mediasReceived(List<Map<String, String>> rawMediaArr) {
		this.updateMedias(rawMediaArr);
	}

	public void mediaReceived(List<Map<String, String>> rawMediaArr) {
		this.updateMedias(rawMediaArr);
	}

	//Different than other models, as it expects raw dicts rather than fully populated objects
	public void updateMedias(List<Map<String, String>> mediaToCacheDicts) {
		// set up query (get by game_id=0 (ARIS generic??) or game_id = [current game id]
		String where = "(game_id = 0) OR (game_id = " + mGamePlayAct.mGame.game_id + ")";
		// get the raw array of media map arrays from the device persistent DB
		Map<Integer, MediaCD> currentlyCachedMediaMap = this.mediaForPredicate(where);// null; // todo: populate from local DB call above

//		//Turn array to dict for quick check of existence in cache
//		NSMutableDictionary *currentlyCachedMediaMap = [[NSMutableDictionary alloc] init];
//		for(long i = 0; i < currentlyCachedMediaArray.count; i++)
//		[currentlyCachedMediaMap setObject:currentlyCachedMediaArray[i] forKey:((MediaCD *)currentlyCachedMediaArray[i]).media_id];

		MediaCD tmpMedia;
		// iterate through the "dict" versions of the media data and do...?
		for (Map<String, String> mediaDict : mediaToCacheDicts) {

			int media_id = Integer.parseInt(mediaDict.get("media_id"));// [mediaDict validIntForKey:@"media_id"]; // get the id from k/v pair with key "media_id"
			mediaIDsToLoad.add(media_id); // setObject:[NSNumber numberWithLong:media_id] forKey:[NSNumber numberWithLong:media_id]];
			tmpMedia = currentlyCachedMediaMap.get(media_id);
			if (tmpMedia == null) // currentlyCachedMediaMap[[NSNumber numberWithLong:media_id]]))
			{
//				tmpMedia = insertNewObjectForEntityForName("MediaCD");
				tmpMedia = new MediaCD();
				tmpMedia.media_id = media_id;
			}

			String remoteURL = mediaDict.get("url"); // validObjectForKey:@"url"];
			if (tmpMedia.remoteURL != null && !remoteURL.contentEquals(tmpMedia.remoteURL)) //if remote URL changed, invalidate local URL
				tmpMedia.localURL = "";
			tmpMedia.remoteURL = remoteURL;

			if (mediaDict.containsKey("game_id")) tmpMedia.game_id = Integer.parseInt(mediaDict.get("game_id"));
			if (mediaDict.containsKey("user_id")) tmpMedia.user_id = Integer.parseInt(mediaDict.get("user_id"));
			if (dbDealer.insertMedia(tmpMedia))
				Log.i(Config.LOGTAG, getClass().getSimpleName() + "Media cache   : Media id:" + media_id + " cached:" + tmpMedia.remoteURL);
			else
				Log.e(Config.LOGTAG, getClass().getSimpleName() + "Failed to insert Media: Media id:" + media_id + " cached:" + tmpMedia.remoteURL);
		}
//		this.commitContext(); // as best I can tell the intention is to insert each of these incoming "medicCDs" but why/how are they being set up for individual insertion in the loop above?
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_media_available(); //_ARIS_NOTIF_SEND_(@"MODEL_MEDIA_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}


	public void requestMedia() {
		mGamePlayAct.mServices.fetchMedias();
	}


	// may be irrelevant to Andoid...
//	public void requestMediaData()
//	{
//		List<Integer> media_ids = mediaIDsToLoad;  // allKeys];
//		Media m;
////		ARISDelegateHandle *d; // ??
//
//		mediaDataLoadDelegateHandles = [[NSMutableArray alloc] init];
//		mediaDataLoadMedia = [[NSMutableArray alloc] init];
//		mediaDataLoaded = 0;
//
//		for(int i = 0; i < media_ids.count; i++)
//		{
//			m = this.mediaForId:[((NSNumber *)media_ids[i]) longValue]];
//			if(!m.data)
//			{
//				d = [[ARISDelegateHandle alloc] initWithDelegate:self];
//				[mediaDataLoadDelegateHandles addObject:d];
//				[mediaDataLoadMedia addObject:m];
//			}
//		}
//		if(mediaDataLoadDelegateHandles.count == 0)
//		this.mediaLoaded:nil];
//
//		for(int i = 0; i < mediaDataLoadMedia.count; i++) //needs separate loop so notif doesn't get sent in same stack as generating count
//		[_SERVICES_MEDIA_ loadMedia:mediaDataLoadMedia[i] delegateHandle:mediaDataLoadDelegateHandles[i]]; //calls 'mediaLoaded' upon complete
//	}

//	public void mediaLoaded(Media m)
//	{
//		mediaDataLoaded++;
//		mGamePlayAct.mDispatch.model_media_data_loaded(); //_ARIS_NOTIF_SEND_(@"MODEL_MEDIA_DATA_LOADED",nil,nil);
//		if(mediaDataLoaded >= mediaDataLoadDelegateHandles.)
//		{
//			// ultimately calls beginGame:
//			mGamePlayAct.mDispatch.model_media_data_complete(); //_ARIS_NOTIF_SEND_(@"MODEL_MEDIA_DATA_COMPLETE",nil,nil);
//			mediaDataLoadMedia = nil;
//			mediaDataLoadDelegateHandles = nil;
//		}
//	}

	public Media mediaForId(int media_id) {
		if (media_id == 0) return null;

		//oh my hack
		if (media_id == Media.DEFAULT_PLAQUE_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			// UIImagePNGRepresentation = Return the data for the specified image in PNG format
			// get data from drawable image and lode into object field
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.plaque_icon_120);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}
		if (media_id == Media.DEFAULT_ITEM_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.item_icon_120);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}
		if (media_id == Media.DEFAULT_DIALOG_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.conversation_icon_120);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}
		if (media_id == Media.DEFAULT_WEB_PAGE_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.webpage_icon_120);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}
		if (media_id == Media.LOGO_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.logo_icon);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}
		if (media_id == Media.DEFAULT_NOTE_ICON_MEDIA_ID) {
			MediaCD mediaCD = new MediaCD();
			mediaCD.media_id = media_id;
			mediaCD.game_id = 0;
			mediaCD.user_id = 0;
			Media media = new Media(mediaCD);
			Drawable drawable = mGamePlayAct.getResources().getDrawable(R.drawable.note_icon);
			media.data = ((BitmapDrawable)drawable).getBitmap();
			media.thumb = media.data;
			media.setPartialLocalURL("blah.png"); //fake name to get it to know it's of type "IMAGE"
			return media;
		}

		Media media = medias.get(media_id); //[[NSNumber numberWithLong:media_id]
		if (media == null) //if doesn't exist in light cache...
		{
			String where = "media_id = " + media_id;
			Map<Integer, MediaCD> matchingCachedMediaArray = this.mediaForPredicate(where);

			if (matchingCachedMediaArray.size() != 0) //if DOES exist in disk cache
				media = new Media(); //new Media(matchingCachedMediaArray.get(0)); //[[Media alloc] initWithMediaCD:(MediaCD *)matchingCachedMediaArray[0]];
			else //if doesn't yet exist
			{
				MediaCD mediaCD = new MediaCD();
				mediaCD.media_id = media_id;
				mediaCD.game_id = 0;
				mediaCD.user_id = 0;
				media = new Media(mediaCD);
			}
		}
		medias.put(media.media_id, media); // [[NSNumber numberWithLong:media.media_id]] = media; //set light cache

//		this.commitContext();

		return media;
	}

	public Media newMedia() {
		MediaCD mediaCD = new MediaCD();
		mediaCD.media_id = 0;
		mediaCD.game_id = 0;
		mediaCD.user_id = 0;
		return new Media(mediaCD);
	}

	public void saveAlteredMedia(Media m) //yuck
	{
//		this.commitContext();
	}


}
