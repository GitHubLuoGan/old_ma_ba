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


//================================================================//
// MoaiCrittercism
//================================================================//
public class Moailefeifan  extends  MoaiBaseSdk {
 

public int serverid = 1;

	//SDK初始化
	
	public  void SDKInit(String parms){
	 		
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
		HUCNSdk.Login(sActivity, new HUCNSDkListener() {
			@Override
			public void LoginSuccess(String userId, String sign, String timestamp) {
				//登陆成功
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
			

			@Override
			public void cancelLogin() {
				//取消登陆
				Log.i("HUCNSDK", "取消登陆");
				JsonRpcCall(Lua_Cmd_LoginCancel,"");	

			}
			
			@Override
			public void exitApp() {
				//退出游戏
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				Log.i("HUCNSDK", "退出游戏");
				System.exit(0);
				
			}
		});
		 return "OK";
	};

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			HUCNSdk.getInstance().Recharge(payinfoJson.get("serverid").asString(),payinfoJson.get("orderno").asString(), 
			sConfigJsonObject.get("paynotify").asString(),payinfoJson.get("price").asInt());
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		HUCNSdk.getInstance().openWebsite("index");
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
		
	HUCNSdk.onStop(sActivity);
	
	}

	//角色升级
	public String LevelUp(JsonValue parms){
		String sLevel=parms.asString();
		int level= Integer.parseInt(sLevel);
		 
		HUCNSdk.getInstance().UpdateLevel(serverid, (short)level);//以后如果标准SDK增加了这一项，则修改这里，更新等级
		return "OK";
	} 



	//角色信息
	public String SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject();
		String nickName = HUCNSdk.getInstance().getNickName();	//获取昵称
		serverid = jsonData.get("serverIndex").asInt();
		if (nickName == null) nickName = jsonData.get("RoleName").asString();
		HUCNSdk.getInstance().ServerLogin(jsonData.get("serverIndex").asInt());
		
		HUCNSdk.getInstance().UpdateNickName(serverid, nickName);
		
 
		 } catch (Exception e) {
			   Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
 
		 }
		return "OK";
	}
	
}

