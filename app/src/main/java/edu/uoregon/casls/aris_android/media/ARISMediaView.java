package edu.uoregon.casls.aris_android.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.services.ARISMediaLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ARISMediaView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ARISMediaView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ARISMediaView extends Fragment {

	public Media media;
	private Bitmap image;
	public  VideoView      videoView;
	public  ImageView      imageView;
	private MediaPlayer    mediaPlayer;
	private Bitmap         bitmap;
	private ProgressDialog pDialog;
	public View fragView;

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
	ARISMediaContentType contentType;

	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

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
	public static ARISMediaView newInstance(String param1, String param2) {
		ARISMediaView fragment = new ARISMediaView();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ARISMediaView() {
		// Required empty public constructor
	}

	// Don't forget to init context!
	public ARISMediaView(GamePlayActivity gamePlayActivity) {
		initContext(gamePlayActivity);
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		fragView = inflater.inflate(R.layout.fragment_arismedia_view, container, false);

		return fragView;
	}



	public void setMedia(Media m)
	{
		if(m.data == null)
		{
			this.clear();
			media = m;
//			this.addSpinner();
//			if(selfDelegateHandle) [selfDelegateHandle invalidate];
//			selfDelegateHandle = [[ARISDelegateHandle alloc] initWithDelegate:self];
			ARISMediaLoader mediaLoader = new ARISMediaLoader(mGamePlayAct);
			mediaLoader.loadMedia(m, null);
//			[_SERVICES_MEDIA_ loadMedia:m delegateHandle:selfDelegateHandle]; //calls 'mediaLoaded' upon complete
			return;
		}
		this.clear();
		media = m;
		this.displayMedia();
	}

	public void mediaLoaded(Media m)
	{
		this.setMedia(m);
	}

	public void setImage(Bitmap i)
	{
		this.clear();
		image = i;
		this.displayImage();
	}

	public void clear()
	{
//		if(selfDelegateHandle) { [selfDelegateHandle invalidate]; selfDelegateHandle = null; }
		image = null;
		media = null;
		this.removeSpinner();
		this.removePlayIcon();
		if(videoView != null) {
			videoView.suspend();
			videoView.setVisibility(View.INVISIBLE);
//			avVC.view removeFromSuperview(); avVC = null;
		}
		if(imageView != null) {
			imageView.setVisibility(View.INVISIBLE); // GONE?
//			imageView removeFromSuperview(); imageView = null;
		}
	}

	public void displayMedia() //simply routes to displayImage, displayVideo, or displayAudio
	{
		String type = media.type();
		switch (contentType)
		{
			case ARISMediaContentTypeThumb:
				if (type.contentEquals("IMAGE"))
			{
				String dataType = this.contentTypeForImageData(media.data);
				if     (dataType.contentEquals("image/gif"))
				{
					image = UIImage animatedImageWithAnimatedGIFData:media.data);
					this.displayImage];
				}
				else if(dataType.contentEquals("image/jpeg") ||
				dataType.contentEquals("image/png"))
				{
					image = UIImage imageWithData:media.thumb];
					this.displayImage];
				}
			}
			else if(type.contentEquals("VIDEO"))
			{
				image = UIImage imageWithData:media.thumb];
				this.displayImage];
			}
			else if(type.contentEquals("AUDIO"))
			{
				image = UIImage imageWithData:media.thumb];
				this.displayImage];
			}
			break;
			case ARISMediaContentTypeFull:
			case ARISMediaContentTypeDefault:
			default:
				if(type.contentEquals("IMAGE"))
			{
				String dataType = this.contentTypeForImageData:media.data];
				if     (dataType.contentEquals("image/gif"))
				{
					image = UIImage animatedImageWithAnimatedGIFData:media.data];
					this.displayImage];
				}
				else if(dataType.contentEquals("image/jpeg") ||
				dataType.contentEquals("image/png"))
				{
					image = UIImage imageWithData:media.data];
					this.displayImage];
				}
			}
			else if(type.contentEquals("VIDEO"))
			this.displayVideo:media];
			else if(type.contentEquals("AUDIO"))
			this.displayAudio:media];
			break;
		}
	}

	public void displayImage()
	{
//		imageView removeFromSuperview();
//		imageView = UIImageView alloc] init];
//		this.addSubview:imageView];
//		if(playIcon) this.addPlayIcon]; //to ensure it's on top of imageView
//		imageView setImage:image];
//		this.conformFrameToMode];

		imageView = (ImageView) fragView.findViewById(R.id.imgvw_media_image);
		// show an image file from interal storage...
		//preview.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Echo/Images/"+file_name));
		imageView.setImageDrawable(getResources().getDrawable(R.drawable.raw_image_sample));
		imageView.setVisibility(View.VISIBLE);

	}

	public void displayVideo(Media m)
	{
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
		videoView.setVideoURI(Uri.parse("android.resource://" + mGamePlayAct.getPackageName() + "/" + R.raw.raw_video_sample));
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				videoView.start();
			}
		});
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {

			}
		});
		videoView.setVisibility(View.VISIBLE);

	}

	public void displayAudio(Media m)
	{
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
	}

	public void playbackFinished()
	{
		this.stop];
		if(n.userInfo objectForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue] == MPMovieFinishReasonUserExited)
		if(delegate && (NSObject )delegate respondsToSelector:@selector(ARISMediaViewFinishedPlayback:)])
		delegate ARISMediaViewFinishedPlayback:self];
	}

	public void addPlayButton() // addPlayIcon in iOS
	{
		if(playIcon) this.removePlayIcon];
		playIcon = UIImageView alloc] initWithImage:UIImage imageNamed:@"play.png"]];
		playIcon addGestureRecognizer:UITapGestureRecognizer alloc] initWithTarget:self action:@selector(playIconTouched)]];
		playIcon.userInteractionEnabled = YES;
		this.centerPlayIcon];
		playIcon.contentMode = UIViewContentModeScaleAspectFit;
		this.addSubview:playIcon];
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
