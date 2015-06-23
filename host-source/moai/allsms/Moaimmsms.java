//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import mm.purchasesdk.Purchase; 
import mm.purchasesdk.PurchaseSkin;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log; 
import android.widget.Toast;
 
import com.chinaMobile.MobileAgent;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


import java.util.HashMap;

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaimmsms  extends  MoaiBaseSdk {
	 
	public static Purchase purchase=null;
	public  static IAPListener mListener;

	public static JsonValue orderParms;
	public static Moai.OnFinishHandler mmOnfinish;
		public static ProgressDialog mProgressDialog;
	//格式化GateWay链接
	public static JsonObject SDKFormatGateWay(String uid,JsonObject jsonData)
	{
		JsonObject jsonParms=new JsonObject();
		jsonParms.add("gatewayurl", sConfigJsonObject.get("gateWayUrl").asString());
		jsonParms.add("gatewaypath", sConfigJsonObject.get("gateWayPath").asString());	
		jsonParms.add("uid",uid);	
		jsonParms.add("data", jsonData);
		return jsonParms; 
	}

	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		
	}
	
	public void MMInit(String parms){
		mmsmsPayCodeConfig.setPayCodeConfig();
		if(purchase==null)
			purchase = Purchase.getInstance();
		IAPHandler iapHandler = new IAPHandler(MoaiBaseSdk.sActivity);
		mListener = new IAPListener(MoaiBaseSdk.sActivity, iapHandler);
		
		String appid=GetJsonVal(sConfigJsonObject,"appid","0");
		String appkey=GetJsonVal(sConfigJsonObject,"appkey","0");
		purchase.setAppInfo(appid, appkey,PurchaseSkin.SKIN_SYSTEM_ONE);  // 设置计费应用ID和Key (必须)
		try { 
			purchase.init(sActivity, mListener); //初始化，传入监听器
          

		} catch (Exception e) {
			MoaiLog.e("purchase.init error"+e.toString());
			//e.printStackTrace();
			return;
		}
		
	}
	
	
	public boolean onMPreRunning(Activity activity, final Moai.OnFinishHandler onfinish){
		mmOnfinish=onfinish;
		MMInit("");
		Moaimmsms.mmOnfinish.callback(true);
		return false;
	}
	
   private void showToast(String hints) {
       Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
   }

public void onMResume(){
	   MobileAgent.onResume(sActivity); 
	}
   
   public void onMPause(){		 
	   MobileAgent.onPause(sActivity); 
	} 


//退出游戏时
	public void onMDestroy(){
		
	 
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
	 
		 return "OK";
	};

    
	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			orderParms=parms;
			JsonObject _json = parms.asObject();     
            JsonObject  payinfoJson=_json.get("payInfo").asObject();
            JsonObject  userinfoJson=_json.get("userInfo").asObject();
            String OrderNo=GetJsonVal(payinfoJson,"orderno","000");
            //payinfoJson.get("orderno").asString();
            int price=GetJsonValInt(payinfoJson,"price",0);
           // payinfoJson.get("price").asInt();
            int total=GetJsonValInt(payinfoJson,"total",0);
            //payinfoJson.get("total").asInt(); 
            String number=GetJsonVal(payinfoJson,"number","0");
            String roleId=GetJsonVal(userinfoJson,"roleId","000");
            if(roleId.length()>16) roleId=roleId.substring(0, 16);
            //GetJsonVal(payinfoJson,"number","0"); 
           //cpparam="20131031101034ff";
            //OrderNo=OrderNo.substring(0,16);            
            int _total=total;         
            if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
            
            mmsmsPayCodeConfig bConfig=new mmsmsPayCodeConfig();
            bConfig.number=number;
            bConfig.money=total;
            bConfig=mmsmsPayCodeConfig.getPayCodeConfig(bConfig);
            if(checkNetworkAvailable(MoaiBaseSdk.sActivity))  
	        {  
      
	        purchase.order(MoaiBaseSdk.sActivity, bConfig.payCode,1,roleId,true, mListener);
	        //当前有可用网络  
	        }  
	        else   
	        {  
        	Toast.makeText(MoaiBaseSdk.sActivity, "请先连接网络！", Toast.LENGTH_SHORT).show();
            //当前无可用网络  
	        }  
          
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		  
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		  return "OK";	 
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		 
		return "";
	}


		/********************* 发送buy_m请求 *************************/
	/**
	 * @param url
	 *            传入的url,包括了查询参数
	 * @return 返回get后的数据
	 */
	public static String sendGet(String url) {
		String result = "";
		// String
		URL realURL = null;
		URLConnection conn = null;
		BufferedReader bufReader = null;
		String line = "";
		try {
			realURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("url 格式错误");
		}
		try {
			conn = realURL.openConnection();
			// 设置连接参数...conn.setRequestProperty("xx", "xx");
			conn.setConnectTimeout(10000); // 10s timeout
			// conn.setRequestProperty("accept", "*/*");
			// conn.setRequestProperty("", "");
			conn.connect(); // 连接就把参数送出去了 get方法
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接错误");
		}
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("读取数据错误");
		} finally {
			// 释放资源
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/********************* 发送buy_m请求 *************************/
 
	
	/**
	 * 将字符串转成MD5值
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString().substring(8, 24);
	}
	
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}

		return false;

	}
	    
	
	public static void showProgress(String tips)
	{
		mProgressDialog = new ProgressDialog(MoaiBaseSdk.sActivity);
		mProgressDialog.setMessage(tips);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	public static void hideProgress()
	{ 
		if(mProgressDialog != null)
		{
			mProgressDialog.cancel();
			mProgressDialog = null;
		} 
	}
	

 
}

