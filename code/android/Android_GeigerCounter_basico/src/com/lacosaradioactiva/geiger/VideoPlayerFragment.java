package com.lacosaradioactiva.geiger;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.VideoView;

public class VideoPlayerFragment extends Fragment {

	private View v;
	private VideoView mVideoView;

	/**
	 * Called when the activity is first created.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.fragment_videoplayer, container, false); 
		
		return v;
		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initVideo();

	}

	public void initVideo() {

		mVideoView = (VideoView) v.findViewById(R.id.surface_view);

		String path = "android.resource://" + getActivity().getPackageName() + "/raw/cityfireflies";

		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(path);
		// MediaController mediaController = new MediaController(this);
		// mediaController.setAnchorView(mVideoView);
		// mVideoView.setMediaController(mediaController);
		mVideoView.requestFocus();
		mVideoView.setKeepScreenOn(true);

		mVideoView.start();

		mVideoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {

				finish();

			}
		});

	}

	public void finish() {
	}
	public void close() {

		mVideoView.stopPlayback();

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		}
		return true;
	}

	
}
