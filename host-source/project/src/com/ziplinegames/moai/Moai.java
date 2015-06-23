//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;
import java.lang.Runtime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.UUID;
import cn.ultralisk.gameapp.*;

//================================================================//
// Moai
//================================================================//
public class Moai {

	public enum ApplicationState {
	
        APPLICATION_UNINITIALIZED,
        APPLICATION_RUNNING,
        APPLICATION_PAUSED;

        public static ApplicationState valueOf ( int index ) {
	
            ApplicationState [] values = ApplicationState.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return APPLICATION_UNINITIALIZED;
            }

            return values [ index ];
        }
    }

	public enum DialogResult {
	
		RESULT_POSITIVE,
		RESULT_NEUTRAL,
		RESULT_NEGATIVE,
		RESULT_CANCEL;

        public static DialogResult valueOf ( int index ) {
	
            DialogResult [] values = DialogResult.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return RESULT_CANCEL;
            }

            return values [ index ];
        }
    }

	public enum ConnectionType {
	
		CONNECTION_NONE,
		CONNECTION_WIFI,
		CONNECTION_WWAN;

        public static ConnectionType valueOf ( int index ) {
	
            ConnectionType [] values = ConnectionType.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return CONNECTION_NONE;
            }

            return values [ index ];
        }
    }

	public enum InputDevice {
	
		INPUT_DEVICE;

        public static InputDevice valueOf ( int index ) {
	
            InputDevice [] values = InputDevice.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return INPUT_DEVICE;
            }

            return values [ index ];
        }
    }

	public enum InputSensor {
	
		SENSOR_COMPASS,
		SENSOR_LEVEL,
		SENSOR_LOCATION,
		SENSOR_TOUCH,
		SENSOR_OS;
		
        public static InputSensor valueOf ( int index ) {
	
            InputSensor [] values = InputSensor.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return SENSOR_TOUCH;
            }

            return values [ index ];
        }
    }
	// CDSC_ENABLE_OS_EVENT
	public enum OsEvent {
		OS_EVENT_START,
		OS_EVENT_PAUSE,
		OS_EVENT_RESUME,
		OS_EVENT_STOP,
		OS_EVENT_RESIZE,
	}
	private static String [] sExternalClasses = {
		"com.ziplinegames.moai.MoaiAdColony",
		"com.ziplinegames.moai.MoaiAmazonBilling",
		"com.ziplinegames.moai.MoaiChartBoost",
		"com.ziplinegames.moai.MoaiCrittercism",
		"com.ziplinegames.moai.MoaiFacebook",
		"com.ziplinegames.moai.MoaiKeyboard",
		"com.ziplinegames.moai.MoaiGoogleBilling",
		"com.ziplinegames.moai.MoaiGooglePush",
		"com.ziplinegames.moai.MoaiTapjoy",
		"com.ziplinegames.moai.MoaiAndroidDeviceInfo",
	    //CDSC start
		"com.ziplinegames.moai.MoaiFmod",
		"com.ziplinegames.moai.MoaiDownjoy",
		"com.ziplinegames.moai.MoaiUc",
		"com.ziplinegames.moai.MoaiNd91",
        "com.ziplinegames.moai.MoaiQihoo360",
        "com.ziplinegames.moai.MoaiXiaomi", 
        "com.ziplinegames.moai.MoaiCmgc",
        "com.ziplinegames.moai.MoaiUltralisk",
		"com.ziplinegames.moai.MoaiJPush",
		"com.ziplinegames.moai.MoaiTalkingdata",
        "com.ziplinegames.moai.MoaiXianguo",
		"com.ziplinegames.moai.MoaiMM",
		"com.ziplinegames.moai.MoaiSijia",
		"com.ziplinegames.moai.MoaiCommonSdk",
		"com.ziplinegames.moai.MoaiTencent",
		"com.ziplinegames.moai.MoaiBaseSdk", 
		"com.ziplinegames.moai.MoaiShareSDK", 
		"com.ziplinegames.moai.Moaidataeye", 
	    //CDSC end
	};
	
	private static Activity 				sActivity = null;
	private static ApplicationState 		sApplicationState = ApplicationState.APPLICATION_UNINITIALIZED;
	private static ArrayList < Class < ? >>	sAvailableClasses = new ArrayList < Class < ? >> ();
		
	public static final Object		sAkuLock = new Object ();

	protected static native boolean	AKUAppBackButtonPressed			();
	protected static native void 	AKUAppDialogDismissed			( int dialogResult );
	protected static native void 	AKUAppDidStartSession			( boolean resumed );
	protected static native void 	AKUAppWillEndSession 			();
	//CDSC_ENABLE_JSON_RPC 
	public static native String AKUJsonRpcCall(String parmsInJson);
	//CDSC_ENABLE_JSON_RPC
	protected static native int	 	AKUCreateContext 				();
	//CDSC add (for interrupt bug) start
	protected static native void 	AKUReleaseGfxContext 			();
	//CDSC add (for interrupt bug) end
	protected static native void 	AKUDetectGfxContext 			();
	protected static native void 	AKUEnqueueLevelEvent 			( int deviceId, int sensorId, float x, float y, float z );
	protected static native void 	AKUEnqueueLocationEvent			( int deviceId, int sensorId, double longitude, double latitude, double altitude, float hAccuracy, float vAccuracy, float speed );
	protected static native void 	AKUEnqueueCompassEvent			( int deviceId, int sensorId, float heading );
	protected static native void 	AKUEnqueueTouchEvent 			( int deviceId, int sensorId, int touchId, boolean down, int x, int y, int tapCount );
	//CDSC_ENABLE_OS_EVENT
	protected static native void AKUEnqueueOsEvent					(int deviceId, int sensorId, int eventid);
	protected static native void 	AKUExtLoadLuacrypto				();
	protected static native void 	AKUExtLoadLuacurl				();
	protected static native void 	AKUExtLoadLuasocket				();
	protected static native void 	AKUExtLoadLuasql				();
	//CDSC_TAG_LUA_PACK
	protected static native void 	AKUExtLoadLuapack				();
	//CDSC_TAG_LUA_PACK END
	//CDSC_TAG_CDSC_USE_LSQLITE3
	protected static native void 	AKUExtLoadLSqllite				();
	//CDSC_TAG_CDSC_USE_LSQLITE3 END

	protected static native void 	AKUFinalize 					();
	protected static native void 	AKUFMODExInit		 			();
	protected static native void 	AKUInit 						();
	protected static native void 	AKUMountVirtualDirectory 		( String virtualPath, String archive );
	protected static native void 	AKUPause 						( boolean paused );
	protected static native void 	AKURender	 					();
	protected static native void 	AKUReserveInputDevices			( int total );
	protected static native void 	AKUReserveInputDeviceSensors	( int deviceId, int total );
	protected static native void	AKURunScript 					( String filename );
	protected static native void	AKUSetConnectionType 			( long connectionType );
	protected static native void 	AKUSetContext 					( int contextId );
	protected static native void 	AKUSetDeviceProperties 			( String appName, String appId, String appVersion, String abi, String devBrand, String devName, String devManufacturer, String devModel, String devProduct, int numProcessors, String osBrand, String osVersion, String udid );
	protected static native void 	AKUSetDocumentDirectory 		( String path );
	protected static native void 	AKUSetInputConfigurationName	( String name );
	protected static native void 	AKUSetInputDevice		 		( int deviceId, String name );
	protected static native void 	AKUSetInputDeviceCompass 		( int deviceId, int sensorId, String name );
	protected static native void 	AKUSetInputDeviceLevel 			( int deviceId, int sensorId, String name );
	protected static native void 	AKUSetInputDeviceLocation 		( int deviceId, int sensorId, String name );
	protected static native void	AKUSetInputDeviceTouch 			( int deviceId, int sensorId, String name );
	// CDSC_ENABLE_OS_EVENT
	protected static native void AKUSetInputDeviceOs				(int deviceId,	int sensorId, String name);
	protected static native void 	AKUSetScreenSize				( int width, int height );
	protected static native void    AKUSetScreenDpi                         ( int dpi );
	protected static native void 	AKUSetViewSize					( int width, int height );
	protected static native void 	AKUSetWorkingDirectory 			( String path );
	protected static native void 	AKUUntzInit			 			();
	//CDSC_ENABLE_OS_EVENT
	public static native void 	AKUUpdate				 		();

	//----------------------------------------------------------------//
	static {
		
		for ( String className : sExternalClasses )
		{
			Class < ? > theClass = findClass ( className );
			if ( theClass != null ) {
				
				sAvailableClasses.add ( theClass );
			}
		}
	}
	
	//----------------------------------------------------------------//
	public static boolean backButtonPressed () {

		boolean result;
		synchronized ( sAkuLock ) {
			result = AKUAppBackButtonPressed ();
		}
		
		return result;
	}

	//----------------------------------------------------------------//
	public static int createContext () {

		int contextId;
		synchronized ( sAkuLock ) {
			contextId = AKUCreateContext ();
			AKUSetContext ( contextId );
		}
		
		return contextId;
	}

	//----------------------------------------------------------------//
	//CDSC add (for interrupt xiaomi bug) start
	public static void releaseGraphicsContext () {
		
		synchronized ( sAkuLock ) {
			AKUReleaseGfxContext ();
		}
	}
	//CDSC add end
	//----------------------------------------------------------------//
	public static void detectGraphicsContext () {
		
		synchronized ( sAkuLock ) {
			AKUDetectGfxContext ();
		}
	}
	
	//----------------------------------------------------------------//
	public static void dialogDismissed ( int dialogResult ) {
		
		synchronized ( sAkuLock ) {
			AKUAppDialogDismissed ( dialogResult );
		}
	}	
	
	//----------------------------------------------------------------//
	public static void endSession () {
		
		synchronized ( sAkuLock ) {
			AKUAppWillEndSession ();
		}
	}	
	
	//----------------------------------------------------------------//
	public static void enqueueLevelEvent ( int deviceId, int sensorId, float x, float y, float z ) {
		
		synchronized ( sAkuLock ) {
			AKUEnqueueLevelEvent ( deviceId, sensorId, x, y, z );
		}
	}

	//----------------------------------------------------------------//
	public static void enqueueLocationEvent ( int deviceId, int sensorId, double longitude, double latitude, double altitude, float hAccuracy, float vAccuracy, float speed ) {
		
		synchronized ( sAkuLock ) {
			AKUEnqueueLocationEvent ( deviceId, sensorId, longitude, latitude, altitude, hAccuracy, vAccuracy, speed );
		}
	}

	//----------------------------------------------------------------//
	public static void enqueueCompassEvent ( int deviceId, int sensorId, float heading ) {
		
		synchronized ( sAkuLock ) {
			AKUEnqueueCompassEvent ( deviceId, sensorId, heading );
		}
	}

	//----------------------------------------------------------------//
	public static void enqueueTouchEvent ( int deviceId, int sensorId, int touchId, boolean down, int x, int y, int tapCount ) {
		
		synchronized ( sAkuLock ) {
			AKUEnqueueTouchEvent ( deviceId, sensorId, touchId, down, x, y, tapCount );
		}
	}
	
	// ----------------------------------------------------------------//
	// CDSC_ENABLE_OS_EVENT
	public static void enqueueOsEvent(int deviceId, int sensorId,
			int eventId) {

		synchronized (sAkuLock) {
			AKUEnqueueOsEvent(deviceId, sensorId, eventId);
		}
	}	
	
	//----------------------------------------------------------------//
	public static void finish () {
		
		synchronized ( sAkuLock ) {
			AKUFinalize ();
		}
	}

	//----------------------------------------------------------------//
	public static ApplicationState getApplicationState () {

		return sApplicationState;
	}

	//----------------------------------------------------------------//
	public static void init () {
		
		synchronized ( sAkuLock ) {

			AKUSetInputConfigurationName 	( "Android" );

			AKUReserveInputDevices			( Moai.InputDevice.values ().length );
			AKUSetInputDevice				( Moai.InputDevice.INPUT_DEVICE.ordinal (), "device" );
		
			AKUReserveInputDeviceSensors	( Moai.InputDevice.INPUT_DEVICE.ordinal (), Moai.InputSensor.values ().length );
			AKUSetInputDeviceCompass		( Moai.InputDevice.INPUT_DEVICE.ordinal (), Moai.InputSensor.SENSOR_COMPASS.ordinal (), "compass" );
			AKUSetInputDeviceLevel			( Moai.InputDevice.INPUT_DEVICE.ordinal (), Moai.InputSensor.SENSOR_LEVEL.ordinal (), "level" );
			AKUSetInputDeviceLocation		( Moai.InputDevice.INPUT_DEVICE.ordinal (), Moai.InputSensor.SENSOR_LOCATION.ordinal (), "location" );
			AKUSetInputDeviceTouch			( Moai.InputDevice.INPUT_DEVICE.ordinal (), Moai.InputSensor.SENSOR_TOUCH.ordinal (), "touch" );
			//CDSC_ENABLE_OS_EVENT
			AKUSetInputDeviceOs(Moai.InputDevice.INPUT_DEVICE.ordinal(), Moai.InputSensor.SENSOR_OS.ordinal(),"os");			

			AKUExtLoadLuasql ();
			AKUExtLoadLuacurl ();
			AKUExtLoadLuacrypto ();
			AKUExtLoadLuasocket ();
			
			//CDSC_TAG_LUA_PACK
			AKUExtLoadLuapack ();
			//CDSC_TAG_LUA_PACK END

			//CDSC_TAG_CDSC_USE_LSQLITE3
			AKUExtLoadLSqllite ();
			//CDSC_TAG_CDSC_USE_LSQLITE3 END


			AKUInit ();
		
			// This AKU call will exist even if FMOD has been disabled in libmoai.so, so it's
			// safe to call unconditionally.
			AKUFMODExInit ();
		
			// This AKU call will exist even if UNTZ has been disabled in libmoai.so, so it's
			// safe to call unconditionally.
			AKUUntzInit ();
		
			String appId = sActivity.getPackageName ();
		
			String appName;
			try {
			
			    appName = sActivity.getPackageManager ().getApplicationLabel ( sActivity.getPackageManager ().getApplicationInfo ( appId, 0 )).toString ();
			} catch ( Exception e ) {
			
				appName = "UNKNOWN";
			}
		
			String appVersion;
			try {
			
				appVersion = sActivity.getPackageManager ().getPackageInfo ( appId, 0 ).versionName;
			}
			catch ( Exception e ) {
			
				appVersion = "UNKNOWN";
			}
		
			String udid	= Secure.getString ( sActivity.getContentResolver (), Secure.ANDROID_ID );
			if ( udid == null ) {
			
				udid = "UNKNOWN";
			}
		
			AKUSetDeviceProperties ( appName, appId, appVersion, Build.CPU_ABI, Build.BRAND, Build.DEVICE, Build.MANUFACTURER, Build.MODEL, Build.PRODUCT, Runtime.getRuntime ().availableProcessors (), "Android", Build.VERSION.RELEASE, udid );
		}
	}	

	//----------------------------------------------------------------//
	public static void mount ( String virtualPath, String archive ) {
		
		synchronized ( sAkuLock ) {
			AKUMountVirtualDirectory ( virtualPath, archive );
		}
	}	

	//----------------------------------------------------------------//
	public static void onActivityResult ( int requestCode, int resultCode, Intent data ) {
	
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onActivityResult", new Class < ? > [] { java.lang.Integer.TYPE, java.lang.Integer.TYPE, Intent.class }, new Object [] { new Integer ( requestCode ), new Integer ( resultCode ), data });
		}	
	}

