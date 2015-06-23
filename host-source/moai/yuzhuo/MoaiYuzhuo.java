//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.ddworlds.Result.ABSListener;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.yzabs.RequestActivity;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;






 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiYuzhuo  extends  MoaiBaseSdk {
	 
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

	//获取登录信息
	
	
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		
		jsonData.add("isUltralisk", true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	
	
	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		   	
		
	}
/*****************************生命周期*****************************/
//退出游戏时
	public void onMDestroy(){
		

	}
	public void onMResume(){
		
		super.onResume();
		
	}
	
	public void onMPuse(){
		
		super.onPause();
		}
	
	public void onMStop(){
		
		super.onStop();
		
	}


	
	public void onMActivityResult(int requestCode, int resultCode, Intent data){
		
		super.onActivityResult(requestCode, resultCode, data);
		
		}
	
	public void onMNewIntent(Intent intent){
		
		super.onNewIntent(intent);
		
	}
	
/*****************************生命周期*****************************/
	
//是否退出时执行

	public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}
	
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
		
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		

		 return "OK";
	};

	///打开支付界面
	public static  String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson  = _json.get("payInfo").asObject();
			JsonObject  userinfoJson = _json.get("userInfo").asObject();
			String OrderNo     = payinfoJson.get("orderno").asString();
			int price          = Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String userName    = userinfoJson.get("roleName").asString(); 
			String roleId      = userinfoJson.get("roleId").asString();
			
			
			Product pro= new Product(productName, OrderNo, (float)price,OrderNo, roleId, OrderNo, userName);
			AllObj.pro=pro;
			ABSListener l1= new ABSListener(){
						@Override
						public void callBack(int result, String msg) {

							if(result==1){
								
							}else if(result==2){
								
							}else if(result==0){
								
							}
						}

					};
			AllObj.ABSListener1=l1;
			Intent intent =new Intent(sActivity, RequestActivity.class);
			sActivity.startActivity(intent);
			
			
			

		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){

	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		 
		 return "OK";	 
	}
	
	//切换账号
	public static String V2_authQuit(JsonValue parms){
		
//		sq.changeAccount(sActivity,new SQResultListener() {
//            @Override
//            public void onSuccess(Bundle bundle) {
//                
//            	getUserInfo(bundle);
//            }
//            
//            @Override
//            public void onFailture(int code, String msg) {
//                
//                Toast.makeText(sActivity, msg, Toast.LENGTH_LONG).show();
//            }
//        });
		return "";
	} 
 
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){

		 JsonObject jsonData=parms.asObject();
//			serverId = jsonData.get("serverIndex").asString();
//			serverName = jsonData.get("serverName").asString();
//			charId = jsonData.get("roleId").asString();
//			userLv = Integer.valueOf(jsonData.get("roleLv").asString());
//			charName = jsonData.get("roleName").asString();
//			vipLv = jsonData.get("vipLv").asString();
//			if(vipLv.equals("")){vipLv = "0";}
			
//			HashMap<String, String> infos = new   HashMap<String, String>();  
//	          infos.put(SQwanCore.INFO_SERVERID , serverId); 
//	          infos.put(SQwanCore.INFO_SERVERNAME, serverName ); 
//	          infos.put(SQwanCore.INFO_ROLEID , charId); 
//	          infos.put(SQwanCore. INFO_ROLENAME , charName); 
//	          infos.put(SQwanCore.INFO_ROLELEVEL, jsonData.get("roleLv").asString()); 
//	          infos.put(SQwanCore.INFO_BALANCE, "0"); 
//	          infos.put(SQwanCore.INFO_PARTYNAME, jsonData.get("roleClass").asString()); 
//	          infos.put(SQwanCore.INFO_VIPLEVEL , vipLv); 
//	          
//	          sq.submitRoleInfo(infos );
		return "";
	}
	
 
}

