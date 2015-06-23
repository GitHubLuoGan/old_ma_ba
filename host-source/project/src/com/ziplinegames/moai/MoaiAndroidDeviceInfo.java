package com.ziplinegames.moai;


import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class MoaiAndroidDeviceInfo {
	static Activity sActivity = null;
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
	}
	public static void getIMEI(final int getImei){
		
		String imei = "";
		try {
			TelephonyManager tm = (TelephonyManager) sActivity.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			if(imei == null)
			{
	        	imei = android.provider.Settings.Secure.getString(sActivity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);        
	        }
		} catch (Exception e) {
			imei = "000000";
        }  
		
		MoaiLuaBridge.callLuaFunctionWithString(getImei, imei);
	}
	public static void getIMSI(final int getImsi){
		String imsi = "";
		try {
			TelephonyManager tm = (TelephonyManager) sActivity.getSystemService(Context.TELEPHONY_SERVICE);
			
			imsi = tm.getSubscriberId();
			
		    if (imsi==null || "".equals(imsi)) 
		    	imsi = tm.getSimOperator();  
		    Class<?>[] resources = new Class<?>[] {int.class};  
		    Integer resourcesId = new Integer(1);  
		    if (imsi==null || "".equals(imsi)) {  
			    try {   //利用反射获取    MTK手机  
			    	Method addMethod = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);  
			    	addMethod.setAccessible(true);  
			    	imsi = (String) addMethod.invoke(tm, resourcesId);  
			    } catch (Exception e) {  
			         imsi = null;  
			    }  
		    }  
		    if (imsi==null || "".equals(imsi)) {  
		       try {   //利用反射获取    展讯手机  
	               Class<?> c = Class  
	                        .forName("com.android.internal.telephony.PhoneFactory");  
	               Method m = c.getMethod("getServiceName", String.class, int.class);  
	                String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);  
	                TelephonyManager tm1 = (TelephonyManager) sActivity.getSystemService(spreadTmService);  
	                imsi = tm1.getSubscriberId();  
	            } catch (Exception e) {  
	                imsi = null;  
	            }  
	       }  
		    if (imsi==null || "".equals(imsi)) {  
		        try {   //利用反射获取    高通手机  
		           Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", resources);  
		           addMethod2.setAccessible(true);  
		            imsi = (String) addMethod2.invoke(tm, resourcesId);  
		        } catch (Exception e) {  
		            imsi = null;  
		        }  
		    }  
	        if (imsi==null || "".equals(imsi)) {  
	            imsi = "000000";  
	        }  
        } catch (Exception e) {  
        	imsi = "000000";  
        }  
        
        MoaiLuaBridge.callLuaFunctionWithString(getImsi, imsi);
	}
}
