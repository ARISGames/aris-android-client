package edu.uoregon.casls.aris_android;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;


public class ForgotPasswordActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
	}

	public void backButtonClick(View v) {
		// kill activity - return to login
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_right, R.animator.slide_in_from_left);
	}

}
