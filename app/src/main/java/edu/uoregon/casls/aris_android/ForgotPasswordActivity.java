package edu.uoregon.casls.aris_android;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;
import edu.uoregon.casls.aris_android.Utilities.Calls;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class ForgotPasswordActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

	private AutoCompleteTextView mAcTvEmail;
	private View mProgressView;
	private View mResetPasswordFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);

		mAcTvEmail = (AutoCompleteTextView) findViewById(R.id.actv_email_address);
		populateAutoComplete();

		mAcTvEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					attemptToSubmitForm();
					return true;
				}
				return false;
			}
		});

		mResetPasswordFormView = findViewById(R.id.scrollvw_for_pw_reset_form);
		mProgressView = findViewById(R.id.network_req_progress);
	}

	private void populateAutoComplete() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PERMISSION_GRANTED) {
			getLoaderManager().initLoader(0, null, this);
		}
	}


	private void pollServer() {
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = AppConfig.SERVER_URL_MOBILE + Calls.HTTP_USER_REQ_FORGOT_PASSWD;

		rqParams.put("request", Calls.HTTP_USER_REQ_FORGOT_PASSWD);

		JSONObject jsonParams;
		jsonParams = new JSONObject();
		StringEntity entity;
		entity = null;

		try {
			jsonParams.put("email", mAcTvEmail.getText().toString());
			jsonParams.put("permission", "read_write");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + "Json string Req to server: " + jsonParams);

		try {
			entity = new StringEntity(jsonParams.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();

			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
					showProgress(false);
					processJsonHttpResponse(Calls.HTTP_USER_REQ_FORGOT_PASSWD, AppConfig.TAG_SERVER_SUCCESS, jsonReturn);

				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
							Toast.LENGTH_SHORT);
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
		Log.d(AppConfig.LOGTAG, getClass().getSimpleName() + " Return status to server Req: " + jsonReturn.toString());
		if (callingReq.equals(Calls.HTTP_USER_REQ_FORGOT_PASSWD)) {
			try {
				// check for login denial response from server
				if (jsonReturn.has(AppConfig.SVR_RETURN_CODE) && jsonReturn.getInt(AppConfig.SVR_RETURN_CODE) > 0) {
					// note: this is just here in case someday the server side validates the email address. Right now it doesn't.
					// This is probably a good thing since it's a potential data exploit if it did.
					Toast t = Toast.makeText(getApplicationContext(), "This email address was not recognized by the server. Please try again.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				else { // Call was successful. Reset email should have been sent. Return to login screen.
					finish();
				}
			} catch (JSONException e) {
				Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + Calls.HTTP_USER_REQ_FORGOT_PASSWD + " Error reported was: " + e.getCause());
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
				new ArrayAdapter<String>(ForgotPasswordActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mAcTvEmail.setAdapter(adapter);
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

			mResetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mResetPasswordFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mResetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
			mResetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Checks the Email field for a valid entry.
	 * If there are form errors, the
	 * errors are presented and no actual server call is made.
	 */
	public void attemptToSubmitForm() {

		// Reset errors.
		mAcTvEmail.setError(null);

		// Store values at the time of the login attempt.
		String email = mAcTvEmail.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mAcTvEmail.setError(getString(R.string.error_field_required));
			focusView = mAcTvEmail;
			cancel = true;
		}
		else if (!isEmailValid(email)) {
			mAcTvEmail.setError(getString(R.string.error_invalid_email));
			focusView = mAcTvEmail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			pollServer();
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

	public void onBackButtonClick(View v) {
		// kill activity - return to login
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_right, R.animator.slide_in_from_left);
	}

}
