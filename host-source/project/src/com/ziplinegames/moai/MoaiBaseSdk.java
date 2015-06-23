package com.ziplinegames.moai;
 

import java.io.InputStreamReader;
import java.util.regex.Pattern;

import android.app.Activity;

import org.json.*;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
 
import android.os.Bundle;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

///数据统计接口
interface MoaiSDKDataInterface
{
	//数据统计统一的口子
	String callDataPost(String cmd,JsonValue parms);
 
}

///其它组件接口（和计费SDK共存类）
interface MoaiSDKBridgeInterface
{
	//其它组件统一的口子
	String callDataPost(String cmd,JsonValue parms);
 	String SDKInit(String parms);
}



//abstract class MoaiBaseSDK implements MoaiSDKInterface{
public class MoaiBaseSdk{
	
	public static Activity sActivity = null; 	
	public static MoaiBaseSdk sBaseSDK=null;
	public static int sdkversion=1;
	public static JsonObject sConfigJsonObject=null;
	public static String sdkJavaName=null;//真正的渠道java文件位置
	

	public static String sdkDataJavaName="com.ziplinegames.moai.Moaidataeye";//数据统计使用哪个类
	public static MoaiSDKDataInterface sDataInterface=null;//=new Moaidataeye();//数据统计使用哪个类

	public static String sdkAdvJavaName="com.ziplinegames.moai.Moaidomob";//广告使用的是哪个类
	public static MoaiSDKBridgeInterface sAdvInterface=null;//广告使用哪个类


	public static String sdkCdkJavaName="com.ziplinegames.moai.Moaicdkey";//CDK使用的是哪个类
	public static MoaiBaseSdk sCdkClass=null;//CDKEY使用哪个类


	public static String sdkShareJavaName="com.ziplinegames.moai.MoaisharesdkNew";//分享使用的是哪个类
	public static MoaiBaseSdk sShareClass=null;//分享使用哪个类



	//回调Lua的路由枚举 开始  
	
	public static String Lua_Cmd_LoginSuccess="/c/loginSuccess";//SDK登陆成功	
	public static String Lua_Cmd_LoginFailed="/c/loginSdkFailed";//登陆SDK失败	
	public static String Lua_Cmd_LoginOut="/c/loginLogout";//注销登陆	
	public static String Lua_Cmd_LoginCancel="/c/loginCancel";//取消登陆	 
	
	
	public static String Lua_Cmd_PaySuccess="/c/paymentSuccess";//支付成功
	public static String Lua_Cmd_PayError="/c/paymentError";//支付失败
	public static String Lua_Cmd_PayCancel="/c/paymentCancel";//取消失败	 
	public static String Lua_Cmd_PayResult="/c/payResult";//充值后回调 ，v2版本后统一
	 

	public static String Lua_Cmd_ResultAddAdv="/adv/resultAddAdv";// 积分墙事件回调接口
	public static String Lua_Cmd_ResultPoint="/adv/resultPoint";// 获取用户积分返回
	public static String Lua_Cmd_ResultConsume="/adv/resultConsume";//消费积分返回
	public static String Lua_Cmd_ResultChannelInfo="/c/channelInfoResult";//设置渠道信息

	public static String Lua_Cmd_ResultGetCdkey="/cdk/resultGetCdkey";//获取CDK回调
	public static String Lua_Cmd_ResultUseCdkey="/cdk/resultUseCdkey";//使用CDK回调

