package com.ziplinegames.moai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import com.eclipsesource.json.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import cn.ultralisk.gameapp.GuoBaoTeGong.R;

public class LogoActivity extends Activity {
	
	public static String configInfo = "";
	public static String configInfo_1 = "{\"tollgate\":[0], \"gifttype\":4,\"itemstype\":1, \"prob\":[0],\"type\":[0],\"mode\":1,\"sdkid\":1,\"merger\":1}";
	
	public static String Ip ="";
	public static String ratio ="";
	public static String copGameId="";
	public static String copChannelId="";
	public static String copAddr="";
	public static String version="";
	public static String device="";
	public static String deviceCode="";
	public static boolean isPMA = false;
	public static String imsi = "";
	public static String iccid = "";
	public static String gifttype = "";
	public static String itemstype = "";
	public  JsonObject sConfigJsonObject=null;
	public static JsonObject goodsInfoJson=null;
	
	//获取cop请求串
	public  String getRequestUrl(){
		
		String reseultUrl = "";
		
		InputStreamReader inputReader;
		try {
			
			inputReader = new InputStreamReader(getResources().getAssets().open("cConfig.json"),"GBK");
			sConfigJsonObject=JsonObject.readFrom(inputReader);
			
			inputReader = new InputStreamReader(getResources().getAssets().open("goodsInfo.json"),"GBK");
			goodsInfoJson = JsonObject.readFrom(inputReader);
			
			copGameId =sConfigJsonObject.get("copGameId").asString();
			copChannelId =sConfigJsonObject.get("copChannelId").asString();
			Ip = getNetIp();
			copAddr = sConfigJsonObject.get("copAddr").asString();
			deviceCode = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
			
			TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			imsi = mTelephonyMgr.getSubscriberId();
			iccid = mTelephonyMgr.getSimSerialNumber();
			
		
			 try {
			        PackageManager manager = this.getPackageManager();
			        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			        version = info.versionName;
			        
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			    device =  android.os.Build.MODEL;
			    device = device.replace(' ', '_');
			    
			    Display mDisplay = getWindowManager().getDefaultDisplay();
			    String W = String.valueOf(mDisplay.getWidth());
			    String H = String.valueOf(mDisplay.getHeight());
			    
			    ratio = W + 'x' +H;

			   
			    reseultUrl = copAddr + "gift.php?" + "gameid=" +copGameId + "&qudao=" +copChannelId + "&uid=" +deviceCode + "&ver=" +version;
			    reseultUrl = reseultUrl + "&os=" +"android-"+android.os.Build.VERSION.RELEASE  + "&devices=" +device +"&ip=" +Ip +"&iccid=" +iccid + "&imsi=" + imsi +"&ratio=" +ratio ;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		}	

		return reseultUrl;
	}
//获取本机ip
	
	public static String getNetIp() {
		URL infoUrl = null;
		InputStream inStream = null;
		try {
			infoUrl = new URL("http://1111.ip138.com/ic.asp");
			URLConnection connection = infoUrl.openConnection();
		  
			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,"gb2312"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");
				inStream.close();
				System.out.println("net-result----->"+strber);
				//从反馈的结果中提取出IP地址
				int start = strber.indexOf("[");
				int end = strber.indexOf("]", start + 1);
				line = strber.substring(start + 1, end);
				return line;
			}
		}
		catch(MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		
		String requestUrl = getRequestUrl();
 		configInfo = sendGet(requestUrl);
 		configInfo = configInfo.replace(":,", ":1,");
 		
 		JsonObject dataJson;
 		try{
 			dataJson = JsonObject.readFrom(configInfo);
 		    
 		}catch(Exception e){
 			dataJson = JsonObject.readFrom(configInfo_1);
 		} 
		
		
		int sdkType    = dataJson.get("sdkid").asInt();
		itemstype = String.valueOf(dataJson.get("itemstype").asInt());
		gifttype = String.valueOf(dataJson.get("gifttype").asInt());
		
		
		MoaiLog.e("运营商信息::"+MoaiTool.getProvidersName(this));
		if(MoaiTool.cardType>MoaiTool.CardType_NO){
			if(sdkType == 2){
			MoaiBaseSdk.configFileName="cConfig.json";
			isPMA = true;
			}
			else{MoaiBaseSdk.configFileName=String.format("cConfig_%d.json", MoaiTool.cardType);}
			if(MoaiTool.cardType==MoaiTool.CardType_YD && !isPMA ){//移动
				//logoImage.setImageResource(R.drawable.logo_1);
				onLoding_YD(savedInstanceState);
			}
			else if(MoaiTool.cardType==MoaiTool.CardType_LT && !isPMA){//联通
				onLoding_LT(savedInstanceState);
				 
			}
			else if(MoaiTool.cardType==MoaiTool.CardType_DX && !isPMA){//电信
				onLoding_DX(savedInstanceState);
			}else
			{
				Intent intent = new Intent();
				intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
				startActivity(intent);
				finish();
				
			}
			 
		}else{
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	
void onLoding_YD(Bundle savedInstanceState){

		// 取消标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo);
		ImageView logoImage = (ImageView) this.findViewById(R.id.logo); 
		logoImage.setImageResource(R.drawable.logo_1);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(3000);
		logoImage.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				  try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
				Intent intent = new Intent();
				intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

//send cop server for info


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


	
void onLoding_LT(Bundle savedInstanceState){
	
	// 取消标题
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// 取消状态栏
	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	//setContentView(R.layout.logo);
	Intent intent = new Intent();
	intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
	startActivity(intent);
	finish();
	 
}

void onLoding_DX(Bundle savedInstanceState){

	// 取消标题
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// 取消状态栏
	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.logo);
	ImageView logoImage = (ImageView) this.findViewById(R.id.logo); 
	logoImage.setImageResource(R.drawable.logo_3);
	AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
	alphaAnimation.setDuration(3000);
	logoImage.startAnimation(alphaAnimation);
	alphaAnimation.setAnimationListener(new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			  try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
			startActivity(intent);
			finish();
		}
	});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.logo, menu);
		return true;
	}

}
