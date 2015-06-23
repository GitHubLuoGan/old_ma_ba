//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.content.Intent;

import com.ddworlds.Result.ABSListener;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.yzabs.RequestActivity;

//================================================================//
// MoaiCrittercism
//================================================================//
public class MoaiYuzhuo {
	static Activity sActivity = null;
	public static void onCreate ( Activity activity ) 
	{
		sActivity = activity;
	}
	public static void openPaymentDialog(String itemName, String itemDesc, float price, String orderID, String userID, String orderDesc, String userName, 
										final int successFunc, final int failFunc, final int cancelFunc)
	{
		Product pro= new Product(itemName, itemDesc, (float)price,orderID, userID, orderDesc, userName);
		AllObj.pro=pro;
		ABSListener l1= new ABSListener(){
					@Override
					public void callBack(int result, String msg) {
		//result 1是付款成功 2取消计费 0扣费失败
		//msg= paytype|orderNo Paytype=0是取消支付 1 短信 2. 支付宝 3.财付通 4.银行卡 5充值卡 6游戏点卡
						if(result==1){
							MoaiLuaBridge.callLuaFunctionWithString(successFunc, msg);
						}else if(result==2){
							MoaiLuaBridge.callLuaFunctionWithString(cancelFunc, msg);
						}else if(result==0){
							MoaiLuaBridge.callLuaFunctionWithString(failFunc, msg);
						}
					}

				};
		AllObj.ABSListener1=l1;
		Intent intent =new Intent(sActivity, RequestActivity.class);
		sActivity.startActivity(intent);
	}
}

