package edu.uoregon.casls.aris_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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

import edu.uoregon.casls.aris_android.Utilities.AppUtils;


public class CreateAccountActivity extends ActionBarActivity {
	private static final String HTTP_CLIENT_CREATE_USER_REQ_API = "v2.users.createUser/";
	private final static String TAG_SERVER_SUCCESS = "success";
	private AutoCompleteTextView mAcTvEmail;
	private EditText mEtUsername;
	private EditText mEtPassword;
	private EditText mEtPasswordAgain;
	private View mProgressView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		mProgressView = findViewById(R.id.create_acct_progress);
		mEtUsername = (EditText) findViewById(R.id.et_aris_id);
		mAcTvEmail = (AutoCompleteTextView) findViewById(R.id.actv_email);
		mEtPassword = (EditText) findViewById(R.id.et_password);
		mEtPasswordAgain = (EditText) findViewById(R.id.et_password_again);

		mEtPasswordAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					attemptRegistration();
					return true;
				}
				return false;
			}
		});


	}




	public void backButtonClick(View v) {
		// kill activity - return to login
		super.onBackPressed();
	}

	public void onClickSignInBtn(View v) {
		attemptRegistration();
	}

	private void attemptRegistration() {
		String username = mEtUsername.getText().toString();
		String email = mAcTvEmail.getText().toString();
		String password = mEtPassword.getText().toString();
		String passwordAgain = mEtPasswordAgain.getText().toString();

		// Reset errors.
		mEtUsername.setError(null);
		mAcTvEmail.setError(null);
		mEtPassword.setError(null);
		mEtPasswordAgain.setError(null);

		boolean cancel = false;
		View focusView = null;

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
		if (!isPasswordValid(password)) {
			mEtPassword.setError(getString(R.string.error_field_too_short));
			focusView = mEtPassword;
			cancel = true;
		}
		if (TextUtils.isEmpty(passwordAgain) /*&& !isPasswordValid(password)*/) {
			mEtPasswordAgain.setError(getString(R.string.error_password_again_required));
			focusView = mEtPasswordAgain;
			cancel = true;
		}

		// Check for a valid email address. -- email disabled
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

		// check password similarity
		if (!password.contentEquals(passwordAgain)) {
			mEtPassword.setError(getString(R.string.error_passwords_must_match));
			focusView = mEtPassword;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt registration and focus the last
			// form field with an error.
			focusView.requestFocus();
		}
		else {
			// perform user registration attempt.
			pollServer(HTTP_CLIENT_CREATE_USER_REQ_API);
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

	private void pollServer(final String request_api) {
		// Create User req example: {"display_name":"","group_name":"","password":"ddd","user_name":"sds","email":""}
		// Response:{"data":{"user_id":null,"user_name":null,"display_name":null,"media_id":null,"read_write_key":null},"returnCode":0,"returnCodeDescription":null}
		showProgress(true);
		RequestParams rqParams = new RequestParams();

		final Context context = this;
		String request_url = AppUtils.SERVER_URL_MOBILE + request_api;

		rqParams.put("request", request_api);
		StringEntity entity;
		entity = null;
		JSONObject jsonMain = new JSONObject();
		try {
			jsonMain.put("user_name", mEtUsername.getText().toString());
			jsonMain.put("email", mAcTvEmail.getText().toString());
			jsonMain.put("password", mEtPassword.getText().toString());
			jsonMain.put("display_name", ""); // Was not avaialble on the iOS registration form, Shouldn't there be one?
			jsonMain.put("group_name", ""); // groupname? ??
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(AppUtils.LOGTAG, "Json string Req to server: " + jsonMain);

		try {
			entity = new StringEntity(jsonMain.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// post data should look like this: {"auth":{"user_id":1,"key":"F7...yzX4"},"game_id":"6"}
		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();

			client.post(context, request_url, entity, "application/json", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
					showProgress(false);
					try {
						processJsonHttpResponse(request_api, TAG_SERVER_SUCCESS, jsonReturn);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppUtils.LOGTAG, "AsyncHttpClient failed server call. ", throwable);
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

	private void processJsonHttpResponse(String callingReq, String returnStatus, JSONObject jsonReturn) throws JSONException {
		Log.d(AppUtils.LOGTAG, "Return status to server Req: " + jsonReturn.toString());
		if (callingReq.contentEquals(HTTP_CLIENT_CREATE_USER_REQ_API) ) { //todo: create user call returns nulls on localhost. Need to fix.
			// Response looks like this: {"data":{"media_id":"0","display_name":"","user_name":"scotta","user_id":"10269","read_write_key":"WQ52...gQN"},"returnCode":0,"returnCodeDescription":null}
			Log.i(AppUtils.LOGTAG, "Landed successfully in colling Req: " + callingReq);
			try {
				// process incoming json data
				if (jsonReturn.has("data")) {
					JSONObject jsonDataBlock = new JSONObject(jsonReturn.getString("data"));
//					if (jsonReturn.has("returnCode") && jsonReturn.getLong("returnCode") == 0) {
					if (jsonDataBlock.getString("user_id") != null) {
						// process return data (log in user)
//						finishWithResult(jsonDataBlock);
						Bundle transitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
								R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();
						Intent i = new Intent(CreateAccountActivity.this, GamesListActivity.class);
//						i.putExtra("user", 		user.toJsonStr()); // future code improvement. use user class to encapsulate data.
						i.putExtra("user_name", mEtUsername.getText().toString());
						i.putExtra("password", mEtPassword.getText().toString());
						i.putExtra("user_id", jsonDataBlock.getString("user_id"));
						i.putExtra("display_name", jsonDataBlock.getString("display_name"));
						i.putExtra("media_id", jsonDataBlock.getString("media_id"));
						i.putExtra("read_write_key", jsonDataBlock.getString("read_write_key"));
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i, transitionAnimationBndl);
						finish();

					}
					else {
						Log.i(AppUtils.LOGTAG, "Server sent error in return code: " + jsonDataBlock.getString("returnCodeDescription"));
						Toast t = Toast.makeText(getApplicationContext(), "There was a problem creating your account. Please try again later.",
								Toast.LENGTH_SHORT);
						t.setGravity(Gravity.CENTER, 0, 0);
						t.show();
					}

				}
			} catch (JSONException e) {
				Log.e(AppUtils.LOGTAG, "Failed while parsing returning JSON from request:" + HTTP_CLIENT_CREATE_USER_REQ_API + " Error reported was: " + e.getCause());
				e.printStackTrace();
			}
		}
		else { // unknown callinRequest
			Log.e(AppUtils.LOGTAG, "AsyncHttpClient returned successfully but with unhandled server callingReq: " + callingReq);
			Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Please try again, later.",
					Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();

		}
	}

//	private void finishWithResult(JSONObject jsonUserData) {
//		Bundle conData = new Bundle();
//		conData.putBoolean("do_login", true);
//		conData.putString("json_user", jsonUserData.toString());
//		conData.putString("password", mEtPassword.getText().toString());
//		Intent intent = new Intent();
//		intent.putExtras(conData);
//		setResult(RESULT_OK, intent);
//		finish();
//	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
		}
	}

	@Override
	public void finish() {
		super.finish();
		// tell transitioning activities how to slide. eg: overridePendingTransition(howThisMovesOut, howNewMovesIn) -sem
		overridePendingTransition(R.animator.slide_out_to_right, R.animator.slide_in_from_left);
	}
}