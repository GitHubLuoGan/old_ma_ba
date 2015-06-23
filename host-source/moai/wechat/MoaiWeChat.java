package com.ziplinegames.moai;

import java.io.File;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.widget.Toast;

public class MoaiWeChat {
	static Activity sActivity = null;
	static private IWXAPI s_api;
	static private final String APP_ID = "wxba3dd5070926322b";
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
		s_api = WXAPIFactory.createWXAPI(sActivity,APP_ID , false);
		s_api.registerApp(APP_ID); 
	}
	
	public static void onPause()
	{	
		
	}
	
	public static void onResume()
	{
		
	}
	
	public static boolean isTimelineSupported()
	{
		int wxSdkVersion = s_api.getWXAppSupportAPI();
		return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
	}
	public static void share(String text, String picUrl)
	{
		boolean isTimeline = isTimelineSupported();
		
		File file = new File(picUrl);
		if (!file.exists()) {
			String tip = "图片不存在";
			Toast.makeText(sActivity, tip + " path = " + picUrl, Toast.LENGTH_LONG).show();
			return;
		}
		
		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(picUrl);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.description = text;
		msg.title = text;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		s_api.sendReq(req);
	}
	
	static private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
