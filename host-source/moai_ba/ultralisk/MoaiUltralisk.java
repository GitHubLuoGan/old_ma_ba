//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;

import com.ultralisk.CallbackListener;
import com.ultralisk.Ultralisk;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiUltralisk {

	protected static native void	AKUNotifyUltraliskPaymentSuccess( String orderNo );
	protected static native void	AKUNotifyUltraliskPaymentError( String errorMessage, String orderNo );


	private static Activity sActivity = null;

	private static Ultralisk sUltralisk = null;

	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MOAIUltralisk onCreate: Initializing donwjoy" );
		
		sActivity = activity;
	}

	public static boolean onPreRunning( Activity activity, final Moai.OnFinishHandler onfinish)
	{
		final String merchantId="0";
	    final String appId="195";
	    final String serverSeqNum="1";
	    final String appKey="j5VEvxhc";
		if (sUltralisk == null) 
		{
			sUltralisk = new Ultralisk(merchantId, appId, serverSeqNum, appKey);
		}
		onfinish.callback(true);
		return true;
	}
	
	public static void openPaymentDialog ( float money, String productName, String roleName, String extInfo, String notify_url) 
	{
	    if (sUltralisk == null || sActivity == null) 
	    {
	    	return;
	    }
    	sUltralisk.openPaymentDialog(sActivity, money, productName, roleName, extInfo, notify_url, new CallbackListener() 
	    {
		    public void onPaymentSuccess(String orderNo) 
		    {
				synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUltraliskPaymentSuccess( orderNo );
				}
		    }

		    @Override
		    public void onPaymentError(String error, String orderNo) 
		    {
		    	synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUltraliskPaymentError( error, orderNo);
				}
		    }
	});
	}

	
}