//cdsc add start
	
	public static abstract interface OnFinishHandler
	{
		public abstract void callback(boolean success);
	}
	
	public static boolean onPreRunning( Activity activity, OnFinishHandler onfinish)
	{
		boolean findMethod = false;
		for ( Class < ? > theClass : sAvailableClasses ) {
			
			Object obj = executeMethod ( theClass, null, "onPreRunning", new Class < ? > [] { Activity.class, OnFinishHandler.class }, new Object [] { activity, onfinish });
			if(obj != null)
			{
				findMethod = true;
			}
		}
		
		return findMethod;
	}
	
	public static boolean onPreExit( Activity activity, OnFinishHandler onfinish)
	{
		boolean findMethod = false;
		for ( Class < ? > theClass : sAvailableClasses ) {
			
			Object obj = executeMethod ( theClass, null, "onPreExit", new Class < ? > [] { Activity.class, OnFinishHandler.class }, new Object [] { activity, onfinish });
			if(obj != null)
			{
				findMethod = true;
			}
		}
		
		return findMethod;
	}
	
//cdsc add end
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {

		sActivity = activity;
		
		MoaiMoviePlayer.onCreate ( activity );

		for ( Class < ? > theClass : sAvailableClasses ) {
			
			executeMethod ( theClass, null, "onCreate", new Class < ? > [] { Activity.class }, new Object [] { activity });
		}
	}
	
	//----------------------------------------------------------------//
	public static void onDestroy () {
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onDestroy", new Class < ? > [] { }, new Object [] { });
		}		
	}

	//----------------------------------------------------------------//
	public static void onPause () {
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onPause", new Class < ? > [] { }, new Object [] { });
		}	
		

	}

	//CDSC_ANDROID_EXIT 娑撹濮╅柅锟藉毉濞撳憡鍨�
	public static void exit() {
		if (sActivity != null) {
			
			Moai.OnFinishHandler onFinishExit = new Moai.OnFinishHandler() 
			{
				@Override
				public void callback(boolean success) 
				{
					if (success)
					{
						sActivity.finish();
					}
				}
			};
			if(!onPreExit(sActivity, onFinishExit))
			{
				sActivity.finish();
			}
		}
	}
	//CDSC_ANDROID_EXIT END

	//----------------------------------------------------------------//
	public static void onResume () {
	
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onResume", new Class < ? > [] { }, new Object [] { });
		}		
	}

	//----------------------------------------------------------------//
	public static void onStart () {
	
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onStart", new Class < ? > [] { }, new Object [] { });
		}		
	}

	//----------------------------------------------------------------//
    public static void onRestart () {
	
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onRestart", new Class < ? > [] { }, new Object [] { });
		}		
	}

	//----------------------------------------------------------------//
	public static void onStop () {
	
		for ( Class < ? > theClass : sAvailableClasses ) {

			executeMethod ( theClass, null, "onStop", new Class < ? > [] { }, new Object [] { });
		}		
	}

	//----------------------------------------------------------------//
	public static void pause ( boolean paused ) {
		
		synchronized ( sAkuLock ) {
			AKUPause ( paused );
		}
	}

	//----------------------------------------------------------------//
	public static void render () {
		
		synchronized ( sAkuLock ) {
			//MoaiKeyboard.update (); //CDSC_REMOVE
			//AKUUpdate ();	//杩欓噷灏濊瘯浠嶇劧鐢眒UpdateRunnable杩涜鍒锋柊銆傜敱浜庡潎闇�閿佸畾锛屽洜姝ら潪甯告�鐤戞媶鍒嗘垚涓や釜绾跨▼
			//鐨勫疄闄呬綔鐢�
			AKURender ();
		}
	}

	//----------------------------------------------------------------//
	public static void runScript ( String filename ) {
		
		synchronized ( sAkuLock ) {
			AKURunScript ( filename );
		}
	}
	
	//----------------------------------------------------------------//
	public static void setApplicationState ( ApplicationState state ) {

		if ( state != sApplicationState ) {
			
			sApplicationState = state;
		
			for ( Class < ? > theClass : sAvailableClasses ) {
			
				executeMethod ( theClass, null, "onApplicationStateChanged", new Class < ? > [] { ApplicationState.class }, new Object [] { sApplicationState });
			}
		}
	}

	//----------------------------------------------------------------//
	public static void setConnectionType ( long connectionType ) {
		
		synchronized ( sAkuLock ) {
			AKUSetConnectionType ( connectionType );
		}
	}	
	
	//----------------------------------------------------------------//
	public static void setDocumentDirectory ( String path ) {
		
		synchronized ( sAkuLock ) {
			AKUSetDocumentDirectory ( path );
		}
	}	
	
	//----------------------------------------------------------------//
	public static void setScreenSize ( int width, int height ) {
		
		synchronized ( sAkuLock ) {
			AKUSetScreenSize ( width, height );
		}
	}	

	//----------------------------------------------------------------//
	public static void setScreenDpi ( int dpi ) {
		
		synchronized ( sAkuLock ) {
			AKUSetScreenDpi ( dpi );
		}
	}	

	//----------------------------------------------------------------//
	public static void setViewSize ( int width, int height ) {
		
		synchronized ( sAkuLock ) {
			AKUSetViewSize ( width, height );
		}
	}	

	//----------------------------------------------------------------//
	public static void setWorkingDirectory ( String path ) {
		
		synchronized ( sAkuLock ) {
			AKUSetWorkingDirectory ( path );
		}
	}	
	
	//----------------------------------------------------------------//
	public static void startSession ( boolean resumed ) {
		
		synchronized ( sAkuLock ) {
			// CDSC PATCH
			if (!resumed)
			{
//				MoaiLog.i (String.format("MoaiLog.enqueueOsEvent(%d,%d,%d)", Moai.InputDevice.INPUT_DEVICE.ordinal (),
//						Moai.InputSensor.SENSOR_OS.ordinal (),
//						Moai.OsEvent.OS_EVENT_START.ordinal())
//						);
				enqueueOsEvent(Moai.InputDevice.INPUT_DEVICE.ordinal (),
						Moai.InputSensor.SENSOR_OS.ordinal (),
						Moai.OsEvent.OS_EVENT_START.ordinal()
				);
			};
			AKUAppDidStartSession ( resumed );
		}
	}	

	//----------------------------------------------------------------//
	public static void update () {
		
		synchronized ( sAkuLock ) {
			AKUUpdate ();
		}
	}	
	
	//================================================================//
	// Private methods
	//================================================================//
	
	//----------------------------------------------------------------//
	public static Class < ? > findClass ( String className ) {
		
		Class < ? > theClass = null;
		try {

			theClass = Class.forName ( className );
		} catch ( Throwable e ) {

		}
		
		return theClass;
	}
	
	//----------------------------------------------------------------//
	public static Object executeMethod ( Class < ? > theClass, Object theInstance, String methodName, Class < ? > [] parameterTypes, Object [] parameterValues ) {
		
		Object result = null;
		if ( theClass != null ) {
			
			try {

				Method theMethod = theClass.getMethod ( methodName, parameterTypes );

				result = theMethod.invoke ( theInstance, parameterValues );
			} catch ( Throwable e ) {
				
			}			
		}
		
		return result;
	}
	
	//================================================================//
	// Miscellaneous JNI callback methods
	//================================================================//

	//----------------------------------------------------------------//
	public static String getGUID () {
	
		return UUID.randomUUID ().toString ();
	}
    //cdsc add start
	public static String getInetAddress(String host){
		String IPAddress = null; 
		java.net.InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
		} catch (java.net.UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return  IPAddress;
		}
		return IPAddress;
	}

	// CDSC_ENABLE_JSON_RPC
	public static String callFromLua(String paramsInJson) {
		String result=null;
		if (null != Moai.sActivity) {
			//result =  MoaiActivity.sInst.JsonAPI(paramsInJson);
			result =MoaiBaseSdk.JsonAPI(paramsInJson);
		};
		if (result == null)
		{
			result = "";
		}
		return result;
	}

	// ----------------------------------------------------------------//
	public static int getStatusBarHeight() {

		int myHeight = 0;
		switch ( sActivity.getResources ().getDisplayMetrics ().densityDpi ) {
	        case DisplayMetrics.DENSITY_HIGH:

	            myHeight = 54;
	            break;
	        case DisplayMetrics.DENSITY_MEDIUM:

	            myHeight = 36;
	            break;
	        case DisplayMetrics.DENSITY_LOW:

	            myHeight = 26;
	            break;
			default:
				
				myHeight = 0;
				break;
			}
			
		return myHeight;
	}
	
	//----------------------------------------------------------------//
	public static long getUTCTime () {
		
		Calendar cal = Calendar.getInstance ( TimeZone.getTimeZone ( "UTC" )); 
		long inSeconds = cal.getTimeInMillis () / 1000;
		return inSeconds;
	}

	//----------------------------------------------------------------//
	public static void localNotificationInSeconds ( int seconds, String message, String [] keys, String [] values ) {
		
		
		MoaiLog.i ( "Moai localNotificationInSeconds: Adding notification alarm" );

		Calendar cal = Calendar.getInstance (); 	// get a Calendar object with current time	
        cal.setTimeInMillis ( System.currentTimeMillis ());
		cal.add ( Calendar.SECOND, seconds );		// add desired time to the calendar object
	
		Intent intent = new Intent ( sActivity, MoaiLocalNotificationReceiver.class );
		for ( int i = 0; i < keys.length; ++i ) {
			intent.putExtra ( keys [ i ], values [ i ]);
		}
		
		PendingIntent sender = PendingIntent.getBroadcast ( sActivity, 0, intent, 0 );

		AlarmManager am = ( AlarmManager ) sActivity.getSystemService ( Context.ALARM_SERVICE );
		am.set ( AlarmManager.RTC_WAKEUP, cal.getTimeInMillis (), sender );	
	}
	
	//----------------------------------------------------------------//
	public static void openURL ( String url ) {

		sActivity.startActivity ( new Intent ( Intent.ACTION_VIEW, Uri.parse ( url )));
	}
	//CDSC add begin
	public static void installApk(String path)
	{
	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(new java.io.File( path )), "application/vnd.android.package-archive");
	    sActivity.startActivity(intent);
	}
	public static int getNetworkState() 
	{
	    try {
		ConnectivityManager connectivity=(ConnectivityManager)sActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity == null) 
		{
		    return 0;
		} 
		else 
		{
		    NetworkInfo[] info=connectivity.getAllNetworkInfo();
		    if(info != null) 
		    {
					    boolean hasWifi 	= false;
			    boolean hasNetwork 	= false;
			for(int i=0; i < info.length; i++) 
			{
			    if(info[i].getState() == NetworkInfo.State.CONNECTED) 
			    {
				    hasNetwork = true;
				    if(info[i].getType() == ConnectivityManager.TYPE_WIFI)
				    {
					    hasWifi = true;
					    break;
				    }
			    }
			}
			if(hasWifi)
			{
			    return 1;
			}
			else if(hasNetwork)
			{
			    return 2;
			}
		    }
		}
		return 0;
	    } 
	    catch(Exception ex) 
	    {
		return 0;
	    }
	}
	//CDSC add end
	//----------------------------------------------------------------//
	public static void share ( String prompt, String subject, String text ) {

		Intent intent = new Intent ( Intent.ACTION_SEND ).setType ( "text/plain" );
		
		if ( subject != null ) intent.putExtra ( Intent.EXTRA_SUBJECT, subject );
		if ( text != null ) intent.putExtra ( Intent.EXTRA_TEXT, text );
	
		sActivity.startActivity ( Intent.createChooser ( intent, prompt ));
	}
	
	//----------------------------------------------------------------//
	public static void showDialog ( String title, String message, String positiveButton, String neutralButton, String negativeButton, boolean cancelable ) {
		final AlertDialog.Builder builder = new AlertDialog.Builder ( sActivity );

		if ( title != null ) builder.setTitle ( title );
		if ( message != null ) builder.setMessage ( message );

		if ( positiveButton != null ) {
			
			builder.setPositiveButton ( positiveButton, new DialogInterface.OnClickListener () {
				
				public void onClick ( DialogInterface arg0, int arg1 ) {

					Moai.dialogDismissed ( Moai.DialogResult.RESULT_POSITIVE.ordinal ());
				}
			});
		}

		if ( neutralButton != null ) {
			
			builder.setNeutralButton ( neutralButton, new DialogInterface.OnClickListener () {
				
				public void onClick ( DialogInterface arg0, int arg1 ) {
					
					Moai.dialogDismissed ( Moai.DialogResult.RESULT_NEUTRAL.ordinal ());
				}
			});
		}

		if ( negativeButton != null ) {
			
			builder.setNegativeButton ( negativeButton, new DialogInterface.OnClickListener () {
				
				public void onClick ( DialogInterface arg0, int arg1 ) {
					
					Moai.dialogDismissed ( Moai.DialogResult.RESULT_NEGATIVE.ordinal ());
				}
			});
		}

		builder.setCancelable ( cancelable );
		
		if ( cancelable ) {
			
			builder.setOnCancelListener ( new DialogInterface.OnCancelListener () {
				
				public void onCancel ( DialogInterface arg0 ) {
					
					Moai.dialogDismissed ( Moai.DialogResult.RESULT_CANCEL.ordinal ());
				}
			});
		}

builder.create ().show ();

/*
		sActivity.runOnUiThread( new Runnable () {
			public void run () {
				builder.create ().show ();
			}
		});
		*/
	}
}

