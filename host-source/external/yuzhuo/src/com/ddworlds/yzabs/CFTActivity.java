package com.ddworlds.yzabs;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.utils.Params;
import com.tenpay.android.service.TenpayServiceHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CFTActivity extends Activity {
	private Button cftBtn;
	private String tokeId;
	private int CFTResult;//财富同支付结果
	public static Context context3;
	
final static int MSG_PAY_RESULT = 100;
	
	//接收支付返回值的Handler
	protected Handler mHandler = new Handler()	{  
		  public void handleMessage(Message msg) {							
				switch (msg.what){
					case MSG_PAY_RESULT:
						String strRet = (String)msg.obj; // 支付返回值
                        
						String statusCode = null;
						String info = null;
						String result = null;
						
						JSONObject jo;
						try {
							jo = new JSONObject(strRet);
							if (jo != null) {
								statusCode = jo.getString("statusCode");
								info = jo.getString("info");
								result  = jo.getString("result");								
							}	
						} catch (JSONException e1) {			
							e1.printStackTrace();
						}		
						
						String ret = "statusCode = " + statusCode + ", info = " + info
									+ ", result = " + result;
						System.out.println(">>>>>>>>>CFTactivity3ret="+ret);
						//按协议文档，解析并判断返回值，从而显示自定义的支付结果界面
						CFTResult=0;//1是计费成功 2是取消扣费0扣费失败
						Product pro=AllObj.pro;
						if (statusCode!=null && (statusCode.equals("0") || statusCode.equals("28020205"))){
							ret="恭喜您成功购买"+pro.name+","+pro.des+"!";
							CFTResult=1;
						}else{
							ret="抱歉您购买"+pro.name+"失败!";
						}
						
						finish();
						SelectActivity.sendCFThandler(ret, statusCode, CFTResult);
						
//						ResultDialog dlg=new ResultDialog(context3,R.style.PayDialog, ret, 3, CFTResult);
//						dlg.show();
//						if (statusCode!=null && statusCode.equals("66200003")==false ){
//							
//							ResultDialog dlg=new ResultDialog(Activity3.context,R.style.PayDialog, ret, 4, CFTResult);
//							dlg.show();
//							
//						}else{
//							if (Params.tip_debug){
//								Toast.makeText(Activity3.context, "用户取消财富通支付", Toast.LENGTH_SHORT).show();
//							}
//						}
						
						break; 
				}
		  }
	};
	
	
	
	
	
	

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.alpha=0f;
		getWindow().setAttributes(lp);
		bundle=getIntent().getExtras();
		tokeId=bundle.getString("tokeid") ;
		CFTclick(tokeId);
		
//		setContentView(R.layout.cftpay);
//		cftBtn=(Button) findViewById(R.id.cftbtn);
//		cftBtn.setOnClickListener(l1);
	}
	
	
	/**
	 * 财付通
	 */
	private void CFTclick(String token_id){
		//先得到财付通订单号
		
		String mTokenId = token_id;
		/*if (mTokenId == null || mTokenId.length() <  32) {
			String _info="订单号为空或位数不对，请重新购买！";
			Toast.makeText(CFTActivity.this, _info, Toast.LENGTH_SHORT).show();
			return;     
		}
		TenpayServiceHelper tenpayHelper = new TenpayServiceHelper(CFTActivity.this);
		tenpayHelper.setLogEnabled(true); //打开log 方便debug, 发布时不需要打开。
		//判断并安装财付通安全支付服务应用  
		boolean _ret=tenpayHelper.isTenpayServiceInstalled(9);
		if (!_ret) {
			tenpayHelper.installTenpayService(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					Toast.makeText(CFTActivity.this, "用户取消了安装", Toast.LENGTH_LONG).show();
					
				}
			}, "/sdcard/test");  
			return;
		}*/
		
		//构造支付参数
		HashMap<String, String> payInfo = new HashMap<String, String>();
		payInfo.put("token_id", mTokenId);         //财付通订单号token_id
		payInfo.put("bargainor_id", "1216130901"); //"1234567890"财付通合作商户ID	
		TenpayServiceHelper tenpayHelper=SelectActivity.tenpayHelper;
		//去支付
		boolean ret=tenpayHelper.pay(payInfo, mHandler, MSG_PAY_RESULT);  
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		String message=newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE ? "屏幕设置为：横屏" : "屏幕设置为：竖屏";
		if (Params.tip_debug){
			 Toast.makeText(CFTActivity.this, message, Toast.LENGTH_LONG).show(); 
		 }
	}

}
