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
 
 
import cn.uc.gamesdk.UCGameSDK;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiUCExpand  extends  MoaiBaseSdk {
	 
	 
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
		sActivity = null;
		System.exit(0);
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    //onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
	 
		 return "OK";
	};

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		 
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
			JsonObject jsonData =parms.asObject();
            JSONObject jsonExData = new JSONObject();
            jsonExData.put("roleId", jsonData.get("RoleId").asInt());// 当前登录的玩家角色ID
            jsonExData.put("roleName", jsonData.get("RoleName").asString());// 当前登录的玩家角色名
            jsonExData.put("roleLevel", jsonData.get("Rolelv").asInt());// 当前登录的玩家角色等级
            jsonExData.put("zoneId", jsonData.get("serverIndex").asInt());// 当前登录的游戏区服ID
            jsonExData.put("zoneName", jsonData.get("serverName").asString());// 当前登录的游戏区服名称
            UCGameSDK.defaultSDK().submitExtendData("loginGameRole", jsonExData);
           
    } catch (Exception e) {
    	MoaiLog.e(" MoaiUCExpand  error "+e.getMessage());
            // 处理异常
    }
		return "";
	}
 
}

