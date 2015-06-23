package com.ddworlds.yzabs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.ddworlds.dialog.RequestDialog;
import com.ddworlds.dialog.ResultDialog;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.utils.MyGet;
import com.ddworlds.utils.Params;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ultralisk.gameapp.yjsg.yuzhuo.R;

public class NewChongZhiActivity extends Activity{
	private TextView cz_type, cz_money;
	//private String[] info1={"移动卡","联通卡","电信卡"};//卡类型
	private String[] info1={"中国移动","中国联通","中国电信"};//卡类型
	private int Cur_CzType=0;//当前卡类型 0移动1联通2电信
	private int Last_CzType=0;//上一个卡类型 0移动1联通2电信
	private RadioOnClick radioOnClick = new RadioOnClick(0);
	private ListView areaRadioListView;
	private String[][] info2={
			{"10元","20元","30元","50元","100元","200元","300元","500元","1000元" },//全国卡
			{"20元","30元","50元","100元","300元","500元"  },
			{"50元","100元" },
	};//面额
	private float[] all_price1={10,20, 30, 50,100, 200, 300, 500, 1000};//价格标准
	
	private int number;//第几个金额
	private String cur_select_money="10元";//当前选择的金额
	private ImageButton cz_pay;
	private EditText card1, card2;
	private Product pro;//产品对象
	
	private ImageView jt1, jt2;
	private ImageView czbackbtn;
	private TextView cztip1, cztip2;//应付金额提示 跟温馨提示
	private ImageButton yd_btn, lt_btn, dx_btn;
	
	
//	private String[][] param_order1;
	
	private String CZ_orderNo;//充值订单号
	private String[][] error_msg={
			{"-1", "第三方支付还未响应"},
			{"-2", "第三方支付请求异常"},
			{"0", "销卡成功，订单成功"},  
			{"1", "销卡成功，订单失败"},
			{"7", "卡号卡密或卡面额不符合规则"},
			{"1002", "本张卡密您提交过于频繁，请您稍后再试"},
			{"1003", "不支持的卡类型（比如电信地方卡）"},
			{"1004", "密码错误或充值卡无效"},
			{"1006", "充值卡无效"},
			{"1007", "卡内余额不足"},
			{"1008", "余额卡过期（有效期1个月）"},
			{"1010", "此卡正在处理中"},
			{"10000", "未知错误"},
			{"2005", "此卡已使用"},
			{"2006", "卡密在系统处理中"},
			{"2007", "该卡为假卡"},
			{"2008", "该卡种正在维护"},
			{"2009", "浙江省移动维护"},
			{"2010", "江苏省移动维护"},
			{"2011", "福建省移动维护"},
			{"2012", "辽宁省移动维护"},
			{"2013", "该卡已被锁定"},
			{"2014", "系统繁忙，请稍后再试"},
			{"3001", "卡不存在"},
			{"3002", "卡已使用过"},
			{"3003", "卡已作废"},  
			{"3004", "卡已冻结"}, 
			{"3005", "卡未激活"},
			{"3006", "密码不正确"},
			{"3007", "卡正在处理中"},
			{"3101", "系统错误"},
			{"3102", "卡已过期"}
		};//错误信息
	private String cur_error;//当前查询错误代码
	
			
	private String orderno_ZFB;//订单号
//	private String cz_type_txt=info1[0];
//	private String cz_moneny_txt=info2[0][0];
//	
	/**
	 * 动态设置scrollview的高度
	 * @param view
	 */
	private void setScrollViewHeight(View view) {
		System.out.println("ScrollViewWidth="+Params.widthpx+">>ScrollViewh="+Params.heightpx);
        int _n=0;
		if (Params.heightpx<=320) {
			_n=70+100;
        }else if(Params.heightpx>=700){
        	_n=70+100+50+50;
        }else{
        	_n=70+100+50;
        }
		int ScrollViewh = Params.heightpx-_n;    
        int ScrollViewWidth = Params.widthpx;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		ScrollViewWidth, ScrollViewh);
        view.setLayoutParams(params);

    }
	private ScrollView ScrollView1;
	
	private void changeBackgroud(int id, ImageButton v){
		if (Last_CzType!=id){
			if (Last_CzType==0){
				yd_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_yd1));
			}else if(Last_CzType==1){
				lt_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_lt1));
			}else if(Last_CzType==2){
				dx_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_dx1));
			}
		}
		if (id==0){
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_yd2));
		}else if(id==1){
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_lt2));
		}else if(id==2){
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.abs_bq_dx2));
		}
		Cur_CzType=id;
		Last_CzType=id;
		
	   Message msg= new Message();
       msg.what=R.id.cz_type;
       mhandler.sendMessage(msg);
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paycardlayout);
//		TextView _txtfocus=(TextView) findViewById(R.id.text_focus);
//		_txtfocus.requestFocus();
//		
//		ScrollView1=(ScrollView) findViewById(R.id.cz_scrollView1);
//		setScrollViewHeight(ScrollView1);
		yd_btn=(ImageButton) findViewById(R.id.yd_btn);
		lt_btn=(ImageButton) findViewById(R.id.lt_btn);
		dx_btn=(ImageButton) findViewById(R.id.dx_btn);
		changeBackgroud(Cur_CzType,yd_btn );
		yd_btn.setOnClickListener(l1);
		lt_btn.setOnClickListener(l1);
		dx_btn.setOnClickListener(l1); 
		
		
		
		bundle=getIntent().getExtras();
		orderno_ZFB= bundle.getString("orderno_ZFB");
		
		cz_type=(TextView) findViewById(R.id.cz_type);
		//cz_type.setOnClickListener(l1);
		
		cz_money=(TextView) findViewById(R.id.cz_money);
		//cz_money.setOnClickListener(l1);
		
		cz_pay=(ImageButton) findViewById(R.id.cz_pay);
		cz_pay.setOnClickListener(l1);
		
		card1=(EditText) findViewById(R.id.cz_number);
		card2=(EditText) findViewById(R.id.cz_pwd);
