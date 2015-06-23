package com.ziplinegames.moai;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import android.app.Activity;

public class MoaiShareSDK{
	static Activity sActivity = null;
	
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
		ShareSDK.initSDK(sActivity);
	}
	
	public static void onPause()
	{	
		
	}
	
	public static void onResume()
	{
		
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
	
	static void onDestroy()
	{
		ShareSDK.stopSDK();
	}
	
	
}
