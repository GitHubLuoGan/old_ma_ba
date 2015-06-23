//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; 
import android.view.View;
import android.widget.Toast;
 
  
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
 
import com.kuyou.platform.core.api.entity.User;
import com.kuyou.platform.core.api.interfaces.DynamicParamsProvider;
import com.kuyou.platform.ui.api.IEventCode;
import com.kuyou.platform.ui.api.IEventDataField;
import com.kuyou.platform.ui.api.KYPlatform;
import com.kuyou.platform.ui.api.OnPlatformEventListener;
import com.kuyou.platform.ui.toolbar.ToolBar;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaikugou  extends  MoaiBaseSdk {
     
public static int serverIndex=0;
public static String serverName="";
public static String roleName="";
public static String orderNo="";
public static String exp1="";
public static String exp2="";
     
       public static ToolBar toolBar=null;
       
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
         
KYPlatform.init(sActivity,mOnPlatformEventListener,mDynamicParamsProvider);

 
    }

    
     /**
     * 事件回调监听器
     */
    private OnPlatformEventListener mOnPlatformEventListener = new OnPlatformEventListener() {

        @Override
        public void onEventOccur(int eventCode, Bundle data) {
            

            switch (eventCode) {
                case IEventCode.ENTER_GAME_SUCCESS: 
                    //进入游戏成功
                    GameLoginSUCCESS(data);
                    break;
                case IEventCode.ENTER_GAME_FAILED: 
                    String loginError=data.getString(IEventDataField.EXTRA_ERROR_MESSAGE);
                    //进入游戏失败
                    JsonRpcCall(Lua_Cmd_LoginFailed,loginError);
                    break;
                case IEventCode.ACCOUNT_CHANGED_SUCCESS:
                     JsonRpcCall(Lua_Cmd_LoginOut,"");
                    //切换账号成功 
                    GameLoginSUCCESS(data);
                   // Log.d("callback", "ApplicationEx:ACCOUNT_CHANGED_SUCCESS");
                    break;
                case IEventCode.EXIT_LOGIN_PAGE:
                    JsonRpcCall(Lua_Cmd_LoginCancel,"");
                   // showToast("退出登录界面");
                    break;
                case IEventCode.REGISTER_SUCCESS: 
                     
                    //注册成功
                    // GameLoginSUCCESS(data);
                   
                    break;
                case IEventCode.EXIT_REGISTER_PAGE:
                    JsonRpcCall(Lua_Cmd_LoginCancel,"");
                   // showToast("退出注册页面");
                    break;
                case IEventCode.RECHARGE_SUCCESS:
                    sBaseSDK.JsonRpcCall(Lua_Cmd_PaySuccess,"");
                    //showToast("充值成功");
                    break;
                case IEventCode.COMPLETE_SCORE_TASK_SUCCESS:                    
                    showToast("成功完成积分任务");
                    break;
            }
        }
    };

    //成功登陆准备进我们自己的游戏
   void GameLoginSUCCESS(Bundle data){
    

        //显示欢迎信息
        KYPlatform.showWelcomeDialog(sActivity); 
        
        // 浮动工具栏
        initToolBar();


        User user = (User)data.getSerializable(IEventDataField.EXTRA_USER);
        String userName=user.getUserName();      
        String token=user.getToken();
        String time=user.getUnixTime();
        
        JsonObject jsonData=new JsonObject();
        jsonData.add("userName", userName); 
        jsonData.add("token",token); 
        jsonData.add("time",time); 
        //jsonData.get(name)
        JsonObject jsonParms=SDKFormatGateWay(userName,jsonData);
        jsonParms.add("data", jsonData);
        JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);    

        
       
   }
    
    /**
     * 动态参数。根据游戏业务需要去返回不同的值
     */
    private DynamicParamsProvider mDynamicParamsProvider = new DynamicParamsProvider() {

     

        /**
         * 区服ID
         */
        public int getServerId() {
            return Moaikugou.serverIndex;
        }

        /**
         * 角色名称
         */
        public String getRoleName() {
            return Moaikugou.roleName;
        }

        /**
         * 充值订单号
         */
        public String createNewOrderId() {
            
            return Moaikugou.orderNo;
        }

        /**
         * 扩展参数1 1.用于厂商客户端与服务端通信 2.游戏客户端从这个接口传入的参数SDK会原样返回给游戏服务端
         * 3.如果不需要使用，直接返回null
         */
        public String getExtension1() {
            return  Moaikugou.exp1;
        }

        /**
         * 扩展参数2 1.用于厂商客户端与服务端通信 2.游戏客户端从这个接口传入的参数SDK会原样返回给游戏服务端
         * 3.如果不需要使用，直接返回null
         */
        public String getExtension2() {
            return  Moaikugou.exp2;
        }
    };

    private void showToast(String hints) {
        Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
    }
    
  

    private void initToolBar() {
        /**
         * ToolBar不是单例模式，所以要放在一个非多次调用的方法里进行初始化，例如onCreate(),不要在登录成功或者切换账号成功的时候调用
         */
        if(toolBar==null)
        {
            toolBar = new ToolBar(sActivity, ToolBar.LEFT_MID);
            toolBar.show();
            // 设置浮动工具栏收拢
            toolBar.setCustomViewVisibility(View.GONE);
        }
    }
    
    
