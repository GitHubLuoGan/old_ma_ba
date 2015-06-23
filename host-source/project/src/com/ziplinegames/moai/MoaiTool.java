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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class MoaiTool  {
		public static ProgressDialog mProgressDialog;
		
		/** 
	     * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家 以及某些类型的用户访问信息。 
	     * 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类 
	     * 使用Context.getSystemService(Context.TELEPHONY_SERVICE)来获取这个类的实例。 
	     */
		private static TelephonyManager telephonyManager=null; 
		
		 /** 
	     * 国际移动用户识别码 
	     */  
	    private static String IMSI;
		
	    /**
	     * 获取卡的信息
	     */
	    public static void SIMCardInfo(Context context) {  
	        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
	    }  
	  
	    /* *//** 
	     * Role:获取当前设置的电话号码 	    
	     *//*  
	    public static String getNativePhoneNumber() {  
	        String NativePhoneNumber=null;  
	        NativePhoneNumber=telephonyManager.getLine1Number();  
	        return NativePhoneNumber;  
	    }  */ 
	  
	    //当前手机运营商
	    public static int cardType=0;
	    
	    /**
	     * 未知运营商
	     */
	    public static int CardType_NO=0;
	    /**
	     * 移动
	     */
	    public static int CardType_YD=1;
	    /**
	     * 联通
	     */
	    public static int CardType_LT=2;
	    /**
	     * 电信
	     */
	    public static int CardType_DX=3;
	    /** 
	     * Role:Telecom service providers获取手机服务商信息
	     * 需要加入权限<uses-permission 
	     * android:name="android.permission.READ_PHONE_STATE"/>	  
	     */  
	    public static  String getProvidersName(Context context) {  
	        String ProvidersName = null;  
	        if(telephonyManager==null){
	        	SIMCardInfo(context);
	        }
	        // 返回唯一的用户ID;就是这张卡的编号神马的  
	        IMSI = telephonyManager.getSubscriberId();  
	        if(IMSI==null){
	        	cardType=-1;
	            ProvidersName = "没卡";
	            return ProvidersName;
	        }
	        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。  
	        //System.out.println(IMSI);  
	        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
	        	cardType=CardType_YD;
	            ProvidersName = "中国移动";  
	        } else if (IMSI.startsWith("46001")) { 
	        	cardType=CardType_LT;
	            ProvidersName = "中国联通";  
	        } else if (IMSI.startsWith("46003")) { 
	        	cardType=CardType_DX;
	            ProvidersName = "中国电信";  
	        }else{
	        	cardType=CardType_NO;
	            ProvidersName = "未知运营商";  
	        }
	        return ProvidersName;  
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

		return hex.toString();
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
	
	public static void showDialog(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MoaiBaseSdk.sActivity);
		builder.setTitle(title);
		//builder.setIcon(MoaiBaseSdk.sActivity.getResources().getDrawable(R.drawable.icon));
		builder.setMessage((msg == null) ? "Undefined error" : msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}


}