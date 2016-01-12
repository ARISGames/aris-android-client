package edu.uoregon.casls.aris_android.models;

import android.util.Log;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 9/29/15.
 */
public class ARISModel {
	public long n_game_data_received;
	public long n_maintenance_data_received;
	public long n_player_data_received;

	public void requestGameData() { } // test comment for git push
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
		long nGameDataToReceive = this.nGameDataToReceive();
		if ( n_game_data_received >= this.nGameDataToReceive()) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    GAMEDATACYCLE  - - - - gameDataReceived. return " + n_game_data_received + " >= " + nGameDataToReceive + " TRUE TRUE");
		}
		else {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    GAMEDATACYCLE  - - - - gameDataReceived. return " + n_game_data_received + " >= " + nGameDataToReceive + " FALSE FALSE");
		}
		return n_game_data_received >= this.nGameDataToReceive();
	}

	public Boolean maintenanceDataReceived() {
		long nMainDataToRcv = this.nMaintenanceDataToReceive();
		long n_maint_data_rcvd = n_maintenance_data_received;
//		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    MAINTCYCLE  - - - - maintenanceDataReceived. return " + nMainDataToRcv + " >= " + n_maint_data_to_rcv + " ??");

		return n_maintenance_data_received >= this.nMaintenanceDataToReceive();
//		if ( n_maintenance_data_received >= this.nMaintenanceDataToReceive()) {
//			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    MAINTCYCLE  - - - - maintenanceDataReceived. return " + n_maint_data_rcvd + " >= " + nMainDataToRcv + " TRUE TRUE");
//			return true;
//		}
//		else {
//			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    MAINTCYCLE  - - - - maintenanceDataReceived. return " + n_maint_data_rcvd + " >= " + nMainDataToRcv + " FALSE FALSE");
//			return false;
//		}
	}

	public Boolean playerDataReceived() {
		long nPlayerDataToRcv = this.nPlayerDataToReceive();
		long n_player_data_rcvd = n_player_data_received;

		if ( n_player_data_received >= this.nPlayerDataToReceive()) {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    PLAYERDATACYCLE  - - - - playerDataReceived. return " + n_player_data_rcvd + " >= " + nPlayerDataToRcv + " TRUE TRUE");
//			return true;
		}
		else {
			Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "    PLAYERDATACYCLE  - - - - playerDataReceived. return " + n_player_data_rcvd + " >= " + nPlayerDataToRcv + " FALSE FALSE");
//			return false;
		}

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
