package com.ddworlds.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MyGet
{
	private String result;

	public String GetResult(String url)
	{
		try
		{
			Log.e("zcc", "url--"+url);
			result = null;
			URL myURL = new URL(url);
			URLConnection ucon = myURL.openConnection();
			ucon.setConnectTimeout(10000);
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			int current = 0; 
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			result = EncodingUtils.getString(baf.toByteArray(), "GB2312");
//			Log.e("zcc", "url--"+url+"--result--"+result);
			System.out.println("zcc result="+result);
			if (null !=result && result.equals("zero"))
			{
				result = null;
			}
			baf.clear();
			bis.close();
			is.close();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;

	}
	
	//保存日志
	protected void save_log(String data) {
		  try { 
			  File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录

		         File saveFile = new File(sdCardDir, "zcc_log.txt");

		       FileOutputStream outStream = new FileOutputStream(saveFile);
	            outStream.write(data.getBytes());
	            outStream.close();
	            //Toast.makeText(AppDetailActivity.this,"Saved",Toast.LENGTH_LONG).show();
	        } catch (FileNotFoundException e) {
	            return;
	        }
	        catch (IOException e){
	            return ;
	        }
	}
	
	public String GetResultForRecommend(String url)
	{
		InputStream is = null;
		BufferedInputStream bis = null;
		ByteArrayBuffer baf = null;
		URLConnection ucon = null;
		try
		{
			result = null;
			if(url.contains("&closetime="))
				return result;
			URL myURL = new URL(url);
			ucon = myURL.openConnection();
			ucon.setConnectTimeout(10000);
			ucon.setReadTimeout(15000);
			is = ucon.getInputStream();
			bis = new BufferedInputStream(is);
			baf = new ByteArrayBuffer(1024);
			int current = 0;

			while ((current = bis.read()) != -1)
			{
				//System.out.println("ttt"+current); 
				baf.append((byte) current);
			}
			//System.out.println("ttt n1="+n1);
			//result = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			result = EncodingUtils.getString(baf.toByteArray(), "GB2312");
			Log.e("sunlei", "url--"+url+"--result--"+result);
			//save_log("url--"+url+"--result--"+result);
			if (null !=result && result.equals("zero"))
			{
				result = null;
			}
			baf.clear();
			bis.close();
			is.close();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if (baf != null)
				baf.clear();
	
			try {
				if (bis != null)
					bis.close();

				if (is != null)
					is.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}
	
	public static void main(String[] args)
	{
//		new MyGet().GetResult("http://zhiku.3gsou.com/get/ModelCase.aspx?td=1001&w=320&h=480&mid=9&pr=6&pi=1");
	}
	
}
