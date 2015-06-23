//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



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
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.listener.*;
import com.sogou.gamecenter.sdk.service.UserService;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.UserInfo;

import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.service.UserService;
import com.sogou.gamecenter.sdk.util.Logger;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.sogou.udp.push.SGPushMessageService;
import com.sogou.udp.push.PushService;
import com.sogou.gamecenter.sdk.listener.PayCallbackListener;
import com.sogou.gamecenter.sdk.listener.SwitchUserListener;
import com.sogou.gamecenter.sdk.listener.OnExitListener;










 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaisougou extends  MoaiBaseSdk {
	 
	public static String appSecret = "";
	public static String appId = "";
	public static String paynotifyUrl = "";
	public static String uid = "";
	public static String gameId = "";
	public static String serverId = "";
	public static String roleId = "";
	public static String TAG = "";
	 
	
	public static SogouGamePlatform sgSdk = SogouGamePlatform.getInstance();
	 
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
		
		
        
		return ""; 		 
	}

	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		 
		
		
		 appSecret	 =  GetJsonVal(sConfigJsonObject,"appSecret","000");
	     appId  =  GetJsonVal(sConfigJsonObject,"appId","000");
	     
	     
	 	SogouGameConfig config = new SogouGameConfig();		

		config.devMode  =  false;
		config.gid      = Integer.parseInt(GetJsonVal(sConfigJsonObject,"gid","000"));
		config.appKey   = GetJsonVal(sConfigJsonObject,"appKey","000");
		config.gameName = GetJsonVal(sConfigJsonObject,"gameName","000");
		// 设置push开启，默认为false不开启，true开启		
		
		//SDK准备初始化
		
		sgSdk.prepare(sActivity, config);
 
	     
	     
	     sgSdk.init(sActivity, new InitCallbackListener() {
				
		    	@Override
				public void initSuccess() {
		    		Log.d(TAG,"初始化成功:");
		    		
		    			
				}
				
				@Override
				public void initFail(int code, String msg) {
					// SDK初始化失败再此关闭游戏即可
					Log.d(TAG,"初始化失败哈哈：");
				}
			});      
	    

	}
	
	public void onMResume(){
		super.onResume();

	}

//退出游戏时
	public void onMDestroy(){
		sgSdk.onTerminate();
		
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
		UserService.getInstance().login(sActivity, new LoginCallbackListener(){

			@Override
			public void loginSuccess(int code, UserInfo userInfo) {
				
				
				uid = String.valueOf(userInfo.getUserId());
				String session = userInfo.getSessionKey();
				String result= userInfo.toString();
				
            	JsonObject jsonData=new JsonObject();
                jsonData.add("uid", uid);
                jsonData.add("session",session);

                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
                jsonParms.add("data", jsonData);
                 
                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);

					Log.d(TAG,"登陆成功2222："+uid+session);
				//login success
			}

			@Override
			public void loginFail(int code, String msg) {
				//login fail
				Log.d(TAG,"登陆失败：");
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
			
			String OrderNo = payinfoJson.get("orderno").asString();
			int price = Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			String roleLv = userinfoJson.get("roleLv").asString();
			
			Map<String, Object> data = new HashMap<String, Object>();
			// 游戏中货币名字（必传）
			data.put("currency", "钻石"); 
			// 人民币兑换比例（必传）进行商品显示
			data.put("rate", 10);
			data.put("amount", price);
			data.put("product_name", productName); 
			// 透传参数,游戏方自行定义
			data.put("app_data", OrderNo);
			// 可选参数:隐藏支付渠道  	//des    1 支付宝2 银联3财付通 4电话卡 5s豆 
//			data.put("hide_channel", "3:4:5"); // 支付渠道之间用冒号分割，隐藏3财付通，4电话卡，S豆支付渠道
			sgSdk.pay(sActivity,data,new PayCallbackListener(){
				@Override

				public void paySuccess(String orderId,String appData) {}
				
				@Override
				public void payFail(int code, String orderId,String appData) {}
	
			
			},true);
			
		
			


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

		sgSdk.switchUser(sActivity,new SwitchUserListener(){


			@Override
			public void switchSuccess(int code, UserInfo userInfo) {

			
			}

			@Override
			public void switchFail(int code, String msg) {

			}			
		
		});
		 return "OK";	
	}
	
	
	///退出游戏
	public String ExitGame(JsonValue parms){
		sgSdk.exit(new OnExitListener(sActivity){


			@Override
			public void onCompleted() {
			}			
		
		});
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
		


		return "";
	}
	
 
}
