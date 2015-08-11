package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smorison on 7/29/15.
 */
public class User {

	public String user_id;      // is Long in iOS, but never really used in as a long
	public String user_name;
	public String password;
	public String display_name;
	public String email;
	public String media_id;     // is Long in iOS, but never really used in as a long
	public String read_write_key;
	public Location location = new Location("0");

	public User() {
		// empty constructor
	}

	// Construct with json string encoded user data.
	public User(String newUserJsonStr) {
		try {
			initWithJson(new JSONObject(newUserJsonStr));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Construct with json object encoded user data.
	public User(JSONObject jsonNewUser) throws JSONException {
		initWithJson(jsonNewUser);
	}

	public void initWithJson (JSONObject jsonUser) throws JSONException {
		// Ex Author Json: "authors":[{"user_id":"1","user_name":"scott","display_name":"scott","media_id":"0"}]
		if (jsonUser.has("user_id") /*&& !jsonUser.getString("user_id").contentEquals("null")*/)
			user_id = jsonUser.getString("user_id");
		if (jsonUser.has("user_name"))
			user_name = jsonUser.getString("user_name");
		if (jsonUser.has("password"))
			password = jsonUser.getString("password");
		if (jsonUser.has("display_name"))
			display_name = jsonUser.getString("display_name");
		if (jsonUser.has("email"))
			display_name = jsonUser.getString("email");
		if (jsonUser.has("media_id") /*&& !jsonUser.getString("media_id").contentEquals("null")*/)
			media_id = jsonUser.getString("media_id");
		// Not sure if this is the exact json key for user. May need to update this.
		if (jsonUser.has("map_latitude") && !jsonUser.getString("map_latitude").contentEquals("null"))
			location.setLatitude(Double.parseDouble(jsonUser.getString("map_latitude")));
		else if (jsonUser.has("latitude") && !jsonUser.getString("latitude").contentEquals("null"))
			location.setLatitude(Double.parseDouble(jsonUser.getString("latitude")));
		if (jsonUser.has("map_longitude") && !jsonUser.getString("map_longitude").contentEquals("null"))
			location.setLongitude(Double.parseDouble(jsonUser.getString("map_longitude")));
		else if (jsonUser.has("longitude") && !jsonUser.getString("longitude").contentEquals("null"))
			location.setLongitude(Double.parseDouble(jsonUser.getString("longitude")));
	}

	// class specific conversion to json string for stashing into appPrefs or intent bundles.
	public String toJsonStr () {
		JSONObject jsonUser = new JSONObject();
		try {
			jsonUser.put("user_id", user_id);
			jsonUser.put("user_name", user_name);
			jsonUser.put("password", password);
			jsonUser.put("display_name", display_name);
			jsonUser.put("email", email);
			jsonUser.put("media_id", media_id);
			jsonUser.put("read_write_key", read_write_key);
			jsonUser.put("latitude", String.valueOf(location.getLatitude()));
			jsonUser.put("longitude", String.valueOf(location.getLongitude()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonUser.toString();
	}
}
