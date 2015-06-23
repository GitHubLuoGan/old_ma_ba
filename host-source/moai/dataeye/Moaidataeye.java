package com.ziplinegames.moai;

import java.lang.reflect.Method;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.dataeye.AccountType;
import com.dataeye.DCAccount;
import com.dataeye.DCAgent;
import com.dataeye.DCCoin;
import com.dataeye.DCItem;
import com.dataeye.DCTask;
import com.dataeye.DCVirtualCurrency;
import com.dataeye.plugin.DCLevels;
public class Moaidataeye implements MoaiSDKDataInterface{
	public	static Activity sActivity = null; 
	public static int channelId=0;
	public static String channelName=null;
	 //----------------------------------------------------------------//
    public static void onCreate ( Activity activity ) 
    {	
    	sActivity=activity;
    	if(MoaiBaseSdk.sDataInterface!=null)
    	SDKInit(""); 
    	//DCAgent.setDebugOn();
    }
    public static void onResume () 
    {	
    	if(MoaiBaseSdk.sDataInterface!=null)
    	 DCAgent.onResume(sActivity); 
    	 
    }
    public static void onPause () 
    {    if(MoaiBaseSdk.sDataInterface!=null)
    	 DCAgent.onPause(sActivity); 
    	 
    }
    //入口
	public String callDataPost(String cmd,JsonValue parms){
	 
		return callDataPostF(cmd,parms);
		/*if(cmd.equals(MoaiBaseSdk.Java_Cmd_SetCharInfo)) return setCharInfo(parms); 
		if(cmd.equals(MoaiBaseSdk.Java_Cmd_Login)) return  login(parms); 
		if(cmd.equals(MoaiBaseSdk.Java_Cmd_AuthQuit)) return  authQuit(parms); 
		if(cmd.equals(MoaiBaseSdk.Java_Cmd_OpenPay)) return OpenPay(parms); 
		return "OK";		*/
	}
	public static String callDataPostF(String cmd,JsonValue parms){
		//数据统计处理
		String[] cmds=cmd.split("/");
		if(cmds.length<3||MoaiBaseSdk.sdkversion<2) 
			{
			MoaiLog.e("数据格式错误或SDK版本过低 cmd:"+cmd+ " sdkversion:"+MoaiBaseSdk.sdkversion);
			return "false";			
			}
		String methodName=cmds[2];		 
		Object result= Moai.executeMethod(Moaidataeye.class, null, methodName, new Class < ? > [] {JsonValue.class}, new Object [] {parms});
		if(result!=null) return result.toString();
		else return ""; 
	}
	
	public static String SDKInit(String  parms){
		return "OK";		
	}
	
	//登陆
	public static String login(JsonValue parms){
		/* 根据Dataeye要求，屏蔽角色登陆
		JsonObject jsonobj=parms.asObject();
		String roleid=MoaiBaseSdk.GetJsonVal(jsonobj,"roleId","0");
		DCAccount.login(roleid);
		*/
		return "OK";
	}
	
	//退出
	public static String authQuit(JsonValue parms){		
	/*	JsonObject jsonobj=parms.asObject();
		String roleid=MoaiBaseSdk.GetJsonVal(jsonobj,"roleId","0");*/
		DCAccount.logout();
		return "OK";
	}
	
	//设置用户信息
	public static String setCharInfo(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String roleid=MoaiBaseSdk.GetJsonVal(jsonobj,"roleId","0");
		
		channelId=MoaiBaseSdk.GetJsonValInt(MoaiBaseSdk.sConfigJsonObject,"channelId",0);
		channelName=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject,"channelName","测试渠道");
		DCAccount.setAccountType(channelId);
		
		DCAccount.setLevel(MoaiBaseSdk.GetJsonValInt(jsonobj,"roleLV",0));
		DCAccount.setGender(MoaiBaseSdk.GetJsonValInt(jsonobj,"gender",0));
		DCAccount.setAge(MoaiBaseSdk.GetJsonValInt(jsonobj,"age",0));
		//DCAccount.setGender(MoaiBaseSdk.GetJsonValInt(jsonobj,"age",0));
		DCAccount.setGameServer(MoaiBaseSdk.GetJsonVal(jsonobj,"servername",""));
		
