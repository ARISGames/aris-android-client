package edu.uoregon.casls.aris_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

	private static final String HTTP_CLIENT_LOGIN_REQ_API = "v2.users.logIn";
	private final static String TAG_SERVER_ERROR = "server_error";
	private final static String TAG_ERROR = "error";

	public android.support.v7.app.ActionBar tabBar;
	public Bundle mTransitionAnimationBndl;
	private static SharedPreferences appPrefs;

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[]{
			"foo@example.com:hello", "bar@example.com:world"
	};
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
//	private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mAcTvEmail;
	private EditText mEtUsername;
	private EditText mEtPassword;
	private View mLlBottomPageLinksView;
	private View mProgressView;
	private View mLoginFormView;
	private int mLoginId = 0;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);


		// SEM

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setLogo(R.drawable.logo_text_nav);
//		getSupportActionBar().setCustomView(); // todo: create view that properly centers ARIS logo.
//		overridePendingTransition(R.animator.slide_in_from_right, R.animator.slide_out_to_right);

		// tell transitioning activities how to slide. eg: makeCustomAnimation(ctx, howNewMovesIn, howThisMovesOut) -sem
		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
			R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();


		// Set up the login form.
		// Not currently using email as login. this is for future capability expansion.
//		mAcTvEmail = (AutoCompleteTextView) findViewById(R.id.email);
//		populateAutoComplete();

		mEtUsername = (EditText) findViewById(R.id.et_username);

		mEtPassword = (EditText) findViewById(R.id.password);
		mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		ImageButton mEmailSignInButton = (ImageButton) findViewById(R.id.imgbtn_sign_in);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLlBottomPageLinksView = findViewById(R.id.ll_bottom_margin_links);
		mLoginFormView = findViewById(R.id.scrollvw_for_login_form);
		mProgressView = findViewById(R.id.login_progress);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

//	@Override // attempt to hide the qr code background when the keyboard is up.
//	public boolean onTouchEvent(MotionEvent event) {
//		// Hide qr thingie when entering text fields.
//		ImageView qrImage  = (ImageView) findViewById(R.id.imageView);
//		InputMethodManager imm = (InputMethodManager)
//				this.getSystemService(Context.INPUT_METHOD_SERVICE);
//
//		if (imm.isAcceptingText()) {
//			qrImage.setVisibility(View.INVISIBLE);
//		} else {
//			qrImage.setVisibility(View.VISIBLE);
//		}
//
//		return true;
//	}


	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
//		if (mAuthTask != null) {
//			return;
//		}

		// Reset errors.
		mEtUsername.setError(null);
//		mAcTvEmail.setError(null);
		mEtPassword.setError(null);

		// Store values at the time of the login attempt.
//		String email = mAcTvEmail.getText().toString();
		String username = mEtUsername.getText().toString();
		String password = mEtPassword.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid username.
		if (TextUtils.isEmpty(username)) {
			mEtUsername.setError(getString(R.string.error_username_required));
			focusView = mEtUsername;
			cancel = true;
		}
		else if (!isUsernameValid(username)) {
			mEtUsername.setError(getString(R.string.error_field_too_short));
			focusView = mEtUsername;
			cancel = true;
		}

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password) /*&& !isPasswordValid(password)*/) {
			mEtPassword.setError(getString(R.string.error_password_required));
			focusView = mEtPassword;
			cancel = true;
		}

		// Check for a valid email address. -- email disabled
