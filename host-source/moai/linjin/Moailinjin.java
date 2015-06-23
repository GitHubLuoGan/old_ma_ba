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
import android.content.Intent; 
 
//import com.channel.channeldemo.MainActivity;
import com.dataeye.DCCoin;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.xinmei365.game.proxy.GameProxy;
import com.xinmei365.game.proxy.PayCallBack;
import com.xinmei365.game.proxy.XMExitCallback;
import com.xinmei365.game.proxy.XMUser;
import com.xinmei365.game.proxy.XMUserListener;
import com.ziplinegames.moai.Moai.OnFinishHandler;

import android.app.Activity;
import android.content.Intent;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moailinjin  extends  MoaiBaseSdk {
	 
	private static XMUserListener sXMUserListener = new XMUserListener() {
	      @Override
	      public void onLogout(Object params) {
	          // 游戏相关的⽤户登出注销逻辑需要写在这⾥
	          // 当渠道的⽤户中⼼界⾯中点击注销，该函数会被调⽤
	          // 当游戏调⽤ GameProxy.getInstance().logout(context, params)时，该函数也会被调⽤
	          // 其中params为⽤户调⽤ GameProxy.getInstance().logout(context, params) 时传⼊的params
	          // params可以⽤来传递⼀些游戏中的上下⽂，当然传null是可以的
	          // 确保游戏中任何时候，该函数被调⽤后，游戏可以正确登出
	    	  JsonRpcCall(Lua_Cmd_LoginOut,"");
	      }
	      @Override
	      public void onLoginSuccess(XMUser user, Object params) {
	          // 游戏的登陆逻辑需要写在这⾥
	          // 当游戏调⽤ GameProxy.getInstance().login(context, params) 时，该函数会被调⽤
	          // 其中user对象为渠道登陆系统返回的⽤户相关参数，包含uid token 渠道号等
	          // 其中params为调⽤GameProxy.gatInstance().login(context, params)时，传⼊的params
	          // 确保该函数被调⽤后，⽤户可以正确地进⼊游戏
	    	      String userId  = user.getUserID();   //server
			      String channel = user.getChannelID(); //server
			      String token    = user.getToken();  //server
			      String productCode = user.getProdcutCode(); //server
			      String channelUserId    = user.getChannelUserId();
			      
			      
			      JsonObject jsonData=new JsonObject();
	              jsonData.add("uid", userId);
	              jsonData.add("channelId",channel);
	              jsonData.add("token",token);
	              jsonData.add("productId",productCode);
	              
	              JsonObject jsonParms=SDKFormatGateWay(userId,jsonData);
	              jsonParms.add("data", jsonData);
	              
	              JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
	      }
	      @Override
	      public void onLoginFailed(String reason, Object params) {
	          // 当⽤户在渠道登陆得到失败结果时，该函数会被调⽤
	    	  JsonRpcCall(Lua_Cmd_LoginCancel,"");
	      }
	  };
	 
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
		 
        GameProxy.getInstance().applicationInit(sActivity);
	    GameProxy.getInstance().onCreate(sActivity);
	    GameProxy.getInstance().setUserListener(sActivity, sXMUserListener);
		
	}

