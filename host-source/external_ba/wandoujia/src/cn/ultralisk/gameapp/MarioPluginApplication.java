package cn.ultralisk.gameapp;
import android.app.Application;
import android.content.Context;

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;

/**
 * @author liuxv@wandoujia.com
 */
public class MarioPluginApplication extends Application {

  private static final long APP_KEY = 100007503;
  private static final String SECURITY_KEY = "62813e447d05a093353318815753c2ad";

  private static WandouGamesApi wandouGamesApi;

  public static WandouGamesApi getWandouGamesApi() {
    return wandouGamesApi;
  }


  @Override
  protected void attachBaseContext(Context base) {
    WandouGamesApi.initPlugin(base, APP_KEY, SECURITY_KEY);
    super.attachBaseContext(base);
  }


  @Override
  public void onCreate() {
    wandouGamesApi = new WandouGamesApi.Builder(this, APP_KEY, SECURITY_KEY).create();
    wandouGamesApi.setLogEnabled(true);
    super.onCreate();
  }
}
