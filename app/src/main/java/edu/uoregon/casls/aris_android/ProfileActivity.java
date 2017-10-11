package edu.uoregon.casls.aris_android;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uoregon.casls.aris_android.data_objects.User;

public class ProfileActivity extends AppCompatActivity {

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			user = new User(extras.getString("user"));
		}

		setContentView(R.layout.activity_profile);
		ImageButton profileBtn = (ImageButton) findViewById(R.id.imgBtn_profile);
		profileBtn.setVisibility(View.INVISIBLE);

		// populate list:
		updateAllViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void onClickLogOut(View v) {
		SharedPreferences.Editor editor = getSharedPreferences("ARIS_LOGIN", 0).edit();
		editor.remove("user_id");
		editor.remove("read_write_key");
		editor.commit();

		Bundle b = this.getIntent().getExtras();
		b.clear(); // delete bundle data:
		Intent i = new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	private void updateAllViews() {
		// populate the list manually for starters, with just Name/Image and PW change.
		LinearLayout llProfileItems = (LinearLayout) findViewById(R.id.ll_profile_list);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// == ROW 1 ==
		View profileItemView = inflater.inflate(R.layout.profile_list_item, null);
		profileItemView.setId(new Integer(0));
		profileItemView.setTag("PublicNameAndImage");
		ImageView ivItemIcon = (ImageView) profileItemView.findViewById(R.id.iv_profile_item_icon);
		TextView tvItemName = (TextView) profileItemView.findViewById(R.id.tv_item_name);
		ivItemIcon.setImageResource(R.drawable.game_play_id_card_small);
		tvItemName.setText("Public Name and Image");
		profileItemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// for now just display a toast:
				Toast.makeText(getApplicationContext(), "Profile Item clicked. Name:, ID: " + 0,
						Toast.LENGTH_LONG).show();
			}
		});
		llProfileItems.addView(profileItemView, 0);

		// == ROW 2 ==
		profileItemView = inflater.inflate(R.layout.profile_list_item, null);
		profileItemView.setId(new Integer(1));
		profileItemView.setTag("ChangePassword");
		ivItemIcon = (ImageView) profileItemView.findViewById(R.id.iv_profile_item_icon);
		tvItemName = (TextView) profileItemView.findViewById(R.id.tv_item_name);
		ivItemIcon.setImageResource(R.drawable.lock_small);
		tvItemName.setText("Change Password");
		profileItemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// for now just display a toast:
				Toast.makeText(getApplicationContext(), "Profile Item clicked. Name:, ID: " + 1,
						Toast.LENGTH_LONG).show();
			}
		});
		llProfileItems.addView(profileItemView, 1);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_profile, menu);
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

	@Override
	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_left, R.animator.slide_in_from_right); // todo: transition flashes a little. Fix?
	}

}
