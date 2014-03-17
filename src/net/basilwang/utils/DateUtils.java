/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.basilwang.utils;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_ABBREV_TIME;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class DateUtils {
	
	public static String getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		String day = String.valueOf(cal.get(Calendar.DATE));
		return day;
	}
	public static String getCurrentWeekOfDay() {
		Calendar cal = Calendar.getInstance();
		int dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK);
		Log.d("DATEUTILS", String.valueOf(dayOfWeekIndex));
		String dayOfWeek = "";
		switch(dayOfWeekIndex)
		{
		case 1:
			dayOfWeek="日";
		    break;
		case 2:
			dayOfWeek="一";
		    break;
		case 3:
			dayOfWeek="二";
		    break;
		case 4:
			dayOfWeek="三";
		    break;
		case 5:
			dayOfWeek="四";
		    break;
		case 6:
			dayOfWeek="五";
		    break;
		case 7:
			dayOfWeek="六";
		    break;
		}
		return "星期"+dayOfWeek;
	}
//	public static String getCurrentDay() {
//	Calendar cal = Calendar.getInstance();
//	String month = String.valueOf(cal.get(Calendar.MONTH) + 1) ;
//	String day = String.valueOf(cal.get(Calendar.DATE));
//	String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
//	String minute = String.valueOf(cal.get(Calendar.MINUTE));
//	return (month + "月" + day +"日");
//}
    /**
     * Lazily create date format objects, one per thread. Use soft references so format
     * may be collected when low on memory.
     */
    private static final ThreadLocal<SoftReference<DateFormat>> cDateFormat = 
        new ThreadLocal<SoftReference<DateFormat>>() {
        
            private SoftReference<DateFormat> createValue() {  
                return new SoftReference<DateFormat>(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));  
            }  
            
            @Override
            public SoftReference<DateFormat> get() {
                SoftReference<DateFormat> value = super.get();
                if (value == null || value.get() == null) {
                    value = createValue();
                    set(value);
                }
                return value;
            }
    };  

    /**
     * Accepts strings in ISO 8601 format. This includes the following cases:
     * <ul>
     * <li>2009-08-15T12:50:03+01:00</li>
     * <li>2009-08-15T12:50:03+0200</li>
     * <li>2010-04-07T04:00:00Z</li>
     * </ul>
     */
    public static long parseIso8601Date(String dateStr) throws ParseException {
        // normalize timezone first
        String timezone = dateStr.substring(19);
        timezone = timezone.replaceAll(":", "");
        if ("Z".equals(timezone)) {
            // Z indicates UTC, so convert to standard representation
            timezone = "+0000";
        }
        String cleanedDateStr = dateStr.substring(0, 19) + timezone;
        DateFormat f = cDateFormat.get().get();
        Date d = f.parse(cleanedDateStr);
        return d.getTime();
    }
    
    public static String formatIso8601Date(long ms) {
        DateFormat f = cDateFormat.get().get();
        String dateStr = f.format(new Date(ms));
        if (dateStr.length() == 24) {
            dateStr = dateStr.substring(0, 22) + ":" + dateStr.substring(22);
        }
        return dateStr;
    }
    
	public static boolean isSameDay(long millisX, long millisY) {
		return Time.getJulianDay(millisX, 0) == Time.getJulianDay(millisY, 0);
	}
	public static boolean isSameDay(Time x,Time y)
	{
		return x.year==y.year&&x.month==y.month&&x.monthDay==y.monthDay;
	}
		
	public static CharSequence displayDateRange(Context context, long startMs, long endMs, boolean includeTime) {
		CharSequence result = "";
		final boolean includeStart = startMs > 0L;
		final boolean includeEnd = endMs > 0L;
		
		if (includeStart) {
		    if (includeEnd) {
	            int flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
	            if (includeTime) {
	                flags |= FORMAT_SHOW_TIME | FORMAT_ABBREV_TIME; 
	            }
	            result = android.text.format.DateUtils.formatDateRange(
	                    context, startMs, endMs, flags);
		    } else {
		        result = displayShortDateTime(context, startMs);
		    }
		} else if (includeEnd) {
            result = displayShortDateTime(context, endMs);
		}
		
		return result;
	}
	
    /**
     * Display date time in short format using the user's date format settings
     * as a guideline.
     * 
     * For epoch, display nothing.
     * For today, only show the time.
     * Otherwise, only show the day and month.
     * 
     * @param context
     * @param timeInMs datetime to display
     * @return locale specific representation
     */
    public static CharSequence displayShortDateTime(Context context, long timeInMs) {
        long now = System.currentTimeMillis();
        CharSequence result;
        if (timeInMs == 0L) {
            result = "";
        } else {
            int flags;
            if (isSameDay(timeInMs, now)) {
                flags = FORMAT_SHOW_TIME | FORMAT_ABBREV_TIME;
            } else {
                flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
            }
            result = android.text.format.DateUtils.formatDateRange(
                    context, timeInMs, timeInMs, flags);
        }
        return result;
    }
	public static int getWeekSpan(Time weekStartTime,Time thisWeekTime)
	{
		int span=0;
		if(Time.compare(weekStartTime,thisWeekTime)<=0)
		{
			int startWeekNum=weekStartTime.getWeekNumber();
			int thisWeekNum=thisWeekTime.getWeekNumber();
			//2012-06-07 basilwang if longer than one year,must recaculate thisWeekNum, notice use iso8601 week
			///TODO 2012-06-07 basilwang we need show some tip to user of not setting span longer than one years
			if(thisWeekTime.year-weekStartTime.year==1)
			{
			   Time Jan1stTime=new Time();
			   Jan1stTime.set(1, Calendar.JANUARY, thisWeekTime.year);
			   Jan1stTime.normalize(true);
			   Time JanJudgeTime=new Time();
			   
			   boolean thisWeekNeedsAdd=true;
			   int weekDay=(Jan1stTime.weekDay==0?7:Jan1stTime.weekDay);
			   //if older than thursday, we need plus 52 of course.
			   if(weekDay>4)
			   {
				  for(int i=1;i<=7-weekDay+1;i++)
				  {
					  JanJudgeTime.set(i,Calendar.JANUARY,thisWeekTime.year);
					  JanJudgeTime.normalize(true);
					  if(DateUtils.isSameDay(thisWeekTime, JanJudgeTime))
					  {
						  thisWeekNeedsAdd=false;
					  }
				  }
				  
			   }
			   if(thisWeekNeedsAdd)  //else must be 52,we don't need set to 52
			   {
				   thisWeekNum+=52;
			   }
			
			   
			   Time Dec31thTime=new Time();
			   Dec31thTime.set(31, Calendar.DECEMBER, weekStartTime.year);
			   Dec31thTime.normalize(true);
			   Time DecJudgeTime=new Time();
			   
			   boolean weekStartNeedsAdd=false;
			   weekDay=(Dec31thTime.weekDay==0?7:Dec31thTime.weekDay);
			   if(weekDay<4)
			   {
				   
				  for(int i=1,j=31;i<=weekDay;i++,j--)
				  {
					  DecJudgeTime.set(j,Calendar.DECEMBER,weekStartTime.year);
					  DecJudgeTime.normalize(true);
					  if(DateUtils.isSameDay(weekStartTime, DecJudgeTime))
					  {
						  weekStartNeedsAdd=true;
					  }
				  }
			   }
			   if(weekStartNeedsAdd)  //must 52+1
			   {
				   startWeekNum=53;
			   }
			}

			span=thisWeekNum-startWeekNum+1;
			
		}
		else
		{
			span=-1;
		}
		return span;
			
	}
	
	private static Calendar cal = Calendar.getInstance();

	/**
	 * 获取具体到秒的时间
	 */
	private static SimpleDateFormat secondDateformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	/**
	 * 获取当前年份和月 例如 2013-07
	 */
	private static SimpleDateFormat yearAndMonthFormat = new SimpleDateFormat(
			"yyyy-MM");

	/**
	 * 
	 * 获取具体到秒的时间
	 * 
	 */
	public static String getCurrentSecondTime() {
		return secondDateformat.format(new Date());
	}

	/**
	 * 获取当前年份和月 格式例如 2013-07
	 */
	public static String getCurrentYearAndMonth() {
		return yearAndMonthFormat.format(new Date());
	}

	public static String getCurrentYear() {
		return String.valueOf(cal.get(Calendar.YEAR));
	}

	public static int getCurrentDayOfMonth() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}
}
