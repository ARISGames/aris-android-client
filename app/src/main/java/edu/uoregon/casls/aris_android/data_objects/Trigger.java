package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class Trigger {
	public long     trigger_id    = 0;
	public long     instance_id   = 0;
	public long     scene_id      = 0;
	public String   type          = "IMMEDIATE";
	public String   name          = "";
	public String   title         = "";
	public long     icon_media_id = 0;
	public Location location      = new Location("0");
	public double   latitude      = 0.0;
	public double   longitude     = 0.0;
	public long     distance      = 10;
	public String   qr_code       = "";
	public long     seconds       = 0;
	public long     time_left     = 0;

	public long requirement_root_package_id = 0;

	//Booleans (as longs for ARIS server json conformance)
	public long infinite_distance = 0; // Boolean as long
	public long wiggle            = 0; // Boolean as long
	public long show_title        = 0; // Boolean as long
	public long hidden            = 0; // Boolean as long
	public long trigger_on_enter  = 0; // Boolean as long

	public Trigger() {
		// deserialize geocoords from discrete values.
		location.setLatitude(latitude);
		location.setLongitude(longitude);
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
		latitude = t.latitude;
		longitude = t.longitude;
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

	public String title() { // is trigger.title ever used anywhere meaningfully?
//		Instance i;
		if (title != null && !title.isEmpty()) return title;
		else if(!name.isEmpty()) return name;
//		else if((i = [_MODEL_INSTANCES_ instanceForId:instance_id]) && i.name) return i.name;
		return "";
	}


}
