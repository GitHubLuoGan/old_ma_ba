package com.ziplinegames.moai;

import java.io.IOException;
import java.util.Calendar;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class Moaicdkey  extends  MoaiBaseSdk {

private static   String GetCDKEYUrl="http://guobaomgr.ultralisk.cn/api/getcdkey.api";
private static   String UseCDKEYUrl="http://guobaomgr.ultralisk.cn/api/usecdkey.api";
/*private static   String GetCDKEYUrl="http://192.168.1.89:9001/api/getcdkey.api";
private static   String UseCDKEYUrl="http://192.168.1.89:9001/api/usecdkey.api";
*/

private final static int appid=1;
private final static String appkey="GuoBAao!-Love12.";
public void SDKInit(String s){
 
}
public static void getCdkey(){
	JsonObject jsonParms =new JsonObject();
	jsonParms.add("roleid", "2344");
	V2_getCdkey(jsonParms);
}



public static String V2_getCdkey(JsonValue parms){
	if(!MoaiTool.checkNetworkAvailable(MoaiBaseSdk.sActivity)){
		MoaiTool.showDialog("提示信息", "请先连接网络");
		return "false";
	}
	JsonObject jsonParms = parms.asObject();
	String roleid=GetJsonVal(jsonParms,"roleid","000"); 
	String time=String.valueOf(Calendar.getInstance().getTimeInMillis());
	String beStrMd5=String.format("%d%s%s%s", appid,time,roleid,appkey);
	String sign=MoaiTool.stringToMD5(beStrMd5);
	String url=String.format("%s?appid=%d&time=%s&roleid=%s&sign=%s",GetCDKEYUrl,appid,time,roleid,sign);
	 String resultStr=MoaiTool.sendGet(url);
	 int code=0;
	 String msg="";
	 String cdkey="";
	if(resultStr.isEmpty()||resultStr.equals("")){
		code=-1;
		msg="获取CDKEY失败!";
	}
	else{
		JsonObject resultJson =null;
		try{
			resultJson=new JsonObject().readFrom(resultStr);  
			code=GetJsonValInt(resultJson,"code",0);
			msg=GetJsonVal(resultJson,"msg","");
			cdkey=GetJsonVal(resultJson,"data","");
			
			/*JsonObject jsonObj=new JsonObject();
			jsonObj.add("roleid", roleid);
			jsonObj.add("cdkey", cdkey);
			V2_useCdkey(jsonObj);*/
		}
		catch(Exception e){
			code=-2;
			msg=e.toString();
		}
		 
	} 
	JsonObject resultJson=new JsonObject();
	resultJson.add("code", code);
	resultJson.add("msg", msg);
	resultJson.add("cdkey",cdkey);				 
	
	MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultGetCdkey,resultJson);
	return "OK";
}

public static String V2_useCdkey(JsonValue parms){
	if(!MoaiTool.checkNetworkAvailable(MoaiBaseSdk.sActivity)){
		MoaiTool.showDialog("提示信息", "请先连接网络");
		return "false";
	}
	JsonObject jsonParms = parms.asObject();
	String roleid=GetJsonVal(jsonParms,"roleid","000"); 
	String cdkey=GetJsonVal(jsonParms,"cdkey","000"); 
	String time=String.valueOf(Calendar.getInstance().getTimeInMillis());
	String beStrMd5=String.format("%d%s%s%s%s", appid,time,roleid,cdkey,appkey);
	String sign=MoaiTool.stringToMD5(beStrMd5);
	String url=String.format("%s?appid=%d&time=%s&roleid=%s&cdkey=%s&sign=%s",UseCDKEYUrl, appid,time,roleid,cdkey,sign);
	 String resultStr=MoaiTool.sendGet(url);
	 int code=0;
	 String msg="";
	 JsonValue goods=null;
	if(resultStr.isEmpty()||resultStr.equals("")){
		code=-1;
		msg="获取CDKEY失败!";
	}
	else{
		JsonObject resultJson =null;
		try{
			resultJson=new JsonObject().readFrom(resultStr);  
			code=GetJsonValInt(resultJson,"code",0);
			msg=GetJsonVal(resultJson,"msg","");
			try{
			goods=resultJson.get("data");
			}
			catch(Exception e){
			} 
		}
		catch(Exception e){
			code=-2;
			msg=e.toString();
		}
	
	} 
	JsonObject resultJson=new JsonObject();
	resultJson.add("code", code);
	resultJson.add("msg", msg);
	resultJson.add("goods",goods);				 
	//计费成功
	MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_ResultUseCdkey,resultJson);
	return "OK";
}

}