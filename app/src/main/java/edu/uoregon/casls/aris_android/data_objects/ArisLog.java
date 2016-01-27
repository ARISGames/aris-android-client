package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

/**
 * Created by smorison on 8/19/15.
 */
public class ArisLog { // Renamed from iOS "Log" to avoid name conflict with android.utilities.Log
	public long     user_log_id = 0;
	public String   event_type  = "MOVE";
	public long     content_id  = 0;
	public long     qty         = 0;
	public String   created     = "";
	public double   latitude    = 0.0;
	public double   longitude   = 0.0;
	public Location location    = new Location("0");

	public ArisLog() {
		initGeos();
	}

	// deserialize geocoords from discrete values.
	public void initGeos() {
		location.setLatitude(latitude);
		location.setLongitude(longitude);
	}
}

/* Sample json from getLogsForPlayer:

{
    "data": [
        {
            "content_id": "0",
            "created": "2016-01-27 17:42:11",
            "deleted": "0",
            "event_type": "BEGIN_GAME",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583544"
        },
        {
            "content_id": "0",
            "created": "2016-01-27 17:42:33",
            "deleted": "0",
            "event_type": "BEGIN_GAME",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583554"
        },
        {
            "content_id": "0",
            "created": "2016-01-27 17:42:11",
            "deleted": "0",
            "event_type": "MOVE",
            "game_id": "64",
            "group_id": "0",
            "latitude": "43.073127861546",
            "longitude": "-89.409259557724",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583542"
        },
        {
            "content_id": "0",
            "created": "2016-01-27 17:42:33",
            "deleted": "0",
            "event_type": "MOVE",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583556"
        },
        {
            "content_id": "2855",
            "created": "2016-01-27 17:42:12",
            "deleted": "0",
            "event_type": "VIEW_INSTANCE",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583547"
        },
        {
            "content_id": "6191",
            "created": "2016-01-27 17:42:12",
            "deleted": "0",
            "event_type": "VIEW_DIALOG_SCRIPT",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583551"
        },
        {
            "content_id": "6192",
            "created": "2016-01-27 17:42:14",
            "deleted": "0",
            "event_type": "VIEW_DIALOG_SCRIPT",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583553"
        },
        {
            "content_id": "1476",
            "created": "2016-01-27 17:42:12",
            "deleted": "0",
            "event_type": "TRIGGER_TRIGGER",
            "game_id": "64",
            "group_id": "0",
            "latitude": "0",
            "longitude": "0",
            "qty": "0",
            "user_id": "10269",
            "user_log_id": "6583549"
        }
    ],
    "returnCode": 0,
    "returnCodeDescription": null
}


* */