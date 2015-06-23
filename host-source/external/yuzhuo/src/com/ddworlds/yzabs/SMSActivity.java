package com.ddworlds.yzabs;

import com.ddworlds.Result.ABSListener;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;

import @APP_PACKAGE@.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class SMSActivity extends Activity {
	/**
	 * 需要接收的数据有 爱贝支付需要的price and waresid
	 * abs支付需要 product_name, price, describe
	 */
	public static ABSListener AbsListener1;//回调给客户端
	
	private Button btn_buy, btn_cancel;
	
	private TextView txt_name, txt_price, txt_des, txt_info;
	private static Product obj;//产品对象  
	/**
	 * 初始化product
	 * @param pro
	 */
	public static void init_product(Product pro){
		obj=pro;  
	}
	public static void init_abs(ABSListener l1){
		AbsListener1=l1;  
	}
	

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.smspay);
		
		txt_info=(TextView)findViewById(R.id.txt_info);
		
		Intent intent1=getIntent();
		bundle=intent1.getExtras();
		String show_tip=bundle.getString("buyinfo");
		txt_info.setText("恭喜您购买成功!"+show_tip);
		
		btn_buy=(Button) findViewById(R.id.btn_buy);
		btn_buy.setOnClickListener(listener1); 
		
		
	}   
	
	
	
	OnClickListener listener1= new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id= v.getId();
			if(id==R.id.btn_buy){
				//buy_click();
				finish();
				SMSActivity.AbsListener1.callBack(1,"1");//扣费成功
			}
			
		}

		
	};
	
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode==KeyEvent.KEYCODE_BACK){
				return true;
			}else if(keyCode==KeyEvent.KEYCODE_HOME){
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

