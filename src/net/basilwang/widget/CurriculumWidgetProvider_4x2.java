/*
 * Copyright (C) 2009 nEx.Software
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

package net.basilwang.widget;

import static net.basilwang.dao.Preferences.CURRICULUM_TO_SHOW;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.utils.Constants.cIdType;
import static net.basilwang.utils.Constants.cPackage;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.basilwang.R;
import net.basilwang.dao.CurriculumService;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Curriculum;
import net.basilwang.entity.Semester;
import net.basilwang.utils.CurriculumUtils;
import net.basilwang.utils.DateUtils;
import net.basilwang.utils.PreferenceUtils;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class CurriculumWidgetProvider_4x2 extends AppWidgetProvider {
	private static final String TAG = "CurriculumWidget";
	private static final int ENTRIES = 3;
	private ContextBitmapProvider mBitmapProvider;
	private static final HashMap<String, Integer> sIdCache = new HashMap<String, Integer>();
	private CurriculumService curriculumService;
	private Context context;
	private RemoteViews views;
	private int pageIndex = 0;
	private List<Curriculum> curriculumList;

	private void init(Context context) {
		this.context = context;
		this.curriculumService = new CurriculumService(context);
	}

	public void onUpdate(android.content.Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		init(context);
		Log.d(TAG, "onUpdate");
		ComponentName thisWidget = new ComponentName(context, getClass());
		int[] localAppWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		Arrays.sort(localAppWidgetIds);

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			if (Arrays.binarySearch(localAppWidgetIds, appWidgetId) >= 0) {
				updateAppWidget(context, appWidgetManager, appWidgetId);
				// TaskListContext listContext =
				// WidgetManager.loadListContextPref(context, appWidgetId);
				// if (listContext != null) {
				// if (Log.isLoggable(TAG, Log.DEBUG)) {
				// Log.d(TAG, "Updating widget " + appWidgetId +
				// " with context " + listContext);
				// }
				// updateAppWidget(context, appWidgetManager, appWidgetId,
				// listContext);
				// } else {
				// Log.e(TAG, "Couldn't build TaskListContext for app widget " +
				// appWidgetId);
				// }
			} else {
				if (Log.isLoggable(TAG, Log.DEBUG)) {
					String message = String.format(
							"App widget %s not handled by this provider %s",
							appWidgetId, getClass());
					Log.d(TAG, message);
				}
			}
		}
	}

	@Override
	public void onDeleted(android.content.Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		init(context);
		ensureRemoteViews(context);
		ensureCurriculumList(context);
		if (intent.getAction().equals("net.basilwang.widget.previous")) {
			if(isPageIndexCanMinus())
			{
			  pageIndex -= 1;
			}
		}
		if (intent.getAction().equals("net.basilwang.widget.next")) {
			if(isPageIndexCanPlus())
			{
			  Log.d(TAG, "onplus repageIndex is " + pageIndex);
			  pageIndex += 1;
			}
		}
		Log.d(TAG, "onreceive repageIndex is " + pageIndex);
		views.setTextViewText(R.id.widget_weekofday,
				DateUtils.getCurrentWeekOfDay());
		views.setTextViewText(R.id.widget_day, DateUtils.getCurrentDay());

		//setupClickIntents(context, views);
		populateCurriculumLayout(context, views);

		AppWidgetManager.getInstance(context).updateAppWidget(
				AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, CurriculumWidgetProvider_4x2.class)), views);
	}

	private void ensureCurriculumList(Context androidContext) {
		if (curriculumList == null) {
			int weekSpan = getWeekSpan(androidContext);
			Log.d(TAG, "weekSpan is " + weekSpan);
			curriculumList = CurriculumUtils.filterCurriclumsByWeek(weekSpan,
					getCurriculumList());
		}

	}

	protected void ensureRemoteViews(
			final android.content.Context androidContext) {
		if (views == null) {
			views = new RemoteViews(androidContext.getPackageName(),
					R.layout.widget_pre_honeycomb_4x2);
		}
	}

	private void updateAppWidget(final android.content.Context androidContext,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			String message = String.format(
					"updateAppWidget appWidgetId=%s provider=%s", appWidgetId,
					getClass());
			Log.d(TAG, message);
		}
		// 2012-12-10 basilwang ensure remote view
		ensureRemoteViews(androidContext);
		ensureCurriculumList(androidContext);
		views.setTextViewText(R.id.widget_weekofday,
				DateUtils.getCurrentWeekOfDay());
		views.setTextViewText(R.id.widget_day, DateUtils.getCurrentDay());

		setupClickIntents(androidContext, views);
		populateCurriculumLayout(androidContext, views);

		AppWidgetManager.getInstance(androidContext).updateAppWidget(
				appWidgetId, views);
	}

	/**
	 * Convenience method for creating an onClickPendingIntent that launches
	 * another activity directly.
	 * 
	 * @param views
	 *            The RemoteViews we're inflating
	 * @param buttonId
	 *            the id of the button view
	 * @param intent
	 *            The intent to be used when launching the activity
	 */
	private void setActivityIntent(android.content.Context androidContext,
			RemoteViews views, int buttonId, Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // just in case intent
														// comes without it
		PendingIntent pendingIntent = PendingIntent.getActivity(androidContext,
				0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(buttonId, pendingIntent);
	}

	private void setupClickIntents(android.content.Context androidContext,
			RemoteViews views) {
			Intent preIntent = new Intent(androidContext,
					CurriculumWidgetProvider_4x2.class);
			preIntent.setAction("net.basilwang.widget.previous");
			PendingIntent prePendingIntent = PendingIntent.getBroadcast(
					androidContext, 0, preIntent, 0);
			views.setOnClickPendingIntent(R.id.widget_imgv_prev_verse,
					prePendingIntent);

			Intent nextIntent = new Intent(androidContext,
					CurriculumWidgetProvider_4x2.class);
			nextIntent.setAction("net.basilwang.widget.next");
			PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
					androidContext, 0, nextIntent, 0);
			views.setOnClickPendingIntent(R.id.widget_imgv_next_verse,
					nextPendingIntent);
		
	}

	private void populateCurriculumLayout(Context androidContext,
			RemoteViews views) {

		Log.d(TAG, "curriculumList size is " + curriculumList.size());
		Log.d(TAG, "pageIndex is " + pageIndex);
		for (int itemIndex = pageIndex * ENTRIES; itemIndex < (pageIndex + 1) * ENTRIES; itemIndex++) {
			//2012-12-10 basilwang we use the same id for 0 and 3, 1 and 4
			int viewItemIndex=itemIndex%ENTRIES;
			
			String curriculumWeek;
			String dayofweek;
			Curriculum curriculum = null;
			if(itemIndex<curriculumList.size())
			{
				curriculum = curriculumList.get(itemIndex);
				curriculumWeek=curriculum.getName();
				dayofweek = CurriculumUtils.formatCurriculumIndex(
						curriculum.getCurriculumIndex(), curriculum.getTimeSpan());
				curriculumWeek = CurriculumUtils.substrCurriculum(curriculumWeek);
			}
			else
			{
				dayofweek="";
				curriculumWeek="";
			}
			
			
			int entryId = updateBackground(androidContext, views, curriculum,
					viewItemIndex);
			int descriptionViewId = updateDescription(androidContext, views,
					dayofweek, viewItemIndex);
			int projectViewId = updateProject(androidContext, views,
					curriculumWeek, viewItemIndex);
			updateContexts(androidContext, views, viewItemIndex);
		}
	}

	private boolean isPageIndexCanPlus() {
		int curriculumSize = curriculumList.size();
		int totalPageCount=(curriculumSize%ENTRIES==0)?curriculumSize%ENTRIES : ((curriculumSize%ENTRIES)+1); 
		Log.d(TAG, "onplus totalPageCount " + totalPageCount);
		if ( (pageIndex + 1) <= totalPageCount-1)
			return true;
		else
			return false;

	}

	private boolean isPageIndexCanMinus() {
		if (pageIndex >= 1)
			return true;
		else
			return false;
	}

	private int updateBackground(android.content.Context androidContext,
			RemoteViews views, Curriculum curriculum, int taskCount) {
		int entryId = getIdIdentifier(androidContext, "entry_" + taskCount);
		if (entryId != 0) {
			int drawableId = R.drawable.list_selector_background;

			drawableId = R.drawable.task_incomplete_selector;

			views.setInt(entryId, "setBackgroundResource", drawableId);
		}
		return entryId;
	}

	private int updateDescription(android.content.Context androidContext,
			RemoteViews views, String curriculum, int taskCount) {
		Log.d(TAG, "curriculum rawinfo is " + curriculum);
		int descriptionViewId = getIdIdentifier(androidContext, "description_"
				+ taskCount);
		if (descriptionViewId != 0) {
			views.setTextViewText(descriptionViewId,
					curriculum != null ? String.valueOf(curriculum) : "");
		}

		return descriptionViewId;
	}

	private void updateContexts(android.content.Context androidContext,
			RemoteViews views, int taskCount) {
		if (mBitmapProvider == null) {
			mBitmapProvider = new ContextBitmapProvider(androidContext);
		}
		Log.d(TAG, "androidContext-->>" + androidContext.toString());
		Log.d(TAG, "taskCount->>" + taskCount);
		int contextViewId = getIdIdentifier(androidContext, "contextColour_"
				+ taskCount);
		Log.d(TAG, "contextViewId" + contextViewId);
		Bitmap bitmap = mBitmapProvider.getBitmapForContexts(taskCount);
		views.setImageViewBitmap(contextViewId, bitmap);
	}

	private int updateProject(android.content.Context androidContext,
			RemoteViews views, String curriculum, int taskCount) {
		Log.d(TAG, "curriculum rawinfo is " + curriculum);
		int projectViewId = getIdIdentifier(androidContext, "project_"
				+ taskCount);
		if (projectViewId != 0) {
			views.setTextViewText(projectViewId,
					curriculum != null ? curriculum : "");
		}

		return projectViewId;
	}

	private List<Curriculum> getCurriculumList() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// 2012-11-21 basilwang sunday is 1 in java calender class while 7 in db
		int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7;
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				context).getString(CURRICULUM_TO_SHOW, "");
		int accountId = PreferenceManager.getDefaultSharedPreferences(context)
				.getInt(LOGON_ACCOUNT_ID, 0);
		return this.curriculumService.getCurriculumListByDay(semesterValue,
				dayOfWeek, accountId);
	}

	static int getIdIdentifier(android.content.Context context, String name) {
		Integer id = sIdCache.get(name);
		Log.d(TAG, "getIdIdentifier--> id:" + id);
		if (id == null) {
			id = getIdentifier(context, name, cIdType);
			Log.d(TAG, "getIdIdentifier--> id2:" + id);
			if (id == 0)
				return id;
			sIdCache.put(name, id);
		}
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Got id " + id + " for resource " + name);
		}
		return id;
	}

	static int getIdentifier(android.content.Context context, String name,
			String type) {
		int id = context.getResources().getIdentifier(name, type, cPackage);
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "Got id " + id + " for resource " + name);
		}
		return id;
	}

	private int getWeekSpan(Context androidContext) {
		Time todayTime = new Time();
		todayTime.setToNow();
		int mWeekDay = todayTime.weekDay;
		// 2012-12-01 basilwang load from db
		Semester semester = new SemesterService(androidContext)
				.getSemesterByName(PreferenceUtils
						.getPreferSemester(androidContext));

		Time showFromTime = new Time();
		showFromTime.setToNow();
		long millis = semester.getBeginDate();
		if (millis != 0) {
			showFromTime.set(millis);
		}
		int weekSpan = net.basilwang.utils.DateUtils.getWeekSpan(showFromTime,
				todayTime);
		return weekSpan;
	}

}
