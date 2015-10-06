package edu.uoregon.casls.aris_android.Utilities;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 10/6/15.
 */
public class Services {

	public GamePlayActivity mGamePlayAct;
	public Game mGame;
	public User mPlayer;

	public Services(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		//convenience references:
		mGame = mGamePlayAct.mGame;
		mPlayer = mGamePlayAct.mPlayer;
	}

	public void fetchInstanceById(long i) {

	}


	public void fetchInstancesForPlayer() {

	}

	public void setQtyForInstanceId(long instance_id, long qty) {

	}

	public void fetchOptionsForPlayerForDialog(long dialog_id, long dialog_script_id) {

	}

	public void fetchRequirementAtoms() {
	}

	public void fetchRequirementAnds() {
	}

	public void fetchRequirementRoots() {

	}
}
