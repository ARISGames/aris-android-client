package edu.uoregon.casls.aris_android.tab_controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Media;
import edu.uoregon.casls.aris_android.data_objects.Overlay;
import edu.uoregon.casls.aris_android.data_objects.Trigger;
import edu.uoregon.casls.aris_android.models.InstancesModel;
import edu.uoregon.casls.aris_android.models.TriggersModel;
import edu.uoregon.casls.aris_android.services.ARISMediaLoader;
import edu.uoregon.casls.aris_android.services.MediaResult;


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
//		refreshViewFromModel();
//		refreshModels();
		return v;
	}

	private void refreshModels() {
		mGamePlayAct.mGame.triggersModel.requestPlayerTriggers();
		mGamePlayAct.mGame.overlaysModel.requestPlayerOverlays();
	}

	public GoogleMap mMap;

	private void setUpMap() {
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

						mMap = googleMap; // globalize it (hopefully!)

						// test putting marker on map:
						Trigger trig = new Trigger(marker_latlng2);
//						final Marker marker1 = googleMap.addMarker(new MarkerOptions()
						trig.triggerMarker = googleMap.addMarker(new MarkerOptions()
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_pause)) // first icon I found. really wanted to use a map oriented one.
								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
								.position(marker_latlng2)
						);
//						marker1.setTitle("Marker One");
						trig.triggerMarker.setTitle("Marker One");
						final Marker marker1 = trig.triggerMarker;
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
						refreshViewFromModel();
						refreshModels();

					}
				}
			});
		}

	}

	/*
	Apple vs Google Map Reference notes:
	Apple map "annotations" are "markers" in Google Maps. They specify a single point on the map.
	Apple Overlays are like Google Circles, Polygons or Polylines but Aris only really uses them as circles
	 where the circle represents the trigger distance radius.
	  (not to be confused with Google Map GroundOverlays, which are literally images overlaid on a map area.)
	  iOS Aris' annotationOverlays array therefor will be called markersAndCircles here.
	The Google and Apple versions of polygon-based "overlays" is (apparently) not implemented in ARIS yet.
	  so it's commented out.
	 */

	public List<Trigger> markersAndCircles = new ArrayList<>();

	public void refreshViewFromModel() {
		if (mMap == null) return; // There will be calls arriving before the map is available; ignore them.

		boolean shouldRemove;
		boolean shouldAdd;
		TriggersModel triggersModel = mGamePlayAct.mGame.triggersModel;

		List<Trigger> markersAndCirclesToRemove = new LinkedList<>();
		Instance modelInstance;
		Overlay mapOverlay;
		Overlay modelOverlay;

		//
		//LOCATIONS
		//

		//Remove locations
		for (Trigger mapTrigger : markersAndCircles) {
			shouldRemove = true;
			for (Trigger modelTrigger : triggersModel.playerTriggers) {
				//@formatter:off
				if (mapTrigger.trigger_id == modelTrigger.trigger_id
					&& (mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).infinite_qty != 0
						|| mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).qty > 0
						|| !mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).object_type.equals("ITEM")
					)
				) shouldRemove = false;
				//@formatter:on
			}
			if (shouldRemove) { // remove the trigger point and its circle from the map
				// remove marker
				if (mapTrigger.triggerMarker != null) mapTrigger.triggerMarker.remove();
				// remove trigger zone Circle
				if (mapTrigger.triggerZoneCircle != null) mapTrigger.triggerZoneCircle.remove();
				// add to list of triggers objects to remove from List. (Done afterward to avoid innerloop conflicts
				markersAndCirclesToRemove.add(mapTrigger);
			}
		}
		// remove any triggers designated for deletion;
		for (Trigger toDie : markersAndCirclesToRemove) {
			markersAndCircles.remove(toDie);
		}
		boolean showFirstMarkerTitle = true;
		//Add locations
		for (Trigger modelTrigger : triggersModel.playerTriggers)
		{
			modelInstance = mGamePlayAct.mGame.instancesModel.instanceForId(modelTrigger.instance_id);
			if ( modelInstance.instance_id == 0 || modelInstance.object() == null) continue;
			//@formatter:off
			if (    ( //trigger not eligible for map
					    !modelTrigger.type.equals("LOCATION") || modelTrigger.hidden != 0
				    )
			     || ( //instance not eligible for map
						modelInstance.object_type.equals("ITEM")
				     && modelInstance.infinite_qty == 0
					 && modelInstance.qty <= 0
				    )
			) continue; // move on to next in list
				//@formatter:on

			shouldAdd = true;
			for (Trigger mapTrigger : markersAndCircles) {
				if(mapTrigger.trigger_id == modelTrigger.trigger_id) shouldAdd = false;
			}
			if (shouldAdd) {
				Media m = mGamePlayAct.mMediaModel.mediaForId((modelTrigger.icon_media_id == 0) ? Media.DEFAULT_PLAQUE_ICON_MEDIA_ID : modelTrigger.icon_media_id);
				Bitmap markerIconBitmap;
				// find the bitmap (media.data) in the swirling vortex of ARIS data...
				if (m.data == null) {
					ARISMediaLoader mediaLoader = new ARISMediaLoader(mGamePlayAct);
					mediaLoader.loadMedia(m);
					if (m.data == null) // if loadData didn't give us a bitmap from a local file set to generic icon
						markerIconBitmap = mGamePlayAct.mMediaModel.mediaForId(Media.DEFAULT_PLAQUE_ICON_MEDIA_ID).data;
					else
						markerIconBitmap = m.data;
				}
				else
					markerIconBitmap =  m.data;
				markerIconBitmap = Bitmap.createScaledBitmap(markerIconBitmap, 50, 50, false);

				MarkerOptions markerOptions = new MarkerOptions()
						.title(modelInstance.name())
						.icon(BitmapDescriptorFactory.fromBitmap(markerIconBitmap))
						.anchor(0.5f, 0.5f) // Center the icon
						.position(new LatLng(modelTrigger.latitude, modelTrigger.longitude)
						);

				modelTrigger.triggerMarker = mMap.addMarker(markerOptions);
				if (showFirstMarkerTitle) { // just show the starting point.
					modelTrigger.triggerMarker.showInfoWindow();
					showFirstMarkerTitle = false;
				}

				// add new circle to map

				modelTrigger.triggerZoneCircle = mMap.addCircle(new CircleOptions()
						.center(new LatLng(modelTrigger.latitude, modelTrigger.longitude))
						.radius(modelTrigger.distance)
						.fillColor(Color.argb(128,0,255,110))
						.strokeWidth(1)
				);
				markersAndCircles.add(modelTrigger);
			}
		}
		if (!markersAndCircles.isEmpty()) setOnclickListenerForMarkers();



		//
		//OVERLAYS
		//
