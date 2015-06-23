//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.UUID;
import android.content.Intent;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log; 
import android.widget.Toast;

import com.baidu.gamesdk.*;
import com.baidu.platformsdk.PayOrderInfo;
import com.baidu.gamesdk.ActivityAdPage.Listener;
import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaibaidu  extends  MoaiBaseSdk {
	 
	
	static String OrderNo="";
	static String price="";
	static String total= "";
	static String info="";
	static String name="";
	static int price_int = 0;
	private ActivityAnalytics mActivityAnalytics;
	private ActivityAdPage mActivityAdPage;
	
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
		BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
		mBDGameSDKSetting.setAppID(sConfigJsonObject.get("appId").asInt());//APPID设置
		mBDGameSDKSetting.setAppKey(sConfigJsonObject.get("appKey").asString());//APPKEY设置
		mBDGameSDKSetting.setDomain(Domain.RELEASE);//设置为正式模式
		setSuspendWindowChangeAccountListener();
		setSessionInvalidListener();
		
		mActivityAnalytics = new ActivityAnalytics(sActivity);
		mActivityAdPage = new ActivityAdPage(sActivity, new Listener(){

			public void onClose() {
				// TODO 关闭暂停页, CP可以让玩家继续游戏
//				Toast.makeText(sActivity, "继续游戏", Toast.LENGTH_LONG).show();
			}
			
		}); 
		

		BDGameSDK.init(sActivity, mBDGameSDKSetting, new IResponse<Void>(){

			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) {
				switch(resultCode){
				case ResultCode.INIT_SUCCESS:
					//初始化成功
				
					break;
					
				case ResultCode.INIT_FAIL:
				default:
					
					//初始化失败
				}
				
			}
			
		}); 
	}
	public void onMPause(){
		mActivityAnalytics.onPause();
		mActivityAdPage.onPause();
	 }      
	public void onMResume(){
		
		mActivityAnalytics.onResume();
		mActivityAdPage.onResume();
	}
	
	public void onMStop(){
		mActivityAdPage.onStop();
	}
	
	private void setSessionInvalidListener(){
		BDGameSDK.setSessionInvalidListener(new IResponse<Void>(){

			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) {
				if(resultCode == ResultCode.SESSION_INVALID){
					//会话失效，开发者需要重新登录或者重启游戏
					JsonRpcCall(Lua_Cmd_LoginOut,"");
					
					
					BDGameSDK.login(new IResponse<Void>() {
						
						@Override
						public void onResponse(int resultCode, String resultDesc, Void extraData) {
							String hint = "";
							switch(resultCode){
							case ResultCode.LOGIN_SUCCESS:
//								Intent intent = new Intent(sActivity, GameActivity.class);
//			                    startActivity(intent);
//			                    finish();
								hint = "登录成功";
								String uid = BDGameSDK.getLoginUid();
								String token = BDGameSDK.getLoginAccessToken();
								
								JsonObject jsonData=new JsonObject();
						        jsonData.add("token",token); 
						        jsonData.add("uid",uid);
						        
						        JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
						        jsonParms.add("data", jsonData);
						        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
								break; 
							case ResultCode.LOGIN_CANCEL:
								hint = "取消登录";
								break;
							case ResultCode.LOGIN_FAIL:
							default:	
								hint = "登录失败";	 
							} 
						}
					});
					
					
				}
				
			}
			
		});
	}
    
    public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	 
