package edu.uoregon.casls.aris_android.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.services.ARISMediaLoader;
import edu.uoregon.casls.aris_android.services.AppServices;

public class ARISMediaViewFragment extends Fragment {

	public  Media          media;
	private Bitmap         image;
	public  VideoView      videoView;
	public  ImageView      imageView;
	private MediaPlayer    mediaPlayer;
	private Bitmap         bitmap;
	private ProgressDialog pDialog;
	public  View           fragView;

	public transient GamePlayActivity mGamePlayAct;

	public enum ARISMediaDisplayMode {
		ARISMediaDisplayModeDefault,
		ARISMediaDisplayModeAspectFill,
		ARISMediaDisplayModeStretchFill,
		ARISMediaDisplayModeAspectFit,
		ARISMediaDisplayModeTopAlignAspectFitWidth,
		ARISMediaDisplayModeTopAlignAspectFitWidthAutoResizeHeight
	}

	ARISMediaDisplayMode displayMode;

	public enum ARISMediaContentType {
		ARISMediaContentTypeDefault,
		ARISMediaContentTypeFull,
		ARISMediaContentTypeThumb
	}

	// set to default to start.
	public ARISMediaContentType contentType = ARISMediaContentType.ARISMediaContentTypeDefault;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment ARISMediaView.
	 */
	// TODO: Rename and change types and number of parameters
	public static ARISMediaViewFragment newInstance(String param1, String param2) {
		ARISMediaViewFragment fragment = new ARISMediaViewFragment();
//		Bundle args = new Bundle();
//		args.putString(ARG_PARAM1, param1);
//		args.putString(ARG_PARAM2, param2);
//		fragment.setArguments(args);
		return fragment;
	}

