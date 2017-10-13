package edu.uoregon.casls.aris_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.Media;

/**
 * Created by smorison on 9/15/16.
 */

public class ARISWebView extends WebView {

	private transient GamePlayActivity mGamePlayAct;
	private transient Game             mGame;
	private Map<Long, MediaPlayer> audioPlayers = new HashMap<>();

	public ARISWebView(Context context) {
		super(context);
	}

	public ARISWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ARISWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void initContext(Activity act) {
		mGamePlayAct = (GamePlayActivity) act;
		mGame = mGamePlayAct.mGame;
	}

	public void initContextAndInjectJavaScript(Activity act) {
		this.initContext(act);
		// standard settings for most ARISWebViews. Override for specific cases.
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.

		this.setWebChromeClient(new WebChromeClient());

		this.setWebViewClient(new WebViewClient() {
			private int webViewPreviousState;
			private final int PAGE_STARTED = 0x1;
			private final int PAGE_REDIRECTED = 0x2;
			private boolean jsEvaluated = false;

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webViewPreviousState = PAGE_REDIRECTED;
				if (url.startsWith("aris:")) { // aka isARISRequest
//					Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D2, "Caught a call to 'aris:' from the webview. ");
					handleARISRequest(url);
					return true; // App will handle it
				}
				else
					return false; // web view will handle it.
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
//					todo: start progress dialog
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				if (webViewPreviousState == PAGE_STARTED) {
//						todo: progress dialog.dismiss();
//						dialog = null;

					//todo: this is where I would want to add the "injectHTMLWithARISjs" ?
					if (!jsEvaluated) { //  make sure we do this just once, as onPageFinished gets called multiple times.
						jsEvaluated = true;
						evaluateJavascript(AppUtils.getArisJs(mGamePlayAct), null);
					}
				}
			}
		});
	}

	public void loadHTMLString(String htmlBodyBlock) {
		// (MT) This used to be loadData(),
		// but that apparently removes newlines in the input string,
		// which usually breaks all your JS.
		loadDataWithBaseURL(null, "<html><body>" + htmlBodyBlock + "</body></html>", "text/html", "UTF-8", null);
	}

	public void handleARISRequest(String request) {
		Uri uri = Uri.parse(request);
		String path = uri.getPath(); // should be "aris" here
		String mainCommand = uri.getHost();
		List<String> components = new ArrayList<String>(uri.getPathSegments());
//		String components = pathSegments.get(0);
		components.add(0, "/"); // (MT) This is a hack to match the behavior of the iOS pathComponents function

		if (mainCommand.contentEquals("cache")) {
			long item_id;
			long item_qty;
			for (Item item : mGame.itemsModel.items().values()) {
				item_id = item.item_id;
				String escapedItemName = item.name;
				escapedItemName = escapedItemName.replace("\"", "\\\"");
				escapedItemName = escapedItemName.replace("\n", "\\n");
				escapedItemName = escapedItemName.replace("\r", "\\r");
				escapedItemName = escapedItemName.replace("\t", "\\t");
//				[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.setItemName(%ld,\"%@\");",item_id,escapedItemName]];
				evaluateJavascript("ARIS.cache.setItemName(" + item_id + ",\"" + escapedItemName + "\");", null);
				item_qty = mGame.playerInstancesModel.qtyOwnedForItem(item_id); //[_MODEL_PLAYER_INSTANCES_ qtyOwnedForItem:item_id];
//				[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.setPlayerItem(%ld,%ld);",item_id,item_qty]];
				evaluateJavascript("ARIS.cache.setPlayerItem(" + item_id + "," + item_qty + ");", null);
				item_qty = mGame.gameInstancesModel.qtyOwnedForItem(item_id); //mGame.instancesModel.qtyOwnedForItem:item_id];
//				[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.setGameItem(%ld,%ld);",item_id,item_qty]];
				evaluateJavascript("ARIS.cache.setGameItem(" + item_id + "," + item_qty + ");", null);
				item_qty = mGame.groupInstancesModel.qtyOwnedForItem(item_id); //mGame.groupInstancesModel.qtyOwnedForItem:item_id];
//				[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.setGroupItem(%ld,%ld);",item_id,item_qty]];
				evaluateJavascript("ARIS.cache.setGroupItem(" + item_id + "," + item_qty + ");", null);
			}

			Media playerMedia = mGamePlayAct.mMediaModel.mediaForId(Long.decode(mGamePlayAct.mPlayer.media_id));
//			String playerJSON = [NSString stringWithFormat:
//			@"{"
//			"\"user_id\":%ld,"
//			"\"key\":\"%@\","
//			"\"user_name\":\"%@\","
//			"\"display_name\":\"%@\","
//			"\"photoURL\":\"%@\""
//			"}",
//					mGamePlayAct.mPlayer.user_id,
//					mGamePlayAct.mPlayer.read_write_key,
//					mGamePlayAct.mPlayer.user_name,
//					mGamePlayAct.mPlayer.display_name,
//					playerMedia.remoteURL];

			JSONObject playerJSON = new JSONObject();
			try {
				playerJSON.put("user_id", mGamePlayAct.mPlayer.user_id);
				playerJSON.put("key", mGamePlayAct.mPlayer.read_write_key);
				playerJSON.put("user_name", mGamePlayAct.mPlayer.user_name);
				playerJSON.put("display_name", mGamePlayAct.mPlayer.display_name);
				playerJSON.put("photoURL", playerMedia.remoteURL);
			} catch (JSONException e) {
				e.printStackTrace();
			}

//			[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.setPlayer(%@);",playerJSON]];
			evaluateJavascript("ARIS.cache.setPlayer(" + playerJSON.toString() + ");", null);
//			[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.cache.detach()"]];
			evaluateJavascript("ARIS.cache.detach()", null);
		}
		else if (mainCommand.contentEquals("logout")) {
			this.clear(); // this.clear];
			if (mGamePlayAct.mPlayer == null) return; // can't log out if noone logged in
			// dismiss self before trying to log out
//			if([delegate respondsToSelector:@selector(ARISWebViewRequestsDismissal:)])
//			[delegate ARISWebViewRequestsDismissal:self];
			mGamePlayAct.logOut();
		}
		else if (mainCommand.contentEquals("exit")) {
			this.clear(); // this.clear];

			String type = "";
			String token = "";
			if (components.size() > 1) type = components.get(1);
			if (components.size() > 2) token = components.get(2);

			if (mGame == null) return; //game doesn't exist yet, can't "exit to"

			if (type.contentEquals("game"))
				mGamePlayAct.leaveGame();
			else if (type.contentEquals("tab"))
				mGame.displayQueueModel.enqueueTab(mGame.tabsModel.tabForType(token)); //[_MODEL_DISPLAY_QUEUE_ enqueueTab:[_MODEL_TABS_ tabForType:token]];
			else if (type.contentEquals("scanner")) {
				mGame.tabsModel.tabForType("SCANNER").info = token;
				mGame.displayQueueModel.enqueueTab(mGame.tabsModel.tabForType("SCANNER"));
			}
			else if (type.contentEquals("plaque"))
				mGame.displayQueueModel.enqueueObject(mGame.plaquesModel.plaqueForId(Long.decode(token)));
			else if (type.contentEquals("webpage"))
				mGame.displayQueueModel.enqueueObject(mGame.webPagesModel.webPageForId(Long.decode(token)));
			else if (type.contentEquals("item"))
				mGame.displayQueueModel.enqueueObject(mGame.itemsModel.itemForId(Long.decode(token)));
			else if (type.contentEquals("character") || type.contentEquals("dialog") || type.contentEquals("conversation"))
				mGame.displayQueueModel.enqueueObject(mGame.dialogsModel.dialogForId(Long.decode(token)));

			// todo: necessary for Android?
//			if([delegate respondsToSelector:@selector(ARISWebViewRequestsDismissal:)])
//			[delegate ARISWebViewRequestsDismissal:self];
		}
		else if (mainCommand.contentEquals("refreshStuff")) // redraw what stuff??
		{
			// looks like the following calls end in empty methods in iOS. Ignore in Android too then.
//			if([delegate respondsToSelector:@selector(ARISWebViewRequestsRefresh:)])
//			[delegate ARISWebViewRequestsRefresh:self];
		}
		else if (mainCommand.contentEquals("vibrate")) {
			Vibrator v = (Vibrator) mGamePlayAct.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
		}
		else if (mainCommand.contentEquals("player")) {
			Media playerMedia = mGamePlayAct.mMediaModel.mediaForId(Long.decode(mGamePlayAct.mPlayer.media_id));
//			String *playerJSON = [String stringWithFormat:
//			@"{"
//			"\"user_id\":%ld,"
//			"\"key\":\"%@\","
//			"\"user_name\":\"%@\","
//			"\"display_name\":\"%@\","
//			"\"photoURL\":\"%@\""
//			"}",
//					mGamePlayAct.mPlayer.user_id,
//					mGamePlayAct.mPlayer.read_write_key,
//					mGamePlayAct.mPlayer.user_name,
//					mGamePlayAct.mPlayer.display_name,
//					playerMedia.remoteURL];

			JSONObject playerJSON = new JSONObject();
			try {
				playerJSON.put("user_id", mGamePlayAct.mPlayer.user_id);
				playerJSON.put("key", mGamePlayAct.mPlayer.read_write_key);
				playerJSON.put("user_name", mGamePlayAct.mPlayer.user_name);
				playerJSON.put("display_name", mGamePlayAct.mPlayer.display_name);
				playerJSON.put("photoURL", playerMedia.remoteURL);
			} catch (JSONException e) {
				e.printStackTrace();
			}

//			[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didReceivePlayer(%@);",playerJSON]];
			evaluateJavascript("ARIS.didReceivePlayer(" + playerJSON.toString() + ");", null);
		}
		else if (mainCommand.contentEquals("group")) {
			mGame.groupsModel.setPlayerGroup(mGame.groupsModel.groupForId(Long.decode(components.get(2))));
		}
		else if (mainCommand.contentEquals("scene")) {
			mGame.scenesModel.setPlayerScene(mGame.scenesModel.sceneForId(Long.decode(components.get(2))));
		}
		else if (mainCommand.contentEquals("instances")) {
			long item_id = Long.decode(components.get(3));
			long qty = Long.decode(components.get(4)); // always at offset 4 except for "get" calls
			if (components.size() > 1 && components.get(1).contentEquals("player")) {
				if (components.size() > 2 && components.get(2).contentEquals("get")) {
					qty = mGame.playerInstancesModel.qtyOwnedForItem(item_id);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateItemQty(%ld,%ld);",item_id,qty]];
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdatePlayerItemQty(%ld,%ld);",item_id,qty]];
					doJSItemUpdates(item_id, qty);
				}
				if (components.size() > 2 && components.get(2).contentEquals("set")) {
					long newQty = mGame.playerInstancesModel.setItemsForPlayer(item_id, qty);
					doJSItemUpdates(item_id, newQty);
				}
				if (components.size() > 2 && components.get(2).contentEquals("give")) {
					long newQty = mGame.playerInstancesModel.giveItemToPlayer(item_id, qty);
					doJSItemUpdates(item_id, newQty);
				}
				if (components.size() > 2 && components.get(2).contentEquals("take")) {
					long newQty = mGame.playerInstancesModel.takeItemFromPlayer(item_id, qty);
					doJSItemUpdates(item_id, newQty);
				}
			}
			if (components.size() > 1 && components.get(1).contentEquals("game")) {
				if (components.size() > 2 && components.get(2).contentEquals("get")) {
					qty = mGame.gameInstancesModel.qtyOwnedForItem(item_id);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGameItemQty(%ld,%ld);",item_id,qty]];
					evaluateJavascript("ARIS.didUpdateGameItemQty(" + item_id + "," + qty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("set")) {
					long newQty = mGame.gameInstancesModel.setItemsForGame(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGameItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGameItemQty(" + item_id + "," + newQty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("give")) {
					long newQty = mGame.gameInstancesModel.giveItemToGame(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGameItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGameItemQty(" + item_id + "," + newQty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("take")) {
					long newQty = mGame.gameInstancesModel.takeItemFromGame(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGameItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGameItemQty(" + item_id + "," + newQty + ");", null);
				}
			}
			if (components.size() > 1 && components.get(1).contentEquals("group")) {
				if (components.size() > 2 && components.get(2).contentEquals("get")) {
					qty = mGame.groupInstancesModel.qtyOwnedForItem(item_id);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGroupItemQty(%ld,%ld);",item_id,qty]];
					evaluateJavascript("ARIS.didUpdateGroupItemQty(" + item_id + "," + qty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("set")) {
					long newQty = mGame.groupInstancesModel.setItemsForGroup(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGroupItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGroupItemQty(" + item_id + "," + newQty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("give")) {
					long newQty = mGame.groupInstancesModel.giveItemToGroup(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGroupItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGroupItemQty(" + item_id + "," + newQty + ");", null);
				}
				if (components.size() > 2 && components.get(2).contentEquals("take")) {
					long newQty = mGame.groupInstancesModel.takeItemFromGroup(item_id, qty);
//					[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateGroupItemQty(%ld,%ld);",item_id,newQty]];
					evaluateJavascript("ARIS.didUpdateGroupItemQty(" + item_id + "," + newQty + ");", null);
				}
			}
		}
		else if (mainCommand.contentEquals("media")) {
			if (components.size() > 2 && components.get(1).contentEquals("prepare"))
				this.loadAudioFromMediaId(Long.decode(components.get(2)));
			else if (components.size() > 2 && components.get(1).contentEquals("play"))
				this.playAudioFromMediaId(Long.decode(components.get(2)));
			else if (components.size() > 2 && components.get(1).contentEquals("stop"))
				this.stopAudioFromMediaId(Long.decode(components.get(2)));
			else if (components.size() > 3 && components.get(1).contentEquals("setVolume"))
				this.setVolumeForMediaId(Long.decode(components.get(2)), Float.parseFloat(components.get(3)));
		}
		else if (mainCommand.contentEquals("vibrate")) {
			Vibrator v = (Vibrator) mGamePlayAct.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
		}
//		[webView stringByEvaluatingJavaScriptFromString:@"ARIS.isNotCurrentlyCalling();"];
		evaluateJavascript("ARIS.isNotCurrentlyCalling();", null);
	}

	public void loadAudioFromMediaId(long media_id) {
		Media media = mGamePlayAct.mMediaModel.mediaForId(media_id);
		MediaPlayer player = MediaPlayer.create(mGamePlayAct, Uri.parse(media.localURL().toString())); //AVPlayer *player = [AVPlayer playerWithURL:media.localURL);
//		MediaPlayer player = MediaPlayer.create(mGamePlayAct, Uri.parse(media.mediaCD.localURL.toString())); //AVPlayer *player = [AVPlayer playerWithURL:media.localURL);
		audioPlayers.put(media_id, player); // [audioPlayers setObject:player forKey:[NSNumber numberWithLong(media_id));
	}

	public void playAudioFromMediaId(long media_id) {
//		AVPlayer *player = [audioPlayers objectForKey:[NSNumber numberWithLong(media_id));
		MediaPlayer player = audioPlayers.get(media_id);
//		CMTime zero = CMTimeMakeWithSeconds(0, 600);
//		[player seekToTime:zero];
		player.reset();
		if (player == null) {
			this.loadAudioFromMediaId(media_id);
			player = audioPlayers.get(media_id); //[audioPlayers objectForKey:[NSNumber numberWithLong(media_id));
		}
		player.start();
	}

	public void stopAudioFromMediaId(long media_id) {
		MediaPlayer player = audioPlayers.get(media_id); //AVPlayer *player = [audioPlayers objectForKey:[NSNumber numberWithLong(media_id));
		player.pause();
	}

	public void setVolumeForMediaId(long media_id, Float volume) {
		MediaPlayer player = audioPlayers.get(media_id); //AVPlayer *player = [audioPlayers objectForKey:[NSNumber numberWithLong(media_id));
		player.setVolume(volume, volume);

		// todo: Not certain how any of the code below applies to this
//		NSArray *audioTracks = [player.currentItem.asset tracksWithMediaType:AVMediaTypeAudio];
//		NSMutableArray *allAudioParams = [NSMutableArray array];
//		for (AVAssetTrack *track in audioTracks) {
//		AVMutableAudioMixInputParameters *audioInputParams =
//		[AVMutableAudioMixInputParameters audioMixInputParameters];
//		[audioInputParams setVolume:volume atTime:kCMTimeZero];
//		[audioInputParams setTrackID:[track trackID]];
//		[allAudioParams addObject:audioInputParams];
//	}
//
//		AVMutableAudioMix *audioMix = [AVMutableAudioMix audioMix];
//		[audioMix setInputParameters:allAudioParams];
//
//		player.currentItem.audioMix = audioMix;
	}

	public Boolean hookWithParams(String params) {
//		[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.hook(%@);",params]];
		evaluateJavascript("ARIS.hook(" + params + ");", null);
		return false;
	}

	public Boolean tickWithParams(String params) {
//		[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.tick(%@);",params]];
		evaluateJavascript("ARIS.tick(" + params + ");", null);
		return false;
	}

	public void disableUserInteraction() {
		this.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) { // capture and ignore all touch events.
				return true;
			}
		});
	}

	private void clear() {
		this.stopLoading();
		this.loadUrl("about:blank");
		// todo: loop through all audio players and pause/stop them.
	}

	private void doJSItemUpdates(Long itemId, Long qty) {
//		[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdateItemQty(%ld,%ld);",item_id,qty]];
		evaluateJavascript("ARIS.didUpdateItemQty(" + itemId + "," + qty + ");", null);
//		[webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"ARIS.didUpdatePlayerItemQty(%ld,%ld);",item_id,qty]];
		evaluateJavascript("ARIS.didUpdatePlayerItemQty(" + itemId + "," + qty + ");", null);
	}
}
		