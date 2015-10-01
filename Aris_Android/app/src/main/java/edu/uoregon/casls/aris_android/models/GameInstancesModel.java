package edu.uoregon.casls.aris_android.models;

import edu.uoregon.casls.aris_android.GamePlayActivity;

/**
 * Created by smorison on 9/29/15.
 */
public class GameInstancesModel extends ARISModel {

	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 1;
	}

}
