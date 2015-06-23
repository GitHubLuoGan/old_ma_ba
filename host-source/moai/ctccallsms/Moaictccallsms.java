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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;
import cn.play.dserv.CheckTool;
import cn.play.dserv.ExitCallBack;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

//////////

import android.content.Intent;
import android.content.pm.PackageManager; 
import android.content.pm.ApplicationInfo;

/////////
 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaictccallsms  extends  MoaiBaseSdk {
	 

	public static JsonValue orderParms;
	public static Moai.OnFinishHandler mmOnfinish;
	public static ProgressDialog mProgressDialog;
	public static final String egameAppPackageName = "com.egame";
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
		payCodeConfig.setPayCodeConfig(); 		
		String appid=GetJsonVal(sConfigJsonObject,"appid","0");
		String appkey=GetJsonVal(sConfigJsonObject,"appkey","0");
		EgamePay.init(MoaiBaseSdk.sActivity);		
	}
	
	 
	
   private void showToast(String hints) {
       Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
   }

//退出游戏时
	public void onMDestroy(){
		
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){
 	
 	CheckTool.exit(activity, new ExitCallBack() { //activity为主Activity

		@Override
		public void exit() {
			//退出游戏操作
		  onfinish.callback(true);//退出游戏的代码 
		}

		@Override
		public void cancel() {
			//取消退出，返回游戏
			onfinish.callback(false);	 //继续游戏的代码  
		}
	});
	   // onfinish.callback(false);	 //继续游戏的代码  
	   // onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  static String V2_login(JsonValue parms){ 
		//登陆
		//V2_OpenMoreGame(parms);
		 
		 return "OK";
	};
 
 
/***************判定是否更多游戏**************************/
public void ResultChannelInfo(){
         
        
        JsonObject channelInfo=new JsonObject();
        channelInfo.add("isMusicEnabled", true);
        channelInfo.add("isMoreGameEnabled", true);
        channelInfo.add("isAboutEnabled", true); 
        channelInfo.add("isConfirmExitEnabled", true);  
        String lastAboutStr=GetJsonVal(sConfigJsonObject,"lastAboutStr","0");
        if(!lastAboutStr.equals("0")){
        	 channelInfo.add("lastAboutStr", lastAboutStr);  
        }
           
        JsonRpcCall(Lua_Cmd_ResultChannelInfo,channelInfo);  
    }
	
/*****************************MoreGame*******************************/	
	public static String V2_openMoreGame(JsonValue parms){
	//CheckTool.more(MoaiBaseSdk.sActivity);//用不了
		try{
			Intent intent = MoaiBaseSdk.sActivity.getPackageManager().getLaunchIntentForPackage(
					egameAppPackageName);
			MoaiBaseSdk.sActivity.startActivity(intent);
		}catch(Exception ex){
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setData(Uri.parse("http://play.cn/"));
			MoaiBaseSdk.sActivity.startActivity(intent);
		}
		return "true";
	}


	private boolean IsAppExist(String appPackageName) {
		if (appPackageName == null || appPackageName.equals(""))
			return false;
		try {
			ApplicationInfo appInfo = MoaiBaseSdk.sActivity.getPackageManager().getApplicationInfo(
					appPackageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}



		
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
            int _total=total;         
            if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
            
            payCodeConfig bConfig=new payCodeConfig();
            bConfig.number=number;
            bConfig.money=total;
            bConfig=payCodeConfig.getPayCodeConfig(bConfig);
            
            
            HashMap<String, String> payParams=new HashMap<String, String>();
			payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_ALIAS, bConfig.payCode);
			payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_DESC,"");
			ctccPay(payParams);
			
            /*if(checkNetworkAvailable(MoaiBaseSdk.sActivity))  
	        { 
            
	        //当前有可用网络  
	        }  
	        else   
	        {  
        	Toast.makeText(MoaiBaseSdk.sActivity, "请先连接网络！", Toast.LENGTH_SHORT).show();
           //当前无可用网络  
	        }  */
          
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";
	}

	

	private static void ctccPay(HashMap<String, String> payParams){
		final Builder dialog=new Builder(MoaiBaseSdk.sActivity);
		dialog.setTitle("中国电信支付");
		
		EgamePay.pay(MoaiBaseSdk.sActivity, payParams,new EgamePayListener() {
			@Override
			public void paySuccess(Map<String, String> params) {
				//dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_DESC)+"支付成功");
				//dialog.show();
				JsonObject jsonParms=new JsonObject();
				jsonParms.add("code", 1);
				jsonParms.add("msg", "支付成功");
				jsonParms.add("payData",Moaictccallsms.orderParms.asObject());				 
				//计费成功
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
				
				
			}
			
			 	
			@Override
			public void payFailed(Map<String, String> params, int errorInt) {
				//dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_DESC)+"支付失败：错误代码："+errorInt);
				//dialog.show();
				String msg="支付失败：错误代码："+errorInt;
				if(errorInt==-100) msg="Activity对象为空";
				if(errorInt==-101) msg="计费文件未找到或者数据读取异常";
				if(errorInt==-102) msg="无法读取当前应用信息";
				if(errorInt==-103) msg="应用校验失败，申报信息和当前产品不一致";
				if(errorInt==-104) msg="非电信用户";
				if(errorInt==-200) msg="初始化失败，无法进行计费";
				if(errorInt==-201) msg="计费回调对象为空";
				if(errorInt==-202) msg="计费道具别名错误";
				if(errorInt==-203) msg="渠道ID数据异常";
				if(errorInt==-204) msg="SERVICE_CODE 数据异常";
				if(errorInt==-205) msg="自定义参数格式异常";
				if(errorInt==-206) msg="计费方法调用过快";
				if(errorInt==-207) msg="计费短信发送失败"; 
					
				JsonObject jsonParms=new JsonObject();
				jsonParms.add("code", errorInt);
				jsonParms.add("msg", msg);
				jsonParms.add("payData",Moaictccallsms.orderParms.asObject());				 
				//计费成功
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
			}
			
			
			@Override
			public void payCancel(Map<String, String> params) {
				//dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_DESC)+"支付已取消");
				//dialog.show();
				JsonObject jsonParms=new JsonObject();
				jsonParms.add("code", 0);
				jsonParms.add("msg", "支付已取消");
				jsonParms.add("payData",Moaictccallsms.orderParms.asObject());				 
				//计费成功
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
			}
		});
	}
	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		  
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		  return "OK";	 
	}
	
//是否存在
	
public static String V2_Exist(JsonValue parms){
	
	String data = parms.asString();
	
	
	if(Java_Cmd_OpenMoreGame.equals(data) || Java_Cmd_OpenAbout.equals(data)){
		
		return "1";
	}
	else
	    return "0";
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

