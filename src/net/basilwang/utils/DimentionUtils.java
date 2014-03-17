package net.basilwang.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DimentionUtils {
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static int getWindowWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		int width;
		try { 
			display.getSize(size); 
			width = size.x; 
		} 
		catch (NoSuchMethodError e)
		{ 
			width = display.getWidth(); 
		}
		return width;
	}
	public static int getWindowHeight(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		int height;
		try { 
			display.getSize(size); 
			height = size.y; 
		} 
		catch (NoSuchMethodError e)
		{ 
			height = display.getHeight(); 
		}
		return height;
	}
	
}
