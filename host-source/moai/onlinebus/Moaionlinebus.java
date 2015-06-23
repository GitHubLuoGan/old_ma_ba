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
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.busap.pay.PaySDK;
import com.busap.pay.IBusapPayResultCallback;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaionlinebus  extends  MoaiBaseSdk {
	 
	 
	String roleName = "";
	
	
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
		 
		String  appid = sConfigJsonObject.get("appId").asString();
		String appkey = sConfigJsonObject.get("appKey").asString();
		String notifyurl = sConfigJsonObject.get("callBack").asString();
		 
		PaySDK.initSDK(sActivity, 1, appid, appkey,notifyurl);
		PaySDK.preGettingData(sActivity);
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
			
			int waresid = 0 ;

		if(MoaiBaseSdk.sdkversion > 1 )price *= 0.01; /*************不同版本价格传入单位不同************/
        
	switch(price){
			
			case 6: waresid = 1;
			break;
			case 10: waresid = 3;
			break;
			case 30: waresid = 4;
			break;
			case 48: waresid =5;
			break;
			case 50: waresid = 6;
			break;
			case 100: waresid = 7;
			break;
			case 200: waresid = 8;
			break;
			case 300: waresid = 9;
			break;
			case 500: waresid = 10;
			break;
			case 2000: waresid = 11;
			break;
			default: waresid = 0;
			break;
	}
		
			
			/****************************************************/
			
	        PaySDK.startPay(sActivity, waresid, OrderNo, price, 1, OrderNo, roleName, 
	        		new IBusapPayResultCallback() {
	        	
						public void onPayResult(int resultCode,
							String signValue, String resultInfo) {
							
							if (PaySDK.PAY_SUCCESS == resultCode) {
								JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功");
								}
							 else if (PaySDK.PAY_CANCEL == resultCode) {
								 JsonRpcCall(Lua_Cmd_PayCancel,"取消支付");
							} 
							 else if (PaySDK.PAY_HANDLING == resultCode) {
								 JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
							} else {
								JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
							}
						}
					});
			
			/****************************************************/
			
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
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
		
		 JsonObject jsonData=parms.asObject();
			
		    JsonObject jsonObject = new JsonObject();
			jsonData.get("serverIndex").asString();
			jsonData.get("RoleId").asString();
			jsonData.get("Rolelv").asString();
			jsonData.get("vipLevel").toString();
			jsonData.get("RoleClass").toString();
			roleName = jsonData.get("RoleName").toString();
			jsonData.get("serverName").toString();
		

		return "OK";
	}
	
}

