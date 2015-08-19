package edu.uoregon.casls.aris_android;

import android.content.Context;

/**
 *
 * A centrally available reference for application context
 * Initially created so I can use context related references in non-activity classes.
 * Might be useful for other such circumstances where context is need.
 *
 * Created by smorison on 8/19/15.
 *
 */

public class ArisApp extends android.app.Application  {

	private static ArisApp instance;

	public ArisApp() {
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}

}
