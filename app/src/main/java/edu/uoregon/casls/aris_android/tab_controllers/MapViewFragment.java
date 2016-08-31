package edu.uoregon.casls.aris_android.tab_controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import android.view.InflateException;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.R;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
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
	private GamePlayActivity mGamePlayAct;

	private SupportMapFragment mSupportMapFragment;
	double latitude = 44.047822;
	double longitude = -123.0695084;
	private final static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 22; // arbitrary?
	LatLng marker_latlng  = new LatLng(latitude, longitude);
	LatLng player_latlng  = new LatLng(0,0); // todo: set to current player location
	LatLng marker_latlng2 = new LatLng(latitude + 0.0008, longitude - 0.0002);

	public GoogleMap mMap;
	private boolean firstLoad = true;

	public class MapViewMarkerCircle {
		public Marker triggerMarker; // Google Marker - set when map is generated (aka annotation in iOS)
		public Circle triggerZoneCircle; // Circle is like anMKOverlay in iOS
		public MapViewMarkerCircle(Marker m, Circle c) {triggerMarker = m; triggerZoneCircle = c;}
	}
	public List<Trigger> markersAndCircles = new ArrayList<>();
//	public List<MapViewMarkerCircle> markerCirclesList = new ArrayList<>();
	public Map<Long, MapViewMarkerCircle> markerCircleByTrigId = new HashMap<>();
//	public List<Circle> circleList = new ArrayList<>();

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
	public void onCreate(Bundle savedInstanceState) { // similar to viewWillAppear in iOS (?)
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
//			mGamePlayAct = (GamePlayActivity) this.getActivity();
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

	private static View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) { // similar to viewDidAppear or possibly viewWillAppear in iOS
		// check for preexisting view; remove it if one is found. Otherwise get Duplicate Fragment error.
		if (v != null) {
			ViewGroup parent = (ViewGroup) v.getParent();
			if (parent != null)
				parent.removeView(v);
		}

		try {
			// Inflate the layout for this fragment
			v = inflater.inflate(R.layout.fragment_map_view, container, false);
		}
		catch(InflateException ie) {
			// do nothing; just go on.
		}

		mGamePlayAct.hideNavBar();
		//get current location of player
		Location l = AppUtils.getGeoLocation(mGamePlayAct);
		player_latlng = new LatLng(l.getLatitude(), l.getLongitude());

		setUpMap();
//		if (mMap != null) {
//			refreshViewFromModel();
//			refreshModels();
//		}

		return v;
	}

	private void refreshModels() {
		mGamePlayAct.mGame.triggersModel.requestPlayerTriggers();
		mGamePlayAct.mGame.overlaysModel.requestPlayerOverlays();
	}

	public void triggersInvalidated(List<Trigger> invalidated) {

		for (Trigger invalidatedTrigger : invalidated) {
//			List<Trigger> markersAndCirclesToRemove = new LinkedList<>();
			Trigger markerToRemove = null;
			for (Trigger mapTrigger : markersAndCircles) {
//				mapTrigger = [self mvaoAt:j].annotation;
				if (mapTrigger.trigger_id == invalidatedTrigger.trigger_id)
					markerToRemove = mapTrigger; // mvao = [self mvaoAt:j]; Note mvaoAt is aniOS helper that returns the markersAndCircles item at an array index (markersAndCircles here is annotationOverlays in iOS)
			}
			if (markerToRemove != null) { //if(mvao)
				if (mMap != null) {
					if (markerToRemove.triggerMarker != null) {
//						Log.d(AppConfig.LOGTAG_D2, "111 Removing Marker ID:" + markerToRemove.triggerMarker.getId()+" TriggerID:"+markerToRemove.trigger_id);
						markerToRemove.triggerMarker.remove();// [mapView removeAnnotation:mvao.annotation];
					}
					if (markerToRemove.triggerZoneCircle != null)
						markerToRemove.triggerZoneCircle.remove(); // [mapView removeOverlay:mvao.overlay];
					markersAndCircles.remove(markerToRemove); // [annotationOverlays removeObject:mvao];
					markerCircleByTrigId.get(markerToRemove.trigger_id).triggerMarker.remove();
					markerCircleByTrigId.get(markerToRemove.trigger_id).triggerZoneCircle.remove();
					markerCircleByTrigId.remove(markerToRemove.trigger_id);
				}
			}
		}
	}

	public void clearLocalData () {
		// stub. not used
	}

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


						CameraPosition cameraPosition = new CameraPosition.Builder().target(player_latlng).zoom(17.0f).build();
						CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
						googleMap.moveCamera(cameraUpdate);

						mMap = googleMap; // globalize it (hopefully!)

