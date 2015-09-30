package edu.uoregon.casls.aris_android.models;

/**
 * Created by smorison on 9/29/15.
 */
public class ARISModel {
	public long n_game_data_received;
	public long n_player_data_received;

	public void requestGameData() { }
	public void requestPlayerData() { }
	public void clearGameData() { this.clearPlayerData(); }
	public void clearPlayerData() { }

	public long nGameDataToReceive() { return 0; }
	public long nPlayerDataToReceive() { return 0; }

	public long nGameDataReceived() {
		if(n_game_data_received > this.nGameDataToReceive()) return this.nGameDataToReceive();
		return n_game_data_received;
	}
	public long nPlayerDataReceived() {
		if(n_player_data_received > this.nPlayerDataToReceive()) return this.nPlayerDataToReceive();
		return n_player_data_received;
	}

	public Boolean gameDataReceived()
	{
		return n_game_data_received >= this.nGameDataToReceive();
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
