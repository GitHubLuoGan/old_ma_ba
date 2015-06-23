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
import android.content.ContextWrapper;
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.*;

import com.mappn.sdk.common.utils.BaseUtils;
import com.mappn.sdk.common.utils.ToastUtil;
import com.mappn.sdk.pay.GfanChargeCallback;
import com.mappn.sdk.pay.GfanConfirmOrderCallback;
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayCallback;
import com.mappn.sdk.pay.model.Order;
import com.mappn.sdk.statitistics.GfanPayAgent;
import com.mappn.sdk.uc.GfanUCCallback;
import com.mappn.sdk.uc.GfanUCenter;
import com.mappn.sdk.uc.User;
import com.mappn.sdk.uc.util.StringUtil;
import com.mappn.sdk.uc.util.UserUtil;
 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaijifeng  extends  MoaiBaseSdk {
	 public static String Uid = "";
	 public static String  OrderNo = "";
	 
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
		GfanPay.getInstance(sActivity.getApplication()).init();
		
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
		GfanUCenter.login(sActivity, new GfanUCCallback() {

			@Override
			public void onSuccess(User user, int loginType) {
				// TODO 登录成功处理
				String username = user.getUserName();
				long uid = user.getUid();
				Uid = String.valueOf(uid);
				String token = user.getToken();
				
				JsonObject jsonData=new JsonObject();
				 jsonData.add("username",username);
				 jsonData.add("uid",Uid);
		        jsonData.add("token",token); 
		        
 
		      //jsonData.get(name)
		        JsonObject jsonParms=SDKFormatGateWay(Uid,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
				
			}

			@Override
			public void onError(int loginType) {
				JsonRpcCall(Lua_Cmd_LoginFailed,"fail");
			}
		});
		 return "OK";
	};
	//send uid
	
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
	
	
	
	
	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			OrderNo=payinfoJson.get("orderno").asString();
			int price=payinfoJson.get("price").asInt();
			int total=payinfoJson.get("total").asInt();
			String info=payinfoJson.get("info").asString(); 
			
			Order order = new Order(info,info,price*10, OrderNo);
			
			//
//			String send_url = sConfigJsonObject.get("gateWayUrl").asString() + sConfigJsonObject.get("sendUid").asString();
//			String body = "?uid=" + Uid + "&orderno="+ OrderNo; 
//			String senduid_url = send_url + body;
//			sendGet(senduid_url);
			GfanPay.getInstance(sActivity.getApplicationContext()).pay(order,new GfanPayCallback() {
			@Override
			public void onSuccess(User user, Order order) {
			// TODO消费成功处理

				
				sBaseSDK.JsonRpcCall(Lua_Cmd_PaySuccess,"");
			}
			@Override
			public void onError(User user) {
			// TODO消费失败处理
				sBaseSDK.JsonRpcCall(Lua_Cmd_PayError,"");
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
		GfanUCenter.logout(sActivity);
		 return "OK";	 
	}
	//切换账号
	public String AuthQuit(JsonValue parms){
		GfanUCenter.logout(sActivity);
		 return "OK";	 
	}
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		return "";
	}catch (Exception e) {
        Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
        
    }
   return "OK";
	} 
}

