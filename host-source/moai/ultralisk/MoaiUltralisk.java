//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.ultralisk.CallbackListener;
import com.ultralisk.Ultralisk;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiUltralisk extends MoaiBaseSdk {

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
	
	
	
	/*************************************************************/
	
	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		 
//		 channelId = sConfigJsonObject.get("channelId").asString();
//	     appSecret = sConfigJsonObject.get("appSecret").asString();
//		 appKey = sConfigJsonObject.get("key").asString();
//		 paynotifyUrl = sConfigJsonObject.get("paynotifyUrl").asString();
//		
//		SdkOpenApi.sdkStart();
//		AppConnect.getInstance(sActivity).initSdk(appKey, channelId);
		final String merchantId="0";
	    final String appId="195";
	    final String serverSeqNum="1";
	    final String appKey="j5VEvxhc";
		if (sUltralisk == null) 
		{
			sUltralisk = new Ultralisk(merchantId, appId, serverSeqNum, appKey);
		}
		
	}
	
	
	 public static String V2_exitGame(JsonValue parms){
			JsonRpcCall(Lua_Cmd_GameExit,"");
			return "";
		}
	 
	 public void ResultChannelInfo(){
			
			JsonObject jsonData=new JsonObject();
			
			jsonData.add("isUltralisk", true);

			JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

	    }
	
	 public static String V2_OpenPay(JsonValue parms){
		 
		 	JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();
			
			String mNotifyUrl = sConfigJsonObject.get("mNotifyUrl").asString();
			
			String OrderNo = payinfoJson.get("orderno").asString();
			float price   = Float.parseFloat(payinfoJson.get("price").asString());
			String proName = payinfoJson.get("name").asString();
			
			String roleName = userinfoJson.get("roleName").asString();
			
			//String callBackUrl = sConfigJsonObject.get("callBackUrl").asString();
			
			
			
			if (sUltralisk == null || sActivity == null) 
		    {
		    	return "";
		    }
	    	sUltralisk.openPaymentDialog(sActivity, price, proName, roleName, OrderNo, mNotifyUrl, new CallbackListener() 
		    {
			    /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
		 
		 
		 return "";
		 
	 }
	/*************************************************************/
	
	
	
	
	
	
	
	
	
	
	/*****************************************************************/
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

	/*******************************************************************/
}

