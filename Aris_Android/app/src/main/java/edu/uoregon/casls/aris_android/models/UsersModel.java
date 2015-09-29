package edu.uoregon.casls.aris_android.models;

/**
 * Created by smorison on 9/29/15.
 */
public class UsersModel extends ARISModel {

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 1;
	}
}
