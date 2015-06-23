package com.ddworlds.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class HttpUtil {
	public final static String HTTP_STATUS = "http_status";
	public final static String HTTP_BODY = "http_body";
	
	private final static int HTTP_CONNECTIONTIMEOUT = 15 * 1000; // 15 seconds
	private final static int HTTP_SOTIMEOUT = 15* 1000; // 15 seconds
	
	private Context mContext;
	
	private HttpUtil(){		
	}
	
	public HttpUtil(Context ctx){
		mContext = ctx;
	}
	
	/**
	 * 执行一个HTTP GET操作，得到返回值
	 * @param request
	 * @return
	 */
	public Bundle doGet(String request){
		return doHttp(0, request);
	}
	
	/**
	 *  执行一个HTTP POST操作，得到返回值
	 * @param request
	 * @return
	 */
	public Bundle doPost (String request){
		return doHttp(1, request);
	}

	
	/**
	 * 
	 * @param op  0 : GET ,  1 : POST
	 * @param request
	 * @return
	 */
	private Bundle doHttp (int op, String request){		
		Bundle data = new Bundle();		
		HttpClient http_client = null;
		HttpResponse http_resp = null;	
		HttpUriRequest http_req = null;
		int status = 0;	
		
		try {
			http_client = createHttpClient();

			//检测代理设置
			HttpHost proxy = detectProxy();
			if (proxy != null) {
				http_client.getParams().setParameter(
						ConnRouteParams.DEFAULT_PROXY, proxy);
			}							
			
			switch (op) {
				case 0:
					http_req = new HttpGet(request);
					break;
				case 1:
					http_req = new HttpPost(request);
					break;
				default:
					http_req = new HttpGet(request);
					break;
					
			}
		
			http_req.setHeader("Accept-Encoding", "gzip,deflate");					
			if (proxy != null){
				http_req.addHeader("X-Online-Host", http_req.getURI().getHost());
				http_req.addHeader("User-Agent","Mozilla/5.0 (Macintosh; PPC Mac OS X; U; en) Opera 8.0");
			}				       
			
			http_resp = http_client.execute(http_req);
			status = http_resp.getStatusLine().getStatusCode();		
			data.putInt(HTTP_STATUS, status);

			if ((status != HttpStatus.SC_OK)
					&& (status != HttpStatus.SC_PARTIAL_CONTENT)) {	
				http_req.abort();
				return data;
			}				
			
			//为移动cmwap拦截页特殊例外处理
			Header header = http_resp.getFirstHeader("Content-Type");
			if (header != null && header.getValue() != null &&
					header.getValue().indexOf("wap.wml") > 0 ){
				Log.d("erik", "cmwap wap.wml content-type found");
				http_resp = http_client.execute(http_req);
				status = http_resp.getStatusLine().getStatusCode();				
				
				if ((status != HttpStatus.SC_OK)
						&& (status != HttpStatus.SC_PARTIAL_CONTENT)) {			
					http_req.abort();
					return data;
				}	
			}
			
			addHttpHeader(data, http_resp);
			addHttpBody(data, http_resp);		
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();	
						
		} catch (java.net.SocketTimeoutException e) {				
			e.printStackTrace();
					
		} catch (java.net.SocketException e) {				
			e.printStackTrace();
					
		} catch (IOException e) {				
			e.printStackTrace();
				
		} finally {					
			if (http_client != null) {
				http_client.getConnectionManager().shutdown();
				http_client = null;
			}
			if (http_resp != null) {
				http_resp = null;
			}		
		}		
	
		return data;
	}		
	
	private HttpHost detectProxy() {
		HttpHost proxy = null;
		String proxyHost = android.net.Proxy.getDefaultHost();
		int port = android.net.Proxy.getDefaultPort();

		boolean enabled = false;
		try {
			if (mContext != null) {
				WifiManager wifiManager = (WifiManager) mContext
						.getSystemService(Context.WIFI_SERVICE);
				if (wifiManager != null) {
					enabled = wifiManager.isWifiEnabled();
					if (enabled) {
						if (wifiManager.getConnectionInfo().getIpAddress() == 0) {
							enabled = false;
						}
					}
				}
			}
		} catch (Exception e) {

		}
		if (!enabled && proxyHost != null) {
			proxy = new HttpHost(proxyHost, port);
		}
		return proxy;
	}
	
	private HttpClient createHttpClient() {	
		HttpParams	params = new BasicHttpParams();	

		//设置链接超时和socket超时，及缓存大小 
		HttpConnectionParams.setConnectionTimeout(params,
				HTTP_CONNECTIONTIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_SOTIMEOUT);
		// HttpConnectionParams.setSocketBufferSize(params,
		// HTTP_SOCKETBUFFERSIZE);
		
		// 设置重定向，缺省为 true
		HttpClientParams.setRedirecting(params, true);
	
		try {			
			SchemeRegistry schemeRegistry = new SchemeRegistry();			
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			SingleClientConnManager mgr = new SingleClientConnManager(params,
					schemeRegistry);

			return new DefaultHttpClient(mgr, params);
		} catch (Exception e) {				
			return new DefaultHttpClient(params);
		}
	}
	
	private void addHttpHeader(Bundle data, HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		int count = response.getAllHeaders().length;
		
		for (int i = 0; i < count; i++) {	
//			Log.d("erik", "headers[" + i + "].getName() = " + headers[i].getName()
//					+ ", value = " +  headers[i].getValue());
			data.putString(headers[i].getName(), headers[i].getValue());
		}

		if (headers != null) {
			headers = null;
		}
	}
	
	private void addHttpBody(Bundle data, HttpResponse response) {	
		HttpEntity entity = response.getEntity();
		String s = data.getString("Content-Encoding");
		if (s != null && s.indexOf("gzip") >= 0) {
			data.putByteArray(HTTP_BODY, readHttpBody(entity, true));
		} else {
			data.putByteArray(HTTP_BODY, readHttpBody(entity, false));
		}
	}
	
	private byte[] readHttpBody(HttpEntity entity, boolean ziped) {
		InputStream in = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] byte_data = null;
	
		try {
			if (ziped) {
				in = new GZIPInputStream(entity.getContent());
			} else {
				in = entity.getContent();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (in == null) {
			return null;
		}

		byte[] bytearray = new byte[1024];
		int len = 0;

		try {
			while ((len = in.read(bytearray)) > 0) {
				bout.write(bytearray, 0, len);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte_data = bout.toByteArray();		

		try {
			if (in != null) {
				in.close();
			}
			if (bout != null) {
				bout.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in = null;
		}		
	
		return byte_data;
	}
	
}