//退出游戏时
	public void onMDestroy(){
		mActivityAdPage.onDestroy();
		BDGameSDK.destroy();
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	public static String V2_exitGame(JsonValue parms){

		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
			BDGameSDK.isLogined();
			BDGameSDK.login(new IResponse<Void>() {
			
			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) {
				String hint = "";
				switch(resultCode){
				case ResultCode.LOGIN_SUCCESS:
//					Intent intent = new Intent(sActivity, GameActivity.class);
//                    startActivity(intent);
//                    finish();
					hint = "登录成功";
					String uid = BDGameSDK.getLoginUid();
					String token = BDGameSDK.getLoginAccessToken();
					
					JsonObject jsonData=new JsonObject();
			        jsonData.add("token",token); 
			        jsonData.add("uid",uid);
			        
			        JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
			        jsonParms.add("data", jsonData);
			        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
					break; 
				case ResultCode.LOGIN_CANCEL:
					hint = "取消登录";
					break;
				case ResultCode.LOGIN_FAIL:
				default:	
					hint = "登录失败";	 
				} 
			}
		});
		 
		 return "OK";
	};

	private void getCurrentPlayerInfo() {
		// TODO Auto-generated method stub
		
	}

	
	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		
			
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			OrderNo = payinfoJson.get("orderno").asString(); 
			price=payinfoJson.get("price").asString();
			 total=payinfoJson.get("total").asString();
			info=payinfoJson.get("info").asString();
			name=payinfoJson.get("name").asString();
			price_int = Integer.parseInt(price);
			//total = Integer.parseInt(string_total);
			
			PayOrderInfo payOrderInfo = buildOrderInfo();
			if(payOrderInfo == null){
				return "payOrderInfo is null!";
			} 
			
			BDGameSDK.pay(payOrderInfo, null, new IResponse<PayOrderInfo>(){
				  	@Override
						public void onResponse(int resultCode, String resultDesc,PayOrderInfo extraData) {
							//alertDialog.cancel();
							String resultStr = "";
							switch(resultCode){
							case ResultCode.PAY_SUCCESS://支付成功
								sBaseSDK.JsonRpcCall(Lua_Cmd_PaySuccess,"");
								resultStr = "支付成功:" + resultDesc;
								break;
							case ResultCode.PAY_CANCEL://订单支付取消
								resultStr = "取消支付";
								break;	
							case ResultCode.PAY_FAIL://订单支付失败
								sBaseSDK.JsonRpcCall(Lua_Cmd_PayError,"");
								resultStr = "支付失败：" + resultDesc;
								Toast.makeText(sActivity, resultDesc, Toast.LENGTH_LONG).show();
								break;	
							case ResultCode.PAY_SUBMIT_ORDER://订单已经提交，支付结果未知（比如：已经请求了，但是查询超时）
								resultStr = "订单已经提交，支付结果未知";
								break;	
							}
						//	Toast.makeText(getApplicationContext(), resultStr, Toast.LENGTH_LONG).show();
							 
						}
			});
			
		
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
			
			return "ok";
	}

	public static PayOrderInfo buildOrderInfo(){
		String cpOrderId = OrderNo;//CP订单号
		String goodsName = name;
		String totalAmount = total;//支付总金额 （以分为单位）  
		int ratio = 10;//该参数为非定额支付时生效 (支付金额为0时为非定额支付,具体参见使用手册)
		String extInfo = OrderNo;//扩展字段，该信息在支付成功后原样返回给CP
		
		if(TextUtils.isEmpty(totalAmount)){
			totalAmount = "0";
		}
		
		PayOrderInfo payOrderInfo = new PayOrderInfo();
		payOrderInfo.setCooperatorOrderSerial(cpOrderId);
		payOrderInfo.setProductName(goodsName); 
		long p = Long.parseLong(totalAmount); 
		payOrderInfo.setTotalPriceCent(p*100);//以分为单位
		payOrderInfo.setRatio(ratio);
		payOrderInfo.setExtInfo(extInfo);//该字段将会在支付成功后原样返回给CP(不超过500个字符)
		
		return payOrderInfo;
	}
	//账号切换

	
	public static String V2_authQuit(JsonValue parms){
		
		BDGameSDK.logout();
		BDGameSDK.destroy();
		
		 return "ok";
	}
	
	//百度切换
	private void setSuspendWindowChangeAccountListener(){//设置切换账号事件监听（个人中心界面切换账号）
		BDGameSDK.setSuspendWindowChangeAccountListener(new IResponse<Void>(){

			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) { 
				 switch(resultCode){
				 case ResultCode.LOGIN_SUCCESS:
					 //TODO 切换账号成功（CP可根据情况更新游戏数据）
				JsonRpcCall(Lua_Cmd_LoginOut,"");
				String uid = BDGameSDK.getLoginUid();
				String token = BDGameSDK.getLoginAccessToken();
				
				JsonObject jsonData=new JsonObject();
		        jsonData.add("token",token); 
		        jsonData.add("uid",uid);
		        
		        JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
		        jsonParms.add("data", jsonData);
		        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
					 break;
				 case ResultCode.LOGIN_FAIL:
					//TODO 切换账号失败（此时用户登录处于注销状态）
					 break;
				 case ResultCode.LOGIN_CANCEL:					 
					//TODO 取消切换账号（此时用户登录处于前一个登录状态）
				 default:
					 //切换账号失败则当前登录处于注销状
					 
				 } 
			}
			
		});
	}
	
	

	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){

	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		BDGameSDK.logout();

		return "ok";
	}
 
	
	//角色信息
	public static String V2_SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		return "";
	}catch (Exception e) {
        Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
        
    }
   return "OK";
	
	}
}