		//设置用户货币信息
		JsonValue jsonVal=jsonobj.get("moneyMap");
		if(jsonVal==null) return "OK";
		JsonArray moneyMap=jsonVal.asArray();
		if(moneyMap!=null){
			JsonObject mapJson=null;
			for(int i=0;i<moneyMap.size();i++){
				mapJson=moneyMap.get(i).asObject();
				DCCoin.setCoinNum(MoaiBaseSdk.GetJsonValInt(mapJson,"money",0),MoaiBaseSdk.GetJsonVal(mapJson,"type","0")); 
				
			}
			
			
		}
		return "OK";
	}
	//用户升级
	public static String levelUp(JsonValue parms){ 
		DCAccount.setLevel(parms.asInt());
		return "OK";
	}
	//充值开始
	public  static String OpenPay(JsonValue parms){		
		JsonObject jsonobj=parms.asObject();
		JsonObject	payInfo=null;
		JsonObject	userInfo=null;
		 
		JsonObject	userinfoJson=null;
		if(MoaiBaseSdk.sdkversion==1){
			if(jsonobj.get("payinfo").isNull()) MoaiLog.e("payinfo is not find");
			if(jsonobj.get("userinfo").isNull()) MoaiLog.e("userinfo is not find");
			payInfo=jsonobj.get("payinfo").asObject();
		 	userInfo=jsonobj.get("userinfo").asObject(); 
		}else
		{
			if(jsonobj.get("payInfo").isNull()) MoaiLog.e("payInfo is not find");
			if(jsonobj.get("userInfo").isNull()) MoaiLog.e("userInfo is not find");
			payInfo=jsonobj.get("payInfo").asObject();
		 	userInfo=jsonobj.get("userInfo").asObject(); 
		}
		
		String orderId=MoaiBaseSdk.GetJsonVal(payInfo,"orderno","0");
		int total=MoaiBaseSdk.GetJsonValInt(payInfo,"total",0);
		String currencyType="CNY";
		if(channelName==null) channelName="充值";
		String paymentType=channelName;
		DCVirtualCurrency.onCharge(orderId,((double)total)/100.00, currencyType,paymentType);
		return "OK";
		
	}
	
	//充值成功
	public static String payResult(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		int code=MoaiBaseSdk.GetJsonValInt(jsonobj,"code",0);
		if(code==1){			
			JsonObject payData=jsonobj.get("payData").asObject();
			JsonObject payInfo=payData.get("payInfo").asObject();
			String orderId=MoaiBaseSdk.GetJsonVal(payInfo,"orderno","0");
			DCVirtualCurrency.onChargeSuccess(orderId);
		} 
		return "OK";
		
	}
	
	//购买道具
	public static String buyItem(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String itemId=MoaiBaseSdk.GetJsonVal(jsonobj, "itemId", "0");//道具ID 或名称，请务必保证该值的唯一性	 
		String itemType=MoaiBaseSdk.GetJsonVal(jsonobj, "itemType", "0");//道具类型，非空，最多32 位字符。 
		int itemCnt=MoaiBaseSdk.GetJsonValInt(jsonobj, "itemCnt", 0);//购买的道具数量。
		int vituralCurrency=MoaiBaseSdk.GetJsonValInt(jsonobj, "vituralCurrency", 0);//购买道具消耗的虚拟币数量。 
		String currencyType=MoaiBaseSdk.GetJsonVal(jsonobj, "currencyType", "0");//虚拟币类型。非空，最多32 位字符。 
		DCItem.buy(itemId, itemType, itemCnt, vituralCurrency, currencyType);
	 
		return "OK";
		
	}
	//购买道具
	public static String changeItem(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String itemId=MoaiBaseSdk.GetJsonVal(jsonobj, "itemId", "0");//道具ID 或名称，请务必保证该值的唯一性	 
		String itemType=MoaiBaseSdk.GetJsonVal(jsonobj, "itemType", "0");//道具类型，非空，最多32 位字符。
		int itemCnt=MoaiBaseSdk.GetJsonValInt(jsonobj, "itemCnt", 0);//购买的道具数量。
		String reason=MoaiBaseSdk.GetJsonVal(jsonobj, "reason", "0");//道具变化的原因。
		if(itemCnt>0){
			DCItem.get(itemId, itemType, Math.abs(itemCnt), reason);			
		}
		if(itemCnt<0){
			DCItem.use(itemId, itemType, Math.abs(itemCnt), reason);	
		}
		if(itemCnt==0){
			MoaiLog.e("变化0个就不用记录了吧。。。");
		}
		return "OK";
	}
	public static String setTask(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		int status=MoaiBaseSdk.GetJsonValInt(jsonobj, "status", 0);//状态 0：开始 1 ：成功 -1：失败 
		String taskId=MoaiBaseSdk.GetJsonVal(jsonobj, "taskId", "");//任务Id 或者任务名，请确保其唯一性，非空，最多32 个字符。
		int taskType=MoaiBaseSdk.GetJsonValInt(jsonobj, "type", 0);/*
		type： Int, 任务类型，枚举如下
		      1:表示新手任务
			2:表示主线任务
			3:表示支线任务
			4:表示日常任务
			5:表示活动任务
			6:表示其它任务。
		
		*/
		String reason=MoaiBaseSdk.GetJsonVal(jsonobj, "reason", "");//备注或原因。
		if(status==0){
			DCTask.begin(taskId, taskType);			
		}
		
		if(status==1){
			DCTask.complete(taskId);
			
		}
		if(status==-1){			
			DCTask.fail(taskId, reason);
		}		
		return "OK";
	}
	
	//货币变化
	public static String changeMoney(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String reason=MoaiBaseSdk.GetJsonVal(jsonobj, "reason", "0");
		int money=MoaiBaseSdk.GetJsonValInt(jsonobj, "money", 0);
		int left=MoaiBaseSdk.GetJsonValInt(jsonobj, "left", 0);
		String moneyType=MoaiBaseSdk.GetJsonVal(jsonobj,"moneyType","0");
		
		if(money>=0){
			DCCoin.gain(reason,moneyType, Math.abs(money), left);
		}
		if(money<0){
			DCCoin.lost(reason,moneyType, Math.abs(money),left);
		}
		
		return "OK";
		/*
		reason：String,变化的原因（可以制定原因列表，将列表中的ID 号传入） 
		money：int,玩家变化的虚拟币数量，正数为增加，负数为减少。 
		left：int,剩余总量。 
		moneyType：String,货币类型*/

	}
	//关卡情况
	public static String setlevel(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		/*
		status：Int,状态 0：开始 1 ：成功 -1：失败 
		levelNumber：Int,关卡在的序列号，如关卡1、2、3、4 等。 
		levelId：String,关卡名或ID 号。
		msg：String,可在此写失败点或原因
*/
		int status=MoaiBaseSdk.GetJsonValInt(jsonobj, "status", 0);
		int levelNumber=MoaiBaseSdk.GetJsonValInt(jsonobj, "levelNumber", 0);
		String levelId=MoaiBaseSdk.GetJsonVal(jsonobj, "levelId", "0");
		String msg=MoaiBaseSdk.GetJsonVal(jsonobj, "msg", "0");
		if(status==0)
		{
			DCLevels.begin(levelNumber, levelId);			 
		}
		
		if(status==1)
		{
			DCLevels.complete(levelId);			 
		}
		
		if(status==-1)
		{
			DCLevels.fail(levelId, msg);			 
		}
		return "OK";
	}
	
	//自定义事件计数
	public static String onEventCount(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		int isHigh=MoaiBaseSdk.GetJsonValInt(jsonobj, "isHigh", 0);
		String eventId=MoaiBaseSdk.GetJsonVal(jsonobj, "eventId", "0");
		if(isHigh==0){			
			DCAgent.onEvent(eventId);
		}
		if(isHigh==1){
			
			DCAgent.onEventCount(eventId);
		}
		return "OK";
	}
	
	//多属性事件
	public static String onEventMore(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String eventId=MoaiBaseSdk.GetJsonVal(jsonobj, "eventId", "0");
		//设置事件属性
		JsonValue jsonVal=jsonobj.get("map");
		if(jsonVal==null) return "OK";
		JsonObject mapObject=jsonVal.asObject();
		Map<String,String> dataMap=new HashMap<String,String>();
		if(mapObject!=null){ 
			List<String> names =mapObject.names();	
			for(int i=0;i<names.size();i++){
				dataMap.put(names.get(i),MoaiBaseSdk.GetJsonVal(mapObject,names.get(i),"0"));			 
			}
		}
		DCAgent.onEvent(eventId, dataMap);
		
		return "OK";
	}
	
	
	//多属性时长
	public static String onEventDuration(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		String eventId=MoaiBaseSdk.GetJsonVal(jsonobj, "eventId", "0");
		int duration=MoaiBaseSdk.GetJsonValInt(jsonobj, "eventId", 0);
		//设置事件属性
		JsonValue jsonVal=jsonobj.get("map");
 
		if(jsonVal==null) return "OK";
		JsonObject mapObject=jsonVal.asObject();
		Map<String,String> dataMap=new HashMap<String,String>();
		if(mapObject!=null){ 
			List<String> names =mapObject.names();	
			for(int i=0;i<names.size();i++){
				dataMap.put(names.get(i),MoaiBaseSdk.GetJsonVal(mapObject,names.get(i),"0"));			 
			}
		}
		DCAgent.onEventDuration(eventId, dataMap,duration);
		
		return "OK"; 
	}
	
	//设置事件
	public static String setEvent(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		int status=MoaiBaseSdk.GetJsonValInt(jsonobj, "status", 0);
		String eventId=MoaiBaseSdk.GetJsonVal(jsonobj, "eventId", "0");
		int duration=MoaiBaseSdk.GetJsonValInt(jsonobj, "eventId", 0);
		String flag=MoaiBaseSdk.GetJsonVal(jsonobj, "flag", "0");
		//设置事件属性
		JsonValue jsonVal=jsonobj.get("map");
 
		if(jsonVal==null) return "OK";
		JsonObject mapObject=jsonVal.asObject();
		Map<String,String> dataMap=new HashMap<String,String>();
		if(mapObject!=null){ 
			List<String> names =mapObject.names();	
			for(int i=0;i<names.size();i++){
				dataMap.put(names.get(i),MoaiBaseSdk.GetJsonVal(mapObject,names.get(i),"0"));			 
			}
		}
		if(status==0){
			if(flag!="")
			DCAgent.onEventBegin(eventId, dataMap, flag);
			else
			DCAgent.onEventBegin(eventId, dataMap);
		}
		if(status==1){
			if(flag!="")
			DCAgent.onEventEnd(eventId, flag);
			else
			DCAgent.onEventEnd(eventId);
		}
	 
		
		return "OK"; 
	}
	
	
}
