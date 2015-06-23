//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
import com.nd.commplatform.*;
import com.nd.commplatform.entry.NdAppInfo;
import com.nd.commplatform.entry.NdBuyInfo;
import com.nd.commplatform.NdPageCallbackListener.OnPauseCompleteListener;
import com.nd.commplatform.gc.widget.NdToolBar;
import com.nd.commplatform.gc.widget.NdToolBarPlace;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.nd.commplatform.NdCommplatform;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiNd91Expand  extends  MoaiBaseSdk {
	 
    
    
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
		 
	 
	}

//退出游戏时
	public void onMDestroy(){
		 
		 
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){
		
		NdCommplatform.getInstance().ndExit(new NdPageCallbackListener.OnExitCompleteListener(sActivity)
    	{
    		public void onComplete() 
    		{
    			onfinish.callback(true);
    		}
    	});
	   // onfinish.callback(false);	 //继续游戏的代码  
	   // onfinish.callback(true);//退出游戏的代码 
		return true;
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		 
		 return "OK";
	};

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{ 
		 
		 return "OK";	
	}

	 public String Exist(JsonValue parms){
		 	 
	    	return "0"; 
	 }
	 
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		 
		 return "OK";	
	}
	///打开BBS 
	public  String OpenBBS(JsonValue parms){
		 return ""; 		 
	}
	
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
	
		 return "OK";	 
	}
 
	
	//账号切换
	public String AuthQuit(JsonValue parms){
		NdCommplatform.getInstance().ndLogout(NdCommplatform.LOGOUT_TO_RESET_AUTO_LOGIN_CONFIG, MoaiNd91.sActivity);//(MoaiNd91.sActivity, MoaiNd91.sLoginProcessListener); 
		return "";
	}
	
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		 MoaiNd91.is_login = false;
		return "";
	}

	//游戏登陆UI加载完后触发
	public String LoginUIInited(JsonValue parms){
		//NdCommplatform.getInstance().ndLogin(MoaiNd91.sActivity, MoaiNd91.sLoginProcessListener); 
		return "";
	}
	
	
	 
	
public void onMStop(){
	 
		
	}

public void onMResume(){
	
	 
}

}

