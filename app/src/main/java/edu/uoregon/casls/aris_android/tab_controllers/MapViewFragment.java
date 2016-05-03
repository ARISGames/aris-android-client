package edu.uoregon.casls.aris_android.tab_controllers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Overlay;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.models.TriggersModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;
	private GamePlayActivity mGamePlayAct   ;

	private SupportMapFragment mSupportMapFragment;
	double latitude = 44.047822;
	double longitude = -123.0695084;
	private final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 22; // arbitrary?
	LatLng marker_latlng  = new LatLng(latitude, longitude);
	LatLng marker_latlng2 = new LatLng(latitude + 0.0008, longitude - 0.0002);


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment GamePlayMapFragment.
	 */
	// TODO: Rename and change types and number of parameters
//	public static GamePlayMapFragment newInstance(String param1, String param2) {
//		GamePlayMapFragment fragment = new GamePlayMapFragment();
//		Bundle args = new Bundle();
//		args.putString(ARG_PARAM1, param1);
//		args.putString(ARG_PARAM2, param2);
//		fragment.setArguments(args);
//		return fragment;
//	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static MapViewFragment newInstance(int sectionNumber) {
		MapViewFragment fragment = new MapViewFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}


	public static MapViewFragment newInstance(String sectionName) {
		MapViewFragment fragment = new MapViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SECTION_NUMBER, sectionName);
		fragment.setArguments(args);
		return fragment;
	}

	public MapViewFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//		if (mGamePlayAct == null) {
////			this.initContext(this.getParentFragment().getActivity());
//		}
//	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof GamePlayActivity){
			mGamePlayAct = (GamePlayActivity) context;
		}
		try {
			mListener = (OnFragmentInteractionListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View v = inflater.inflate(R.layout.fragment_map_view, container, false);
		mGamePlayAct.hideNavBar();

		setUpMap();
		return v;
	}

	private void setUpMap() {
//		mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fl_map_parent);
		mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.inner_fragment_map);
		if (mSupportMapFragment == null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			mSupportMapFragment = SupportMapFragment.newInstance();
			fragmentTransaction.replace(R.id.inner_fragment_map, mSupportMapFragment).commit();
		}

		if (mSupportMapFragment != null) {
			mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {
					if (googleMap != null) {
						if ( ContextCompat.checkSelfPermission(mGamePlayAct, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
							ActivityCompat.requestPermissions(mGamePlayAct, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
									MY_PERMISSION_ACCESS_COURSE_LOCATION );
						}
						if ( Build.VERSION.SDK_INT >= 23 &&
								ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
								&& ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//								&& isLocationEnabled(context)
								) {
							return;
						}

						googleMap.getUiSettings().setAllGesturesEnabled(true);
						googleMap.getUiSettings().setMapToolbarEnabled(false); // gets rid of the google links at the bottom
						googleMap.setPadding(0,0,0,0);
						googleMap.setMyLocationEnabled(true);

						CameraPosition cameraPosition = new CameraPosition.Builder().target(marker_latlng).zoom(17.0f).build();
						CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
						googleMap.moveCamera(cameraUpdate);

						final Marker marker1 = googleMap.addMarker(new MarkerOptions()
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_pause)) // first icon I found. really wanted to use a map oriented one.
								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
								.position(marker_latlng2)
						);
						marker1.setTitle("Marker One");
						googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
							@Override
							public boolean onMarkerClick(Marker marker) {
//								if (marker.getTitle().equalsIgnoreCase(marker1.getTitle())) {
								Log.d("SEM", "Marker Clicked:" + marker.getTitle() + ", Id: " + marker.getId() + ", marker1 id: " + marker1.getId());
								if (marker.getId() == marker1.getId()) {
									Log.d("SEM", "Marker Clicked:" + marker.getTitle() + ", Id: " + marker.getId());
//									if (marker.isInfoWindowShown()) {
//										marker.hideInfoWindow();
//										return false;
//									}
//									else {
//										marker.showInfoWindow();
//										return false;
//									}
								}
								return false;
							}
						});
					}
				}
			});
		}

	}

