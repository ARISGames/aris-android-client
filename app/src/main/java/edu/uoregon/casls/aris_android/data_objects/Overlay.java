package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class Overlay {
	public long     overlay_id         = 0;
	public long     media_id           = 0;
	public Location top_left_corner    = new Location("0");
	public Location top_right_corner   = new Location("0");
	public Location bottom_left_corner = new Location("0");

}
