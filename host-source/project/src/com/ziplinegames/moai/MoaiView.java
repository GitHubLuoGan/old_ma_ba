//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;
 
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
//CSDC_PATCH_GLSurfaceView
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.util.DisplayMetrics;

// Moai
import com.ziplinegames.moai.*;

//================================================================//
// MoaiView
//================================================================//
//CSDC_PATCH_GLSurfaceView
public class MoaiView extends CDSCGLSurfaceView {

	private static final long	AKU_UPDATE_FREQUENCY = 1000 / 60; // 60 Hz, in milliseconds

	private Context		mAppContext;
	private Handler		mHandler;
	private int 		mHeight;
	private Runnable	mUpdateRunnable;
	private int 		mWidth;
	
    //----------------------------------------------------------------//
	public MoaiView ( Context context, int width, int height, int glesVersion ) {

		super ( context );
		
		mAppContext = context.getApplicationContext();
		//防止中断出去的时候Opengl Context被破坏
		setPreserveEGLContextOnPause(true);
		setScreenDimensions ( width, height );
		Moai.setScreenSize ( mWidth, mHeight );
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		Moai.setScreenDpi(metrics.densityDpi);

		if ( glesVersion >= 0x20000 ) {
			
			// NOTE: Must be set before the renderer is set.
			setEGLContextClientVersion ( 2 );
		}
		
		// Create a handler that we can use to post to the main thread and a pseudo-
		// periodic runnable that will handle calling Moai.update on the main thread.
		mHandler = new Handler ( Looper.getMainLooper ());
		//#if CDSC_PATCH
		mUpdateRunnable = new Runnable () {
			public void run () {
				mHandler.postDelayed ( mUpdateRunnable , AKU_UPDATE_FREQUENCY );
				MoaiKeyboard.update ();
				Moai.update(); //仔细思量后认为网游的刷新，不能和渲染完全同步，因此这里应该是即时更新的
				requestRender();
			}
		};
		//#else
/*
		mUpdateRunnable = new Runnable () {
			public void run () {
				//MoaiKeyboard.update (); //CDSC_REMOVE
				Moai.update ();
				mHandler.postDelayed ( mUpdateRunnable , AKU_UPDATE_FREQUENCY );
			}
		};
*/
		
		//#endif
        setRenderer ( new MoaiRenderer ());
		onPause (); // Pause rendering until restarted by the activity lifecycle.		
	}		
	
	//================================================================//
	// Public methods
	//================================================================//

	//----------------------------------------------------------------//
	@Override
	public void onSizeChanged ( int newWidth, int newHeight, int oldWidth, int oldHeight ) {
		
		setScreenDimensions ( newWidth, newHeight );
		Moai.setViewSize ( mWidth, mHeight );
		// CDSC_ENABLE_OS_EVENT
		Moai.enqueueOsEvent(Moai.InputDevice.INPUT_DEVICE.ordinal (),
				Moai.InputSensor.SENSOR_OS.ordinal (),
				Moai.OsEvent.OS_EVENT_RESIZE.ordinal()
		);
	}
	
	//----------------------------------------------------------------//
	public void pause ( boolean paused ) {
	
		if ( paused ) {

			mHandler.removeCallbacks ( mUpdateRunnable ); 
			
			Moai.pause ( true );
			setRenderMode ( CDSCGLSurfaceView.RENDERMODE_WHEN_DIRTY );
			onPause ();			
		}
		else {

			onResume ();
			//#if CDSC_PATCH
			setRenderMode ( CDSCGLSurfaceView.RENDERMODE_WHEN_DIRTY );
			//#else
			//setRenderMode ( GLSurfaceView.RENDERMODE_CONTINUOUSLY );
			//#endif
			Moai.pause ( false );

			//#if CDSC_PATCH
			mHandler.postDelayed ( mUpdateRunnable , 0 ); 
			//#else
			//mHandler.postDelayed ( mUpdateRunnable , AKU_UPDATE_FREQUENCY ); 
			//#endif
		}
	}

	//================================================================//
	// MotionEvent methods
	//================================================================//

