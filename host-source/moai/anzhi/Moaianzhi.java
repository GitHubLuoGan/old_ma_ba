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

import cn.ultralisk.gameapp.hxsg.R;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.LoginActivity;
import com.anzhi.usercenter.sdk.NoticeAndLoginHintShow;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.inter.OfficialLoginCallback;
import com.anzhi.usercenter.sdk.inter.b;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaianzhi  extends  MoaiBaseSdk {
	 
	public static AnzhiUserCenter mAnzhiCenter=null;
	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		
	 
			final CPInfo info = new CPInfo();
			// info.setOpenOfficialLogin(true);//是否开启游戏官方账号登录，默认为关闭
			info.setAppKey(sConfigJsonObject.get("appKey").asString());
			info.setSecret(sConfigJsonObject.get("appSecret").asString());
			info.setChannel("AnZhi");
			info.setGameName(super.sActivity.getResources().getString(R.string.app_name));
			mAnzhiCenter = AnzhiUserCenter.getInstance();			
			mAnzhiCenter.setCPInfo(info);
			mAnzhiCenter.islogin = true;			
			mAnzhiCenter.createFloatView(super.sActivity);
			
			mAnzhiCenter.setCallback(mCallback);
			mAnzhiCenter.setOfficialCallback(mOfficialCall);
			
			
			mAnzhiCenter.setKeybackCall(new KeybackCall() {			
				@Override
				public void KeybackCall(String st) {
					if(st.equals("Login")){ 
						JsonRpcCall(Lua_Cmd_LoginCancel,"");
                        Log.e("xugh","*****************login");//登录界面back键回调 
                    }else if(st.equals("gamePay")){ 
                    	JsonRpcCall(Lua_Cmd_PayCancel,"");
                        Log.e("xugh","*****************GamePay");//支付界面的back键回调 
                    } else if(st.equals("AnzhiCurrent")){ 
                    	JsonRpcCall(Lua_Cmd_PayCancel,"");
                        Log.e("xugh","*****************GamePay");//安智币充值界面  
                    } 
                    
/*                    
 *  UserCenter           个人中心界面 
    GameGift         是礼包页面 
    anzhiBbs         是论坛页面 
    Message          是消息页面 
    Feedback      是客服中心页面 
    Login             是登录页面 
    gamePay       是充值页面 
    AnzhiCurrent  安智币充值界面 
                          */
				
				
				Log.e("Moaianzhi", "st=========="+st);
				}
			});
			/*
			mAnzhiCenter.setCallback(mCallback);
			mAnzhiCenter.setOfficialCallback(mOfficialCall);
			*/
			
		
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
		mAnzhiCenter.login(super.sActivity, true);
		 return "OK";
	};

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payinfo").asObject();
			String OrderNo=payinfoJson.get("orderno").asString();
			int price=payinfoJson.get("price").asInt();
			int total=payinfoJson.get("total").asInt();
			String info=payinfoJson.get("info").asString();
			mAnzhiCenter.pay(super.sActivity,0,total,info,OrderNo);  
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		   mAnzhiCenter.getInstance().viewUserInfo(super.sActivity); 
	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		mAnzhiCenter.getInstance().logout(super.sActivity);		 
		 return "OK";	 
	}
 
	
	
	
	AnzhiCallback mCallback = new AnzhiCallback() {

		@Override
		public void onCallback(CPInfo cpInfo, final String result) {
			Log.e("anzhi", "result " + result);
			try {
				JSONObject json = new JSONObject(result);
				String key = json.optString("callback_key");
				if ("key_pay".equals(key)) {
					int code = json.optInt("code");
					String desc = json.optString("desc");
					String orderId = json.optString("order_id");
					String price = json.optString("price");
					String time = json.optString("time");
					if (code == 200) {
						JsonRpcCall(Lua_Cmd_PaySuccess,"");
						Toast.makeText(sActivity,"返回信息 orderid: " + orderId + "price: " + price
										+ "time: " + time, Toast.LENGTH_SHORT).show();
					} else {
						JsonRpcCall(Lua_Cmd_PayError,"");
						Toast.makeText(sActivity, desc,Toast.LENGTH_SHORT).show();
					}
				}else if("key_logout".equals(key)){
					JsonRpcCall(Lua_Cmd_LoginOut,"");					 
					Toast.makeText(sActivity, "退出登陆",Toast.LENGTH_SHORT).show();
					mAnzhiCenter.login(sActivity, true); 
				}else if("key_login".equals(key)){
					int code = json.optInt("code");
					String desc = json.optString("code_desc");
					String loginName = json.optString("login_name");
					String	uid = json.getString("uid");
					String	sid = json.getString("sid");
					String nick_name=json.getString("nick_name");
					if (code == 200) {
						JsonObject jsonParms=new JsonObject();
						jsonParms.add("gatewayurl", sConfigJsonObject.get("gateWayUrl").asString());
						jsonParms.add("gatewaypath", sConfigJsonObject.get("gateWayPath").asString());
						JsonObject jsonData=new JsonObject();
						jsonData.add("uid", uid);
						jsonData.add("code_desc",desc);
						jsonData.add("login_name",loginName);
						jsonData.add("nick_name",nick_name);
						jsonData.add("sid",sid); 					 
						//jsonData.get(name)
						jsonParms.add("data", jsonData);
						JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
						//jsonData.add("gatewayurl", sConfigJsonObject.get("gateWayUrl").asString());
						
						//mHandler.sendEmptyMessage(1);
					} else {
						JsonRpcCall(Lua_Cmd_LoginFailed,"");							 
					}
				}
			} catch (Exception e) {
				Log.e("Moaianzhi Error ", e + "");
			}

		}
	};
	
	 
	OfficialLoginCallback mOfficialCall = new OfficialLoginCallback() {

		@Override
		public String login(String arg0, String arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onOfficialLoginResult(String arg0) {
			// TODO Auto-generated method stub

		}

	};
}

