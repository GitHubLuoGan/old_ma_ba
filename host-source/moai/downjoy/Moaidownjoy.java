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
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.downjoy.CallbackListener;
import com.downjoy.Downjoy;
import com.downjoy.DownjoyError;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaidownjoy extends  MoaiBaseSdk {
	 
	public static Downjoy sDownjoy = null;
	public static String merchantId = "";
	public static String appId = "";
	public static String serverSeqNum = "";
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
		merchantId = sConfigJsonObject.get("merchantId").asString();
		appId = sConfigJsonObject.get("appId").asString();
		serverSeqNum = sConfigJsonObject.get("serverSeqNum").asString();
		appKey = sConfigJsonObject.get("appKey").asString();
		
		sDownjoy = Downjoy.getInstance(sActivity, merchantId, appId, serverSeqNum, appKey);
		sDownjoy.showDownjoyIconAfterLogined(true);
		sDownjoy.setRestartOnSwitchAccount(false);
	}

//退出游戏时
	public void onMDestroy(){
	//	super.onDestroy();
		 if (sDownjoy  !=  null) {  
			 sDownjoy.destroy(); 
			 sDownjoy  = null;  
	        }
			
	}
	
	public void onMResume(){
	//	super.onResume();
		sDownjoy.resume(sActivity);
		
	}
	
	public void onMPause(){
//		super.onPause();
		sDownjoy.pause();
	}
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);
		

    }
	
	
	
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		sDownjoy.openLoginDialog(sActivity,new CallbackListener() 
	    {

                @Override
                public void onLoginSuccess(Bundle bundle) 
			    {
                    String memberId=bundle.getString(Downjoy.DJ_PREFIX_STR + "mid");
                    String token=bundle.getString(Downjoy.DJ_PREFIX_STR + "token");

					
					      JsonObject jsonData=new JsonObject();
			              jsonData.add("token", token);
			              jsonData.add("djMemberID",memberId);
			              
			              JsonObject jsonParms=SDKFormatGateWay(memberId,jsonData);
			              jsonParms.add("data", jsonData);		    
			              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
					
			    }

                @Override
                public void onLoginError(DownjoyError error) 
                {
                    int errorCode=error.getMErrorCode();
                    String errorMsg=error.getMErrorMessage();

                    //Util.alert(sActivity, "onLoginError:" + errorCode + "|" + errorMsg);
					synchronized ( Moai.sAkuLock ) 
					{
						JsonRpcCall(Lua_Cmd_LoginOut,"注销登录");
					}
                }

                @Override
                public void onError(Error error) 
			    {
                    //String errorMessage=error.getMessage();
                    //Util.alert(sActivity, "onError:" + errorMessage);
					String errorMessage = error.getMessage();
					synchronized ( Moai.sAkuLock ) 
					{
						JsonRpcCall(Lua_Cmd_LoginCancel,"注销登录");
					}
	            }

                @Override  
                public void onSwitchAccountAndRestart() {   
                	final Intent intent = sActivity.getPackageManager().getLaunchIntentForPackage(sActivity.getPackageName());  
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
                    sActivity.startActivity(intent); 
                	//JsonRpcCall(Lua_Cmd_LoginOut,"注销登录");
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
			float price=Float.parseFloat(payinfoJson.get("price").asString());
			String info=payinfoJson.get("name").asString();
			
			float Price=0;
			//if(MoaiBaseSdk.sdkversion>1){Price = price / 100;}
			
			
			String orderNo= sDownjoy.openPaymentDialog(sActivity, price, info, OrderNo, new CallbackListener() 
		    {

		    @Override
		    public void onPaymentSuccess(String orderNo) 
		    {
			//Util.alert(sActivity, "payment success! \n orderNo:" + orderNo);
			synchronized ( Moai.sAkuLock ) 
			{
				JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功");
			}
		    }

		    @Override
		    public void onPaymentError(DownjoyError error, String orderNo) 
		    {
			int errorCode=error.getMErrorCode();
			String errorMsg=error.getMErrorMessage();

			
			synchronized ( Moai.sAkuLock ) 
			{
				JsonRpcCall(Lua_Cmd_PayError,"支付失败");
			}
		    }

		    @Override
		    public void onError(Error error) 
		    {
			//Util.alert(sActivity, "onError:" + error.getMessage());
			String errorMessage = error.getMessage();
			synchronized ( Moai.sAkuLock ) 
			{
				JsonRpcCall(Lua_Cmd_PayError,"支付失败");
			}
		    }
		});
			
		 
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	public static void openMemberCenterDialog ( ) 
	{

	    if (sDownjoy == null || sActivity == null) 
	    {
		return;
	    }

	    sDownjoy.openMemberCenterDialog(sActivity, new CallbackListener() {

            @Override
            public void onSwitchAccountAndRestart() {
                // TODO Auto-generated method stub
            	JsonRpcCall(Lua_Cmd_LoginOut,"注销登录");

            }

            @Override
            public void onError(Error error) {
                String errorMessage = error.getMessage();
            }
        }, false);

	}
	
	public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}
	
	public static void logout () 
	{


	    sDownjoy.logout(sActivity, new CallbackListener() 
		{

		@Override
		public void onLogoutSuccess() 
		{
		    	JsonRpcCall(Lua_Cmd_LoginOut,"注销登录");
		}

		@Override
		public void onLogoutError(DownjoyError error) 
		{
		    int errorCode=error.getMErrorCode();
		    String errorMsg=error.getMErrorMessage();
		    JsonRpcCall(Lua_Cmd_LoginCancel,"注销登录");
		}

		@Override
		public void onError(Error error) 
		{
		    //Util.alert(sActivity, "onError:" + error.getMessage());
		    String errorMessage = error.getMessage();
		    JsonRpcCall(Lua_Cmd_LoginCancel,"注销登录");
		}
	    });
	}
	///SDK会员中心 
	public static String V2_OpenPlatform(JsonValue parms){
		  
		openMemberCenterDialog();
		 return "OK";	
	}
	 
	 public String Exist(JsonValue parms){
	    String data = parms.asString();

	    if(data.equals(Java_Cmd_OpenMemberCenter))return "1";
	    
	    else return "0";
	    }
	
	///退出游戏
	public String ExitGame(JsonValue parms){
		
		logout();
		 return "OK";	 
	}
	//账号切换
	public static String V2_authQuit(JsonValue parms){
		logout();
		return "";
	}
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		
	}
		catch(Exception e){
			
		MoaiLog.e("SetCharInfo Error::::");
		
		}
	
		return "";
	}
}

