//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log; 
import android.widget.Toast;
 
 
import cn.cmgame.billing.api.BillingResult;
import cn.cmgame.billing.api.GameInterface;
import cn.cmgame.billing.api.GameInterface.GameExitCallback;
import cn.cmgame.billing.api.GameInterface.ILoginCallback;
import cn.cmgame.billing.api.LoginResult;
import cn.cmgame.billing.api.PropsType;
import cn.cmgame.gamepad.api.Gamepad;
import cn.cmgame.gamepad.api.KeyState;
import cn.cmgame.leaderboard.api.GameLeaderboard;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
 

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiheyouxi  extends  MoaiBaseSdk {
     
    public static boolean isMusicEnable=true;
//    public static String userName="";
//    public static String token=null;
    public static List<billingConfig> bListConfig=null;
//    public static GameInterface.ILoginCallback loginCallback = new GameInterface.ILoginCallback(){
//        @Override
//        public void onResult(int i, String s, Object o) {
//          MoaiLog.i(" setLoginListener onResult :"+i);
//          if(s!=null) MoaiLog.i(" setLoginListener onResult s:"+s);
//           
//          System.out.println("Login.Result=" + s);
//          if(i == LoginResult.SUCCESS_EXPLICIT || i == LoginResult.SUCCESS_IMPLICIT ){
//        	  MoaiLog.e("用户登录成功"); 
//            
//            userName=s;  
//            isMusicEnable=GameInterface.isMusicEnabled();
//            
//          }
//          if(i == LoginResult.FAILED_EXPLICIT){
//        	  MoaiLog.e("用户登录失败,请重新登陆");
//          
//            
//          }
//          if(i == LoginResult.UNKOWN){
//        	  MoaiLog.e("用户取消登录，或无网络状态,请重新登陆");
//           
//             
//          }
//        }
//    };
      
    
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
     
        setBillingConfig();
        GameInterface.initializeApp(sActivity);
       
     
        
    }
    
    //setverSion回调
    
    public void ResultChannelInfo(){
    	isMusicEnable=GameInterface.isMusicEnabled();
    	JsonObject myJson= new JsonObject();
    	
    	if(isMusicEnable){myJson.add("isMusicEnabled", true);}
    	else{myJson.add("isMusicEnabled", false);}
    		
    	JsonRpcCall(Lua_Cmd_ResultChannelInfo,myJson);
    	MoaiLog.d("ResultChannelInfo:::::::::::" + myJson.toString() + isMusicEnable);
    	
    }
    
//是否存在
     public String Exist(JsonValue parms){
            if(parms.toString().equalsIgnoreCase(Java_Cmd_OpenAbout)) return "true";
            return "false"; 
     }
    
    public static boolean isMusicEnabled() {
        return GameInterface.isMusicEnabled();
    }

    
    public static void setBillingConfig(){
        if(bListConfig!=null||sConfigJsonObject==null) return;
        
        //读取计费点
        JsonValue jsonVal=sConfigJsonObject.get("billing");
        if(jsonVal==null) return;
        JsonArray billingMap=jsonVal.asArray();
        if(billingMap!=null){
            bListConfig=new ArrayList<billingConfig>();         
            JsonObject mapJson=null;
            for(int i=0;i<billingMap.size();i++){
                billingConfig bConfig=new billingConfig();
                mapJson=billingMap.get(i).asObject(); 
                bConfig.billingIndex=GetJsonVal(mapJson,"billingIndex","000");
                bConfig.isRepeated=GetJsonVal(mapJson,"isRepeated","false").equalsIgnoreCase("true");
                bConfig.money=GetJsonValInt(mapJson,"money",0);
                bConfig.number=GetJsonValInt(mapJson,"number",0);
                bConfig.useSms=GetJsonVal(mapJson,"useSms","true").equalsIgnoreCase("true");
                bListConfig.add(bConfig);
            }
            
            
        }
    }
    
    ///
    public static billingConfig getBillingConfig(billingConfig bconfig){
        if(bconfig==null) return bconfig;
        if(bListConfig==null||sConfigJsonObject==null) return null;     
        if(bListConfig.size()<=0) return null;
        billingConfig _bconfig;    
        for(int i=0;i<bListConfig.size();i++){
            _bconfig=bListConfig.get(i);
            if(bconfig.number>0){
                if(_bconfig.number==bconfig.number) return _bconfig;
            }
            else
            {
                if(_bconfig.money==bconfig.money) return _bconfig;
            }
        }
        return null;
    }
    private static void showToast(String hints) {
        Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
    }
    
    
