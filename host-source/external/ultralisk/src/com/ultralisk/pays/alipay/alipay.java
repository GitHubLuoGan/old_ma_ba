/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.ultralisk.pays.alipay;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ultralisk.CallbackListener;

import @APP_PACKAGE@.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 模拟商户应用的商品列表，交易步骤。
 * 
 * 1. 将商户ID，收款帐号，外部订单号，商品名称，商品介绍，价格，通知地址封装成订单信息 2. 对订单信息进行签名 3.
 * 将订单信息，签名，签名方式封装成请求参数 4. 调用pay方法
 * 
 * @version v4_0413 2012-03-02
 */
@SuppressLint("HandlerLeak")
public class alipay {
	private Activity mActivity;

//	private ProgressDialog mProgress = null;
	
	private float mPrice;
	private String mProductDesc;
	private String mExtInfo;
	private String mNotifyUrl;
	private CallbackListener mCallback;
	
	public alipay(Activity activity, float price, String desc, String extInfo, String notifyUrl, CallbackListener callback) 
	{
		mActivity 		= activity;
		mPrice 			= price;
		mProductDesc 	= desc;
		mExtInfo 		= extInfo;
		mNotifyUrl 		= notifyUrl;
		mCallback		= callback;
	}
	
	public boolean startPay()
	{
		// 检测安全支付服务是否被安装
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mActivity);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist) 
		{	
			return false;
		}
		
		// 检测配置信息
		if (!checkInfo()) {
			BaseHelper
					.showDialog(
							mActivity,
							"提示",
							"缺少partner或者seller，请在src/com/ultralisk/pays/alipay/PartnerConfig.java中增加。",
							R.drawable.infoicon);
			return false;
		}
		
		// 根据订单信息开始进行支付
		try {
			// prepare the order info.
			// 准备订单信息
			String orderInfo = getOrderInfo();
			// 这里根据签名方式对订单信息进行签名
			String signType = getSignType();
			String strsign = sign(signType, orderInfo);
			Log.v("sign:", strsign);
			// 对签名进行编码
			strsign = URLEncoder.encode(strsign, "UTF-8");
			// 组装好参数
			String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
					+ getSignType();
			Log.v("orderInfo:", info);
			// start the pay.
			// 调用pay方法进行支付
			MobileSecurePayer msp = new MobileSecurePayer();
			msp.pay(info, mHandler, AlixId.RQF_PAY, mActivity);

//			if (bRet) {
//				// show the progress bar to indicate that we have started
//				// paying.
//				// 显示“正在支付”进度条
//				closeProgress();
//				mProgress = BaseHelper.showProgress(mActivity, null, "正在支付", false,
//						true);
//			} 
		} catch (Exception ex) {
			Toast.makeText(mActivity, R.string.ul_alipay_remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}

	String getOrderInfo() 
	{
		String strOrderInfo = "partner=\"" 	+ PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&seller=\"" 		+ PartnerConfig.SELLER 	+ "\"";
		strOrderInfo += "&out_trade_no=\"" 	+ getOutTradeNo() 		+ "\"";
		strOrderInfo += "&subject=\"" 		+ mProductDesc 			+ "\"";
		strOrderInfo += "&body=\"" 			+ mProductDesc 			+ "\"";
		strOrderInfo += "&total_fee=\"" 	+ mPrice 				+ "\"";
		strOrderInfo += "&notify_url=\"" 	+ mNotifyUrl 			+ "\"";

		return strOrderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 * @return
	 */
	String getOutTradeNo()
	{
		
		String strKey = mExtInfo;
		
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String strTime = format.format(date);

		java.util.Random r = new java.util.Random();
		strTime = strTime + r.nextInt();
		strTime = strTime.substring(0, 15);
		strKey = strKey + "_" + strTime;
		
		return strKey;
	}

	//
	//
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	String sign(String signType, String content) {
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	/**
	 * get the char set we use. 获取字符集
	 * 
	 * @return
	 */
	String getCharset() {
		String charset = "charset=" + "\"" + "utf-8" + "\"";
		return charset;
	}

	/**
	 * check some info.the partner,seller etc. 检测配置信息
	 * partnerid商户id，seller收款帐号不能为空
	 * 
	 * @return
	 */
	private boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		if (partner == null || partner.length() <= 0 || seller == null
				|| seller.length() <= 0)
			return false;

		return true;
	}

	//
	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String ret = (String) msg.obj;

				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
//					closeProgress();

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						
						String tradeStatus = "resultStatus={";
						int imemoStart = ret.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						String subFromStatus = ret.substring(imemoStart);
						int imemoEnd = subFromStatus.indexOf("};");
						
						tradeStatus = subFromStatus.substring(0, imemoEnd);

						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(ret);
						int retVal = resultChecker.checkSign();
						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(
									mActivity,
									"提示",
									mActivity.getResources().getString(
											R.string.ul_alipay_check_sign_failed),
									android.R.drawable.ic_dialog_alert);
						} else 
						{	
							// 验签成功。验签成功后再判断交易状态码
							if (tradeStatus.equals("9000"))// 判断交易状态码，只有9000表示交易成功
							{
								if (mCallback != null)
								{
									mCallback.onPaymentSuccess("0");
								}
							}
							else
							{
								String errMsg = "支付失败";
								if ("6001".equals(tradeStatus))
								{
									errMsg = "取消支付";
								}
								else
								{
									errMsg = errMsg + "，错误码" + tradeStatus;
								}
//								if (mCallback != null)
//								{
//									mCallback.onPaymentError(errMsg, "0");
//								}
								Toast.makeText(mActivity, "支付失败，"+errMsg,Toast.LENGTH_SHORT).show();
							}
							
								
						}

					} catch (Exception e) {
						e.printStackTrace();
//						BaseHelper.showDialog(mActivity, "提示", ret,
//								R.drawable.infoicon);
						Toast.makeText(mActivity, "支付异常", Toast.LENGTH_SHORT).show();
//						if (mCallback != null)
//						{
//							mCallback.onPaymentError("支付异常", "0");
//						}
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	//
	//
	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
	 */
	static class AlixOnCancelListener implements
			DialogInterface.OnCancelListener {
		Activity mcontext;

		AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	//
	// close the progress bar
	// 关闭进度框
//	void closeProgress() {
//		try {
//			if (mProgress != null) {
//				mProgress.dismiss();
//				mProgress = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	void destroy()
//	{
//		try {
//			mProgress.dismiss();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}