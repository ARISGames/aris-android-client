package edu.uoregon.casls.aris_android.data_objects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.models.ARISModel;
import edu.uoregon.casls.aris_android.models.DialogsModel;
import edu.uoregon.casls.aris_android.models.EventsModel;
import edu.uoregon.casls.aris_android.models.FactoriesModel;
import edu.uoregon.casls.aris_android.models.GameInstancesModel;
import edu.uoregon.casls.aris_android.models.GroupInstancesModel;
import edu.uoregon.casls.aris_android.models.GroupsModel;
import edu.uoregon.casls.aris_android.models.InstancesModel;
import edu.uoregon.casls.aris_android.models.ItemsModel;
import edu.uoregon.casls.aris_android.models.LogsModel;
import edu.uoregon.casls.aris_android.models.NotesModel;
import edu.uoregon.casls.aris_android.models.OverlaysModel;
import edu.uoregon.casls.aris_android.models.PlaquesModel;
import edu.uoregon.casls.aris_android.models.PlayerInstancesModel;
import edu.uoregon.casls.aris_android.models.QuestsModel;
import edu.uoregon.casls.aris_android.models.RequirementsModel;
import edu.uoregon.casls.aris_android.models.ScenesModel;
import edu.uoregon.casls.aris_android.models.TabsModel;
import edu.uoregon.casls.aris_android.models.TagsModel;
import edu.uoregon.casls.aris_android.models.TriggersModel;
import edu.uoregon.casls.aris_android.models.WebPagesModel;
import edu.uoregon.casls.aris_android.services.PollTimerService;

/*
  Created by smorison on 7/28/15.
 */
public class GameDataLite {

	public long game_id;
	public String name = "";
	public String desc = "";
	public boolean published;
	public String type = "";
	public Location location = new Location("0"); // from iOS; not used?
	public long player_count;

	public long icon_media_id;
	public long media_id;

	public long intro_scene_id;

	public String map_type = "";
	public String map_focus = "";
	public Location map_location = new Location("0");
	public double map_zoom_level;
	public boolean map_show_player;
	public boolean map_show_players;
	public boolean map_offsite_mode;

	public boolean notebook_allow_comments;
	public boolean notebook_allow_likes;
	public boolean notebook_allow_player_tags;

	public long inventory_weight_cap;
	public boolean allow_download;
	public boolean preload_media;
	public long version;

	//local stuff
	public long downloadedVersion = 0;
	public boolean know_if_begin_fresh = false;
	public boolean begin_fresh = false;

}
