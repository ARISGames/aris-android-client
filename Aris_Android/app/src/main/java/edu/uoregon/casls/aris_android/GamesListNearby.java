package edu.uoregon.casls.aris_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class GamesListNearby extends ActionBarActivity {

	private String mUser_name;
	private String mPassword;
	private String mUser_Id;
	private String mDisplay_name;
	private String mMedia_id;
	private String mRead_write_key;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games_list_nearby);
		if (mUser_name == null || mPassword == null || mUser_Id == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				mUser_name = extras.getString("user_name");
				mPassword = extras.getString("password");
				mUser_Id = extras.getString("user_id");
				mDisplay_name = extras.getString("display_name");
				mMedia_id = extras.getString("media_id");
				mRead_write_key = extras.getString("read_write_key");
			}
			else
				Log.i(AppUtils.LOGTAG, getClass().getSimpleName() + ": extras) was NULL");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_games_list_nearby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
