package edu.uoregon.casls.aris_android.services;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.cd_data_objects.MediaCD;
import edu.uoregon.casls.aris_android.media.ARISDelegateHandle;
import edu.uoregon.casls.aris_android.models.MediaModel;

/*
* Created by smorison on 11/18/15.
*/
public class ARISMediaLoader {

	public Map<String, MediaResult> dataConnections = new LinkedHashMap<>(); //NSMutableDictionary dataConnections;
	public List<MediaResult>        metaConnections = new ArrayList<>();  //NSMutableArray metaConnections;

	public transient GamePlayActivity mGamePlayAct;

	public ARISMediaLoader(GamePlayActivity gamePlayActivity) {
		this.initContext(gamePlayActivity);
	}

	private void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
	}

//	public void loadMedia(Media m, ARISDelegateHandle dh) {
	public void loadMedia(Media m) { // fixme: media base values not getting set. mediaCD =is= though. Find this missing step.
		if (m == null) return;

		MediaResult mr = new MediaResult();
		mr.media = m;
//		mr.delegateHandles.add(dh); // Android: avoiding this concept

		this.loadMediaFromMR(mr);
	}

	public void loadMediaFromMR(MediaResult mr) {
		if (mr.media.thumb != null)      { this.mediaLoadedForMR(mr); }
		else if (mr.media.data != null)       { this.deriveThumbForMR(mr); }
		else if (mr.media.localURL() != null)   { // get from the file if it already has been loaded
//			mr.media.data = dataWithContentsOfURL:mr.media.localURL;
			mr.media.data = BitmapFactory.decodeFile(mr.media.localURL().getPath());//.decodeStream(mr.media.localURL.openConnection().getInputStream());
		}
		else if (mr.media.remoteURL() != null) { // todo: call a pollServer type method to get media data, but one that can handle load failure and schedule to reload.
			// set upa a server request for the call to get Media data
//			NSURLRequest request = NSURLRequest requestWithURL:mr.media.remoteURL cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0;
//			if (mr.connection) mr.connection cancel; // if it's already defined, bail.
//			mr.data = NSMutableData alloc initWithCapacity:2048; // allocate defined size for the data property Android necessary?
//			mr.connection = NSURLConnection alloc initWithRequest:request delegate:self; // fill in the connection request params with a delegate
			// the key for this map is the media "url" itself, eg:"http://arisgames.org/server/gamedatav2/64/aris8e4de40283cdc5d1aefbbf93d7df82f3.jpg"
//			dataConnections setObject:mr forKey:mr.connection.description; // add this connection to the array (of currently active connections)
			// todo: may need to make this an async req. otherwise it might bog down the main thread -sem
			try {
				mr.media.data = BitmapFactory.decodeStream(mr.media.localURL().openConnection().getInputStream()); // for server retrieval of local file
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (mr.media.remoteURL() == null) { this.loadMetaDataForMR(mr); }
	}

	public void loadMetaDataForMR(MediaResult mr)
	{
		for (int i = 0; i < metaConnections.size(); i++) {
//		for (MediaResult existingMR : metaConnections) { //not sure if this iteration style will allow proper referencing to original MR objects or spin off new one's that will dissolve after the loop.
			MediaResult existingMR = metaConnections.get(i);
			if (existingMR.media.media_id() == mr.media.media_id()) { // this makes the bold assumption that mediaid is not 0.
				// If mediaresult already exists, merge delegates to notify rather than 1.Throwing new request out (need to keep delegate) or 2.Redundantly requesting
//				existingMR.delegateHandles = existingMR.delegateHandles arrayByAddingObjectsFromArray:mr.delegateHandles;
//				vvvvv fixme vvvvv;
				existingMR.delegateHandles.addAll(mr.delegateHandles); //FIXME: make this work w/o delegates please. NPE here.
				/* 03-03 14:13:30.424 edu.uoregon.casls.aris_android E/AndroidRuntime: FATAL EXCEPTION: main
                                                                    Process: edu.uoregon.casls.aris_android, PID: 11838
                                                                    java.lang.NullPointerException
                                                                        at edu.uoregon.casls.aris_android.services.ARISMediaLoader.loadMetaDataForMR(ARISMediaLoader.java:108)
                                                                        at edu.uoregon.casls.aris_android.services.ARISMediaLoader.loadMediaFromMR(ARISMediaLoader.java:97)
                                                                        at edu.uoregon.casls.aris_android.services.ARISMediaLoader.loadMedia(ARISMediaLoader.java:71)
                                                                        at edu.uoregon.casls.aris_android.models.MediaModel.deferedLoadMedia(MediaModel.java:245)
                                                                        at edu.uoregon.casls.aris_android.models.MediaModel$1.run(MediaModel.java:236)
                                                                        at android.os.Handler.handleCallback(Handler.java:733)
                                                                        at android.os.Handler.dispatchMessage(Handler.java:95)
                                                                        at android.os.Looper.loop(Looper.java:146)
                                                                        at android.app.ActivityThread.main(ActivityThread.java:5694)
                                                                        at java.lang.reflect.Method.invokeNative(Native Method)
                                                                        at java.lang.reflect.Method.invoke(Method.java:515)
                                                                        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1291)
                                                                        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1107)
                                                                        at dalvik.system.NativeStart.main(Native Method)*/
				return;
			}
		}
		metaConnections.add(mr);// addObject:mr;
		mGamePlayAct.mAppServices.fetchMediaById(mr.media.media_id()); //_SERVICES_ fetchMediaById:mr.media.media_id;
	}

	// calling stack as formed at start of game or continue game:
	//  this.retryLoadingAllMedia()
	//  <- listen from Dispatcher.model_media_available() <-
	//  MediaModel.updateMedias() <-- call to Dispatcher.model_media_available()
	//  MediaModel.mediasReceived()
	//  <- Disptcher.services_medias_received() <-
	//  ResponseHandler.processJsonHttpResponse( HTTP_GET_MEDIA_4_GAME ... ) <-- call to Dispatcher.services_medias_received()
	//  (From among others...) MediaModel.requestGameData <-- ergo, ...AppServices.fetchMedias()
	//  .........................Game.requestGameData()
	//  .........................GamePlayActivity.requestGameData()
	//  .........................GamePlayActivity.onStart() <-- loading sequence at start of game (or continue game)
	public void retryLoadingAllMedia() {
		// this is where we need to walk through the list of media that need to be loaded from server

		// walk through list of all media meta data MediaModel.mediaIdsToLoad array (ids of urls that need to gat their (binary) data from server)
		for (int mediaIdToLoad : mGamePlayAct.mMediaModel.mediaIDsToLoad) {
			// dispatch an async service to try and load this data into a MediaResult obj
			Media mediaToLoad = mGamePlayAct.mMediaModel.mediaForId(mediaIdToLoad);
			this.pollServer(mediaToLoad.mediaCD.remoteURL, mediaToLoad.mediaCD);
				// in that call: if call succeeds, add image to DB with it's data or just save as a file and put the rest in DB
				//  if load failed, leave it in the queue to be retried.
		}
	}

//		//do the ol' switcheroo so we wont get into an infinite loop of adding, removing, readding, etc...
//		NSMutableArray oldMetaConnections = metaConnections;
//		metaConnections = NSMutableArray alloc initWithCapacity:10;
//
//		MediaResult mr;
//		while(oldMetaConnections.count > 0)
//		{
//			mr = oldMetaConnections objectAtIndex:0;
//			mr.media = mGamePlayAct.mMediaModel.mediaForId(mr.media.media_id); //_MODEL_MEDIA_ mediaForId:mr.media.media_id;
//			oldMetaConnections removeObjectAtIndex:0;
//			this.loadMediaFromMR(mr);
//		}
//	}
//
//	public void connection(URLConnection c, Bitmap d)
//	{
//		MediaResult mr = ;
//		if (!(mr = dataConnections objectForKey:c.description)) return;
//		mr.data = d;// appendData:d;
//	}
//
//	public void connectionDidFinishLoading(URLConnection c)
//	{
//		MediaResult mr; if(!(mr = dataConnections objectForKey:c.description)) return;
//		dataConnections removeObjectForKey:c.description;
//		mr.media.data = mr.data;
//		mr cancelConnection;//MUST do this only AFTER data has already been transferred to media
//
//		//short names to cope with obj-c verbosity
//		NSString g = NSString stringWithFormat("%ld",mr.media.game_id; //game_id as string
//		NSString f = mr.media.remoteURL absoluteString componentsSeparatedByString("/" lastObject stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding; //filename
//
//		NSString newFolder = _ARIS_LOCAL_URL_FROM_PARTIAL_PATH_(g);
//		if(!NSFileManager defaultManager fileExistsAtPath:newFolder isDirectory:nil)
//		NSFileManager defaultManager createDirectoryAtPath:newFolder withIntermediateDirectories:YES attributes:nil error:nil;
//		mr.media setPartialLocalURL:NSString stringWithFormat("%@/%@",g,f;
//		mr.media.data writeToURL:mr.media.localURL options:nil error:nil;
//		mr.media.localURL setResourceValue:NSNumber numberWithBool:YES forKey:NSURLIsExcludedFromBackupKey error:nil;
//
//		_MODEL_MEDIA_ saveAlteredMedia:mr.media;//not as elegant as I'd like...
//		_ARIS_LOG_(@"Media loader  : Media id:%ld loaded:%@",mr.media.media_id,mr.media.remoteURL);
//		this.loadMediaFromMR:mr;
//	}
//
	public void mediaLoadedForMR(MediaResult mr)
	{
		//This is so ugly. See comments in ARISDelegateHandle.h for reasoning
		for (int i = 0; i < mr.delegateHandles.size(); i++) {
			ARISDelegateHandle dh = mr.delegateHandles.get(i);
			if (dh.delegate() != null) { // todo see about a java equiv of this call: && dh.delegate class conformsToProtocol(protocol(ARISMediaLoaderDelegate))
				MediaModel.class.cast(dh.delegate()).mediaLoaded(mr.media);       // mediaLoaded(mr.media);
			}
		}
	}

	public void deriveThumbForMR(MediaResult mr)
	{
		Bitmap data = mr.media.data;
		int h = 128;
		int w = 128;

		String type = mr.media.type();
		if (type.contentEquals("IMAGE"))
		{
			mr.media.thumb = ThumbnailUtils.extractThumbnail(data, h, w);
//			i = UIImage imageWithData:data;
		}
		else if (type.contentEquals("VIDEO"))
		{
			mr.media.thumb = ThumbnailUtils.createVideoThumbnail(mr.media.localURL.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
//			AVAsset asset = AVAsset assetWithURL:mr.media.localURL;
//			AVAssetImageGenerator imageGenerator = AVAssetImageGenerator allocinitWithAsset:asset;
//			CMTime t = asset duration;
//			t.value = 1000;
//			CGImageRef imageRef = imageGenerator copyCGImageAtTime:t actualTime:NULL error:NULL;
//			i = UIImage imageWithCGImage:imageRef;
//			CGImageRelease(imageRef);  // CGImageRef won't be released by ARC
		}
		else if (type.contentEquals("AUDIO"))
		{
			Resources res = mGamePlayAct.getResources();
			Drawable drawable = res.getDrawable(R.drawable.default_audio_icon);
			mr.media.thumb = ((BitmapDrawable)drawable).getBitmap();
//			i = UIImage imageNamed("microphone"); //hack
		}
//		if (!i) i = UIImage imageNamed("logo_icon");
//
//		int s = 128;
//		int w = s;
//		int h = s;
//		if(i.size.width > i.size.height)
//			h = i.size.height  (s/i.size.width);
//		else
//			w = i.size.width  (s/i.size.height);
//		UIGraphicsBeginImageContext(CGSizeMake(w,h));
//		i drawInRect:CGRectMake(0,0,w,h);
//		UIImage newImage = UIGraphicsGetImageFromCurrentImageContext();
//		UIGraphicsEndImageContext();

//		mr.media.thumb = UIImagePNGRepresentation(newImage);
		this.loadMediaFromMR(mr);
	}

//	public void mediaResultThumbFound(NSNotification)notification
//	{
//		MediaResult mr = notification.userInfo@"media_result";
//		this.loadMediaFromMR(mr);
//	}

/**
	retrieve media bitmap data from server
*/
	public void pollServer(final String requestURL, final MediaCD mediaCDToLoad) {
//		showProgress(true);

		RequestParams rqParams = new RequestParams();

		final Context context = mGamePlayAct;
//		final String request_url = AppConfig.SERVER_URL_MOBILE + requestURL;

//		StringEntity entity;
//		entity = null;
		String[] allowedContentTypes = new String[]{"image/png", "image/jpeg", "image/gif"};

		// Get request
		if (AppUtils.isNetworkAvailable(mGamePlayAct.getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();
//			static String reqCall
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient Sending Req for Media Data: " + requestURL);
			client.get(context, requestURL, new BinaryHttpResponseHandler(allowedContentTypes /*, looper here? */) { // the looper might be able to handle failed attempts?
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] mediaBytes) {
					processLoadedMedia(mediaBytes, mediaCDToLoad);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
					processFailedMediaLoad(mediaCDToLoad);
				}
			});

//			client.post(context, requestURL, entity, "application/json", new JsonHttpResponseHandler() {
//				@Override
//				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
////					showProgress(false);
//					try {
//						// todo: custom response handler for media data
//						mGamePlayAct.mResposeHandler.processJsonHttpResponse(requestURL, AppConfig.TAG_SERVER_SUCCESS, jsonReturn);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				@Override
//				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//					Log.w(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
////					showProgress(false);
//					Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
//							Toast.LENGTH_SHORT);
//					t.setGravity(Gravity.CENTER, 0, 0);
//					t.show();
//					super.onFailure(statusCode, headers, responseString, throwable);
//				}
//				@Override
//				public void onProgress(int remaining, int total) {
////					Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient Progress for Req: " + requestApi + ". Progress: " + remaining + "/" + total);
//					// todo: set up progress bars of some sort for each request.
//				}
//			});
		}
		else {
			// todo: what to do when internet is unavailable. A toast is not what we want here. May even want to test connectivity in calling method.
//			Toast t = Toast.makeText(mGamePlayAct.getApplicationContext(), "You are not connected to the internet currently. Please try again later.",
//					Toast.LENGTH_SHORT);
//			t.setGravity(Gravity.CENTER, 0, 0);
//			t.show();
		}

	}

/*
	 Android replacement of connectionDidFinishLoading();
	 Save data to local file, and populate the localURL with file location
*/
	private String processLoadedMedia(byte[] mediaBytes, MediaCD mediaCDToLoad) {
		// save binary to a local file.
		// enter this media data into database and a local URI to the file (Should there
		// already be a row for this from prior entry or upstream empty placeholder stub?)
		ContextWrapper cw = new ContextWrapper(mGamePlayAct);
		// path to /data/data/appName/app_data/gameMedia_(game_id)
		File directory = cw.getDir("gameMedia_" + mGamePlayAct.mGame.game_id, Context.MODE_PRIVATE);
		// Create directory with file.
		File mypath = new File(directory, mediaCDToLoad.media_id + "." + mediaCDToLoad.fileExtension());

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);

			fos.write(mediaBytes);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mediaCDToLoad.localURL = mypath.getAbsolutePath();

		// update DB to reflect new local URL
		mGamePlayAct.mMediaModel.addOrUpdateMediaCD(mediaCDToLoad);

		return mypath.getAbsolutePath();
	}

	private void processFailedMediaLoad(MediaCD mediaToLoad) {
		// todo: finish me
	}

}
