//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.lenovo.lsf.gamesdk.LenovoGameApi;
import com.lenovo.lsf.gamesdk.LenovoGameApi.GamePayRequest;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IPayResult;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moailenovo  extends  MoaiBaseSdk {
	 
	public static String appId = sConfigJsonObject.get("appId").asString();
	public static String appKey = sConfigJsonObject.get("appKey").asString();
	 
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
		 
		LenovoGameApi.doInit(sActivity,appId) ;
		
	}
	
	
	/********************* get uid请求 *************************/
	
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
			conn.setConnectTimeout(10000);
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("连接错误");
		}
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "gb2312"));
			while ((line = bufReader.readLine()) != null) {
				result += line + "\n";
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

	/********************* get uid请求 *************************/
	

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
		
		LenovoGameApi.doAutoLogin(sActivity, new LenovoGameApi.IAuthResult() { 
		      @Override 
	             //登录回调，处理登录结果 
	      public void onFinished(boolean ret, String data) { 
	        if (ret) { 
	          //登录成功 
	        	
	        	

		        /*******************获取UID***********************/
		        String uidUrl = sConfigJsonObject.get("gateWayUrl").asString() + sConfigJsonObject.get("getUidPath").asString() + "?lpsust=" + data;
		        String  Uid = sendGet(uidUrl).toString();
		        int Len = Uid.length();
		        Uid = Uid.substring(1, Len-2);
		        /*******************获取UID***********************/
		        
		        JsonObject jsonData=new JsonObject();
		        jsonData.add("lpsust", data);
		        jsonData.add("uid",  Uid); 
		        
		        
		        JsonObject jsonParms=SDKFormatGateWay(Uid,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
	        	
	        	
	        } else { 
	          // 登录失败(失败原因开启飞行模式、 网络不通等) 
	        	
	        	JsonRpcCall(Lua_Cmd_LoginCancel,"");
	        } 
	      } 
	    }); 
		
		 return "OK";
	};

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=payinfoJson.get("price").asInt();
			int total=payinfoJson.get("total").asInt();
			String info=payinfoJson.get("info").asString();
			String waresid = sConfigJsonObject.get(info).asString();
					
			GamePayRequest payRequest = new GamePayRequest();
			 payRequest.addParam("notifyurl", sConfigJsonObject.get("paynotifyUrl").asString()); 
	         payRequest.addParam("appid", sConfigJsonObject.get("appId").asString()); 
	         payRequest.addParam("waresid", waresid); 
	         payRequest.addParam("exorderno", OrderNo); 
	         payRequest.addParam("price", price); 
	         payRequest.addParam("cpprivateinfo", OrderNo); 
	         
	        
	         
	         
	         LenovoGameApi.doPay(sActivity,appKey, payRequest, new IPayResult() {
	 			public void onPayResult(int resultCode, String signValue,String resultInfo) {

	 				if (LenovoGameApi.PAY_SUCCESS == resultCode) {
	 					if (null == signValue) {
	 						JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
	 					}
	 					boolean flag = GamePayRequest.isLegalSign(signValue,
	 							appKey);
	 					if (flag) {
	 						
	 						JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功");
	 		
	 					} else {

	 						JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
	 					}
	 				} else if (LenovoGameApi.PAY_CANCEL == resultCode) {
	 					
	 					JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");	
	 					
	 				} else {
	 					
	 					JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
	 				}
	 			}
	 		});
			
						
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
		try {
		JsonObject jsonData=parms.asObject(); 
		
		
		}
		catch (Exception e) {
			MoaiLog.e("sdkJavaName is ::::"+sdkJavaName); 
		}
		return "";
	}
}

