//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.text.SimpleDateFormat;
import java.util.Date;

import net.umipay.android.GameParamInfo;
import net.umipay.android.UmiPaySDKManager;
import net.umipay.android.UmipaySDKStatusCode;
import net.umipay.android.interfaces.AccountCallbackListener;
import net.umipay.android.interfaces.InitCallbackListener;

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
import net.umipay.android.GameParamInfo;
import net.umipay.android.GameRolerInfo;
import net.umipay.android.GameUserInfo;
import net.umipay.android.UmiPaySDKManager;
import net.umipay.android.UmiPaymentInfo;









 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyoumi extends  MoaiBaseSdk {
	 
	public static String appSecret = "";
	public static String appId = "";
	public static String paynotifyUrl = "";
	public static String uid = "";
	public static String gameId = "";
	public static String serverId = "";
	public static String roleId = "";
	 
	 
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
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);
		

    }
	
	public static  String V2_OpenPlatform(JsonValue parms){
		
		 UmiPaySDKManager.showAccountManageView(sActivity);//调用帐号中心接口
        
		return ""; 		 
	}

	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		 
		 appSecret	 =  GetJsonVal(sConfigJsonObject,"appSecret","000");
	     appId  =  GetJsonVal(sConfigJsonObject,"appId","000");
	     
	     
	     
	     
	     GameParamInfo gameParamInfo = new GameParamInfo();
	        gameParamInfo.setAppId(appId);//设置AppID
	        gameParamInfo.setAppSecret(appSecret);//设置AppSecret
	        gameParamInfo.setTestMode(false); //【可选】设置测试模式，默认为false
	
	        UmiPaySDKManager.initSDK(sActivity, gameParamInfo, new InitCallbackListener() {
	          @Override
	          public void onSdkInitFinished(int code, String message) {
	              if (code == UmipaySDKStatusCode.SUCCESS) {
	              
	              } else {
	         
	              }
	          }
	        }, new AccountCallbackListener() {
	            @Override
	            public void onLogin(int code, GameUserInfo userInfo) {
	                if (code == UmipaySDKStatusCode.SUCCESS && userInfo!=null) {
	                	
	                	
	                	uid = userInfo.getUid();
	                	String sign = userInfo.getSign();
	                	String timeNow = String.valueOf(userInfo.getTimestamp_s());
	                	
	                	
	                	JsonObject jsonData=new JsonObject();
		                jsonData.add("uid", uid);
		                jsonData.add("sign",sign);
		                jsonData.add("time",timeNow);

		                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
		                jsonParms.add("data", jsonData);
		                 
		                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
	                } else {
	                    //用户按返回键,退出了sdk登录界面,需要进行相关的操作,比如弹出对话框提醒用户重新登录。
	                	
	                	 JsonRpcCall(Lua_Cmd_LoginOut,"");
	                }
	            }
	            @Override
	            public void onLogout(int code) {
	                if(code == UmipaySDKStatusCode.SUCCESS) {
	                    //客户端成功登出游戏账户
	                	JsonRpcCall(Lua_Cmd_LoginOut,"");
	                }
	            }
	        });


		

	}
	
	public void onMResume(){
		

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
	public static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		UmiPaySDKManager.showLoginView(sActivity);
						
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();
			
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			String roleLv = userinfoJson.get("roleLv").asString();
			
			
			
			
			UmiPaymentInfo paymentInfo = new UmiPaymentInfo();
			
	        paymentInfo.setServiceType(UmiPaymentInfo.SERVICE_TYPE_QUOTA);
	        paymentInfo.setPayMoney(price);
	        paymentInfo.setDesc(productName);

	        paymentInfo.setRoleGrade(roleLv); //【必填】设置用户的游戏角色等级
	        paymentInfo.setRoleId(roleId);// 【必填】设置用户的游戏角色的ID
	        paymentInfo.setRoleName(roleName);// 【必填】设置用户的游戏角色名字
	        paymentInfo.setServerId(serverId);//【必填】设置用户所在的服务器ID
	        paymentInfo.setCustomInfo(OrderNo);// 【可选】游戏开发商自定义数据。该值将在用户充值成功后，在充值回调接口通知给游戏开发商时携带该数据
	        UmiPaySDKManager.showPayView(sActivity, paymentInfo);//调用充值接口
			
			

			
		
			


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

		UmiPaySDKManager.showChangeAccountView(sActivity);//登出账户接口
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
		String roleLv = GetJsonVal(jsonData,"roleLv","000");
		String roleName = GetJsonVal(jsonData,"roleName","000");
		 roleId = GetJsonVal(jsonData,"roleId","000");
		String serverName = GetJsonVal(jsonData,"serverName","渠道");
		
	      GameRolerInfo gameRolerInfo = new GameRolerInfo();
	        gameRolerInfo.setServerId(serverId);
	        gameRolerInfo.setServerName(serverName);
	        gameRolerInfo.setRoleId(roleId);
	        gameRolerInfo.setRoleName(roleName);
	        gameRolerInfo.setRoleLevel(roleLv);
	        UmiPaySDKManager.setGameRolerInfo(sActivity,gameRolerInfo);

		return "";
	}
	
 
}
