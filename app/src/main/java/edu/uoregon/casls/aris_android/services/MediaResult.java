package edu.uoregon.casls.aris_android.services;

import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpClient;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.media.ARISDelegateHandle;

/**
 * Created by smorison on 11/18/15.
 */
public class MediaResult {
	public Media           media;
	public Bitmap          data;  // todo Bitmap or ByteBuffer?
	public URL             url;

	public AsyncHttpClient connection; // allows tracking of live/uncompleted connections

	public Date start;
	public long time; // see: http://stackoverflow.com/a/16558666

	public List<ARISDelegateHandle> delegateHandles; //NSArray *delegateHandles;

}
