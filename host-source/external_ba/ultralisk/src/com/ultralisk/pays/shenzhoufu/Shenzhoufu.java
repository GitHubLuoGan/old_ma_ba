package com.ultralisk.pays.shenzhoufu;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.ultralisk.CallbackListener;
import com.ultralisk.Constants;

import @APP_PACKAGE@.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Shenzhoufu extends Activity implements View.OnClickListener {

	private int mCardType;
	private String mNotifyUrl;
	private String mUserName;
	private String mExtInfo;
	private int mMoney;
	private CallbackListener mCallback;
	private String mPaymentUrl;
	private static final String SHENZHOUFU_PAY_URL_BASE = "http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx";
	
	private String mOrderId; //for call back
	private boolean mIsPaying = false;
	private ProgressDialog mProgressDialog;
	 @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.ul_payment_card);
		 
		 Intent intent = getIntent(); 
	        
		 mCallback	= (CallbackListener) intent.getSerializableExtra(Constants.PARAM_CALLBACK);
		 mCardType  = intent.getIntExtra(Constants.CHARGE_CARD_PARAM_CARD_TYPE, Constants.CHARGE_CARD_CHINAMOBILE);
		 mMoney 	= intent.getIntExtra(Constants.PARAM_PRICE, 10);
		 mExtInfo 	= intent.getStringExtra(Constants.PARAM_EXT_INFO);
		 mNotifyUrl = intent.getStringExtra(Constants.PARAM_NOTIFY_URL);
		 mUserName	= intent.getStringExtra(Constants.PARAM_ROLE_NAME);
		 
		 findViewById(R.id.recharge_back_btn).setOnClickListener(this);
	     findViewById(R.id.recharge_btn).setOnClickListener(this);
	     
	     TextView tbType = (TextView)findViewById(R.id.recharge_mode_text);
	     TextView tbMoney = (TextView)findViewById(R.id.recharge_price_text);
	     String strType = "";
	     switch(mCardType)
	     {
	     	case Constants.CHARGE_CARD_CHINAMOBILE:
	     		strType = Constants.CARD_NAME_CHINAMOBILE;
	     		break;
	     	case Constants.CHARGE_CARD_CHINAUNICOM:
	     		strType = Constants.CARD_NAME_CHINAUNICOM;
	     		break;
	     	case Constants.CHARGE_CARD_CHINATELECOM:
	     		strType = Constants.CARD_NAME_CHINATELECOM;
	     		break;
	     }
	     tbType.setText(strType);
	     tbMoney.setText(""+mMoney+"元");
		 
	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
	        case R.id.recharge_back_btn:
	            finish();
	            break;
	        case R.id.recharge_btn:
	        	onClickChargeBtn();
	            break;
		}
	}
	private String urlEncodeUTF8(String str)
	{
		String res = str;
		try {
			res = java.net.URLEncoder.encode(res, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	private String getOrderId(String merId)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
		Date date = new Date();
		String strTime = format.format(date);
		String strOrder =  strTime + "-" + merId;
		
		String uuid = UUID.randomUUID ().toString ();
		if(uuid.length() > 20)
		{
			uuid = uuid.substring(0, 20);
		}
		uuid = uuid.replace("-", "");
		strOrder += "-" + uuid;
		
		return strOrder;
	}
	private String getCardInfo(String cardNo, String cardPw)
	{
		String cardMoney = "" + mMoney;
		String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(cardMoney, cardNo, cardPw, Config.MERCHANT_DES_KEY);
		return cardInfo;
	}
	
	
	private String makePaymentUrl(String cardNo, String cardPw)
	{
		String version 		= "3";
		String merId 		= Config.MERCHANT_ID;
		int    payMoney 	= mMoney * 100;
		String orderId		= getOrderId(merId);
		String returnUrl	= mNotifyUrl;
		String cardInfo		= getCardInfo(cardNo, cardPw);
		String merUserName	= urlEncodeUTF8(mUserName);
		String merUserMail	= "unkownmail";
		String privateField = mExtInfo;
		String verifyType 	= "1";
		int cardTypeCombine = mCardType;
		String md5String 	= MD5Util.MD5(version +merId+ payMoney + orderId + returnUrl + cardInfo 
								+ privateField + verifyType + Config.MERCHANT_PRIVATE_KEY);
		
		
		String payUrl = SHENZHOUFU_PAY_URL_BASE +"?";
		String requestCode = "";
		requestCode = requestCode + "version=" + version;
		requestCode = requestCode + "&merId=" + merId;
		requestCode = requestCode + "&payMoney=" + payMoney;
		requestCode = requestCode + "&orderId=" + orderId;
		requestCode = requestCode + "&returnUrl=" + returnUrl;
		requestCode = requestCode + "&cardInfo=" + urlEncodeUTF8(cardInfo);
		requestCode = requestCode + "&merUserName=" + merUserName;
		requestCode = requestCode + "&merUserMail=" + merUserMail;
		requestCode = requestCode + "&privateField=" + privateField;
		requestCode = requestCode + "&verifyType=" + verifyType;
		requestCode = requestCode + "&cardTypeCombine=" + cardTypeCombine;
		requestCode = requestCode + "&md5String=" + md5String;
		
//		try {
//			requestCode = URLEncoder.encode(requestCode, "UTF-8");
//		} catch (UnsupportedEncodingException e) 
//		{
//			e.printStackTrace();
//		}
		
		payUrl = payUrl + requestCode;
		
		mOrderId = orderId;
		
		return payUrl;
	}
	private void startPay(String cardNo, String cardPw)
	{
		if(mIsPaying)
		{
			Toast.makeText(this, "正在支付，请不要重复提交。",Toast.LENGTH_SHORT).show();
			return;
		}
		mIsPaying = true;
		showProgress("正在支付...");
		mPaymentUrl = makePaymentUrl(cardNo, cardPw);
		new Thread(new Runnable() {
			public void run() {
				try {  
		            HttpRequester request = new HttpRequester();  
		            HttpRespons hr = request.sendGet(mPaymentUrl);
		            Message msg = new Message();
					msg.obj = hr.getContent();
					mHandler.sendMessage(msg);
					mIsPaying = false;
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        
			}
		}).start();
	}
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String retStr = (String)msg.obj;
			hideProgress();
			CheckPayResult(retStr);
		}

	};
	private void CheckPayResult(String content)
	{
		int    code = -1;
		String msg = "";
		
		content = content.trim();
		try
		{
			code = Integer.parseInt(content);
		}catch(NumberFormatException e){}
		
		switch(code)
		{
			case 101:  
				msg = "md5 验证失败";
				break;
			case 102:  
				msg = "订单号重复";
				break; 
			case 103:  
				msg = "恶意用户";
				break;
			case 104:  
				msg = "序列号，密码简单验证失败或之前曾提交过的卡密已验证失败";
				break;
			case 105:  
				msg = "密码正在处理中";
				break; 
			case 106:  
				msg = "系统繁忙，暂停提交";
				break; 
			case 107:  
				msg = "多次支付时卡内余额不足";
				break; 
			case 109:  
				msg = "des 解密失败";
				break; 
			case 201: 
				msg = "证书验证失败";
				break; 
			case 501:  
				msg = "插入数据库失败";
				break;
			case 502:  
				msg = "插入数据库失败";
				break; 
			case 200:  
				msg = "请求成功，神州付收单（非订单支付成功）";
				break; 
			case 902:  
				msg = "商户参数不全";
				break; 
			case 903:  
				msg = "商户 ID 不存在";
				break; 
			case 904:  
				msg = "商户没有激活";
				break; 
			case 905:  
				msg = "商户没有使用该接口的权限";
				break; 
			case 906:  
				msg = "商户没有设置  密钥（privateKey）";
				break; 
			case 907: 
				msg = "商户没有设置  DES 密钥 ";
				break; 
			case 908:  
				msg = "该笔订单已经处理完成 （订单状态已经为确定的状态： 成功  或 者  失败）";
				break; 
			case 909:  
				msg = "该笔订单不符合重复支付的条件";
				break; 
			case 910:  
				msg = "服务器返回地址，不符合规范";
				break; 
			case 911:  
				msg = "订单号，不符合规范";
				break; 
			case 912:  
				msg = "非法订单";
				break; 
			case 913:  
				msg = "该地方卡暂时不支持";
				break; 
			case 914:  
				msg = "支付金额非法";
				break; 
			case 915:  
				msg = "卡面额非法";
				break; 
			case 916:  
				msg = "商户不支持该充值卡的支付";
				break; 
			case 917:  
				msg = "参数格式不正确";
				break; 
			case 0:  
				msg = "网络连接失败";
				break; 
			default:
				msg = "未知错误";
				break;
		}
		
		if (code == 200)
		{
			Toast.makeText(this, "支付订单提交成功！",Toast.LENGTH_SHORT).show();
			mCallback.onPaymentSuccess(mOrderId);
		}
		else
		{
			Toast.makeText(this, "支付失败，" + msg,Toast.LENGTH_SHORT).show();
			mCallback.onPaymentError(msg, mOrderId);
		}
	}
	private void onClickChargeBtn()
	{
		EditText tbCardNo = (EditText)findViewById(R.id.recharge_number_edit);
		EditText tbCardPw = (EditText)findViewById(R.id.recharge_password_edit);

	    String cardNo = tbCardNo.getText().toString();
	    String cardPw = tbCardPw.getText().toString();
	    
	    if (cardNo.equals("") || cardPw.equals(""))
	    {
	    	Toast.makeText(this, "充值卡号或密码不能为空",Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
	    startPay(cardNo, cardPw);
	    
	}
	
	protected void showProgress(String tips)
	{
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(tips);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	protected void hideProgress()
	{ 
		if(mProgressDialog != null)
		{
			mProgressDialog.cancel();
			mProgressDialog = null;
		} 
	}
}