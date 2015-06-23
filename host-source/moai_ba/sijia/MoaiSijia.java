//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import com.sjyxsdk.activity.Sijiu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.eclipsesource.json.JsonObject;
import java.io.InputStreamReader;
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiSijia 
{

    private static Activity sActivity 		= null;
  
    protected static native void AKUNotifySijiaLoginSuccess (String uid, String sessionId, String userName);
    protected static native void AKUNotifySijiaLoginFail (String msg);
    protected static native void AKUNotifySijiaPaymentSuccess ();
    protected static native void AKUNotifySijiaPaymentFail (String msg);
    protected static native void AKUNotifySijiaPaymentCancel ();
    
    static  String appId         = "24";
    static String appKey        = "c27f03c0a0c1eaac2d94aa64a004d3dd"
	static String server_id		= "0"
	static String agent			= ""
    		
    		
    //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
		MoaiLog.i ( "MOAISijia onCreate: Initializing sijia" );
		sActivity = activity;
		registerBoradcastReceiver();
		loadChannelConfig();
    }
    
  //读取配置文件
	public static void loadChannelConfig(){	 
		try { 
			String fileName="cConfig.json";
            InputStreamReader inputReader = new InputStreamReader(sActivity.getResources().getAssets().open(fileName) ); 
            JsonObject jsonObj=JsonObject.readFrom(inputReader);
            appId=jsonObj.get("appId").asString();
            appKey=jsonObj.get("appKey").asString();
            server_id=jsonObj.get("server_id").asString();
            agent=jsonObj.get("agent").asString(); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
	}
	
	public static void onDestroy () 
    {	
		sActivity = null;
    }
	private static BroadcastReceiver sBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			if (action.equals("com.sjyx.login")) {
				Bundle db = intent.getExtras();
				String flag = db.getString("result");
				String uid = db.getString("userid");// 用户id
				String timeStamp = db.getString("timestamp");// 登录时间戳
				//String sign = db.getString("sign");
				if (flag.equals("success")) {
					synchronized(Moai.sAkuLock)
					{
						AKUNotifySijiaLoginSuccess(uid, timeStamp, null);
					}
				} else {
					synchronized(Moai.sAkuLock)
					{
						AKUNotifySijiaLoginFail(flag);
					}
				}
			}
			if(action.equals("com.sjyx.payment")){
				Bundle db = intent.getExtras();
				String flag = db.getString("result");
				if (flag.equals("success"))
				{
					synchronized(Moai.sAkuLock)
					{
						AKUNotifySijiaPaymentSuccess();
					}
				}
				else
				{
					synchronized(Moai.sAkuLock)
					{
						AKUNotifySijiaPaymentFail(flag);
					}
				}
			}
		}
	};
	
	 public static void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("com.sjyx.login");
		myIntentFilter.addAction("com.sjyx.payment");		
		// 注册广播
		sActivity.registerReceiver(sBroadcastReceiver, myIntentFilter);
	}
	 
    public static void openLoginDialog (String appid, String appkey, String server_id, String agent)
    {
    	if (sActivity == null ) 
		{
		    return;
		}
    	
    	Sijiu  sijiu = new  Sijiu(sActivity);
    	sijiu.Login(appid, appkey, server_id, agent);
    }
   
    public static void openPaymentDialog(String app_id, 
    									 String app_key, 
    									 String order_id, 
    									 String goods_name, 
    									 String goods_info, 
    									 String amount, 
    									 String agent,
    									 String user,
    									 String server_id,
    									 String extra_info,
    									 String subject,
    									 int multiple)
    {
    	if (sActivity == null) 
    	{
    	    return;
    	}
    	
    	Sijiu  sijiu = new  Sijiu(sActivity);
    	sijiu.recharge(app_id, app_key, order_id, goods_name,  goods_info, 
    					amount, agent, user, server_id, extra_info, subject, multiple);
    }
    
    public static void exit()
    {
    	Sijiu  sijia = new  Sijiu(sActivity);
    	sijia.Exit();
    }

}