//						// test putting marker on map:// manual trigger set for POC.
//						Trigger trig = new Trigger(marker_latlng2);
////						final Marker marker1 = googleMap.addMarker(new MarkerOptions()
//						trig.triggerMarker = googleMap.addMarker(new MarkerOptions()
//								.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_pause)) // first icon I found. really wanted to use a map oriented one.
//								.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//								.position(marker_latlng2)
//						);
////						marker1.setTitle("Marker One");
//						trig.triggerMarker.setTitle("Marker One");
//						final Marker marker1 = trig.triggerMarker;
//						googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//							@Override
//							public boolean onMarkerClick(Marker marker) {
////								if (marker.getTitle().equalsIgnoreCase(marker1.getTitle())) {
//								Log.d("SEM", "Marker Clicked:" + marker.getTitle() + ", Id: " + marker.getId() + ", marker1 id: " + marker1.getId());
//								if (marker.getId() == marker1.getId()) {
//									Log.d("SEM", "Marker Clicked:" + marker.getTitle() + ", Id: " + marker.getId());
////									if (marker.isInfoWindowShown()) {
////										marker.hideInfoWindow();
////										return false;
////									}
////									else {
////										marker.showInfoWindow();
////										return false;
////									}
//								}
//								return false;
//							}
//						});
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

	int testcount = 0;

	public void refreshViewFromModel() {

		if (mMap == null) return; // There might be calls arriving before the map is available; ignore them.

		boolean shouldRemove;
		boolean shouldAdd;
		// reset old markersAndCircles; start fresh each time. ?
//		markersAndCircles.clear();

		TriggersModel triggersModel = mGamePlayAct.mGame.triggersModel;
		Trigger mapTrigger;

		List<Trigger> markersAndCirclesToRemove = new LinkedList<>();
		Instance modelInstance;
//		Overlay mapOverlay;
//		Overlay modelOverlay;

		//
		//LOCATIONS
		//

		//Remove locations
//		Log.d(AppConfig.LOGTAG_D2, "Looping through all markersAndCircles to look for triggers to REMOVE...");
//		for (Trigger mapTrigger : markersAndCircles) { // markersAndCircles is "annotationOverlays" in iOS to match GoogleMap dialect.
		for (int i=0; i < markersAndCircles.size(); i++) { // markersAndCircles is "annotationOverlays" in iOS to match GoogleMap dialect.
			mapTrigger = markersAndCircles.get(i);
			shouldRemove = true;

//			Instance inst = mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id);
//			Log.d(AppConfig.LOGTAG_D2, "Inst inf_qty:" + i.infinite_qty +" Inst qty:" + i.qty + " Inst obj_type:" + i.object_type.toString());
			for (Trigger modelTrigger : triggersModel.playerTriggers) {
//				Log.d(AppConfig.LOGTAG_D2, "Trying to match:" + mapTrigger.trigger_id+"=?="+modelTrigger.trigger_id + (mapTrigger.trigger_id == modelTrigger.trigger_id ? " MATCH!" : ""));
				//@formatter:off
				if (mapTrigger.trigger_id == modelTrigger.trigger_id
					&& (
						   mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).infinite_qty != 0
						|| mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).qty > 0
						|| !mGamePlayAct.mGame.instancesModel.instanceForId(mapTrigger.instance_id).object_type.equals("ITEM")
					   )
				) {
//					if (mapTrigger.triggerMarker == null) Log.d(AppConfig.LOGTAG_D2, "...which has a NULL triggerMarker");
					shouldRemove = false;
				}
				//@formatter:on
			}