//退出游戏时
	public void onMDestroy(){
		
		GameProxy.getInstance().onDestroy(sActivity);
		GameProxy.getInstance().applicationDestroy(sActivity);
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
		GameProxy.getInstance().exit(sActivity, new XMExitCallback() {

		    @Override
		    public void onNo3rdExiterProvide() {
		        // 无渠道退出界面时，游戏自行处理退出界面
		    	onfinish.callback(true);//退出游戏的代码
		    }

		    @Override
		    public void onExit() {
		        // 有渠道退出界面时，完成渠道退出界面后，会调用此函数
		        // 游戏要在这里完成游戏的退出
		    	onfinish.callback(true);//退出游戏的代码
		    }
		});
	     
		return true;
	}

	///打开登陆界面
	public  String OpenLogin(JsonValue parms){ 
		//登陆
		 JsonRpcCall(Lua_Cmd_LoginCancel,"");
		GameProxy.getInstance().login(sActivity, "login");
		 return "OK";
	};
	
	///切换账号
	public  String AuthQuit(JsonValue parms){ 
		//
		 GameProxy.getInstance().logout(sActivity, "logout");
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
			String info=sConfigJsonObject.get("productName").asString();
			int count = payinfoJson.get("amount").asInt();
			String callBackUrl = sConfigJsonObject.get("callBackUrl").asString();
			String callBackInfo = OrderNo;
			
			if(MoaiBaseSdk.sdkversion > 1 )price *= 0.01; /*************不同版本价格传入单位不同************/
		//	public void charge(Context context, String itemName, int unitPrice, int defaultCount, String callBackInfo, String callBackUrl, PayCallBack payCallBack);

		//	参考例子：
			GameProxy.getInstance().charge(sActivity, info, price*100, count,callBackInfo, callBackUrl, new PayCallBack() {

			    @Override
			    public void onSuccess(String successInfo) {
			    	JsonRpcCall(Lua_Cmd_PaySuccess,"支付成功");
			    }

			    @Override
			    public void onFail(String failInfo) {
			        //支付失败后
			    	JsonRpcCall(Lua_Cmd_PayCancel,"支付失败");
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
	 
	///退出游戏
	public String ExitGame(JsonValue parms){	 
		 return "OK";	 
	}
 
	
	/////////////////////////////////////////////////////////linjin_start
	public void onMStop(){
		
		GameProxy.getInstance().onStop(sActivity);	
	}

	
	public void onMResume(){
		GameProxy.getInstance().onResume(sActivity);
	}
	
	 public void onMPause(){	 
		 GameProxy.getInstance().onPause(sActivity); 
	}
	
	 public void onMRestart(){
		 GameProxy.getInstance().onRestart(sActivity);		
	}
	 
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    GameProxy.getInstance().onActivityResult(sActivity, requestCode,
		        resultCode, data);		    
	}
	 
	
	 
	////////////////////////////////////////////////////////linjin_end
	//角色信息
	public String SetCharInfo(JsonValue parms){
	
 /*
context 当前游戏的上下文，请传让activity，不要使用getApplication()

roleId 当前登录的玩家角色ID--

roleName 当前登录的玩家角色名--

roleLevel 当前登录的玩家角色等级--

zoneId 当前登录的游戏区服ID--

zoneName 当前登录的游戏区服名称--

balance 当前用户游戏币余额

vip 当前用户VIP等级--

partyName 当前用户所属帮派-- */
		    JsonObject jsonData=parms.asObject();
		
		    JsonObject jsonObject = new JsonObject();
			jsonObject.add("zoneId", jsonData.get("serverIndex").asString());
			jsonObject.add("roleId", jsonData.get("RoleId").asString());
			jsonObject.add("roleLevel", jsonData.get("Rolelv").asString());
			jsonObject.add("vip", jsonData.get("vipLevel").toString());
			jsonObject.add("partyName", jsonData.get("RoleClass").toString());
			jsonObject.add("roleName", jsonData.get("RoleName").toString());
			jsonObject.add("zoneName", jsonData.get("serverName").toString());
		
			int Balance = 0;
			
			  JsonValue jsonVal=jsonData.get("moneyMap");
			  if(jsonVal != null){
			  JsonArray moneyMap=jsonVal.asArray();
			  if(moneyMap!=null){
			   JsonObject mapJson=null;
			   
			    mapJson=moneyMap.get(0).asObject(); 
			    Balance =  MoaiBaseSdk.GetJsonValInt(mapJson,"money",0);
			    DCCoin.setCoinNum(MoaiBaseSdk.GetJsonValInt(mapJson,"money",0),MoaiBaseSdk.GetJsonVal(mapJson,"type","0")); 
			    
				   
			  }
			  }
		       
			  
			  jsonObject.add("balance", Balance);
			  
			String Json_string = jsonObject.toString();

			GameProxy.getInstance().setExtData(sActivity, Json_string);
			
			//money
			//RoleName
			//serverName
		
		return "ok";
	}
	
	
 
}

