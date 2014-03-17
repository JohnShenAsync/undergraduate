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

import java.io.FileDescriptor;
import java.io.PrintWriter;

import android.annotation.TargetApi;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.widget.RemoteViewsService;
public class CurriculumWidgetProviderV11 extends AppWidgetProvider {
	private WidgetManager mWidgetManager=new WidgetManager();
    public void onUpdate(android.content.Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	super.onUpdate(context, appWidgetManager, appWidgetIds);
        mWidgetManager.updateWidgets(context, appWidgetIds);
    }

    @Override
    public void onDeleted(android.content.Context context, int[] appWidgetIds) {
    	 mWidgetManager.deleteWidgets(context, appWidgetIds);
         super.onDeleted(context, appWidgetIds);
    }

    /**
     * We use the WidgetService for two purposes:
     *  1) To provide a widget factory for RemoteViews, and
     *  2) Catch our command Uri's (i.e. take actions on user clicks) and let TaskWidget
     *     handle them.
     */
    @TargetApi(11)
	public static class WidgetService extends RemoteViewsService {

        //@Inject
        //private WidgetManager mWidgetManager;
        private WidgetManager mWidgetManager=new WidgetManager();
        @Override
        public void onCreate() {
//            final Injector injector = RoboGuice.getInjector(this);
//            injector.injectMembers(this);

            super.onCreate();
        }

        @Override
        public void onDestroy() {
        	 super.onDestroy();
//            try {
//                RoboGuice.destroyInjector(this);
//            } finally {
//                super.onDestroy();
//            }
        }

        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (widgetId == -1) return null;
            // Find the existing widget or create it
            return mWidgetManager.getOrCreateWidget(this, widgetId);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
//            if (intent.getData() != null) {
//                // TaskWidget creates intents, so it knows how to handle them.
//                TaskWidget.processIntent(this, intent);
//            }
            return Service.START_NOT_STICKY;
        }

        @Override
        protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
            WidgetManager.dump(fd, writer, args);
        }
    }
}
