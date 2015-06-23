package com.ddworlds.Result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ddworlds.dialog.RequestDialog;
import com.ddworlds.dialog.ResultDialog;
import com.ddworlds.listener.ResultListener;
import cn.ultralisk.gameapp.yjsg.yuzhuo.R;
import com.ddworlds.yzabs.RequestActivity;


public class MyResult implements ResultListener{
	private Context context;
	private Handler myHandler;
	public MyResult(Context context,Handler myHandler){
		this.context=context;
		this.myHandler=myHandler;
	}

	@Override
	public void onBillingFinish(String result) {
		// TODO Auto-generated method stub
		System.out.println("My Result="+result);
		String show_tip="抱歉，获取支付请求失败,请重试！";
		String result_flag=result;
		RequestDialog.waitDialog.dismiss();
		Message msg = new Message();
		Bundle b = new Bundle();// 存放数据
		b.putString("info", show_tip);
		b.putInt("result", Integer.parseInt(result_flag));
		msg.setData(b);
		msg.what=1;
		myHandler.sendMessage(msg);
		
		
	}
	

		


}
