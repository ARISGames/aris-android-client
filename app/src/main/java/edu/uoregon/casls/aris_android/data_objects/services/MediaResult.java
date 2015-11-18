package edu.uoregon.casls.aris_android.data_objects.services;

import android.graphics.Bitmap;

import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.media.ARISDelegateHandle;

/**
 * Created by smorison on 11/18/15.
 */
public class MediaResult {
	public Media media;
	public Bitmap data;  // todo Bitmap or ByteBuffer?
	public URL url;
	// todo convert the NSURLConnection below to something digestible by java.
	// todo   heres a link that might help: https://books.google.com/books?id=2fQBGOU4UfQC&pg=PA232&lpg=PA232&dq=NSURLConnection+in+java&source=bl&ots=y7vr0APHHx&sig=6JiH0pH4iZMQNirPZ54LOj8XM7Y&hl=en&sa=X&ved=0CB0Q6AEwAGoVChMIhaja2fyayQIVRJOUCh1H9Qzs#v=onepage&q=NSURLConnection%20in%20java&f=false
	public URLConnection connection;

	public Date start;
	public long time; // see: http://stackoverflow.com/a/16558666

	public List<ARISDelegateHandle> delegateHandles; //NSArray *delegateHandles;

}
