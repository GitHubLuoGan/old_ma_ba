package com.ziplinegames.moai;
import java.util.HashMap;
 

import com.eclipsesource.json.JsonObject;  

import mm.sms.purchasesdk.OnSMSPurchaseListener;
import mm.sms.purchasesdk.PurchaseCode;
import mm.sms.purchasesdk.SMSPurchase;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class IAPListener implements OnSMSPurchaseListener {
	private final String TAG = "IAPListener";
	//private Demo context;
	private IAPHandler iapHandler;

	
	public static String result = "订购结果：订购成功"; 
	// 商品信息
	public static String paycode = null;
	// 商品的交易 ID，用户可以根据这个交易ID，查询商品是否已经交易
	public static 	String tradeID = null;
	
	public static String orderID=null;
	
//	String OrderType = null;
	public static  int resultCode=0;
	
	
	public IAPListener(Context context, IAPHandler iapHandler) {
		//this.context = (Demo) context;
		this.iapHandler = iapHandler;
	}

	@Override
	public void onInitFinish(int code) {
		Log.d(TAG, "Init finish, status code = " + code);
		Message message = iapHandler.obtainMessage(IAPHandler.INIT_FINISH);
		String result = "初始化结果：" + SMSPurchase.getReason(code);
		message.obj = result;
		message.sendToTarget();
		//Moaimmsms.mmOnfinish.callback(true);
	}

	@Override
	public void onBillingFinish(int code, HashMap arg1) {
		Log.d(TAG, "billing finish, status code = " + code);
		 result = "订购结果：订购成功";
		Message message = iapHandler.obtainMessage(IAPHandler.BILL_FINISH);
		// 商品信息
		  paycode = null;
		// 商品的交易 ID，用户可以根据这个交易ID，查询商品是否已经交易
		  tradeID = null;
		  orderID=null;
//		String OrderType = null;
		  resultCode=0;
	
		//if (code == PurchaseCode.ORDER_OK){
		  if (code == PurchaseCode.ORDER_OK ) {
				

							JsonObject jsonParms=new JsonObject();
							jsonParms.add("code", 1);
							jsonParms.add("msg", "支付成功");
							jsonParms.add("payData",Moaimmsms.orderParms.asObject());
							 
							//计费成功
							MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);

				
			
		} else {
			/**
			 * 表示订购失败。
			 */
			resultCode=0;
			//result = "订购结果：" + SMSPurchase.getReason(code);
			result="订购失败:"+ SMSPurchase.getReason(code);
			JsonObject jsonParms=new JsonObject();
			jsonParms.add("code", resultCode);
			jsonParms.add("msg", "支付失败");
			jsonParms.add("payData",Moaimmsms.orderParms.asObject());
			Toast.makeText(MoaiBaseSdk.sActivity, "订购失败！", Toast.LENGTH_SHORT).show();
			//计费成功
			MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
			//context.dismissProgressDialog();
			System.out.println(result);
		}
		

	}

	
//	@Override
//	public void onQueryFinish(int code, HashMap arg1) {
//		Log.d(TAG, "license finish, status code = " + code);
//		Message message = iapHandler.obtainMessage(IAPHandler.QUERY_FINISH);
//		String result = "查询成功,该商品已购买";
//		// 此次订购的orderID
//		String orderID = null;
//		// 商品的paycode
//		String paycode = null;
//		// 商品的有效期(仅租赁类型商品有效)
//		String leftday = null;
//		if (code != PurchaseCode.QUERY_OK) {
//			/**
//			 * 查询不到商品购买的相关信息
//			 */
//			result = "查询结果：" + Purchase.getReason(code);
//		} else {
//			/**
//			 * 查询到商品的相关信息。
//			 * 此时你可以获得商品的paycode，orderid，以及商品的有效期leftday（仅租赁类型商品可以返回）
//			 */
//			leftday = (String) arg1.get(OnPurchaseListener.LEFTDAY);
//			if (leftday != null && leftday.trim().length() != 0) {
//				result = result + ",剩余时间 ： " + leftday;
//			}
//			orderID = (String) arg1.get(OnPurchaseListener.ORDERID);
//			if (orderID != null && orderID.trim().length() != 0) {
//				result = result + ",OrderID ： " + orderID;
//			}
//			paycode = (String) arg1.get(OnPurchaseListener.PAYCODE);
//			if (paycode != null && paycode.trim().length() != 0) {
//				result = result + ",Paycode:" + paycode;
//			}
//		}
//		System.out.println(result);
//	 
//	}
//
//	
//
//	@Override
//	public void onUnsubscribeFinish(int code) {
//		// TODO Auto-generated method stub
//		String result = "退订结果：" + Purchase.getReason(code);
//		System.out.println(result);
//		 
//	}
	
 
	public Handler handler = new Handler()
	{ 
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				{
			String msgStr=msg.getData().getString("msg"); 
			Toast.makeText(MoaiBaseSdk.sActivity, msgStr, Toast.LENGTH_SHORT).show();
					
				}
				break; 
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	 
}