//		bankCardNumAddSpace(card1);
//		bankCardNumAddSpace(card2); 
		pro=AllObj.pro;
		
//		jt1=(ImageView) findViewById(R.id.jt1);
//		jt1.setOnClickListener(l1);
		
		jt2=(ImageView) findViewById(R.id.jt2);
		jt2.setOnClickListener(l1);
		
		czbackbtn=(ImageView) findViewById(R.id.czbtnback);
		czbackbtn.setOnClickListener(l1); 
		
		float _price1= pro.price;
		int cur_num1=0;//
		for (int i=0; i<all_price1.length; i++){
			if(all_price1[i]==_price1){
				cur_num1=i;
				break; 
			}
		}
		cur_select_money=info2[0][cur_num1];
		cz_money.setText(cur_select_money);
		System.out.println("activity cur_select_money="+cur_select_money);
		
		cztip1=(TextView) findViewById(R.id.cztip1);//应付金额提示
		String _txt1= "本次支付金额:"+pro.price+"元";//cztip1.getText().toString();
		System.out.println("_txt1.length="+_txt1.length());
		SpannableString s11 = new SpannableString(_txt1);
		int color1=getResources().getColor(R.color.yellow1);
		s11.setSpan(new ForegroundColorSpan(color1), 7, _txt1.length()-1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);	
		cztip1.setText(s11);
		
		
