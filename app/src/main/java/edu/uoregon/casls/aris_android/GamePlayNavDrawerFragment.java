package edu.uoregon.casls.aris_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;


public class GamePlayNavDrawerFragment extends Fragment {

	private static final java.lang.String STATE_SELECTED_ITEM_NAME = "selected_item_name";
	public String[] mDrawerListItems;
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	public DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	public View mFragmentContainerView;
	public ActionBar mActionBar;

	GamePlayActivity mGamePlayActivity;

	private int mCurrentSelectedPosition = 0;
	private String mCurrentSelectedItemName;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

	public GamePlayNavDrawerFragment() {
	}

	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayActivity = gamePlayActivity;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mCurrentSelectedItemName = savedInstanceState.getString(STATE_SELECTED_ITEM_NAME);
			mFromSavedInstanceState = true;
		}

		// disable this default. We want the game to pick the first fragment.
		// Select either the default item (0) or the last selected item. -sem
//		selectItem(mCurrentSelectedPosition, mCurrentSelectedItemName);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_nav_drawer_view, container, false);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.tv_drawer_item_name);
				String itemName = tv.getText().toString();
				selectItem(position, itemName);
			}
		});


		// add nav list items
		// Todo: these will come in from the server in a custom order and with custom icons and names.
		// todo: Will need to check server resp data for this list and override these defaults if it exists.
		// The specific tabs that will be available in a given game will be listed in TabsModel.playerTabs
		//  and will have a sort index. Use this list to populate the drawer options.
//		mDrawerListItems = getResources().getStringArray(R.array.game_drawer_list_items); // temp. get fixed array from strings.xml
//		String iconURL;
//		mGamePlayAct.mGame.tabsModel.playerTabNames().toArray(mDrawerListItems);
//		for (int i=0; i < mDrawerListItems.length; i++) {
//			iconURL = "http://dummy.fillinlater.com/media.png";
//			mNavItems.add(new NavItem(mDrawerListItems[i], "Nosubtitle", AppConfig.gameDrawerItemIconByName.get(mDrawerListItems[i]), iconURL));
//
//		}
		// todo: this default action bar is white on black, the opposite of the previous pages with the custom actionbar. Find a way to match the custom style.
		// note about built in action bar and drawer:
		// the login and game picker activities use custom toolbars (xml based) and need to have a "NoActionBar" theme to work.
		// To use the native actionbar stuff as we do here, you need to set the activity's theme to an action bar friendly theme
		// E.g.: android:theme="Theme.AppCompat.Light.DarkActionBar"
//		DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
//		mDrawerListView.setAdapter(adapter);
//
//		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);


		return mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.hide();

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),                    /* host Activity */
				mDrawerLayout,                    /* DrawerLayout object */
//				R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret (param for .v4 only; hide for .v7 sem) */
				R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

//				getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}
//				getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
				drawerView.bringToFront();
//				mDrawerLayout.requestLayout();
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position, String itemName) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(itemName);
//			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
//		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.action_example) {
			Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	public ActionBar getActionBar() {
//		return ((ActionBarActivity) getActivity()).getSupportActionBar();
		return ((AppCompatActivity) getActivity()).getSupportActionBar();
	}

	public void refreshFromModel() {
		this.addItems(mGamePlayActivity.mGame.tabsModel.playerTabTypes());
	}

	public void addItems(List<String> playerTabNames) {
		String iconURL = "http://dummy.fillinlater.com/media.png";
		for (String tabName : playerTabNames) {
			if (AppConfig.gameDrawerItemIconByName.get(tabName) != null)
				mNavItems.add(new NavItem(AppUtils.prettyName(tabName), "Nosubtitle", AppConfig.gameDrawerItemIconByName.get(tabName), iconURL));
		}

		// add the leave game option
		if (mGamePlayActivity.leave_game_enabled) {
			mNavItems.add(new NavItem("Leave Game", "Exit", AppConfig.gameDrawerItemIconByName.get("EXIT"), iconURL));
		}

		DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), mNavItems);
		mDrawerListView.setAdapter(adapter);

		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
//		void onNavigationDrawerItemSelected(int position);
		void onNavigationDrawerItemSelected(String itemName);
	}

	/**
	 * classes for custom list item elements that have icons
	 */
	class NavItem {
		String mTitle;
		String mSubtitle;
		int mIconResId;
		String mIconURL;

		public NavItem(String title, String subtitle, int iconResId, String iconURL) {
			mTitle = title;
			mSubtitle = subtitle;
			mIconResId = iconResId;
			mIconURL = iconURL;
		}
	}

	class DrawerListAdapter extends BaseAdapter {

		Context mContext;
		ArrayList<NavItem> mNavItems;

		public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
			mContext = context;
			mNavItems = navItems;
		}

		@Override
		public int getCount() {
			return mNavItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mNavItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.game_nav_drawer_item, null);
			}
			else {
				view = convertView;
			}

			TextView titleView = (TextView) view.findViewById(R.id.tv_drawer_item_name);
			titleView.setText( mNavItems.get(position).mTitle );
//			TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
//			ImageView iconView = (ImageView) view.findViewById(R.id.icon); // orig imagevw

//			subtitleView.setText( mNavItems.get(position).mSubtitle );
//			iconView.setImageResource(mNavItems.get(position).mIconResId);

			// set webview to display remote icon
//			WebView wvGameIcon = new WebView(mContext);
//			wvGameIcon = (WebView) view.findViewById(R.id.wv_nav_item_icon); // these two lines - same as below.
			WebView wvGameIcon = (WebView) view.findViewById(R.id.wv_nav_item_icon);
			wvGameIcon.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // get rid of HWUI Protection error?
//			wvGameIcon.setWebViewClient(new WebViewClient()); // no dif
			if (/*no custom icon from svr*/mNavItems.get(position).mIconResId != 0) { // 0 = custom icon (reverse from game item icons, btw)
				wvGameIcon.setBackgroundColor(0x00000000);
				wvGameIcon.setBackgroundResource(mNavItems.get(position).mIconResId); // set to a default icon
			}
			else { //  show custom icon.
				wvGameIcon.getSettings().setJavaScriptEnabled(true);
				wvGameIcon.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
				wvGameIcon.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.
				wvGameIcon.getSettings().setUseWideViewPort(true);
				// todo: send in a URL to this class also to use instead of resource id.
				wvGameIcon.loadUrl(mNavItems.get(position).mIconURL);
			}

			return view;
		}
	}
}
