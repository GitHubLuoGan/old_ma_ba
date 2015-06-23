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
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sqwan.msdk.SQwanCore;
import com.sqwan.msdk.api.SQResultListener;





 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moai37wan  extends  MoaiBaseSdk {
	 
	
	public 	static String appKey = "";
	public  static String userId = "";
	public  static String userName = "";
	public  static String accessToken = "";
	public  static String pid = "";
	public  static String gid = "";
	
	
	public static String serverId = "";
	public static String serverName = "";
	public static String charId = "";
	public static String charName = "";
	public static int userLv = 0;
	public static String vipLv = "";

	
	
	public static  SQwanCore sq;
	
	
	 
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
	
	public static void getUserInfo(Bundle bundle){
		
		userId = bundle.getString("userid");
    	userName = bundle.getString("username");
    	accessToken = bundle.getString("token");
    	pid = bundle.getString("pid");
    	gid = bundle.getString("gid");
    	
    	JsonObject jsonData=new JsonObject();
			jsonData.add("userId",userId);
			jsonData.add("userName",userName);
			jsonData.add("accessToken",accessToken);
			jsonData.add("pid",pid);
			jsonData.add("gid",gid);
			
	        JsonObject jsonParms=SDKFormatGateWay(userId,jsonData);
	        jsonParms.add("data", jsonData);
	        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
		
	}
	
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	
	
	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		 
		appKey = sConfigJsonObject.get("appKey").asString();
		sq = SQwanCore.getInstance();
		
		sq.init(sActivity,appKey, new SQResultListener() {
            
            @Override
            public void onSuccess(Bundle bundle) {
                
                
            }
            
            @Override
            public void onFailture(int code, String msg) {
                
 
                Toast.makeText(sActivity,"初始化失败,"+msg,Toast.LENGTH_SHORT).show();
                
            }
        });
		
		 sq.setSwitchAccountListener( new SQResultListener() { 
             
	            @Override 
	            public void onSuccess(Bundle bundle) {  
	                 
	            	getUserInfo(bundle);
	                 
	            }  
	             
	            @Override 
	            public void onFailture(int code,String msg) { 
	                 
	            }  
	        });
	        
		
		
		
	}
/*****************************生命周期*****************************/
//退出游戏时
	public void onMDestroy(){
		
		sq.logout( sActivity , new   SQResultListener(){ 
	           
	          @Override 
	          public  void   onSuccess(Bundle bundle) {}  
	           
	          @Override 
	          public  void  onFailture( int   code, String msg) {}  
	        });  
		sq.onDestroy();
	}
	public void onMResume(){
		
		super.onResume();
		sq.onResume();
		
	}
	
	public void onMPuse(){
		
		super.onPause();
		sq.onPause();
		}
	
	public void onMStop(){
		
		super.onStop();
		sq.onStop();
		
	}


	
	public void onMActivityResult(int requestCode, int resultCode, Intent data){
		
		super.onActivityResult(requestCode, resultCode, data);
		sq.onActivityResult(requestCode, resultCode, data);
		
		}
	
	public void onMNewIntent(Intent intent){
		
		super.onNewIntent(intent);
		sq.onNewIntent(intent);
		
	}
	
/*****************************生命周期*****************************/
	
//是否退出时执行

	public static String V2_exitGame(JsonValue parms){
		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}
	
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
		
		
		sq.logout( sActivity , new   SQResultListener(){ 
	           
	          @Override 
	          public  void   onSuccess(Bundle bundle) {}  
	           
	          @Override 
	          public  void  onFailture( int   code, String msg) {}  
	        }); 
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		
		 sq.login(sActivity,new SQResultListener() {
             @Override
             public void onSuccess(Bundle bundle) {
                 
            	 getUserInfo(bundle);
  
             }
             
             @Override
             public void onFailture(int code, String msg) {
                 System.out.println(msg);
                 Toast.makeText(sActivity, msg, Toast.LENGTH_LONG).show();
             }
         });

		 return "OK";
	};

	///打开支付界面
	public static  String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			String OrderNo     = payinfoJson.get("orderno").asString();
			int price          = Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			
			
			sq.pay(sActivity, OrderNo, productName, "钻石", serverId, serverName, OrderNo, charId, charName, userLv, price, 10, new SQResultListener() {
                
                @Override
                public void onSuccess(Bundle bundle) {
                    
                    Toast.makeText(sActivity, "支付成功", Toast.LENGTH_LONG).show();
                }
                
                @Override
                public void onFailture(int code, String msg) {
                    
                    Toast.makeText(sActivity, msg, Toast.LENGTH_LONG).show();
                }
            });

			String info=payinfoJson.get("info").asString();

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
		
		sq.changeAccount(sActivity,new SQResultListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                
            	getUserInfo(bundle);
            }
            
            @Override
            public void onFailture(int code, String msg) {
                
                Toast.makeText(sActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
		return "";
	} 
 
	
	//角色信息
	public static String V2_setCharInfo(JsonValue parms){

		 JsonObject jsonData=parms.asObject();
			serverId = jsonData.get("serverIndex").asString();
			serverName = jsonData.get("serverName").asString();
			charId = jsonData.get("roleId").asString();
			userLv = Integer.valueOf(jsonData.get("roleLv").asString());
			charName = jsonData.get("roleName").asString();
			vipLv = jsonData.get("vipLv").asString();
			if(vipLv.equals("")){vipLv = "0";}
			
			HashMap<String, String> infos = new   HashMap<String, String>();  
	          infos.put(SQwanCore.INFO_SERVERID , serverId); 
	          infos.put(SQwanCore.INFO_SERVERNAME, serverName ); 
	          infos.put(SQwanCore.INFO_ROLEID , charId); 
	          infos.put(SQwanCore. INFO_ROLENAME , charName); 
	          infos.put(SQwanCore.INFO_ROLELEVEL, jsonData.get("roleLv").asString()); 
	          infos.put(SQwanCore.INFO_BALANCE, "0"); 
	          infos.put(SQwanCore.INFO_PARTYNAME, jsonData.get("roleClass").asString()); 
	          infos.put(SQwanCore.INFO_VIPLEVEL , vipLv); 
	          
	          sq.submitRoleInfo(infos );
		return "";
	}
	
 
}