	public ARISMediaViewFragment() {
		// Required empty public constructor
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (getArguments() != null) {
//			mParam1 = getArguments().getString(ARG_PARAM1);
//			mParam2 = getArguments().getString(ARG_PARAM2);
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		fragView = inflater.inflate(R.layout.fragment_arismedia_view, container, false);

		return fragView;
	}


	public void setMedia(Media m) {

		// make sure we have this view inflated
		// in iOS they seem to want to pass the raw binary data for the media from place to place or at least a pointer to it.
		// I think in Android it'll be much easier to just point UI elements at the LocalURL (assuming it's avalailable.)
		// todo: check that we have a valid mediaCD.localURL. if not look for remoteURL and load it (to device?).
		// todo: if no remoteURL? well uhm...

		if ( m.data == null) {
			//dowload remote file and return local URL.
			this.clear();
			media = m;
			ARISMediaLoader mediaLoader = new ARISMediaLoader(mGamePlayAct);
			// mediaLoader.loadMedia will call loadMediaFromMR which will attempt to pull in bitmap (.data) from previously downloaded file
			// if the media file hasn't yet been downloaded loadMediaFromMR will attempt to do so. This is essentially the same sequence as in iOS.
			mediaLoader.loadMedia(m);
		}

//		if (m.data == null) {
//			this.clear();
//			media = m;
////			this.addSpinner();
////			if(selfDelegateHandle) [selfDelegateHandle invalidate];
////			selfDelegateHandle = [[ARISDelegateHandle alloc] initWithDelegate:self];
//			ARISMediaLoader mediaLoader = new ARISMediaLoader(mGamePlayAct);
//			mediaLoader.loadMedia(m);
////			mediaLoader.loadMedia(m, null);
////			[_SERVICES_MEDIA_ loadMedia:m delegateHandle:selfDelegateHandle]; //calls 'mediaLoaded' upon complete
//			return;
//		}
		this.clear();

		media = m;
		this.displayMedia();
	}

	public void mediaLoaded(Media m) {
		this.setMedia(m);
	}

	public void setImageFromDrawableRes(String drawableResourceFileName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile("file:///android_res/drawable/" + drawableResourceFileName, options);
		setImage(bitmap);
	}

	public void setImage(Bitmap i) {
		this.clear();
		image = i;
		this.displayImage();
	}

	public void clear() {
//		if(selfDelegateHandle) { [selfDelegateHandle invalidate]; selfDelegateHandle = null; }
		image = null;
		media = null;
//	todo	this.removeSpinner();
//	todo	this.removePlayIcon();
		if (videoView != null) {
			videoView.suspend();
			videoView.setVisibility(View.INVISIBLE);
//			avVC.view removeFromSuperview(); avVC = null;
		}
		if (imageView != null) {
			imageView.setVisibility(View.INVISIBLE); // GONE?
//			imageView removeFromSuperview(); imageView = null;
		}
	}

	public void displayMedia() { //simply routes to displayImage, displayVideo, or displayAudio

		String type = media.type();
		if (contentType == null) contentType = ARISMediaContentType.ARISMediaContentTypeDefault;
		switch (contentType) {
			case ARISMediaContentTypeThumb:
				if (type.contentEquals("IMAGE")) {
					String dataType = this.getMimeTypeOfFile(media.mediaCD.localURL.toString());
//				if (dataType == null)
//					dataType = this.contentTypeForImageData(media.data);

					if (dataType.contentEquals("image/gif")) { // do a gif display
						this.displayGif();
					}
					else if (dataType.contentEquals("image/jpeg") ||
							dataType.contentEquals("image/png")) {
						this.displayImage();
					}
				}
				else if (type.contentEquals("VIDEO")) {
					displayVideo();
				}
				else if (type.contentEquals("AUDIO")) {
					displayAudio();
				}
				break;
			case ARISMediaContentTypeFull:
			case ARISMediaContentTypeDefault:
			default:
				if (type.contentEquals("IMAGE")) {
					// todo: getting a NPE on media.mediaCD.localURL - debug and fix

					String dataType;
					if (media.mediaCD.localURL != null) {
						dataType = this.getMimeTypeOfFile(media.mediaCD.localURL.toString());
					}
					else if (media.mediaCD.remoteURL != null) {
						dataType = this.getMimeTypeOfFile(media.mediaCD.remoteURL.toString());
					}
					else
						dataType = "image/jpeg"; // just guess - todo: ensure we never have to guess like this.

//					String dataType = this.getMimeTypeOfFile(media.mediaCD.localURL.toString() != null ? media.mediaCD.localURL.toString() : "");
//				    String dataType = this.contentTypeForImageData:media.data];
					if (dataType.contentEquals("image/gif")) { // do a gif display
						this.displayGif();
					}
					else if (dataType.contentEquals("image/jpeg") ||
							dataType.contentEquals("image/png")) {
						this.displayImage();
					}
				}
				else if (type.contentEquals("VIDEO")) {
					displayVideo();
				}
				else if (type.contentEquals("AUDIO")) {
					displayAudio();
				}
				break;
		}
	}

	private void displayGif() {
		WebView webView = (WebView) mGamePlayAct.findViewById(R.id.wv_media_gif);
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		webView.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
		webView.getSettings().setUseWideViewPort(true); // constrain the image horizontally
		// with load from local url
		DisplayMetrics displaymetrics = new DisplayMetrics();
		mGamePlayAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenWidth = displaymetrics.widthPixels;
		String mediaUrl = media.mediaCD.localURL.toString();
//		String mediaUrl = "file:///android_res/drawable/dancing_peaks.gif"; // local sample for testing
		String data = "<html><head></head><body><img width=" + screenWidth + " src=\"" + mediaUrl + "\" /></body></html>";
		webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);

		webView.setVisibility(View.VISIBLE);

	}

	public void displayImage() {
		// NPE Note: Make sure any fragments using this sub-fragment wait until their respective
		// onResume() to call setMedia() or the following line will throw NPE.
		imageView = (ImageView) fragView.findViewById(R.id.imgvw_media_image);
		// show an image file from internal storage...
		if (media.mediaCD.localURL != null)
			imageView.setImageURI(Uri.parse(media.mediaCD.localURL.toString()));
		else if (media.mediaCD.remoteURL != null)
			imageView.setImageURI(Uri.parse(media.mediaCD.remoteURL.toString()));
		imageView.setVisibility(View.VISIBLE);

	}

