
package com.ziplinegames.moai;

import com.tendcloud.tenddata.TalkingDataGA;

import android.app.Activity;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiTalkingdata {
	static Activity sActivity = null;
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
	}
	
	public static void onPause()
	{	
		if(sActivity != null)
		{
			TalkingDataGA.onPause(sActivity);
		}
		
	}
	
	public static void onResume()
	{
		if(sActivity != null)
		{
			TalkingDataGA.onResume(sActivity);
		}
	}
}