//			if (shouldRemove) Log.d(AppConfig.LOGTAG_D2, "YES Marked for REMOVE TriggerID:"+mapTrigger.trigger_id);
//			else Log.d(AppConfig.LOGTAG_D2, "NOT Marked for remove TriggerID:"+mapTrigger.trigger_id);
//			if (mapTrigger.triggerMarker == null) Log.d(AppConfig.LOGTAG_D2, "...which has a NULL triggerMarker!");

			if (shouldRemove) { // remove the trigger point and its circle from the map
				// for some reason the triggerMarker and triggerZoneMarker references get nulled.
				// When that happens find the marker/circle reference in the markerCircleByTrigId hashmap. todo: pretty this up a bit.

				// remove marker
				if (mapTrigger.triggerMarker != null) {
//					Log.d(AppConfig.LOGTAG_D2, "222 REMOVING Marker ID:" + mapTrigger.triggerMarker.getId()+" TriggerID:"+mapTrigger.trigger_id);
					mapTrigger.triggerMarker.remove();
				}
				else {
					int size = markerCircleByTrigId.size();
					String markerId = markerCircleByTrigId.get(mapTrigger.trigger_id).triggerMarker.getId();
//					Log.d(AppConfig.LOGTAG_D2, "222 Marker ID: WAS NULL. NOT removing TriggerID:"+mapTrigger.trigger_id);
					markerCircleByTrigId.get(mapTrigger.trigger_id).triggerMarker.remove();
				}
				// remove trigger zone Circle
				if (mapTrigger.triggerZoneCircle != null)
					mapTrigger.triggerZoneCircle.remove();
				else
					markerCircleByTrigId.get(mapTrigger.trigger_id).triggerZoneCircle.remove();

//				if (mapTrigger.triggerMarker != null) {
					Trigger mvao = markersAndCircles.get(i); // same as mapTrigger set above???
					markersAndCircles.remove(mvao); // [annotationOverlays removeObject:mvao];
					i--; // remove one from running count.
					// add to list of triggers objects to remove from List. (Done afterward to avoid innerloop conflicts
//				markersAndCirclesToRemove.add(mapTrigger);
//				}
				markerCircleByTrigId.remove(mapTrigger.trigger_id);

			}
		}
		// remove any triggers designated for deletion;
		// this is done directly in the shouldRemove block in iOS. Done separately here to avoid deforming the loop base while looping.
//		for (Trigger markerToRemove : markersAndCirclesToRemove) {
////			if (markerToRemove.triggerMarker != null) {
////				Log.d(AppConfig.LOGTAG_D2, "333 Removing Marker ID:" + markerToRemove.triggerMarker.getId());
////				markerToRemove.triggerMarker.remove();// [mapView removeAnnotation:mvao.annotation];
////			}
////			else {
////				Log.d(AppConfig.LOGTAG_D1, "triggerMarker to remove was null for this trigger.");
////			}
////			if (markerToRemove.triggerZoneCircle != null)
////				markerToRemove.triggerZoneCircle.remove(); // [mapView removeOverlay:mvao.overlay];
//			Log.d(AppConfig.LOGTAG_D2, "Removing from markersAndCircles TrID:" + markerToRemove.trigger_id);
//			markersAndCircles.remove(markerToRemove);
//		}
		boolean showFirstMarkerTitle = true;

		//Add locations