//		cztip2=(TextView) findViewById(R.id.warm_tip);//温馨提示
//		String _txt2= cztip2.getText().toString();
//		SpannableString s22 = new SpannableString(_txt2);
//		int color2=getResources().getColor(R.color.yellow1);
//		s22.setSpan(new ForegroundColorSpan(color2), 0, 5,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);	
//		cztip2.setText(s22);  
	}
	
	


	
	OnClickListener l1= new OnClickListener(){
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id= v.getId();
			if(id==R.id.czbtnback){
				finish();
			}else if(id==R.id.yd_btn){
				changeBackgroud(0, yd_btn);
			}else if(id==R.id.lt_btn){
				changeBackgroud(1, lt_btn);
			}else if(id==R.id.dx_btn){
				changeBackgroud(2, dx_btn);
			}else if(id==R.id.cz_type || id==R.id.jt1){
				AlertDialog ad =new AlertDialog.Builder(NewChongZhiActivity.this).setTitle("请选择卡类型")
			   .setSingleChoiceItems(info1,radioOnClick.getIndex(),radioOnClick).create();
//			   areaRadioListView=ad.getListView();
			   ad.show();
			}else if(id==R.id.cz_money || id==R.id.jt2){
				//int _index= radioOnClick.getIndex();
				int _index=Cur_CzType;
				new AlertDialog.Builder(NewChongZhiActivity.this).setTitle("请选择卡面额").setItems(info2[_index],new DialogInterface.OnClickListener(){
			      public void onClick(DialogInterface dialog, int which){
//			       int _index= radioOnClick.getIndex();
			       int _index=Cur_CzType;
			       number=which;
			       System.out.println("click number="+number);
			       cur_select_money=info2[_index][which];
			       System.out.println("cur_select_money="+cur_select_money);
			       if (Params.tip_debug){
			    	   Toast.makeText(NewChongZhiActivity.this, "您已经选择了: " + which + ":" + info2[_index][which],Toast.LENGTH_LONG).show();
			       }
			       
			       dialog.dismiss();
			       Message msg= new Message();
			       msg.what=R.id.cz_money;
			       mhandler.sendMessage(msg);
			      }
			   }).show();
			}else if(id==R.id.cz_pay){
				String card_num=(card1.getText().toString())==null?"":(card1.getText().toString());
				String card_pwd=(card2.getText().toString())==null?"":(card2.getText().toString());
				card_num=card_num.replaceAll(" ", "");
				card_pwd=card_pwd.replaceAll(" ", "");
				String result=juge_length(card_num, card_pwd);
				System.out.println("CZresult>>>>>>>>>="+result);
				String[] _table1=result.split("\\|");
				if(_table1[1].equals("1")){//可以支付
					if (Params.tip_debug){
						Toast.makeText(NewChongZhiActivity.this, "可以支付购买",Toast.LENGTH_LONG).show();
					}
					System.out.println("result before");
					RequestDialog.requestDialogShow(NewChongZhiActivity.this, "正在提交充值请求，请耐心等待...");

					try {
						new Thread() {
							public void run() {
								String _result=sendRequest();
								if(_result!=null){
									try {
										JSONObject jsonObject = new JSONObject(_result.toString());
										String result1=jsonObject.get("result").toString();
										if(result1.equals("0")){//请求成功
//											System.out.println(">>>>data="+jsonObject.get("data").toString());
//											JSONObject jsonObject1=new JSONObject(jsonObject.get("data").toString());
//											CZ_orderNo=(String) jsonObject1.get("orderno");
//											timer = new Timer(true);
//											timer.schedule(task,10000, 10000); //延时1000ms后执行，1000ms执行一次
//											Product pro=Activity3.getPro();
//											String ret="恭喜您成功购买"+pro.name+","+pro.des+"!";
											RequestDialog.waitDialog.dismiss();
											finish();
											Message msg= new Message();
									        msg.what=995;
									        mhandler.sendMessage(msg);
										}else{//请求失败
//											Toast.makeText(ChongZhiActivity.this, "请求发送失败！",Toast.LENGTH_LONG).show();
											RequestDialog.waitDialog.dismiss();
											finish();
//											String _s=String.valueOf(Activity3.payType)+"|"+Activity3.orderno_ZFB;
//											Activity1.AbsListener1.callBack(0,_s);//扣费失败
											Message msg= new Message();
									        msg.what=996;
									        mhandler.sendMessage(msg);
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}else{
									RequestDialog.waitDialog.dismiss();
									finish();
//									String _s=String.valueOf(Activity3.payType)+"|"+Activity3.orderno_ZFB;
//									Activity1.AbsListener1.callBack(0,_s);//扣费失败
									Message msg= new Message();
							        msg.what=996;
							        mhandler.sendMessage(msg);
								}
								System.out.println("result after");
							}
						}.start();
					} catch (Exception e) {
						e.printStackTrace();
						//PmxLog.log_e("Exception~~~~", e.getMessage());
					}
					
				}else{
					String[] _t1=result.split("\\|");
					Toast.makeText(NewChongZhiActivity.this, _t1[0],Toast.LENGTH_LONG).show();
				}
			}
			
			
		}
		
	};
	
	/**
	 * 查询订单状态
	 * @return
	 */
	private Timer timer;
	private int query_count=1;//查询次数
	private void queryOrder(){
//		String result;
		String _num=(Math.random()*10000)+"";
		String _url="http://game.yl000.com/Yeepay/GetOrderstatu?orderNo="+CZ_orderNo+"&app_type=ylchs&sign_type=1&num="+_num;		
		String _sign="e1666f081f6045bf59521d966bf54a06";
		String params="app_typeylchsnum"+_num+"orderNo"+CZ_orderNo+"sign_type1";
		String _sign1= md5(_sign+params);
		String url=_url+"&Sign="+_sign1;
		MyGet myget= new MyGet();
		String query_result=myget.GetResult(url); 
		
		System.out.println("query_result="+query_result);
		//result=0&desc=成功&delims=|;,&data=7
		String[] _t1=query_result.split("&");
		String _str1=_t1[0];
		
		if(query_result!=null && _str1.equals("result=0")){//订单正在查询
			String[] data1=_t1[3].split("=");
			System.out.println("data1="+data1[1]);
			if(data1[1].equals("0")){//订单成功
				timer.cancel();  
				query_count=1;
				RequestDialog.waitDialog.dismiss();
				finish();
				Message msg= new Message();
		        msg.what=997;
		        mhandler.sendMessage(msg); 
			}else if(data1[1].equals("-1")){//继续查询订单
				query_count=query_count+1;
//				timer = new Timer(true);
//				timer.schedule(task,5000, 5000); //延时1000ms后执行，1000ms执行一次
			}else{//所有的错误代码遍历
				timer.cancel();
				query_count=1;
				cur_error=data1[1];
				RequestDialog.waitDialog.dismiss();
				finish();
				Message msg= new Message();
		        msg.what=996;
		        mhandler.sendMessage(msg);
			}
			
//			MyDialog.dialog(Activity3.context, "充值失败!", 3, 0);
//			Toast.makeText(ChongZhiActivity.this, "充值支付失败!",Toast.LENGTH_LONG).show();
//			Activity1.AbsListener1.callBack(0);//扣费失败
			
		}else {//订单查询失败
			RequestDialog.waitDialog.dismiss();
			finish();
			Message msg= new Message();
	        msg.what=998;
	        mhandler.sendMessage(msg);
		}
		
//		return result;
	}
	
	
	TimerTask task = new TimerTask(){  
	       public void run() {  
	    	   queryOrder();
	    	   if (query_count==6){
	    		    timer.cancel(); //退出计时器
	    		    RequestDialog.waitDialog.dismiss();
		   			finish();
		   			Message msg= new Message();
		   	        msg.what=998;
		   	        mhandler.sendMessage(msg);
		   	        query_count=1;
	    	   }
	    	   
	    }  
	 };
	
	
	/**
	 * 订单发送支付请求
	 * @return
	 */
	private String sendRequest(){
		String result;
		//String url="http://weather.kk570.com/weather/GetWeatherInfo?city=820&signmsg=9d2bc1bc5cac3a3fa4c1388d50979401";					
		String url=getRequestUrl();
		MyGet myget= new MyGet();
		result=myget.GetResult(url); 
		return result;
	}
	
	private static String replace1(String str1){
		String s="";
		String s1=str1.substring(0, 1);
		String s3=str1.substring(1, str1.length());
		String s2=s1.toLowerCase();
		System.out.println("s1="+s2+s3);
		s=s2+s3;
		return s;
	}
	
	public String params_order(String[][] str){
		String params="";
		StringBuffer buf1=new StringBuffer();
		List list1= new  ArrayList();
		for (int i=0; i< str.length;i++){
			String str1=replace1(str[i][0]);
			User _user1= new User(str1, str[i][1]);
			list1.add(_user1);
		}
		 ComparatorUser comparator=new ComparatorUser();
		 Collections.sort(list1, comparator);
		 
		 for (int i=0;i<list1.size();i++){
		   User user_temp=(User)list1.get(i);
		   System.out.println(user_temp.getKey()+","+user_temp.getValue()); 
		   
		   if (user_temp.getKey().equals("amount")) {
			   buf1.append("Amount");
		   }else if(user_temp.getKey().equals("merchantExtentionalDescription")){
			   buf1.append("MerchantExtentionalDescription");
		   }else{
			   buf1.append(user_temp.getKey());
		   }
		   
		   buf1.append(user_temp.getValue());
		  }
		System.out.println(">buf1 string="+buf1.toString());
		params=buf1.toString();
		return params;
	}
	
	
	/**
	 * 获取充值的url
	 * @return
	 */
	private String getRequestUrl(){
		String url="";
		
		//int _index= radioOnClick.getIndex();
		int _index= Cur_CzType;
		String cardType;
		if(_index==0){
			cardType="3";
		}else if(_index==1){
			cardType="6";
		}else{
			cardType="12";
		}
		String cardNo=card1.getText().toString();
		String cardPassword=card2.getText().toString();
		cardNo=cardNo.replaceAll(" ", "");
		cardPassword=cardPassword.replaceAll(" ", "");
//		String cardAmount=info2[_index][number].replace("元", "");
		String cardAmount=cur_select_money.replace("元", "");
		String callbackurl="http://sync.ff505.com/Pay/YeePayNotify.aspx";
		
		String userid=pro.CpUserID;
		String username="DDZ";//pro.CpUserName;  
		username=username.replace(" ", "");     
		String[][] data={
				{"cardType",cardType},
				{"cardNo", cardNo},
				{"cardPassword", cardPassword},
				{"cardAmount", cardAmount},
				{"Amount", ""},
				{"productName", "ABS"},//ABS
				{"productType", "youle"},
				{"productDescription","YouleDescription"},
				{"MerchantExtentionalDescription",orderno_ZFB},//orderno_ZFB
				{"callBackUrl",callbackurl},
				{"sign_type","1"},
				{"app_type","ylchs"},
				{"format","json"},
				{"istest", "1"},
				{"userid", userid},
				{"username", username}
		};


		String url1="http://game.yl000.com/yeepay/CardPay?cardType="+cardType+"&cardNo="+cardNo+"&cardPassword="+cardPassword+"&cardAmount="+cardAmount+"&Amount=&productName=ABS&productType=youle&productDescription=YouleDescription&MerchantExtentionalDescription="+orderno_ZFB+"&callBackUrl="+callbackurl+"&sign_type=1&app_type=ylchs&format=json&istest=1&userid="+userid+"&username="+username;//+"&Sign=eced89da83d1d815c2da4cca46de191416ccac99";		
		String _sign="e1666f081f6045bf59521d966bf54a06";
		String params=params_order(data);
		System.out.println("_sign1="+(_sign+params));
		String _sign1= md5(_sign+params);
		System.out.println("_sign2="+_sign1);
		if(_sign1.length()>0){
			url=url1+"&Sign="+_sign1;
		}else{
			url="";
		}
//		url="http://game.yl000.com/yeepay/CardPay?cardType=3&cardNo=1234678905&cardPassword=12345678&cardAmount=30&Amount=&productName=ABS&productType=youle&productDescription=YouleDescription&MerchantExtentionalDescription=171&callBackUrl=http://sync.ff505.com/Pay/YeePayNotify.aspx&sign_type=1&app_type=ylchs&format=json&istest=1&userid=1866&username=MI 2&Sign=91364853F64E11229A8EB82E93DADED8";
		return url;
	}
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
        'A', 'B', 'C', 'D', 'E', 'F' };  
	public static String toHexString(byte[] b) {  
	    //String to  byte  
	    StringBuilder sb = new StringBuilder(b.length * 2);    
	    for (int i = 0; i < b.length; i++) {    
	        sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);    
	        sb.append(HEX_DIGITS[b[i] & 0x0f]);    
	    }    
	    return sb.toString();    
	}  
	
	public String md5(String s) {  
	    try {  
	        // Create MD5 Hash  
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");  
	        digest.update(s.getBytes());  
	        byte messageDigest[] = digest.digest();  
	                                  
	        return toHexString(messageDigest);  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	    }  
	                          
	    return "";  
	} 
	
	Handler mhandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int id= msg.what;
			if (id==R.id.cz_type){
//				String _text=info1[radioOnClick.getIndex()];
//				cz_type.setText(_text);
//				cur_select_money=info2[radioOnClick.getIndex()][0];
//				cz_money.setText(info2[radioOnClick.getIndex()][0]);
				
				String _text=info1[Cur_CzType]; 
				cz_type.setText(_text+"充值卡充值");
				cur_select_money=info2[Cur_CzType][0];
				cz_money.setText(info2[Cur_CzType][0]);
				System.out.println("mhandler cur_select_money="+cur_select_money);
			}else if (id==R.id.cz_money){
//				int _index=radioOnClick.getIndex();
				int _index=Cur_CzType;
				String _text=info2[_index][number];
				cz_money.setText(_text);
			}else if(id==999){
				RequestDialog.requestDialogShow(NewChongZhiActivity.this, "正在提交充值请求，请耐心等待...");
			}else if(id==998){
//				MyDialog.dialog1(Activity3.context, "充值失败!", 3, 0);
				
				String ret="抱歉您购买"+pro.name+"失败!";
				ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, ret, 3, 0);
				dlg.show();
			}else if(id==997){
//				MyDialog.dialog(Activity3.context, "恭喜您充值成功!", 3, 1);
				String ret="恭喜您成功购买"+pro.name+","+pro.des+"!";
				ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, ret, 3, 1);
				dlg.show();
			}else if(id==996){
				String _tip="抱歉您购买"+pro.name+"失败!";
				for (int i=0; i<error_msg.length; i++){
					String _tip1=error_msg[i][0];
					if(_tip1.equals(cur_error)){
						_tip=error_msg[i][1];
						break;
					}
				}
//				MyDialog.dialog1(Activity3.context, _tip, 3, 0);
				String ret=_tip;//"抱歉您购买"+pro.name+"失败!";
				ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, ret, 3, 0);
				dlg.show();    
			}else if(id==995){
				String ret="您的充值请求已提交，由于移动网关的问题不能立刻查到数据，稍后会给您发送充值成功的邮件，请注意查收！";
				ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, ret, 3, 1);
				dlg.show();
			}
		}  
		
	};
	
	
  /**
   * radio单击事件
   * @author ZCC
   *
   */
  class RadioOnClick implements DialogInterface.OnClickListener{
	  private int index;
	
	  public RadioOnClick(int index){
	   this.index = index;
	  }
	  public void setIndex(int index){
	   this.index=index;
	  }
	  public int getIndex(){
	   return index;
	  }
	
	  public void onClick(DialogInterface dialog, int whichButton){
	    setIndex(whichButton);
	    if (Params.tip_debug){
	    	Toast.makeText(NewChongZhiActivity.this, "您已经选择了： " + index + ":" + info1[index], Toast.LENGTH_LONG).show();
	    }
	    dialog.dismiss();
	    
	   Message msg= new Message();
       msg.what=R.id.cz_type;
       mhandler.sendMessage(msg);
	  }
  }
  
  private String juge_length(String card_num, String card_pwd){
	  System.out.println("card_num="+card_num+"card_pwd="+card_pwd);
	  String result="";
	  String des="";
	  String flag="0";//0时不能支付1匙可以支付
	  int _index= radioOnClick.getIndex(); 
	  System.out.println("_index="+_index+">>number="+number);
//	  String cardAmount=info2[_index][number].replace("元", "");
	  String cardAmount=cur_select_money.replace("元", "");
	  System.out.println("cardAmount="+cardAmount);
	  float _amount=pro.price;
	  float _curAmount=Float.parseFloat(cardAmount);
	  if(_curAmount<_amount){
		  des="您当前选择的卡面额小于支付金额,请重新选择卡面额";  
	  }else if(card_num.length()<1){   
		  des="请输入卡序列号";
	  }else if(card_pwd.length()<1){
		  des="请输入卡密码"; 
	  }else{
		  //int _type1=radioOnClick.getIndex();//0是移动卡1是联通卡2时电信卡
		  System.out.println("Cur_CzType="+Cur_CzType);
		  int _type1=Cur_CzType;
		  if(_type1==0){
			  if((card_num.length()==17 && card_pwd.length()==18)||(card_num.length()==10 && card_pwd.length()==8)||(card_num.length()==16 && card_pwd.length()==17)){
				  flag="1";
			  }else{
				  des="卡号或者卡密码长度有错误,请重新输入";
			  }
		  }else if(_type1==1){
			  if((card_num.length()==15 && card_pwd.length()==19)){
				  flag="1";
			  }else{
				  des="卡号或者卡密码长度有错误,请重新输入";
			  }
		  }else if(_type1==2){
			  if((card_num.length()==19 && card_pwd.length()==18)){
				  flag="1";
			  }else{
				  des="卡号或者卡密码长度有错误,请重新输入";
			  }
		  }
		  
	  }
	  result=des+"|"+flag;
	  return result;
  }
  
  
  class User {
		 String key;
		 String value;
		 
		 public User(String key,String value){
		  this.key=key;
		  this.value=value;
		 }

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		 
		}


	class ComparatorUser implements Comparator{

		 public int compare(Object arg0, Object arg1) {
		  User user0=(User)arg0;
		  User user1=(User)arg1;

		   //首先比较年龄，如果年龄相同，则比较名字

		  return user0.getKey().compareTo(user1.getKey());
		 }
		 
		}
	
	
	 /**  
     * 银行卡四位加空格  
     *   
     * @param mEditText  
     */  
    protected void bankCardNumAddSpace(final EditText mEditText) {  
        mEditText.addTextChangedListener(new TextWatcher() {  
            int beforeTextLength = 0;  
            int onTextLength = 0;  
            boolean isChanged = false;  
  
            int location = 0;// 记录光标的位置  
            private char[] tempChar;  
            private StringBuffer buffer = new StringBuffer();  
            int konggeNumberB = 0;  
  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
                beforeTextLength = s.length();  
                if (buffer.length() > 0) {  
                    buffer.delete(0, buffer.length());  
                }  
                konggeNumberB = 0;  
                for (int i = 0; i < s.length(); i++) {  
                    if (s.charAt(i) == ' ') {  
                        konggeNumberB++;  
                    }  
                }   
            }  
  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  
                    int count) {  
            	if (buffer.length()<=25){
            		onTextLength = s.length();  
                    buffer.append(s.toString());  
                    if (onTextLength == beforeTextLength || onTextLength <= 3  
                            || isChanged) {  
                        isChanged = false;  
                        return;  
                    }  
                    isChanged = true; 
            	}
                 
            }  
  
            @Override  
            public void afterTextChanged(Editable s) {  
                if (isChanged) {  
                    location = mEditText.getSelectionEnd();  
                    int index = 0;  
                    while (index < buffer.length()) {  
                        if (buffer.charAt(index) == ' ') {  
                            buffer.deleteCharAt(index);  
                        } else {  
                            index++;  
                        }  
                    }  
  
                    index = 0;  
                    int konggeNumberC = 0;  
                    while (index < buffer.length()) {  
                        if ((index == 4 || index == 9 || index == 14 || index == 19|| index == 23)) {  
                            buffer.insert(index, ' ');  
                            konggeNumberC++;  
                        }  
                        index++;  
                    }  
  
                    if (konggeNumberC > konggeNumberB) {  
                        location += (konggeNumberC - konggeNumberB);  
                    }  
  
                    tempChar = new char[buffer.length()];  
                    buffer.getChars(0, buffer.length(), tempChar, 0);  
                    String str = buffer.toString();  
                    if (location > str.length()) {  
                        location = str.length();  
                    } else if (location < 0) {  
                        location = 0;    
                    }  
  
                    mEditText.setText(str);  
                    Editable etable = mEditText.getText();
                    System.out.println("location="+location);
                    if(location>25) {
                    	location=25;
                    }
                    Selection.setSelection(etable, location);  
                    isChanged = false;  
                }  
            }  
        });  
    }  
	

}
