//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import java.io.*;

import com.bbox.api.BillCallBack;
import com.bbox.api.BillInterface;
import com.xgdata.XGCurrency;
import com.xgdata.XGGame;
import com.xgdata.XGAccount;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiXianguo {

	protected static native void	AKUNotifyXianguoPaymentSuccess(  );
	protected static native void	AKUNotifyXianguoPaymentFail( );
	protected static native void	AKUNotifyXianguoPaymentTimeout( );


	private static Activity sActivity = null;
	private static BillInterface sBill = null;
	private static boolean sIsPaying = false;
	private static boolean sHasActived = false;
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MOAIXianguo onCreate: Initializing xianguo" );
		
		sActivity = activity;
		sBill = new BillInterface(activity, getChannelID(activity) , 0);
		XGGame.init(sActivity);
	}
	
	public static String getChannelID(Activity activity)
	{
		String fileName = "channel";
		String id = "";
		
		boolean hasSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); 
		String rcdRoot = null;
		String rcdName = null;
		if(hasSD)
		{
			String sdPath 		= Environment.getExternalStorageDirectory().getPath();
			String packageName  = "ultralisk";
			try {
				packageName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rcdRoot = sdPath + "/ultralisk/" +  packageName;
			rcdName = rcdRoot + "/" + fileName;
	
			 try {
				FileInputStream fis = new FileInputStream(rcdName);
				byte[] content = new byte[32];
				fis.read(content);
				fis.close();
				if (content != null)
				{
					id = new String(content);
					id = id.trim();
					return id;
				}
			} catch (FileNotFoundException e) {} 
			catch (IOException ioe){}
		}
		
		try {
			InputStream in = activity.getResources().getAssets().open(fileName);
			byte[] content = new byte[32];
			in.read(content);
			in.close();
			if (content != null)
			{
				id = new String(content);
				id = id.trim();
			}
			
			if(rcdRoot != null && rcdName != null)
			{
				File destDir = new File(rcdRoot);
				if (!destDir.exists()) {destDir.mkdirs();}
				
				FileOutputStream ofs = new FileOutputStream(rcdName);
				ofs.write(id.getBytes());
				ofs.close();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return id;
	}

	private static BillCallBack sPaymentCallback = new BillCallBack()
	{
		public void onBillingSuccess() {
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyXianguoPaymentSuccess();
				sIsPaying = false;
			}
		}
		public void onBillingFailed() {
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyXianguoPaymentFail();
				sIsPaying = false;
			}
		}
		public void onBillingError(String msg) {
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyXianguoPaymentFail();
				sIsPaying = false;
			}
		}
		public void onBillingTimeOut() {
			synchronized ( Moai.sAkuLock ) 
			{
				AKUNotifyXianguoPaymentTimeout();
				sIsPaying = false;
			}
		}
	};
	public static void openPaymentDialog (int fee,String orderID,String userID,String consumerId, String consumerName,String extraStr, String feeInfo) 
	{
		if (sBill == null)
		{
			return;
		}
		if(sIsPaying)
		{
			return;
		}
		sBill.billing(sActivity, fee, orderID, userID ,consumerId, consumerName, extraStr, feeInfo, sPaymentCallback);
		sIsPaying = true;
	}
	
	public static void onResume ()
	{
		XGGame.onResume (sActivity); 
	} 

	public static void onPause ()
	{
		XGGame.onPause(sActivity); 
	} 
	
	public static void active()
	{
		if (!sHasActived)
		{
			XGGame.Active();
			sHasActived = true;
		}
	}
	
	public static void setAccount(String account)
	{
		XGAccount.getAccount(account);
	}
	
	public static void setChargeRequest(String orderId, String iapId, double currencyAmount, double virtualCurrencyAmount, String paymentType)
	{
		XGCurrency.onChargeRequest(orderId, iapId, currencyAmount, virtualCurrencyAmount, paymentType); 
	}
	
	public static void setChargeSuccess(String orderId)
	{
		XGCurrency.onChargeSuccess(orderId); 
	}

	
}

