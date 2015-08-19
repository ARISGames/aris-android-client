package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

import java.util.Date;

/**
 * Created by smorison on 8/19/15.
 */
public class Factory {
	public long factory_id = 0;
	public long game_id = 0;
	public String name = "";
	public String desc = "";
	public String object_type = "";
	public long object_id = 0;
	public long seconds_per_production = 0;
	public double production_probability = 0;
	public long max_production = 0;
	public long produce_expiration_time = 0;
	public long produce_expire_on_view = 0;
	public String production_bound_type = "";
	public String location_bound_type = "";
	public long min_production_distance = 0;
	public long max_production_distance = 0;
	public Date production_timestamp = new Date();
	public long requirement_root_package_id = 0;

	public long trigger_distance = 0;
	public String trigger_title = "";
	public long trigger_icon_media_id = 0;
	public Location trigger_location = new Location("0");
	public Boolean trigger_infinite_distance = false;
	public Boolean trigger_wiggle = false;
	public Boolean trigger_show_title = false;
	public Boolean trigger_hidden = false;
	public Boolean trigger_on_enter = false;
	public long trigger_requirement_root_package_id;
	public long trigger_scene_id;

}
