package com.ultralisk;

import java.io.Serializable;

/**
 * 业务回调父类
 */
public abstract class CallbackListener  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2685415344548814256L;

	/**
     * 支付成功
     */
    public void onPaymentSuccess(String orderNo) 
    {
    }
    
    /**
     * 支付错误回调
     * @param error
     */
    public void onPaymentError(String error, String orderNo)
    {
    }
}