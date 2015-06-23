package com.ddworlds.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	// 定义Context
		private Context		mContext;
		private int[]	mImageIds;

		public ImageAdapter(Context c,int[] data1)
		{
			mContext = c;
			mImageIds=data1;//初始化图片
		}

		// 获取图片的个数
		public int getCount()
		{
			return mImageIds.length;
		}

		// 获取图片在库中的位置
		public Object getItem(int position)
		{
			return position;
		}


		// 获取图片ID
		public long getItemId(int position)
		{
			return position;
		}


		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView imageView;
			if (convertView == null)
			{
				// 给ImageView设置资源
				imageView = new ImageView(mContext);
				// 设置布局 图片120×120显示
				int width=Params.widthpx;
				int img_w;  
				int h;
				if(width<=400){
					h=33;
					img_w= (width-60)/3;
				}else if (width<=480){
					h=60;  
					img_w= (width-60)/3;
				}else if (width<=800){
					h=105;
					img_w= (width-60)/3;  
				}else{
					h=108+54;
					img_w= (width-95)/3; //img_w= (width-75)/3;
					//h=108;
					//img_w=218;
				}
				System.out.println("width="+width+"img_w="+img_w);
				imageView.setLayoutParams(new GridView.LayoutParams(img_w,  h));
				// 设置显示比例类型
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER );//FIT_CENTER
				
			}
			else
			{
				imageView = (ImageView) convertView;
			}

//			imageView.setImageResource(mImageIds[position]);
			imageView.setBackgroundResource(mImageIds[position]);
			return imageView;
		}
}
