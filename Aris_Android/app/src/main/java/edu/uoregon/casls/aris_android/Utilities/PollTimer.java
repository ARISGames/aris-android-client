package edu.uoregon.casls.aris_android.Utilities;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by smorison on 8/20/15.
 */
public class PollTimer {
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private Runnable mStatusChecker;
	private int UPDATE_INTERVAL = 10000;

	/**
	 * Creates an PollTimer object, that can be used to
	 * perform UIUpdates on a specified time interval.
	 *
	 * @param pollTimer A runnable containing the update routine.
	 */
	public PollTimer(final Runnable pollTimer) {
		mStatusChecker = new Runnable() {
			@Override
			public void run() {
				// Run the passed runnable
				pollTimer.run();
				// Re-run it after the update interval
				mHandler.postDelayed(this, UPDATE_INTERVAL);
			}
		};
	}

	/**
	 * The same as the default constructor, but specifying the
	 * intended update interval.
	 *
	 * @param pollTimer A runnable containing the update routine.
	 * @param interval  The interval over which the routine
	 *                  should run (milliseconds).
	 */
//	public PollTimer(Runnable pollTimer, int interval){
//		UPDATE_INTERVAL = interval;
//		this(pollTimer);
//	}

	/**
	 * Starts the periodical update routine (mStatusChecker
	 * adds the callback to the handler).
	 */
	public synchronized void startUpdates(){
		mStatusChecker.run();
	}

	/**
	 * Stops the periodical update routine from running,
	 * by removing the callback.
	 */
	public synchronized void stopUpdates(){
		mHandler.removeCallbacks(mStatusChecker);
	}
}
