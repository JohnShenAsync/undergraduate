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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.util.Log;

/**
 * Class that maintains references to all widgets.
 */
public class WidgetManager {
    private static final String TAG = "WidgetManager";

    // Widget ID -> Widget
    private final static Map<Integer, CurriculumWidget> mWidgets =
            new ConcurrentHashMap<Integer, CurriculumWidget>();

    //private ContextScopedProvider<CurriculumWidget> mTaskWidgetProvider;

    public synchronized void createWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            getOrCreateWidget(context, widgetId);
        }
    }

    public synchronized void deleteWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            // Find the widget in the map
            final CurriculumWidget widget = get(widgetId);
            if (widget != null) {
                // Stop loading and remove the widget from the map
                widget.onDeleted();
            }
            remove(context, widgetId);
        }
        
    }

    public synchronized void updateWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            // Find the widget in the map
            final CurriculumWidget widget = get(widgetId);
            if (widget != null) {
                widget.reset();
            } else {
                getOrCreateWidget(context, widgetId);
            }
        }
    }

    public synchronized CurriculumWidget getOrCreateWidget(Context context, int widgetId) {
        CurriculumWidget widget = get(widgetId);
        if (widget == null) {
            Log.d(TAG, "Create email widget; ID: " + widgetId);
            widget = new CurriculumWidget(context);
            widget.setWidgetId(widgetId);
            put(widgetId, widget);
            widget.start();
        }
        return widget;
    }

    private CurriculumWidget get(int widgetId) {
        return mWidgets.get(widgetId);
    }

    private void put(int widgetId, CurriculumWidget widget) {
        mWidgets.put(widgetId, widget);
    }

    private void remove(Context context, int widgetId) {
        mWidgets.remove(widgetId);
        //WidgetManager.removeWidgetPrefs(context, widgetId);
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        int n = 0;
        for (CurriculumWidget widget : mWidgets.values()) {
            writer.println("Widget #" + (++n));
            writer.println("    " + widget.toString());
        }
    }

//    /** Saves shared preferences for the given widget */
//    static void saveWidgetPrefs(Context context, int appWidgetId, TaskListContext listContext) {
//        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
//        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
//        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
//        TaskSelector selector = listContext.createSelectorWithPreferences(context);
//        Preferences.getEditor(context).
//                putString(queryKey, listContext.getListQuery().name()).
//                putLong(contextIdKey, selector.getContextId().getId()).
//                putLong(projectIdKey, selector.getProjectId().getId()).
//                commit();
//    }
//
//    /** Removes shared preferences for the given widget */
//    static void removeWidgetPrefs(Context context, int appWidgetId) {
//        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
//        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
//        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
//        SharedPreferences.Editor editor = Preferences.getEditor(context);
//        editor.remove(queryKey).
//                remove(contextIdKey).
//                remove(projectIdKey).
//                apply(); // just want to clean up; don't care when preferences are actually removed
//    }
//
//    /**
//     * Returns the saved list context for the given widget.
//     */
//    static TaskListContext loadListContextPref(Context context, int appWidgetId) {
//        TaskListContext listContext = null;
//        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
//        Id contextId = Preferences.getWidgetId(context, contextIdKey);
//        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
//        Id projectId = Preferences.getWidgetId(context, projectIdKey);
//        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
//        String queryName = Preferences.getWidgetQuery(context, queryKey);
//        if (queryName != null) {
//            ListQuery query;
//            try {
//                query = ListQuery.valueOf(queryName);
//                listContext = TaskListContext.create(query, contextId, projectId);
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to parse key " + queryName);
//                // default to next tasks when can't parse key
//                query = ListQuery.nextTasks;
//                contextId = projectId = Id.NONE;
//                listContext = TaskListContext.create(query, contextId, projectId);
//                saveWidgetPrefs(context, appWidgetId, listContext);
//            }
//        }
//        return listContext;
//    }

}
