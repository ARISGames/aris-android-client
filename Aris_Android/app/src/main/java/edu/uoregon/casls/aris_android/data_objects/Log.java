package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class Log {
	public long log_id;
	public String event_type;
	public long content_id;
	public long qty;
	public Location location = new Location("0");

}
