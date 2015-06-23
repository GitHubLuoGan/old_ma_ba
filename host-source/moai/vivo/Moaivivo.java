//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; 
import android.widget.Toast;
 
 
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.vivo.account.base.activity.LoginActivity;
import com.bbkmobile.iqoo.payment.PaymentActivity;


 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaivivo  extends  MoaiBaseSdk {
	
	public final static String KEY_LOGIN_RESULT = "LoginResult";
	public final static String KEY_SWITCH_ACCOUNT = "switchAccount";
	public final static String KEY_NAME = "name";
	public final static String KEY_OPENID = "openid";
	public final static String KEY_AUTHTOKEN = "authtoken";
	public final static String KEY_SHOW_TEMPLOGIN = "showTempLogin";
	private static final int   REQUEST_CODE_LOGIN = 0;
	private static final int   REQUEST_CODE_PAY = 1;
	
	public static String Uid = "";
	

	 
	 
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

	///打开登陆界面
	public  static String V2_OpenLogin(JsonValue parms){ 
		//登陆
		Intent loginIntent = new Intent(sActivity, LoginActivity.class);
		sActivity.startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
		
		
		 return "OK";
	};
	
	//退出游戏
	public static String V2_exitGame(JsonValue parms){
		
		JsonRpcCall(Lua_Cmd_GameExit,"");
		
		return "";
	}
	
	
	//回调信息
	public void onMActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		
		if(requestCode == REQUEST_CODE_LOGIN){
			if(resultCode == Activity.RESULT_OK){
				String loginResult = data.getStringExtra(KEY_LOGIN_RESULT);
				JSONObject loginResultObj;
				try {
					loginResultObj = new JSONObject(loginResult);
					String name = loginResultObj.getString(KEY_NAME);
					String openid = loginResultObj.getString(KEY_OPENID);
					String authtoken = loginResultObj.getString(KEY_AUTHTOKEN);
					
					
					JsonObject jsonData=new JsonObject();
			         jsonData.add("uid", openid);
			         jsonData.add("name",name);
			         jsonData.add("authtoken",authtoken);
			         
			         Uid = openid;
			         
			         JsonObject jsonParms=SDKFormatGateWay(openid,jsonData);
			         jsonParms.add("data", jsonData);
			         
			         JsonRpcCall(Lua_Cmd_LoginSuccess,jsonParms);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		else if (requestCode == REQUEST_CODE_PAY) {
		Bundle extras = data.getBundleExtra("pay_info");
		String trans_no = extras.getString("transNo");
		boolean pay_result = extras.getBoolean("pay_result");
		String res_code = extras.getString("result_code");
		String pay_msg = extras.getString("pay_msg");
		}
	}
	
	public void ResultChannelInfo(){
		
		JsonObject jsonData=new JsonObject();

		JsonRpcCall(Lua_Cmd_ResultChannelInfo,jsonData);

    }
	
	
	
	/* 
	 * 
	 * 
	 * get请求
	 *
	 *
	 */
	public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
       
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
       
            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
     
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }

        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	
	public static String V2_authQuit(JsonValue parms){
		
		String KEY_SWITCH_ACCOUNT = "switchAccount";
		Intent swithIntent = new Intent(sActivity, LoginActivity.class);
		swithIntent.putExtra(KEY_SWITCH_ACCOUNT, true);
		sActivity.startActivityForResult(swithIntent, REQUEST_CODE_LOGIN);
		
		return "";
	}

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			JsonObject _json = parms.asObject();	 
			JsonObject	payinfoJson=_json.get("payInfo").asObject();
			JsonObject	userInfo=_json.get("userInfo").asObject();
			
			String serverId = userInfo.get("serverIndex").asString();
			String OrderNo  =  payinfoJson.get("orderno").asString();
			String price    = payinfoJson.get("price").asString() + ".00";
			double priceDouble = Double.parseDouble(price);
			//price = roundDouble(price,3);
			DecimalFormat df = new DecimalFormat("##.00");
			
			String test = df.format(priceDouble);
			
			priceDouble = Double.parseDouble(df.format(priceDouble));
			
			String proName  = payinfoJson.get("name").asString();
			
			proName = URLEncoder.encode(proName,"utf-8"); 
			
			SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyyMMddhhmmss"); 
			String   date   =   sDateFormat.format(new   java.util.Date()); 
			
			String body = "storeOrder=" + OrderNo + "&notifyUrl=" + sConfigJsonObject.get("callBackUrl").asString()
			   + "&orderTime=" + date + "&orderAmount=" + price + "&orderTitle=" + proName + "&orderDesc=" + proName;
			
			 
			
			String vivoSign = sendGet(sConfigJsonObject.get("getSignUrl").asString(),body);
			JsonObject vivoSignJson = JsonObject.readFrom(vivoSign); 
			
			String vivoSignature = vivoSignJson.get("vivoSignature").asString();
			String vivoOrder = vivoSignJson.get("vivoOrder").asString();
			
			
			proName = URLDecoder.decode(proName,"utf-8");
			
			String packageName = sActivity.getPackageName();//获取应用的包名
			Bundle localBundle = new Bundle();
			localBundle.putString("transNo", vivoOrder);// 交易流水号，由订单推送接口返回
			localBundle.putString("signature", vivoSignature);// 签名信息，由订单推送接口返回
			localBundle.putString("package", packageName); //在开发者平台创建应用时填写的包名，务必一致，否则SDK界面不会被唤起
			localBundle.putString("useMode", "00");//固定值
			localBundle.putString("productName", proName);//商品名称
			localBundle.putString("productDes", proName);//商品描述
			localBundle.putDouble("price", priceDouble);//价格
			localBundle.putString("userId", Uid);//vivo账户id，不允许为空
			Intent target = new Intent(sActivity, PaymentActivity.class);
			target.putExtra("payment_params", localBundle);
			
			sActivity.startActivityForResult(target, REQUEST_CODE_PAY);

			

			//mAnzhiCenter.pay(super.sActivity,0,total,info,OrderNo);  
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";	
	}

	///SDK会员中心 
	public String OpenMemberCenter(JsonValue parms){
		//   mAnzhiCenter.getInstance().viewUserInfo(super.sActivity); 
	 
		 return "OK";	
	}
	 
	///退出游戏
	public String ExitGame(JsonValue parms){
		//mAnzhiCenter.getInstance().logout(super.sActivity);		 
		 return "OK";	 
	}
 
	
	//角色信息
	public String SetCharInfo(JsonValue parms){
		
		JsonObject jsonData=parms.asObject(); 
		return "";
	}
	
 
}

