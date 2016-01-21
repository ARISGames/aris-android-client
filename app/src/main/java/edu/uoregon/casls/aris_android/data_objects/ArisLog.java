package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class ArisLog { // Renamed from iOS "Log" to avoid name conflict with android.utilities.Log
	public long     log_id     = 0;
	public String   event_type = "MOVE";
	public long     content_id = 0;
	public long     qty        = 0;
	public Location location   = new Location("0");

}
