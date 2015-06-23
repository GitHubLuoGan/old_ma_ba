package com.ultralisk;

import com.ultralisk.Constants;
import com.ultralisk.paymentActivity;

import android.content.Context;
import android.content.Intent;

public class Ultralisk {


    private String merchantId;

//    private String appId;
//
//    private String serverSeqNum;
//
//    private String appKey;

    public Ultralisk(String merchantId, String appId, String serverSeqNum, String appKey) {
        this.merchantId=merchantId;
//        this.appId=appId;
//        this.serverSeqNum=serverSeqNum;
//        this.appKey=appKey;
    }

    
    /**
     * 打开支付界面
     * @param context
     * @param money 金额，单位：元
     * @param productName 商品名称
     * @param roleName 角色名
     * @param notify_url  通知地址
     * @param extInfo cp自定义信息
     * @param listener
     * @return 返回当乐订单号
     */
    public void openPaymentDialog(Context context, float money, String productName, String roleName, String extInfo, String notify_url, CallbackListener listener) 
    {
    	Intent intent = new Intent(context, paymentActivity.class);
    	
    	intent.putExtra(Constants.PARAM_PRICE, 		money);
    	intent.putExtra(Constants.PARAM_DESC, 		productName);
    	intent.putExtra(Constants.PARAM_ROLE_NAME, 	roleName);
    	intent.putExtra(Constants.PARAM_EXT_INFO, 	extInfo);
    	intent.putExtra(Constants.PARAM_NOTIFY_URL, notify_url);
    	intent.putExtra(Constants.PARAM_MERCHANT_ID, merchantId);
    	intent.putExtra(Constants.PARAM_CALLBACK, listener);
    	
        context.startActivity(intent);
    }
    
    


}

