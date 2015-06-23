package cn.ultralisk.gameapp;

import android.app.Application;

/**
 * Created by afwang on 13-9-17.
 */
public class cmgcApplication extends Application {
	public void onCreate() {
		System.loadLibrary("megjb");
	}

}
