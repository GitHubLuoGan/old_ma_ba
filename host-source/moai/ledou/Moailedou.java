//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.ultralisk.gameapp.MoaiActivity;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



import com.skynet.android.Skynet;
import com.skynet.android.SkynetSettings;






 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moailedou extends  MoaiBaseSdk {
	 
	public static String appSecret = "";
	public static String appKey = "";
	public static String paynotifyUrl = "";
	public static String uid = "";
	public static String gameId = "";
	public static String serverId = "";
	 
	 
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
		 
		 appSecret	 =  GetJsonVal(sConfigJsonObject,"appSecret","000");
	     appKey  =  GetJsonVal(sConfigJsonObject,"appKey","000");
	     
	     Skynet.initialize(sActivity, new SkynetSettings(appKey,appSecret));
	     
	     Skynet.setCharegeOnce(true);

		

	}
	
	public void onMResume(){
		
		super.onResume();            
		Skynet.setCurrentActivity(sActivity);
	}

//退出游戏时
	public void onMDestroy(){
		
		Skynet.exit(sActivity);

	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		Skynet.showLoginView(sActivity, "ledouLogin",
				new Skynet.LoginListener() {

					@Override
					public void onUserLoggedIn(Bundle bundle) {
					
						//透传参数
						String extraInfo = bundle.getString(Skynet.LoginListener.EXTRAS_EXTRA_INFO);
						//游戏id
						gameId = bundle.getString(Skynet.LoginListener.EXTRAS_GAME_ID);	
						//唯一标识
						uid= bundle.getString(Skynet.LoginListener.EXTRAS_OPEN_ID);
						//会话id
						String sessionId = bundle.getString(Skynet.LoginListener.EXTRAS_SESSION_ID);
						
						JsonObject jsonData=new JsonObject();
		                jsonData.add("extraInfo", extraInfo);
		                jsonData.add("gameId",gameId);
		                jsonData.add("openId",uid);
		                jsonData.add("sessionId",sessionId);
		                
		                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
		                jsonParms.add("data", jsonData);
		                 
		                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
			
					}

					@Override
					public void onLoginCanceled() {

					}
				});

		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();
			
			serverId = userinfoJson.get("serverIndex").asString();
			String OrderNo=payinfoJson.get("orderno").asString();
			float price=Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			
			
			Skynet.showChargePage(price, productName, OrderNo, serverId, new Skynet.ChargeCallback() {

						@Override
						public void onOrderCreated(String extraInfo,String serverId, String orderId) {
							
							
						}

						@Override
						public void onChargePageFinished() {
							
							
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
	 

	 public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}

	///SDK切换账号 
	public static String V2_authQuit(JsonValue parms){

		Skynet.switchAccount(sActivity, "testExtraInfo",
				new Skynet.LoginListener() {
			
			@Override
			public void onUserLoggedIn(Bundle bundle) {

				String extraInfo = bundle.getString(Skynet.LoginListener.EXTRAS_EXTRA_INFO);
		
				String game_id = bundle.getString(Skynet.LoginListener.EXTRAS_GAME_ID);	
				//唯一标识
				String openId = bundle.getString(Skynet.LoginListener.EXTRAS_OPEN_ID);
				//会话id
				String sessionId = bundle.getString(Skynet.LoginListener.EXTRAS_SESSION_ID);
				
				if(!openId.equals("")){
				
				uid = openId;
				JsonObject jsonData=new JsonObject();
                jsonData.add("extraInfo", extraInfo);
                jsonData.add("gameId",game_id);
                jsonData.add("openId",openId);
                jsonData.add("sessionId",sessionId);
                
                JsonObject jsonParms=SDKFormatGateWay(openId,jsonData);
                jsonParms.add("data", jsonData);
                 
                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
				}

			}
			
			@Override
			public void onLoginCanceled() {
	;
			}
		});
		
		 return "OK";	
	}
	
	
	///退出游戏
	public String ExitGame(JsonValue parms){
	 
		 return "OK";	 
	}
 
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
	
		JsonObject jsonData=parms.asObject(); 
		
		serverId = jsonData.get("serverIndex").asString();

		return "";
	}
	
 
}
