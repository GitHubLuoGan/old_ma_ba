package com.ultralisk.pays.shenzhoufu;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import @APP_PACKAGE@.R;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.ultralisk.CallbackListener;
import com.ultralisk.Constants;
import com.ziplinegames.moai.MoaiBaseSdk;
import com.ziplinegames.moai.MoaiLog;
 
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ShenzhoufuNew extends Activity implements View.OnClickListener {

	private int mCardType;
	private String mNotifyUrl;
	private String mUserName;
	private String mExtInfo;
	private int mMoney;
	private CallbackListener mCallback;
	private String mPaymentUrl;
	private static final String SHENZHOUFU_PAY_URL_BASE = "http://pay3.shenzhoufu.com/interface/version3/serverconngc/entry.aspx";
	
	private String mOrderId; //for call back
	private boolean mIsPaying = false;
	private ProgressDialog mProgressDialog;
	
	public static List<payCodeConfig> pCodeList=null;
	private List<String> payTypeList = new ArrayList<String>();   
	private Spinner typeSpinner=null;
	public static Context sContext=null;
	 @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		 
		 super.onCreate(savedInstanceState);
		 sContext=this;
		 loadPayConfig();
		 
		 setContentView(R.layout.ul_payment_card_new);
		 
		 Intent intent = getIntent(); 
	        
		 mCallback	= (CallbackListener) intent.getSerializableExtra(Constants.PARAM_CALLBACK);
		 //mCardType  = intent.getIntExtra(Constants.CHARGE_CARD_PARAM_CARD_TYPE, Constants.CHARGE_CARD_CHINAMOBILE);
		 mMoney 	= intent.getIntExtra(Constants.PARAM_PRICE, 10);
		 mExtInfo 	= intent.getStringExtra(Constants.PARAM_EXT_INFO);
		 mNotifyUrl = intent.getStringExtra(Constants.PARAM_NOTIFY_URL);
		 mUserName	= intent.getStringExtra(Constants.PARAM_ROLE_NAME);
		 
		 findViewById(R.id.recharge_back_btn_new).setOnClickListener(this);
	     findViewById(R.id.recharge_btn_new).setOnClickListener(this);
	     
	     //TextView tbType = (TextView)findViewById(R.id.recharge_mode_text);
	     TextView tbMoney = (TextView)findViewById(R.id.recharge_price_text_new);
	    /*
	     String strType = "";
	     switch(mCardType)
	     {
	     	case Constants.CHARGE_CARD_CHINAMOBILE:
	     		strType = Constants.CARD_NAME_CHINAMOBILE;
	     		break;
	     	case Constants.CHARGE_CARD_CHINAUNICOM:
	     		strType = Constants.CARD_NAME_CHINAUNICOM;
	     		break;
	     	case Constants.CHARGE_CARD_CHINATELECOM:
	     		strType = Constants.CARD_NAME_CHINATELECOM;
	     		break;
	     }
	     tbType.setText(strType);
	     */
	     tbMoney.setText(""+mMoney+"元");
	     bindPayType();
	}
	
	 /*
	  * 绑定支付方式方
	  */
	 protected void bindPayType(){
		 if(pCodeList==null){
			 MoaiLog.e("pCodeList is error ");
			 return;
		 }
		 payCodeConfig bConfig;
		 for(int i=0;i<pCodeList.size();i++){
			 
         	  bConfig=pCodeList.get(i);
         	payTypeList.add(bConfig.payName); 
         }
		 payTypeList.add(0, "请选择充值卡类型");
		 //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				 android.R.layout.simple_spinner_item,payTypeList); 
		 
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
	        //第四步：将适配器添加到下拉列表上   
		  typeSpinner = (Spinner)findViewById(R.id.recharge_typs_spinner_new);
		 if(typeSpinner==null){			 
			 MoaiLog.e("支付方式下拉菜单没找到");
			 return;
		 }
		 typeSpinner.setAdapter(adapter); 
		// typeSpinner.getSelectedItemId();
		  
		//添加Spinner事件监听 

		 typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			 @Override
		 public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) { 
				 if(typeSpinner.getSelectedItemPosition()==0){
					 Toast.makeText(ShenzhoufuNew.sContext, "请先选择卡类型",Toast.LENGTH_SHORT).show();
						
					}
					
		 // TODO Auto-generated method stub  
				 if(typeSpinner.getSelectedItemPosition()>0){						
						if(!checkCardLimit()){
							Toast.makeText(ShenzhoufuNew.sContext, "对不起，不支持此面额的充值卡",Toast.LENGTH_SHORT).show();
				    		return;
						} 
					}else
					{
						Toast.makeText(ShenzhoufuNew.sContext, "请选择充值卡类型!",Toast.LENGTH_SHORT).show(); 	
						return;
					}
					
		 }

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// TODO Auto-generated method stub
				
			}  
		 });

	
	 }
	  
	
	 /*
	  * 加载充值配置项
	  */
	 protected void loadPayConfig(){
		 if(pCodeList==null){
			 MoaiBaseSdk.loadChannelConfig();			 
		 }		 
		 if(MoaiBaseSdk.sConfigJsonObject==null){
			 MoaiLog.e("找不到支付配置文件");
			 return;			 
		 };
	        
	        //读取计费点
	        JsonValue jsonVal=MoaiBaseSdk.sConfigJsonObject.get("payCodes");
	        if(jsonVal==null) {
	        	 MoaiLog.e("找不到支付配置支点 payCodes");
				 return;	
	        	
	        }
	        JsonArray payMap=jsonVal.asArray();
	        if(payMap!=null){
	            pCodeList=new ArrayList<payCodeConfig>();         
	            JsonObject mapJson=null;
	            //String payStr="";
	            String[] payListStr;
	           
	            for(int i=0;i<payMap.size();i++){
	            	payCodeConfig bConfig=new payCodeConfig();
	                mapJson=payMap.get(i).asObject(); 
	                bConfig.payIndex=MoaiBaseSdk.GetJsonValInt(mapJson,"payIndex",0);
	                bConfig.payName=MoaiBaseSdk.GetJsonVal(mapJson,"payName","0");
	                 
	                payListStr=MoaiBaseSdk.GetJsonVal(mapJson,"payPrices","0").split(",");
	               
	                if(payListStr.length>0){
	                	bConfig.payPrices=new int[payListStr.length];
	                    for(int b=0;b<payListStr.length;b++){	                    	
	                    	bConfig.payPrices[b]= Integer.parseInt(payListStr[b]);
	                    } 
	                } 
	                bConfig.kardRule=MoaiBaseSdk.GetJsonVal(mapJson,"kardRule","0");
	                bConfig.pwdRule=MoaiBaseSdk.GetJsonVal(mapJson,"pwdRule","0");
	                pCodeList.add(bConfig);
	            } 
	        }
		 
	 }
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
	        case R.id.recharge_back_btn_new:
	            finish();
	            break;
	        case R.id.recharge_btn_new:
	        	onClickChargeBtn();
	            break;
		}
	}
	private String urlEncodeUTF8(String str)
	{
		String res = str;
		try {
			res = java.net.URLEncoder.encode(res, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	private String getOrderId(String merId)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
		Date date = new Date();
		String strTime = format.format(date);
		String strOrder =  strTime + "-" + merId;
		
		String uuid = UUID.randomUUID ().toString ();
		if(uuid.length() > 20)
		{
			uuid = uuid.substring(0, 20);
		}
		uuid = uuid.replace("-", "");
		strOrder += "-" + uuid;
		
		return strOrder;
	}
	private String getCardInfo(int cardTypeCombine,String cardNo, String cardPw)
	{
		String cardMoney = "" + mMoney;
		String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(cardTypeCombine,cardMoney, cardNo, cardPw, Config.MERCHANT_DES_KEY);
		return cardInfo;
	}
	
	
	
	private String makePaymentUrl(String cardNo, String cardPw)
	{
		String version 		= "3";
		String merId 		= Config.MERCHANT_ID;
		int    payMoney 	= mMoney * 100;
		String orderId		= getOrderId(merId);
		String returnUrl	= mNotifyUrl;
		int cardTypeCombine = mCardType;
		String cardInfo		= getCardInfo(cardTypeCombine,cardNo, cardPw);
		String merUserName	= urlEncodeUTF8(mUserName);
		String merUserMail	= "unkownmail";
		String privateField = mExtInfo;
		String verifyType 	= "1";
	
		String md5String 	= MD5Util.MD5(version +merId+ payMoney + orderId + returnUrl + cardInfo 
								+ privateField + verifyType + Config.MERCHANT_PRIVATE_KEY);
		
		
		String payUrl = SHENZHOUFU_PAY_URL_BASE +"?";
		String requestCode = "";
		requestCode = requestCode + "version=" + version;
		requestCode = requestCode + "&merId=" + merId;
		requestCode = requestCode + "&payMoney=" + payMoney;
		requestCode = requestCode + "&orderId=" + orderId;
		requestCode = requestCode + "&returnUrl=" + returnUrl;
		requestCode = requestCode + "&cardInfo=" + urlEncodeUTF8(cardInfo);
		requestCode = requestCode + "&merUserName=" + merUserName;
		requestCode = requestCode + "&merUserMail=" + merUserMail;
		requestCode = requestCode + "&privateField=" + privateField;
		requestCode = requestCode + "&verifyType=" + verifyType; 
		//requestCode = requestCode + "&cardTypeCombine=" + cardTypeCombine;//卡类不用这里
		requestCode = requestCode + "&md5String=" + md5String;
		
//		try {
//			requestCode = URLEncoder.encode(requestCode, "UTF-8");
//		} catch (UnsupportedEncodingException e) 
//		{
//			e.printStackTrace();
//		}
		
		payUrl = payUrl + requestCode;
		
		mOrderId = orderId;
		
		return payUrl;
	}
	private void startPay(String cardNo, String cardPw)
	{
		if(mIsPaying)
		{
			Toast.makeText(this, "正在支付，请不要重复提交。",Toast.LENGTH_SHORT).show();
			return;
		}
		mIsPaying = true;
		showProgress("正在支付...");
		mPaymentUrl = makePaymentUrl(cardNo, cardPw);
		new Thread(new Runnable() {
			public void run() {
				try {  
		            HttpRequester request = new HttpRequester();  
		            HttpRespons hr = request.sendGet(mPaymentUrl);
		            Message msg = new Message();
					msg.obj = hr.getContent();
					mHandler.sendMessage(msg);
					mIsPaying = false;
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        
			}
		}).start();
	}
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String retStr = (String)msg.obj;
			hideProgress();
			CheckPayResult(retStr);
		}

	};
	private void CheckPayResult(String content)
	{
		int    code = -1;
		String msg = "";
		
		content = content.trim();
		try
		{
			code = Integer.parseInt(content);
		}catch(NumberFormatException e){}
		
		switch(code)
		{
			case 101:  
				msg = "md5 验证失败";
				break;
			case 105:  
				msg = "重复提交点卡，此点卡正在处理中";
				break; 
			case 106:  
				msg = "系统繁忙，请重新发起新订单";
				break;
			case 109:  
				msg = "des 解密失败";
				break;
		 
			case 200:  
				msg = "请求通过，神州付收单并准备处理";
				break; 
			case 902:  
				msg = "商户参数不全";
				break; 
			case 903:  
				msg = "商户 ID 不存在";
				break; 
			case 904:  
				msg = "商户没有激活";
				break; 
			case 905:  
				msg = "商户没有使用该接口的权限";
				break; 
			case 906:  
				msg = "商户没有设置  密钥（privateKey）";
				break; 
			case 907: 
				msg = "商户没有设置  DES 密钥 ";
				break; 
			case 908:  
				msg = "该笔订单已经处理完成 （订单状态已经为确定的状态： 成功  或 者  失败）";
				break; 
			case 910:  
				msg = "服务器返回地址，不符合规范";
				break; 
			case 911:  
				msg = "订单号，不符合规范";
				break; 
			case 912:  
				msg = "非法订单";
				break; 
			case 915:  
				msg = "点卡信息有误或卡号密码无效";
				break; 
			
			case 917:  
				msg = "参数格式不正确";
				break; 
			case 0:  
				msg = "网络连接失败";
				break; 
			default:
				msg = "未知错误";
				break;
		}
		
		if (code == 200)
		{
			Toast.makeText(this, "支付订单提交成功！",Toast.LENGTH_SHORT).show();
			mCallback.onPaymentSuccess(mOrderId);
		}
		else
		{
			Toast.makeText(this, "支付失败，" + msg,Toast.LENGTH_SHORT).show();
			mCallback.onPaymentError(msg, mOrderId);
		}
	}
	private void onClickChargeBtn()
	{
		/*if(!checkCardLimit()){
			Toast.makeText(this, "对不起，不支持此面额的充值卡",Toast.LENGTH_SHORT).show();
    		return;
		}*/
		if(typeSpinner.getSelectedItemPosition()>0){						
			if(!checkCardLimit()){
				Toast.makeText(ShenzhoufuNew.sContext, "对不起，不支持此面额的充值卡",Toast.LENGTH_SHORT).show();
	    		return;
			} 
		}else
		{
			Toast.makeText(ShenzhoufuNew.sContext, "请选择充值卡类型!",Toast.LENGTH_SHORT).show(); 	
			return;
		}
	
		EditText tbCardNo = (EditText)findViewById(R.id.recharge_number_edit_new);
		EditText tbCardPw = (EditText)findViewById(R.id.recharge_password_edit_new);

	    String cardNo = tbCardNo.getText().toString();
	    String cardPw = tbCardPw.getText().toString();
	    
	    if (cardNo.equals("") || cardPw.equals(""))
	    {
	    	Toast.makeText(this, "充值卡号或密码不能为空",Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
	    startPay(cardNo, cardPw);
	    
	}
	/*
	 * 验证冲值卡面值	
	 */
	private boolean checkCardLimit(){
		
		
		if(typeSpinner==null||pCodeList==null){	
			Toast.makeText(this, "充值方式为空或充值配置文件没有加载成功!",Toast.LENGTH_SHORT).show();
			MoaiLog.e("充值方式为空或充值配置文件没有加载成功!");
			return false;
		}
		
		
		payCodeConfig pConfig=pCodeList.get(typeSpinner.getSelectedItemPosition()-1);
		if(pConfig==null){			
			Toast.makeText(this, "找不到当前支付方式"+typeSpinner.getSelectedItem().toString()+"支持的配置文件!",Toast.LENGTH_SHORT).show();
			MoaiLog.e("找不到当前支付方式"+typeSpinner.getSelectedItem().toString()+"支持的配置文件! typeSpinner.getSelectedItemPosition()::"+typeSpinner.getSelectedItemPosition());
			return false;
		}
		int priceInt =mMoney; 
		mCardType=pConfig.payIndex;
    	for (int price:pConfig.payPrices)
    	{
    		if(price == priceInt)
    		{
    			return true;
    		}
    	}
    	
    	return false; 
		// mMoney
	}
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