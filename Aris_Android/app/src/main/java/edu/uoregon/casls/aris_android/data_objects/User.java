package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smorison on 7/29/15.
 */
public class User {

	public long user_id;
	public String user_name;
	public String display_name;
	public String email;
	public long media_id;
	private String read_write_key;
	public Location location = new Location("0");

	
	public User(JSONObject jsonNewUser) throws JSONException {
		initWithJson(jsonNewUser);
	}

	public void initWithJson (JSONObject jsonUser) throws JSONException {
		// Ex Author Json: "authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}]
		if (jsonUser.has("user_id") && !jsonUser.getString("user_id").contentEquals("null"))
			user_id = Long.parseLong(jsonUser.getString("user_id"));
		if (jsonUser.has("user_name"))
			user_name = jsonUser.getString("user_name");
		if (jsonUser.has("display_name"))
			display_name = jsonUser.getString("display_name");
		if (jsonUser.has("email"))
			display_name = jsonUser.getString("email");
		if (jsonUser.has("media_id") && !jsonUser.getString("media_id").contentEquals("null"))
			media_id = Long.parseLong(jsonUser.getString("media_id"));
		// Not sure if this is the exact json key for user. May need to update this.
		if (jsonUser.has("map_latitude") && !jsonUser.getString("map_latitude").contentEquals("null"))
			location.setLatitude(Double.parseDouble(jsonUser.getString("map_latitude")));
		if (jsonUser.has("map_longitude") && !jsonUser.getString("map_longitude").contentEquals("null"))
			location.setLongitude(Double.parseDouble(jsonUser.getString("map_longitude")));
	}
}
