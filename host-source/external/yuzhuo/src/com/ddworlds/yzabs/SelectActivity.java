package com.ddworlds.yzabs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import @APP_PACKAGE@.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;






import com.ddworlds.alix.AlixId;
import com.ddworlds.alix.BaseHelper;
import com.ddworlds.alix.MobileSecurePayHelper;
import com.ddworlds.alix.MobileSecurePayer;
import com.ddworlds.alix.PartnerConfig;
import com.ddworlds.alix.ResultChecker;
import com.ddworlds.alix.Rsa;
import com.ddworlds.dialog.ResultDialog;
import com.ddworlds.obj.AllObj;
import com.ddworlds.obj.Product;
import com.ddworlds.utils.HttpUtil;
import com.ddworlds.utils.ImageAdapter;
import com.ddworlds.utils.MyGet;
import com.ddworlds.utils.Params;
import com.ddworlds.utils.XmlUtil;
import com.ddworlds.utils.XmlUtil.ParseException;
import com.tenpay.android.service.TenpayServiceHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class SelectActivity extends Activity {
	
	private Button[] buy_button=new Button[6];
	
	private TextView name, price, des;
	private static Product pro;
	public static String orderno_ZFB;//支付宝订单号
	
	private ProgressDialog mProgress = null;
	static String TAG = "SelectActivity";
	private GridView mGridView;
	private ImageButton btnBack;//返回按钮
	public static Context context;
	public static int payType;//支付方式
	public static final Product select_pro=AllObj.pro;
	
//	public static Product getPro(){
//		return pro;
//	}
	
	//所有图片
	private int[] all_image={R.drawable.an1,R.drawable.an2,
			R.drawable.an3,
			R.drawable.an4,R.drawable.an5,R.drawable.an6};
	private int[] img_id;//所有支付图片
	
	private String tokeId;//财富通订单号
	final static int MSG_PAY_RESULT1 = 100;//财付通返回调用
	final static int CFTDialog = 200;//财付通支付dialog
	final static int CFTDelay = 300;//财付通支付延迟
	private Timer timer;
	private int CFTResult;//财富同支付结果

	
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//初始化分辨率
		DisplayMetrics dm = new DisplayMetrics();   
		getWindowManager().getDefaultDisplay().getMetrics(dm); 
		Params p1= new Params(dm.widthPixels, dm.heightPixels);
		
		context=SelectActivity.this;
		bundle=getIntent().getExtras();
		String msg= bundle.getString("method");
		orderno_ZFB= bundle.getString("orderno_ZFB");
//		msg="1,2,3,4,5,6";
		String[] msg1=msg.split(",");
		img_id=new int[msg1.length];
		setContentView(R.layout.otherpay);
		
		
		pro=AllObj.pro;
		name=(TextView) findViewById(R.id.otherpay_name);
		price=(TextView) findViewById(R.id.otherpay_price);
		des=(TextView) findViewById(R.id.otherpay_des);
		if (pro==null){
			System.out.println("pro");  
			pro=select_pro;
//			finish();
//			AllObj.ABSListener1.callBack(2,"0");//取消扣费
//			return;
			if (pro==null){
				finish();
				return;
			}
		}
		
		System.out.println("pro1");  
		System.out.println(pro);
		String s1="商品名称:"+pro.name;
		SpannableString s11 = new SpannableString(s1);
		s11.setSpan(new ForegroundColorSpan(Color.BLACK), 5, s1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);	
		name.setText(s11);
		String s2="商品价格:"+(float)(pro.price)+"元";
		SpannableString s22 = new SpannableString(s2);
		int color1=getResources().getColor(R.color.yellow1);
		s22.setSpan(new ForegroundColorSpan(color1), 5, s2.length()-1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);	
		price.setText(s22);
		des.setText("商品描述:"+pro.des);   
		
		// 检测安全支付服务是否被安装
//		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
//		mspHelper.detectMobile_sp();
		
//		1 话费 2. 支付宝 3.财付通 4.银行卡 5充值卡 6游戏点卡
		mGridView=(GridView) findViewById(R.id.gridView1);
		int[] img= new int[msg1.length];
		for (int i=0; i<msg1.length;i++){
			int id=Integer.parseInt(msg1[i]);
			img[i]=all_image[id-1];
			img_id[i]=id;
		}
		int width=Params.widthpx;
		int img_w= (width-100)/3;  
		mGridView.setColumnWidth(img_w);
		mGridView.setAdapter(new ImageAdapter(this, img));
		int color2=getResources().getColor(R.color.hs1);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		mGridView.setBackgroundColor(color2);  
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
//				Toast.makeText(Activity3.this, "你选择了" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
				int _pos= img_id[position];
				pay1(_pos);
			}
		});
		
		btnBack= (ImageButton) findViewById(R.id.btnback);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				AllObj.ABSListener1.callBack(2,"0");//取消扣费
			}
			
		});
		
		
		
			
	}
	
	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 * @return
	 */
	String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);

		java.util.Random r = new java.util.Random();
		strKey = strKey + r.nextInt();
		strKey = strKey.substring(0, 15);
		return strKey;
	}
	
	String getOrderInfo() {
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		//strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		strOrderInfo += "out_trade_no=" + "\"" + orderno_ZFB + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + pro.name+ "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + pro.des + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\""
				+ (float)(pro.price) + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\""
				+ "http://sync.ff505.com/pay/alipaynotify.aspx" + "\"";	
//			http://notify.java.jpxx.org/index.jsp

		return strOrderInfo;
	}
	
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	String sign(String signType, String content) {
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}
	
	//
	// close the progress bar
	// 关闭进度框
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付宝支付
	 */
	private void ZFBclick(){
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;
		
		// start pay for this order.
				// 根据订单信息开始进行支付
				try {
					// prepare the order info.
					// 准备订单信息
					String orderInfo = getOrderInfo();
					// 这里根据签名方式对订单信息进行签名
					String signType = getSignType();
					String strsign = sign(signType, orderInfo);
					Log.v("sign:", strsign);
					// 对签名进行编码
					strsign = URLEncoder.encode(strsign);
					// 组装好参数
					String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
							+ getSignType();
					Log.v("orderInfo:", info);
					// start the pay.
					// 调用pay方法进行支付
					MobileSecurePayer msp = new MobileSecurePayer();
					boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, this);

					if (bRet) {
						// show the progress bar to indicate that we have started
						// paying.
						// 显示“正在支付”进度条
						closeProgress();
						mProgress = BaseHelper.showProgress(this, null, "正在请求支付，请稍候...", false,
								true);
					} else
						;
				} catch (Exception ex) {
					Toast.makeText(SelectActivity.this, R.string.remote_call_failed,
							Toast.LENGTH_SHORT).show();
				}
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
	
	
	/**
	 * 获得财付通订单号请求的url
	 * @return
	 */
	private String getCFTRequestUrl(){
		String url="";
//		float =pro.price;
		int _price1=(int) Math.ceil(pro.price*100);
		String _desc=pro.name;
		url="http://sync.ff505.com/Pay/TenpayRequest.aspx?OrderNo="+orderno_ZFB+"&Price="+(_price1)+"&Desc="+_desc+"&ABSSign=";
		String _sign1= md5(orderno_ZFB+"jadegreat1qaz21");
		url=url+_sign1;
		return url;
				
	}
	/**
	 * 请求财付通订单号
	 * @return
	 */
	private String sendRequest(){
		String result;
		//String url="http://weather.kk570.com/weather/GetWeatherInfo?city=820&signmsg=9d2bc1bc5cac3a3fa4c1388d50979401";					
		String url=getCFTRequestUrl();
		MyGet myget= new MyGet();
		result=myget.GetResult(url); 
		return result;  
	}
	
	private static  final int CFTType=330;
	private static String CFTRet;
	private static int CFTResult1;
	/**
	 * 处理财富通结果
	 * @param ret
	 * @param statusCode
	 * @param CFTResult
	 */
	public static void sendCFThandler(String ret, String statusCode, int result){
		if (statusCode!=null && statusCode.equals("66200003")==false ){
			Message msg= new Message();
			msg.what=CFTType;
			mHandler1.sendMessage(msg);
			CFTRet=ret;
			CFTResult1=result;
		}else{
			if (Params.tip_debug){
				Toast.makeText(context, "用户取消财富通支付", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	public static Handler mHandler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CFTType:
				ResultDialog dlg=new ResultDialog(context,R.style.PayDialog, CFTRet, 3, CFTResult1);
				dlg.show();
				break;
			}
		}
	};
	
	//判断财富通是否安装
		private String PKG_CFT="com.tenpay.android.service";
		private String APK_GAME_HALL = "TenpayService.apk";
		private  boolean isGameHallAPKExist() {
			PackageManager manager = SelectActivity.this.getPackageManager();
			List<PackageInfo> pkgList = manager.getInstalledPackages(0);
			for (int i = 0; i < pkgList.size(); i++) {
				PackageInfo pI = pkgList.get(i);
				if (pI.packageName.equalsIgnoreCase(PKG_CFT))
					return true;
			}

			return false;
		}
		private  void InstallGameHall(final Context context,
				final String cachePath) {
			chmod("777", cachePath);
			// install the apk.
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + cachePath),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		
		}
		public  void chmod(String permission, String path) {
			try {
				String command = "chmod " + permission + " " + path;
				Runtime runtime = Runtime.getRuntime();
				runtime.exec(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private  boolean retrieveApkFromAssets(Context context, String fileName,
				String path) {
			boolean bRet = false;

			try {
				InputStream is = context.getAssets().open(fileName);

				File file = new File(path);
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);

				byte[] temp = new byte[1024];
				int i = 0;
				while ((i = is.read(temp)) > 0) {
					fos.write(temp, 0, i);
				}

				fos.close();
				is.close();

				bRet = true;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return bRet;
		}
		
		public  void startGameHall(String token_id) {
			String mTokenId = token_id;
			if (mTokenId == null || mTokenId.length() <  32) {
				String _info="订单号为空或位数不对，请重新购买！";
				Toast.makeText(SelectActivity.this, _info, Toast.LENGTH_SHORT).show();
				return;     
			}
			tenpayHelper = new TenpayServiceHelper(SelectActivity.this);
			tenpayHelper.setLogEnabled(true); //打开log 方便debug, 发布时不需要打开。
			if (isGameHallAPKExist()) {
				// open it
//				PackageManager pm = SelectActivity.this.getPackageManager();
//				Intent intent = pm.getLaunchIntentForPackage(PKG_CFT);
//				SelectActivity.this.startActivity(intent);
				CFTActivity.context3=SelectActivity.this;
				Intent intent1= new Intent(SelectActivity.this, CFTActivity.class);
				intent1.putExtra("tokeid", tokeId);//财富同订单号
				startActivity(intent1);
				
			} else {
				// install it
				File cacheDir = SelectActivity.this.getCacheDir();
				final String cachePath = cacheDir.getAbsolutePath() + File.separator + APK_GAME_HALL;
				retrieveApkFromAssets(SelectActivity.this, APK_GAME_HALL, cachePath);
				InstallGameHall(SelectActivity.this, cachePath);
			}
		}
	
	/**
	 * 判断财富通插件是否安装
	 */
	public static TenpayServiceHelper tenpayHelper;
	private boolean pdCFTCJ(String token_id){
		String mTokenId = token_id;
		if (mTokenId == null || mTokenId.length() <  32) {
			String _info="订单号为空或位数不对，请重新购买！";
			Toast.makeText(SelectActivity.this, _info, Toast.LENGTH_SHORT).show();
			return false;     
		}
		tenpayHelper = new TenpayServiceHelper(SelectActivity.this);
		tenpayHelper.setLogEnabled(true); //打开log 方便debug, 发布时不需要打开。
		//判断并安装财付通安全支付服务应用  
		boolean _ret=tenpayHelper.isTenpayServiceInstalled(9);
		if (!_ret) {
			tenpayHelper.installTenpayService(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					Toast.makeText(SelectActivity.this, "用户取消了安装", Toast.LENGTH_LONG).show();
					
				}
			}, "/sdcard/test");  
			return false;
		}
		return true;
	}
	
	
	
	
	// the handler use to receive the pay result
		private  Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					switch (msg.what) {
					case CFTDelay:
						dismissProgressDialog();
//						CFTclick(tokeId);//tokeId
						boolean _cft= pdCFTCJ(tokeId);//检查才腹痛插件是否安装
						if (_cft){
							CFTActivity.context3=SelectActivity.this;
							Intent intent1= new Intent(SelectActivity.this, CFTActivity.class);
							intent1.putExtra("tokeid", tokeId);//财富同订单号
							startActivity(intent1);
						}
//						startGameHall(tokeId);  
						break;
					case MSG_PAY_RESULT1:
						//财富通
						System.out.println(">>>>>>>>>wwwwwMSG_PAY_RESULT");
						String strRet1 = (String)msg.obj; // 支付返回值  
						System.out.println(">>>>>>>>>strRet1="+strRet1);                 
						String statusCode = null;
						String info = "";
						String result = null;
						String ret="支付失败";
						if (strRet1!=null){
							JSONObject jo;
							try {
								jo = new JSONObject(strRet1);
								if (jo != null) {
									statusCode = jo.getString("statusCode");
									info = jo.getString("info");
									result  = jo.getString("result");								
								}	
							} catch (JSONException e1) {			
								e1.printStackTrace();
							}		
							
							ret = "statusCode = " + statusCode + ", info = " + info
										+ ", result = " + result;
                        }
						System.out.println(">>>>>>>>>ret="+ret);
						//按协议文档，解析并判断返回值，从而显示自定义的支付结果界面
						CFTResult=0;//1是计费成功 2是取消扣费0扣费失败
						if (statusCode!=null && statusCode.equals("0")){
							ret="恭喜您支付成功!";
							CFTResult=1;
						}else{
//							ret=info+",支付失败!";
							ret="抱歉您支付失败!";
						}
//						Activity3.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						if (statusCode!=null && statusCode.equals("66200003")==false ){
							/*new AlertDialog.Builder(Activity3.this)
							.setTitle("支付结果")
							.setMessage(ret)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if (CFTResult==1) {
										finish();
										dialog.dismiss();
										Activity1.AbsListener1.callBack(CFTResult);//扣费成功
									}else{
										dialog.dismiss();
									}
									
								}
							})
							.setCancelable(false).create().show();
							*/
							ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, ret, 3, CFTResult);
							dlg.show();
							
						}else{
							if (Params.tip_debug){
								Toast.makeText(SelectActivity.this, "用户取消财富通支付", Toast.LENGTH_SHORT).show();
							}
						}
						break;
					case AlixId.RQF_PAY: {
						//支付宝
						String strRet = (String) msg.obj;
						Log.e(TAG, strRet);	// strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
						
						closeProgress();

						BaseHelper.log(TAG, strRet);

						// 处理交易结果
						try {
							// 获取交易状态码，具体状态代码请参看文档
							String tradeStatus = "resultStatus={";
							int imemoStart = strRet.indexOf("resultStatus=");
							imemoStart += tradeStatus.length();
							tradeStatus=strRet.substring(imemoStart, imemoStart+4);
//							int imemoEnd = strRet.indexOf("};result=");
//							tradeStatus = strRet.substring(imemoStart, imemoEnd);//memo={操作已经取消。};resultStatus={6001};result={}

							
							//先验签通知
							ResultChecker resultChecker = new ResultChecker(strRet);
							int retVal = resultChecker.checkSign();
							// 验签失败
							if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
								/*BaseHelper.showDialog(
										Activity3.this,
										"提示",
										getResources().getString(
												R.string.check_sign_failed),
										android.R.drawable.ic_dialog_alert,0);
										*/
								ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, "抱歉您购买"+pro.name+"失败!", 3, 0);
								dlg.show();
							} else {// 验签成功。验签成功后再判断交易状态码
								if(tradeStatus.equals("9000")){//判断交易状态码，只有9000表示交易成功
//									BaseHelper.showDialog(Activity3.this, "提示","支付成功。交易状态码："+tradeStatus, R.drawable.infoicon,1);
//									BaseHelper.showDialog(Activity3.this, "提示","支付成功!", R.drawable.infoicon,1);
									
									ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, "恭喜您成功购买"+pro.name+","+pro.des+"!", 3, 1);
									dlg.show();
								}else{
									if(tradeStatus.equals("4000")){
										if (Params.tip_debug){
											Toast.makeText(SelectActivity.this, "用户取消支付宝支付", Toast.LENGTH_SHORT).show();
										}
									}else{
//										BaseHelper.showDialog(Activity3.this, "提示", "支付失败。交易状态码:"+ tradeStatus, R.drawable.infoicon,0);
//										BaseHelper.showDialog(Activity3.this, "提示", "抱歉，支付失败！", R.drawable.infoicon,0);
										ResultDialog dlg=new ResultDialog(SelectActivity.context,R.style.PayDialog, "抱歉您购买"+pro.name+"失败!", 3, 0);
										dlg.show();
										
									}
								}
									
							}

						} catch (Exception e) {
							e.printStackTrace();
							BaseHelper.showDialog(SelectActivity.this, "提示", strRet,
									R.drawable.infoicon,0);
						}
					}
						break;
					}

					super.handleMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	
	
		
	/**
	 * 财付通
	 */
	private void CFTclick(String token_id){
		//先得到财付通订单号
		String mTokenId = token_id;
		if (mTokenId == null || mTokenId.length() <  32) {
			String _info="订单号为空或位数不对，请重新购买！";
			Toast.makeText(SelectActivity.this, _info, Toast.LENGTH_SHORT).show();
			return;     
		}
		
		TenpayServiceHelper tenpayHelper = new TenpayServiceHelper(this);
		tenpayHelper.setLogEnabled(true); //打开log 方便debug, 发布时不需要打开。
		//判断并安装财付通安全支付服务应用
		if (!tenpayHelper.isTenpayServiceInstalled(9)) {
			tenpayHelper.installTenpayService(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					Toast.makeText(SelectActivity.this, "用户取消了安装，可以在这里做一些想做的逻辑！", Toast.LENGTH_LONG).show();
					
				}
			}, "/sdcard/test");  
			return;
		}	
		//构造支付参数
		HashMap<String, String> payInfo = new HashMap<String, String>();
		payInfo.put("token_id", mTokenId);         //财付通订单号token_id
		payInfo.put("bargainor_id", "1216130901"); //"1234567890"财付通合作商户ID	
		
		  
		//去支付
		boolean ret=tenpayHelper.pay(payInfo, mHandler, MSG_PAY_RESULT1);  
	}
		
	private void pay1(int id){
		if (Params.tip_debug){
			Toast.makeText(SelectActivity.this, "orderno_ZFB="+orderno_ZFB, Toast.LENGTH_SHORT).show();
		}
		if(id==1){  
			//话费支付
			payType=1;
			Toast.makeText(SelectActivity.this, "暂不支持话费支付", Toast.LENGTH_SHORT).show();
		}else if(id==2){
			//支付宝
			payType=2;
			ZFBclick();
		}else if(id==3){
			//财付通
//			Activity3.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
			payType=3;
			System.out.println("11111");
			showProgressDialog(R.string.loading);
			System.out.println("2222");
			new GetOrderTokenTask().execute(getCFTRequestUrl());
		}else if(id==4){
			//银行卡 
			payType=4;
			Toast.makeText(SelectActivity.this, "暂不支持银行卡", Toast.LENGTH_SHORT).show();
		}else if(id==5){
			//充值卡
//			Toast.makeText(Activity3.this, "暂不支持充值卡", Toast.LENGTH_SHORT).show();
			payType=5;
			czk_pay();
		}else if(id==6){
			//游戏点卡
			payType=6;
			Toast.makeText(SelectActivity.this, "暂不支持游戏点卡", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 充值卡支付
	 */
	private void czk_pay(){
		Intent intent1= new Intent(SelectActivity.this, ChongZhiActivity.class);
		intent1.putExtra("orderno_ZFB", orderno_ZFB);//支付宝订单号
		startActivity(intent1);
	}
	
	
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
	 
	 
	 
	 
	 private class GetOrderTokenTask extends AsyncTask<String, String, String >{	
			
			@Override
			protected String doInBackground(String... params) {	
				String token_id = null;
				String request = params[0];
				request=request.replaceAll(" ", "");
				System.out.println("request="+request);
				
				HttpUtil httpUtil = new HttpUtil(SelectActivity.this);
				Bundle data = httpUtil.doGet(request);
				
				if ( data.getInt(HttpUtil.HTTP_STATUS) == HttpStatus.SC_OK){
					byte[] ret = data.getByteArray(HttpUtil.HTTP_BODY);		
					if (ret != null){
						String recieved = new String(ret);
						Log.d("erik", "doInBackground, recieved = " + recieved);
						 
						try {
							HashMap<String ,String> dataMap = XmlUtil.parse(recieved);
							token_id = dataMap.get("token_id");
							Log.d("erik", "doInBackground, token_id = " + token_id);
						} catch (ParseException e) {						
							e.printStackTrace();
						}				
					}
				}
				
				return token_id;
			}		
			
			@Override
			protected void onProgressUpdate(String... progress) {
		    
		    }

			@Override
			protected void onPostExecute(String result) {		
				super.onPostExecute(result);
//				dismissProgressDialog();
				Log.d("erik", "onPostExecute, result = " + result);
				if (result != null){
					 Log.d("erik", "onPostExecute, result = " + result);
					 tokeId=result;
					 Message message = new Message();      
					 message.what = CFTDelay;      
					 mHandler.sendMessage(message);   
//					 timer = new Timer(true);
//					 timer.schedule(task,5000, 1000); //延时1000ms后执行，1000ms执行一次
					 if (Params.tip_debug){
						 Toast.makeText(SelectActivity.this, "成功生成订单，请点击支付按钮！", Toast.LENGTH_LONG).show(); 
					 }
						 
				} else {
					dismissProgressDialog();
					Toast.makeText(SelectActivity.this, "生成订单失败，请检查请求参数再重试吧！", Toast.LENGTH_LONG).show();
				}
			
			}
		}
	 
	 private ProgressDialog mReadingProgress;
	 public void showProgressDialog(int msgId) {
			if (mReadingProgress == null || !mReadingProgress.isShowing()) {
				mReadingProgress = new ProgressDialog(this);
				if (mReadingProgress != null) {
					mReadingProgress.setMessage(getString(msgId));
					mReadingProgress.setIndeterminate(true);
					mReadingProgress.setCancelable(false);
					mReadingProgress.show();
				}
			}
		}

		public void dismissProgressDialog() {
			if (mReadingProgress != null) {
				mReadingProgress.dismiss();
			}
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			// TODO Auto-generated method stub
			super.onConfigurationChanged(newConfig);
			String message=newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE ? "屏幕设置为：横屏" : "屏幕设置为：竖屏";
			if (Params.tip_debug){
				 Toast.makeText(SelectActivity.this, message, Toast.LENGTH_LONG).show(); 
			 }
		}
		
		
		/*TimerTask task = new TimerTask(){  
		       public void run() {  
		       Message message = new Message();      
		       message.what = CFTDelay;      
		       mHandler.sendMessage(message);   
		       timer.cancel(); //退出计时器
		    }  
		 };
		 */
	

}
