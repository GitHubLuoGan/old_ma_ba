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
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import android.os.Bundle;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfoOnline;
import com.xiaomi.gamecenter.sdk.entry.PayMode;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;
import com.xiaomi.gamecenter.sdk.GameInfoField; 

 
//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiXiaomi  extends  MoaiBaseSdk {
     
    public static String Vip_level;
    public static String Party;
    public static String serverIndex;
    public static String serverName;
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
         
        MiAppInfo appInfo = new MiAppInfo();
        appInfo.setAppId(sConfigJsonObject.get("appId").asString());
        appInfo.setAppKey(sConfigJsonObject.get("appKey").asString());
        
//      appInfo.setOrientation( sConfigJsonObject.get("isLandscape").asString().equalsIgnoreCase("true")? ScreenOrientation.horizontal:ScreenOrientation.vertical );//横竖屏
//        appInfo.setAppType(sConfigJsonObject.get("appType").asInt() == 1? MiGameType.online:MiGameType.offline ); 
        MiCommplatform.Init( sActivity, appInfo);
        
     
        
    }

//退出游戏时
    public void onMDestroy(){
        
        
    }

    public void ResultChannelInfo(){
        
        JsonObject jsonData=new JsonObject();

        JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }

//是否退出时执行
    public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

       // onfinish.callback(false);  //继续游戏的代码  
        onfinish.callback(true);//退出游戏的代码 
        return true;
    }

    ///打开登陆界面
    public  static String V2_OpenLogin(JsonValue parms){ 
        //登陆
        MiCommplatform.getInstance().miLogin( sActivity, sLoginProcessListener );
         return "OK";
    };

    
    private static OnLoginProcessListener sLoginProcessListener = new OnLoginProcessListener()
    {
        public void finishLoginProcess( int errCode, MiAccountInfo accountInfo )
        {
        
            if ( MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS == errCode )
            {
                
                String uid          = "" + accountInfo.getUid();
                String sessionId    = accountInfo.getSessionId();
                String nickName     = accountInfo.getNikename();
                
                JsonObject jsonData=new JsonObject();
                jsonData.add("uid", uid);
                jsonData.add("sessionId",sessionId);
                jsonData.add("nickName",nickName);  
                JsonObject jsonParms=SDKFormatGateWay(uid,jsonData);
                jsonParms.add("data", jsonData);
                 
                JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);    
                
  
            }
            else if (MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL == errCode )
            {
                 
                    JsonRpcCall(Lua_Cmd_LoginCancel,"");
                
            }
            else if ( MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGINOUT_SUCCESS == errCode )
            {
                 
                    JsonRpcCall(Lua_Cmd_LoginOut,"");
                
            }
            else if ( MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGINOUT_FAIL == errCode )
            {
                
                    JsonRpcCall(Lua_Cmd_LoginFailed,"error,code:"+errCode); 
               
            }
            else if ( MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED == errCode )
            {
                Toast.makeText( sActivity, "登录正在进行中……", Toast.LENGTH_SHORT ).show();
            }
            else
            {
                 
                    JsonRpcCall(Lua_Cmd_LoginFailed,"登陆失败");                 
               
            }
        }
    };
    
    public static String V2_exitGame(JsonValue parms){
        JsonRpcCall(Lua_Cmd_GameExit,"");
        return "";
    }
    
    ///打开支付界面
    public static String V2_OpenPay(JsonValue parms)
    { 
         
        
        JsonObject _json = parms.asObject();     
        JsonObject  payinfoJson=_json.get("payInfo").asObject();
        String OrderNo=payinfoJson.get("orderno").asString();
        int price=Integer.valueOf(payinfoJson.get("price").asString());
       // int total=payinfoJson.get("total").asInt();
       // String info=payinfoJson.get("name").asString();
        
        JsonObject userinfoJson=_json.get("userInfo").asObject();
        
       // if(MoaiBaseSdk.sdkversion > 1 )price *= 0.01; /*************不同版本价格传入单位不同************/
        
        MiBuyInfoOnline online = new MiBuyInfoOnline();
        online.setCpOrderId( OrderNo );
        online.setCpUserInfo( OrderNo );
        online.setMiBi( price );

        
        Bundle mBundle = new Bundle(); 
        
     //   Bundle mBundle = new Bundle();
        mBundle.putString( GameInfoField.GAME_USER_BALANCE, "0");  //用户余额
        mBundle.putString( GameInfoField.GAME_USER_GAMER_VIP, Vip_level);  //vip 等级  登陆
        mBundle.putString( GameInfoField.GAME_USER_LV, userinfoJson.get("roleLv").toString());          //角色等级  
        mBundle.putString( GameInfoField.GAME_USER_PARTY_NAME, Party);  //工会，帮派
        mBundle.putString( GameInfoField.GAME_USER_ROLE_NAME, userinfoJson.get("roleName").asString()); //角色名称
        mBundle.putString( GameInfoField.GAME_USER_ROLEID, userinfoJson.get("roleId").toString());   //角色id
        mBundle.putString( GameInfoField.GAME_USER_SERVER_NAME, userinfoJson.get("serverName").asString());  //所在服务器
        
        
        try
        {
            MiCommplatform.getInstance().miUniPayOnline( sActivity, online, mBundle,sPayProcessListener );
        }
        catch ( Exception e )
        {
             
                JsonRpcCall(Lua_Cmd_PayError,"");  
            
        }
        
        
         return "OK";   
    }

    
    private static OnPayProcessListener   sPayProcessListener = new OnPayProcessListener()
    {

        @Override
        public void finishPayProcess(int errCode) {
            
            if ( errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS )// 成功
            {
                 
                    JsonRpcCall(Lua_Cmd_PaySuccess,"");
                 
                 
            }
            else if ( errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL 
                    || errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_CANCEL )// 取消
            {
                 
                    JsonRpcCall(Lua_Cmd_PayCancel,"");
                     
               
            }
            else if ( errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_FAILURE )// 失败
            { 
                    JsonRpcCall(Lua_Cmd_PayError,"");
                     
                
            }
            else if ( errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED )
            {
                Toast.makeText( sActivity, "请不要重复操作", Toast.LENGTH_SHORT ).show();
            }
            else if( errCode == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL )
            {
                
                    JsonRpcCall(Lua_Cmd_PayError,"支付失败");
                
            }
            else
            {
               
                    JsonRpcCall(Lua_Cmd_PayError,"支付失败");
                } 
            
        }
        
    };
    
    
    ///SDK会员中心 
    public String OpenMemberCenter(JsonValue parms){
          
         return "OK";   
    }
     
    ///退出游戏
    public String ExitGame(JsonValue parms){
    
         return "OK";    
    }
 
    

    //角色设置角色信息
    public String SetCharInfo(JsonValue parms){
        try {
        JsonObject jsonData=parms.asObject();
        Vip_level = jsonData.get("vipLv").toString();
        Party     = jsonData.get("roleClass").toString();
        serverIndex=jsonData.get("serverIndex").toString();
        serverName=jsonData.get("serverName").asString();
         } catch (Exception e) {
               Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
 
         }
        return "OK";
    }
    
 
}

