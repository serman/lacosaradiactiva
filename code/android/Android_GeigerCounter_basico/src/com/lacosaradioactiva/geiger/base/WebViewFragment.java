package com.lacosaradioactiva.geiger.base;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.lacosaradioactiva.geiger.R;

public class WebViewFragment extends Fragment {

	static Context c;

	private WebView mWebView;
	private String uiPath;
	//private String url = "file:/sdcard/Portfolio/_site/index.html";
	private String url = "file:///android_asset/web.html";
	//private String url = "http://192.168.1.43:8081/static/livecoding/index.html#html,client";
	//private String url = "http://www.google.es";

	public WebViewFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 

		return inflater.inflate(R.layout.webviewmanual, container, false);
	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mWebView = (WebView) getActivity().findViewById(R.id.webview);

		// mWebView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

		// mWebView = new WebView(this);

		

		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());

		WebSettings webSettings = mWebView.getSettings();
		// webSettings.setSavePassword(false);
		// webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setSupportZoom(true);
		//webSettings.setDefaultZoom(ZoomDensity.CLOSE);
		// webSettings.setDefaultFontSize(35);
		webSettings.setPluginsEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); 
		//webSettings.setDomStorageEnabled(true); // localStorage

		
		//mWebView.setInitialScale(1);
		mWebView.setFocusable(true);
		mWebView.setFocusableInTouchMode(true);
		//mWebView.setBackgroundDrawable(android.R.color.transparent);
		mWebView.addJavascriptInterface(new QQ(), "qq1"); 
		mWebView.addJavascriptInterface(new QQ2(), "qq1"); 
		mWebView.clearCache(false); 
		
		//mWebView.getSettings().setDatabasePath()

		//mWebView.clearHistory();
	

		mWebView.loadUrl(url);
		mWebView.setBackgroundColor(0x11000000);

		mWebView.requestFocus(View.FOCUS_DOWN);
		mWebView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		}); 

	} 
	
	final class QQ {
		public QQ() {
		}
		
	} 
	
	public void senToJavascript() { 
		
	    String qq = "qq2"; 
	    
		mWebView.loadUrl("javascript:callFromActivity(\""+ qq  +"\")");

	}

	final class QQ2 {
		public QQ2() {
		
		}
	} 

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		mWebView.loadUrl(url);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	/* 
	 * 
	 */
	final class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("", "override url loading");

			if (url.contains("MP4")) {
				// Intents.openWeb(getApplicationContext(), url);
				return true;
			} else {
				view.loadUrl(url);
				return false;
			} 
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d("", errorCode + " " + description);
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d("", "Loading web"); 

		}

		public void onPageFinished(WebView view, String url) {
			Log.d("", "Loading web"); 

			//capture webview 
			Picture p = view.capturePicture();
			
		} 
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			result.confirm();
			return true;
		} 
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
			if (view instanceof FrameLayout) {
				FrameLayout frame = (FrameLayout) view;
				if (frame.getFocusedChild() instanceof VideoView) {
					VideoView video = (VideoView) frame.getFocusedChild();
					video.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mp.stop();

						}
					});

					frame.removeView(video);
					/*
					 * a.setContentView(video);
					 * video.setOnCompletionListener(this);
					 * video.setOnErrorListener(this); video.start();
					 */
				}
			}
		}

	} 
	

}