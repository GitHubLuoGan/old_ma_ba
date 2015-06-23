package com.ziplinegames.moai;



import android.app.Activity;
import android.util.Log;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.pps.sdk.platform.PPSPlatform;
import com.pps.sdk.platform.PPSResultCode;
import com.pps.sdk.listener.PPSPlatformListener;
import com.pps.sdk.services.PPSUser;

 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiaiqiyi  extends  MoaiBaseSdk {
	 
	
	static String TAG = "";
	static String OrderNo="";
	static String price="";
	static String roleId="";
	static String gameId="";
	static String userData="";
	static int price_int = 0;
	public static String uid = "";
	static String serverId="";
	static String serverIndex="";
	static String PPSmobile="ppsmobile_s";

	
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
		
		gameId  =  GetJsonVal(sConfigJsonObject,"gameid","000");
		PPSPlatform.initPPSPlatform(sActivity, gameId);
	}
	public void onMPause(){
		super.onMPause();
	 }      
	public void onMResume(){
		super.onResume();
	}
	
	public void onMStop(){
		super.onMStop();

	}
	 
//退出游戏时
	public void onMDestroy(){
		super.onMDestroy();
	
		
	}
	//是否退出时执行
	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}


	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
//		//登陆
		PPSPlatform.getInstance().ppsLogin(sActivity,new PPSPlatformListener() {
			@Override
			public void leavePlatform() {
				// TODO Auto-generated method stub
				super.leavePlatform();
				System.out.println("离开PPS游戏联运平台");
			}

			@Override
			public void loginResult(int resultCode, PPSUser user) {
				// TODO Auto-generated method stub
				super.loginResult(resultCode, user);
				if(resultCode == PPSResultCode.SUCCESSLOGIN){
					uid = user.uid;
					String timestamp = String.valueOf(user.timestamp);
					String sign= user.sign;
					
	            	JsonObject jsonData=new JsonObject();
	                jsonData.add("uid", uid);
	                jsonData.add("timestamp",timestamp);	                
	                jsonData.add("sign",sign);
	                

	                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
	                jsonParms.add("data", jsonData);
	                 
	                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);

						Log.d(TAG,"登陆成功2222："+uid);
						Log.d(TAG,"登陆成功2222："+timestamp);
						Log.d(TAG,"登陆成功2222："+sign);
				}
				if(resultCode == PPSResultCode.ERRORLOGIN){
					Log.d(TAG,"登录失败3333:");
				}
			}
		});
		

	 return "OK";
	};

	

	
	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		
			
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userinfoJson=_json.get("userInfo").asObject();

			OrderNo 	= payinfoJson.get("orderno").asString(); 
			price		= payinfoJson.get("price").asString();
			userData	= payinfoJson.get("orderno").asString();
			roleId		= userinfoJson.get("roleId").asString();
			serverIndex	= userinfoJson.get("serverIndex").asString();
			price_int 	= Integer.parseInt(price);
			serverId	= PPSmobile+serverIndex;

			
			
			
			PPSPlatform.getInstance().ppsPayment(sActivity,1,roleId,serverId,userData,new PPSPlatformListener() {
				@Override
				public void leavePlatform() {
					// TODO Auto-generated method stub
					super.leavePlatform();
					System.out.println("离开PPS游戏联运平台");
				}

				@Override
				public void paymentResult(int result) {
					// TODO Auto-generated method stub
					super.paymentResult(result);
					if (result == PPSResultCode.SUCCESSPAYMENT) {
						
						System.out.println("充值成功");
						//PPS服务器会通知游戏方后台进行发放元宝
						//这个地方只是作为一个提示充值成功，不代表PPS后台已经成功通知游戏方发放元宝完成
					}else{
						System.out.println("充值失败");  
					}
				}

			});
			}catch (Exception e) { 
				MoaiLog.i(" OpenPay is Error:"+e.getMessage());
				}
			return "ok";
	}


	
	//账号切换
	public static String V2_authQuit(JsonValue parms){
		
		
		PPSPlatform.getInstance().ppsChangeAccount(sActivity,new PPSPlatformListener(){
			@Override
			public void leavePlatform() {
				// TODO Auto-generated method stub
				super.leavePlatform();
				System.out.println("离开PPS游戏联运平台");
			}

			@Override
			public void loginResult(int resultCode, PPSUser user) {
				// TODO Auto-generated method stub
				super.loginResult(resultCode, user);
				if(resultCode == PPSResultCode.SUCCESSLOGIN){
					
					System.out.println("用户登入成功");
					System.out.println("uid => " + user.uid);
					System.out.println("timestamp => " + user.timestamp);
					System.out.println("sign => " + user.sign);
					
					//添加 SDK浮标
					PPSPlatform.getInstance().initSlideBar(sActivity);
				}
				if(resultCode == PPSResultCode.ERRORLOGIN){
					System.out.println("用户登入失败");
				}
			}
			@Override
			public void logout() {
				// TODO Auto-generated method stub
				super.logout();
				if(!PPSPlatform.getInstance().isLogin()){
					System.out.println("账号已经注销");
				}
			}   
		});

		 return "ok";
	}
	

	

	
	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){

	 
		 return "OK";	
	}
	public static String V2_exitGame(JsonValue parms){

		JsonRpcCall(Lua_Cmd_GameExit,"");
		return "";
	}
	///退出游戏
	public String ExitGame(JsonValue parms){
		PPSPlatform.getInstance().ppsLogout(sActivity, new PPSPlatformListener() {
			@Override
			public void logout() {
				// TODO Auto-generated method stub
				super.logout();
				//账户已经成功退出   游戏方需要在此回调中关闭游戏进程

			}
		});
		return "ok";
	}
 
	
	//角色信息
	public static String V2_SetCharInfo(JsonValue parms){
		try {
		JsonObject jsonData=parms.asObject(); 
		return "";
	}catch (Exception e) {
        Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
        
    }
   return "OK";
	
	}
}