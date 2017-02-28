package edu.uoregon.casls.analogu;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

//import org.apache.http.Header;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
//import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static edu.uoregon.casls.analogu.AppConfig.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {

	private LoginButton mFacebookSignInButton;
	private SignInButton mGoogleSignInButton;

	public Bundle mTransitionAnimationBndl;

	private       GoogleApiClient   mGoogleApiClient;
	private       CallbackManager   mFacebookCallbackManager;
	private       AccessToken       mAccessToken;
	private static SharedPreferences appPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		mFacebookCallbackManager = CallbackManager.Factory.create();
		appPrefs = getSharedPreferences(AppConfig.APP_PREFS_FILE_NAME, MODE_PRIVATE);

		setContentView(R.layout.activity_login);

		// todo: check for prior saved login state for FB or G and disallow letting user log in with both at the same time.

		mFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);
		mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(final LoginResult loginResult) {
						//TODO: Use the Profile class to get information about the current user.
						mAccessToken = loginResult.getAccessToken();
						if (mAccessToken.getToken() != null)
							getFBUserGraphData();
						handleSignInResult(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								LoginManager.getInstance().logOut();
								return null;
							}
						});
					}

					@Override
					public void onCancel() {
						handleSignInResult(null);
					}

					@Override
					public void onError(FacebookException error) {
						Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
						handleSignInResult(null);
					}
				}
		);

		mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
		mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signInWithGoogle();
			}
		});

		mTransitionAnimationBndl = ActivityOptions.makeCustomAnimation(getApplicationContext(),
				R.animator.slide_in_from_right, R.animator.slide_out_to_left).toBundle();
	}

	private void getFBUserGraphData() {
		GraphRequest request = GraphRequest.newMeRequest(
				mAccessToken,
				new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(
							JSONObject jsonResponseFields,
							GraphResponse response) {
						storeUserDataInAppPrefs(jsonResponseFields);
						pollServer(AppConfig.REQ_API_LOGIN, "Facebook", jsonResponseFields);
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,interested_in,gender,birthday,email,age_range,name,picture.width(480).height(480)");
		request.setParameters(parameters);
		request.executeAsync();


	}

	private void handleSignInResult(Callable<Void> callable) {
		if (callable != null) {
			// passed the third party signin, now validate with DB.

			// go to dashboard page
			Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
			i.putExtra("user_creds", "to be completed");
			startActivityForResult(i, AppConfig.RC_ACTIVITY_TREE,mTransitionAnimationBndl);
		}
		else {
			// login failed. Do I need to tell them or will FB & Google API tell them?
		}
	}

	private void signInWithGoogle() {
		if(mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	private void storeUserDataInAppPrefs(JSONObject jsonResponseFields) {
		// assumes the following fields are available: id,interested_in,gender,birthday,email,age_range,name,picture.width(480).height(480)
		SharedPreferences.Editor prefsEd = appPrefs.edit();
		try {
			prefsEd.putString("id", jsonResponseFields.getString("id"));
			prefsEd.putString("interested_in", jsonResponseFields.getString("interested_in"));
			prefsEd.putString("gender", jsonResponseFields.getString("gender"));
			prefsEd.putString("birthday", jsonResponseFields.getString("birthday"));
			prefsEd.putString("email", jsonResponseFields.getString("email"));
			prefsEd.putString("age_range", jsonResponseFields.getString("age_range"));
			prefsEd.putString("name", jsonResponseFields.getString("name"));
			if (jsonResponseFields.getString("gender").equals("fake"))
				prefsEd.putString("accessToken",  "FACCE123123");
			else
				prefsEd.putString("accessToken",  mAccessToken.getToken());
		} catch (JSONException e) {
			// failed to retrieve one or more of the fields above
			e.printStackTrace();
		}
		prefsEd.commit();
	}

	private void pollServer(String requestAPI, String source, JSONObject jsonResponseFields) {
		String reqUrl = AppConfig.BASE_SERVER_URL + requestAPI;
		//			case .Login(let email, let username, let password, let accessToken, let userId, let source):
		JSONObject jsonReqParams = new JSONObject();
		JSONObject jsonAuth = new JSONObject();

		try {
			jsonReqParams.put("username", jsonResponseFields.getString("email"));
			jsonReqParams.put("password", "");
			jsonReqParams.put("userId", jsonResponseFields.getString("id"));
			jsonReqParams.put("email", jsonResponseFields.getString("email"));
			jsonReqParams.put("source", source);
			if (jsonResponseFields.getString("gender").equals("fake"))
				jsonReqParams.put("accessToken",  "EAAHZAedTRMbUBACcroRH5FHxAcbL1YL8ZAcPEc1Im4SqnmCXCeDhXVmEod245pfvOhRZBY2yh469iwDEFaInZAKxUowovU0Po9ReiQT6lnhiGLKczl4C9v3Ow9eKNKJBSg26ZChU9oFhlg2FhpqIXdCLXn9w1y8WZAG6Fr8QaOkzSO5u0GNt1GdHvZCLrLFSNBxMPCq22PO4AZDZD");
			else
				jsonReqParams.put("accessToken",  mAccessToken.getToken());
			// auth block
			jsonAuth.put("username", jsonResponseFields.getString("email"));
			jsonAuth.put("password", "");
			jsonReqParams.put("auth", jsonAuth);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		StringEntity entity = null;
		try {
			entity = new StringEntity(jsonReqParams.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


		if (AppUtils.isNetworkAvailable(getApplicationContext())) {
			AsyncHttpClient client = new AsyncHttpClient();
//			AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
			client.setMaxRetriesAndTimeout(2, 4000); // looks like timeout is =always= 10 seconds regardless of this setting.
			client.setTimeout(4000); // milliseconds
			client.post(this, reqUrl, entity, "application/json", new JsonHttpResponseHandler() {
//			client.post(this, reqUrl, entity, "application/x-www-form-urlencoded; charset=utf-8", new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject jsonReturn) {
//					showProgress(false);
					processJsonHttpResponse(AppConfig.REQ_API_LOGIN, AppConfig.TAG_SERVER_SUCCESS, jsonReturn);

				}
				@Override // this one will fire if there is a wifi connection but there's no internet feed.
				public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject responseJson) {
					Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
//					showProgress(false);
					Toast t = Toast.makeText(getApplicationContext(), "There was a problem receiving data from the server. Make sure you are connected to the internet.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					super.onFailure(statusCode, headers, throwable, responseJson);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "AsyncHttpClient failed server call. ", throwable);
//					showProgress(false);
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
		if (callingReq.equals(AppConfig.REQ_API_LOGIN)) {
			try {
				// check for login denial response from server
				if (jsonReturn.has(AppConfig.SVR_RETURN_CODE) && jsonReturn.getInt(AppConfig.SVR_RETURN_CODE) > 0) {
					Toast t = Toast.makeText(getApplicationContext(), "This username and/or password were not recognized by the server. Please try again.",
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				else { // passed login
					// process incoming json data
					if (jsonReturn.has("data")) {
						JSONObject jsonObj = jsonReturn.getJSONObject("data");

					}
				}
			}
			catch(JSONException e) {
				Log.e(AppConfig.LOGTAG, getClass().getSimpleName() + "Failed while parsing returning JSON from request:" + AppConfig.REQ_API_LOGIN + " Error reported was: " + e.getCause());
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

			if(result.isSuccess()) {
				final GoogleApiClient client = mGoogleApiClient;
				GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

				handleSignInResult(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						if (client != null) {

							Auth.GoogleSignInApi.signOut(client).setResultCallback(
									new ResultCallback<Status>() {
										@Override
										public void onResult(Status status) {
											Log.d(LoginActivity.class.getCanonicalName(),
													status.getStatusMessage());

                                        /* TODO: handle logout failures */
										}
									}
							);

						}

						return null;
					}
				});

			}
			else {
				handleSignInResult(null);
			}

		} else if (requestCode == AppConfig.RC_ACTIVITY_TREE) {
			if (resultCode == AppConfig.LOGOUT_REQUESTED) {
				LoginManager.getInstance().logOut();
			}
		}
		else {
			mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
		}

	}

	// easy login for development testing only
	// todo: REMOVE THIS FUNCTIONALITY BEFORE RELEASE!
	public void onDevCycleEasyLoginRemoveForReleaseClick(View v) {
		if (v.isClickable()) {
			fakeFBAuthorization();

//			handleSignInResult(new Callable<Void>() {
//				@Override
//				public Void call() throws Exception {
//					return null;
//				}
//			});
		}
	}

	private void fakeFBAuthorization() {
		JSONObject fakeJson = new JSONObject();
		try {
			fakeJson.put("id", "141808656265805");
			fakeJson.put("interested_in", "faking it");
			fakeJson.put("gender", "fake");
			fakeJson.put("birthday", "2016-10-11");
			fakeJson.put("email", "caslsmobile@gmail.com");
			fakeJson.put("age_range", "all");
			fakeJson.put("name", "caslsmobile");
			fakeJson.put("accessToken", "EAAHZAedTRMbUBACcroRH5FHxAcbL1YL8ZAcPEc1Im4SqnmCXCeDhXVmEod245pfvOhRZBY2yh469iwDEFaInZAKxUowovU0Po9ReiQT6lnhiGLKczl4C9v3Ow9eKNKJBSg26ZChU9oFhlg2FhpqIXdCLXn9w1y8WZAG6Fr8QaOkzSO5u0GNt1GdHvZCLrLFSNBxMPCq22PO4AZDZD");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		pollServer(AppConfig.REQ_API_LOGIN, "Facebook", fakeJson);

	}

}