//退出游戏时
    public void onMDestroy(){
        
         // 退出游戏时调用
        KYPlatform.exit(true);
    }
//是否退出时执行
    public boolean onMPreExit(Activity activity, final Moai.OnFinishHandler onfinish){

       // onfinish.callback(false);  //继续游戏的代码  
        onfinish.callback(true);//退出游戏的代码 
        return true;
    }

    ///打开登陆界面
    public  String OpenLogin(JsonValue parms){ 
        
        KYPlatform.enterGame(sActivity);
         
         return "OK";
    };

    ///打开支付界面
    public String OpenPay(JsonValue parms)
    { 
        try { 
            JsonObject _json = parms.asObject();     
            JsonObject  payinfoJson=_json.get("payinfo").asObject();
            String OrderNo=payinfoJson.get("orderno").asString();
            int price=payinfoJson.get("price").asInt();
            int total=payinfoJson.get("total").asInt(); 
             
            int _total=total;
        
            if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
            Moaikugou.orderNo=OrderNo;
            Moaikugou.exp1=OrderNo;
            Moaikugou.exp2=OrderNo;
            KYPlatform.enterRechargeCenter (sActivity,_total);
            
            //String info=payinfoJson.get("info").asString();
             
        } catch (Exception e) { 
            MoaiLog.i(" OpenPay is Error:"+e.getMessage());
        }       
        
         return "OK";   
    }

    ///SDK会员中心 
    public String OpenMemberCenter(JsonValue parms){
         
        KYPlatform.enterUserCenter(sActivity);
         return "OK";   
    }
     
    ///退出游戏
    public String ExitGame(JsonValue parms){
        KYPlatform.exit(false);
         return "OK";    
    }
 
    
    //角色信息
    public String SetCharInfo(JsonValue parms){
        
        KYPlatform.sendEnterGameStatics();
 
        
        try {
        JsonObject jsonData=parms.asObject();
        serverIndex=MoaiBaseSdk.GetJsonValInt(jsonData, "serverIndex", 0); 
        serverName=MoaiBaseSdk.GetJsonVal(jsonData, "serverName", "0"); 
        roleName=MoaiBaseSdk.GetJsonVal(jsonData, "RoleName", "0");
         } catch (Exception e) {
               Log.e(" MoaiSDK", " MoaiSDK  SetCharInfo is error "+e.getMessage());
 
         }
        return "OK";
                
                
         
    } 
     
    
}

