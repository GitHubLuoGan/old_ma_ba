package com.ddworlds.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class RequestDialog {
	public static ProgressDialog waitDialog;
	
	
	public static void requestDialogShow(Context context,String msg) {  //,Thread thread
      waitDialog = new ProgressDialog(context);  
      //设置风格为圆形  
      waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
      waitDialog.setTitle("付费提示");  
      waitDialog.setIcon(null);  
      //设置提示信息  
      waitDialog.setMessage(msg);  
      //设置是否可以通过返回键取消  
      waitDialog.setCancelable(false);  
      waitDialog.setIndeterminate(false);  
      //设置取消监听  
//      waitDialog.setOnCancelListener(new OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				// TODO Auto-generated method stub
//			}  
//      });  
      waitDialog.show();  
  }  
	
}
