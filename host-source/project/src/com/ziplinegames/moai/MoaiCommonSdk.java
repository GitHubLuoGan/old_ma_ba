//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import java.util.ArrayList;

import android.app.Activity;
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiCommonSdk {
	
	static Activity sActivity = null;
	static String sChannelName 		= null;
	static int sOnLoginCommon 		= -1;
	static int sOnLoginSdkSuccess 	= -1;
	static int sOnLoginSdkFailed 	= -1;
	
	private static ArrayList < Class < ? >>	sAvailableClasses = new ArrayList < Class < ? >> ();
	
	private static String [] sExternalClasses = {
		"com.ziplinegames.moai.MoaiWdj"
	};
	static {
		
		for ( String className : sExternalClasses )
		{
			Class < ? > theClass = Moai.findClass ( className );
			if ( theClass != null ) {
				
				sAvailableClasses.add ( theClass );
			}
		}
	}
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
		for ( Class < ? > theClass : sAvailableClasses ) {
			Moai.executeMethod ( theClass, null, "onCreate", new Class < ? > [] { Activity.class }, new Object [] { activity });
		}	
		
	}
	
	public static void checkSdkLoginExist(String channelName, int callback)
	{
		if(channelName.equalsIgnoreCase("wdj"))
		{
			MoaiLuaBridge.callLuaFunctionWithString(callback, "true");
		}else{
			MoaiLuaBridge.callLuaFunctionWithString(callback, "false");
		}
	}
	public static void checkSdkChargeExist(String channelName, int callback)
	{
		if(channelName.equalsIgnoreCase("wdj"))
		{
			MoaiLuaBridge.callLuaFunctionWithString(callback, "true");
		}else{
			MoaiLuaBridge.callLuaFunctionWithString(callback, "false");
		}
	}
	
	//channelName:   从assets/init.lua里填的_G.CHANNEL_NAME
	//onSetUrl:      设置登录url
	//onLoginCommon: 普通登录的回调
	//onLoginSdkSuccess: sdk登录成功的回调
	//onLoginSdkFailed:  sdk登录失败的回调
	public static void login(String channelName, final int onSetUrl, final int onLoginCommon, final int onLoginSdkSuccess, final int onLoginSdkFailed)
	{
		sChannelName = channelName;
		sOnLoginCommon 		= onLoginCommon;
		sOnLoginSdkSuccess	= onLoginSdkSuccess;
		sOnLoginSdkFailed 	= onLoginSdkFailed;
		
		if(channelName.equalsIgnoreCase("wdj")) //直接登录
		{

			for ( Class < ? > theClass : sAvailableClasses ) {
				Moai.executeMethod ( theClass, null, "onSetUrl", new Class < ? > [] { java.lang.Integer.TYPE}, new Object [] { new Integer ( onSetUrl ) });
			}	
			for ( Class < ? > theClass : sAvailableClasses ) {
				Moai.executeMethod ( theClass, null, "onLoginWdj", new Class < ? > [] { }, new Object [] { });
			}	
		}
	}
	public static void onResume(){
		if (sChannelName.equalsIgnoreCase("wdj")){
			for ( Class < ? > theClass : sAvailableClasses ) {

				Moai.executeMethod ( theClass, null, "onResume", new Class < ? > [] { }, new Object [] { });
			}	
		}
	}
	public static void onLogout(final int callback){
		
		if (sChannelName.equalsIgnoreCase("wdj")){
			for ( Class < ? > theClass : sAvailableClasses ) {
				Moai.executeMethod ( theClass, null, "onLogout", new Class < ? > [] { java.lang.Integer.TYPE }, new Object [] { new Integer (callback )});
			}	
		}
	}
	
	//设置支付服务器地址
	public static void getSdkChargeUrl(String channelName, final int onUrl)
	{
//		if (channelName.equalsIgnoreCase("wdj")){
			for ( Class < ? > theClass : sAvailableClasses ) {
				Moai.executeMethod ( theClass, null, "getSdkChargeUrl", new Class < ? > [] { java.lang.Integer.TYPE}, new Object [] { new Integer ( onUrl ) });
			}	
//		}
	}
//	price:   价格
//	orderID: 服务器生成的订单号
//	desc:    描述，如：“1000元宝，送200元宝”
//	userID：   userID
//	roleName: 角色名
//	mfOnJavaPaymentSuccess：支付成功的回调
//	mfOnJavaPaymentError： 支付失败的回调
	public static void charge(float price, String orderID, String desc, String userID, String roleName, final int onPaymentSuccess, final int onPaymentFailed)
	{
		if(sChannelName.equalsIgnoreCase("wdj"))
		{
			//支付开始
//			MoaiWdj.onCharge( price, desc, orderID, onPaymentSuccess, onPaymentFailed );
			for ( Class < ? > theClass : sAvailableClasses ) {
				Moai.executeMethod ( theClass, null, "onCharge", new Class < ? > [] { 
						java.lang.Float.TYPE, java.lang.String.class, java.lang.String.class,java.lang.Integer.TYPE,java.lang.Integer.TYPE}, 
				new Object [] { 
						new Float ( price ),desc,orderID, new Integer (onPaymentSuccess), new Integer (onPaymentFailed) });
			}	
			
		}
	}
}

