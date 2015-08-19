package edu.uoregon.casls.aris_android.data_objects;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import edu.uoregon.casls.aris_android.Utilities.ArisApp;
import edu.uoregon.casls.aris_android.data_objects.cd_data_objects.MediaCD;

/**
 * Created by smorison on 8/13/15.
 */
public class Media {
	public long media_id;
	public long game_id;
	public long user_id; //??
	public String name;
	public String fileName;

	public URL localURL; // local file URL?
	public URL remoteURL; // are both url and thumb_url always the same?
	public URL localThumbURL;
	public URL remoteThumbURL; // are both url and thumb_url always the same?

	public MediaCD mediaCD = new MediaCD();

	// From iOS. Carl suggested these hold the actual file binary data.
	// Could not find any place in iOS where data is getting store locally on client.
//	@property (nonatomic, strong) NSData *data;
//	@property (nonatomic, strong) NSData *thumb;

	public Media initWithMediaCD(MediaCD mcd)
	{
		mediaCD = mcd;
		return this;
	}

	public long game_id()
	{
		return mediaCD.game_id ;
	}

	public void setGameId(long gid)
	{
		mediaCD.game_id = (int) gid;
	}

	public long user_id()
	{
		return (int) mediaCD.user_id;
	}

	public void setUserId(long uid)
	{
		mediaCD.user_id = (int) uid;
	}

	public long media_id()
	{
		return (int) mediaCD.media_id;
	}

	public void setMediaId(long mid)
	{
		mediaCD.media_id = (int) mid;
	}

	public URL localURL()
	{
		URL url	= null;
		if(mediaCD.localURL == null) return null;
		if(mediaCD.localURL.startsWith("file://"))
		{
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
				url = new URL("file://" + path.getPath() + mediaCD.localURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	public void setPartialLocalURL(String lURL)
	{
		mediaCD.localURL = lURL;
	}

	public URL  remoteURL()
	{
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

	public void setRemoteURL(URL url)
	{
		mediaCD.remoteURL = url.toString();
	}


	public String fileExtension()
	{
		if (mediaCD.remoteURL != null && !mediaCD.remoteURL.isEmpty())
			return mediaCD.remoteURL.substring(mediaCD.remoteURL.lastIndexOf(".")+1);
		// (else)
		return mediaCD.localURL.substring(mediaCD.localURL.lastIndexOf(".")+1) ;
	}

	// return general media file type
	public String type() {

			String ext = fileExtension().toLowerCase();
			if(ext.contentEquals("jpg")  ||
			ext.contentEquals("jpeg") ||
			ext.contentEquals("png")  ||
			ext.contentEquals("gif"))
			{
				return "IMAGE";
			}
			else if(ext.contentEquals("mov") ||
			ext.contentEquals("avi") ||
			ext.contentEquals("3gp") ||
			ext.contentEquals("m4v") ||
			ext.contentEquals("mp4"))
			{
				return "VIDEO";
			}
			else if(ext.contentEquals("mp3") ||
			ext.contentEquals("wav") ||
			ext.contentEquals("m4a")||
			ext.contentEquals("ogg") ||
			ext.contentEquals("caf"))
			{
				return "AUDIO";
			}
			else return "";
	}
}
