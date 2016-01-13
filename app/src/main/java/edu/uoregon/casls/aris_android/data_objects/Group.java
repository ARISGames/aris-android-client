package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 9/29/15.
 */
public class Group {
	public long group_id = 0;
	public String name = "";

	public void init() { // redundant since new Group() will start with these values.
		this.group_id = 0;
		this.name = "";
	}

	public long icon_media_id() {
		return 0; // stub for compatibility. Should be fleshed out with real values.
	}


}
