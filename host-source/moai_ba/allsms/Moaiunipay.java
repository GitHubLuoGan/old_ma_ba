//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log; 
import android.widget.Toast; 

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;
 
 
//================================================================//
// MoaiCrittercism
//================================================================//
public class Moaiunipay  extends  MoaiBaseSdk {
	 
 

	public static JsonValue orderParms;
	public static Moai.OnFinishHandler mmOnfinish;
		public static ProgressDialog mProgressDialog;
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
		unipayPayCodeConfig.setPayCodeConfig(); 		
		String appid=GetJsonVal(sConfigJsonObject,"appid","0");
		String appkey=GetJsonVal(sConfigJsonObject,"appkey","0");
	 
		Utils.getInstances().initSDK(MoaiBaseSdk.sActivity,1);
		//0表示联通平台打包 下载计费文件，1 ,下载计费文件 自己打包
	}
	
	 
	
	 
	
   private void showToast(String hints) {
       Toast.makeText(sActivity.getApplicationContext(), hints, Toast.LENGTH_SHORT).show();
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
	public  String OpenLogin(JsonValue parms){ 
		//登陆
	 
		 return "OK";
	};

	///打开支付界面
	public static String V2_OpenPay(JsonValue parms)
	{ 
		try { 
			orderParms=parms;
			JsonObject _json = parms.asObject();     
            JsonObject  payinfoJson=_json.get("payInfo").asObject();
            JsonObject  userinfoJson=_json.get("userInfo").asObject();
            String OrderNo=GetJsonVal(payinfoJson,"orderno","000");
            //payinfoJson.get("orderno").asString();
            int price=GetJsonValInt(payinfoJson,"price",0);
           // payinfoJson.get("price").asInt();
            int total=GetJsonValInt(payinfoJson,"total",0);
            //payinfoJson.get("total").asInt(); 
            String number=GetJsonVal(payinfoJson,"number","0");
            String roleId=GetJsonVal(userinfoJson,"roleId","000");
            if(roleId.length()>16) roleId=roleId.substring(0, 16);  
            int _total=total;         
            if(MoaiBaseSdk.sdkversion>=2) _total=total/100;
            
            unipayPayCodeConfig bConfig=new unipayPayCodeConfig();
            bConfig.number=number;
            bConfig.money=total;
            bConfig=unipayPayCodeConfig.getPayCodeConfig(bConfig);
             
            Utils.getInstances().pay(MoaiBaseSdk.sActivity,bConfig.payCode,
new UnipayPayResultListener(){

	@Override
	public void PayResult(String paycode, int flag, String desc) {
			int  resultCode=0;
			String  result = "订购结果：订购成功";
			if(flag == Utils.SUCCESS_SMS ){   
    		//如果是短信发送成功或者延时超过指定时间，SDK都返回成功，
    		//开发者可以在在desc中可以看到成功结果的描述
    		resultCode=1;
    		result="支付成功";
    				//Toast.makeText(MainActivity.this, "支付成功", 1000).show();
    	             //添加发放道具的逻辑
    			}
    	if(flag == Utils.SUCCESS_3RDPAY ){   
    		//SDK使用第三方支付返回成功
    		resultCode=1;
    		result="支付成功";
    				//Toast.makeText(MainActivity.this, "支付成功", 1000).show();
    	             //添加发放道具的逻辑,desc中放支付方式
    			}
    	else if (flag == Utils.FAILED) {
    		resultCode=-1;
    		result="支付失败";
    				//Toast.makeText(MainActivity.this, "支付失败", 1000).show();
    	            //desc中放支付失败的原因
    			}
    	else if (flag == Utils.CANCEL) {
    		resultCode=0;
    		result="支付取消";
    				//Toast.makeText(MainActivity.this, "支付取消", 1000).show();
    	             //desc中放在哪个界面取消
    			}

    	else if (flag == Utils.OTHERPAY) {    		
    		resultCode=-2;
    		result="未知的支付";
    				//这里写调用联通之外的其他第三方支付方式的处理逻辑即可
    			}
    	JsonObject jsonParms=new JsonObject();
		jsonParms.add("code",resultCode);
		jsonParms.add("msg", result);
		jsonParms.add("payData",Moaiunipay.orderParms.asObject());
    	//返回
		MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
    		}
            	
            	
            } );
			
            /*if(checkNetworkAvailable(MoaiBaseSdk.sActivity))  
	        { 
            
	        //当前有可用网络  
	        }  
	        else   
	        {  
        	Toast.makeText(MoaiBaseSdk.sActivity, "请先连接网络！", Toast.LENGTH_SHORT).show();
            //当前无可用网络  
	        }  */
          
			
		} catch (Exception e) { 
			MoaiLog.i(" OpenPay is Error:"+e.getMessage());
		}		
		
		 return "OK";
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


		/********************* 发送buy_m请求 *************************/
	/**
	 * @param url
	 *            传入的url,包括了查询参数
	 * @return 返回get后的数据
	 */
	public static String sendGet(String url) {
		String result = "";
		// String
		URL realURL = null;
		URLConnection conn = null;
		BufferedReader bufReader = null;
		String line = "";
		try {
			realURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("url 格式错误");
		}
		try {
			conn = realURL.openConnection();
			// 设置连接参数...conn.setRequestProperty("xx", "xx");
			conn.setConnectTimeout(10000); // 10s timeout
			// conn.setRequestProperty("accept", "*/*");
			// conn.setRequestProperty("", "");
			conn.connect(); // 连接就把参数送出去了 get方法
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接错误");
		}
		try {
			bufReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("读取数据错误");
		} finally {
			// 释放资源
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/********************* 发送buy_m请求 *************************/
 
	
	/**
	 * 将字符串转成MD5值
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString().substring(8, 24);
	}
	
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}

		return false;

	}
	    
	
	public static void showProgress(String tips)
	{
		mProgressDialog = new ProgressDialog(MoaiBaseSdk.sActivity);
		mProgressDialog.setMessage(tips);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	public static void hideProgress()
	{ 
		if(mProgressDialog != null)
		{
			mProgressDialog.cancel();
			mProgressDialog = null;
		} 
	}
	

 
}