// TODO: looks like overlays are an unused concept in Aris. Leaving this un translated code in in the
		// TODO: event that it ever become necessary.
		//Remove overlays
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

		//refresh views (ugly)
		// todo: below
//		[mapView setCenterCoordinate:mapView.region.center animated:NO];
//		if(firstLoad)
//		{
//			if     ([_MODEL_GAME_.map_focus isEqualToString:@"PLAYER"])        [self centerMapOnPlayer];
//			else if([_MODEL_GAME_.map_focus isEqualToString:@"LOCATION"])      [self centerMapOnLoc:_MODEL_GAME_.map_location.coordinate zoom:_MODEL_GAME_.map_zoom_level];
//			else if([_MODEL_GAME_.map_focus isEqualToString:@"FIT_LOCATIONS"]) [self zoomToFitAnnotations:NO];
//		}
//		firstLoad = false;
	}

	private void setOnclickListenerForMarkers() {
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				for (final Trigger trigger : markersAndCircles) {
					Instance  modelInstance = mGamePlayAct.mGame.instancesModel.instanceForId(trigger.instance_id);
					String in = modelInstance.name();
					String mt = marker.getTitle();
					if (modelInstance.name().equals(marker.getTitle())) {
						// get distance from player.
						trigger.setLocationFromExistingLatLng();
						float distance = trigger.location.distanceTo(mGamePlayAct.mPlayer.location);
						if (mGamePlayAct.mGame.map_offsite_mode != 0
							|| trigger.infinite_distance != 0
							|| (distance <= trigger.distance && mGamePlayAct.mPlayer.location != null))
						{
							// temporarily just show a popup dialog instead of fancy on screen icon
							// todo: do fancy icon
							new AlertDialog.Builder(mGamePlayAct)
									.setIcon(mGamePlayAct.getResources().getDrawable(R.drawable.plaque_icon_120))
									.setTitle("View Plaque?")
//									.setMessage("Are you sure you want to quit Aris?")
									.setPositiveButton("View", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											//kill inner map fragment
											FragmentManager fm = getChildFragmentManager();
											Fragment innerMapFragment = fm.findFragmentById(R.id.inner_fragment_map);
											if (innerMapFragment != null) {
												fm.beginTransaction().remove(innerMapFragment).commit();
											}


											// enqueueTrigger
											if (trigger != null) mGamePlayAct.mGame.displayQueueModel.enqueueTrigger(trigger);

										}
									})
									.setNegativeButton("Back", null)
									.show();

							return false;
						}
						else {
							float distanceToWalk = distance - trigger.distance;
							Toast t = Toast.makeText(mGamePlayAct, "You are not in range to interact with this. Walk " + distanceToWalk + "m",
									Toast.LENGTH_LONG);
							t.setGravity(Gravity.CENTER, 0, 0);
							t.show();

						}
					}
				}

				return false;
			}
		});
	}

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

	@Override
	public void onDestroyView() {

		FragmentManager fm = getFragmentManager();

		Fragment innerMapFragment = fm.findFragmentById(R.id.inner_fragment_map);
		if (innerMapFragment != null) {
			fm.beginTransaction().remove(innerMapFragment).commit();
		}

		super.onDestroyView();
	}

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
