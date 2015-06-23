package com.ziplinegames.moai;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.security.auth.callback.Callback;


import cn.ultralisk.gameapp.r02.R;

import com.chinaMobile.MobileAgent;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
 
 
public class mmsmsVali  extends Activity implements View.OnClickListener {

  public static String tradeID="";
  public static TextView statusText;
  private ProgressDialog mProgressDialog;
  public static boolean isTryLuck=false;
  public static View valiSubmit;
  @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		 
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.mmsms_vali);
		 Intent intent = getIntent();
		 tradeID = intent.getStringExtra("tradeID");		 
		 findViewById(R.id.submit_Button).setOnClickListener(this); 
		 valiSubmit=findViewById(R.id.submit_Button);
		 findViewById(R.id.mmsmsvali_back_btn).setOnClickListener(this); 
	     statusText = (TextView)findViewById(R.id.status_text); 
	     setValiMsg();
	}
	   @Override
	public void onResume(){
		   MobileAgent.onResume(this); 
	}
	  @Override
   public void onPause(){		 
	   MobileAgent.onPause(this); 
	}

  
	public   String getMMsmsValiMsg(String tradeID){
		
        
		int resultCode=0;
		String result = "订购结果：未知";
		String mmsmsValiUrl=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject, "mmsmsValiUrl", "http://mmsms.ultralisk.cn/api/mmquery.php");
		String appid=MoaiBaseSdk.GetJsonVal(MoaiBaseSdk.sConfigJsonObject,"appid","0");
		 
		mmsmsValiUrl=mmsmsValiUrl+"?appid="+appid+"&tradeid="+tradeID;
		String resultStr="";
		try{
			resultStr=Moaimmsms.sendGet(mmsmsValiUrl);
		}
		catch(Exception ex){
			resultStr="";
		}
		if(resultStr.equals("0")){
			tradeID="";
			resultCode=1;
			result="购买成功"; 
			 //该部分是传参并更新控件  
            Message msg = new Message();  
            msg.what = 1;     
            //发送消息到Handler  
            handler.sendMessage(msg);  
			
			
		}
		else if(resultStr.equals("-1")){					
			resultCode=0;
			result="验证数据时发生严重错误，请稍候重试!";
		}
		else if(resultStr.equals("-2")){					
			resultCode=0;
			result="订购信息不存在，请稍候重试!";
		}
		else if(resultStr.equals("-3")){					
			resultCode=0;
			result="订单已失败，可能是操作太频繁或卡上余额不足!";
			 //该部分是传参并更新控件  
            Message msg = new Message();  
            msg.what = 1;     
            //发送消息到Handler  
            handler.sendMessage(msg);  
		}
		else if(resultStr.equals("2")){					
			resultCode=0;
			result="订购信息不存在，请稍候重试!";
		}
		else if(resultStr.equals("11")){					
			resultCode=0;
			result="没有授权调用该能力";
		}
		else if(resultStr.equals("12")){					
			resultCode=0;
			result="服务忙，请稍候重试!";
		}
		else if( resultStr.equals("13")){					
			resultCode=0;
			result="未知错误，请稍候重试!";
		}
		 
		else if(resultStr.equals("17")){					
			resultCode=0;
			result="数字签名不正确!";
		}
		else if(resultStr.equals("18")){					
			resultCode=0;
			result="请求能力不存在，请稍候重试!";
		}
		else if(resultStr.equals("19")){					
			resultCode=0;
			result="应用不存在!";
			  
		}
		else if(resultStr==""){					
			resultCode=0;
			result="获取验证信息失败";
		}
		else
		{
			resultCode=0;
			result="购买失败";			
		} 
		
		JsonObject jsonParms=new JsonObject();
		jsonParms.add("code", resultCode);
		jsonParms.add("msg", result);
		jsonParms.add("payData",Moaimmsms.orderParms.asObject());
		//计费成功
		MoaiBaseSdk.JsonRpcCall(MoaiBaseSdk.Lua_Cmd_PayResult,jsonParms);
		return result;
	}
	
	public void setValiMsg(){	
		showProgress("正在验证充值状态,请稍候！");
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			hideProgress();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//毫秒   
		new Thread(new Runnable() {
			public void run() {
				String reMsg=getMMsmsValiMsg(tradeID);
			 
				 //该部分是传参并更新控件  
                Message msg = new Message();  
                msg.what = 0;  
                Bundle bundle = new Bundle();  
                bundle.putString("msg",reMsg);  
                msg.setData(bundle);  
                //发送消息到Handler  
                handler.sendMessage(msg);  
                
				hideProgress();
				isTryLuck=false;
			}
		}).start();
		//Toast.makeText(this, "正在验证充值状态！", Toast.LENGTH_SHORT).show();
		
	
		//Toast.makeText(this, "充值状态验证完成！", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
	        case R.id.mmsmsvali_back_btn:
	            finish();
	            break;
	        case R.id.submit_Button:
	        	if(!isTryLuck){
	        	setValiMsg();
	        	isTryLuck=true;
	        	}else{
	        	Toast.makeText(this, "有一个请求正在处理，请稍候！", Toast.LENGTH_SHORT).show();
	        	}
	            break;
		}
	}
	
	public Handler handler = new Handler()
	{ 
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				{
					statusText.setText(msg.getData().getString("msg")); 
				}
				break;
			case 1:
				valiSubmit.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	
	protected void showProgress(String tips)
	{
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(tips);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	protected void hideProgress()
	{ 
		if(mProgressDialog != null)
		{
			mProgressDialog.cancel();
			mProgressDialog = null;
		} 
	}
	
	
	
}