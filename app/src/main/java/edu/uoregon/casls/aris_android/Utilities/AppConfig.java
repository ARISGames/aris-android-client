package edu.uoregon.casls.aris_android.Utilities;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.R;

/**
 * Created by smorison on 8/27/15.
 */
public class AppConfig {

	//	public static final String SERVER_URL_BASE = "http://10.223.178.105"; //localhost
	public static final String SERVER_URL_BASE   = "http://arisgames.org"; //aris server
	public static final String SERVER_URL_MOBILE = SERVER_URL_BASE + "/server/json.php/";
	public static final String LOGTAG            = "ARIS_ANDROID";
	public static final String LOGTAG_D1            = "DEBUG_1";

	public static final Boolean DEBUG_ON        = true; //todo: Make sure to turn this off for release version
	public static final Boolean FAKE_GOOD_LOGIN = false; //todo: Make sure to turn this off for release version

	// PollTimer Consts
	public static final int    POLLTIMER_CYCLE_PASS = 1;
	public static final int    POLLTIMER_RESULT     = 2;
	public static final String SERVER_POLLER_SVC_ACTION  =
			"edu.uoregon.casls.aris_android.ACTION_SERVERPOLLER_SVC";
	public static final String TRIGGER_POLLER_SVC_ACTION =
			"edu.uoregon.casls.aris_android.ACTION_TRIGGERPOLLER_SVC";
	public static final String COMMAND                   = "command";
	public static final String DATA                      = "data";

	public static final String TAG_SERVER_SUCCESS = "success";
	public static final String SVR_RETURN_CODE    = "returnCode";

	public static final String APP_PREFS_FILE_NAME = "LFO_ToGo_Prefs";
	public static final String GAME_DATE_FORMAT    = "yyyy-MM-dd hh:mm:ss";

	public static final String[] gameDrawerList = {
			// moved to strings.xml for proper internationalization potential
	};


	// Must have the same list of elements as the string array in strings.xml and must also have a valid drawable resource.
	public static Map<String, Integer> gameDrawerItemIconByName = new LinkedHashMap<String, Integer>() {{

		put("QUESTS", R.drawable.game_play_todo_2x);
		put("MAP", R.drawable.game_play_map_2x);
		put("INVENTORY", R.drawable.game_play_toolbox_2x);
		put("SCANNER", R.drawable.game_play_qr_icon_2x);
		put("DECODER", R.drawable.game_play_qr_icon_2x);
		put("PLAYER", R.drawable.game_play_id_card_2x);
		put("NOTEBOOK", R.drawable.game_play_notebook_2x);
		put("INVENTORY", R.drawable.game_play_toolbox_2x);

	}};
}
