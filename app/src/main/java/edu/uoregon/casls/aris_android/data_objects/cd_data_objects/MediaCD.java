package edu.uoregon.casls.aris_android.data_objects.cd_data_objects;

/**
 * Created by smorison on 8/19/15.
 * In keeping with original iOS data structures
 */
public class MediaCD {
	public int media_id;
	public int game_id;
	public int user_id;
	public String localURL;
	public String remoteURL;
	public long autoplay = 0; // added in iOS 8/16;

	public String fileExtension() {
		if (this.remoteURL != null && !this.remoteURL.isEmpty())
			return this.remoteURL.substring(this.remoteURL.lastIndexOf(".") + 1);
		// (else)
		return this.localURL.substring(this.localURL.lastIndexOf(".") + 1);
	}

}
