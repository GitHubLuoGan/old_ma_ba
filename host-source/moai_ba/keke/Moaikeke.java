//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.nearme.gamecenter.open.api.ApiCallback;
import com.nearme.gamecenter.open.api.GameCenterSDK;
import com.nearme.gamecenter.open.api.GameCenterSettings;
import com.nearme.gamecenter.open.api.PayInfo;
import com.nearme.gamecenter.open.api.RatePayInfo;
import com.nearme.gamecenter.open.core.util.Util;
import com.nearme.gamecenter.open.*;
import com.nearme.oauth.model.UserInfo;

 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaikeke  extends  MoaiBaseSdk {
	 
	 
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
		 
		// 测试用的appkey和secret
		// TODO 这个里的为测试key和secret，请务必替换为正式的！
		GameCenterSettings gameCenterSettings = new GameCenterSettings(
				sConfigJsonObject.get("appKey").asString(), sConfigJsonObject.get("appSecret").asString()) {

			@Override
			public void onForceReLogin() {
				// sdk由于某些原因登出,此方法通知cp,cp需要在此处清理当前的登录状态并重新请求登录.
				// 可以发广播通知页面重新登录
				JsonRpcCall(Lua_Cmd_LoginOut,"");
			}
			
			@Override 
			public void onForceUpgradeCancel() {
				// 游戏自升级，后台有设置为强制升级，用户点击取消时的回调函数。
				// 若开启强制升级模式 ，  一般要求不更新则强制退出游戏并杀掉进程。
				 System.exit(0);
			}
		};
		// TODO for test old
//		AccountAgent.useNewApi = true;
		GameCenterSettings.isDebugModel = true;// 测试log开关
		GameCenterSettings.isOritationPort = true;// 控制SDK activity的横竖屏 true为竖屏
		GameCenterSDK.init(gameCenterSettings, super.sActivity);
		
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
		/////////////////////////////////////////////////////
	
		GameCenterSDK.setmCurrentContext(sActivity); 
		ApiCallback callback = new ApiCallback() { 
			@Override 
			public void onSuccess(String content, int code){ 
				 
				
				/////////////////成功后处理
				
				
				GameCenterSDK.getInstance().doGetUserInfo(
						
						new ApiCallback(){
							public void onSuccess(String content, int code) 
		                    {
								
								final String data = GameCenterSDK.getInstance().doGetAccessToken();
								final String token_key = data.split("&")[0].split("=")[1];
								final String token_secret = data.split("&")[1].split("=")[1];
								
								UserInfo ui = new UserInfo(content);  
							      
							      String uid  = ui.id;
							      String Sessionid = ui.username;
							      String nickName = ui.nickname;
							      String Token    = token_key;
							      String tokenSecret    = token_secret;
							      
							      
							      JsonObject jsonData=new JsonObject();
					              jsonData.add("uid", uid);
					              jsonData.add("sessionId",Sessionid);
					              jsonData.add("nickName",nickName);
					              jsonData.add("token",Token);
					              jsonData.add("tokenSecret",tokenSecret);
					              
					              JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
					              jsonParms.add("data", jsonData);
					              
					              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
							}
							

							public void onFailure(String content,int code){
								/////失败处理
								
								JsonRpcCall(Lua_Cmd_LoginFailed,""); 
							}
							
						},sActivity);
				
				////////////////////////////
				
				  	      
			}
			  @Override 
			public void onFailure(String content, int code){   
			//获取失败后的处理 
				  JsonRpcCall(Lua_Cmd_LoginCancel,""); 
			  }
			  };
		GameCenterSDK.getInstance().doLogin(callback, sActivity);
		
		//////////////////////////////////////////////////////
		 return "OK";
	};
	
	public void onMResume(){
		
		
		ApiCallback callback = new ApiCallback() { 
			@Override 
			 public void onSuccess(String content, int code) { 
				Util.makeToast("切换账号成功",sActivity);
				JsonRpcCall(Lua_Cmd_LoginOut,"");
		      } 
		      @Override 
		      public void onFailure(String content, int code) { 
		    	  Util.makeToast("切换账号失败",sActivity); 
		      }
		      
		};
		
        GameCenterSDK.getInstance().doShowSprite(callback,sActivity);
		
	}
	
	 public void onMPause(){
		 
		 GameCenterSDK.getInstance().doDismissSprite(sActivity);
		 
	 } 
	 

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price  = payinfoJson.get("price").asInt();
			int total  = payinfoJson.get("total").asInt();
			int amount = payinfoJson.get("price").asInt();
			String info=payinfoJson.get("info").asString();
			
			final ApiCallback kebiPayment = new ApiCallback() {
				@Override
				public void onSuccess(String content, int code) {
					Util.makeToast("消耗可币成功",
							sActivity);
					//refreshHeadView();
					
					JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功"); 
				}

				@Override
				public void onFailure(String content, int code) {
					Util.makeToast("消耗可币失败", sActivity);
					JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");  ////////////////////////向cp server通知购买结果
				}
			};
			
			if(MoaiBaseSdk.sdkversion > 1 )amount *= 0.01; /*************不同版本价格传入单位不同************/
	
				final PayInfo payInfo = new PayInfo(System.currentTimeMillis()
						+ new Random().nextInt(1000) + "", OrderNo, amount*100);
				payInfo.setProductDesc(info);
				payInfo.setProductName(info);
				payInfo.setCallbackUrl(sConfigJsonObject.get("callBack").asString());
				GameCenterSDK.getInstance().doNormalKebiPayment(kebiPayment, payInfo,
						sActivity);			
		 
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
	//角色设置角色信息
	public String SetCharInfo(JsonValue parms){
		
		   JsonObject jsonData=parms.asObject();  
		    int gameId = sConfigJsonObject.get("appId").asInt();
		    StringBuilder extendInfo = new StringBuilder().
		    append("gameId=").append(gameId).                            
			append("&service=").append(jsonData.get("serverIndex").asString()).
			append("&role=").append(jsonData.get("RoleId").asString()).
			append("&grade=").append(jsonData.get("Rolelv").asString());
		    
		   // extendInfo = String(extendInfo);
				
			GameCenterSDK.getInstance().doSubmitExtendInfo(new ApiCallback() {
			
			@Override
			public void onSuccess(String content, int code) {
				// TODO Auto-generated method stub
				Util.makeToast("提交角色信息成功",
						sActivity);
			}
			
			@Override
			public void onFailure(String content, int code) {
				// TODO Auto-generated method stub
				Util.makeToast("提交角色信息失败" ,sActivity);
			}
		}, extendInfo.toString(), sActivity);
		
		return "";
	}
	
	
}


