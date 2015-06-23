//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log; 
import android.widget.Toast;

 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
//import com.hucn.sdksample.MainActivity;

import cn.edg.sdk.HUCNSDkListener;
import cn.edg.sdk.HUCNSdk;
import cn.edg.common.utils.L;
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moailefeifan  extends  MoaiBaseSdk {
 

public static int serverid = 1;

	//SDK初始化

	 static HUCNSDkListener sdkListener = createListener();
	
	public  void SDKInit(String parms){
		
		
		//HUCNSdk.getInstance().Init(sActivity, sdkListener);
		
		
	}
	
	
	private static HUCNSDkListener createListener() {
		return new HUCNSDkListener() {

			@Override
			public void exitApp() {
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				Log.i("HUCNSDK", "退出游戏");
				System.exit(0);
			}

			@Override
			public void cancelLogin() {
				Log.i("HUCNSDK", "取消登陆");
				JsonRpcCall(Lua_Cmd_LoginCancel,"");	
			}

			@Override
			public void LoginSuccess(String userId, String sign,
					String timestamp) {
				Log.i("HUCNSDK", "登陆成功!");
				Log.i("HUCNSDK", "　　唯一用户ID="+userId);
				Log.i("HUCNSDK", "　　签名数据="+sign);
				Log.i("HUCNSDK", "　　时间戳="+timestamp);
				
				JsonObject jsonData=new JsonObject();
				jsonData.add("userid", userId);
				jsonData.add("sign",sign);
				jsonData.add("timeStamp",timestamp); 
				//jsonData.get(name)
				JsonObject jsonParms=SDKFormatGateWay(userId,jsonData);
				jsonParms.add("data", jsonData);
				JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);	
			}
		};
	}

	///打开登陆界面
	public static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		
		
		HUCNSdk.getInstance().Login(sActivity, sdkListener);

		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userInfoJson=_json.get("userInfo").asObject();
			
			String serverIndex = userInfoJson.get("serverIndex").asString();
			String orderno = payinfoJson.get("orderno").asString();
			String url = sConfigJsonObject.get("paynotify").asString();
			String price_str  = payinfoJson.get("price").asString();
			
			
			double price = Double.valueOf(price_str).doubleValue();
			
			MoaiLog.i(" serverIndex:"+serverIndex);
			MoaiLog.i(" orderno:"+orderno);
			MoaiLog.i(" url:"+url);
			MoaiLog.i(" price:"+price);
			
			HUCNSdk.getInstance().Recharge(serverIndex,orderno, url, price);
			
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public static String V2_OpenPlatform(JsonValue parms){
		HUCNSdk.getInstance().OpenWebsite("index");
		 return "OK";	
	}
	
	
	public static String V2_authQuit(JsonValue parms){
	//	HUCNSdk.getInstance().GameExit();
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		HUCNSdk.getInstance().GameExit(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				System.exit(0);	//退出
			}
		});
		 return "OK";	 
	}
 

//退出游戏时
	public void onMDestroy(){
		
		HUCNSdk.getInstance().GameExit(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				System.exit(0);	//退出
			}
		}); 
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	
	public void onMResume(){
		
		HUCNSdk.onResume(sActivity);
		
	}
	
public void onMStop(){
		
	//HUCNSdk.onPause(sActivity);
	
	}

public void onMPause(){
		
	HUCNSdk.onPause(sActivity);
	
	}

	//角色升级
	public static String V2_levelUp(JsonValue parms){
		String sLevel=parms.asString();
		int level= Integer.parseInt(sLevel);
		 
		HUCNSdk.getInstance().UpdateLevel(serverid, (short)level);//以后如果标准SDK增加了这一项，则修改这里，更新等级
		return "OK";
	} 

	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);
		

    }

    public static String V2_exitGame(JsonValue parms){
        JsonRpcCall(Lua_Cmd_GameExit,"");
        return "";
    }


	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject();
		String nickName = "";	//获取昵称
		serverid =Integer.valueOf(jsonData.get("serverIndex").asString());
		 nickName = jsonData.get("roleName").asString();
		HUCNSdk.getInstance().ServerLogin(serverid);
		
		HUCNSdk.getInstance().UpdateNickName(serverid, nickName);
		
 
		 } catch (Exception e) {
			   Log.e(" MoaiSDK", " MoaiSDK  setCharInfo is error "+e.getMessage());
 
		 }
		return "OK";
	}
	
}

