
package com.ziplinegames.moai;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiJPush {

	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) 
	{
		JPushInterface.setDebugMode(false); 
		JPushInterface.init(activity.getApplicationContext());
	}
	
}

