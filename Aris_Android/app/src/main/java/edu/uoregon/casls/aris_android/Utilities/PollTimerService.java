package edu.uoregon.casls.aris_android.Utilities;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by smorison on 11/5/15.
 */
public class PollTimerService extends IntentService {
	private boolean runTimer = true;

	public PollTimerService() {
		super("PollTimerService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
//		customHandler.postDelayed(updateTimerThread, 0);
		int i = 1;
		Log.d("SEM", "OnHandleIntent Called. ");
		while (runTimer) {
			sendUpdateMessage(i++);
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception e)
			{
				Log.d("KBR", "SendError: " + e.getMessage());
			}
		}
	}

	private void sendUpdateMessage(int pct) {
		Log.d("SEM", "Broadcasting update message: " + pct);
		Intent intent = new Intent(AppConfig.POLLTIMER_SVC_ACTION);
		intent.putExtra(AppConfig.COMMAND, AppConfig.POLLTIMER_CYCLE_PASS);
		intent.putExtra(AppConfig.DATA, pct);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void sendResultMessage(String data) {
		Log.d("SEM", "Broadcasting result message: " + data);
		Intent intent = new Intent(AppConfig.POLLTIMER_SVC_ACTION);
		intent.putExtra(AppConfig.COMMAND, AppConfig.POLLTIMER_RESULT);
		intent.putExtra(AppConfig.DATA, data);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		runTimer = false;
		sendResultMessage("PollTimerService Has Been Stopped");
		super.onDestroy();
	}
}