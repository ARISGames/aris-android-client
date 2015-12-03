package edu.uoregon.casls.aris_android.services;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Media;
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

	public void loadMedia(Media m, ARISDelegateHandle dh) {
		if (m == null) return;

		MediaResult mr = new MediaResult();
		mr.media = m;
		mr.delegateHandles.add(dh);

		this.loadMediaFromMR(mr);
	}

	public void loadMediaFromMR(MediaResult mr) {
		if (mr.media.thumb != null)      { this.mediaLoadedForMR(mr); }
		else if (mr.media.data != null)       { this.deriveThumbForMR(mr); }
		else if (mr.media.localURL != null)   {
//			mr.media.data = dataWithContentsOfURL:mr.media.localURL;
			mr.media.data = BitmapFactory.decodeFile(mr.media.localURL.getPath());//.decodeStream(mr.media.localURL.openConnection().getInputStream());
		}
		else if (mr.media.remoteURL != null) { // todo: call a pollServer type method to get media data, but one that can handle load failure and schedule to reload.
			// set upa a server request for the call to get Media data
//			NSURLRequest request = NSURLRequest requestWithURL:mr.media.remoteURL cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0;
//			if (mr.connection) mr.connection cancel; // if it's already defined, bail.
//			mr.data = NSMutableData alloc initWithCapacity:2048; // allocate defined size for the data property Android necessary?
//			mr.connection = NSURLConnection alloc initWithRequest:request delegate:self; // fill in the connection request params with a delegate
			// the key for this map is the media "url" itself, eg:"http://arisgames.org/server/gamedatav2/64/aris8e4de40283cdc5d1aefbbf93d7df82f3.jpg"
//			dataConnections setObject:mr forKey:mr.connection.description; // add this connection to the array (of currently active connections)
			// todo: may need to make this an async req. otherwise it might bog down the main thread -sem
			try {
				mr.media.data = BitmapFactory.decodeStream(mr.media.localURL.openConnection().getInputStream()); // for server retrieval
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.loadMediaFromMR(mr);
		}
		else if (mr.media.remoteURL == null) { this.loadMetaDataForMR(mr); }
	}

	public void loadMetaDataForMR(MediaResult mr)
	{
		for (int i = 0; i < metaConnections.size(); i++) {
//		for (MediaResult existingMR : metaConnections) { //not sure if this iteration style will allow proper referencing to original MR objects or spin off new one's that will dissolve after the loop.
			MediaResult existingMR = metaConnections.get(i);
			if (existingMR.media.media_id == mr.media.media_id) {
				// If mediaresult already exists, merge delegates to notify rather than 1.Throwing new request out (need to keep delegate) or 2.Redundantly requesting
//				existingMR.delegateHandles = existingMR.delegateHandles arrayByAddingObjectsFromArray:mr.delegateHandles;
				existingMR.delegateHandles.addAll(mr.delegateHandles);
				return;
			}
		}
		metaConnections.add(mr);// addObject:mr;
		mGamePlayAct.mServices.fetchMediaById(mr.media.media_id); //_SERVICES_ fetchMediaById:mr.media.media_id;
	}

//	public void retryLoadingAllMedia()
//	{
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


}
