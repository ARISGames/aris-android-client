package edu.uoregon.casls.aris_android.Utilities;

/**
 * Created by smorison on 8/27/15.
 */
public class Constant {

	//	public static final String SERVER_URL_BASE = "http://10.223.178.105"; //localhost
	public static final String SERVER_URL_BASE = "http://arisgames.org"; //aris server
	public static final String SERVER_URL_MOBILE = SERVER_URL_BASE + "/server/json.php/";
	public static final String LOGTAG = "ARIS_ANDROID";

	public final static Boolean DEBUG_ON = true; //todo: Make sure to turn this off for release version
	public final static Boolean FAKE_GOOD_LOGIN = false; //todo: Make sure to turn this off for release version
	public static final String TAG_SERVER_SUCCESS = "success";

	public static final int UPDATE_PROGRESS = 1;
	public static final int POLLTIMER_RESULT = 2;
	public static final String POLLTIMER_FILTER =
			"edu.uoregon.casls.aris_android.REQUEST_PROCESSED";
	public static final String COMMAND = "command";
	public static final String DATA = "data";
}
