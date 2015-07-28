package edu.uoregon.casls.aris_android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by smorison on 7/16/15.
 */
public class AppUtils {
	// scott was here and here
	public static final String SERVER_URL_BASE = "http://10.223.178.105"; //localhost
	public static final String SERVER_URL_MOBILE = SERVER_URL_BASE + "/server/json.php";
	public static final String LOGTAG = "ARIS_ANDROID";

	public final static Boolean DEBUG_ON = true; //todo: Make sure to turn this off for release version
	public final static Boolean FAKE_GOOD_LOGIN = false; //todo: Make sure to turn this off for release version

	public static boolean isNetworkAvailable(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if ("WIFI".equals(ni.getTypeName()))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if ("MOBILE".equals(ni.getTypeName()))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

}
