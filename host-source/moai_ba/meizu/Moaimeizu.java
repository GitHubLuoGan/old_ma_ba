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
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.fgwansdk.FGwan;
import com.fivegwan.multisdk.api.ResultListener;

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaimeizu  extends  MoaiBaseSdk {
	 private static String appid = "";
	 private static String appkey = "";
	 private static FGwan fg;
	 
	 private static String server_id = "";
	 
	 
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
	public   void SDKInit(String parms){
		appid = sConfigJsonObject.get("appId").asString();
		appkey = sConfigJsonObject.get("appKey").asString();
		fg = new FGwan(MoaiBaseSdk.sActivity,appid,appkey);
		fg.setSwitchAccountListener(new ResultListener() {
			
			@Override
			public void onSuccess(Bundle bundle) { 
				// TODO Auto-generated method stub 
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				String userid=bundle.getString("userid");      
		        String token=bundle.getString("token");
		        String username=bundle.getString("username");
		        
		        JsonObject jsonData=new JsonObject();
		        jsonData.add("username", username); 
		        jsonData.add("token",token); 
		        jsonData.add("userid",userid); 
		      //jsonData.get(name)
		        JsonObject jsonParms=SDKFormatGateWay(username,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms); 
			}
			
			@Override
			public void onFailture(int code, String msg) {
				// TODO Auto-generated method stub  
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
			}
		});
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
		fg.login(new ResultListener() {
			
			@Override
			public void onSuccess(Bundle bundle) { 
				// TODO Auto-generated method stub  
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
				String userid=bundle.getString("userid");      
		        String token=bundle.getString("token");
		        String username=bundle.getString("username");
		        
		        JsonObject jsonData=new JsonObject();
		        jsonData.add("username", username); 
		        jsonData.add("token",token); 
		        jsonData.add("userid",userid); 
		      //jsonData.get(name)
		        JsonObject jsonParms=SDKFormatGateWay(username,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms); 
			}
			
			@Override
			public void onFailture(int code, String msg) {
				// TODO Auto-generated method stub  
				 
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
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
			
			//魅族支付界面
			fg.pay(info,price,OrderNo,server_id, new ResultListener() {
				@Override
				public void onSuccess(Bundle bundle) {
					// TODO Auto-generated method stub
					//充值结果，直接在充值页面体现，客户端不做提示
					JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功");
				}
				   
				@Override
				public void onFailture(int code, String msg) {
					// TODO Auto-generated method stub
					JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
				}
			});
			//魅族支付完
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
	 
	
	public String AuthQuit(JsonValue parms){
		
		fg.logout(MoaiBaseSdk.sActivity,new ResultListener() {
			
			@Override
			public void onSuccess(Bundle bundle) {
				// TODO Auto-generated method stub
				
				//魅族退出
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				
			}
			
			@Override
			public void onFailture(int code, String msg) {
				// TODO Auto-generated method stub
				
			}
		});	 
		return "";
	}
	
	
	///退出游戏
	public String ExitGame(JsonValue parms){
		fg.logout(MoaiBaseSdk.sActivity,new ResultListener() {
			
			@Override
			public void onSuccess(Bundle bundle) {
				// TODO Auto-generated method stub
				
				//魅族退出
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				
			}
			
			@Override
			public void onFailture(int code, String msg) {
				// TODO Auto-generated method stub
				
			}
		});	 
		 return "OK";	 
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		String serverId = jsonData.get("serverIndex").toString();
		String ServerName  = jsonData.get("serverName").toString();
		String roleId = jsonData.get("RoleId").toString();
		server_id = serverId;
		String roleName = jsonData.get("RoleName").asString();
		String roleLevel = jsonData.get("Rolelv").toString();
		
		fg.submitRoleInfo(serverId, ServerName, roleId, roleName, roleLevel);
		
		}catch (Exception e) {  
           
        }
		return "";
	}
}

