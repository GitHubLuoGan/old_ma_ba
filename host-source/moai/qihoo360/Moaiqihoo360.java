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
import android.text.TextUtils;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback; 
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;




 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiqihoo360  extends  MoaiBaseSdk {
	 
	public static String appId = sConfigJsonObject.get("appId").asString();
	public static String appKey = sConfigJsonObject.get("appKey").asString();
	public static String appSecret = sConfigJsonObject.get("appSecret").asString();
	protected static final String RESPONSE_TYPE_CODE = "code";
	public static String userId = "";
	public static String accessToken = "";
	public static String userName = "";
	public static String userId_us = "";
	 
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
		 
		 Matrix.init(sActivity);
	}
	
	/***************************************************/
	 
    public static void openPaymentDialog(String accessToken, String qihooUserId,
    									 String ammount, String exchangeRate, 
	    								 String productName, String productId,
	    								 String notifyUri,
	    								 String appName, String appUserName, String appUserId,
	    								 String appExt1, String appExt2,
	    								 String appOrderId)
    {
    	
    	Bundle bundle = new Bundle();
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, true);

        bundle.putString(ProtocolKeys.ACCESS_TOKEN, accessToken);
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, qihooUserId);
        bundle.putString(ProtocolKeys.AMOUNT, ammount);
        bundle.putString(ProtocolKeys.RATE, exchangeRate);
        bundle.putString(ProtocolKeys.PRODUCT_NAME, productName);
        bundle.putString(ProtocolKeys.PRODUCT_ID, productId);
        bundle.putString(ProtocolKeys.NOTIFY_URI, notifyUri);
        bundle.putString(ProtocolKeys.APP_NAME, appName);
        bundle.putString(ProtocolKeys.APP_USER_NAME, appUserName);
        bundle.putString(ProtocolKeys.APP_USER_ID, appUserId);
        bundle.putString(ProtocolKeys.APP_EXT_1, appExt1);
        bundle.putString(ProtocolKeys.APP_EXT_2, appExt2);
        bundle.putString(ProtocolKeys.APP_ORDER_ID, appOrderId);
        bundle.putStringArray(ProtocolKeys.PAY_TYPE, null);
		bundle.putString(ProtocolKeys.DEFAULT_PAY_TYPE,"");
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);
        Intent intent = new Intent(sActivity, ContainerActivity.class);
        intent.putExtras(bundle);
        
        Matrix.invokeActivity(sActivity, intent, sPayCallback);
    }
    
    // 登录、注册的回调
    private static IDispatcherCallback sLoginCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            String authorizationCode = parseAuthorizationCode(data);
            synchronized ( Moai.sAkuLock ) 
			{
            	String token = "";
            	//AKUNotifyQihoo360LoginFinish(data, authorizationCode);
            	try {
					JSONObject data360 = new JSONObject(data);
					token = data360.optString("data");
					JSONObject forToken = new JSONObject(token);
					
					accessToken = forToken.optString("access_token");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	
            	JsonObject jsonData=new JsonObject();
	//              jsonData.add("authcode", authorizationCode);
	              jsonData.add("json",data);
	              
	              JsonObject jsonParms=SDKFormatGateWay("360",jsonData);
	              jsonParms.add("data", jsonData);
	              
	              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
	      
			}
        }
    };
    // 切换账号的回调
    private static IDispatcherCallback sAccountSwitchCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            String authorizationCode = parseAuthorizationCode(data);
            synchronized ( Moai.sAkuLock ) 
			{
            	 JsonObject jsonData=new JsonObject();
	              jsonData.add("json",data);
	              
	              JsonObject jsonParms=SDKFormatGateWay("360",jsonData);
	              jsonParms.add("data", jsonData);
	              
	              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
			}
        }
    };
    // 支付的回调
    private static IDispatcherCallback sPayCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
      
        	synchronized ( Moai.sAkuLock ) 
			{
        		JsonRpcCall(Lua_Cmd_PaySuccess,"");
			}
        }
    };
    /**
     * 从Json字符中获取授权码
     * 
     * @param data Json字符串
     * @return 授权码
     */
    private static String parseAuthorizationCode(String data) {
        String authorizationCode = null;
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject json = new JSONObject(data);
                int errCode = json.getInt("errno");
                if (errCode == 0) {
                    // 只支持code登陆模式
                    JSONObject content = json.getJSONObject("data");
                    if (content != null) {
                        // 360SDK登录返回的Authorization Code（授权码，60秒有效）。
                        authorizationCode = content.getString("code");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return authorizationCode;
    }
    
    private static Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent) {

        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 界面相关参数，360SDK登录界面背景是否透明。
        bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

        // *** 以下非界面相关参数 ***

        // 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
        bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

        // 必需参数，使用360SDK的登录模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);

        Intent intent = new Intent(sActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
    
    /***
     * 生成调用360SDK切换账号接口的Intent
     * 
     * @param isLandScape 是否横屏
     * @param isBgTransparent 是否背景透明
     * @return Intent
     */
    private static Intent getSwitchAccountIntent(boolean isLandScape, boolean isBgTransparent) {

        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 界面相关参数，360SDK登录界面背景是否透明。
        bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

        // *** 以下非界面相关参数 ***

        // 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
        bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);

        // 必需参数，使用360SDK的切换账号模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

        Intent intent = new Intent(sActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
    
    /***
     * 生成调用360SDK论坛接口的Intent
     * 
     * @param isLandScape
     * @return Intent
     */
    private static Intent getBBSIntent(boolean isLandScape) {

        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        // 必需参数，使用360SDK的论坛模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_BBS);

        Intent intent = new Intent(sActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
	
	/***************************************************/
	
	public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}

//退出游戏时
	public void onMDestroy(){
		
		Matrix.destroy(sActivity);
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
       
		Intent intent = getLoginIntent(true, true);
        Matrix.invokeActivity(sActivity, intent, sLoginCallback);
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=Integer.valueOf(payinfoJson.get("price").asString());
			//int total=payinfoJson.get("total").asInt();
			String info=payinfoJson.get("name").asString();
			
			price = price*100;
			
			String Price = String.valueOf(price);
			
			openPaymentDialog(accessToken, userId,
					 Price, 
					 "1", 
					 info, 
					 "1000",
					 sConfigJsonObject.get("paynotifyUrl").asString(),
					 sConfigJsonObject.get("appName").asString(), 
					 userName,
					 OrderNo,
					 OrderNo, 
					 OrderNo,
					 OrderNo);
			
			  
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	//切换账号
	public static String V2_authQuit(JsonValue parms){
		
		Intent intent = getSwitchAccountIntent(true, true);
        Matrix.invokeActivity(sActivity, intent, sAccountSwitchCallback);
		return "";
	}
	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		   
	 
		 return "OK";	
	}
	//是否存在
	public String Exist(JsonValue parms){
		String data =parms.asString();
		if(data.equals(Java_Cmd_OpenBBS)){
			return "1";
		}
		else
	    	return "0";	
	    }
	///打开BBS 
	public static  String V2_OpenPlatform(JsonValue parms){
		
		Intent intent = getBBSIntent(true);
        Matrix.invokeActivity(sActivity, intent, null);
        
		return ""; 		 
	}
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);
		

    }
	
	///退出游戏
	public String ExitGame(JsonValue parms){
			 
		 return "OK";	 
	}
 //从gateway获取更多信息
	
	public String ReceiveInfo(JsonValue parms){
		 
		JsonObject _json = parms.asObject();
		 userId = _json.get("uid").asString();
		 accessToken = _json.get("access_token").asString();
		
		 return "OK";	 
	}
	
	
	public static String V2_sendMoreInfo(JsonValue parms){
		JsonObject _json = parms.asObject();
		 userId = _json.get("uid").asString();

		return "";
	}
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		userName = jsonData.get("roleName").toString();
		userId_us = jsonData.get("roleId").asString();
		}
		catch(Exception e){
			
			MoaiLog.i(" SetCharInfo is Error:"+e.getMessage());
		}
	return "";
	}	
 
}

