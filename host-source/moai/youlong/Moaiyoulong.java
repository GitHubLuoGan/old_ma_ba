//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.ultralisk.gameapp.MoaiActivity;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.yx9158.external.ChangeActivity;
import com.yx9158.external.LoginIDispatcherCallback;
import com.yx9158.external.Payment;
import com.yx9158.external.RegisterIDispatcherCallback;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyoulong extends  MoaiBaseSdk {
	 
	public static String uid = "";
	 
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

//退出游戏时
	public void onMDestroy(){
		
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	
	//退出游戏
	public static String V2_exitGame(JsonValue parms){
		
		JsonRpcCall(Lua_Cmd_GameExit,"");
		
		return "";
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		
		ChangeActivity.getInstance().toLogin(sActivity,new LoginIDispatcherCallback(){

			@Override
			public void onFinished(Context context, Intent intent) {
				 uid=intent.getStringExtra("用户名");
				
				JsonObject jsonData=new JsonObject();
		         jsonData.add("uid", uid);
		         
		         
		         JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
		         jsonParms.add("data", jsonData);
		         
		         JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
				
			}},new RegisterIDispatcherCallback(){

				@Override
				public void onFinished(Context context, Intent intent) {
					 uid=intent.getStringExtra("用户名");
					
					JsonObject jsonData=new JsonObject();
			         jsonData.add("uid", uid);
			         
			         
			         JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
			         jsonParms.add("data", jsonData);
			         
			         JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);

				}});
		
		 return "OK";
	};

	///打开支付界面
	public static  String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userInfo=_json.get("userInfo").asObject();
			
			String serverId = userInfo.get("serverIndex").asString();
			String OrderNo  =  payinfoJson.get("orderno").asString();
			String price    = payinfoJson.get("price").asString() + ".00";
			String roleName = userInfo.get("roleName").asString();
			double priceDouble = Double.parseDouble(price);
			
			String url = Payment.getInstance().toRecharge( uid, serverId, roleName, OrderNo, price, OrderNo, sActivity);
			
			Intent intent = new Intent(sActivity, com.ziplinegames.moai.YXWebActivity.class); 

       	    intent.putExtra("url",url);

       	    sActivity.startActivity(intent);
			
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		  // mAnzhiCenter.getInstance().viewUserInfo(super.sActivity); 
	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		//mAnzhiCenter.getInstance().logout(super.sActivity);		 
		 return "OK";	 
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		
		JsonObject jsonData=parms.asObject(); 
		return "";
	}
	
 
}

