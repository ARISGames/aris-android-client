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

	public void backButtonClick (View v) {
		//close and return up stack
		finish();
	}


}
