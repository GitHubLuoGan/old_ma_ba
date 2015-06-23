package com.ddworlds.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;
import android.util.Log;


public class AbsLog {
	public static void log_X(){

	}

	/**
	 * 读取配置文件
	 * @return
	 */
	public static String read_debug(){
		String _path=Environment.getExternalStorageDirectory().getPath();

		System.out.println("path="+_path);
//		String fileName = _path+"/DDWorlds/ddz/yl_debug.ini";
		String fileName = _path+File.separator+"DDWorlds"+File.separator+"ddz"+File.separator+"yl_debug.ini";
//		String fileName = _path+"/yl_debug.ini";
		System.out.println("fileName="+fileName);

		//也可以用String fileName = "mnt/sdcard/Y.txt";

		String res="";

		try{

		FileInputStream fin = new FileInputStream(fileName);

//		FileInputStream fin = openFileInput(fileName);

		//用这个就不行了，必须用FileInputStream

		    int length = fin.available();

		    byte [] buffer = new byte[length];

		    fin.read(buffer);

		    res = EncodingUtils.getString(buffer, "UTF-8");

		    fin.close();

		    }catch(Exception e){

		           e.printStackTrace();

		}
		return res;
	}

	/**
	 * 另一种读取文件的方法
	 */
	public static void read_file(){
		String _path=Environment.getExternalStorageDirectory().getPath();

		System.out.println("path="+_path);
		String fileName = _path+File.separator+"DDWorlds"+File.separator+"ddz"+File.separator+"yl_debug.ini";
//		String fileName = _path+"/yl_debug.ini";
		System.out.println("fileName="+fileName);
		File file = new File(fileName);
		FileReader m_Fr;
		BufferedReader m_Readbuf;

		if(!file.exists())
		{
			log_i("文件不存在", " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
		try {
			m_Fr = new FileReader(fileName);
			m_Readbuf = new BufferedReader(m_Fr);
			String str = null;
			while((str=m_Readbuf.readLine()) != null)
			{
				log_i("一行字符串输出",str + "&&&&&&&&&&&&&&&&&&&&&&&");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}



	public static void log_i(String tag,String msg){
		String _path=Environment.getExternalStorageDirectory().getPath();
		File file = new File(_path+"/abs.log");
//		if(!file.exists()){
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		if(file.exists()){
			FileOutputStream fileStream;
			try {
				fileStream = new FileOutputStream(file, true);

				Date date = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String nowTime = df.format(date);
				fileStream.write((nowTime + " i  " + tag+"   "+msg + "\n").getBytes("UTF-8"));
				fileStream.write("\r\n".getBytes());
				fileStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i(tag, msg);
		}

	}


}
