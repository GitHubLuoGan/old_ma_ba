//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.ultralisk.gameapp.MoaiActivity;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.mogoo.appserver.*;
import com.mogoo.common.Constants;
import com.mogoo.common.PaymentTO;
import com.mogoo.listener.PaymentListener;
import com.mogoo.listener.ServiceListener;
import com.mogoo.bean.*;
import com.mogoo.error.DialogError;
import com.mogoo.error.MogooError;

import com.mogoo.listener.DialogListener;
import com.mogoo.mg.Mogoo;




 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaikudong extends  MoaiBaseSdk {
	 
	public static String appId = "";
	public static String appKey = "";
	public static String paynotifyUrl = "";
	public static String mid = "";
	public static String usn = "";
	public static String gameId = "";
	public static String serverId = "";
	
	public static MGBaseAbstract kuDong; 
	 
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
		 
		 appId	 =  GetJsonVal(sConfigJsonObject,"appId","000");
	     appKey  =  GetJsonVal(sConfigJsonObject,"appKey","000");
	     
	     kuDong = MGBaseAbstract.getInstance(Mogoo.class, sActivity, appId, appKey);
	     
	     kuDong.mgInit(sActivity, true, initListener );
		

	}
	
	public void onMResume(){
		

	}

//退出游戏时
	public void onMDestroy(){
		
		kuDong.mgDestroy(sActivity);

	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		kuDong.mgLogin(sActivity, loginListener);
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();
			
			
			String OrderNo=payinfoJson.get("orderno").asString();
			String price=payinfoJson.get("price").asString();
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			
			
			
			PaymentTO payment = new PaymentTO();
			payment.uid = mid;
			payment.usn = usn;
			payment.gid = gameId;
			payment.sid = serverId;// cp服务器id传值,必填
			payment.eif = OrderNo;// cp自定义信息，数组或英文组成
			payment.nickname = roleName;// 游戏的角色名,必填
			payment.fixedmoney = "1";// 是否固定金额充值 0：不固定金额充值 1：固定金额充值,必填
			payment.paymoney = price;// 如果选择固定金额充值，请填写paymoney，否则默认为0；充值金额，单位元（RMB）必填

		  kuDong.mgPayment(sActivity, payment, new PaymentListener() {
				@Override
				public void onComplete(Bundle bundle) {
					// state状态 success： 订单成功 fail:订单失败 back:返回操作
					String result = bundle.getString("state");
					if(result.equals("success")){
						
						JsonRpcCall(Lua_Cmd_PaySuccess,"");
						
					}
					
				}

				@Override
				public void onMogooError(MogooError error) {
					JsonRpcCall(Lua_Cmd_PayError,"");
				}

				@Override
				public void onError(Error error) {
					JsonRpcCall(Lua_Cmd_PayError,"");
				}

			});
			


		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	
	
	
	
	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){

		
		 return "OK";	
	}
	 

	 public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}

	///SDK切换账号 
	public static String V2_authQuit(JsonValue parms){


		kuDong.mgLogout(sActivity,new ServiceListener(){
			@Override
			public void onComplete(Bundle bundle){
				JsonRpcCall(Lua_Cmd_LoginCancel,"");
			}
			@Override
			public void onMogooError(MogooError error){

			}
			@Override
			public void onError(Error error){

			}
			});
		
		 return "OK";	
	}
	
	
	///退出游戏
	public String ExitGame(JsonValue parms){
	 
		 return "OK";	 
	}
 
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){
	
		JsonObject jsonData=parms.asObject(); 
		
		serverId = jsonData.get("serverIndex").asString();
		kuDong.mgSendCpSid(sActivity,mid, serverId);
		return "";
	}
	
	/////////////////////////////////
	
	public static DialogListener loginListener = new DialogListener() {
		@Override
		public void onCannel() {
			
			JsonRpcCall(Lua_Cmd_LoginOut,"");
		
		}

		@Override
		public void onComplete(Bundle bundle) {
			// Util.alert(context, "mid:" +
			// bundle.getString(Constants.MG_PREFIX_STR +
			// "mid") + "token: "+
			// bundle.getString(Constants.MG_PREFIX_STR +
			// "token"));
			
			 mid   = bundle.getString(Constants.MG_PREFIX_STR + "mid");
			String token = bundle.getString(Constants.MG_PREFIX_STR + "token");
			usn    = bundle.getString(Constants.MG_PREFIX_STR + "username");
			gameId = bundle.getString(Constants.MG_PREFIX_STR + "gameid");
			
			 
			  	JsonObject jsonData=new JsonObject();
                jsonData.add("mid", mid);
                jsonData.add("token",token);
                
                JsonObject jsonParms=SDKFormatGateWay(mid,jsonData);
                jsonParms.add("data", jsonData);
                 
                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
		
		}

		@Override
		public void onMogooError(MogooError error) {
			JsonRpcCall(Lua_Cmd_LoginOut,"");
		}

		@Override
		public void onError(DialogError error) {
			JsonRpcCall(Lua_Cmd_LoginOut,"");
		}
	};
	
	public static MGCallbackListener initListener =  new MGCallbackListener() {
		@Override
		public void callback(int code, String msg) {
			// 初始化成功后 cp 需要做的处理， code： 200 初始化成功
			if(code == 200){
				}

		}
	};
	
	
	
 
}
