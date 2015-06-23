//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.duoku.platform.DkErrorCode;
import com.duoku.platform.DkPlatform;
import com.duoku.platform.DkPlatformSettings;
import com.duoku.platform.DkPlatformSettings.GameCategory;
 
import com.duoku.platform.ui.*;
 
import com.duoku.platform.DkProtocolConfig;
import com.duoku.platform.DkProtocolKeys;
import com.duoku.platform.IDKSDKCallBack; 

 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiDuoku  extends  MoaiBaseSdk {
	 
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
		//1、SDK初始化
		DkPlatformSettings appInfo = new DkPlatformSettings(); 
		appInfo.setGameCategory(GameCategory.ONLINE_Game); 
		appInfo.setAppid(sConfigJsonObject.get("appId").asString());// 应用ID，由多酷分 配 
		appInfo.setAppkey(sConfigJsonObject.get("appKey").asString());// 应用Key，由多 
		appInfo.setOrient(DkPlatformSettings.SCREEN_ORIENTATION_LANDSCAPE);	//横屏
		DkPlatform.init(sActivity, appInfo);
		//2、初始化结束之后，设置悬浮窗回调，目前悬浮窗中仅切换帐号功能支持回调，暂时只需处理这一种情况。不设置该回调或者设置错误回调，将无法通过上线测试。
		setDkSuspendWindowCallBack();
	}
	 
	//退出游戏时
	public void onMDestroy(){
		
		DkPlatform.destroy(sActivity); 
	}

	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	   onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	

	public  void setDkSuspendWindowCallBack() {
		DkPlatform.setDKSuspendWindowCallBack(new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				//paramString为返回结果JSON串，切换帐号json串示例如下：
				//{"state_code":2005}
				int _stateCode = 0;
				try {
					JSONObject _jsonObj = new JSONObject(paramString);
					_stateCode = _jsonObj.getInt(DkProtocolKeys.FUNCTION_STATE_CODE);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				if(_stateCode == DkErrorCode.DK_CHANGE_USER){
					JsonRpcCall(Lua_Cmd_LoginOut,"");
				    //切换帐号处理逻辑，以下代码仅为示例代码，cp可根据自身需要进行操作，如重新弹出登录界面等
					 
				}
			}
		});
	}
	
	
	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 

 
		DkPlatform.invokeActivity(sActivity, getLoginIntent(), new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				//onResponse()方法中的paramString为返回结果JSON串，例如：登录成功通知内容格式如下：
				//{"user_id":"103256","user_name":"dktest","user_sessionid":"52469875215469875462545698754562","state_code":1021}
				//其中：state_code为状态码，"user_id"为用户id, "user_name"为用户的用户名，"user_sessionid"为用户的sessionid
				int _loginState = 0;
				String _userName = null;
				String _userId="";
				String _userSessionId="";
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(paramString);
					_loginState = jsonObj.getInt(DkProtocolKeys.FUNCTION_STATE_CODE);// 用户登录状态
					_userName = jsonObj.getString(DkProtocolKeys.USER_NAME);// 用户姓名
					_userId = jsonObj.getString(DkProtocolKeys.USER_ID);// 用户id
					_userSessionId = jsonObj.getString(DkProtocolKeys.USER_SESSIONID);// 用户seesionId
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (DkErrorCode.DK_LOGIN_SUCCESS == _loginState) {
					JsonObject jsonData=new JsonObject();
					jsonData.add("userName", _userName);
					jsonData.add("uid",_userId);
					jsonData.add("sessionid",_userSessionId); 
					//jsonData.get(name)
					JsonObject jsonParms=SDKFormatGateWay(_userId,jsonData);
					jsonParms.add("data", jsonData);
					JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);					
					//登录成功回调，必须正确处理
				} else if (DkErrorCode.DK_LOGIN_CANCELED == _loginState) {
					//取消登录回调，必须正确处理
					JsonRpcCall(Lua_Cmd_LoginCancel,"");
					 
				} else if (DkErrorCode.DK_NEEDLOGIN == _loginState) {
					//用户登录状态失效回调，必须正确处理					
					JsonRpcCall(Lua_Cmd_LoginOut,""); 
					
				}
			}
		});
	
 
		 return "OK";
	}
 

	public Intent getLoginIntent() {
		Bundle bundle = new Bundle();
		bundle.putInt(DkProtocolKeys.FUNCTION_CODE,DkProtocolConfig.FUNCTION_LOGIN); 
		 
		Intent intent = new Intent(sActivity, DKContainerActivity.class);
		intent.putExtras(bundle);
		
		
		return intent;
	}
	
	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=payinfoJson.get("price").asInt();
			int total=payinfoJson.get("total").asInt();
			String info=OrderNo;//payinfoJson.get("info").asString();
			int exchangeRatio=super.sConfigJsonObject.get("exchangeRatio").asInt();
			String gamebiName=super.sConfigJsonObject.get("gamebiName").asString();
            
            if(MoaiBaseSdk.sdkversion > 1 )total *= 0.01; /*************不同版本价格传入单位不同************/
            
			DkPlatform.invokeActivity(super.sActivity, getRechargeIntent(total, exchangeRatio, gamebiName, OrderNo, info), new IDKSDKCallBack() {
				@Override
				public void onResponse(String paramString) {
					try {
						JSONObject jsonObject = new JSONObject(paramString);
						
						int mStateCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_STATE_CODE);		// 状态码
						String mMessage = jsonObject.getString(DkProtocolKeys.FUNCTION_MESSAGE);		// 信息
						String mOrderId = (!jsonObject.isNull(DkProtocolKeys.FUNCTION_ORDER_ID)) 
								? jsonObject.getString(DkProtocolKeys.FUNCTION_ORDER_ID) : "" ;				// 订单号
						
						if(mStateCode == DkErrorCode.DK_ORDER_NEED_CHECK) { // 需要查询订单
							sBaseSDK.JsonRpcCall(Lua_Cmd_PaySuccess,"");
							 
						} else if (mStateCode == DkErrorCode.DK_ORDER_NOT_CHECK) {// 不需要查询订单
							sBaseSDK.JsonRpcCall(Lua_Cmd_PayCancel,""); 
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
			});
		 
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	/**
	 * 参数说明
	 * @param amount					定额支付消费金额（人民币），如购买1元的物品或者游戏币，则amount为1；若不需要定额支付则传入0
	 * @param exchangeRatio				兑换比例，如1酷币兑换100游戏币，则兑换比例为100
	 * @param strGamebiName				游戏币名称，如金币、符石、元宝等
	 * @param strOrderId				cp生成的订单号，充值结束后，多酷服务器会通知业务服务器该订单号及充值结果
	 * @param strPayDesc				支付描述，cp可以在此处自定义字段完成自己的需求，若不需要请传入”none”，请勿传null及空串
	 * @return							返回Intent对象
	 */
	// 特别注意：此处的参数二和参数三，均需在百度多酷开发者平台去进行配置，若不配置或配置错误，将会导致支付异常
	private Intent getRechargeIntent(int amount , int exchangeRatio , String strGamebiName , String strOrderId , String strPayDesc) {
		Bundle bundle = new Bundle(); 
		bundle.putInt(DkProtocolKeys.FUNCTION_CODE , DkProtocolConfig.FUNCTION_Pay); 
		bundle.putString(DkProtocolKeys.FUNCTION_AMOUNT, amount + "");  								// 金额（转换成String）
		bundle.putString(DkProtocolKeys.FUNCTION_EXCHANGE_RATIO, exchangeRatio + "");  	 	// 兑换比例 （转换成String）
		bundle.putString(DkProtocolKeys.FUNCTION_ORDER_ID , strOrderId);									// 订单号
		bundle.putString(DkProtocolKeys.FUNCTION_PAY_DESC, strPayDesc);   								// 支付描述
		bundle.putString(DkProtocolKeys.FUNCTION_GAMEBI_NAME, strGamebiName) ; 					// 游戏币名称
		Intent intent = new Intent(super.sActivity,DKPaycenterActivity.class);
		intent.putExtras(bundle);
		
		return intent;
	}
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		   
		 return "OK";	
	}
	 
 
	
	
 
}

