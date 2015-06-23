package com.ziplinegames.moai;



import android.util.Log;
import android.widget.Toast;
import cn.dm.android.DMOfferWall;
import cn.dm.android.data.listener.CheckPointListener;
import cn.dm.android.model.ErrorInfo;
import cn.dm.android.model.Point;


import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Moaidomob implements MoaiSDKBridgeInterface{
	 
	//public static OManager mDomobOfferWallManager;
  
	public static  String PublisherID = "";
	public static	boolean  isOnline;
    //入口
	public String callDataPost(String cmd,JsonValue parms){
	 
		return callDataPostF(cmd,parms); 
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
		Object result= Moai.executeMethod(Moaidomob.class, null, methodName, new Class < ? > [] {JsonValue.class}, new Object [] {parms});
		if(result!=null) return result.toString();
		else return ""; 
	}
	
	public  String SDKInit(String  parms){
		isOnline = true;
		PublisherID=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject, "PublisherID", "");
		
		DMOfferWall.getInstance(MoaiBaseSdk.sActivity);
		DMOfferWall.init(MoaiBaseSdk.sActivity, PublisherID ); 
		return "OK";		
	}
	
	//打开列表积分墙
	public static String loadOfferWall(JsonValue parms){
	  
		
		DMOfferWall.getInstance(MoaiBaseSdk.sActivity).showOfferWall(MoaiBaseSdk.sActivity);
		
		return "OK";
	}
	
	//打开视频积分墙
	public static String  presentVideoWall(JsonValue parms){
		 
		
		return "OK";
	}
	
	//检测积分
	public static String checkPoints(JsonValue parms){
		
		DMOfferWall.getInstance(MoaiBaseSdk.sActivity).checkPoints(new CheckPointListener() {
			@Override
			public void onError(ErrorInfo errorInfo) {
				JsonObject resultOjb = new JsonObject();
				resultOjb.add("code", -1);
				resultOjb.add("msg", errorInfo.toString());
				resultOjb.add("point",0);
				resultOjb.add("consumed",0);
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultPoint, resultOjb);
				
			}
			@Override
			public void onResponse(Point data) {
				JsonObject resultOjb = new JsonObject();
				resultOjb.add("code", 1);
				resultOjb.add("msg", "成功");
				resultOjb.add("point",data.point);
				resultOjb.add("consumed",data.consumed);
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultPoint, resultOjb);
			}
		});
		return "OK";
	}
	
	//消费积分
	public static String consumePoints(JsonValue parms){
		JsonObject jsonobj=parms.asObject();
		final int consumePoints=MoaiBaseSdk.GetJsonValInt(jsonobj, "point", 0);
		
		DMOfferWall.getInstance (MoaiBaseSdk.sActivity).consumePoints(consumePoints,new CheckPointListener(){ 
	        @Override  
	        public  void  onError(ErrorInfo errorInfo) { 
	        	
				JsonObject resultOjb = new JsonObject();
				resultOjb.add("code", -1);
				resultOjb.add("msg", errorInfo.toString());
				resultOjb.add("point",0);
				resultOjb.add("consumed",0);
				resultOjb.add("consumePoints",consumePoints);
				
				MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultConsume, resultOjb);
	        	}  
	    @Override  
	    public  void  onResponse(Point data) { 
	    	JsonObject resultOjb = new JsonObject();	
	    	switch  (data.status) { 
	    	
	    	case  consume_success : //成功 	  
			resultOjb.add("code", 1);
			resultOjb.add("msg","成功");
			resultOjb.add("point",data.point);
			resultOjb.add("consumed",data.consumed);
			resultOjb.add("consumePoints",consumePoints);		  
			break ;  
			
	    	case  lack_point: //积分余额不足 
			resultOjb.add("code", -1);
			resultOjb.add("msg","积分不足");
			resultOjb.add("point",data.point);
			resultOjb.add("consumed",data.consumed); 
			resultOjb.add("consumePoints",consumePoints);
			break ;  
			
	    	case  unknown_error : 
	    	resultOjb.add("code", -3);
	    	resultOjb.add("msg","未知错误");
	    	resultOjb.add("point",data.point);
	    	resultOjb.add("consumed",data.consumed); 
	    	resultOjb.add("consumePoints",consumePoints);	
	    	break ;  
	    	} 
	  MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultConsume,resultOjb); 
	}  
	}); 
		
		return "OK";
	}
	
	
	
}
