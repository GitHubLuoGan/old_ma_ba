package com.ziplinegames.moai;
import java.util.HashMap;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;

public class MoaisharesdkNew extends  MoaiBaseSdk {
	
	public  void SDKInit ( String parms ) 
	{
		ShareSDK.initSDK(MoaiBaseSdk.sActivity);
	}
	
	
	public static void share(String title, String content, String picUrl)
	{
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setText(content);
		sp.setShareType(Platform.SHARE_IMAGE);
		sp.setImagePath(picUrl);
		
		Platform plat = ShareSDK.getPlatform("WechatMoments");
		plat.setPlatformActionListener(new PlatformActionListener()
		{

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub
				
			}
			
		});
		plat.share(sp);
	}
	
	public static String V2_openShare(JsonValue parms){
		
		JsonObject jsonStr = parms.asObject();
		String title  = jsonStr.get("name").asString();
		String content  = jsonStr.get("description").asString();
		String picpath  = jsonStr.get("picture").asString();
		
		
		share(title, content, picpath);
		
		return "";
	}
	
	public void onMDestroy()
	{
		ShareSDK.stopSDK();
	}
	
	
}
