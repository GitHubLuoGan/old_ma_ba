//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.uc.gamesdk.UCCallbackListener;
import cn.uc.gamesdk.UCCallbackListenerNullException;
import cn.uc.gamesdk.UCFloatButtonCreateException;
import cn.uc.gamesdk.UCGameSDK;
import cn.uc.gamesdk.UCGameSDKStatusCode;
import cn.uc.gamesdk.UCLogLevel;
import cn.uc.gamesdk.UCOrientation;
import cn.uc.gamesdk.info.GameParamInfo;
import cn.uc.gamesdk.info.OrderInfo;
import cn.uc.gamesdk.info.PaymentInfo;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiuc  extends  MoaiBaseSdk {
	
	 public static boolean debugMode = false;
	 public static UCGameSDK sUcSdk = null;
	 public static int cpId = sConfigJsonObject.get("cpId").asInt();
	 public static int gameId = sConfigJsonObject.get("gameId").asInt();
	 public static int serverId = 0;
	 
	 public static String roleId = "";
	 public static String roleName = "";
	 public static String level = "";
	 
	 
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
		
		 try {
			    final boolean dmode = debugMode;
			    sUcSdk = UCGameSDK.defaultSDK();
			    sUcSdk.setOrientation(UCOrientation.LANDSCAPE);
			    GameParamInfo gpi = new GameParamInfo();
			    gpi.setCpId( cpId );
			    gpi.setGameId( gameId );
			    gpi.setServerId(0);
			    sUcSdk.initSDK(sActivity, UCLogLevel.DEBUG, false, gpi, new UCCallbackListener<String>() {
				@Override
				public void callback(int code, String msg) {
				 
					MoaiLog.i("UCGameSDK::UCGameSDK初始化接口返回数据 msg:" + msg + ",code:" + String.valueOf(code) + "\n");
				    switch (code) {
				    // 初始化成功，调用登录
				    case UCGameSDKStatusCode.SUCCESS:
				    	createFloatButton();
						
					break;

				    // 初始化失败
				    case UCGameSDKStatusCode.INIT_FAIL:
				    default:
					break;
				    }
				}
			});
			    
			    
			    
				UCGameSDK.defaultSDK().setLogoutNotifyListener(
						new UCCallbackListener<String>() {
							@Override
							public void callback(int statuscode, String msg) {
								// TODO 此处需要游戏客户端注销当前已经登录的游戏角色信息
								String s = "游戏接收到用户退出通知。" + msg + statuscode;
								Log.e("UCGameSDK", s);
								// 未成功初始化
								if (statuscode == UCGameSDKStatusCode.NO_INIT) {
									// 调用SDK初始化接口
								}
								// 未登录成功
								if (statuscode == UCGameSDKStatusCode.NO_LOGIN) {
									// 调用SDK登录接口
								}
								// 退出账号成功
								if (statuscode == UCGameSDKStatusCode.SUCCESS) {
									// 执行销毁悬浮按钮接口
									// 调用SDK登录接口
									UCGameSDK.defaultSDK().destoryFloatButton(sActivity);
								}
								// 退出账号失败
								if (statuscode == UCGameSDKStatusCode.FAIL) {
									// 调用SDK退出当前账号接口
								}
							}
						});
			    
			    
		    } catch (UCCallbackListenerNullException e) {
			e.printStackTrace();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
	}

//退出游戏时
	public void onMDestroy(){
		
//		UCGameSDK.defaultSDK().exitSDK(sActivity, new UCCallbackListener<String>() {
//			@Override
//			public void callback(int code, String msg) {
//				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
//					// 此加入继续游戏的代码
//
//				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
//					// 在此加入退出游戏的代码
//					sUcSdk.destoryFloatButton(sActivity);
//					System.exit(0);
//				}
//			}
//		});
		System.exit(0);
		
	}


	//创建悬浮按钮
	public static void createFloatButton(){
	try {
		sUcSdk.createFloatButton(sActivity,
				new UCCallbackListener<String>() {

					@Override
					public void callback(int statuscode, String data) {
						Log.d("SelectServerActivity`floatButton Callback",
								"statusCode == " + statuscode
										+ "  data == " + data);
					}
				});
	} catch (UCCallbackListenerNullException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UCFloatButtonCreateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	//显示悬浮按钮
	public static void showFloatButton(){
		
					// 显示悬浮图标，游戏可在某些场景选择隐藏此图标，避免影响游戏体验
					try {
						sUcSdk.showFloatButton(sActivity, 100, 50, true);
					} catch (UCCallbackListenerNullException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
	}

	public static String V2_exitGame(JsonValue parms){
		UCGameSDK.defaultSDK().exitSDK(sActivity, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码

				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					sUcSdk.destoryFloatButton(sActivity);
					System.exit(0);
				}
			}
		});
		return "";
	}
	
	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
	 
		try {
			sUcSdk.login(sActivity,  new UCCallbackListener<String>() {
				@Override
				public void callback(int code, String msg) {
					Log.e("UCGameSDK", "UCGameSdk登录接口返回数据:code=" + code
							+ ",msg=" + msg);

					// 登录成功。此时可以获取sid。并使用sid进行游戏的登录逻辑。
					// 客户端无法直接获取UCID
					if (code == UCGameSDKStatusCode.SUCCESS) {


						String uid = sUcSdk.getSid();
						createFloatButton();
						showFloatButton();
						
					      JsonObject jsonData=new JsonObject();
			              jsonData.add("sid", uid);
			              
			              JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
			              jsonParms.add("data", jsonData);		    
			              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
						
						
					}

					// 登录失败。应该先执行初始化成功后再进行登录调用。
					if (code == UCGameSDKStatusCode.NO_INIT) {
						// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
						JsonRpcCall(Lua_Cmd_LoginCancel,"注销登录");
					}

					// 登录退出。该回调会在登录界面退出时执行。
					if (code == UCGameSDKStatusCode.LOGIN_EXIT) {
						// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
						JsonRpcCall(Lua_Cmd_LoginOut,"登录失败");
					}
				}
			});
		} catch (UCCallbackListenerNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return "OK";
	}

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		 
		JsonObject _json = parms.asObject();	 
		JsonObject	payinfoJson=_json.get("payInfo").asObject();
		String OrderNo=payinfoJson.get("orderno").asString();
		int price=Integer.parseInt(payinfoJson.get("price").asString());

		

	    PaymentInfo paymentInfo = new PaymentInfo();
	    paymentInfo.setRoleId(roleId);         // 用户角色id
	    paymentInfo.setRoleName(roleName);     // 角色名字
	    paymentInfo.setGrade(level);           // 角色等级
	    paymentInfo.setCustomInfo(OrderNo); // 游戏自定义信息
	    paymentInfo.setServerId(0);	    
	    // 服务器分区id
	    // 通过设置参数会有以下效果：
	    // 当amount <= 0.0时，正常支付
	    // 当amount > 0.0时，使用定额支付
	    paymentInfo.setAmount(price);
	    //是否允许连续充值，这个在SDK2.1.0后不起作用
	    paymentInfo.setAllowContinuousPay(false);

	    try {
                    sUcSdk.pay(sActivity, paymentInfo, new UCCallbackListener<OrderInfo>() {
                		@Override
                		public void callback(int statudcode, OrderInfo orderInfo) {
                			if (statudcode == UCGameSDKStatusCode.NO_INIT) {
                				// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
                			}
                			if (statudcode == UCGameSDKStatusCode.SUCCESS) {
                				// 成功充值
                				JsonRpcCall(Lua_Cmd_PaySuccess,"");
                			}
                			if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
                				// 用户退出充值界面。
                			}
                		}

                	});
                } catch (Exception e) {
                    Log.e("UCGameSDK", "UCGameSDK支付接口调用异常", e);
                    //addOutputResult("charge failed - Exception: " + e.toString() + "\n");
		  
                }
		
		
		 return "OK";	
	}

	///SDK会员中心 
	public static String V2_OpenPlatform(JsonValue parms){
		 
		try {
			sUcSdk.enterUserCenter(sActivity, new UCCallbackListener<String>() {
			    @Override
			    public void callback(int statuscode, String data) {
				String msg = null;
				switch (statuscode) {
				case UCGameSDKStatusCode.SUCCESS:
				    msg = "个人中心退出code=" + statuscode;
				   
				    break;
				// !!! 未初始化成功。应该先调用初始化
				case UCGameSDKStatusCode.NO_INIT:
				    msg = "调用个人中心失败，SDK没有初始化成功";

				    break;
				case UCGameSDKStatusCode.NO_LOGIN:
				    msg = "调用个人中心失败，没有登录";
			
				    break;
				default:
				    break;
				}
			    }
			});
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
	 	 
		
		JsonRpcCall(Lua_Cmd_GameExit,"");
		 return "OK";	 
	}
	
	
	
	
	//是否存在
	public String Exist(JsonValue parms){
		String data =parms.asString();
		if(data.equals(Java_Cmd_OpenPlatform)){
			return "1";
		}
		else
	    	return "0";	
	  }
	
	//返回渠道初始信息
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	
	//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	
	//切换角色
	public static String V2_authQuit(JsonValue parms) throws UCCallbackListenerNullException{
		

			sUcSdk.logout();
	
		return  "";
	}
	
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
		try {
			
			 JsonObject jsonData=parms.asObject();
				serverId = 0;
				roleId = jsonData.get("roleId").asString();
				level = jsonData.get("roleLv").asString();
				jsonData.get("vipLv").toString();
				jsonData.get("roleClass").toString();
				roleName = jsonData.get("roleName").asString();
				String serverName = jsonData.get("serverName").asString();
				
				if(serverName.equals("")){
					serverName = "渠道";
					}
				
				
				
				JSONObject jsonExData=new JSONObject();
				jsonExData.put("roleId",roleId);
				jsonExData.put("roleName",roleName);
				jsonExData.put("roleLevel",level);
				jsonExData.put("zoneId",jsonData.get("serverIndex").asString());
				jsonExData.put("zoneName",serverName);
				UCGameSDK.defaultSDK().submitExtendData("loginGameRole",jsonExData);
     
            
    } catch (Exception e) {
    	MoaiLog.e(" MoaiUCExpand  error "+e.getMessage());
            // 处理异常
    }
		return "";
	}
 
}

