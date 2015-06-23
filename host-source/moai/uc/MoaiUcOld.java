//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import cn.uc.gamesdk.*;
import cn.uc.gamesdk.info.*;

import com.eclipsesource.json.JsonObject;
import java.io.InputStreamReader;
import org.json.JSONObject;
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiUcOld {

	protected static native void	AKUNotifyUcInitSuccess ( );
	protected static native void	AKUNotifyUcInitError ( String msg );

	protected static native void	AKUNotifyUcLoginSuccess ( String sid );
	protected static native void	AKUNotifyUcLoginError ( int code, String msg );
	protected static native void	AKUNotifyUcLoginExit ( );

	protected static native void	AKUNotifyUcUserCenterSuccess( int statusCode );
	protected static native void	AKUNotifyUcUserCenterError( int errorCode, String errorMessage );

	protected static native void	AKUNotifyUcPaymentSuccess( float orderAmount, String orderId, int payWay );
	protected static native void	AKUNotifyUcPaymentError( int errorCode, String errorMessage );
	protected static native void	AKUNotifyUcPaymentUserExit( int errorCode, String errorMessage );

	protected static native void	AKUNotifyUcSdkOpen();
	protected static native void	AKUNotifyUcSdkClose();
	
	
	private static Activity sActivity = null;

	private static UCGameSDK sUcSdk = null;

	static int cpId      = 0;    
	static int gameId    = 0;
	static int serverId  = 0;
	
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MOAIUc onCreate: Initializing uc" );
		
		sActivity = activity;
		loadChannelConfig();
	}
	
	//读取配置文件
	public static void loadChannelConfig(){	 
		try { 
			String fileName="cConfig.json";
            InputStreamReader inputReader = new InputStreamReader(sActivity.getResources().getAssets().open(fileName) ); 
            JsonObject jsonObj=JsonObject.readFrom(inputReader);
            cpId=jsonObj.get("cpId").asInt();
            gameId=jsonObj.get("gameId").asInt();
            serverId=jsonObj.get("serverId").asInt(); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
	}
	

	public static boolean onPreExit(Activity activity, final Moai.OnFinishHandler onfinish)
	{
		if(sActivity != null)
		{ 
			UCGameSDK.defaultSDK().exitSDK(activity, new UCCallbackListener<String>() {
	     	@Override 
	     	public void callback(final int statuscode,final String data) { 
	         if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE==statuscode) {
	            //此加入继续游戏的代码 
	        	 onfinish.callback(false);	         	 
	
	         }else if (UCGameSDKStatusCode.SDK_EXIT==statuscode) { 
	             //在此加入退出游戏的代码 
	        	 onfinish.callback(true);
	         }
		  }
		}); 
		}
		return true;
	}
 
	public static boolean onPreRunning( Activity activity, final Moai.OnFinishHandler onfinish)
	{
		 
		 boolean debugMode = false;
		    
		final ProgressDialog dialog = ProgressDialog.show(sActivity, "", "正在初始化", true);
	    dialog.setCancelable(false);
	    try {
		    final boolean dmode = debugMode;
		    sUcSdk = UCGameSDK.defaultSDK();
		    sUcSdk.setOrientation(UCOrientation.LANDSCAPE);
		    GameParamInfo gpi = new GameParamInfo();
		    gpi.setCpId( cpId );
		    gpi.setGameId( gameId );
		    gpi.setServerId( serverId );
		    sUcSdk.initSDK(sActivity, UCLogLevel.DEBUG, dmode, gpi, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
			    dialog.dismiss();
			    Log.e("UCGameSDK", "UCGameSDK初始化接口返回数据 msg:" + msg + ",code:" + code + ",debug:" + dmode + "\n");
			    switch (code) {
			    // 初始化成功，调用登录
			    case UCGameSDKStatusCode.SUCCESS:
					createFloatButton(sActivity);
					onfinish.callback(true);
				break;

			    // 初始化失败
			    case UCGameSDKStatusCode.INIT_FAIL:
			    default:
			    	onfinish.callback(false);
				break;
			    }
			}
		});
	    } catch (UCCallbackListenerNullException e) {
		e.printStackTrace();
		onfinish.callback(false);
	    } catch (Exception e) {
		e.printStackTrace();
		onfinish.callback(false);
	    }
		
		return true;
	}
	//================================================================//
	// uc JNI callback methods
	//================================================================//

	//----------------------------------------------------------------//
	public static void init ( int cpId, int gameId, int serverId, boolean debugMode)
	{
		synchronized ( Moai.sAkuLock ) 
		{
		    AKUNotifyUcInitSuccess ( );
		}
		//intInternal( cpId, gameId, serverId, debugMode);
	}
	
	public static void intInternal ( int _cpId, int _gameId, int _serverId, boolean debugMode) 
	{
	    if (sActivity == null) 
	    {
		return;
	    }
	     
	    final ProgressDialog dialog = ProgressDialog.show(sActivity, "", "正在初始化", true);
	    dialog.setCancelable(false);
	    try {
		    final boolean dmode = debugMode;
		    sUcSdk = UCGameSDK.defaultSDK();
		    sUcSdk.setOrientation(UCOrientation.LANDSCAPE);
		    GameParamInfo gpi = new GameParamInfo();
		    gpi.setCpId( cpId );
		    gpi.setGameId( gameId );
		    gpi.setServerId( serverId );
		    sUcSdk.initSDK(sActivity, UCLogLevel.DEBUG, dmode, gpi, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
			    dialog.dismiss();
			    Log.e("UCGameSDK", "UCGameSDK初始化接口返回数据 msg:" + msg + ",code:" + code + ",debug:" + dmode + "\n");
			    switch (code) {
			    // 初始化成功，调用登录
			    case UCGameSDKStatusCode.SUCCESS:
				//dispalyGameLoginUI();
 

				// 调用sdk登录接口
				//ucSdkLogin();
				synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUcInitSuccess ( );
				}
				break;

			    // 初始化失败
			    case UCGameSDKStatusCode.INIT_FAIL:
				//ucNetworkAndInitUCGameSDK();
				synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUcInitError ("sdk init error!");
				}
			    default:
				break;
			    }
			}
		});
	    } catch (UCCallbackListenerNullException e) {
		e.printStackTrace();
		synchronized ( Moai.sAkuLock ) 
		{
		    AKUNotifyUcInitError ( e.toString() );
		}
	    } catch (Exception e) {
		e.printStackTrace();
		synchronized ( Moai.sAkuLock ) 
		{
		    AKUNotifyUcInitError (  e.toString() );
		}
	    }
	}

	//----------------------------------------------------------------//
	public static void openLoginDialog () 
	{
	    if (sUcSdk == null || sActivity == null) 
	    {
		return;
	    }

	    // 登录接口回调。从这里可以获取登录结果。
            UCCallbackListener<String> loginCallbackListener = new UCCallbackListener<String>() {
                @Override
                public void callback(int code, String msg) {
                    Log.e("UCGameSDK", "UCGameSdk登录接口返回数据:code=" + code + ",msg=" + msg);

                    // 登录成功。此时可以获取sid。并使用sid进行游戏的登录逻辑。
                    if (code == UCGameSDKStatusCode.SUCCESS) {

                       String sid = sUcSdk.getSid(); 


			try {
                        JSONObject jsonExData = new JSONObject();
                        jsonExData.put("roleId", sid);// 当前登录的玩家角色ID
                        jsonExData.put("roleName", sid);// 当前登录的玩家角色名
                        jsonExData.put("roleLevel", 10);// 当前登录的玩家角色等级
                        jsonExData.put("zoneId", 1);// 当前登录的游戏区服ID
                        jsonExData.put("zoneName", "游戏一区");// 当前登录的游戏区服名称
                        UCGameSDK.defaultSDK().submitExtendData("loginGameRole", jsonExData);
                       
                } catch (Exception e) {
                        // 处理异常
                }



		       synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyUcLoginSuccess ( sid );
			}
                    }

                    // 登录失败。应该先执行初始化成功后再进行登录调用。
                    if (code == UCGameSDKStatusCode.NO_INIT) {
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyUcLoginError ( code, msg );
			}
                    }

                    // 登录退出。该回调会在登录界面退出时执行。
                    if (code == UCGameSDKStatusCode.LOGIN_EXIT) {
			synchronized ( Moai.sAkuLock ) 
			{
			    AKUNotifyUcLoginExit ( );
			}
                    }
                }
            };

	    try{
		sUcSdk.login(sActivity, loginCallbackListener);
	    }catch(UCCallbackListenerNullException cbEx)
	    {
		synchronized ( Moai.sAkuLock ) 
		{
		    AKUNotifyUcLoginError ( -1, cbEx.toString() );
		}
	    }
	}
		
	//----------------------------------------------------------------//
	public static void openMemberCenterDialog ( ) 
	{

	    if (sUcSdk == null || sActivity == null) 
	    {
		return;
	    }

	    try {
		sUcSdk.enterUserCenter(sActivity, new UCCallbackListener<String>() {
		    @Override
		    public void callback(int statuscode, String data) {
			String msg = null;
			switch (statuscode) {
			case UCGameSDKStatusCode.SUCCESS:
			    msg = "个人中心退出code=" + statuscode;
			    synchronized ( Moai.sAkuLock ) 
			    {
				AKUNotifyUcUserCenterSuccess ( statuscode );
			    }
			    break;
			// !!! 未初始化成功。应该先调用初始化
			case UCGameSDKStatusCode.NO_INIT:
			    msg = "调用个人中心失败，SDK没有初始化成功";
			    synchronized ( Moai.sAkuLock ) 
			    {
				AKUNotifyUcUserCenterError (statuscode, data );
			    }
			    break;
			case UCGameSDKStatusCode.NO_LOGIN:
			    msg = "调用个人中心失败，没有登录";
			    synchronized ( Moai.sAkuLock ) 
			    {
				AKUNotifyUcUserCenterError (statuscode, data );
			    }
			    break;
			default:
			    break;
			}
		    }
		});
	    } catch (Exception e) {
		e.printStackTrace();
		synchronized ( Moai.sAkuLock ) 
		{
		    AKUNotifyUcUserCenterError (-1, e.toString() );
		}
	    }
	}

	//----------------------------------------------------------------//
	public static void openPaymentDialog ( String roleId, String roleName, String grade, String customInfo, int _serverID, float amount) 
	{
		
	    if (sUcSdk == null || sActivity == null) 
	    {
		return;
	    }

	    PaymentInfo paymentInfo = new PaymentInfo();
	    paymentInfo.setRoleId(roleId);         // 用户角色id
	    paymentInfo.setRoleName(roleName);     // 角色名字
	    paymentInfo.setGrade(grade);           // 角色等级
	    paymentInfo.setCustomInfo(customInfo); // 游戏自定义信息
	    // 服务器分区id
	    paymentInfo.setServerId(serverId);
	    // 通过设置参数会有以下效果：
	    // 当amount <= 0.0时，正常支付
	    // 当amount > 0.0时，使用定额支付
	    paymentInfo.setAmount(amount);
	    //是否允许连续充值，这个在SDK2.1.0后不起作用
	    paymentInfo.setAllowContinuousPay(false);

	    try {
                    sUcSdk.pay(sActivity, paymentInfo, new UCCallbackListener<OrderInfo>() {
                        @Override
                        public void callback(int statudcode, OrderInfo orderInfo) {
                            /** 获取充值结果 */
                            String texts = "";
                            switch (statudcode) {
                            case UCGameSDKStatusCode.SUCCESS:
                                if (orderInfo != null) {
				    float amount = orderInfo.getOrderAmount();
				    String orderId = orderInfo.getOrderId();
				    int payWay =  orderInfo.getPayWay();
                                    String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
                                    Log.d("UCGameSDK", "UCGameSDK调用支付下订单成功。支付回调信息＝" + txt);
                                    //addOutputResult(txt + "\n");
				    
				    synchronized ( Moai.sAkuLock ) 
				    {
					AKUNotifyUcPaymentSuccess (amount, orderId, payWay);
				    }
                                }
                                break;
                            case UCGameSDKStatusCode.NO_INIT:
                                String str = "UCGameSDK调用支付接口失败，未初始化";
                                Log.e("UCGameSDK", str);
				synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUcPaymentError( statudcode, str );
				}
                                break;
                            case UCGameSDKStatusCode.PAY_USER_EXIT:
                                String strLog = "UCGameSDK支付界面退出";
                                Log.e("UCGameSDK", strLog);
				synchronized ( Moai.sAkuLock ) 
				{
				    AKUNotifyUcPaymentUserExit( statudcode, strLog );
				}
                                //addOutputResult(texts);
                                break;
                            default:
                                break;
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("UCGameSDK", "UCGameSDK支付接口调用异常", e);
                    //addOutputResult("charge failed - Exception: " + e.toString() + "\n");
		    synchronized ( Moai.sAkuLock ) 
		    {
			AKUNotifyUcPaymentError( -1, e.toString() );
		    }
                }
	}

	public static void showFloatButton(double x, double y, boolean visible)
	{
		if (sActivity != null)
		{
			try {
				UCGameSDK.defaultSDK().showFloatButton(sActivity, x, y, visible);
			} catch (UCCallbackListenerNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void createFloatButton(Activity activity)
	{
		try
		{
			try {
				sUcSdk.createFloatButton(sActivity, new UCCallbackListener<String>()
					{
						@Override
						public void callback(int statuscode, String data) {
							switch(statuscode)
							{
							case UCGameSDKStatusCode.SDK_OPEN:  // data="SdkOpen" 表示将要打开 SDK 界面
								synchronized ( Moai.sAkuLock ) 
							    {
									AKUNotifyUcSdkOpen();
							    }
								
								break;
							case UCGameSDKStatusCode.SDK_CLOSE: // data="SdkClose" 表示将要关闭 SDK 界面，返回游戏画面
								synchronized ( Moai.sAkuLock ) 
							    {
									AKUNotifyUcSdkClose();
							    }
								break;
							}
						}
				
					});
			} catch (UCFloatButtonCreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch(UCCallbackListenerNullException e)
		{
			
		}
		
	}
}