	public void displayVideo() {
//		this.addPlayIcon();
//
//		videoView = (VideoView) fragView.findViewById(R.id.videoView);
//		videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw_video_sample));
//
//		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//			@Override
//			public void onPrepared(MediaPlayer mp) {
//				videoView.start();
//			}
//		});
//
//		if(m.thumb)
//		{
//			image = UIImage imageWithData:m.thumb];
//			this.displayImage];
//		}
//		if(delegate && (NSObject )delegate respondsToSelector:@selector(ARISMediaViewIsReadyToPlay:)])
//		delegate ARISMediaViewIsReadyToPlay:self];

		videoView = (VideoView) fragView.findViewById(R.id.vidvw_media_video);
		// sample URIs one local, one www
//		videoView.setVideoURI(Uri.parse("https://linguafolio.uoregon.edu/uploads/video/201510/16/61712_20151016-071909_732.mp4"));
//		videoView.setVideoURI(Uri.parse("android.resource://" + mGamePlayAct.getPackageName() + "/" + R.raw.raw_video_sample));
		if (media.mediaCD.localURL != null) {
			videoView.setVideoURI(Uri.parse(media.localURL().toString()));
		} else {
			videoView.setVideoURI(Uri.parse(media.remoteURL().toString()));
		}
		videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(mGamePlayAct, "Media error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				videoView.start();
			}
		});
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// todo send something? activate controls? ET phone home? nothing?
			}
		});
		videoView.setVisibility(View.VISIBLE);

	}

	public void displayAudio() {
		// todo: looks like audio has a potential image component, so I'll need to send the image to the image view
//		this.addPlayIcon();
//
//		avVC = MPMoviePlayerViewController alloc] initWithContentURL:media.localURL];
//		avVC.moviePlayer.shouldAutoplay = NO;
//		avVC.moviePlayer.controlStyle = MPMovieControlStyleNone;
//		_ARIS_NOTIF_LISTEN_(MPMoviePlayerPlaybackDidFinishNotification,self,@selector(playbackFinished:),null);
//		if(m.thumb != null)
//		{
//			image = UIImage imageWithData:m.thumb];
//			this.displayImage];
//		}

//		mediaPlayer = MediaPlayer.create(mGamePlayAct, Uri.parse("https://linguafolio.uoregon.edu/uploads/mp3/201510/16/59678_20151016-000531_888.mp3"));
		mediaPlayer = MediaPlayer.create(mGamePlayAct, Uri.parse(media.mediaCD.localURL.toString()));
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayer.start();
			}
		});

	}

	public void playbackFinished() {
//		this.stop];
//		if(n.userInfo objectForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue] == MPMovieFinishReasonUserExited)
//		if(delegate && (NSObject )delegate respondsToSelector:@selector(ARISMediaViewFinishedPlayback:)])
//		delegate ARISMediaViewFinishedPlayback:self];
	}

	public void addPlayButton() // addPlayIcon in iOS
	{
		// todo: Android wants one?
//		if(playIcon) this.removePlayIcon];
//		playIcon = UIImageView alloc] initWithImage:UIImage imageNamed:@"play.png"]];
//		playIcon addGestureRecognizer:UITapGestureRecognizer alloc] initWithTarget:self action:@selector(playIconTouched)]];
//		playIcon.userInteractionEnabled = YES;
//		this.centerPlayIcon];
//		playIcon.contentMode = UIViewContentModeScaleAspectFit;
//		this.addSubview:playIcon];
	}

	public static String getMimeTypeOfFile(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}

	public String contentTypeForImageData(Bitmap d) {
		int c;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		d.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		c = byteArray[byteArray.length];

//		d getBytes:&c length:1];

		switch (c) {
			case 0xFF:
				return "image/jpeg";
			case 0x89:
				return "image/png";
			case 0x47:
				return "image/gif";
			case 0x49:
			case 0x4D:
				return "image/tiff";
		}
		return null;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		}
		else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
}
