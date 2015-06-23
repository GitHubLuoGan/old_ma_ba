//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.snowfish.cn.ganga.helper.SFOnlineExitListener;
import com.snowfish.cn.ganga.helper.SFOnlineHelper;
import com.snowfish.cn.ganga.helper.SFOnlineLoginListener;
import com.snowfish.cn.ganga.helper.SFOnlinePayResultListener;
import com.snowfish.cn.ganga.helper.SFOnlineUser;



 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyijie  extends  MoaiBaseSdk {
	 
	 
	public static String  serverId = "";
	public static String  roleId = "";
	public static String  level = "";
	public static String  roleName = "";

	
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
		SFOnlineHelper.onCreate(sActivity);
		
		SFOnlineHelper.setLoginListener(sActivity, new SFOnlineLoginListener(){
			  public void onLogout(Object paramObject){
				  
				  JsonRpcCall(Lua_Cmd_LoginOut,"");
				  SFOnlineHelper.login(sActivity, "Login");
				  
			  }

			  public  void onLoginSuccess(SFOnlineUser paramSFOnlineUser, Object paramObject){
				  
				  
				  JsonObject jsonData=new JsonObject();
	                jsonData.add("sdkId", paramSFOnlineUser.getChannelId());
	                jsonData.add("token",paramSFOnlineUser.getToken());
	                jsonData.add("userId",paramSFOnlineUser.getChannelUserId());  
	                jsonData.add("gameId",paramSFOnlineUser.getProductCode()); 
	                
	                JsonObject jsonParms=SDKFormatGateWay(paramSFOnlineUser.getChannelUserId(),jsonData);
	                jsonParms.add("data", jsonData);
	                 
	                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
				  
			  }

			  public  void onLoginFailed(String paramString, Object paramObject){}
			
			
		});
		
	}

//退出游戏时
	public void onMDestroy(){
		
		SFOnlineHelper.onDestroy(sActivity);
	}
	//Stop
public void onMStop(){
		
	  SFOnlineHelper.onStop(sActivity);
	}

	//Resume
public void onMResume(){
	
    SFOnlineHelper.onResume(sActivity);

}

	//Pause
public void onMPause(){
	
    SFOnlineHelper.onPause(sActivity);

}
//Restart

public void onMRestart(){
	
	SFOnlineHelper.onRestart (sActivity);
}


	
//是否退出时执行
public static String V2_exitGame(JsonValue parms){

	SFOnlineHelper.exit(sActivity, new SFOnlineExitListener() {
			/*  onSDKExit
			 *  @description　当SDK有退出方法及界面，回调该函数
			 *  @param bool   是否退出标志位  
			 */
			@Override
			public void onSDKExit(boolean bool) {
				if (bool){
					//apk退出函数，demo中也有使用System.exit()方法；但请注意360SDK的退出使用exit（）会导致游戏退出异常
					sActivity.finish();
				}
			}
			/*  onNoExiterProvide
			 *  @description　SDK没有退出方法及界面，回调该函数，可在此使用游戏退出界面
			 */
			@Override
			public void onNoExiterProvide() {


						JsonRpcCall(Lua_Cmd_GameExit,"");
						
				
			}
		});
		return "";
	}
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
		
		
		SFOnlineHelper.exit(sActivity, new SFOnlineExitListener() {
			/*  onSDKExit
			 *  @description　当SDK有退出方法及界面，回调该函数
			 *  @param bool   是否退出标志位  
			 */
			@Override
			public void onSDKExit(boolean bool) {
				if (bool){
					//apk退出函数，demo中也有使用System.exit()方法；但请注意360SDK的退出使用exit（）会导致游戏退出异常
					
				}
			}
			/*  onNoExiterProvide
			 *  @description　SDK没有退出方法及界面，回调该函数，可在此使用游戏退出界面
			 */
			@Override
			public void onNoExiterProvide() {
				AlertDialog.Builder builder = new Builder(sActivity);
				builder.setTitle("游戏自带退出界面");
				builder.setPositiveButton("退出",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						sActivity.finish();
						//								System.exit(0);
					}
				});
				builder.show();
			}
		});
		
		onfinish.callback(true);
		
		
	    
		return true;
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		 SFOnlineHelper.login(sActivity, "Login");
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=Integer.parseInt(payinfoJson.get("price").asString()) * 100;
			String proName = payinfoJson.get("name").asString();
			String callBackUrl = sConfigJsonObject.get("callBackUrl").asString();
			
			
			SFOnlineHelper.pay(sActivity, price, proName, 1, OrderNo, callBackUrl, new SFOnlinePayResultListener() {
				@Override
				public void onSuccess(String remain) {
				}
				@Override
				public void onFailed(String remain) {
				}
				@Override
				public void onOderNo(String orderNo) {
					
				}
			});
 
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}
	
	
	public static String V2_gameExit(JsonValue parms){
		
		
		
		SFOnlineHelper.exit(sActivity, new SFOnlineExitListener() {
			/*  onSDKExit
			 *  @description　当SDK有退出方法及界面，回调该函数
			 *  @param bool   是否退出标志位  
			 */
			@Override
			public void onSDKExit(boolean bool) {
				if (bool){
					//apk退出函数，demo中也有使用System.exit()方法；但请注意360SDK的退出使用exit（）会导致游戏退出异常
					sActivity.finish();
				}
			}
			/*  onNoExiterProvide
			 *  @description　SDK没有退出方法及界面，回调该函数，可在此使用游戏退出界面
			 */
			@Override
			public void onNoExiterProvide() {
				
				
				JsonRpcCall(Lua_Cmd_GameExit,"");

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
 
		 return "OK";	 
	}
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	public static String V2_authQuit(JsonValue parms){
		 
		SFOnlineHelper.logout(sActivity, "logOut");
		 return "OK";	 
	}
 
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
		
		
			JsonObject jsonData=parms.asObject();
			serverId  = jsonData.get("serverIndex").asString();
			roleId    = jsonData.get("roleId").asString();
			level     = jsonData.get("roleLv").asString();
			jsonData.get("vipLv").toString();
			jsonData.get("roleClass").toString();
			roleName = jsonData.get("roleName").toString();
			String serverName = jsonData.get("serverName").toString();
		
			SFOnlineHelper.setRoleData(sActivity, roleId, roleName, level, serverId, serverName);
		
		return "";
		
	}
	

	
 
}

