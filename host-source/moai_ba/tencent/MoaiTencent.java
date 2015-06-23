//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
 
import cn.ultralisk.gameapp.mysg.tencent.R;

import com.tencent.open.SocialConstants;
import com.tencent.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.unipay.plugsdk.IUnipayServiceCallBack;
import com.tencent.unipay.plugsdk.UnipayPlugAPI;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiTencent{

	private static Activity sActivity 			= null;
	private static Tencent mTencent 			= null;
	private static Weibo mWeibo = null;
	
	private static SharedPreferences sharedPreferences = null;
	
	private static Dialog mProgressDialog;
	
	
	private static final String APP_ICON_URL    = "http://mysgup01.ultralisk.cn:10301/tencentShare/shareWeibo.png";  
	private static final String APP_TARGET_URL  = "http://www.qq.com/news/1.html";
	
	private static final String APP_ID 			= "1101239975";
	// 设置是否自动登陆 
	private static final boolean isFreeLogin 	= true;
	
	private static String RecordName 			= APP_ID;
	// 登陆回调方法
	private static int sLoginSuccessFunc 		= -1;
	private static int sLoginOutFunc     		= -1;
	private static int sLoginFailedFunc  		= -1;
	private static int sPaySuccessFunc 			= -1;
	private static int sPayFailedFunc 			= -1;
	private static int sChargeDataFunc           = -1;
	private static int sWeiboShareSuccessFunc  = -1;
	private static int sWeiboShareFailedFunc   = -1;
	// 微博分享时的扩展信息 用于分享成功回传
	private static String sWeiboShareExtInfo      = "";
	// 用于自动登陆的相关
	private static String sLoginOpenid 			= "";
	private static String access_token 			= "";
	private static String expires_in 			= "";
	// 登陆校验json字符串
	private static JSONObject sLoginJson        = null;
	// 支付相关定义
	private static UnipayPlugAPI unipayAPI 		= null;
	private static int    unipayResId        	= 0;
	private static byte[] appResData 		 	= null;
	
	//用户ID类型,应用根据自己的登录类型传递,如uin、openid、hy_gameid、uin
	private static final String sessionId   = "openid";
	//用户登录态类型,应用根据自己的登录类型传递,如skey、kp_actoken、wc_actoken、sid
	private static final String sessionType = "kp_actoken";	
	
	private static final String unipayOfferid         = APP_ID;
	private static final String unipayEnv 		    = "test";
	
	
	//支付流程失败
	private static final int PAYRESULT_ERROR        = -1;
	//支付流程成功
	private static final int PAYRESULT_SUCC         = 0;
	//用户取消
	private static final int PAYRESULT_CANCEL       = 2;

	
	// 登陆相关的接口
	private static class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError arg0) {
			if (sLoginFailedFunc > 0 ){
				MoaiLuaBridge.callLuaFunctionWithString(sLoginFailedFunc, arg0.errorMessage
                    + "errorDetail:" + arg0.errorDetail);
			}
		}

		@Override
		public void onCancel() {
			if (sLoginFailedFunc > 0 ){
				MoaiLuaBridge.callLuaFunctionWithString(sLoginFailedFunc,"onCancel");
			}
		}
	}
	
	public static final void showResultDialog(Context context, String msg,
			String title) {
		if(msg == null) return;
		String rmsg = msg.replace(",", "\n");
		Log.d("Util", rmsg);
		new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg)
				.setNegativeButton("知道了", null).create().show();
	}
	
	public static final void showProgressDialog(Context context, String title,
			String message) {
		dismissDialog();
		if (TextUtils.isEmpty(title)) {
			title = "请稍候";
		}
		if (TextUtils.isEmpty(message)) {
			message = "正在加载...";
		}
		mProgressDialog = ProgressDialog.show(context, title, message);
	}

	public static final void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	private static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Bundle params = msg.getData();
			String title = params.getString("title");
			String response = params.getString("response");
			showProgressDialog(sActivity, response, title);
			super.handleMessage(msg);
			dismissDialog();
		}
		
	};
	
	private static class TQQApiListener implements IUiListener {
		private String mScope = "all";
        private Boolean mNeedReAuth = false;
    	public TQQApiListener(final String scope, final boolean needReAuth) {
			mScope = scope;
			mNeedReAuth = needReAuth;
		}

		@Override
		public void onComplete(final Object response) {
			try {
				final JSONObject json =(JSONObject)response;
				final int ret = json.getInt("ret");
				
				if (ret == 0) {
					final Message msg = mHandler.obtainMessage(0, mScope);
					final Bundle data = new Bundle();
					data.putString("response", response.toString());
					msg.setData(data);
					mHandler.sendMessage(msg);
				} else if (ret == 100030) {
					if (mNeedReAuth) {
						final Runnable r = new Runnable() {
							public void run() {
								mTencent.reAuth(sActivity,mScope, new BaseUiListener());
							}
						};
						sActivity.runOnUiThread(r);
					}
				}
				showResultDialog(sActivity, "", "微博分享成功！");
				// 微博分享成功回调
				if (sWeiboShareSuccessFunc > 0 ){
					MoaiLuaBridge.callLuaFunctionWithString(sWeiboShareSuccessFunc, sWeiboShareExtInfo);
				}
			} catch (final JSONException e) {
			}
			
			dismissDialog();
		}

		@Override
		public void onCancel() {
			System.out.print("");
			dismissDialog();
		}

		@Override
		public void onError(UiError arg0) {
			System.out.print(arg0.errorMessage);
			// 分享失败回调
			if (sWeiboShareFailedFunc > 0 ){
				MoaiLuaBridge.callLuaFunctionWithString(sWeiboShareFailedFunc, sWeiboShareExtInfo);
			}
			showResultDialog(sActivity, arg0.errorMessage, null);
			dismissDialog();
		}
	}
	/**
	 * 
	 * 充值字符回调接口
	 */
	private static IUnipayServiceCallBack.Stub unipayStubCallBack = new IUnipayServiceCallBack.Stub() {
		
		@Override
		public void UnipayNeedLogin() throws RemoteException
		{
			Log.i("UnipayPlugAPI", "UnipayNeedLogin");
		}

		@Override
		public void UnipayCallBack(int resultCode, int payChannel,
				int payState, int providerState, int saveNum, String resultMsg,
				String extendInfo) throws RemoteException
		{
			switch( resultCode ){
				case PAYRESULT_SUCC:
					MoaiLuaBridge.callLuaFunctionWithString(sPaySuccessFunc,"success");
					break;
				case PAYRESULT_ERROR:
					MoaiLuaBridge.callLuaFunctionWithString(sPayFailedFunc, "支付错误!错误码：" + resultMsg);
					break;
				case PAYRESULT_CANCEL:
					MoaiLuaBridge.callLuaFunctionWithString(sPayFailedFunc,"取消支付！");
					break;
			}
		}
	};
	
	
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
		sharedPreferences = sActivity.getSharedPreferences(RecordName, Context.MODE_PRIVATE);
		mTencent = Tencent.createInstance(APP_ID, sActivity.getApplicationContext());
		
		// 绑定支付服务
		unipayAPI = new UnipayPlugAPI(sActivity);
		unipayAPI.setCallBack(unipayStubCallBack);
		unipayAPI.bindUnipayService();
	}

	public static void onStop(){
		// 去绑定支付服务
		unipayAPI.unbindUnipayService();
	}
	
	/*
	 * 设置支付服务相关参数
	 */
	private static void setUnipayParams(){
		unipayResId = R.drawable.sample_yuanbao;
		unipayAPI.setEnv(unipayEnv);
        unipayAPI.setOfferId(unipayOfferid);
        unipayAPI.setLogEnable(true);
        
        Bitmap bmp = BitmapFactory.decodeResource(sActivity.getResources(), unipayResId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		appResData = baos.toByteArray();
	}
	
	private static void onChargeData(JSONObject jsonValues){
		String chargeData = "";
		JSONObject chargeDataJsonStr = new JSONObject();
		try {
			
			String pay_token 	= jsonValues.getString("pay_token");
			String openid		= jsonValues.getString("openid");
			String openkey      = jsonValues.getString("access_token");
			String pf 			= jsonValues.getString("pf");
			String pfkey 		= jsonValues.getString("pfkey");
			
			chargeDataJsonStr.put("pay_token", pay_token);
			chargeDataJsonStr.put("openid", openid);
			chargeDataJsonStr.put("openkey", openkey);
			chargeDataJsonStr.put("pf", pf);
			chargeDataJsonStr.put("pfkey", pfkey);
			
		} catch (Exception e) {
		}
		chargeData = chargeDataJsonStr.toString();
		MoaiLuaBridge.callLuaFunctionWithString(sChargeDataFunc, chargeData);
	}
	
	public static void onChargeData(final int chargeDataFunc){
		sChargeDataFunc = chargeDataFunc;
		
		if( ready()){
			onChargeData(sLoginJson);
			
		}else{
			IUiListener listener = new BaseUiListener(){
				@Override
				protected void doComplete(JSONObject values) {
					JSONObject response = (JSONObject) values;
					saveLoginInfo(response);
					onChargeData(response);
				}
			};
	        mTencent.login(sActivity, "all", listener);
		}
	}
	
	/**
	 * 执行真正的支付
	 * @param zoneId 分区 
	 * @param saveValue 充值数量
	 */
	private static void onCharge(final String zoneId, final String tokenUrl, final String roleName, final String remark){
		try
		{
			// 
			setUnipayParams();
			String pf 			= sLoginJson.getString("pf");
			String pfkey 		= sLoginJson.getString("pfkey");
			String pay_token 	= sLoginJson.getString("pay_token");
			//充值游戏币
			unipayAPI.SaveGoods(
					sLoginOpenid, pay_token,
					sessionId, sessionType,
					zoneId + "_" + URLEncoder.encode(roleName), pf, pfkey, 
					tokenUrl, appResData, remark);
			
		}catch (Exception e)
		{
			MoaiLuaBridge.callLuaFunctionWithString(sPayFailedFunc, "支付失败!");
		}
	}

	/**
	 * 打开支付相关逻辑处理
	 */
	public static void onCharge(final String roleName, final String zoneId, final String tokenUrl, final String remark, final int paySuccessFunc, final int payFailFunc){
		sPaySuccessFunc = paySuccessFunc;
		sPayFailedFunc = payFailFunc;
		
		
		if( ready()){
			onCharge(zoneId, tokenUrl, roleName,remark);
		}else{
			IUiListener listener = new BaseUiListener(){
				@Override
				protected void doComplete(JSONObject values) {
					Log.i("loginSuccess", values.toString());
					JSONObject response = (JSONObject) values;
					saveLoginInfo(response);
					// 登陆成功后重新调用支付
					onCharge(zoneId, tokenUrl, roleName,remark);
				}
			};
	        mTencent.login(sActivity, "all", listener);
		}		
	}
	public static void onLogin(final int successFunc, final int failFunc){
		
		sLoginSuccessFunc = successFunc;
		sLoginFailedFunc = failFunc;
		
		
		if (mTencent == null){
			mTencent = Tencent.createInstance(APP_ID, sActivity.getApplicationContext());
		}
		// 目前自动登陆还算稳定 
		if ( isFreeLogin &&  ready()){
			sLoginOpenid =  sharedPreferences.getString("openid", "");
			access_token = sharedPreferences.getString("access_token", "");
			expires_in = sharedPreferences.getString("expires_in", "0");
			// 判断保存的登陆信息是否为空 如果为空则重新走登陆流程 负责直接设置为登陆态
			if (!("".equals(sLoginOpenid) || "".equals(access_token) || "".equals(expires_in))){
				long expiresTime = (Long.parseLong(expires_in) - System.currentTimeMillis())/1000;
				String responseJson = sharedPreferences.getString("responseJson", "");
	
				// expiresTime <= 0 标示登陆态失效
				if (responseJson != "" ) {
					mTencent.setOpenId(sLoginOpenid);
					mTencent.setAccessToken(access_token, String.valueOf(expiresTime));
					MoaiLuaBridge.callLuaFunctionWithString(sLoginSuccessFunc, responseJson);
					return;	
				}
			}
		}
		
		IUiListener listener = new BaseUiListener(){
			@Override
			protected void doComplete(JSONObject values) {
				JSONObject response = (JSONObject) values;
				saveLoginInfo(response);
				MoaiLuaBridge.callLuaFunctionWithString(sLoginSuccessFunc, response.toString());
			}
		};
		
        mTencent.login(sActivity, "all", listener);
	}
	
	/**
	 * 保存登陆相关的信息
	 * @param response
	 */
	private static void saveLoginInfo(JSONObject response){
		sLoginJson = response;
		try {
			sLoginOpenid = response.getString("openid");
			access_token = response.getString("access_token");
			expires_in  = response.getString("expires_in");
			expires_in = String.valueOf(System.currentTimeMillis() + Long.parseLong(expires_in) * 1000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("openid", sLoginOpenid);
		editor.putString("access_token", access_token);
		editor.putString("expires_in", expires_in);
		editor.putString("responseJson", response.toString());
		editor.commit();
	}
	
	/**
	 * 玩家是否登陆，且是否已过期
	 * @return
	 */
	public static boolean ready(){
		if (sLoginJson == null){
			return false;
		}
		String expires_in = sharedPreferences.getString("expires_in", "0");
		if (!"".equals(expires_in)){
			long expiresTime = (Long.parseLong(expires_in) - System.currentTimeMillis())/1000;
			if (expiresTime > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 客户端调用注销方法
	 * @param logoutFunc
	 */
	public static void onLogout(final int logoutFunc){
		sLoginOutFunc = logoutFunc;
		mTencent.logout(sActivity);
		sLoginOpenid = "";
		access_token = "";
		expires_in = "0";
		sLoginJson = null;
		// 注销之后将玩家登陆信息清空
		SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
		editor.clear();
		editor.commit();
		MoaiLuaBridge.callLuaFunctionWithString(sLoginOutFunc, "logout");
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data) ;
	}
	
	/**
	 * 分享带图微博
	 * @param shareInfo 分享类容
	 * @param resPath   分享图片路径
	 */
	public static void onShareToWeibo(final String shareInfo, final String resPath, final String ext_info, final int successFunc, final int faildFunc){
		sWeiboShareSuccessFunc = successFunc;
		sWeiboShareFailedFunc = faildFunc;
		sWeiboShareExtInfo    = ext_info;
		if (mWeibo == null){
			mWeibo = new Weibo(sActivity, mTencent.getQQToken());
		}
		String filePath = getSDPath() + "/" + resPath ;
		mWeibo.sendPicText(shareInfo, filePath, new TQQApiListener("add_t", false));
    	showProgressDialog(sActivity, null, "正在发送微博...");
	}
	
	/**
	 * 分享纯文本微博
	 * @param shareInfo 分享类容
	 * @param resPath   分享图片路径
	 */
	public static void onShareToWeiboText(final String shareInfo,final String ext_info, final int successFunc, final int faildFunc){
		sWeiboShareSuccessFunc = successFunc;
		sWeiboShareFailedFunc = faildFunc;
		sWeiboShareExtInfo    = ext_info;
		if (mWeibo == null){
			mWeibo = new Weibo(sActivity, mTencent.getQQToken());
		}
		mWeibo.sendText(shareInfo, new TQQApiListener("add_t", false));
		showProgressDialog(sActivity, null, "正在发送微博...");
	}
	/**
	 * 分享游戏到QQ空间 OK
	 * @param title
	 * @param sharMsg
	 */
	public static void onShareToQZone(final String title, final String sharMsg) {
		if (mTencent.isSessionValid() && mTencent.getOpenId() != null) {
			Bundle params = new Bundle();
			params.putString(SocialConstants.PARAM_TITLE, title);
			params.putString(SocialConstants.PARAM_IMAGE, APP_ICON_URL);

			params.putString(SocialConstants.PARAM_COMMENT,	sharMsg);
			
			mTencent.story(sActivity, params, new IUiListener(){
				
				@Override
				public void onCancel() {
					System.out.print("onCancel");
				}

				@Override
				public void onComplete(Object arg0) {
					System.out.print("onComplete");
				}

				@Override
				public void onError(UiError arg0) {
					System.out.print("onError");
				}
				
			});
		}
	}
	/**
	 * 分享到QQ OK
	 * @param ganeName
	 * @param shareMsg
	 */
	public static void shareToQQ(final String ganeName, final String shareMsg){
		if (mTencent.isSessionValid() && mTencent.getOpenId() != null) {
			Bundle params = new Bundle();
			params.putString(SocialConstants.PARAM_TITLE, ganeName);
			params.putString(SocialConstants.PARAM_IMAGE_URL,   APP_ICON_URL);
			params.putString(SocialConstants.PARAM_TARGET_URL,	APP_TARGET_URL);

			params.putString(SocialConstants.PARAM_SUMMARY, shareMsg);
			params.putString(SocialConstants.PARAM_APP_SOURCE, ganeName + APP_ID);
			mTencent.shareToQQ(sActivity, params, new IUiListener(){
				
				@Override
				public void onCancel() {
					System.out.print("onCancel");
				}

				@Override
				public void onComplete(Object arg0) {
					System.out.print("onComplete");
				}

				@Override
				public void onError(UiError arg0) {
					System.out.print("onError");
				}
				
			});

		}
	}
	public static String getSDPath(){
	       File sdDir = null;
	       boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
	       if (sdCardExist)  
	       {                              
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录
	      }  
	       return sdDir.toString();
	      
	} 
}

