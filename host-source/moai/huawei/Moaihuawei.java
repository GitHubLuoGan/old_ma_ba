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
 
 
import com.android.huawei.pay.plugin.IHuaweiPay;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.PayParameters;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import com.huawei.deviceCloud.microKernel.core.MicroKernelFramework;
import com.huawei.gamebox.buoy.sdk.IBuoyOpenSDK;
import com.huawei.gamebox.buoy.sdk.InitParams;
import com.huawei.gamebox.buoy.sdk.util.BuoyConstant;
import com.huawei.gamebox.buoy.sdk.util.DebugConfig;

import com.huawei.hwid.openapi.out.IHwIDCallBack;
import com.huawei.hwid.openapi.out.microkernel.IHwIDOpenSDK;
 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaihuawei  extends  MoaiBaseSdk {
	 
	public static String accessToken = "";
	public static String userID = "";
	public static String userName = "";
	public static String loginStatus = "";
	public static long timedate = 0;
	public static String Timedate = "";
	public static MicroKernelFramework framework = null;
	public static IBuoyOpenSDK  hwBuoy = null;
	public static IHwIDOpenSDK  hwId = null;
	public static IHuaweiPay hwPay = null;
	 
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
		 
		new InitParams(sConfigJsonObject.get("appId").asString(), sConfigJsonObject.get("CPID").asString(), sConfigJsonObject.get("fubiao").asString(), new GameCallBack());
		framework = MicroKernelFramework.getInstance(sActivity);
		framework.start();

		framework.loadPlugin("hwIDOpenSDK");
		framework.loadPlugin("HuaweiPaySDK");
		framework.loadPlugin(BuoyConstant.PLUGIN_NAME);
		List<Object> services_login = framework.getService("hwIDOpenSDK");
		List<Object> services_pay = framework.getService("HuaweiPayApk");
		List<Object> services_floatbutton = framework.getService(BuoyConstant.PLUGIN_NAME);
		
		if(services_login != null && services_pay != null && services_floatbutton != null){
			
			hwId = (IHwIDOpenSDK) services_login.get(0);
			hwPay = (IHuaweiPay) services_pay.get(0);
			hwBuoy = (IBuoyOpenSDK) services_floatbutton.get(0);
			MoaiLog.i("qu ni mei de !!!!");
		}


	
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

	//////////////huawei
	private class LoginCallBack implements IHwIDCallBack {

       

        @Override
        public void onUserInfo(HashMap userInfo) {

            // 用户信息为空，登录失败
            if (null == userInfo) {


            }
            // 使用华为账号登录且成功，进行accessToken验证
            else if ("1".equals((String) userInfo.get("loginStatus"))) {
            	
                accessToken = (String) userInfo.get("accesstoken");
                userID = (String) userInfo.get("userID");
                userName = (String) userInfo.get("userName");
                timedate = System.currentTimeMillis()/1000;
                Timedate = String.valueOf(timedate);
                JsonObject jsonData=new JsonObject();
   			 jsonData.add("username",userName);
   			 jsonData.add("userID",userID);
   			 jsonData.add("accessToken",accessToken);
   			jsonData.add("Timedate",Timedate);

   	      //jsonData.get(name)
   			
   			hwBuoy.showSamllWindow(sActivity);
   	        JsonObject jsonParms=SDKFormatGateWay(userID,jsonData);
   	        jsonParms.add("data", jsonData);
   	        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
            }
        }
	
	}
	
	
	
	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
             Bundle loginBundle = new Bundle();
             hwId.setLoginProxy(sActivity,sConfigJsonObject.get("appId").asString(), new LoginCallBack(),loginBundle);
             hwId.login(new Bundle());
            
		
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
			
			if(MoaiBaseSdk.sdkversion>1)price = price/100;
			
			
			
			double price_double = (double)price;
			//price_double = 0.1;
			String Price_ = String.format("%.2f", price_double);
			String userName_ = "成都雷兽互动科技有限公司";
			
//		    Map<String,Object> payInfo=new HashMap<String,Object>();
//
//		    payInfo.put("userName", userName_);
//		    payInfo.put("userID", sConfigJsonObject.get("CPID").asString());
//		    payInfo.put("applicationID",sConfigJsonObject.get("appId").asString());
//		    payInfo.put("amount", Price_str);
//		    payInfo.put("productName", info);
//		    payInfo.put("requestId", OrderNo);
//		    payInfo.put("productDesc", info);
//		    payInfo.put("accessToken", accessToken);
		    
		    /*********************************************/
		    
		    
		    Map<String, String> params = new HashMap<String, String>();
	        params.put("userID",sConfigJsonObject.get("CPID").asString());
	        params.put("applicationID", sConfigJsonObject.get("appId").asString());
	        params.put("amount", Price_);
	        params.put("productName", info);
	        params.put("productDesc", info);
	        params.put("requestId", OrderNo);

	        String noSign = HuaweiPayUtil.getSignData(params);
	        MoaiLog.i("nosign:::::::::::" + noSign);
	        
	        String sign = Rsa.sign(noSign, sConfigJsonObject.get("privateKey").asString());
	        MoaiLog.i("sign:::::::::::::" + sign);

	        Map<String, Object> payInfo = new HashMap<String, Object>();
	        payInfo.put("amount", Price_);
	        payInfo.put("productName", info);
	        payInfo.put("requestId", OrderNo);
	        payInfo.put("productDesc", info);
	        payInfo.put("userName", userName_);
	        payInfo.put("applicationID", sConfigJsonObject.get("appId").asString());
	        payInfo.put("userID", sConfigJsonObject.get("CPID").asString());
	        payInfo.put("sign", sign);
	        payInfo.put("notifyUrl", null);
	        payInfo.put("accessToken", accessToken);
	        
	        // 调试期可打开日志，发布时注释掉
	       // payInfo.put("showLog", true);

	        MoaiLog.i( "支付请求参数 ::::::::: " + payInfo.toString());

		    /**********************************************/
			
			hwPay.startPay(sActivity, payInfo, new IPayHandler() {
		        @Override
		        public void onFinish(Map<String, String> payResp) {
	
		            String pay = "支付未成功！";

		            // 支付成功，进行验签
		            if ("0".equals(payResp.get(PayParameters.returnCode))) {
		                if ("success".equals(payResp.get(PayParameters.errMsg))) {
		                // 支付成功，验证信息的安全性；待验签字符串需要去除returnCode和sign两个参数
		                } 
		                else {}
		            } 
		            else if ("30002".equals(payResp.get(PayParameters.returnCode))) {
		                pay = "支付结果查询超时！";
		            }
		           }});
			
			
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
		hwId.login(new Bundle());
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
	

