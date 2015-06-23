//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.Enumeration;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.tencent.msdk.api.LocationRet;
import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.MsdkBaseInfo;
import com.tencent.msdk.api.ShareRet;
import com.tencent.msdk.api.TokenRet;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.msdk.api.WGPlatformObserver;
import com.tencent.msdk.api.WGQZonePermissions;
import com.tencent.msdk.api.WakeupRet;
import com.tencent.msdk.consts.CallbackFlag;
import com.tencent.msdk.consts.EPlatform;
import com.tencent.msdk.consts.TokenType;
import com.tencent.msdk.remote.api.RelationRet;
import com.tencent.msdk.tools.Logger;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.tmgp.r01x7abs.R;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.unipay.plugsdk.IUnipayServiceCallBack;
import com.tencent.unipay.plugsdk.UnipayPlugAPI;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyyb extends MoaiBaseSdk {

	private static Tencent mTencent = null;
	private static SharedPreferences sharedPreferences = null;


	private static final String APP_ID = sConfigJsonObject.get("qqappId").asString();
	private static final String Requesturl_ = sConfigJsonObject.get("gateWayUrl_buy").asString();
	public static int ZoneId;
	private static String access_token= "";
	private static String openid = "";
	private static String pfkey = "";
	private static String pay_token = "";
	private static String pf = "";
	public static String roleName = "";
	public static boolean isLogin = false;
	public static boolean isWeChat = false;


	private static String RecordName = APP_ID;
	private static int sLoginFailedFunc = -1;
	private static UnipayPlugAPI unipayAPI = null;
	private static int unipayResId = 0;
	private static byte[] appResData = null;

	private static final String sessionId = "openid";
	private static final String sessionType = "kp_actoken";

	private static final String unipayOfferid = APP_ID;
	private static final String unipayEnv = sConfigJsonObject.get("evn").asString();

	private static final int PAYRESULT_ERROR = -1;
	private static final int PAYRESULT_SUCC = 0;
	private static final int PAYRESULT_CANCEL = 2;

	private static class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError arg0) {
			if (sLoginFailedFunc > 0) {
				MoaiLuaBridge.callLuaFunctionWithString(sLoginFailedFunc,arg0.errorMessage + "errorDetail:" + arg0.errorDetail);
			}
		}

		@Override
		public void onCancel() {
			if (sLoginFailedFunc > 0) {
				MoaiLuaBridge.callLuaFunctionWithString(sLoginFailedFunc,"onCancel");
			}
		}
	}

	// *
	// * 充值字符回调接口
	// */
	private static IUnipayServiceCallBack.Stub unipayStubCallBack = new IUnipayServiceCallBack.Stub() {

		@Override
		public void UnipayNeedLogin() throws RemoteException {
			Log.i("UnipayPlugAPI", "UnipayNeedLogin");
		}

		@Override
		public void UnipayCallBack(int resultCode, int payChannel,
				int payState, int providerState, int saveNum, String resultMsg,
				String extendInfo) throws RemoteException {
			switch (resultCode) {
			case PAYRESULT_SUCC:
				JsonRpcCall(Lua_Cmd_PayCancel, "取消支付");
				break;
			case PAYRESULT_ERROR:
				JsonRpcCall(Lua_Cmd_PayError, "支付失败");
				break;
			case PAYRESULT_CANCEL:
				JsonRpcCall(Lua_Cmd_PayCancel, "取消支付");
				break;
			}
		}
	};

	// 格式化GateWay链接
	public static JsonObject SDKFormatGateWay(String uid, JsonObject jsonData) {
		JsonObject jsonParms = new JsonObject();
		jsonParms.add("gatewayurl", sConfigJsonObject.get("gateWayUrl").asString());
		jsonParms.add("gatewaypath", sConfigJsonObject.get("gateWayPath").asString());
		jsonParms.add("uid", uid);
		jsonParms.add("data", jsonData);
		return jsonParms;
	}

	// SDK初始化
	public void SDKInit(String parms) {
		MsdkBaseInfo baseInfo = new MsdkBaseInfo();
		baseInfo.qqAppId = sConfigJsonObject.get("qqappId").asString();
		baseInfo.qqAppKey = sConfigJsonObject.get("qqappKey").asString();
		baseInfo.wxAppId = sConfigJsonObject.get("wxappId").asString();
		baseInfo.wxAppKey = sConfigJsonObject.get("wxappKey").asString();

		baseInfo.offerId = APP_ID;


		WGPlatform.Initialized(sActivity, baseInfo);
		WGPlatform.WGSetPermission(WGQZonePermissions.eOPEN_ALL); // 设置拉起QQ时候需要用户授权的项
		
		WGPlatform.WGSetObserver(new MsdkCallback());
		
		WGPlatform.handleCallback(sActivity.getIntent());

		// 绑定支付服务
		if (unipayAPI == null)
			unipayAPI = new UnipayPlugAPI(sActivity);
		unipayAPI.setCallBack(unipayStubCallBack);
		unipayAPI.bindUnipayService();
		/*********** 初始化时绑定支付服务 *************/

	}

	// 退出游戏时
	public void onMDestroy() {

		// 去绑定支付服务
		WGPlatform.onDestory(sActivity);
		unipayAPI.unbindUnipayService();
	}
	
	public void onMResume() {

		WGPlatform.onResume();
	}

	public void onMPause() {
		
	    WGPlatform.onPause();
	}
	
	public void onMNewIntent(Intent intent){
		
	WGPlatform.handleCallback(intent);
	
	}

	// 是否退出时执行
	public boolean onMPreExit(Activity activity,
			final Moai.OnFinishHandler onfinish) {

		// onfinish.callback(false); //继续游戏的代码
		onfinish.callback(true);// 退出游戏的代码
		return true;
	}

	// /打开登陆界面
	public static String V2_OpenLogin(JsonValue parms) {
//		// 登陆
//		// mAnzhiCenter.login(super.sActivity, true);
//		mTencent = Tencent.createInstance(APP_ID,sActivity.getApplicationContext());
//		mTencent.logout(sActivity);
//
//		IUiListener listener = new BaseUiListener() {
//			@Override
//			protected void doComplete(JSONObject values) {
//				// String values_ = values.toString();
//				// String ret = values.get("ret").toString();
//				JsonObject _jsonData = JsonObject.readFrom(values.toString());
//
//				access_token_ = _jsonData.get("access_token").asString();
//				openid = _jsonData.get("openid").asString();
//				pfkey = _jsonData.get("pfkey").asString();
//				pay_token = _jsonData.get("pay_token").asString();
//				pf = _jsonData.get("pf").asString();
//
//				JsonObject jsonData = new JsonObject();
//				jsonData.add("pay_token", pay_token);
//				jsonData.add("openid", openid);
//				jsonData.add("access_token", access_token_);
//				jsonData.add("pfkey", pfkey);
//				jsonData.add("pf", pf);
//
//				String uid = openid;
//
//				JsonObject jsonParms = SDKFormatGateWay(uid, jsonData);
//				jsonParms.add("data", jsonData);
//
//				JsonRpcCall(Lua_Cmd_LoginSuccess, jsonParms);
//
//			}
//		};
//		mTencent.login(sActivity, "all", listener);
		
		WGPlatform.WGLogout();
		WGPlatform.WGLoginWithLocalInfo();
		if(!isLogin)
		{
		WGPlatform.WGLogin(EPlatform.ePlatform_QQ);
		}
		
		return "OK";
	};

	/********************* 发送buy_m请求 *************************/
	/**
	 * @param url
	 *            传入的url,包括了查询参数
	 * @return 返回get后的数据
	 */
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
			conn.setConnectTimeout(10000);
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("连接错误");
		}
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "gb2312"));
			while ((line = bufReader.readLine()) != null) {
				result += line + "\n";
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

	/********************* 发送buy_m请求 *************************/

	// /打开支付界面
	public String OpenPay(JsonValue parms) {
		try {

			JsonObject _json = parms.asObject();
			JsonObject payinfoJson = _json.get("payinfo").asObject();
			int price = payinfoJson.get("price").asInt();
			String OrderNo = payinfoJson.get("orderno").asString();

            if(MoaiBaseSdk.sdkversion > 1 )price *= 0.01; /*************不同版本价格传入单位不同************/
            
			int zoneid = ZoneId;
			String rolename = roleName;

			if (pf != "") {

				JsonObject jsonData = new JsonObject();
				jsonData.add("pf", pf);
				jsonData.add("pay_token", pay_token);
				jsonData.add("openid", openid);
				jsonData.add("access_token", access_token);
				jsonData.add("pfkey", pfkey);
				jsonData.add("zoneId", zoneid);
				jsonData.add("price", price);
				jsonData.add("orderid", OrderNo);

				String json = jsonData.toString();
				String MakeUrl = Requesturl_ + "?json=" + json;

				String resultStr = sendGet(MakeUrl);
				JsonObject Jsondata = JsonObject.readFrom(resultStr);
				String Url_info = Jsondata.get("desc").asString();

				unipayResId = R.drawable.icon; // /////////////////yuanbao_ico
				unipayAPI.setEnv(unipayEnv);
				unipayAPI.setOfferId(unipayOfferid);
				unipayAPI.setLogEnable(true);

				Bitmap bmp = BitmapFactory.decodeResource(sActivity.getResources(), unipayResId);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				appResData = baos.toByteArray();

				unipayAPI.SaveGoods(openid, pay_token, sessionId, sessionType,zoneid + "_" + URLEncoder.encode(rolename), pf, pfkey,Url_info, appResData, "");

			}

		} catch (Exception e) {
			MoaiLog.i(" OpenPay is Error:" + e.getMessage());
		}

		return "OK";
	}

	// 账号切换
	public String AuthQuit(JsonValue parms) {

		mTencent.logout(sActivity);
		return "";
	}

	// /SDK会员中心
	public String OpenMemberCenter(JsonValue parms) {
		return "OK";
	}

	// /退出游戏
	public String ExitGame(JsonValue parms) {
    
		mTencent.logout(sActivity);
		return "OK";
	}

	// 角色信息
	public static String V2_SetCharInfo(JsonValue parms) {
		JsonObject jsonData = parms.asObject();


		ZoneId = jsonData.get("serverIndex").asInt();
		roleName = jsonData.get("RoleName").toString();
		// String zone_id = jsonData.get("serverIndex").asString();

		return "";
	}
	
	
    class MsdkCallback implements WGPlatformObserver {
		@SuppressWarnings("unused")
		public void OnLoginNotify(LoginRet ret) {

			switch (ret.flag) {
			case CallbackFlag.eFlag_Succ:
				// 登陆成功, 读取各种票据
				isLogin = true ;
				
				String ip = getLocalIpAddress();
				openid = ret.open_id;
				pf = ret.pf;
				pfkey = ret.pf_key;
				
				String AccessToken = "";
				long AccessTokenExpire = 0;
				String RefreshToken = "";
				long RefreshTokenExpire = 0;
				
				for (TokenRet tr : ret.token) {
					switch (tr.type) {
					case TokenType.eToken_WX_Access:
						access_token = tr.value;
						AccessTokenExpire = tr.expiration;
						break;
					case TokenType.eToken_WX_Refresh:
						access_token = tr.value;
						RefreshTokenExpire = tr.expiration;
						break;
					default:
						access_token = tr.value;
						break;
					}
				}

								pay_token = access_token;
								JsonObject jsonData = new JsonObject();
								jsonData.add("pay_token", pay_token);
								jsonData.add("openid", openid);
								jsonData.add("access_token", access_token);
								jsonData.add("pfkey", pfkey);
								jsonData.add("pf", pf);
								jsonData.add("ip", ip);
				
								String uid = openid;
				
								JsonObject jsonParms = SDKFormatGateWay(uid, jsonData);
								jsonParms.add("data", jsonData);
				
								JsonRpcCall(Lua_Cmd_LoginSuccess, jsonParms);
				
				
				break;
			case CallbackFlag.eFlag_WX_UserCancel:
			case CallbackFlag.eFlag_WX_NotInstall:
			case CallbackFlag.eFlag_WX_NotSupportApi:
			case CallbackFlag.eFlag_WX_LoginFail:
				// 登陆失败处理
				Logger.d(ret.desc);
				break;
			case CallbackFlag.eFlag_Local_Invalid:
				// 显示登陆界面
				//stopWaiting();
			default:
				break;
			}
		}

		@Override
		public void OnShareNotify(ShareRet paramShareRet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnWakeupNotify(WakeupRet paramWakeupRet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnRelationNotify(RelationRet paramRelationRet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnLocationNotify(RelationRet paramRelationRet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnLocationGotNotify(LocationRet paramLocationRet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnFeedbackNotify(int paramInt, String paramString) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String OnCrashExtMessageNotify() {
			// TODO Auto-generated method stub
			return null;
		}
	}
    
    
    public String getLocalIpAddress() {  
        String ipaddress="";
        
    try {  
        for (Enumeration<NetworkInterface> en = NetworkInterface  
                .getNetworkInterfaces(); en.hasMoreElements();) {  
            NetworkInterface intf = en.nextElement();  
            for (Enumeration<InetAddress> enumIpAddr = intf  
                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                InetAddress inetAddress = enumIpAddr.nextElement();  
                if (!inetAddress.isLoopbackAddress()) {  
                        ipaddress=inetAddress.getHostAddress().toString();  
                }  
            }  
        }  
    } catch (SocketException ex) {  
        Log.e("WifiPreference IpAddress", ex.toString());  
    }  
    return ipaddress; 
    }

}