//	public void refreshViewFromModel()
//	{
//		if (mapView == null) return;
//
//		boolean shouldRemove;
//		boolean shouldAdd;
//		TriggersModel triggersModel = mGamePlayAct.mGame.triggersModel;
//
//		Trigger mapTrigger;
//		Trigger modelTrigger;
//		Instance modelInstance;
//		Overlay mapOverlay;
//		Overlay modelOverlay;
//
//		//
//		//LOCATIONS
//		//
//
//		//Remove locations
//		for(long i = 0; i <  annotationOverlays.count; i++)
//		{
//			mapTrigger = [self mvaoAt:i].annotation;
//			shouldRemove = true;
//			for(long j = 0; j < _MODEL_TRIGGERS_.playerTriggers.count; j++)
//			{
//				modelTrigger = _MODEL_TRIGGERS_.playerTriggers[j];
//				if(mapTrigger.trigger_id == modelTrigger.trigger_id &&
//						(
//								[_MODEL_INSTANCES_ instanceForId:mapTrigger.instance_id].infinite_qty ||
//				[_MODEL_INSTANCES_ instanceForId:mapTrigger.instance_id].qty > 0 ||
//					![[_MODEL_INSTANCES_ instanceForId:mapTrigger.instance_id].object_type isEqualToString:@"ITEM"]
//				)
//				) shouldRemove = false;
//			}
//			if(shouldRemove)
//			{
//				MapViewAnnotationOverlay *mvao = [self mvaoAt:i];
//				[mapView removeAnnotation:mvao.annotation];
//				[mapView removeOverlay:mvao.overlay];
//				[annotationOverlays removeObject:mvao];
//				i--;
//			}
//		}
//		//Add locations
//		for(long i = 0; i < _MODEL_TRIGGERS_.playerTriggers.count; i++)
//		{
//			modelTrigger = _MODEL_TRIGGERS_.playerTriggers[i];
//			modelInstance = [_MODEL_INSTANCES_ instanceForId:modelTrigger.instance_id];
//			if(modelInstance.instance_id == 0 || !modelInstance.object) continue;
//
//			if(
//					( //trigger not eligible for map
//							![modelTrigger.type isEqualToString:@"LOCATION"] || modelTrigger.hidden
//			)
//			||
//			( //instance not eligible for map
//					[modelInstance.object_type isEqualToString:@"ITEM"] &&
//			!modelInstance.infinite_qty &&
//					modelInstance.qty <= 0
//			)
//			) continue;
//
//			shouldAdd = YES;
//			for(long j = 0; j < annotationOverlays.count; j++)
//			{
//				mapTrigger = [self mvaoAt:j].annotation;
//				if(mapTrigger.trigger_id == modelTrigger.trigger_id) shouldAdd = NO;
//			}
//			if(shouldAdd)
//			{
//				MKCircle *circle = [MKCircle circleWithCenterCoordinate:modelTrigger.location.coordinate radius:(modelTrigger.infinite_distance ? 0 : modelTrigger.distance)];
//				MapViewAnnotationOverlay *mvao = [[MapViewAnnotationOverlay alloc] initWithAnnotation:modelTrigger overlay:circle];
//
//				[mapView addAnnotation:mvao.annotation];
//				[mapView addOverlay:mvao.overlay];
//				[annotationOverlays addObject:mvao];
//			}
//		}
//
//
//		//
//		//OVERLAYS
//		//
//
//		//Remove overlays
//		for(long i = 0; i < overlays.count; i++)
//		{
//			mapOverlay = [self mvoAt:i];
//			shouldRemove = YES;
//			for(long j = 0; j < _MODEL_OVERLAYS_.playerOverlays.count; j++)
//			{
//				modelOverlay = _MODEL_OVERLAYS_.playerOverlays[j];
//				if(mapOverlay.overlay_id == modelOverlay.overlay_id) shouldRemove = NO;
//			}
//			if(shouldRemove)
//			{
//				[mapView removeOverlay:mapOverlay];
//				[overlays removeObject:mapOverlay];
//				i--;
//			}
//		}
//		//Add overlays
//		for(long i = 0; i < _MODEL_OVERLAYS_.playerOverlays.count; i++)
//		{
//			modelOverlay = _MODEL_OVERLAYS_.playerOverlays[i];
//			shouldAdd = YES;
//			for(long j = 0; j < overlays.count; j++)
//			{
//				mapOverlay = [self mvoAt:j];
//				if(mapOverlay.overlay_id == modelOverlay.overlay_id) shouldAdd = NO;
//			}
//			if(shouldAdd)
//			{
//				[mapView addOverlay:modelOverlay];
//				[overlays addObject:modelOverlay];
//			}
//		}
//
//		//refresh views (ugly)
//		[mapView setCenterCoordinate:mapView.region.center animated:NO];
//		if(firstLoad)
//		{
//			if     ([_MODEL_GAME_.map_focus isEqualToString:@"PLAYER"])        [self centerMapOnPlayer];
//			else if([_MODEL_GAME_.map_focus isEqualToString:@"LOCATION"])      [self centerMapOnLoc:_MODEL_GAME_.map_location.coordinate zoom:_MODEL_GAME_.map_zoom_level];
//			else if([_MODEL_GAME_.map_focus isEqualToString:@"FIT_LOCATIONS"]) [self zoomToFitAnnotations:NO];
//		}
//		firstLoad = false;
//	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			mListener = (OnFragmentInteractionListener) activity;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}
//
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct;
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
		public void onFragmentInteraction(Uri uri);
		public void onSecondFragButtonClick(String message);
	}

}
