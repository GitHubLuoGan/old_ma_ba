package com.ddworlds.Result;

public class CallBackResult implements ABSListener {

	@Override
	public void callBack(int result, String msg) {
		// TODO Auto-generated method stub
		System.out.println("callbackresult="+result+"msg="+msg);
	}

}
