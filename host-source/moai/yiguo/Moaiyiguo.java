//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;



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

import com.pada.gamesdk.PadaSDKManager;
import com.pada.gamesdk.constant.PadaSDKConstant;
import com.pada.gamesdk.sdkinterface.ILoginCallbackListener;
import com.pada.gamesdk.sdkinterface.IPayCallbackListener;
import com.pada.padasf.sdk.entry.PadaOrderInfo;










 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiyiguo extends  MoaiBaseSdk {
	 
	public static String appKey = "";
	public static String appId = "";
	public static String paynotifyUrl = "";
	public static String uid = "";
	public static String gameId = "";
	public static String serverId = "";
	public static String roleId = "";
	
	public static PadaSDKManager YiGuo = PadaSDKManager.init(sActivity);
	 
	 
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
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();
		//jsonData.add("isUserCenter",true);

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);
		

    }
	
	public static  String V2_OpenPlatform(JsonValue parms){
		

        
		return ""; 		 
	}

	//SDK初始化	  
	public  void SDKInit(String parms){	 	
		 
		 appKey	 =  GetJsonVal(sConfigJsonObject,"appKey","000");
	     appId  =  GetJsonVal(sConfigJsonObject,"appId","000");
	     
	     
	     YiGuo.initialAppInfo( Integer.parseInt(appId) , appKey , PadaSDKConstant.GAME_TYPE_ONLINE );
            
	}
	
	public void onMResume(){
		

	}

//退出游戏时
	public void onMDestroy(){
		
	}
//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	
	
	// 艺果登陆监听
	
	private static class SDKLoginCallBack implements ILoginCallbackListener
    {

	@Override
	//登录失败 
	public void onPadaLoginFail( int errorCode )
	{
	    switch ( errorCode )
	    {
		case PadaSDKConstant.SDK_PSF_NOT_INSTALL :
		    //padaServiceFramework没有安装
			  JsonRpcCall(Lua_Cmd_LoginOut,"");
		    break;
		case PadaSDKConstant.SDK_PSF_INSTALLING :
		    //padaServiceFramework正在安装
			  JsonRpcCall(Lua_Cmd_LoginOut,"");
		    break;
		case PadaSDKConstant.SDK_NOT_INITIALED :
		    //在调用SDK前没有初始化，既没有调用init  
			JsonRpcCall(Lua_Cmd_LoginOut,"");
		    break;
		    
		case PadaSDKConstant.SDK_APPINFO_NOT_INITIALED :
		    //之前没有调用initialAppInfo
			  JsonRpcCall(Lua_Cmd_LoginOut,"");
		    break;
		case PadaSDKConstant.SDK_USER_CANCEL :
		    //用户取消
			  JsonRpcCall(Lua_Cmd_LoginOut,"");
		    break;

		default :
		    break;
	    }
	    Log.e( "APP Sample" , "onPadaLoginFail errorCode:" + errorCode );
	    // 出错处理
	}

	@Override
	// 登录成功
	public void onPadaLoginSuccess()
	{

	   uid = PadaSDKManager.getInstance( ).getLoginRoleId( );
	    String roleName = PadaSDKManager.getInstance( ).getLoginRoleName( );
	    String roleToken = PadaSDKManager.getInstance( ).getLoginRoleToken( );
	    
    	JsonObject jsonData=new JsonObject();
        jsonData.add("uid", uid);
        jsonData.add("token",roleToken);
        jsonData.add("roleName",roleName);

        JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
        jsonParms.add("data", jsonData);
         
        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);


	   
	}

    }
	

	///打开登陆界面
	public static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		YiGuo.initialAppInfo( Integer.parseInt(appId) , appKey , PadaSDKConstant.GAME_TYPE_ONLINE );
		YiGuo.padaLogin( sActivity , new SDKLoginCallBack());
				
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();
			
			String orderNo=payinfoJson.get("orderno").asString();
			int price=Integer.valueOf(payinfoJson.get("price").asString());
			String productName = payinfoJson.get("name").asString();
			String roleName  = userinfoJson.get("roleName").asString();
			String roleLv = userinfoJson.get("roleLv").asString();
			
			PadaOrderInfo orderInfo = new PadaOrderInfo( );
			orderInfo.setItemName( productName );
		
			orderInfo.setItemCode( "" );
	
			orderInfo.setItemCount(1);
			// 设置购买物品的价值，以Pa币为单位，1RMB=10Pa币
			orderInfo.setValue(price*10);
			// 设置cp端的订单号，用于游戏与cp业务服务器对账
			orderInfo.setCpOrderId( orderNo);
			/** 
			 * 1.正式服务器中：设置附加信息，供游戏使用的透传参数，pada服务器透传给cp业务服务器，并在订单完成后由SDK返回;
			 * 2.测试服务器中：如果使用demo中的测试appId=25&appKey=71ce902e83323b124b0c102aa0ac9f86进行消费测试请更改此参数值，
			 * 更换参数值的格式如“callbackurl=你方提供的消费测试回调地址”，即用你方提供的消费回调地址替换http://awszp2.awbase.com:8002/, 如未修改此参数值，测试消费过程中不进行回调；
			 **/
			String callBackUrl = GetJsonVal(sConfigJsonObject,"callBackUrl","000");
			orderInfo.setExInfo( callBackUrl );
			YiGuo.initialAppInfo( Integer.parseInt(appId) , appKey , PadaSDKConstant.GAME_TYPE_ONLINE );
			
			YiGuo.padaPay( sActivity , orderInfo , new SDKPayCallBack( ) );
			

		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	
	//艺果支付回调
	 private  static class SDKPayCallBack implements IPayCallbackListener
	    {
		@Override
		//购买成功
		public void onPadaPaySuccess( PadaOrderInfo orderInfo )
		{
			JsonRpcCall(Lua_Cmd_PaySuccess,"");
		}

		@Override
		//购买失败
		public void onPadaPayFail( int errorCode )
		{
		    switch ( errorCode )
		    {
			case PadaSDKConstant.SDK_PSF_NOT_INSTALL :
			    //padaServiceFramework没有安装
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_PSF_INSTALLING :
			    //padaServiceFramework正在安装
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_NOT_INITIALED :
			    //在调用SDK前没有初始化，既没有调用init
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_APPINFO_NOT_INITIALED :
			    //之前没有调用initialAppInfo
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_ORDER_NOT_INITIALED :
			    //没有设置PadaOrderInfo
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_USER_CANCEL :
			    //用户取消
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_BALANCE_NOT_ENOUGH :
			    //余额不足
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_ROLE_TOKEN_FAIL :
			    //角色token失效,当前情况下，游戏可以正常进行，但如需消费需重新角色登录。
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			case PadaSDKConstant.SDK_NOT_LOGIN :
			    //用户还没有登录，当前情况下，游戏可以正常进行，但如需消费需重新登录。
			    //ps必须在主线程里调用登录，和消费接口
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
			default :
			    //支付失败
				 JsonRpcCall(Lua_Cmd_PayError,"");
			    break;
		    }

		}
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

		
		YiGuo.initialAppInfo( Integer.parseInt(appId) , appKey , PadaSDKConstant.GAME_TYPE_ONLINE );
		YiGuo.padaLogin( sActivity , new SDKLoginCallBack());

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
		String roleLv = GetJsonVal(jsonData,"roleLv","000");
		String roleName = GetJsonVal(jsonData,"roleName","000");
		 roleId = GetJsonVal(jsonData,"roleId","000");
		String serverName = GetJsonVal(jsonData,"serverName","渠道");
		


		return "";
	}
	
 
}
