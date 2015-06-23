package com.ddworlds.yzabs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ddworlds.Result.MyResult;
import com.ddworlds.dialog.RequestDialog;
import com.ddworlds.dialog.ResultDialog;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.utils.AbsLog;
import com.ddworlds.yzpma.PayRequest;
import cn.ultralisk.gameapp.yjsg.yuzhuo.R;

public class RequestActivity extends Activity {
	private Product pro;
	private TextView textView1;
	private Button btn1;  
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView1=(TextView) findViewById(R.id.request_txt1);
		textView1.setText("抱歉，您支付失败！");
		btn1=(Button) findViewById(R.id.request_btn1);
		btn1.setOnClickListener(l1);
		RequestDialog.requestDialogShow(RequestActivity.this, "付费请求已发出，请耐心等待...");
		try {
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						send_request();
					}
					
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
	OnClickListener l1= new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
			AllObj.ABSListener1.callBack(0, "88888");//强制失败
		}
		
	};
	
	
	
	/**
	 * 发送支付请求
	 */
	private void send_request(){
//		RequestDialog.requestDialogShow(RequestActivity.this, "付费请求已发出，请耐心等待...");
		pro=AllObj.pro;
		if (pro==null){
			getResult("0"); 
			return;
		}
		float price=pro.price;
		String cporderid=pro.CpOrderID;
		String cpuserid=pro.CpUserID;
		String cpdesc=pro.name; 
		
		String _debugStr= AbsLog.read_debug();//debug信息
		AbsLog.log_i("_debugStr", _debugStr);
		System.out.println("_debugStr="+_debugStr);  
		String _paramId="3000";//正式是1000测试是2000  
		if(_debugStr!=null && _debugStr.length()>0){
			String[] _s1=_debugStr.split("\\[PayDebug]");
			if(_s1!=null&&_s1.length==2){
				System.out.println("_s1[1]="+_s1[1]);        
				String[] _s2;
				if(_s1!=null && _s1.length==2 ){
					String _temp=_s1[1];
					_s2=_temp.split("\\=");
					if(_s2!=null && _s2.length==2){
						_paramId=_s2[1];    
					}
				}
			}
			
		}
		AbsLog.log_i("_paramId", _paramId);
		_paramId = _paramId.replace("\\s","");
		_paramId = _paramId.replace("\n","");
		AbsLog.log_i("_paramId1", _paramId);
		
		//测试渠道cid=2000 斗地主产品编号是2000  正式cid是1000 斗地主产品编号是1000
		String str=_paramId+"&fid="+price+"&cid="+_paramId+"&CpOrderID="+cporderid+"&CpUserID="+cpuserid+"&CpOrderDesc="+cpdesc;//cid渠道号
		AbsLog.log_i("_params", str);
		System.out.println("str="+str);
//		MyResult myResult1= new MyResult(RequestActivity.this,myHandler);//请求没有反应的时候就走这个回调
//		String result_msg=new PayRequest(RequestActivity.this,str, myResult1).run();
//		String result_msg=new PayRequest(RequestActivity.this,str).run1();
		String result_msg=new PayRequest(RequestActivity.this,str).run();
		
		System.out.println("_str activity="+result_msg);
		if(result_msg!="99999999"){//"99999999"是数据已经被处理过了这个由于execute处理没有返回值所以这样特殊处理的 "99999999"是已经走回调走掉了 所以不要再处理
			getResult(result_msg);        
		}
		
	}
	
	/**
	 * 处理返回结果
	 * @param result_msg
	 */
	private void getResult(String result_msg){
		String show_tip;
		String orderno_ZFB="";//支付宝订单号
		String result_flag="0";//1是成功0是失败-1是其他支付方式
//		1 话费 2. 支付宝 3.财付通 4.银行卡 5充值卡 6游戏点卡
		String other_pay="";//other's pay method 
		if(result_msg==null || result_msg.length()<1||result_msg.equals("0")){
			show_tip="抱歉，您支付失败，请检查网络是否正常！";
		}else{
			String[] _table1= result_msg.split("\\|");
//			AbsLog.log_i("_table1[0]=",_table1[0]);
//			_table1[0]="1";
			if(_table1[0].equals("1")){//支付成功
				show_tip=_table1[1];
				result_flag="1";
			}else if(_table1[0].equals("-1")){//其他支付方式
				orderno_ZFB=_table1[2];
				show_tip="您可以选择其他支付方式";
				result_flag="-1";
				other_pay=_table1[1];
			}else if(_table1[0].equals("0")){//支付失败
				show_tip=_table1[1];
				result_flag="0";  
			}else{//支付失败
				show_tip="抱歉，您支付失败，请检查网络是否正常！";
				result_flag="0";  
			}
		}
		
//		PmxLog.log_i("show_tip=", show_tip);
//		show_tip="【初入职场应学的10种能力】：1、考虑问题时的换位思考能力。2、强于他人的总结能力。3、简洁的文字表达能力。4、信息资料收集能力。5、目标调整能力。6、超强的自我安慰能力。7、书面沟通能力。8、企业文化的适应能力。9、客观对待忠诚。10、勇于接受份外之事";
//		result_flag="0";//use for testing
//		1 话费 2. 支付宝 3.财付通 4.银行卡 5充值卡 6游戏点卡
		if(result_flag.equals("-1")){//其他支付方式
			finish();
			Intent intent1= new Intent(RequestActivity.this, SelectActivity.class);
			intent1.putExtra("method", other_pay);//购买提示信息
			intent1.putExtra("orderno_ZFB", orderno_ZFB);//支付宝订单号
			startActivity(intent1);
			
		}else if(result_flag.equals("1")){//支付成功
			//显示计费掉ABSActivity
			finish();
			SMSActivity.init_product(pro);
			SMSActivity.init_abs(AllObj.ABSListener1);
			Intent intent1= new Intent(RequestActivity.this, SMSActivity.class);
			intent1.putExtra("buyinfo", show_tip);//购买提示信息
			startActivity(intent1);
		}
		else if(result_flag.equals("0")){//支付失败 
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
	
	
	Handler myHandler= new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1){
				Bundle b = msg.getData();
				String info = b.getString("info");
				int result=b.getInt("result");
				//ResultDialog dlg=new ResultDialog(RequestActivity.this,R.style.PayDialog, info, 3, result);
				ResultDialog dlg=new ResultDialog(RequestActivity.this,R.style.PayDialog, info, 5, result);
				dlg.show();
			}

		}
		
	};
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
