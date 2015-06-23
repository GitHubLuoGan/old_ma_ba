package cn.ultralisk.gameapp;

import android.content.Intent;
import android.graphics.Color;

import cn.ultralisk.gameapp.MoaiActivity;

import com.snowfish.cn.ganga.helper.SFOnlineSplashActivity;

public class SplashActivity extends SFOnlineSplashActivity {
    public int getBackgroundColor() {
        // 返回闪屏的背景颜色
            return Color.WHITE;
            }
        @Override
        public void onSplashStop() {
        // 闪屏结束进入游戏
                Intent intent = new Intent(this, MoaiActivity.class);
                startActivity(intent);
                this.finish();
            }
    }