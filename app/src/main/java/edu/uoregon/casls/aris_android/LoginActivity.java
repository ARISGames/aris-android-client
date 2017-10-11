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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.Calls;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

	private final static String TAG_SERVER_ERROR = "server_error";
	private final static String TAG_ERROR = "error";
	private static final int CREATE_ACCOUNT_REQ_CODE = 222;

	public android.support.v7.app.ActionBar tabBar;
	public Bundle mTransitionAnimationBndl;
	private static SharedPreferences appPrefs;

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

	// todo:
	// todo:  Need to check for avatar pic, and redirect to camera if no pic exists and set Public Name field
	// todo:

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

		appPrefs = getSharedPreferences("ARIS_LOGIN", 0);
		String user_id = appPrefs.getString("user_id", null);
		String read_write_key = appPrefs.getString("read_write_key", null);
		if (user_id != null && read_write_key != null) {
			pollServer(null, null, user_id, read_write_key);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (AppConfig.DEBUG_ON) { // preset the input fields to save time during testing.
			mEtUsername.setText("scotta"); // test account on arisgames.org
//			mEtUsername.setText("scott"); // localhost
			mEtPassword.setText("123123");
		}
		else {
			mEtUsername.setText("");
			mEtPassword.setText("");
		}

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
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
			pollServer(username, password, null, null);
		}

	}

	/* How to pos json data list
	        JSONObject jsonParams = new JSONObject();
        jsonParams.put("notes", "Test api support");
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
	 */

	private void pollServer(String user_name, String password, String user_id, String read_write_key) {
		RequestParams rqParams = new RequestParams();
//		JSONObject jsonParams = new JSONObject();

		final Context context = this;
		String request_url = AppConfig.SERVER_URL_MOBILE + Calls.HTTP_USER_LOGIN_REQ_API;

		rqParams.put("request", Calls.HTTP_USER_LOGIN_REQ_API);

		JSONObject jsonParams;
		jsonParams = new JSONObject();
		StringEntity entity;
		entity = null;

		try {
			if (user_name != null && password != null) {
				jsonParams.put("user_name", user_name);
				jsonParams.put("password", password);
				jsonParams.put("permission", "read_write");
			} else if (user_id != null && read_write_key != null) {
				JSONObject authObj = new JSONObject();
				authObj.put("user_id", user_id);
				authObj.put("key", read_write_key);
				authObj.put("permission", "read_write");
				jsonParams.put("auth", authObj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "Json string Req to server: " + jsonParams);

		try {
			entity = new StringEntity(jsonParams.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/*
		client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
		 */
		// post data should look like this: {"password":"123123","permission":"read_write","user_name":"scott"}
		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();
//			AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
			client.setMaxRetriesAndTimeout(2, 4000); // looks like timeout is =always= 10 seconds regardless of this setting.
			client.setTimeout(4000); // milliseconds
			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
					showProgress(false);
					processJsonHttpResponse(Calls.HTTP_USER_LOGIN_REQ_API, AppConfig.TAG_SERVER_SUCCESS, jsonReturn);

				}
				@Override // this one will fire if there is a wifi connection but there's no internet feed.
				public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject responseJson) {
					Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Make sure you are connected to the internet.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, throwable, responseJson);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, responseString, throwable);
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

	private void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) {
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "Return status to server Req: " + jsonReturn.toString());
 		if (callingReq.equals(Calls.HTTP_USER_LOGIN_REQ_API)) {
			try {
				// check for login denial response from server
				if (jsonReturn.has("returnCode") && jsonReturn.getInt(AppConfig.SVR_RETURN_CODE) > 0) {
					Toast t = Toast.makeText(getApplicationContext(), "This username and/or password were not recognized by the server. Please try again.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				else { // passed login
					// process incoming json data
					if (jsonReturn.has("data")) {
//					int returnCode = (jsonReturn.has("returnCode")) ? jsonReturn.getInt("returnCode") : null; // what do I do?
//					String returnCodeDescription = (jsonReturn.has("returnCode")) ? jsonReturn.getString("returnCodeDescription") : ""; // For what?
						JSONObject jsonObj = jsonReturn.getJSONObject("data");
						String mUserId = jsonReturn.has("returnCode") ? jsonObj.getString("user_id") : "null";
						if (mUserId != null && !mUserId.contentEquals("null")) { // login creds accepted
						    String mReadWriteKey = jsonObj.getString("read_write_key");
							SharedPreferences.Editor editor = appPrefs.edit();
							editor.putString("user_id", mUserId);
							editor.putString("read_write_key", mReadWriteKey);
							editor.commit();
							/*
							appPrefs = getPreferences(0);
							String user_name = appPrefs.getString("user_name", null);
							String read_write_key = appPrefs.getString("read_write_key", null);
							if (user_name != null && read_write_key != null) {
								pollServer(user_name, null, read_write_key);
							}
							 */
							// log in the user
							Intent i = new Intent(LoginActivity.this, GamesListActivity.class);
							i.putExtra("user_name", jsonObj.getString("user_name"));
							i.putExtra("user_id", mUserId);
							i.putExtra("display_name", jsonObj.getString("display_name"));
							i.putExtra("media_id", jsonObj.getString("media_id"));
							i.putExtra("read_write_key", mReadWriteKey);
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i, mTransitionAnimationBndl);
							finish();
						}
						else { // login creds denied
							Toast t = Toast.makeText(getApplicationContext(), "This username and/or password were not recognized by the server. Please try again.",
									Toast.LENGTH_SHORT);
							t.setGravity(Gravity.CENTER, 0, 0);
							t.show();
						}
					}
				}
			}
			catch(JSONException e) {
				Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + Calls.HTTP_USER_LOGIN_REQ_API + " Error reported was: " + e.getCause());
				e.printStackTrace();
			}
		}
		else { // unknown callinRequest
			Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient returned unknown server callingReq: " + callingReq);
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
	}


	private boolean isEmailValid(String email) {
		return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private boolean isUsernameValid(String username) {
		//TODO: Replace with more specific logic
		return username.length() > 3;
	}

	private boolean isPasswordValid(String password) {
		//TODO: Replace with more specific logic
		return password.length() > 4;
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch(requestCode) {
//			case CREATE_ACCOUNT_REQ_CODE:
//				if (resultCode == RESULT_OK) {
//					Bundle res = data.getExtras();
//					Boolean doLogin = res.getBoolean("do_login");
//					if (doLogin) {
//						try {
//							JSONObject jsonUser = new JSONObject(res.getString("json_user"));
//							mUserId = jsonUser.getString("user_id");
//							mEtUsername.setText(jsonUser.getString("user_name"));
//							mEtPassword.setText(res.getString("password"));
//							pollServer();
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//				break;
//		}
//	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
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
//		startActivityForResult(i, CREATE_ACCOUNT_REQ_CODE, mTransitionAnimationBndl);
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
}

