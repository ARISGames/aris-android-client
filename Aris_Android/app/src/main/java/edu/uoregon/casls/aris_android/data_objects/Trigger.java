package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class Trigger {
	public long trigger_id;
	public long instance_id;
	public long scene_id;
	public String type;
	public String name;
	public String title;
	public long icon_media_id;
	public Location location;
	public long distance;
	public Boolean infinite_distance;
	public Boolean wiggle;
	public Boolean show_title;
	public Boolean hidden;
	public Boolean trigger_on_enter;
	public String qr_code;

}
