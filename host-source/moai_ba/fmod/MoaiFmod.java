//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import org.fmod.FMODAudioDevice;
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiFmod 
{

    private static Activity sActivity 		= null;
    private static FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice(); 
    //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
		MoaiLog.i ( "MOAIFmod onCreate: Initializing fmod" );
		sActivity = activity;
    }
	public static void onDestroy () 
    {	
		
    }
   
	public static void onStart()
    {
    	mFMODAudioDevice.start(); 
    }
	
    public static void onStop()
    {
    	mFMODAudioDevice.stop(); 
    }
    
    public static void onPause()
	{	
    	mFMODAudioDevice.stop();
	}
	
	public static void onResume()
	{
		mFMODAudioDevice.start();
	}
}



