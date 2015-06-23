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
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.ultralisk.gameapp.MarioPluginApplication;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnCheckLoginCompletedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLoginFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnUserInfoSettingFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.LoginFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.LogoutFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.UnverifiedPlayer;
import com.wandoujia.mariosdk.plugin.api.model.model.WandouPlayer;
import com.wandoujia.mariosdk.plugin.api.model.model.result.UserInfoSettingResult;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wandoujia.mariosdk.plugin.api.model.callback.OnMessageReceivedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.MessageEntity;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiwandoujia  extends  MoaiBaseSdk {
	 
	private static WandouGamesApi wandouGamesApi;
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
		wandouGamesApi = MarioPluginApplication.getWandouGamesApi();
	    wandouGamesApi.init(sActivity);
		
	}
	public void onMPause(){
		 
		super.onPause();
	    wandouGamesApi.onPause(sActivity);
	 }        
	public void onMResume(){
		
		super.onResume();
	    wandouGamesApi.onResume(sActivity);
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
		 boolean wdjIsLogin = wandouGamesApi.isLoginned();
		 
		 wandouGamesApi.isLoginned(new OnCheckLoginCompletedListener() {
		      @Override
		      public void onCheckCompleted(boolean isLogin) {
		        boolean wdjIsLogin = isLogin;
		      }
		    });
		 

		 wandouGamesApi.login(new OnLoginFinishedListener() {
		      @Override
		      public void onLoginFinished(LoginFinishType loginFinishType, UnverifiedPlayer unverifiedPlayer) {
		        String Id = unverifiedPlayer.getId();	
		        String token = unverifiedPlayer.getToken();
		        //获取帐号信息
		        WandouPlayer wandouPlayer = wandouGamesApi.getCurrentPlayerInfo();
		        
		        
		        JsonObject jsonData=new JsonObject();
		        jsonData.add("token",token); 
		        jsonData.add("Id",Id); 
 
		      //jsonData.get(name)
		        JsonObject jsonParms=SDKFormatGateWay(Id,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
		      }
		    });
		 
		 return "OK";
	};

	private void getCurrentPlayerInfo() {
		// TODO Auto-generated method stub
		
	}

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
			
			wandouGamesApi.pay(sActivity, info, price*100, OrderNo,
					new OnPayFinishedListener() {
                @Override
                public void onPaySuccess(PayResult payResult) {
                	sBaseSDK.JsonRpcCall(Lua_Cmd_PaySuccess,"");
                }

                @Override
                public void onPayFail(PayResult payResult) {
                	sBaseSDK.JsonRpcCall(Lua_Cmd_PayError,"");
                }
              });
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	//账号切换
	public String AuthQuit(JsonValue parms){
		 wandouGamesApi.logout(new OnLogoutFinishedListener() {
		      @Override
		      public void onLoginFinished(LogoutFinishType logoutFinishType) {
		    	  sBaseSDK.JsonRpcCall(Lua_Cmd_LoginOut,"");
		      }
		    });
		return "";
	}
	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){

	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		wandouGamesApi.logout(new OnLogoutFinishedListener() {
		      @Override
		      public void onLoginFinished(LogoutFinishType logoutFinishType) {
		    	  JsonRpcCall(Lua_Cmd_LoginOut,"");
		      }
		    });
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

