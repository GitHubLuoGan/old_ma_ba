//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log; 
import android.view.Display;
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.xgdata.XGGame;
import com.bbox.api.*;
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaipma  extends  MoaiBaseSdk {
	 
	public static String model = ""; 
	public static JsonObject copConfig ;
	public static String PMAGameId = "";
	public static String PMAChannelId = "";
	public static String Uid = "";
	public static String number = "";
	public static String fee = "";
	public static String sdkid = "2";
	public static String itemstype = LogoActivity.itemstype;
	public static String gifttype = LogoActivity.gifttype;
	public static String copAddr = LogoActivity.copAddr;
	public static JsonValue staticParm ;
	public static boolean isPayFail = true;

	public  String payChannel(int type, JsonValue parms) throws IOException{
		
		switch(type){
		
		case 1:
			
			
			Moaimmsms.V2_OpenPay(parms);
			break;
			
		case 2:
			Moaiunipay.V2_OpenPay(parms);
			break;
			
		case 3:
			Moaictccallsms.V2_OpenPay(parms);
			break;
			
		default:
			break;


		}
		
			return "0";
		
		}

	public  static String getBillingUrl(String ok){
		
		String goodsId = GetJsonVal(LogoActivity.goodsInfoJson,number,"39");
		
		String url = "";
		url = copAddr + "log.php?" + "gameid=" + LogoActivity.copGameId + "&qudao=" + LogoActivity.copChannelId + "&ver=" + LogoActivity.version;
		url = url + "&paychannel=" + goodsId +"&billing=" + fee +"&tollgate=0" + "&ok=" +ok + "&uid=" + Uid + "&type=" + ok + "&sdkid=" + sdkid ;
		
		return sendGet(url);
		
	}
	
	public  static String getCommitScoreUrl(String star, String level, String scroe, String succ){
		
		String url = "";
		url = copAddr + "logscore.php?" + "gameid=" + LogoActivity.copGameId + "&qudao=" + LogoActivity.copChannelId + "&ver=" + LogoActivity.version;
		url = url + "&uid=" + Uid + "&score=" + scroe + "&succ=" + succ + "&level=" + level + "&star=" + star;
		
		return sendGet(url);
		
	}


	public void ResultChannelInfo(){
    
    
    JsonObject channelInfo=new JsonObject();

    if(gifttype.equals("6")){channelInfo.add("recBuyStyle", 1);}
       
    JsonRpcCall(Lua_Cmd_ResultChannelInfo,channelInfo);  
}
	
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
	public   void SDKInit(String parms){	
		
		try {
			
			InputStreamReader inputReader;
			inputReader = new InputStreamReader(sActivity.getResources().getAssets().open("cConfig_1.json"),"GBK");
			MoaiBaseSdk.sConfigJsonObject=JsonObject.readFrom(inputReader);
			Moaimmsms.pmaInitmmsms();
			
			
			inputReader = new InputStreamReader(sActivity.getResources().getAssets().open("cConfig_2.json"),"GBK");
			MoaiBaseSdk.sConfigJsonObject=JsonObject.readFrom(inputReader);
			Moaiunipay.pmaInitUnipay("");
			
			inputReader = new InputStreamReader(sActivity.getResources().getAssets().open("cConfig_3.json"),"GBK");
			MoaiBaseSdk.sConfigJsonObject=JsonObject.readFrom(inputReader);
			Moaictccallsms.pmaInitCtcc("");
			
			
			
			inputReader = new InputStreamReader(sActivity.getResources().getAssets().open("cConfig.json"),"GBK");
			MoaiBaseSdk.sConfigJsonObject=JsonObject.readFrom(inputReader);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		copConfig = JsonObject.readFrom(LogoActivity.configInfo);
		model = String.valueOf(copConfig.get("mode").asInt());
		gifttype = String.valueOf(copConfig.get("gifttype").asInt());
		itemstype = String.valueOf(copConfig.get("itemstype").asInt());
		PMAGameId = sConfigJsonObject.get("PMAGameId").asString();
		PMAChannelId= sConfigJsonObject.get("PMAChannelId").asString();
		copAddr = sConfigJsonObject.get("copAddr").asString();
		
		XGGame.init(sActivity, PMAGameId, PMAChannelId);
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
	
	public static String sendGet(String url) {
		String result = "";
		// String
		URL realURL = null;
		URLConnection conn = null;
		BufferedReader bufReader = null;
		String line = "";
		try {
			realURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("url 格式错误");
		}
		try {
			conn = realURL.openConnection();
			// 设置连接参数...conn.setRequestProperty("xx", "xx");
			conn.setConnectTimeout(10000); // 10s timeout
			// conn.setRequestProperty("accept", "*/*");
			// conn.setRequestProperty("", "");
			conn.connect(); // 连接就把参数送出去了 get方法
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接错误");
		}
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("读取数据错误");
		} finally {
			// 释放资源
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	///打开支付界面
	public String OpenPay(final JsonValue parms)
	{ 
		staticParm = parms;
		
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
			number=GetJsonVal(payinfoJson,"number","0");
			 
			
			int price=GetJsonValInt(payinfoJson,"price",0); 
			
			int total=GetJsonValInt(payinfoJson,"total",0);  
			 
			String info=GetJsonVal(payinfoJson,"info","0");
			Uid=GetJsonVal(userinfoJson,"roleId","0"); 
			 
			int PMAdirection=MoaiBaseSdk.GetJsonValInt(sConfigJsonObject, "PMADirection", 1);
			BillInterface bill=new BillInterface(sActivity,PMAGameId,PMAChannelId,PMAdirection);


		  
			//自定义回调函数
			BillCallBack bcb= new BillCallBack(){
				String updateBillData = "";
				public void onBillingSuccess() {
					JsonObject jsonParms=new JsonObject();
					jsonParms.add("code", 1);
					jsonParms.add("msg", "充值成功");
					jsonParms.add("payData", parms.asObject());
					//计费成功
					
					updateBillData = getBillingUrl("1");
					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
	             //接下来可以做账户充值验证工作
				}		
				// 异常错误				 
				public void onBillingError (String msg) {

//					JsonObject jsonParms=new JsonObject();
//					jsonParms.add("code",-2);
//					jsonParms.add("msg", "未知的支付");
//					jsonParms.add("payData", parms.asObject());
//					//计费成功
//					
//					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
					
				}         
	           // 计费超时			 
			   public void onBillingTimeOut(){

//				   JsonObject jsonParms=new JsonObject();
//					jsonParms.add("code",-2);
//					jsonParms.add("msg", "支付超时");
//					jsonParms.add("payData", parms.asObject());
//					//计费成功
//					
//					JsonRpcCall(Lua_Cmd_PayResult,jsonParms);

			    }

			@Override
			public void onBillingFailed(String arg0) {

//				JsonObject jsonParms=new JsonObject();
//				jsonParms.add("code",-1);
//				jsonParms.add("msg", "支付失败");
//				jsonParms.add("payData", parms.asObject());
//				//计费成功
//				
//				JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
				
			}
			};
			
		 
	        if(checkNetworkAvailable(MoaiBaseSdk.sActivity))  
	        {  
	        	
	        	 // bill.billing(sActivity, price,OrderNo,UserId ,number, info, OrderNo, "", bcb);
	        	 CPConsumer consumer=new CPConsumer(price);
	        	 fee = String.valueOf(price);
	             consumer.CPConsumerID = number;
	             consumer.CPConsumerName=info;
	             consumer.CPUserID=Uid;
	             consumer.CPOrderID=OrderNo;
	             consumer.CPExtraStr=OrderNo;
	          //   bill.newBilling(sActivity, consumer, bcb);
	             
	             
	             bill.newBilling(MoaiBaseSdk.sActivity,
	            		 	consumer, 
	            		 false, //是否显示等待框
	            		 false, //是否显示资费提示
	            		 false,  //是否需要第三方计费（支付宝，易宝……），
	            		 0, //重试次数
	            		 1,	
	            		 bcb) ;
	            //当前有可用网络  
	             
	         //    Moaimmsms.showProgress("支付请求发送中,请稍候！");
					
					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.currentThread().sleep(5000);
//								Moaimmsms.hideProgress();
							} catch (InterruptedException e) {
								
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//毫秒   
							try {
								
								//运营商扣费
								payChannel(MoaiTool.cardType,staticParm);
								
					        } catch (Exception e) {  
					            e.printStackTrace();  
					        }  
					        
						}
					}).start();
	             
	             
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
	
	public static String V2_missionData(JsonValue parms){
		
		//{"scroe":0,"level":1,"succ":true,"star":3}
		JsonObject jsonData = parms.asObject();
		String star = String.valueOf(jsonData.get("star").asInt());
		String level = String.valueOf(jsonData.get("level").asInt());
		String scroe = String.valueOf(jsonData.get("star").asInt());
		boolean isSuccess = jsonData.get("succ").asBoolean();
		String succ = "0";
		
		if(isSuccess){succ = "1";}
		
		
		
		return getCommitScoreUrl(star ,level ,scroe ,succ);
	}
 
}

