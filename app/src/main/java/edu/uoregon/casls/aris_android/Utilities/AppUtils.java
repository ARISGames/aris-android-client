package edu.uoregon.casls.aris_android.Utilities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.GamesListActivity;

/**
 * Created by smorison on 7/16/15.
 */
public class AppUtils {

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


	public static Location getGeoLocation(Context context) {
		// Get LocationManager object from System Service LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Create a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		// Get the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		// Return Current Location
		return locationManager.getLastKnownLocation(provider);
	}


	public class deltaMap {
		Map<String, Map<String, Object>> deltas;
	}

	public static File appStorageDir(GamePlayActivity gamePlayAct) {
		File appDir = new File(gamePlayAct.getFilesDir().getPath());
		File gameDir = gamePlayAct.getDir(appDir + String.valueOf(gamePlayAct.mGame.game_id), Context.MODE_PRIVATE); //Creating an internal dir;
		return gameDir;
	}

	public static File gameStorageFile(GamePlayActivity gamePlayAct) {
		File appDir = new File(gamePlayAct.getFilesDir().getPath());
		File gameDir = gamePlayAct.getDir(appDir + String.valueOf(gamePlayAct.mGame.game_id), Context.MODE_PRIVATE); //Creating an internal dir;
		File gameFile = new File(gameDir, "game.json"); //Getting a file within the dir.
		return gameFile;
	}

	public static void writeToFileStream(Context context, File file, String stringData) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file.getName(), Context.MODE_PRIVATE));
			outputStreamWriter.write(stringData);
			outputStreamWriter.close();
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static String readFromFileStream(Context context, File file) {

		String ret = "";

		try {
			InputStream inputStream = context.openFileInput(file.getName());

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}

}