	//回调Lua的路由枚举 结束
	
	
	//外部调用Java的路由枚举  开始
	public static String Java_Cmd_SetVersion="setVersion";//设置版本信息
	public static String Java_Cmd_OpenLogin="/c/OpenLogin";//打开登陆页	
	public static String Java_Cmd_OpenPay="/c/OpenPay";//打开支付页
	public static String Java_Cmd_Exist="/c/Exist";//是否存在	
	public static String Java_Cmd_OpenMemberCenter="/c/openMemberCenter";//打开用户中心	
	public static String Java_Cmd_OpenBBS="/c/OpenBBS";//打开官方论坛
	public static String Java_Cmd_OpenWeb="/c/OpenWeb";//打开官方网站
	public static String Java_Cmd_OpenMoreGame="/c/openMoreGame";//打开更多游戏
	public static String Java_Cmd_AuthQuit="/c/authQuit";//切换账号
	public static String Java_Cmd_OpenAbout="/c/OpenAbout";//打开关于
	public static String Java_Cmd_LevelUp="/c/levelUp";//用户升级
	public static String Java_Cmd_SetCharInfo="/c/setCharInfo";//设置角色信息
	public static String Java_Cmd_LoginUIInited="/c/LoginUIInited";//账号登陆界面加载完成后执行
	public static String Java_Cmd_Login="/c/login";//登陆成功后调用
	
	public static String Java_Cmd_GetCdkey="/cdk/getCdkey";//获取cdkey
	public static String Java_Cmd_UseCdkey="/cdk/useCdkey";//使用cdkey
	 
	//结束
 
	//配置Json文件名
	public static String configFileName="cConfig.json";
	
	 //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
    	  
    	MoaiLog.i ( "MoaiCommSDK onCreate: Initializing MoaiCommSDK" );   
    	sActivity=activity;
    	loadChannelConfig();//不要和 sActivity=activity; 交换位置
    	try {
			Class<?> cls=null;
			try{
			if(sdkJavaName!=null&&!sdkJavaName.isEmpty()) cls= Class.forName(sdkJavaName);
			}
			catch (Exception e) {
				MoaiLog.e("sdkJavaName is ::::"+sdkJavaName); 
			}
			
			Class<?> clsdata=null;
			try{
			if(sdkDataJavaName!=null&&!sdkDataJavaName.isEmpty()&&sdkDataJavaName!="NO") clsdata = Class.forName(sdkDataJavaName);
			}
			catch (Exception e) {
				MoaiLog.e("sdkDataJavaName is ::::"+sdkDataJavaName); 
			}
			
			Class<?> clsadv=null;
			try{
			if(sdkAdvJavaName!=null&&!sdkAdvJavaName.isEmpty()&&sdkAdvJavaName!="NO") clsadv = Class.forName(sdkAdvJavaName);
			}
			catch (Exception e) {
				MoaiLog.e("sdkAdvJavaName is ::::"+sdkAdvJavaName); 
			}
			
			Class<?> clscdk=null;
			try{
			if(sdkCdkJavaName!=null&&!sdkCdkJavaName.isEmpty()&&sdkCdkJavaName!="NO") clscdk = Class.forName(sdkCdkJavaName);
			}
			catch (Exception e) {
			MoaiLog.e("sdkCdkJavaName is ::::"+sdkCdkJavaName); 
			}
			
		   Class<?> clsshare=null;
		   try{
			if(sdkShareJavaName!=null&&!sdkShareJavaName.isEmpty()&&sdkShareJavaName!="NO") clsshare = Class.forName(sdkShareJavaName);
		   }
		   catch (Exception e) {
				MoaiLog.e("sdkShareJavaName is ::::"+sdkShareJavaName); 
			}



			try {
				if(cls!=null) sBaseSDK=(MoaiBaseSdk)cls.newInstance();
				if(clsdata!=null) sDataInterface=(MoaiSDKDataInterface)clsdata.newInstance(); 
				if(clsadv!=null) sAdvInterface=(MoaiSDKBridgeInterface)clsadv.newInstance(); 
				if(clscdk!=null) sCdkClass=(MoaiBaseSdk)clscdk.newInstance();		
 				if(clsshare!=null) sShareClass=(MoaiBaseSdk)clsshare.newInstance();	
 

			} catch (InstantiationException e) {
				MoaiLog.e("Convert com.ziplinegames.moai."+sdkJavaName+" to BaseSDK ERROR::::"+e.getMessage()); 
				 
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				MoaiLog.e("Convert com.ziplinegames.moai."+sdkJavaName+" to BaseSDK ERROR::::"+e.getMessage()); 
				e.printStackTrace();
			}
		} catch (Exception e) {
			MoaiLog.e("not find com.ziplinegames.moai."+sdkJavaName+" to BaseSDK ERROR::::"+e.getMessage());			 
			e.printStackTrace();
		} 
		
