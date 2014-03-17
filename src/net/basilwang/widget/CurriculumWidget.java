/*
 * Copyright (C) 2011 The Android Open Source Project
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.basilwang.R;
import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import net.basilwang.utils.CurriculumUtils;
import net.basilwang.utils.DateUtils;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


/**
 * The task widget.
 * <p><em>NOTE</em>: All methods must be called on the UI thread so synchronization is NOT required
 * in this class)
 */
@SuppressLint({ "NewApi" })
public class CurriculumWidget implements RemoteViewsService.RemoteViewsFactory{
    public static final String TAG = "CurriculumWidget";

    private static String sContentsSnippetDivider;
    private static int sProjectFontSize;
    private static int sContentsFontSize;
    private static int sDefaultTextColor;
    private static int sLightTextColor;

    private final Context mContext;
    private final AppWidgetManager mWidgetManager;

    private int mWidgetId;

    ContextBitmapProvider mBitmapProvider;
    CurriculumService curriculumService;
    List<Curriculum> curriculumList;
    public CurriculumWidget(Context context) {
        super();
        mContext = context.getApplicationContext();
        mWidgetManager = AppWidgetManager.getInstance(mContext);
        mBitmapProvider = new ContextBitmapProvider(mContext);
        curriculumService=new CurriculumService(context);
        curriculumList=getCurriculumList();
		
        if (sContentsSnippetDivider == null) {
            // Initialize string, color, dimension resources
            Resources res = mContext.getResources();
            sContentsSnippetDivider = " - ";
            sProjectFontSize = res.getDimensionPixelSize(R.dimen.widget_project_font_size);
            sContentsFontSize = res.getDimensionPixelSize(R.dimen.widget_contents_font_size);
            sDefaultTextColor = res.getColor(R.color.widget_default_text_color);
            sLightTextColor = res.getColor(R.color.widget_light_text_color);
        }
    }
    private List<Curriculum> getCurriculumList() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		//2012-11-21 basilwang  sunday is 1 in java calender class while 7 in db
		int dayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - 1 + 7 )% 7;
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				mContext).getString(CURRICULUM_TO_SHOW, "");
		int accountId = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getInt(LOGON_ACCOUNT_ID, 0);
		return this.curriculumService.getCurriculumListByDay(semesterValue,
				dayOfWeek, accountId);
	}
    public void setWidgetId(int widgetId) {
        mWidgetId = widgetId;
    }

    /**
     * Start loading the data.  At this point nothing on the widget changes -- the current view
     * will remain valid until the loader loads the latest data.
     */
    public void start() {
    	updateHeader();
    }

    /**
     * Resets the data in the widget and forces a reload.
     */
    public void reset() {
        //mLoader.reset();
        start();
    }
    private void setupWeekDay(RemoteViews views) {
    	views.setTextViewText(R.id.widget_weekofday, DateUtils.getCurrentWeekOfDay());
        views.setTextViewText(R.id.widget_day, DateUtils.getCurrentDay());
    }
    /**
     * Update the "header" of the widget (i.e. everything that doesn't include the scrolling
     * task list)
     */
    private void updateHeader() {
        Log.d(TAG, "#updateHeader(); widgetId: " + mWidgetId);

        // Get the widget layout
        RemoteViews views =
                new RemoteViews(mContext.getPackageName(), R.layout.widget_2x2);

        // Set up the list with an adapter
        Intent intent = new Intent(mContext, CurriculumWidgetProviderV11.WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.task_list, intent);

        setupWeekDay(views);

        mWidgetManager.updateAppWidget(mWidgetId, views);
    }

    /**
     * Add size and color styling to text
     *
     * @param text the text to style
     * @param size the font size for this text
     * @param color the color for this text
     * @return a CharSequence quitable for use in RemoteViews.setTextViewText()
     */
    private CharSequence addStyle(CharSequence text, int size, int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(
                new AbsoluteSizeSpan(size), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (color != 0) {
            builder.setSpan(new ForegroundColorSpan(color), 0, text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * Create styled text for our combination subject and snippet
     *
     * @param subject the message's subject (or null)
     * @param snippet the message's snippet (or null)
     * @param read whether or not the message is read
     * @return a CharSequence suitable for use in RemoteViews.setTextViewText()
     */
    private CharSequence getStyledContents(String subject, String snippet, boolean read) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        boolean hasSubject = false;
        if (!TextUtils.isEmpty(subject)) {
            SpannableString ss = new SpannableString(subject);
            ss.setSpan(new StyleSpan(read ? Typeface.NORMAL : Typeface.BOLD), 0, ss.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(sDefaultTextColor), 0, ss.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);
            hasSubject = true;
        }
        if (!TextUtils.isEmpty(snippet)) {
            if (hasSubject) {
                ssb.append(sContentsSnippetDivider);
            }
            SpannableString ss = new SpannableString(snippet);
            ss.setSpan(new ForegroundColorSpan(sLightTextColor), 0, snippet.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(ss);
        }
        return addStyle(ssb, sContentsFontSize, 0);
    }

    @Override
    public RemoteViews getViewAt(int position) {
    	Log.d(TAG,"getview called:" + position);
        RemoteViews views =
            new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        int drawableId = R.drawable.task_complete_selector;
        drawableId = R.drawable.task_incomplete_selector;
        views.setInt(R.id.widget_task, "setBackgroundResource", drawableId);

        Curriculum curriculum = curriculumList.get(position);
		String curriculumEvenWeek=curriculum.getName().split("\\;")[0];
		curriculumEvenWeek+=CurriculumUtils.formatCurriculumIndex(curriculum.getCurriculumIndex(),curriculum.getTimeSpan());
		curriculumEvenWeek=CurriculumUtils.substrCurriculum(curriculumEvenWeek);
		String curriculumOddWeek="";
		if(curriculum.getName().split("\\;").length==2)
		{
			curriculumOddWeek=curriculum.getName().split("\\;")[1];
			Log.d(TAG, "curriculumOddWeek str is "+curriculumOddWeek);
			curriculumOddWeek+=CurriculumUtils.formatCurriculumIndex(curriculum.getCurriculumIndex(),curriculum.getTimeSpan());
			Log.d(TAG, "curriculumOddWeek str is "+curriculumOddWeek);
			curriculumOddWeek=CurriculumUtils.substrCurriculum(curriculumOddWeek);
			Log.d(TAG, "curriculumOddWeek str is "+curriculumOddWeek);
		}
        SpannableStringBuilder projectBuilder = new SpannableStringBuilder(curriculumEvenWeek);
        projectBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0,
                projectBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence styledProject = addStyle(projectBuilder, sProjectFontSize, sDefaultTextColor);
        views.setTextViewText(R.id.widget_project, styledProject);


        CharSequence contents = getStyledContents(curriculumOddWeek, null, true);
        views.setTextViewText(R.id.widget_contents, contents);


//        views.setImageViewBitmap(R.id.widget_context,
//				mBitmapProvider.getBitmapForContexts(curriculum));


//        TaskSelector selector = mListContext.createSelectorWithPreferences(mContext);
//        String queryName = selector.getListQuery().name();
//        String contextId = String.valueOf(selector.getContextId().getId());
//        String projectId = String.valueOf(selector.getProjectId().getId());
//        
//        setFillInIntent(views, R.id.widget_task, COMMAND_URI_VIEW_TASK,
//                queryName, contextId, projectId, String.valueOf(position));

        return views;
    }

    @Override
    public int getCount() {
        return curriculumList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_loading);
        view.setTextViewText(R.id.loading_text, mContext.getString(R.string.widget_loading));
        return view;
    }

    @Override
    public int getViewTypeCount() {
        // Regular list view and the "loading" view
        return 2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
    }

    public void onDeleted() {
        Log.d(TAG, "#onDeleted(); widgetId: " + mWidgetId);

//        if (mLoader != null) {
//            mLoader.reset();
//        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "#onCreate(); widgetId: " + mWidgetId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "#onDestroy(); widgetId: " + mWidgetId);

//        if (mLoader != null) {
//            mLoader.reset();
//        }
    }

    @Override
    public String toString() {
    	 return "";
//        return "View=" + mListContext.toString();
    }
}
