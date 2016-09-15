package edu.uoregon.casls.aris_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;
import edu.uoregon.casls.aris_android.Utilities.AppUtils;

/**
 * Created by smorison on 9/15/16.
 */

public class ARISWebView extends WebView {

	private transient GamePlayActivity mGamePlayAct;

	public ARISWebView(Context context) {
		super(context);
	}

	public ARISWebView (Context context, AttributeSet attrs) {
		super (context, attrs);
	}

	public ARISWebView (Context context, AttributeSet attrs, int defStyleAttr) {
		super (context, attrs, defStyleAttr);
	}

	public void initWithContext (Activity act) {
		mGamePlayAct = (GamePlayActivity) act;
		// standard settings for most ARISWebViews. Override for specific cases.
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		this.getSettings().setLoadWithOverviewMode(true); // causes the content (image) to fit into webview's window size.

		this.setWebViewClient(new WebViewClient() {
			private int       webViewPreviousState;
			private final int PAGE_STARTED    = 0x1;
			private final int PAGE_REDIRECTED = 0x2;
			private boolean jsEvaluated = false;

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webViewPreviousState = PAGE_REDIRECTED;
				if (url.startsWith("aris:")) { // aka isARISRequest
					Log.d(AppConfig.LOGTAG + AppConfig.LOGTAG_D2, "Caught a call to 'aris:' from the webview. ");
					Uri uri = Uri.parse(url);
					String path = uri.getPath();
					String mainCommand = uri.getHost();
					List<String> pathSegments = uri.getPathSegments();
					String components = pathSegments.get(0);
					return true; // App will handle it
				}
				else
					return false; // web view will handle it.
//						wvPlaqueDescription.loadUrl(urlNewString);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
//					start progress dialog
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				if (webViewPreviousState == PAGE_STARTED) {
//						progress dialog.dismiss();
//						dialog = null;

					//todo: this is where I would want to add the "injectHTMLWithARISjs" ?
					if (!jsEvaluated) { //  make sure we do this just once, as onPageFinished gets called multiple times.
						jsEvaluated = true;
						evaluateJavascript(AppUtils.getArisJs(mGamePlayAct), null);
					}
				}
			}
		});
	}
}
