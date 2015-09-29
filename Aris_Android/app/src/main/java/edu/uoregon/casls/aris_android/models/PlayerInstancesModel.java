package edu.uoregon.casls.aris_android.models;

/**
 * Created by smorison on 8/20/15.
 */
public class PlayerInstancesModel extends ARISModel {
	public void clearGameData() {
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void touchPlayerInstances() {
		// call (via two messages) Game.gamePieceReceived()

	}
}