//退出游戏时
    public void onMDestroy(){
        
    
        
        GameInterface.exit(sActivity, new GameExitCallback() { 
            @Override
            public void onCancelExit() {
                 
            }

            @Override
            public void onConfirmExit() {
                sActivity = null;
            }

        });
        
    }
//是否退出时执行
    public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

        GameInterface.exit(activity, new GameInterface.GameExitCallback() {
            public void onCancelExit() {
                // nothing
            }

            public void onConfirmExit() {
                onfinish.callback(true);
            }
        });

        return true;
    }

    public static void viewMoreGames() {
        GameInterface.viewMoreGames(sActivity);
    }
    
    ///打开登陆界面
    public static  String V2_OpenLogin(JsonValue parms){ 
//        //登陆
//    	MoaiLog.d("V2_OpenLogin:::::::::::1111");
//        if(userName==null){
//            showToast("用户登录失败,请重新登陆");
//            JsonRpcCall("/c/isUltraliskLogin","");
//               return "OK";
//        }
//        JsonObject jsonData=new JsonObject();
//        jsonData.add("userName", userName); 
//        jsonData.add("token",token);  
//        JsonObject jsonParms=SDKFormatGateWay(userName,jsonData);
//        jsonParms.add("data", jsonData);
//        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);  
//        
//        MoaiLog.d("V2_OpenLogin:::::::::::" + jsonParms.toString());
         return "OK";
    };

    
    
    ///打开支付界面
    public String OpenPay(final JsonValue parms)
    { 
        try { 
        	
        	Log.e("nimabi","parms:::" + parms.toString());
        	
             JsonObject _json = parms.asObject();     
             JsonObject  payinfoJson=_json.get("payInfo").asObject();
             String OrderNo=payinfoJson.get("orderno").asString();
             int price=payinfoJson.get("price").asInt();
 
             
             
             
  
             int number=GetJsonValInt(payinfoJson,"number",0);             
             OrderNo=OrderNo.replace('_', 'b')+"sssssss";
 
             OrderNo=OrderNo.substring(0,16);            
             int _total=price;         

             Log.e("nimabi","OrderNo   " + OrderNo);
             Log.e("nimabi","price   " + String.valueOf(price));
             Log.e("nimabi","number   " + String.valueOf(number));
             
             billingConfig bConfig=new billingConfig();
             bConfig.number=number;
             bConfig.money=price;
             String gameId = sConfigJsonObject.get("gameName").asString();
             if(gameId.equals("balala")){ 
            	 bConfig.money=price;
             bConfig.number=0;}
             
             Log.e("nimabi","bConfig1   " + bConfig.toString());
             bConfig=getBillingConfig(bConfig);
             
             Log.e("nimabi","bConfig   " + bConfig.toString());

             GameInterface.doBilling(sActivity, bConfig.useSms, bConfig.isRepeated, bConfig.billingIndex, OrderNo,
                    new GameInterface.IPayCallback() {

                        @Override
                        public void onResult(int resultCode, String index,
                                Object arg2) {
                        	JsonObject jsonParms=new JsonObject();
                            switch (resultCode) {
                            case BillingResult.SUCCESS:
                            	
                            	Log.e("blala","pay success!");
    							jsonParms.add("code", 1);
    							jsonParms.add("msg", "支付成功");
    							jsonParms.add("payData",parms.asObject());
                       
    							JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
 
                                break;
                            case BillingResult.FAILED:
                            	
                            	Log.e("blala","pay error!");
                            	jsonParms.add("code", 0);
    							jsonParms.add("msg", "支付失败");
    							jsonParms.add("payData",parms.asObject());
                       
    							JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
    							
                                break;
                            default:
                            	
                            	Log.e("blala","unknow error!");
                            	jsonParms.add("code", -1);
    							jsonParms.add("msg", "未知错误");
    							jsonParms.add("payData",parms.asObject());
                       
    							JsonRpcCall(Lua_Cmd_PayResult,jsonParms);
    							
                                break;
                            }
                        }
                    });
                 
               
        } catch (Exception e) { 
            MoaiLog.i(" OpenPay is Error:"+e.getMessage());
        }       
        
         return "OK";   
    }

//SDK2.0模式
    public static String V2_OpenPay(JsonValue parms){
        return MoaiBaseSdk.sBaseSDK.OpenPay(parms);
    }

    ///SDK会员中心 
    public String OpenMemberCenter(JsonValue parms){
          
         return "OK";   
    }
     
    ///退出游戏
    public String ExitGame(JsonValue parms){
          return "OK";   
    }
 
    
    //角色信息
    public String SetCharInfo(JsonValue parms){
         
        return "";
    }
    
 
}

