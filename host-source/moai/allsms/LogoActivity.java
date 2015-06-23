package com.ziplinegames.moai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import @APP_PACKAGE@.R;

public class LogoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);

		
		
		MoaiLog.e("运营商信息::"+MoaiTool.getProvidersName(this));
		if(MoaiTool.cardType>MoaiTool.CardType_NO){
			MoaiBaseSdk.configFileName=String.format("cConfig_%d.json", MoaiTool.cardType);
			if(MoaiTool.cardType==MoaiTool.CardType_YD){//移动
				//logoImage.setImageResource(R.drawable.logo_1);
				onLoding_YD(savedInstanceState);
			}
			else if(MoaiTool.cardType==MoaiTool.CardType_LT){//联通
				onLoding_LT(savedInstanceState);
				 
			}
			else if(MoaiTool.cardType==MoaiTool.CardType_DX){//电信
				onLoding_DX(savedInstanceState);
			}else
			{
				Intent intent = new Intent();
				intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
				startActivity(intent);
				finish();
				
			}
			 
		}else{
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	
void onLoding_YD(Bundle savedInstanceState){

		// 取消标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo);
		ImageView logoImage = (ImageView) this.findViewById(R.id.logo); 
		logoImage.setImageResource(R.drawable.logo_1);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(3000);
		logoImage.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				  try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
				Intent intent = new Intent();
				intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
void onLoding_LT(Bundle savedInstanceState){
	
	// 取消标题
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// 取消状态栏
	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	//setContentView(R.layout.logo);
	Intent intent = new Intent();
	intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
	startActivity(intent);
	finish();
	 
}

void onLoding_DX(Bundle savedInstanceState){

	// 取消标题
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// 取消状态栏
	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.logo);
	ImageView logoImage = (ImageView) this.findViewById(R.id.logo); 
	logoImage.setImageResource(R.drawable.logo_3);
	AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
	alphaAnimation.setDuration(3000);
	logoImage.startAnimation(alphaAnimation);
	alphaAnimation.setAnimationListener(new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			  try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, cn.ultralisk.gameapp.MoaiActivity.class);
			startActivity(intent);
			finish();
		}
	});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.logo, menu);
		return true;
	}

}
