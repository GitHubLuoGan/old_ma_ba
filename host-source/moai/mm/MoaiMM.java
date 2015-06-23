//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.ArrayList;
import java.util.List;

import mm.purchasesdk.Purchase;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
  
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;



 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaimm  extends  MoaiBaseSdk {
	public static Purchase  purchase=null;
	public static IAPListener mListener; 
	public static JsonValue orderParms;

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
		
		payCodeConfig.setPayCodeConfig();
		purchase=Purchase.getInstance();
		
		IAPHandler iapHandler = new IAPHandler(MoaiBaseSdk.sActivity);
		mListener = new IAPListener(MoaiBaseSdk.sActivity, iapHandler);
		String appid=GetJsonVal(sConfigJsonObject,"appid","0");
		String appkey=GetJsonVal(sConfigJsonObject,"appkey","0");
		purchase.setAppInfo(appid, appkey);  // 设置计费应用ID和Key (必须)
		//purchase.setTimeout(10000, 10000); // 设置超时时间(可选)，可不设置，缺省都是10s		
		try { 
			purchase.init(sActivity, mListener); //初始化，传入监听器

		} catch (Exception e) {
			MoaiLog.e("purchase.init error"+e.toString());
			//e.printStackTrace();
			return;
		}
	}

	
	 
    private void showToast(String hints) {
        Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
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
	public String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();     
            JsonObject  payinfoJson=_json.get("payinfo").asObject();
            String OrderNo=payinfoJson.get("orderno").asString();
            int price=payinfoJson.get("price").asInt();
            int total=payinfoJson.get("total").asInt(); 
             String number=GetJsonVal(payinfoJson,"number","0"); 
           //cpparam="20131031101034ff";
            //OrderNo=OrderNo.substring(0,16);            
            int _total=total;         
            if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
            
            payCodeConfig bConfig=new payCodeConfig();
            bConfig.number=number;
            bConfig.money=total;
            bConfig=payCodeConfig.getPayCodeConfig(bConfig);
            String tradeid = purchase.order(MoaiBaseSdk.sActivity, bConfig.payCode, mListener);
          
			
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
		return "";
	}
	
 
}

