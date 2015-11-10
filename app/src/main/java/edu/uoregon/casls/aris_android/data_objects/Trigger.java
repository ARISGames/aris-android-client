package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class Trigger {
	public long trigger_id;
	public long requirement_root_package_id;
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
	public long seconds;
	public long time_left;

	public Trigger() {
		trigger_id = 0;
		requirement_root_package_id = 0;
		instance_id = 0;
		scene_id = 0;
		type = "IMMEDIATE";
		name = "";
		title = "";
		icon_media_id = 0;
		location = new Location("0");
		distance = 10;
		infinite_distance = false;
		wiggle = false;
		show_title = false;
		hidden = false;
		trigger_on_enter = false;
		qr_code = "";
		seconds = 0;
		time_left = 0;

	}

	public Boolean mergeDataFromTrigger(Trigger t) //returns whether or not an update occurred
	{
		Boolean e = this.equals(t);//[self trigIsEqual:t];
		trigger_id = t.trigger_id;
		requirement_root_package_id = t.requirement_root_package_id;
		instance_id = t.instance_id;
		scene_id = t.scene_id;
		type = t.type;
		name = t.name;
		title = t.title;
		icon_media_id = t.icon_media_id;
		location = t.location;
		distance = t.distance;
		infinite_distance = t.infinite_distance;
		wiggle = t.wiggle;
		show_title = t.show_title;
		hidden = t.hidden;
		trigger_on_enter = t.trigger_on_enter;
		qr_code = t.qr_code;
		seconds = t.seconds;
		if (time_left > seconds) time_left = seconds;
		return e;
	}

}
