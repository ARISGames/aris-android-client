package edu.uoregon.casls.aris_android.data_objects;

import android.graphics.Bitmap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import edu.uoregon.casls.aris_android.Utilities.ArisApp;
import edu.uoregon.casls.aris_android.data_objects.cd_data_objects.MediaCD;

/**
 * Created by smorison on 8/13/15.
 */
public class Media {

	// Phil's hack for default icon images.
	public static final int DEFAULT_PLAQUE_ICON_MEDIA_ID   = -1;
	public static final int DEFAULT_ITEM_ICON_MEDIA_ID     = -2;
	public static final int DEFAULT_DIALOG_ICON_MEDIA_ID   = -3;
	public static final int DEFAULT_WEB_PAGE_ICON_MEDIA_ID = -4;
	public static final int LOGO_ICON_MEDIA_ID             = -5;
	public static final int DEFAULT_NOTE_ICON_MEDIA_ID     = -6;

	public MediaCD mediaCD;

	// no default values assigned. from iOS: "SHOULDNT MANUALLY INIT MEDIA- get it from mediaModel"
	public long   media_id;
	public long   game_id;
	public long   user_id; //??
	public long   autoplay = 0;
	public String name; // omitted in iOS, but a valid field in server return data from getMediaForGame (fetchMedias)
	public String file_name; // omitted in iOS, but a valid field in server return data as stated above

	public URL    localURL; // local file URL?
	public URL    url; // this is the field that comes in on the json form server\
	public URL    remoteURL; // was: url; // are both url and thumb_url always the same?
	public URL    localThumbURL;
	// todo: why store the data if the file is already downloaded and can be piped in to whatever view we need via its URI?
	public Bitmap data; // todo: need to flesh out how this would hold the data across serialization. (see above before fleshing)
	public Bitmap thumb;

	// Not sure if we need this ability. Delete block later if clearly unneeded. -sem
	/*   Get the raw data from a resource and convert to a file:
	Resources res = getResources();
	Drawable drawable = res.getDrawable(R.drawable.my_pic);
	Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	byte[] bitMapData = stream.toByteArray();
        */

	public Media() {
		this.mediaCD = new MediaCD();
	}

	public Media(MediaCD mCD) {
		initWithMediaCD(mCD);
	}

	public Media initWithMediaCD(MediaCD mcd) {
		this.mediaCD = mcd;
		return this;
	}

	/**
	 * since some of the member vars are not consistent with the json media data from the server
	 * call this to patch the discrepancy
	 */
	public void initUrlDuctTape() {
		if (url != null && !url.toString().contentEquals(""))
			this.remoteURL = this.url;
	}

	public long game_id() {
		return mediaCD.game_id;
	}

	public void setGameId(long gid) {
		mediaCD.game_id = (int) gid;
	}

	public long user_id() {
		return (int) mediaCD.user_id;
	}

	public void setUserId(long uid) {
		mediaCD.user_id = (int) uid;
	}

	public long media_id() {
		return (int) mediaCD.media_id;
	}

	public void setMediaId(long mid) {
		mediaCD.media_id = (int) mid;
	}

	public boolean autoplay() {
		return mediaCD.autoplay != 0;
	}

	public URL localURL() {
		URL url = null;
		if (mediaCD.localURL == null) return null;
		if (mediaCD.localURL.startsWith("file://")) {
			try {
				url = new URL(mediaCD.localURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		else // return with local device storage directory appended to file url. todo:test for proper behaviour
		{
			File path = ArisApp.getContext().getFilesDir();

			try {
				if (mediaCD.localURL.startsWith("/data/data/"))
					url = new URL("file://" + mediaCD.localURL);
				else
					url = new URL("file://" + path.getPath() + mediaCD.localURL);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	public void setPartialLocalURL(String lURL) {
		mediaCD.localURL = lURL;
	}

	public URL remoteURL() {
		//Hack to accommodate for server error
		String fixedURLString = mediaCD.remoteURL.replace("gamedata//", "gamedata/player/");
		URL fixedURL = null;

		try {
			fixedURL = new URL(fixedURLString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return fixedURL;
	}

	public void setRemoteURL(URL url) {
		mediaCD.remoteURL = url.toString();
	}


	public String fileExtension() {
		if (mediaCD.remoteURL != null && !mediaCD.remoteURL.isEmpty())
			return mediaCD.remoteURL.substring(mediaCD.remoteURL.lastIndexOf(".") + 1);
		// (else)
		return mediaCD.localURL.substring(mediaCD.localURL.lastIndexOf(".") + 1);
	}

	// return general media file type
	public String type() {

		String ext = fileExtension().toLowerCase();
		if (ext.contentEquals("jpg") ||
				ext.contentEquals("jpeg") ||
				ext.contentEquals("png") ||
				ext.contentEquals("gif")) {
			return "IMAGE";
		}
		else if (ext.contentEquals("mov") ||
				ext.contentEquals("avi") ||
				ext.contentEquals("3gp") ||
				ext.contentEquals("m4v") ||
				ext.contentEquals("mp4")) {
			return "VIDEO";
		}
		else if (ext.contentEquals("mp3") ||
				ext.contentEquals("wav") ||
				ext.contentEquals("m4a") ||
				ext.contentEquals("ogg") ||
				ext.contentEquals("caf")) {
			return "AUDIO";
		}
		else return "";
	}

}
