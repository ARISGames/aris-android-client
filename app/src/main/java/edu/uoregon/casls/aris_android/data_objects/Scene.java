package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Scene implements InstantiableProtocol {
	public long   scene_id = 0;
	public String name     = "";

	public long icon_media_id = 0; // may be irrelevant

	public long icon_media_id() {
		return icon_media_id;
	}
}
