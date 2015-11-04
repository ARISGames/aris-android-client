package edu.uoregon.casls.aris_android.models;

import edu.uoregon.casls.aris_android.GamePlayActivity;

/**
 * Created by smorison on 9/29/15.
 */
public class ARISModel {
	public long n_game_data_received;
	public long n_maintenance_data_received;
	public long n_player_data_received;

	public void requestGameData() { }
	public void requestMaintenanceData() { }
	public void requestPlayerData() { }
	public void clearGameData() { this.clearPlayerData(); }
	public void clearPlayerData() { }

	public long nGameDataToReceive() { return 0; }
	public long nMaintenanceDataToReceive() { return 0; }
	public long nPlayerDataToReceive() { return 0; }

	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) { // for visibility to game play activity elements
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public long nGameDataReceived() {
		if(n_game_data_received > this.nGameDataToReceive()) return this.nGameDataToReceive();
		return n_game_data_received;
	}

	public long nMaintenanceDataReceived() {
		if (n_maintenance_data_received > this.nMaintenanceDataToReceive()) return this.nMaintenanceDataToReceive();
		return n_maintenance_data_received;
	}

	public long nPlayerDataReceived() {
		if(n_player_data_received > this.nPlayerDataToReceive()) return this.nPlayerDataToReceive();
		return n_player_data_received;
	}

	public Boolean gameDataReceived()
	{
		return n_game_data_received >= this.nGameDataToReceive();
	}

	public Boolean maintenanceDataReceived() {
		return n_maintenance_data_received >= this.nMaintenanceDataToReceive();
	}

	public Boolean playerDataReceived() {
		return n_player_data_received >= this.nPlayerDataToReceive();
	}

	public String serializedName()
	{
		return "aris";
	}

	public String serializeGameData()
	{
		return "";
	}

	public void deserializeGameData(String data) {

	}

	public String serializePlayerData()
	{
		return "";
	}

	public void deserializePlayerData(String data) {

	}

}
