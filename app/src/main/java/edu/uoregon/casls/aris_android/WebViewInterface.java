package edu.uoregon.casls.aris_android;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by smorison on 9/13/16.
 */

public class WebViewInterface {

	public transient GamePlayActivity mGamePlayAct;

	/** Instantiate the interface and set the context */
	public WebViewInterface(Activity act) {
		mGamePlayAct = (GamePlayActivity) act;
	}


	@JavascriptInterface
	public void showToast(String toast) {
		Toast.makeText(mGamePlayAct, "just got javascript: showToast" + toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void ready(String toast) {
		Toast.makeText(mGamePlayAct, "just got javascript ready: " + toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void logout(String toast) {
		Toast.makeText(mGamePlayAct, "just got javascript logout: " + toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void exit(String toast) {
		Toast.makeText(mGamePlayAct, "just got javascript exit: " + toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void vibrate(String toast) {
		Toast.makeText(mGamePlayAct, "just got javascript vibrate: " + toast, Toast.LENGTH_SHORT).show();
	}



}
