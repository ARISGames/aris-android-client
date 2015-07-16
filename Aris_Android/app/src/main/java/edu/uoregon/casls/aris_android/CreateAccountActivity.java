package edu.uoregon.casls.aris_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;


public class CreateAccountActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		// built in toolbar - painfully difficult to customize, so using custom "titlebar.xml for this now.
		// Todo: saving this code for now just in case I need to use it for something later. delete if unused.
//		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(toolbar);
//		toolbar.setLogo(R.drawable.logo_text_nav);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);

//		overridePendingTransition(R.animator.slide_in_from_right, R.animator.slide_out_to_left);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_create_account, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//
//		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}

	public void backButtonClick(View v) {
		// kill activity - return to login
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_left, R.animator.slide_in_from_left);
	}
}