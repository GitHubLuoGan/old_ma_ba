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

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
 

 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaicmgcsms  extends  MoaiBaseSdk {
     
    
    public static String userName=null;
    public static String token=null;
   
    public static  JsonValue orderParms=null;
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
     
        billingConfig.setBillingConfig();
        GameInterface.initializeApp(sActivity); 
        
    }
    
 
    @Override
    public void ResultChannelInfo(){

        JsonObject channelInfo=new JsonObject();
        channelInfo.add("isMusicEnabled",GameInterface.isMusicEnabled());
        channelInfo.add("isMoreGameEnabled",true);
        channelInfo.add("isAboutEnabled", true); 
        channelInfo.add("isConfirmExitEnabled", true);     
        JsonRpcCall(Lua_Cmd_ResultChannelInfo,channelInfo);  
    }
    
//是否存在
     public   String Exist(JsonValue parms){
            if(parms.asString().equals(Java_Cmd_OpenAbout)) return "1";
            if(parms.asString().equals(Java_Cmd_OpenMoreGame)) return "1";
            
            return "0"; 
     }
    
     public static String V2_Exist(JsonValue parms){
     return new Moaicmgcsms().Exist(parms);
     }
     
    public static boolean isMusicEnabled() {
        return GameInterface.isMusicEnabled();
    }

    
    
   
    private void showToast(String hints) {
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
                onfinish.callback(false);
            }

            public void onConfirmExit() {
                onfinish.callback(true);
            }
        });

        return true;
    }

    public static void openMoreGame(JsonValue parms) {
        GameInterface.viewMoreGames(sActivity);
    }
    public static void V2_openMoreGame(JsonValue parms) {
        openMoreGame(parms);
    }
    
    
    ///打开登陆界面
    public  String OpenLogin(JsonValue parms){ 
        //登陆
        if(userName==null||token==null){
            showToast("用户登录失败,请重新登陆");
            JsonRpcCall(Lua_Cmd_LoginFailed,"用户登录失败");
               return "OK";
        }
        JsonObject jsonData=new JsonObject();
        jsonData.add("userName", userName); 
        jsonData.add("token",token);  
        JsonObject jsonParms=SDKFormatGateWay(userName,jsonData);
        jsonParms.add("data", jsonData);
        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);    
         return "OK";
    };

    
    
    ///打开支付界面
    public String OpenPay(JsonValue parms)
    { 
        try { 
            orderParms=parms;
             JsonObject _json = parms.asObject();     
             JsonObject  payinfoJson=_json.get("payInfo").asObject();
             String OrderNo=MoaiBaseSdk.GetJsonVal(payinfoJson, "orderno", "");
             int price=MoaiBaseSdk.GetJsonValInt(payinfoJson, "price", 0);//payinfoJson.get("price").asInt();
             int total=MoaiBaseSdk.GetJsonValInt(payinfoJson, "total", 0);//payinfoJson.get("total").asInt(); 
             int number=MoaiBaseSdk.GetJsonValInt(payinfoJson, "number", 0);//GetJsonValInt(payinfoJson,"number",0);             
             OrderNo=OrderNo.replace('_', 'b')+"sssssss";
            //cpparam="20131031101034ff";
             OrderNo=OrderNo.substring(0,16);            
             int _total=total;         
             if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
             
             billingConfig bConfig=new billingConfig();
             bConfig.number=number;
             bConfig.money=total;
             bConfig=billingConfig.getBillingConfig(bConfig);
            // sConfigJsonObject
             GameInterface.doBilling(sActivity, bConfig.useSms, bConfig.isRepeated, bConfig.billingIndex, OrderNo,
                    new GameInterface.IPayCallback() {

                        @Override
                        public void onResult(int resultCode, String index,
                                Object arg2) {
                            int rCode=0;
                            String result="";
                            // TODO Auto-generated method stub
                            switch (resultCode) {
                            case BillingResult.SUCCESS:
                                rCode=1;
                                result="支付成功";                                 
                                break;
                            case BillingResult.FAILED:
                                rCode=-1;
                                result="支付失败";
                                break;
                            default:
                                rCode=0;
                                result="取消支付"; 
                                break;
                            }
                            
                            JsonObject jsonParms=new JsonObject();
                            jsonParms.add("code", rCode);
                            jsonParms.add("msg", result);
                            jsonParms.add("payData",orderParms.asObject());
                            JsonRpcCall(Lua_Cmd_PayResult,jsonParms);   
                        }
                    });
                 
               
        } catch (Exception e) { 
            MoaiLog.i(" OpenPay is Error:"+e.getMessage());
        }       
        
         return "OK";   
    }

//SDK2.0模式
    public static String V2_OpenPay(JsonValue parms){
        return new Moaicmgcsms().OpenPay(parms);
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
     
    public static String V2_SetCharInfo(JsonValue parms){
        new Moaicmgcsms().SetCharInfo(parms);
        return "";
    }
 
}

