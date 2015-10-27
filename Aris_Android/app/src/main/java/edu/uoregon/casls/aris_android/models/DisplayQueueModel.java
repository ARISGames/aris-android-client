package edu.uoregon.casls.aris_android.models;

import edu.uoregon.casls.aris_android.GamePlayActivity;

/**
 * Created by smorison on 8/20/15.
 */
public class DisplayQueueModel {
	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public void clear() {

	}

}
