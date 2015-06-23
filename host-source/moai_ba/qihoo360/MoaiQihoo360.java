//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import org.json.JSONException;
import org.json.JSONObject;

import com.qihoo.gamecenter.sdk.common.IDispatcherCallback; 
import com.qihoo.gamecenter.sdk.protocols.pay.ProtocolConfigs; 
import com.qihoo.gamecenter.sdk.protocols.pay.ProtocolKeys; 
import com.qihoopay.insdk.activity.ContainerActivity; 
import com.qihoopay.insdk.matrix.Matrix; 

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiQihoo360 
{
    private static Activity sActivity 		= null;
    
    private static String sAppId			= "";
    private static String sAppKey			= "";
    private static String sPrivateKey		= "";
    private static String sAppChannel		= "";
    private static boolean sIsLanscape		= true;
    // 登录响应模式：CODE模式。
    protected static final String RESPONSE_TYPE_CODE = "code";
    // 360SDK登录返回的Json字符串中的Json name，代表CODE模式登录返回的Authorization Code（授权码，60秒有效）。
    private static final String AUTH_CODE = "code";
    
    protected static native void AKUNotifyQihoo360InitFinish (String data);
    protected static native void AKUNotifyQihoo360LoginFinish (String jsondata, String authcode);
    protected static native void AKUNotifyQihoo360SwitchAccountFinish (String jsondata, String authcode);
    protected static native void AKUNotifyQihoo360PaymentFinish (String jsondata);

    //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
    	MoaiLog.i ( "MOAIQihoo360 onCreate: Initializing qihoo360" );
		sActivity = activity;
    }
	public static void onDestroy () 
    {	
        Matrix.destroy(sActivity);
		sActivity = null;
    }
    public static void init (String appId, 
    						 String appKey, 
    						 String privateKey, 
    						 String appChannel, 
    						 boolean isLandscape) 
    {
        sAppId 		= appId;
    	sAppKey 	= appKey;
    	sPrivateKey = privateKey;
    	sAppChannel = appChannel;
    	sIsLanscape = isLandscape;

        Matrix.init(sActivity, false, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
            	synchronized ( Moai.sAkuLock ) 
    			{
            		AKUNotifyQihoo360InitFinish (data );
    			}
            }
        });
    }
    
    public static void openLoginDialog ()
    {
    	Intent intent = getLoginIntent(sIsLanscape, true);
        Matrix.invokeActivity(sActivity, intent, sLoginCallback);
    }

    public static void openSwitchAccountDialog()
    {
    	Intent intent = getSwitchAccountIntent(sIsLanscape, true);
        Matrix.invokeActivity(sActivity, intent, sAccountSwitchCallback);
    }

    public static void openBBSDialog()
    {
    	Intent intent = getBBSIntent(sIsLanscape);
        Matrix.invokeActivity(sActivity, intent, null);
    }
    
    public static void openPaymentDialog(String accessToken, String qihooUserId,
    									 String ammount, String exchangeRate, 
	    								 String productName, String productId,
	    								 String notifyUri,
	    								 String appName, String appUserName, String appUserId,
	    								 String appExt1, String appExt2,
	    								 String appOrderId)
    {
    	
    	Bundle bundle = new Bundle();
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, sIsLanscape);

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
            	AKUNotifyQihoo360LoginFinish(data, authorizationCode);
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
            	AKUNotifyQihoo360SwitchAccountFinish(data, authorizationCode);
			}
        }
    };
    // 支付的回调
    private static IDispatcherCallback sPayCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
      
        	synchronized ( Moai.sAkuLock ) 
			{
        		AKUNotifyQihoo360PaymentFinish(data);
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
    
 
}

