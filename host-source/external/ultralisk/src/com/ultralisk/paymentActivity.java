package com.ultralisk;

import com.ultralisk.Constants;
import com.ultralisk.CallbackListener;
import com.ultralisk.pays.shenzhoufu.Shenzhoufu;
import com.ultralisk.pays.shenzhoufu.ShenzhoufuNew;
import com.ultralisk.pays.alipay.alipay;
 
import @APP_PACKAGE@.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class paymentActivity extends Activity implements View.OnClickListener {

	private float  mMoney 		= 0.0f;
	private String mProductName = "";
	private String mRoleName 	= "";
	private String mExtInfo 	= "";
	private String mNotifyUrl 	= "";
	private String mMerchantId	= "0";
	private CallbackListener mCallback = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ul_payment);

        findViewById(R.id.cashier_back).setOnClickListener(this);
        findViewById(R.id.alipay).setOnClickListener(this);
        findViewById(R.id.china_mobile).setOnClickListener(this);
        findViewById(R.id.china_unicom).setOnClickListener(this);
        findViewById(R.id.china_telecom).setOnClickListener(this);
        findViewById(R.id.yikatong_telecom).setOnClickListener(this);
        Intent intent = getIntent(); 
        
        mMoney 			= intent.getFloatExtra(Constants.PARAM_PRICE, 1.0f);
        mProductName 	= intent.getStringExtra(Constants.PARAM_DESC);
        mRoleName 		= intent.getStringExtra(Constants.PARAM_ROLE_NAME);
        mExtInfo 		= intent.getStringExtra(Constants.PARAM_EXT_INFO);
        mMerchantId		= intent.getStringExtra(Constants.PARAM_MERCHANT_ID);
        mExtInfo 		= mExtInfo.replace('|', '_') + "_" + mMerchantId;
        mNotifyUrl 		= intent.getStringExtra(Constants.PARAM_NOTIFY_URL);
        mCallback		= (CallbackListener)intent.getSerializableExtra(Constants.PARAM_CALLBACK);
        
        ((TextView)findViewById(R.id.price_text)).setText("" + mMoney + "元");
        ((TextView)findViewById(R.id.name_text)).setText(mRoleName);
        ((TextView)findViewById(R.id.des_text)).setText(mProductName);
       
        
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cashier_back:
                finish();
                break;
            case R.id.alipay:
            	onClickAlipay();
                break;
            case R.id.china_mobile:
            	onClickChargeCard(Constants.CHARGE_CARD_CHINAMOBILE);
            	break;
            case R.id.china_unicom:
            	onClickChargeCard(Constants.CHARGE_CARD_CHINAUNICOM);
            	break;
            case R.id.china_telecom:
            	onClickChargeCard(Constants.CHARGE_CARD_CHINATELECOM);
            	break;
            case R.id.yikatong_telecom:
            	onClickChargeCard(Constants.CHARGE_CARD_YIKATONG);
            	break;
            default:
                break;
        }
    }
    private boolean checkCardLimit(int type)
    {
    	int[] priceLimit = null;
    	switch(type)
    	{
	    	case Constants.CHARGE_CARD_CHINAMOBILE:
	    		priceLimit = Constants.CHINAMOBILE_PRICES;
	    		break;
	    	case Constants.CHARGE_CARD_CHINAUNICOM:
	    		priceLimit = Constants.CHINAUNICOM_PRICES;
	    		break;
	    	case Constants.CHARGE_CARD_CHINATELECOM:
	    		priceLimit = Constants.CHINATELECOM_PRICES;
	    		break;
	    	case Constants.CHARGE_CARD_YIKATONG:
	    		 return true;//如果是新加的统一一卡通方式，则不在这里验证。
	    		//break;
	    	default:
	    		break;
    	}
    	
    	if (priceLimit == null)
    	{
    		return false;
    	}
    	
    	int priceInt = (int)mMoney;
    	for (int price:priceLimit)
    	{
    		if(price == priceInt)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    private void onClickChargeCard(int type)
    {
    	if(!checkCardLimit(type))
    	{
    		Toast.makeText(this, "对不起，不支持此面额的充值卡",Toast.LENGTH_SHORT).show();
    		return;
    	}
    	int cardType = type;
    	String notifyUrl = mNotifyUrl + "/ultralisk/shenzhoufu/paynotify";
    	Intent intent;
    	if(type==Constants.CHARGE_CARD_YIKATONG)
    	{
    	  intent = new Intent(this, ShenzhoufuNew.class);
    	  notifyUrl=mNotifyUrl + "/ultralisk/shenzhoufu/paynotify_ka";
    	}
    	else{
    	   intent = new Intent(this, Shenzhoufu.class);    		
    	}
    	
    	intent.putExtra(Constants.PARAM_PRICE, 					(int)mMoney);
    	intent.putExtra(Constants.PARAM_EXT_INFO, 				mExtInfo);
    	intent.putExtra(Constants.PARAM_ROLE_NAME, 				mRoleName);
    	intent.putExtra(Constants.PARAM_NOTIFY_URL, 			notifyUrl);
    	intent.putExtra(Constants.CHARGE_CARD_PARAM_CARD_TYPE,	cardType);
    	intent.putExtra(Constants.PARAM_CALLBACK, 				mCallback);
        startActivity(intent);
    	
    }
	
    private void onClickAlipay()
    {
    	String notifyUrl = mNotifyUrl + "/ultralisk/alipay/paynotify";
    	alipay pay = new alipay(this, mMoney, mProductName, mExtInfo, notifyUrl, mCallback);
    	pay.startPay();
    }

}

