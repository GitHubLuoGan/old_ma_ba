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
import android.util.Log; 
import android.widget.Toast;
 
 
import com.blocks.thirdpay.AppConnect;
import com.blocks.thirdpay.FeeCallBack;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.uucun.android.passport.openApi.SdkOpenApi;
import com.uucun.android.passport.openApi.SdkCallback;
import com.uucun.android.passport.openApi.UserSimpleInfo;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyyc extends  MoaiBaseSdk {
	 
	public static String channelId = "";
	public static String appSecret = "";
	public static String appKey = "";
	public static String paynotifyUrl = "";
	public static String uucId = "";
	 
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
		 
		 channelId = sConfigJsonObject.get("channelId").asString();
	     appSecret = sConfigJsonObject.get("appSecret").asString();
		 appKey = sConfigJsonObject.get("key").asString();
		 paynotifyUrl = sConfigJsonObject.get("paynotifyUrl").asString();
		
		SdkOpenApi.sdkStart();
		AppConnect.getInstance(sActivity).initSdk(appKey, channelId);
		
	}
	
	public void onMResume(){
		
		AppConnect.getInstance(sActivity).initSdk(appKey, channelId);
	}

//退出游戏时
	public void onMDestroy(){
		
		AppConnect.getInstance(sActivity).finalize();
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
		
//		SdkOpenApi.openPassportSdk(sActivity, new SdkCallback() {
//
//			@Override
//			public void onResult(boolean ret) {
//				// 此处ret值只为true
//				if (SdkOpenApi.isLogined()) {
//					//Toast.makeText(mContext, "回调-退出sdk-登录成功", Toast.LENGTH_SHORT).show();
//				} else {
//					//Toast.makeText(mContext, "回调-退出sdk-登录失败", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
		
		SdkOpenApi.openPassportSdk(sActivity, new SdkCallback(){

			@Override
			public void onResult(boolean paramBoolean) {
				// TODO Auto-generated method stub
				if (SdkOpenApi.isLogined()) {
			       long time = SdkOpenApi.getUserInfo().loginTime;
					String uid = SdkOpenApi.getUserInfo().uuptid;
					String token = SdkOpenApi.getUserInfo().ssotoken;
					String  nickName = SdkOpenApi.getUserInfo().nickName;
					String Time = String.valueOf(time);
					
					uucId = uid;
					
					   JsonObject jsonData=new JsonObject();
			   			 jsonData.add("nickName",nickName);
			   			 jsonData.add("userId",uid);
			   			 jsonData.add("token",token);
			   			jsonData.add("time",Time);

			   	      //jsonData.get(name)
			   			
			   	        JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
			   	        jsonParms.add("data", jsonData);
			   	        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
					
					
		} else {
				//Toast.makeText(mContext, "回调-退出sdk-登录失败", Toast.LENGTH_SHORT).show();
			}
				
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
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			price =price * 100;

			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = sdf.format(date.getTime());
			
			
			AppConnect.getInstance(sActivity).pay(OrderNo, appKey, appSecret,String.valueOf(price),paynotifyUrl,
					null, 10,
					"钻石", productName, 1,
					OrderNo, nowTime, null, getBack(), uucId,"");
			

		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	
	private static FeeCallBack getBack() {
		return new com.blocks.thirdpay.FeeCallBack() {
			public void onStart() {
				MoaiLog.i("callback:支付开始");
			}
			
			public void onEnd() {
				MoaiLog.i("callback:支付结束");
			}

			public void onOrderSuccess() {
				MoaiLog.i("callback:订单提交成功");
			}

			public void onOrderError(int errorType, String msg) {
				MoaiLog.i("callback:支付错误:" + errorType + " " + msg);
			}
		};
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

		SdkOpenApi.logoutPassport(sActivity, new SdkCallback(){
			
			public  void onResult(boolean paramBoolean){
				if(paramBoolean) JsonRpcCall(Lua_Cmd_LoginOut,"");
			};
			
		});
		 return "OK";	
	}
	
	
	///退出游戏
	public String ExitGame(JsonValue parms){
	 
		 return "OK";	 
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
	
		JsonObject jsonData=parms.asObject(); 
		return "";
	}
 
}