    	if(sBaseSDK!=null) sBaseSDK.SDKInit(""); 
    	if(sAdvInterface!=null) sAdvInterface.SDKInit("");
    	if(sShareClass!=null) sShareClass.SDKInit("");
    		 
    }
    
    //获取JsonValue，如果没有则设置为默认值
    public static String GetJsonVal(JsonObject jsonObject,String key,String defVal){
    	JsonValue _jsonVal=jsonObject.get(key);
    	if(_jsonVal==null) return defVal;
    	if(_jsonVal.isNull()) return defVal;
    	if(!_jsonVal.isString()) return _jsonVal.toString();
    	 return _jsonVal.asString();
    
    }
    //获取JsonValue,如果没有字段或是字段不是整数，则返回默认值
    public static int GetJsonValInt(JsonObject jsonObject,String key,int defVal){
    	Pattern pattern = Pattern.compile("^[-+]?[0-9]*"); 
    	String str=GetJsonVal(jsonObject,key,Integer.toString(defVal));
         if(pattern.matcher(str).matches()) {
        	 return Integer.parseInt(str);
         }else{
        	 return defVal;
         }
    }
    
  //读取配置文件
	public static void loadChannelConfig(){	 
		try { 
		
            InputStreamReader inputReader = new InputStreamReader(sActivity.getResources().getAssets().open(configFileName),"GBK");
            sConfigJsonObject=JsonObject.readFrom(inputReader); 
            sdkJavaName=GetJsonVal(sConfigJsonObject,"sdkJavaName","");
            sdkDataJavaName=GetJsonVal(sConfigJsonObject,"sdkDataJavaName", "com.ziplinegames.moai.Moaidataeye");
            sdkAdvJavaName=GetJsonVal(sConfigJsonObject,"sdkAdvJavaName",   "com.ziplinegames.moai.Moaidomob");
		   sdkCdkJavaName=GetJsonVal(sConfigJsonObject,"sdkCdkJavaName",    "com.ziplinegames.moai.Moaicdkey");
		   sdkShareJavaName=GetJsonVal(sConfigJsonObject,"sdkShareJavaName","com.ziplinegames.moai.MoaisharesdkNew"); 


            } catch (Exception e) { 
            	MoaiLog.e("not find cConfig.json ERROR::::"+e.toString()); 
               // e.printStackTrace(); 
            }
	}
	

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

	
	//退出游戏时
	public static void onDestroy () 
    {	
		if(sBaseSDK!=null){
		sBaseSDK.onMDestroy();			
		}
		sActivity = null;
		
    }	
    //退出游戏时
	public void onMDestroy(){
		
		
	}
	//准备退出游戏时，是否退出可在这里加
	public static boolean onPreExit(Activity activity, final Moai.OnFinishHandler onfinish)
	{
		if(sBaseSDK!=null){
			return sBaseSDK.onMPreExit(activity,onfinish);			
		}else
		{
			onfinish.callback(true);//退出游戏的代码 			
		}
		return true;
	}

	public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

	   // onfinish.callback(false);	 //继续游戏的代码  
	    onfinish.callback(true);//退出游戏的代码 
		return true;
	}
	
	
    public static void onRestart()
	    {
		 if(sBaseSDK!=null){
			sBaseSDK.onMRestart();			
		 }
	    }
	public void onMRestart(){
		
		
	}
    
	public static void onStop()
	    {
		 if(sBaseSDK!=null){
			sBaseSDK.onMStop();			
		 }
	    }
	public void onMStop(){
		
		
	}
		 
		public static void onResume()
		{
			if(sBaseSDK!=null){
				sBaseSDK.onMResume();			
			 }
			 
		}

		public void onMResume(){
			
			
		}
        

   public static boolean onPreRunning(Activity activity, final Moai.OnFinishHandler onfinish)
	    {
			 if(sBaseSDK!=null){
					return sBaseSDK.onMPreRunning(activity,onfinish);			
			 }else
			 {
				 onfinish.callback(true);
			 }
			 
			 return true;
		 
	    }
	    
	 public boolean onMPreRunning(Activity activity, final Moai.OnFinishHandler onfinish){
		 onfinish.callback(true);
		 return true;
		}
	


         public static void onPause(){
			 if(sBaseSDK!=null){
				sBaseSDK.onMPause();
		  }
		 }
		 
		 public void onMPause(){
			 
			 
		 }                    /////////////////////add the pause in the sdk activity,shoul add in moai_realease
		
			//统一入口 
	public  static String JsonAPI(String paramInJson)
	{
		try
		{
			MoaiLog.d("from Lua: JsonAPI " + paramInJson);
			String _paramInJson=paramInJson;
			JsonObject json = JsonObject.readFrom(_paramInJson);
			String cmd = json.get("cmd").asString();
			JsonValue data=json.get("data");
		
			if(cmd.equals(Java_Cmd_SetVersion)){				 
				sdkversion=data.asInt();//设置版本号
				if(sBaseSDK!=null)
				{
					sBaseSDK.ResultChannelInfo();
				}
				return "OK";
			}
			
			
			switch(sdkversion)
			{
			case 1:return JsonAPIV1(cmd,data); 
			case 2:return JsonAPIV2(cmd,data); 
			default:return JsonAPIV1(cmd,data);
			}
	 
		} catch (Exception e) {
		  MoaiLog.d(" MoaiLog cmd error :" + e.getMessage());
		}
		return "";
	}
	 
	//V1.0版本的SDK 统一入口
	public static String JsonAPIV1(String cmd,JsonValue data){
		try
		{
			//数据统计处理
			String[] cmds=cmd.split("/");
			if(cmds.length>=1&&cmds[0].equalsIgnoreCase("d")&&sDataInterface!=null)
			{
				return sDataInterface.callDataPost(cmd,data);				
			}
			
			if(cmd.equals(Java_Cmd_OpenLogin)){return sBaseSDK.OpenLogin(data);}
			if(cmd.equals(Java_Cmd_OpenPay)){return sBaseSDK.OpenPay(data);}
			if(cmd.equals(Java_Cmd_Exist)){return sBaseSDK.Exist(data);}
			
			if(cmd.equals(Java_Cmd_OpenMemberCenter)){return sBaseSDK.OpenMemberCenter(data);}
			if(cmd.equals(Java_Cmd_OpenBBS)){return sBaseSDK.OpenBBS(data);}
			if(cmd.equals(Java_Cmd_OpenWeb)){return sBaseSDK.OpenWeb(data);}
			if(cmd.equals(Java_Cmd_AuthQuit)){return sBaseSDK.AuthQuit(data);}
			if(cmd.equals(Java_Cmd_LevelUp)){return sBaseSDK.LevelUp(data);}
			if(cmd.equals(Java_Cmd_OpenMoreGame)){return sBaseSDK.OpenMoreGame(data);}
			if(cmd.equals(Java_Cmd_OpenAbout)){return sBaseSDK.OpenAbout(data);}
			
			if(cmd.equals(Java_Cmd_SetCharInfo)){return sBaseSDK.SetCharInfo(data);}	
			if(cmd.equals(Java_Cmd_LoginUIInited)){return sBaseSDK.LoginUIInited(data);}	
			
			MoaiLog.d(" MoaiLog cmd not find :::::::" + cmd);
	 
	  } catch (Exception e) {
		  MoaiLog.d(" MoaiLog cmd error :" + e.getMessage());
	  }
		return ""; 
	}
	
	
	//V2.0版本的SDK 统一入口
	
	
	public static String JsonAPIV2(String cmd,JsonValue data){
		try
		{
			//数据统计处理
			String[] cmds=cmd.split("/");
			if(cmds.length>=3&&cmds[1].equalsIgnoreCase("d")&&sDataInterface!=null)
			{
				return sDataInterface.callDataPost(cmd,data);				
			}  
			if(cmds.length>=3&&cmds[1].equalsIgnoreCase("adv")&&sAdvInterface!=null)
			{
				return sAdvInterface.callDataPost(cmd,data);				
			} 
			
			if(sDataInterface!=null){
				sDataInterface.callDataPost(cmd, data);
			}

			if(cmds.length>=3&&cmds[1].equalsIgnoreCase("cdk")&&sCdkClass!=null)
			{
			String methodName=cmds[2];		
			methodName="V2_"+methodName;
			Object result= Moai.executeMethod (sCdkClass.getClass(), null, methodName, new Class < ? > [] {JsonValue.class}, new Object [] {data});
			if(result!=null) return result.toString();	
			else return "";
			} 

			String methodName=cmds[2];	

			if(sBaseSDK!=null)
			{ 
				methodName="V2_"+methodName;
				Object result= Moai.executeMethod (sBaseSDK.getClass(), null, methodName, new Class < ? > [] {JsonValue.class}, new Object [] {data});
				if(result!=null) return result.toString();
			}
			else if(methodName.equalsIgnoreCase("openShare")&&sShareClass!=null){
				methodName=cmds[2];
				methodName="V2_"+methodName;
				Object result= Moai.executeMethod (sShareClass.getClass(), null, methodName, new Class < ? > [] {JsonValue.class}, new Object [] {data});
				if(result!=null) return result.toString();
			}
			else
			{
				MoaiLog.d(" MoaiLog sBaseSDK  is null");		 
			}
			return "OK";
	 
	  } catch (Exception e) {
		  MoaiLog.d(" MoaiLog cmd error :" + e.getMessage());
	  }
		return ""; 
	}
	
	
	//统一出口，调用Lua
	public static String JsonRpcCall(String cmd,JsonValue dataObj){
		 
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("cmd", cmd);
		jsonObject.add("data", dataObj);
		if(sdkversion>=2){
			if(sDataInterface!=null&&cmd.equals(Lua_Cmd_PayResult)){
				sDataInterface.callDataPost(cmd, dataObj);
			}
		}
		 synchronized ( Moai.sAkuLock ) 
         {
		return Moai.AKUJsonRpcCall(jsonObject.toString());
         }
	}
	
	//统一出口，调用Lua
	public static String JsonRpcCall(String cmd,String parms){
		 
		 synchronized ( Moai.sAkuLock ) 
         {
			 return Moai.AKUJsonRpcCall(FormatParms(cmd,parms));
         }
	}
	
	///格式化输入字符串成Json格式
	public static String FormatParms(String cmd,String data){
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("cmd", cmd);
		jsonObject.add("data", data);
		return jsonObject.toString();
	}
	 
	
 
	 
	public void SDKInit(String parms){
		
	}


 
    public void ResultChannelInfo(){

    }
    public String Exist(JsonValue parms){
    	return "0";
    	
    }
	
    public String OpenAbout(JsonValue parms){
    	return "OK";
    }
	///打开登陆界面
	public String OpenLogin(JsonValue parms)
	{
		 return "";
	}

	///打开支付界面
	public String OpenPay(JsonValue parms)
	{
		return "";
	}

	///打开会员中心 
	public  String OpenMemberCenter(JsonValue parms){
		return ""; 
	}
	 
	///打开BBS 
	public  String OpenBBS(JsonValue parms){
		return ""; 		 
	}
	
	///打开官网
	public  String OpenWeb(JsonValue parms){
		return ""; 
	}
	
	///打开更多游戏
	public  String OpenMoreGame(JsonValue parms){
		return ""; 
	}
	
	///退出游戏
	public String ExitGame(JsonValue parms){
		return "";
	}
	
	
	
	//账号切换
	public String AuthQuit(JsonValue parms){
		return "";
	}
	//角色升级
	public String LevelUp(JsonValue parms){
		return "";
	}
	
	//角色设置角色信息
	public String SetCharInfo(JsonValue parms){
		return "";
	}
	
	//游戏登陆UI加载完后触发
	public String LoginUIInited(JsonValue parms){
		 
		return "";
	}
	
	
	 
	
} 
 