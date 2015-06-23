package com.ddworlds.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ddworlds.obj.AllObj;
import com.ddworlds.yzabs.SMSActivity;
import cn.ultralisk.gameapp.yjsg.yuzhuo.R;
import com.ddworlds.yzabs.RequestActivity;
import com.ddworlds.yzabs.SelectActivity;

public class ResultDialog extends Dialog implements OnClickListener{
	private String msg; 
	private  int flag1;
	private  int result1;
	private Context context1;
	public ResultDialog(Context context,int theme,String msg,int flag,int result) {
		//super(context,theme);
		super(context,R.style.ResultDialog);
		System.out.println("new ResultDialog");
		this.msg=msg;
		flag1= flag;
		result1= result;
		this.context1=context;
		// TODO Auto-generated constructor stub
	}
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultdialog);
		initViews();
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME:return true;
		case KeyEvent.KEYCODE_BACK:return true;
		case KeyEvent.KEYCODE_CALL:return true;
		case KeyEvent.KEYCODE_SYM: return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
		case KeyEvent.KEYCODE_VOLUME_UP: return true;
		case KeyEvent.KEYCODE_STAR: return true;
		}
        
		return super.onKeyDown(keyCode, event);
	}




	private void initViews(){
		TextView resultinfo=(TextView)findViewById(R.id.resultinfo);
		resultinfo.setText(msg);
		Button btn_ok= (Button)findViewById(R.id.btn_confirm);
		btn_ok.setOnClickListener(this);
	}




	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id=v.getId();
		if (id==R.id.btn_confirm){
			System.out.println("confirm");
			 if(flag1==1){
			    	((RequestActivity) context1).finish();
			    }else if(flag1==2){
			    	((SMSActivity) context1).finish();
			    }else if(flag1==3){
			    	if(result1==1){//扣费成功
			    		((SelectActivity) context1).finish();
			    		String _s=String.valueOf(SelectActivity.payType)+"|"+SelectActivity.orderno_ZFB;
			    		AllObj.ABSListener1.callBack(result1, _s);//0扣费失败1成功
			    	}else{//扣费失败
//			    		((RequestActivity) context1).finish();
			    		((SelectActivity) context1).finish();
			    		String _s="9999";
//			    		String _s=String.valueOf(SelectActivity.payType)+"|"+SelectActivity.orderno_ZFB;
			    		AllObj.ABSListener1.callBack(result1, _s);//0扣费失败1成功
			    	}
			    }else if(flag1==4){
			    	if(result1==1){//扣费成功
//			    		((CFTActivity) context1).finish();
//			    		String _s=String.valueOf(Activity3.payType)+"|"+Activity3.orderno_ZFB;
//			    		Activity1.AbsListener1.callBack(result1, _s);//0扣费失败1成功
			    	}
			    }else if(flag1==5){
			    	if(result1==1){//扣费成功
			    		((RequestActivity) context1).finish();
			    		String _s=String.valueOf(SelectActivity.payType)+"|"+SelectActivity.orderno_ZFB;
			    		AllObj.ABSListener1.callBack(result1, _s);//0扣费失败1成功
			    	}else{//扣费失败
			    		((RequestActivity) context1).finish();
			    		String _s="9999";
//			    		String _s=String.valueOf(SelectActivity.payType)+"|"+SelectActivity.orderno_ZFB;
			    		AllObj.ABSListener1.callBack(result1, _s);//0扣费失败1成功
			    	}
			    }
			    	
			
			dismiss();
		}
	}

}
