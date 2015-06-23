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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.xgdata.XGGame;
import com.bbox.api.*;
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaipma  extends  MoaiBaseSdk {
	 
	 
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
		 
		XGGame.Active(sActivity);
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
	public String OpenPay(final JsonValue parms)
	{ 
		try { 
			JsonValue tempValue;
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=null;
			JsonObject	userinfoJson=null; 
			 
			if(MoaiBaseSdk.sdkversion==1){
				if(_json.get("payinfo")==null) MoaiLog.e("payinfo is not find");
				if(_json.get("userinfo")==null) MoaiLog.e("userinfo is not find");
				payinfoJson=_json.get("payinfo").asObject();
				userinfoJson=_json.get("userinfo").asObject(); 
			}else
			{
				if(_json.get("payInfo")==null) MoaiLog.e("payInfo is not find");
				if(_json.get("userInfo")==null) MoaiLog.e("userInfo is not find");
				payinfoJson=_json.get("payInfo").asObject();
				userinfoJson=_json.get("userInfo").asObject();			 	 
			}
			
			
			String OrderNo=GetJsonVal(payinfoJson,"orderno","0");		 
			String number=GetJsonVal(payinfoJson,"number","0");
			 
			
			int price=GetJsonValInt(payinfoJson,"price",0); 
			
			int total=GetJsonValInt(payinfoJson,"total",0);  
			 
			String info=GetJsonVal(payinfoJson,"info","0");
			String UserId=GetJsonVal(userinfoJson,"roleId","0"); 
			 
			int PMAdirection=MoaiBaseSdk.GetJsonValInt(sConfigJsonObject, "PMADirection", 1);
			String PMAchannelID=MoaiBaseSdk.GetJsonVal(sConfigJsonObject, "PMAChannelID", "");
			BillInterface bill=new BillInterface(sActivity,PMAchannelID,PMAdirection);

		  
			//自定义回调函数
			BillCallBack bcb= new BillCallBack(){
			 
				public void onBillingSuccess() {
					JsonObject jsonParms=new JsonObject();
					jsonParms.add("code", 1);
					jsonParms.add("msg", "充值成功");
					jsonParms.add("payData", parms.asObject());
					//计费成功
					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
	             //接下来可以做账户充值验证工作
				}
				// 计费失败
				 
				public void onBillingFailed() {
					JsonObject jsonParms=new JsonObject();
					jsonParms.add("code", -1);
					jsonParms.add("msg", "充值失败");
					jsonParms.add("payData", parms.asObject());
					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
				}
				// 异常错误				 
				public void onBillingError (String msg) {
					JsonObject jsonParms=new JsonObject();
					jsonParms.add("code", -2);
					jsonParms.add("msg", msg);
					jsonParms.add("payData", parms.asObject());
					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
				}         
	           // 计费超时			 
			   public void onBillingTimeOut(){
				   JsonObject jsonParms=new JsonObject();
					jsonParms.add("code", -4);
					jsonParms.add("msg", "计费超时");
					jsonParms.add("payData", parms.asObject());
					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
	             //计费超时，通常由第三方支付引起，第三方返回结果超过1分钟则会调用此回调函数 ，产品给出相应提示，
				   //计费服务器获得计费结果将异步发送给cp服务器
			    }
			};
			
		 
	        if(checkNetworkAvailable(MoaiBaseSdk.sActivity))  
	        {  
	        	//bill.billing(sActivity, price,OrderNo,UserId ,number, info, OrderNo, "正在购买道具{ ConsumerName },资费{fee},由{SP}代收,客服电话{tel}", bcb);
	        	  bill.billing(sActivity, price,OrderNo,UserId ,number, info, OrderNo, "", bcb);
	            //当前有可用网络  
	        }  
	        else   
	        {  
	        	Toast.makeText(MoaiBaseSdk.sActivity, "请先连接网络！", Toast.LENGTH_SHORT).show();
	            //当前无可用网络  
	        }  
	        
	        
		

		} catch (Exception e) { 
			 
			MoaiLog.i(" OpenPay is Error:"+e.toString());
			MoaiLog.e(" Error Content:",e);
			
			return "false";
		}		
		 
		 return "OK";	
	}
	
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}

		return false;

	}
	

	//SDK2.0模式
	public static String V2_OpenPay(JsonValue parms){
		return MoaiBaseSdk.sBaseSDK.OpenPay(parms);
	}
	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){ 
	 
		 return "OK";	
	}
	
	//SDK2.0模式
	public static String V2O_penMemberCenter(JsonValue parms){
		return MoaiBaseSdk.sBaseSDK.OpenMemberCenter(parms);
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){  
		 return "OK";	 
	}
 
	//SDK2.0模式
	public static String V2_ExitGame(JsonValue parms){
		return MoaiBaseSdk.sBaseSDK.ExitGame(parms);
	}
	
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		 
		return "";
	}
	//SDK2.0模式
	public static String V2_setCharInfo(JsonValue parms){
		return MoaiBaseSdk.sBaseSDK.SetCharInfo(parms);
	}
 
}

