//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.Serializable;
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
import com.cloudpoint.sdk.common.*;
import com.cloudpoint.sdk.pojo.UserInfo;





 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyouyou extends  MoaiBaseSdk {
	 
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
	     
	     
	     doGameNotify(sActivity,"1","","启动","启动",gameNotifyCallback);
	     

		

	}
	
	public void onMResume(){
		

	}

//退出游戏时
	public void onMDestroy(){
		
		doGameNotify(sActivity,"2","","退出","退出",gameNotifyCallback);

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
		

		Matrix.login(sActivity, loginCallback);	

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
			int price=Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			
			
			Matrix.buy(sActivity, price, OrderNo, OrderNo,
					new IDispatcherCallback() {
						@Override
						public void doFinished(int errorCode,
								Serializable resultObj) {
							switch (errorCode) {
							case Contants.SUCCESS:
								// 成功
								JsonRpcCall(Lua_Cmd_PaySuccess,"");
								break;
							default:
								// 失败
								JsonRpcCall(Lua_Cmd_PayError,"");
								break;
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
	 

	 public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}

	///SDK切换账号 
	public static String V2_authQuit(JsonValue parms){

		Matrix.logout(sActivity, new IDispatcherCallback() {
			@Override
			public void doFinished(int errorCode, Serializable resultObj) {
				switch (errorCode) {
				case Contants.SUCCESS:
					// 成功
					JsonRpcCall(Lua_Cmd_LoginOut,"");
					break;
				default:
					// 失败
					JsonRpcCall(Lua_Cmd_LoginOut,"");
					break;
				}
			}
		});
		 return "OK";	
	}
	
	//角色升级
	public static String V2_levelUp(JsonValue parms){
		
		String level = parms.asString();
		 
		doGameNotify(sActivity,"3",level,"","",gameNotifyCallback);
		
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
	
	//友游登录回调
	private static IDispatcherCallback loginCallback = new IDispatcherCallback() {
		@Override
		public void doFinished(int errorCode, Serializable resultObj) {
			switch (errorCode) {
			case Contants.SUCCESS:
				// 成功返回UserInfo对象
				UserInfo userInfo = (UserInfo) resultObj;
				
				uid = userInfo.getUid();
				
				JsonObject jsonData=new JsonObject();
                jsonData.add("uid", uid);

                
                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
                jsonParms.add("data", jsonData);
                 
                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
                
				break;
			case Contants.NO_USER:
				// 用户不存在
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
				break;
			case Contants.TIMEOUT_ERROR:
				// 超时
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
				break;
			case Contants.NETWORK_ERROR:
				// 网络错误
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
				break;
			default:
				// 失败返回String
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
				break;
			}
		}
	};
	
	private static void doGameNotify(Context context,String type,String level,String info,String expend,IDispatcherCallback callback){
			//调用游戏操作通知接口
			Matrix.gameNotify(context,type,level,info, expend,callback);
			}
			
	private static IDispatcherCallback gameNotifyCallback = new IDispatcherCallback() {
		@Override
		public void doFinished(int errorCode,
				Serializable resultObj) {
			switch (errorCode) {
			case Contants.SUCCESS:
				// 成功

				break;
			default:
				// 失败

				break;
			}
		}
	};
	
 
}
