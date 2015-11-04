package edu.uoregon.casls.aris_android.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.cd_data_objects.MediaCD;

/**
 * Created by smorison on 10/15/15.
 */
public class DBDealer extends SQLiteOpenHelper {

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

	public DBDealer(Context context) {
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

	public boolean insertMedia(MediaCD newMedia) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MEDIA_ID, newMedia.media_id);
		contentValues.put(GAME_ID, newMedia.game_id);
		contentValues.put(LOCAL_URL, newMedia.localURL);
		contentValues.put(REMOTE_URL, newMedia.remoteURL);
		db.insert(MEDIA, null, contentValues);
		return true;
	}

	public Cursor getMedias(String whereClause) {
		SQLiteDatabase db = this.getReadableDatabase();
		Map<Integer, MediaCD> listOfMedia = new HashMap<>();
//		Cursor cursor =  db.rawQuery( "select * from contacts where id="+id+"", null );
		// execute query against the DB
		Cursor cursor =  db.query(DBDealer.MEDIA,    // db name
				DBDealer.MEDIA_ALL_COLS,            // columns to return
				whereClause,                        // e.g. "name = 'Bob' AND (this = 1 OR that >= 33)"
				null,                                // selection Args
				null,                                // group by
				null,                                // having
				null); 							// order by

		return cursor;
	}

	public Integer deleteMedia (Integer mediaId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(MEDIA,
				MEDIA_ID + " = ? ",
				new String[] { Integer.toString(mediaId) });
	}

	public Integer deleteAllMedias() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(MEDIA, null, null);
	}

}