package com.ziplinegames.moai;
import java.util.HashMap;
 

import com.eclipsesource.json.JsonObject;  

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import mm.purchasesdk.core.PurchaseCode;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class IAPListener implements OnPurchaseListener {
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
	public void onAfterApply() {

	}

	@Override
	public void onAfterDownload() {

	}

	@Override
	public void onBeforeApply() {

	}

	@Override
	public void onBeforeDownload() {

	}
	@Override
	public void onInitFinish(int code) {
		Log.d(TAG, "Init finish, status code = " + code);
		Message message = iapHandler.obtainMessage(IAPHandler.INIT_FINISH);
		String result = "初始化结果：" + Purchase.getReason(code);
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

		  tradeID = null;
		  orderID=null;

		  resultCode=0;
	

		  if (code == PurchaseCode.ORDER_OK || (code == PurchaseCode.AUTH_OK) ||(code == PurchaseCode.WEAK_ORDER_OK)) {
				
			  Moaimmsms.getBillingUrl("1");
			resultCode=1; 
			if (arg1 != null) {
				paycode = (String) arg1.get(OnPurchaseListener.PAYCODE);
				if (paycode != null && paycode.trim().length() != 0) {
					result = result + ",Paycode:" + paycode;
				}
				
				tradeID = (String) arg1.get(OnPurchaseListener.TRADEID);
				if (tradeID != null && tradeID.trim().length() != 0) {
					result = result + ",tradeid:" + tradeID;
				}
				
				tradeID = (String) arg1.get(OnPurchaseListener.TRADEID);
				if (tradeID != null && tradeID.trim().length() != 0) {
					result = result + ",tradeid:" + tradeID;
				}
				if (tradeID == null ||tradeID.trim().length()== 0) {
					tradeID = (String) arg1.get(OnPurchaseListener.ORDERID);
					if (tradeID != null && tradeID.trim().length() != 0) {
						result = result + ",orderID:" + tradeID;
					}
				}
				
				
				Moaimmsms.showProgress("正在验证充值状态,请稍候！");
				
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							Moaimmsms.hideProgress();
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//毫秒   
						try {
							String mmsmsValiUrl=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject, "mmsmsValiUrl", "http://mmsms.ultralisk.cn/api/mmquery.php");
							String appid=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject,"gameAppid","300008384942");
							 
							mmsmsValiUrl=mmsmsValiUrl+"?appid="+appid+"&tradeid="+tradeID; 
							 
							String resultStr="";
							try{
								resultStr=Moaimmsms.sendGet(mmsmsValiUrl);
								Moaimmsms.hideProgress();
							}
							catch(Exception ex){
								Moaimmsms.hideProgress();
								resultStr="";
							}
							
							if(resultStr.equals("0")){
								resultCode=1;
								result="购买成功";

								 //该部分是传参并更新控件  
				                Message msg = new Message();  
				                msg.what = 0;  
				                Bundle bundle = new Bundle();  
				                bundle.putString("msg","购买成功！");  
				                msg.setData(bundle);  
				                //发送消息到Handler  
				                handler.sendMessage(msg);  
				                }
							else
							{ 
								Intent intent = new Intent(MoaiBaseSdk.sActivity,mmsmsVali.class);			    	  
						      	intent.putExtra("tradeID",tradeID);
						        MoaiBaseSdk.sActivity.startActivity(intent);
								/*resultCode=0;
								result="购买失败";*/
								return;
							}
							JsonObject jsonParms=new JsonObject();
							jsonParms.add("code", resultCode);
							jsonParms.add("msg", result);
							jsonParms.add("payData",Moaimmsms.orderParms.asObject());
							 
							//计费成功
							MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
							System.out.println(result);
							
				        } catch (Exception e) {  
				            e.printStackTrace();  
				        }  
				        
					}
				}).start();
				
				
//				OrderType = (String) arg1.get(OnSMSPurchaseListener.ORDERTYPE);
//				if (OrderType != null) {
//					result = result + ",ordertype:" + OrderType;
//				}
			}
		} else {
			/**
			 * 表示订购失败。
			 */
			resultCode=0;
			
			Moaimmsms.getBillingUrl("0");
			//result = "订购结果：" + SMSPurchase.getReason(code);
			result="订购失败:"+ Purchase.getReason(code);
			JsonObject jsonParms=new JsonObject();
			jsonParms.add("code", resultCode);
			jsonParms.add("msg", result);
			jsonParms.add("payData",Moaimmsms.orderParms.asObject());
			Toast.makeText(MoaiBaseSdk.sActivity, "订购失败！", Toast.LENGTH_SHORT).show();
			//计费成功
			MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
			//context.dismissProgressDialog();
			System.out.println(result);
		}
		

	}

	
	@Override
	public void onQueryFinish(int code, HashMap arg1) {
		Log.d(TAG, "license finish, status code = " + code);
		Message message = iapHandler.obtainMessage(IAPHandler.QUERY_FINISH);
		String result = "查询成功,该商品已购买";
		// 此次订购的orderID
		String orderID = null;
		// 商品的paycode
		String paycode = null;
		// 商品的有效期(仅租赁类型商品有效)
		String leftday = null;
		if (code != PurchaseCode.QUERY_OK) {
			/**
			 * 查询不到商品购买的相关信息
			 */
			result = "查询结果：" + Purchase.getReason(code);
		} else {
			/**
			 * 查询到商品的相关信息。
			 * 此时你可以获得商品的paycode，orderid，以及商品的有效期leftday（仅租赁类型商品可以返回）
			 */
			leftday = (String) arg1.get(OnPurchaseListener.LEFTDAY);
			if (leftday != null && leftday.trim().length() != 0) {
				result = result + ",剩余时间 ： " + leftday;
			}
			orderID = (String) arg1.get(OnPurchaseListener.ORDERID);
			if (orderID != null && orderID.trim().length() != 0) {
				result = result + ",OrderID ： " + orderID;
			}
			paycode = (String) arg1.get(OnPurchaseListener.PAYCODE);
			if (paycode != null && paycode.trim().length() != 0) {
				result = result + ",Paycode:" + paycode;
			}
		}
		System.out.println(result);
	 
	}

	

	@Override
	public void onUnsubscribeFinish(int code) {
		// TODO Auto-generated method stub
		String result = "退订结果：" + Purchase.getReason(code);
		System.out.println(result);
		 
	}
	
 
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
