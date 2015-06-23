package com.ddworlds.Result;

public interface ABSListener {
	//result 1是计费成功 2是取消扣费0扣费失败
	//msg= paytype|orderNo paytype 0是取消支付没有任何支付方式 1 短信 2. 支付宝 3.财付通 4.银行卡 5充值卡 6游戏点卡
	public void callBack(int result,String msg);//返回结果
}