//		if (TextUtils.isEmpty(email)) {
//			mAcTvEmail.setError(getString(R.string.error_field_required));
//			focusView = mAcTvEmail;
//			cancel = true;
//		}
//		else if (!isEmailValid(email)) {
//			mAcTvEmail.setError(getString(R.string.error_invalid_email));
//			focusView = mAcTvEmail;
//			cancel = true;
//		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
//			mAuthTask = new UserLoginTask(username, password);
//			mAuthTask.execute((Void) null);
			pollServer();
		}
	}

	private void pollServer() {
		RequestParams rqParams = new RequestParams();

		final Context context = this;

//		List<NameValuePair> params = new ArrayList<NameValuePair>();
		rqParams.put("request", HTTP_CLIENT_LOGIN_REQ_API);
//		rqParams.put("reqCode", Integer.toString(HTTP_CLIENT_LOGIN_REQ_CODE));
		rqParams.put("username", mEtUsername.getText().toString());
		rqParams.put("password", mEtPassword.getText().toString());
		rqParams.put("permission", "read_write");
		rqParams.put("device_id", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)); // android call to get a device id. gets reset on device wipe.

		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();
			RequestHandle rqHandle = client.post(AppUtils.SERVER_URL_MOBILE, rqParams, new TextHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//					if (progressDialog != null) {
//						progressDialog.dismiss();
//					}
					showProgress(false);
					processHttpResponse(HTTP_CLIENT_LOGIN_REQ_API, TAG_SERVER_ERROR, "{\"error\":\"error\"}");
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					processHttpResponse(HTTP_CLIENT_LOGIN_REQ_API, "success", responseString);
				}

				@Override
				public void onStart() {
//					progressDialog = new ProgressDialogShower(context, "Checking with Server...", "Please wait.", false, true);
					showProgress(true);
					super.onStart();
				}

				@Override
				public void onFinish() {
//					if (progressDialog != null) {
//						progressDialog.dismiss();
//					}
					showProgress(false);
					super.onFinish();
				}
			});
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(), "You are not connected to the internet currently. Please try again later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}
	}

	protected void processHttpResponse(String callingReq, String returnStatus, String jsonResult) {

		if (returnStatus.contentEquals(TAG_SERVER_ERROR)) {
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}
		else if (callingReq == HTTP_CLIENT_LOGIN_REQ_API) {
			// get result
			//decode json and pull out result success, which in this case is a user ID greater than 0.
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(jsonResult);
				if (jsonObject.has(TAG_SERVER_ERROR)) {
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				else if (jsonObject.has(TAG_ERROR)) {
					mEtPassword.setText("");
					Toast t = Toast.makeText(getApplicationContext(), "We were unable to log you in to LFO To Go. Reason: " + jsonObject.getString("error"),
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();

				}
				else if (jsonObject.has("loginid")) {
					mLoginId = jsonObject.getInt("loginid");
					if (mLoginId > 0) { // Store mLoginId and PW and send them to the home screen/activity
						SharedPreferences.Editor spEditor = appPrefs.edit();
						spEditor.putInt("loginid", mLoginId);
						spEditor.putString("username", mEtUsername.getText().toString());
						spEditor.putString("password", mEtPassword.getText().toString());
						spEditor.commit();

						// go to home screen. (aka: passport)
//						Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
//						// remove this login activity from the stack when proceeding to home activity (so they don't use back button)
//						homeIntent.putExtra("username", edTxtUsername.getText().toString());
//						homeIntent.putExtra("loginid", mLoginId);
//						homeIntent.putExtra("password", edTxtPassword.getText().toString());
//						homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						startActivity(homeIntent);
						Toast t = Toast.makeText(getApplicationContext(), "Successful Login Temp Message.",
								Toast.LENGTH_SHORT);


					}
				}
				else { // Something unexpected happened. Bad server URL??
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again later.",
							Toast.LENGTH_SHORT);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else {
			// resultCode was NOT OK
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem logging in to the server. Make sure you are connected to the internet.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}
	}

	private boolean isEmailValid(String email) {
		if (email == null) {
			return false;
		}
		else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}

	private boolean isUsernameValid(String username) {
		//TODO: Replace with more specific logic
		return username.length() > 3;
	}

	private boolean isPasswordValid(String password) {
		//TODO: Replace with more specific logic
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
			mLlBottomPageLinksView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLlBottomPageLinksView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLlBottomPageLinksView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		}
		else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLlBottomPageLinksView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}


	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mAcTvEmail.setAdapter(adapter);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createAccountClicked(View v) {
		// start CreateAccountActivity

		Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
		startActivity(i, mTransitionAnimationBndl);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void forgotPassClicked(View v) {
		// start ForgotPasswordActivity
		Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
		startActivity(i, mTransitionAnimationBndl);
	}

	public void loginButtonClick(View v) {
		// user has clicked the login button
		attemptLogin();
	}

//	/**
//	 * Represents an asynchronous login/registration task used to authenticate
//	 * the user.
//	 */
//	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//		private final String mUsername;
//		private final String mPassword;
//
//		UserLoginTask(String username, String password) {
//			mUsername = username;
//			mPassword = password;
//		}
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			// TODO: attempt authentication against a network service.
//
//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				return false;
//			}
//
//			for (String credential : DUMMY_CREDENTIALS) {
//				String[] pieces = credential.split(":");
//				if (pieces[0].equals(mUsername)) {
//					// Account exists, return true if the password matches.
//					return pieces[1].equals(mPassword);
//				}
//			}
//
//			// TODO: register the new account here.
//			return true;
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//			mAuthTask = null;
//			showProgress(false);
//
//			if (success) {
//				// todo: success should take them into the app flow.
//				Toast.makeText(LoginActivity.this, "Pretend login successful", Toast.LENGTH_SHORT).show();
//
////				finish();
//			}
//			else {
//				LoginActivity.this.mEtPassword.setError(getString(R.string.error_incorrect_password));
//				LoginActivity.this.mEtPassword.requestFocus();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
//			showProgress(false);
//		}
//	}
}

