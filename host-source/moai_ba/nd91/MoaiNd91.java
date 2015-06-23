//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.nd.commplatform.*;
import com.nd.commplatform.entry.NdAppInfo;
import com.nd.commplatform.entry.NdBuyInfo;
import com.nd.commplatform.NdPageCallbackListener.OnPauseCompleteListener;
import com.nd.commplatform.gc.widget.NdToolBar;
import com.nd.commplatform.gc.widget.NdToolBarPlace;


import com.eclipsesource.json.JsonObject;
import java.io.InputStreamReader;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiNd91 
{

    public static Activity sActivity 		= null;
    
    private static ProgressDialog sProgressDialog = null;
	
	private static OnInitCompleteListener mOnInitCompleteListener;
	private static boolean sInited = true;
	private static NdToolBar sToolBar;
    protected static void showLoading()
    {
	if (sActivity != null)
	{
	    sProgressDialog = new ProgressDialog(sActivity);
	    sProgressDialog.setMessage("正在加载...");
	    sProgressDialog.setIndeterminate(true);
	    sProgressDialog.setCancelable(false);
	    sProgressDialog.show();
	}
    }
    
    
    protected static void showLoading(String tips)
    {
	if (sActivity != null)
	{
	    sProgressDialog = new ProgressDialog(sActivity);
	    sProgressDialog.setMessage(tips);
	    sProgressDialog.setIndeterminate(true);
	    sProgressDialog.setCancelable(false);
	    sProgressDialog.show();
	}    
    }
    
    protected static void hideLoading()
    { 
	if(sProgressDialog != null)
	{
	    sProgressDialog.cancel();
	    sProgressDialog = null;
	} 
    }
    protected static native void AKUNotifyNd91InitSuccess ();
    protected static native void AKUNotifyNd91InitError ();
    protected static native void AKUNotifyNd91LoginSuccess (String uin, String sessionId);
    protected static native void AKUNotifyNd91LoginError ();
    protected static native void AKUNotifyNd91LoginExit ();
    protected static native void AKUNotifyNd91PaymentSuccess ();
    protected static native void AKUNotifyNd91PaymentError ();
    protected static native void AKUNotifyNd91PaymentErrorAsynSmsSent ();
    protected static native void AKUNotifyNd91PaymentErrorAsynRequestSubmitted ();
    protected static native void AKUNotifyNd91PaymentUserExit ();

    static int appId			= 105339;
    static String appKey 		= "bf1e8ff0cbe4eb5e8ee05113eb2988d6dd56baf4619fa09a";
    static boolean is_login     = false;

    //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
	MoaiLog.i ( "MOAINd91 onCreate: Initializing nd91" );
	sActivity = activity;
	
	loadChannelConfig();
    }

	//读取配置文件
	public static void loadChannelConfig(){	 
		try { 
			String fileName="cConfig.json";
            InputStreamReader inputReader = new InputStreamReader(sActivity.getResources().getAssets().open(fileName) ); 
            JsonObject jsonObj=JsonObject.readFrom(inputReader);
            appId=jsonObj.get("appId").asInt();
            appKey=jsonObj.get("appKey").asString(); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
	}
	
	public static void onDestroy () 
    {	
		if(sToolBar != null)
		{
			sToolBar.recycle();
			sToolBar = null;
		}
		if(mOnInitCompleteListener != null){
			mOnInitCompleteListener.destroy();
		}
		sActivity = null;
		sInited = false;
    }
    public static void init ( int appId, String appKey, boolean isLandscape, boolean debugMode) 
    {
    	//nothing
    }
    public static boolean onPreRunning( Activity activity, final Moai.OnFinishHandler onfinish)
    {
    	if (Moai.getNetworkState() == 0)
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder ( activity );
    		
    		builder.setTitle ( "温馨提示" );
    		builder.setMessage ( "请检查网络是否正确连接" );
    		builder.setPositiveButton ( "确定", new DialogInterface.OnClickListener () 
    		{	
    			public void onClick ( android.content.DialogInterface arg0, int arg1 ) 
    			{
    				onfinish.callback(false);
    			}
    		});
    		
    		builder.create ().show ();
    		return false;
    	}
    	
    	boolean debugMode	= false;
    	boolean isLandscape	= true;
    	
    	
    	if(debugMode)
    	{
    		NdCommplatform.getInstance().ndSetDebugMode(0);
    	}
    	
    	NdAppInfo appInfo = new NdAppInfo(); 
    	appInfo.setCtx( activity );
    	appInfo.setAppId( appId );
    	appInfo.setAppKey( appKey );
    	OnInitCompleteListener mOnInitCompleteListener = new OnInitCompleteListener()
    	{
    		protected void onComplete(int ndFlag) 
    		{

				sInited = true;
    			onfinish.callback(true);
    		}
    	};
    	NdCommplatform.getInstance().ndInit(activity, appInfo, mOnInitCompleteListener);
    	int orient = NdCommplatform.SCREEN_ORIENTATION_PORTRAIT; 
    	
    	if (isLandscape)
    	{
    		orient = NdCommplatform.SCREEN_ORIENTATION_LANDSCAPE; 
    	}
    	NdCommplatform.getInstance().ndSetScreenOrientation(orient);

    	return true;
    }
    
    public static boolean onPreExit( Activity activity, final Moai.OnFinishHandler onfinish )
    {
    	NdCommplatform.getInstance().ndExit(new NdPageCallbackListener.OnExitCompleteListener(sActivity)
    	{
    		public void onComplete() 
    		{
    			onfinish.callback(true);
    		}
    	});
    	return true;
    }
    
	

    
    public static NdMiscCallbackListener.OnLoginProcessListener sLoginProcessListener = new NdMiscCallbackListener.OnLoginProcessListener() 
	{
	    @Override
	    public void finishLoginProcess(int code) 
	    {
		String tip = null; 
		hideLoading();
		
		// 登录的返回码检查
		if (code == NdErrorCode.ND_COM_PLATFORM_SUCCESS) 
		{  
			String uin 	 = NdCommplatform.getInstance().getLoginUin();
			String sessionId = NdCommplatform.getInstance().getSessionId();
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyNd91LoginSuccess (uin, sessionId );
			}
		} 
		else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_CANCEL) 
		{
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyNd91LoginExit ( );
			}
			
			tip = "取消登录"; 
		} 
		else  
		{
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyNd91LoginError ( );
			}
			tip = "登录失败，错误代码：" + code; 
		}

		if ( tip != null && tip.length() > 0 ) 
		{
		    Toast.makeText(sActivity, tip, Toast.LENGTH_SHORT).show(); 
		}
		
	    } 
	};
    public static void openLoginDialog ()
    {
    	if (sActivity == null) 
		{
		    return;
		}
    	
    	NdCommplatform.getInstance().ndLogin(sActivity, sLoginProcessListener); 
    }
    
    public static void openSwitchAccountDialog()
    {
    	if (sActivity == null) 
		{
		    return;
		}
    	is_login = true;
    	NdCommplatform.getInstance().ndLogin(sActivity, sLoginProcessListener); 
    
    	
    	//NdCommplatform.getInstance().ndSwitchAccount(sActivity, sLoginProcessListener);
    }
	
	public static void showFloatButton(boolean show)
	{
		 
		if (sToolBar == null)
			{
			sToolBar = NdToolBar.create(sActivity, NdToolBarPlace.NdToolBarRightMid);
			}
		if(show)
		{
			sToolBar.show();
		}else
		{
			sToolBar.hide();
		}
	}

    public static void paySyn(String serial, String productId, String productName, double price, int amount, String description)
    {
    	if (sActivity == null) 
    	{
    	    return;
    	}
    	
    	NdBuyInfo buyInfo = new NdBuyInfo();
		buyInfo.setSerial(serial);
		buyInfo.setProductId(productId);
		buyInfo.setProductName(productName);
		buyInfo.setProductPrice(price);
		buyInfo.setCount(amount);
		buyInfo.setPayDescription(description);
		
		int aError = NdCommplatform.getInstance().ndUniPay(buyInfo, sActivity, new NdMiscCallbackListener.OnPayProcessListener() {
			
			@Override
			public void finishPayProcess(int code) {
				if (code == NdErrorCode.ND_COM_PLATFORM_SUCCESS) {
					Toast.makeText(sActivity, "购买成功", Toast.LENGTH_SHORT).show(); 
					//购买成功，此时可向玩家发放道具
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentSuccess ( );
					}
				    
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_FAILURE) {
					Toast.makeText(sActivity, "购买失败", Toast.LENGTH_SHORT).show(); 
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentError ( );
					}
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_CANCEL) {
					Toast.makeText(sActivity, "取消", Toast.LENGTH_SHORT).show(); 
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentUserExit ( );
					}
				} else {
					Toast.makeText(sActivity, "购买失败" + code, Toast.LENGTH_SHORT).show(); 
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentError ( );
					}
				}
			}
		});
		if(aError != 0){
			Toast.makeText(sActivity, "您输入的商品信息格式不正确", Toast.LENGTH_SHORT).show(); 
		}
		
    }

    public static void payAsyn(String serial, String productId, String productName, double price, int amount, String description)
    {
    	if (sActivity == null) 
    	{
    	    return;
    	}
    	
    	NdBuyInfo buyInfo = new NdBuyInfo();
		buyInfo.setSerial(serial);
		buyInfo.setProductId(productId);
		buyInfo.setProductName(productName);
		buyInfo.setProductPrice(price);
		buyInfo.setCount(amount);
		buyInfo.setPayDescription(description);
		
		int aError = NdCommplatform.getInstance().ndUniPayAsyn(buyInfo, sActivity, new NdMiscCallbackListener.OnPayProcessListener() {
			
			@Override
			public void finishPayProcess(int code) {
				if (code == NdErrorCode.ND_COM_PLATFORM_SUCCESS) {
					Toast.makeText(sActivity, "购买成功", Toast.LENGTH_SHORT).show();
					//购买成功，此时可向玩家发放道具
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentSuccess ( );
					}
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_FAILURE) {
					Toast.makeText(sActivity, "购买失败", Toast.LENGTH_SHORT).show(); 
					//购买失败
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentError ( );
					}
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_CANCEL) {
					Toast.makeText(sActivity, "取消购买", Toast.LENGTH_SHORT).show(); 
					//取消购买
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentUserExit ( );
					}
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_ASYN_SMS_SENT) {
					Toast.makeText(sActivity, "订单已提交，充值短信已发送", Toast.LENGTH_SHORT).show(); 
					//此时用户账号余额不足，通过短信充值来购买道具，由于短信有时需要玩家回确认短信，
					//所以开发者需要记下购买的订单号，以便向服务器查询玩家是否购买成功以便向玩家发放道具
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentErrorAsynSmsSent ( );
					}
				
				} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_REQUEST_SUBMITTED) {
					Toast.makeText(sActivity, "订单已提交", Toast.LENGTH_SHORT).show(); 
					//此时用户账号余额不足，通过充值来购买道具，单此时还无法知道玩家此次购买是否成功，
					//所以开发者需要记下购买的订单号，以便向服务器查询玩家是否购买成功以便向玩家发放道具
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentErrorAsynRequestSubmitted ( );
					}
				} else {
					Toast.makeText(sActivity, "购买失败" + code, Toast.LENGTH_SHORT).show(); 
					synchronized ( Moai.sAkuLock ) 
					{
						AKUNotifyNd91PaymentError ( );
					}
				}
			}
		});
		if(aError != 0){
			Toast.makeText(sActivity, "您输入的商品信息格式不正确", Toast.LENGTH_SHORT).show(); 
		}
    }

    public static void payForCoin(String cooOrderSerial, int needPayCoins, String note)
    {
    	if (sActivity == null ) 
    	{
    	    return;
    	}
		cooOrderSerial = cooOrderSerial.replace("-", "".trim());  
		NdCommplatform.getInstance().ndUniPayForCoin(cooOrderSerial, needPayCoins, note, sActivity);
    }

    public static void openMemberCenterDialog()
    {
    	NdCommplatform.getInstance().ndEnterPlatform(0, MoaiBaseSdk.sActivity);
    }
    public static void openBBSDialog()
    {
    	NdCommplatform.getInstance().ndEnterAppBBS(MoaiBaseSdk.sActivity,0);
    }
    public static void openFeedbackDialog()
    {
    	NdCommplatform.getInstance().ndUserFeedback(MoaiBaseSdk.sActivity);
    }

	private static boolean isAppForeground = true;
	public static boolean isAppOnForeground() {
		android.app.ActivityManager activityManager = (android.app.ActivityManager) sActivity.getApplicationContext()
				.getSystemService(android.content.Context.ACTIVITY_SERVICE);
		String packageName = sActivity.getApplicationContext().getPackageName();
		java.util.List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	} 
    public static void onStop()
    {
    	if(!isAppOnForeground() && sInited){//app进入后台
			isAppForeground = false;
		}
    }

	public static void onResume()
	{
		if(!isAppForeground && sInited && !is_login){//从后台切到前台，打开91SDK暂停页
			NdCommplatform.getInstance().ndPause(new OnPauseCompleteListener(sActivity){

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					//Toast.makeText(sActivity, "退出暂停页", Toast.LENGTH_LONG).show();
				}
				
			});
			isAppForeground = true;
		}
	}


}



