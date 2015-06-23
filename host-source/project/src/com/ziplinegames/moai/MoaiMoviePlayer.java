//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.IOException;
import @APP_PACKAGE@.R;
import @PACKAGE@.*;



// CDSC_VIDEO_ENABLE_CONTROL BEGIN
import android.widget.MediaController;
// CDSC_VIDEO_ENABLE_CONTROL END


// TODO:
// Allow orientation control.
// MoaiView overlay.
// Allow on-screen controls to be specified.
// Support playing video over WIFI only.
// Support multiple videos to be played in order.
// On backgrounding... Lock screen...

//================================================================//
// MoaiMoviePlayer
//================================================================//
public class MoaiMoviePlayer extends Activity implements OnCompletionListener, OnPreparedListener {

// CDSC_VIDEO_ENABLE_CONTROL BEGIN
	// Same as enum in MoaiMoviePlayerIOS
	public final static int MPMovieControlStyleNone = 0;       // No controls
	public final static int MPMovieControlStyleEmbedded = 1 + MPMovieControlStyleNone;       // No controls
	public final static int MPMovieControlStyleFullscreen = 1 + MPMovieControlStyleEmbedded;       // No controls
	public final static int MPMovieControlStyleDefault = MPMovieControlStyleEmbedded;       // No controls

	private static int	mControlStyle = MPMovieControlStyleNone;
	protected MediaController mController = null;
	public static void setControlStyle(int style) {
		mControlStyle = style;
		
	}

// CDSC_VIDEO_ENABLE_CONTROL END

	private static Activity 		sActivity = null;
	protected static MoaiMoviePlayer	sMovie = null;

	protected static native void AKUNotifyMoviePlayerReady		();
	protected static native void AKUNotifyMoviePlayerCompleted	();

	protected VideoView	mVideoView = null;
	protected RelativeLayout mLayout = null;
	//----------------------------------------------------------------//
		public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MoaiMoviePlayer onCreate: Initializing Movie Player" );
		
		sActivity = activity;
	}
	
	//================================================================//
	// MoviePlayer JNI callback methods
	//================================================================//
	
	//----------------------------------------------------------------//
	public static void init ( String url ) {
		
		if ( sMovie != null ) {
			
			sMovie.finish ();
		}
		Class <?> theClass = MoaiActivity.sInst.getMyClass("MoaiMoviePlayer");
		Intent movie = new Intent ( sActivity.getApplication (), theClass );
		movie.putExtra ( "url", url );
		sActivity.startActivity ( movie );
	}

	//----------------------------------------------------------------//
	public static void play () {
		
		if ( sMovie != null ) {
			
			sMovie.startPlayback ();
		}
	}
	
	//----------------------------------------------------------------//
	public static void pause () {
		
		if ( sMovie != null ) {
			
			sMovie.pausePlayback ();
		}
	}

	//----------------------------------------------------------------//
	public static void stop () {
		
		if ( sMovie != null ) {
			
			sMovie.stopPlayback ();
		}
	}
	
	//================================================================//
	// MoaiMoviePlayer instance methods
	//================================================================//
	
	//----------------------------------------------------------------//
	public void onCreate ( Bundle savedInstanceState ) {

		MoaiLog.i ( "MoaiMoviePlayer onCreate: activity CREATED" );
		
		super.onCreate ( savedInstanceState );

		sMovie = this;

	
		mVideoView = new VideoView ( this );
		mVideoView.setOnPreparedListener ( this );
        mVideoView.setOnCompletionListener ( this );

//CDSC_VIDEO_ENABLE_CONTROL BEGIN     
        mController = new MediaController(this);
        mVideoView.setMediaController(mController);
        mController.setMediaPlayer(mVideoView);
//CDSC_VIDEO_ENABLE_CONTROL END

		mLayout = new RelativeLayout ( this );
		mLayout.setGravity ( Gravity.CENTER );
		
        RelativeLayout.LayoutParams rlVideo = new RelativeLayout.LayoutParams ( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
        rlVideo.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlVideo.addRule(RelativeLayout.CENTER_VERTICAL);
        
        mLayout.addView ( mVideoView, rlVideo);
		if (true)
		{
			// CDSC_VIDEO_SKIP_BUTTON BEGIN
			ImageButton ibtn = new ImageButton( this );
			ibtn.setBackgroundResource(R.drawable.movieskip);
			RelativeLayout.LayoutParams ibtnLp = new RelativeLayout.LayoutParams ( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			
			ibtnLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			ibtnLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			ibtn.setOnClickListener(new ImageButton.OnClickListener(){
	
				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					MoaiLog.i ( "MoaiMoviePlayer onCompletion" );
					
					AKUNotifyMoviePlayerCompleted ();
					if (sMovie != null ){
						sMovie.finish();
					}
				}  
			
			});		
			mLayout.addView( ibtn, ibtnLp);
			// CDSC_VIDEO_SKIP_BUTTON END		
		}
		setContentView ( mLayout );		
		String url = this.getIntent ().getStringExtra ( "url" );
		
		MoaiLog.i ( "MoaiMoviePlayer onCreate: Initializing video player with media URL " + url );

		mVideoView.setVideoURI ( Uri.parse ( url ));
		mVideoView.requestFocus ();
	}	

	//----------------------------------------------------------------//
	protected void onDestroy () {
		
		super.onDestroy ();
		
		sMovie = null;
	}
	
	//----------------------------------------------------------------//
	public void startPlayback () {
		
		if ( mVideoView.isPlaying ()) {

			mVideoView.resume ();
		} else {

			mVideoView.start ();
		}
	}
	
	//----------------------------------------------------------------//
	public void pausePlayback () {
		
		mVideoView.pause ();
	}

	//----------------------------------------------------------------//
	public void stopPlayback () {

		mVideoView.stopPlayback ();
	}

	//================================================================//
	// OnPreparedListener methods
	//================================================================//

	public void onPrepared ( MediaPlayer mediaPlayer ) {

		MoaiLog.i ( "MoaiMoviePlayer onPrepared" );
		
		AKUNotifyMoviePlayerReady ();
	}

	//================================================================//
	// OnCompletionListener methods
	//================================================================//

	public void onCompletion ( MediaPlayer mediaPlayer ) {

		MoaiLog.i ( "MoaiMoviePlayer onCompletion" );
		
		AKUNotifyMoviePlayerCompleted ();
// CDSC BEGIN		
		if (sMovie != null ){
			sMovie.finish();
		}
// CDSC END		
	}
}