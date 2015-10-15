package edu.uoregon.casls.aris_android.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by smorison on 10/15/15.
 */
public class DBHelper extends SQLiteOpenHelper {

	public static final String MEDIA = "media";
	public static final String MEDIA_ID = "media_id";
	public static final String GAME_ID = "game_id";
	public static final String USER_ID = "user_id";
	public static final String LOCAL_URL = "localURL";
	public static final String REMOTE_URL = "remoteURL";
	public static final String[] MEDIA_ALL_COLS = {MEDIA_ID, GAME_ID, USER_ID, LOCAL_URL, REMOTE_URL};

	private static final String DATABASE_NAME = "ARIS.db";
	private static final int DATABASE_VERSION = 1;

	// creation SQLite statement
	private static final String DATABASE_CREATE = "create table " + MEDIA
			+ "("
			+ MEDIA_ID + " integer primary key autoincrement, "
			+ GAME_ID + " integer not null, "
			+ USER_ID + " integer, "
			+ LOCAL_URL + " text, "
			+ REMOTE_URL + " text "
			+ ");";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MEDIA);
		onCreate(db);
	}

}