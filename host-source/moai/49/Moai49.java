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
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sjsdk.activity.Sjyx;
import com.sjsdk.info.SjyxLoginInfo;
import com.sjsdk.info.SjyxPaymentInfo;
import com.sjsdk.init.InitData.InitListener;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moai49 extends  MoaiBaseSdk {
	 
	public static int appId = 0;
	public static String appKey = "";
	 
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
		 
		appId = sConfigJsonObject.get("appId").asInt();
		appKey = sConfigJsonObject.get("appKey").asString();
		
		Sjyx.initInterface(sActivity, appId, appKey,
				"", true, new InitListener() {

					@Override
					public void fail(String msg) {
						// TODO Auto-generated method stub
					}

					@Override
					public void Success(String msg) {
						// TODO Auto-generated method stub
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

	
public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }

public void onMActivityResult(int requestCode, int resultCode, Intent data){
	// TODO Auto-generated method stub
//	super.onActivityResult(requestCode, resultCode, data);
	if (data == null) {
		return;
	}
	switch (requestCode) {
	case Sjyx.LOGIN_RESULT_CODE:

		String userName = data.getStringExtra("userName");
		String uid = data.getStringExtra("uid");
		String timeStamp = data.getStringExtra("timeStamp");
		String sign = data.getStringExtra("sign");
		
		 JsonObject jsonData=new JsonObject();
         jsonData.add("uid", uid);
         jsonData.add("timeStamp",timeStamp);
         jsonData.add("sign",sign);
         jsonData.add("userName",userName);
         
         JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
         jsonParms.add("data", jsonData);
         
         JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
		
		break;
	case Sjyx.PAY_RESULT_CODE:
		String pay = data.getStringExtra("result");
		break;
	}
}


	
	
	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		SjyxLoginInfo loginInfo = new SjyxLoginInfo();
		loginInfo.setAppid(appId);
		loginInfo.setAppkey(appKey);
		loginInfo.setAgent("");
		loginInfo.setServer_id("");
		loginInfo.setOritation("landscape");
		Sjyx.login(sActivity, loginInfo);
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userInfo=_json.get("userInfo").asObject();
			
			String serverId = userInfo.get("serverIndex").asString();
			String OrderNo=payinfoJson.get("orderno").asString();
			String price=payinfoJson.get("price").asString();

			
			
			
			SjyxPaymentInfo payInfo = new SjyxPaymentInfo();
			payInfo.setAppId(appId);
			payInfo.setAppKey(appKey);
			payInfo.setAgent("");
			payInfo.setServerId(serverId);
			payInfo.setBillNo(OrderNo);
			payInfo.setAmount(price);
			payInfo.setOritation("landscape");
			payInfo.setSubject(sConfigJsonObject.get("gameName").asString());
			payInfo.setExtraInfo(OrderNo);
			payInfo.setUid(""); // 如果为“”，说明是接入了我们的登陆sdk，如果要只接入充值sdk，则需要传入对方平台的username
			payInfo.setIsTest(""); // 是否测试，可为空，0为非测试，1为测试，空为非测试
			payInfo.setGameMoney(sConfigJsonObject.get("proName").asString());// 游戏中的游戏币名称
			payInfo.setMultiple(10); // 人民币与游戏币的兑换倍数
			Sjyx.payment(sActivity, payInfo);
			

		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
	//	   mAnzhiCenter.getInstance().viewUserInfo(super.sActivity); 
	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
	//	mAnzhiCenter.getInstance().logout(super.sActivity);		 
		 return "OK";	 
	}
	
	
	public static String V2_exitGame(JsonValue parms){
		
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		
		JsonObject jsonData=parms.asObject(); 
		return "";
	
	}
 
}