//		Log.d(AppConfig.LOGTAG_D2, "Looping through all triggersModel.playerTriggers to look for triggers to ADD...");
		for (Trigger modelTrigger : triggersModel.playerTriggers) { // walk through all playerTriggers
			modelInstance = mGamePlayAct.mGame.instancesModel.instanceForId(modelTrigger.instance_id); // get instance
			if ( modelInstance.instance_id == 0 || modelInstance.object() == null) continue; // bogus instance? skip it
			//@formatter:off
			if (    ( //trigger not eligible for map
					    !modelTrigger.type.equals("LOCATION") || modelTrigger.hidden != 0
				    )
			     || ( //instance not eligible for map
						modelInstance.object_type.equals("ITEM")
				     && modelInstance.infinite_qty == 0
					 && modelInstance.qty <= 0
				    )
			) continue; // not eligible, move on to next in list
				//@formatter:on

			shouldAdd = true; // we found a good one to add.
			//todo: in iOS the map view retains the old markers when redrawn after going out to a plaque (for example)
			//todo: in Android the map is redrawn fresh each time, so decide if we need the loop below that excludes
			//todo: markers that have previously been added. Perhaps we can maintain a list of those that are visible
			//todo: instead of checking those that are just in a data list.
			for (Trigger mapTrig : markersAndCircles) { // look through any/all locations already in list
				if (mapTrig.trigger_id == modelTrigger.trigger_id) shouldAdd = false; // revoke their shouldAdd pass if they're already in the list.
			}
			if (shouldAdd) { // having vetted this location as one to be added...
				// get any custom icon media if there was a valid media id provided; otherwise use default icon.
				Media m;
				if (modelTrigger.icon_media_id(mGamePlayAct.mGame) != 0 )
					m = mGamePlayAct.mMediaModel.mediaForId(modelTrigger.icon_media_id(mGamePlayAct.mGame));
				else
					m = mGamePlayAct.mMediaModel.mediaForId(Media.STAR_BLUE_ICON_MEDIA_ID);
				//== 0) ? Media.DEFAULT_PLAQUE_ICON_MEDIA_ID : modelTrigger.icon_media_id);
				Bitmap markerIconBitmap; // reminder: a "marker" here in Android, is an "annotation" in iOS
				// find the bitmap (media.data) in the swirling vortex of ARIS data.
				// (this is more involved in Android than iOS)
				if (m.data == null) {
					ARISMediaLoader mediaLoader = new ARISMediaLoader(mGamePlayAct);
					mediaLoader.loadMedia(m);
					if (m.data == null) // if loadMedia() didn't give us a bitmap from a local file, set to generic icon
						markerIconBitmap = mGamePlayAct.mMediaModel.mediaForId(Media.DEFAULT_PLAQUE_ICON_MEDIA_ID).data;
					else
						markerIconBitmap = m.data;
				}
				else
					markerIconBitmap = m.data; // image bitmap data looks valid so we'll use it for the icon
				markerIconBitmap = Bitmap.createScaledBitmap(markerIconBitmap, 50, 50, false); // scale it to map marker size
				// set various marker properties
				MarkerOptions markerOptions = new MarkerOptions()
						.title(modelInstance.name())
						.icon(BitmapDescriptorFactory.fromBitmap(markerIconBitmap))
						.anchor(0.5f, 0.5f) // Center the icon
						.position(new LatLng(modelTrigger.latitude, modelTrigger.longitude)
						);

				// todo for future consideration, add animated marker drop: http://stackoverflow.com/a/38008342
				modelTrigger.triggerMarker = mMap.addMarker(markerOptions); // iOS: [mapView addAnnotation:mvao.annotation];
//				Log.d(AppConfig.LOGTAG_D2, "ADDING Marker ID:" + modelTrigger.triggerMarker.getId()+" TriggerID:"+modelTrigger.trigger_id);
				if (showFirstMarkerTitle) { // just show the starting point.
					modelTrigger.triggerMarker.showInfoWindow();
					showFirstMarkerTitle = false;
				}

				// add the new trigger circle to map that accompanies the marker
				modelTrigger.triggerZoneCircle = mMap.addCircle(new CircleOptions()
						.center(new LatLng(modelTrigger.latitude, modelTrigger.longitude))
						.radius(modelTrigger.distance)
						.fillColor(Color.argb(128,0,255,110))
						.strokeWidth(1)
				);
				markersAndCircles.add(modelTrigger);
				MapViewMarkerCircle mc = new MapViewMarkerCircle(modelTrigger.triggerMarker, modelTrigger.triggerZoneCircle);
				markerCircleByTrigId.put(modelTrigger.trigger_id, mc);

//				// ##### testing
//				double testLng = marker_latlng2.longitude - (.0003 * testcount);
//				LatLng testLatLng = new LatLng(marker_latlng2.latitude, testLng);
//				Trigger trig = new Trigger(testLatLng);
////						final Marker marker1 = googleMap.addMarker(new MarkerOptions()
//				trig.triggerMarker = mMap.addMarker(new MarkerOptions()
//						.icon(BitmapDescriptorFactory.fromResource(R.drawable.appicon_40x40)) // first icon I found. really wanted to use a map oriented one.
//						.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//						.position(testLatLng)
//				);
////						marker1.setTitle("Marker One");
//				trig.triggerMarker.setTitle("Marker " + testcount++);
//				// ##### end testing

			}
		}

		// In iOS, a clicked marker will result in a call to displayHUDWithTrigger via an internal MKMapView call;
		// in Android we explicitly set up the onClickListener for the map markers all at once.
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
//		[mapView setCenterCoordinate:mapView.region.center animated:NO]; // map should already be centered on user location, as set in setUpMap()
		if (firstLoad) {
			if     (mGamePlayAct.mGame.map_focus.contentEquals("PLAYER"))        this.centerMapOnPlayer();
			else if(mGamePlayAct.mGame.map_focus.contentEquals("LOCATION"))      this.centerMapOnLoc(mGamePlayAct.mGame.map_location, mGamePlayAct.mGame.map_zoom_level);
			else if(mGamePlayAct.mGame.map_focus.contentEquals("FIT_LOCATIONS")) this.zoomToFitAnnotations(false);
		}
		firstLoad = false;
	}

	public void centerMapOnPlayer() {
		Location l = AppUtils.getGeoLocation(mGamePlayAct);
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(l.getLatitude(), l.getLongitude())).zoom(17.0f).build();
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//		mMap.moveCamera(cameraUpdate);
		mMap.animateCamera(cameraUpdate);
	}

	public void centerMapOnLoc(Location loc) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude()));
		mMap.animateCamera(cameraUpdate);
	}

	public void centerMapOnLoc(Location loc, double map_zoom_level) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(loc.getLatitude(), loc.getLongitude()))
				.zoom((float)map_zoom_level == 0.0 ? 17.0f : (float)map_zoom_level) // if zoom level is 0 set arbitrarily to a sensible value of roughly 1/4 mile diagonally
				.build();
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//		mMap.moveCamera(cameraUpdate);
		mMap.animateCamera(cameraUpdate);
	}

	private void zoomToFitAnnotations(boolean b) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Trigger mapTrigger : markersAndCircles) {
			Marker marker = mapTrigger.triggerMarker;
			builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();

		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		mMap.animateCamera(cameraUpdate);
	}

	private void setOnclickListenerForMarkers() {
		// In iOS, a clicked marker will result in a call to displayHUDWithTrigger via an internal MKMapView call;
		// in Android we explicitly set up the onClickListener for the map markers all at once.
		// todo: possibly move the logic for determining which Instance to reference based on the marker clicked, to a separate method? Any advantages?
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
						// below is found in displayHUDWithTrigger in iOS
						float distance = trigger.location.distanceTo(mGamePlayAct.mPlayer.location);
						if (mGamePlayAct.mGame.map_offsite_mode != 0
							|| trigger.infinite_distance != 0
							|| (distance <= trigger.distance && mGamePlayAct.mPlayer.location != null))
						{
							// todo: custom icon not getting displayed
							Drawable alertImage;
							if (modelInstance.icon_media_id() == 0)
								alertImage = ContextCompat.getDrawable(mGamePlayAct, R.drawable.plaque_icon_120);
							else {
								Media m = mGamePlayAct.mMediaModel.mediaForId(modelInstance.icon_media_id());
								alertImage = new BitmapDrawable(getResources(), m.data);
							}
							String triggerType = trigger.name;
							new AlertDialog.Builder(mGamePlayAct)
									.setIcon(alertImage)
//									.setIcon(ContextCompat.getDrawable(mGamePlayAct, R.drawable.plaque_icon_120))
//									.setIcon(mGamePlayAct.getResources().getDrawable(R.drawable.plaque_icon_120))
									.setTitle("View?")
//									.setMessage("Are you sure you want to quit Aris?")
									.setPositiveButton("View", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											//kill inner map fragment todo: put this in onDetsroyView/detach
											FragmentManager fm = getChildFragmentManager();
											Fragment innerMapFragment = fm.findFragmentById(R.id.inner_fragment_map);
											if (innerMapFragment != null) {
												fm.beginTransaction().remove(innerMapFragment).commit();
											}

											// enqueueTrigger (found in interactWithLocation in iOS)
											if (trigger != null) mGamePlayAct.mGame.displayQueueModel.enqueueTrigger(trigger);
										}
									})
									.setNegativeButton("Back", null)
									.show();

							return false;
						}
						else {
							float distanceToWalk = distance - trigger.distance;
							Toast t = Toast.makeText(mGamePlayAct, "You are not in range to interact with this. Walk " + String.format("%.1f", distanceToWalk) + "m",
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
