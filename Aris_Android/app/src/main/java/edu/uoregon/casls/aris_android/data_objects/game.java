package edu.uoregon.casls.aris_android.data_objects;

import android.location.Location;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/*
  Created by smorison on 7/28/15.
 */
public class game {
	{
		long game_id;
		String name;
		String desc;
		String tick_script;
		long tick_delay;
		boolean published;
		String type;
		Location location;
		long player_count;

		long icon_media_id;
		long media_id;

		long intro_scene_id;

		List<NameValuePair> authors = new ArrayList<NameValuePair>();
		List<NameValuePair> comments = new ArrayList<NameValuePair>();

		String map_type;
		Location map_location;
		double map_zoom_level;
		boolean map_show_player;
		boolean map_show_players;
		boolean map_offsite_mode;

		boolean notebook_allow_comments;
		boolean notebook_allow_likes;
		boolean notebook_allow_player_tags;

		long inventory_weight_cap;

		// Supporting classes
//		ScenesModel     scenesModel;
//		PlaquesModel    plaquesModel;
//		ItemsModel      itemsModel;
//		DialogsModel    dialogsModel;
//		WebPagesModel   webPagesModel;
//		NotesModel      notesModel;
//		TagsModel       tagsModel;
//		EventsModel     eventsModel;
//		TriggersModel   triggersModel;
//		FactoriesModel  factoriesModel;
//		OverlaysModel   overlaysModel;
//		InstancesModel  instancesModel;
//		PlayerInstancesModel  playerInstancesModel;
//		TabsModel       tabsModel;
//		LogsModel       logsModel;
//		QuestsModel     questsModel;
//		DisplayQueueModel displayQueueModel;
	}

}
