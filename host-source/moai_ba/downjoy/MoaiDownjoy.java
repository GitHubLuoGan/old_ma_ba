//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
 
import com.downjoy.*;
import com.downjoy.util.Util;


import com.eclipsesource.json.JsonObject;
import java.io.InputStreamReader;


//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiDownjoy {

	protected static native void	AKUNotifyDownjoyLoginSuccess ( String memberId, String username, String nickname, String token );
	protected static native void	AKUNotifyDownjoyLoginError ( int errorCode, String errorMessage );
	protected static native void	AKUNotifyDownjoyLoginErrorOther ( String errorMessage );

	protected static native void	AKUNotifyDownjoyMemberCenterBack( );
	protected static native void	AKUNotifyDownjoyMemberCenterError( int errorCode, String errorMessage );
	protected static native void	AKUNotifyDownjoyMemberCenterErrorOther( String errorMessage );

	protected static native void	AKUNotifyDownjoyUserInfoSuccess( String memberId, String username, String nickname, String gender, int level, String avatarUrl, String createdDate );
	protected static native void	AKUNotifyDownjoyUserInfoError( int errorCode, String errorMessage );
	protected static native void	AKUNotifyDownjoyUserInfoErrorOther( String errorMessage );

	protected static native void	AKUNotifyDownjoyLogoutSuccess( );
	protected static native void	AKUNotifyDownjoyLogoutError( int errorCode, String errorMessage );
	protected static native void	AKUNotifyDownjoyLogoutErrorOther( String errorMessage );

	protected static native void	AKUNotifyDownjoyPaymentSuccess( String orderNo );
	protected static native void	AKUNotifyDownjoyPaymentError( int errorCode, String errorMessage );
	protected static native void	AKUNotifyDownjoyPaymentErrorOther( String errorMessage );


	private static Activity sActivity = null;

	private static Downjoy sDownjoy = null;

	
	static String _merchantId="";
	static String _appId="";
	static String _serverSeqNum="";
	static String _appKey="";
	
	
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MOAIDownjoy onCreate: Initializing donwjoy" );
		
		sActivity = activity;
		loadChannelConfig();
	}

	//读取配置文件
	public static void loadChannelConfig(){	 
		try { 
			String fileName="cConfig.json";
            InputStreamReader inputReader = new InputStreamReader(sActivity.getResources().getAssets().open(fileName) ); 
            JsonObject jsonObj=JsonObject.readFrom(inputReader);
            _merchantId=jsonObj.get("merchantId").asString();
            _appId=jsonObj.get("appId").asString();
            _serverSeqNum=jsonObj.get("serverSeqNum").asString();
            _appKey=jsonObj.get("appKey").asString(); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
	}
	
 public static void onResume() {   
        if (sDownjoy != null) {  
            sDownjoy.resume(sActivity);  
        }  
    }  
  
 
    protected void onPause() {   
  
        if (sDownjoy != null) {  
            sDownjoy.pause();  
        }  
        
    } 

public void onDestroy() {   
        if (sDownjoy != null) {  
            sDownjoy.destroy();  
            sDownjoy = null;  
        }  
  
    }  


	//================================================================//
	// downjoy JNI callback methods
	//================================================================//

	//----------------------------------------------------------------//
	 //final String merchantId="101"; // 当乐分配的MERCHANT_ID
     //final String appId="195"; // 当乐分配的APP_ID
     //final String serverSeqNum="1"; // 当乐分配的SERVER_SEQ_NUM，不同服务器序列号可使用不同计费通知地址
     //final String appKey="j5VEvxhc"; // 当乐分配的APP_KEY
	public static void init ( String merchantId, String appId, String serverSeqNum, String appKey ) 
	{
	    if (sDownjoy == null) {
	    sDownjoy =Downjoy.getInstance(sActivity,_merchantId, _appId, _serverSeqNum, _appKey);
		//sDownjoy = new Downjoy(merchantId, appId, serverSeqNum, appKey);
	    }
	}

	//----------------------------------------------------------------//
	public static void openLoginDialog ( int serverId ) 
	{
	    if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }

	    sDownjoy.openLoginDialog(sActivity,new CallbackListener() 
		    {

                    @Override
                    public void onLoginSuccess(Bundle bundle) 
				    {
                        String memberId=bundle.getString(Downjoy.DJ_PREFIX_STR + "mid");
                        String username=bundle.getString(Downjoy.DJ_PREFIX_STR + "username");
                        String nickname=bundle.getString(Downjoy.DJ_PREFIX_STR + "nickname");
                        String token=bundle.getString(Downjoy.DJ_PREFIX_STR + "token");

                        //Util.alert(sActivity, "mid:" + memberId + "\nusername:" + username + "\nnickname:" + nickname + "\ntoken:" + token);
						synchronized ( Moai.sAkuLock ) 
						{
						    AKUNotifyDownjoyLoginSuccess ( memberId, username, nickname, token );
						}
				    }

                    @Override
                    public void onLoginError(DownjoyError error) 
                    {
                        int errorCode=error.getMErrorCode();
                        String errorMsg=error.getMErrorMessage();

                        //Util.alert(sActivity, "onLoginError:" + errorCode + "|" + errorMsg);
						synchronized ( Moai.sAkuLock ) 
						{
						    AKUNotifyDownjoyLoginError ( errorCode, errorMsg );
						}
                    }

                    @Override
                    public void onError(Error error) 
				    {
                        //String errorMessage=error.getMessage();
                        //Util.alert(sActivity, "onError:" + errorMessage);
						String errorMessage = error.getMessage();
						synchronized ( Moai.sAkuLock ) 
						{
						    AKUNotifyDownjoyLoginErrorOther ( errorMessage );
						}
		            }
 
                    @Override  
                    public void onSwitchAccountAndRestart() {   
                    	final Intent intent = sActivity.getPackageManager().getLaunchIntentForPackage(sActivity.getPackageName());  
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
                        sActivity.startActivity(intent); 
                    }  
                    
                });
	}
		
	//----------------------------------------------------------------//
	public static void openMemberCenterDialog ( ) 
	{

	    if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }

	    sDownjoy.openMemberCenterDialog(sActivity, new CallbackListener() {

		@Override
		public void onMemberCenterBack() 
		{
		    //Util.alert(sActivity, "onMemberCenterBack");

		    synchronized ( Moai.sAkuLock ) 
		    {
		    	AKUNotifyDownjoyMemberCenterBack ( );
		    }
		}

		@Override
		public void onMemberCenterError(DownjoyError error) 
		{
		    int errorCode=error.getMErrorCode();
		    String errorMsg=error.getMErrorMessage();

		    //Util.alert(sActivity, "onMemberCenterError:" + errorCode + "|" + errorMsg);
		    synchronized ( Moai.sAkuLock ) 
		    {
		    	AKUNotifyDownjoyMemberCenterError (errorCode,  errorMsg);
		    }
		}

		@Override
		public void onError(Error error) 
		{
		    //String errorMessage=error.getMessage();
		    //Util.alert(sActivity, "onError:" + errorMessage);
		    String errorMessage = error.getMessage();
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyMemberCenterErrorOther ( errorMessage);
		    }
		}
	    });
	}
	//----------------------------------------------------------------//
	public static void getInfo ( ) 
	{
	   if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }
	    
	    sDownjoy.getInfo(sActivity, new CallbackListener() 
	    {	
		@Override
		public void onInfoSuccess(Bundle bundle) 
		{
		    String memberId=bundle.getString(Downjoy.DJ_PREFIX_STR + "mid");
		    String username=bundle.getString(Downjoy.DJ_PREFIX_STR + "username");
		    String nickname=bundle.getString(Downjoy.DJ_PREFIX_STR + "nickname");
		    String gender=bundle.getString(Downjoy.DJ_PREFIX_STR + "gender");
		    int level=Integer.parseInt(bundle.getString(Downjoy.DJ_PREFIX_STR + "level"));
		    String avatarUrl=bundle.getString(Downjoy.DJ_PREFIX_STR + "avatarUrl");
		    //long createdDate=Long.parseLong(bundle.getString(Downjoy.DJ_PREFIX_STR + "createdDate"));
		    String createdDate = bundle.getString(Downjoy.DJ_PREFIX_STR + "createdDate");
		    //Util.alert(sActivity, "mid: " + memberId + "\n username: " + username + "\n nickname: " + nickname
			//+ "\n gender: " + gender + "\n level: " + level + "\n avatarUrl: " + avatarUrl + "\n createdDate: "
			//+ createdDate);
		    synchronized ( Moai.sAkuLock ) 
		    {
			  AKUNotifyDownjoyUserInfoSuccess( memberId, username, nickname, gender, level, avatarUrl, createdDate );
		    }
		}

		@Override
		public void onInfoError(DownjoyError error) 
		{
		    int errorCode=error.getMErrorCode();
		    String errorMsg=error.getMErrorMessage();

		    //Util.alert(sActivity, "onInfoError:" + errorCode + "|" + errorMsg);
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyUserInfoError( errorCode, errorMsg );
		    }
		}

		@Override
		public void onError(Error error) 
		{
		    String errorMessage=error.getMessage();
		    //Util.alert(sActivity, "onError:" + errorMessage);
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyUserInfoErrorOther( errorMessage );
		    }
		}
	    });
		
	}
	//----------------------------------------------------------------//
	public static void logout ( ) 
	{

	    if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }
	   
	    sDownjoy.logout(sActivity, new CallbackListener() 
		{

		@Override
		public void onLogoutSuccess() 
		{
		    //Util.alert(sActivity, "logout ok");
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyLogoutSuccess( );
		    }
		}

		@Override
		public void onLogoutError(DownjoyError error) 
		{
		    int errorCode=error.getMErrorCode();
		    String errorMsg=error.getMErrorMessage();

		    //Util.alert(sActivity, "onLogoutError:" + errorCode + "|" + errorMsg);
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyLogoutError( errorCode, errorMsg );
		    }
		}

		@Override
		public void onError(Error error) 
		{
		    //Util.alert(sActivity, "onError:" + error.getMessage());
		    String errorMessage = error.getMessage();
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyDownjoyLogoutErrorOther( errorMessage );
		    }
		}
	    });
	}
	//----------------------------------------------------------------//
	public static void openPaymentDialog ( float money, String productName, String extInfo) 
	{

	    if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }
	    try {
	    	
	    	String orderNo= sDownjoy.openPaymentDialog(sActivity, money, productName, extInfo, new CallbackListener() 
		    {

		    @Override
		    public void onPaymentSuccess(String orderNo) 
		    {
			//Util.alert(sActivity, "payment success! \n orderNo:" + orderNo);
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyDownjoyPaymentSuccess( orderNo );
			}
		    }

		    @Override
		    public void onPaymentError(DownjoyError error, String orderNo) 
		    {
			int errorCode=error.getMErrorCode();
			String errorMsg=error.getMErrorMessage();

			//Util.alert(sActivity, "onPaymentError:" + errorCode + "|" + errorMsg + "\n orderNo:" + orderNo);
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyDownjoyPaymentError( errorCode, errorMsg );
			}
		    }

		    @Override
		    public void onError(Error error) 
		    {
			//Util.alert(sActivity, "onError:" + error.getMessage());
			String errorMessage = error.getMessage();
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyDownjoyPaymentErrorOther( errorMessage );
			}
		    }
		});

	    } 
	    catch(Exception e) 
	    {
		e.printStackTrace();
		//Util.alert(sActivity, "onError:" + e.getMessage());
	    }	
	}

	
}