    //----------------------------------------------------------------//
	@Override
	public boolean onTouchEvent ( MotionEvent event ) {

		boolean isDown = true;
        
		switch( event.getActionMasked() )
		{
			case MotionEvent.ACTION_CANCEL:
				/*Moai.enqueueTouchEventCancel(
					Moai.InputDevice.INPUT_DEVICE.ordinal (),
					Moai.InputSensor.SENSOR_TOUCH.ordinal ()
				);*/
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				isDown = false;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			{
				final int pointerIndex = event.getActionIndex();
				int pointerId = event.getPointerId ( pointerIndex );
				Moai.enqueueTouchEvent (
					Moai.InputDevice.INPUT_DEVICE.ordinal (),
					Moai.InputSensor.SENSOR_TOUCH.ordinal (),
					pointerId, 
					isDown, 
					Math.round ( event.getX ( pointerIndex )), 
					Math.round ( event.getY ( pointerIndex )), 
					1
				);
				break;
			}
			case MotionEvent.ACTION_MOVE:
			default:
			{
				final int pointerCount = event.getPointerCount ();
				for ( int pointerIndex = 0; pointerIndex < pointerCount; ++pointerIndex ) {
				
					int pointerId = event.getPointerId ( pointerIndex );
					Moai.enqueueTouchEvent (
						Moai.InputDevice.INPUT_DEVICE.ordinal (),
						Moai.InputSensor.SENSOR_TOUCH.ordinal (),
						pointerId, 
						isDown, 
						Math.round ( event.getX ( pointerIndex )), 
						Math.round ( event.getY ( pointerIndex )), 
						1
					);
				}
				break;
			}
		}
		
		return true;
	}
	
	//================================================================//
	// Private methods
	//================================================================//
	
	//----------------------------------------------------------------//
	public void setScreenDimensions ( int width, int height ) {
		Resources resources = mAppContext.getResources();
		Configuration config = resources.getConfiguration();

		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mWidth = Math.min(width, height);
			mHeight = Math.max(width, height);
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mWidth = Math.max(width, height);
			mHeight = Math.min(width, height);
		} else {
			mWidth = width;
			mHeight = height;
		}
	}
	
	//================================================================//
	// MoaiRenderer
	//================================================================//
	private class MoaiRenderer implements GLSurfaceView.Renderer {

		private boolean mRunScriptsExecuted = false;

	    //----------------------------------------------------------------//
		@Override
		public void onDrawFrame ( GL10 gl ) {

			Moai.render ();
		}

	    //----------------------------------------------------------------//
		@Override
		public void onSurfaceChanged ( GL10 gl, int width, int height ) {

			MoaiLog.i ( "MoaiRenderer onSurfaceChanged: surface CHANGED" );

			setScreenDimensions ( width, height );
			Moai.setViewSize ( mWidth, mHeight );
		}
        
	    //----------------------------------------------------------------//
		@Override
		public void onSurfaceCreated ( GL10 gl, EGLConfig config ) {

			MoaiLog.i ( "MoaiRenderer onSurfaceCreated: surface CREATED" );
			//CDSC add for interrupt bug(ex. xiaomi 2s)
			Moai.releaseGraphicsContext();
			//CDSC add end
			Moai.detectGraphicsContext ();
			
			if ( !mRunScriptsExecuted ) {

				mRunScriptsExecuted = true;

				mHandler.postAtFrontOfQueue ( new Runnable () {

					public void run () {
				
						MoaiLog.i ( "MoaiRenderer onSurfaceCreated: Running game scripts" );
				
						@RUN_COMMAND@

						Moai.startSession ( false );

						Moai.setApplicationState ( Moai.ApplicationState.APPLICATION_RUNNING );
					
					}
				});
			} else {
				
				mHandler.post ( new Runnable () {

					public void run () {
				

						Moai.startSession ( true );

						Moai.setApplicationState ( Moai.ApplicationState.APPLICATION_RUNNING );
					
					}
				});
			}
		}	
		
	    //----------------------------------------------------------------//
		private void runScripts ( String [] filenames ) {

			for ( String file : filenames ) {
				
				MoaiLog.i ( "MoaiRenderer runScripts: Running " + file + " script" );
					
				Moai.runScript ( file );
			}
		}	
	}
}

