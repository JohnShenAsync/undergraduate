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

package net.basilwang.dao;

import net.basilwang.config.SAXParse;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {
	private static final String cTag = "Preferences";

	public static final String FIRST_TIME = "first_time";
	public static final String ANALYTICS_ENABLED = "send_analytics";

	public static final String LAST_VERSION = "last_version";

	public static final String TOP_LEVEL_COUNTS_KEY = "top_level_counts";
	public static final String CALENDAR_ID_KEY = "calendar_id";
	public static final String DEFAULT_REMINDER_KEY = "default_reminder";

	public static final String LOGON_URL = "logon_url";
	public static final String LOGON_USER = "logon_user";
	public static final String LOGON_ACCOUNT_ID = "logon_account_id";
	public static final String LOGON_PASSWORD = "logon_password";
	public static final String LOGON_SELF_SIGNED_CERT = "logon_self_signed_cert";
	public static final String LOGON_INTERVAL = "logon_interval";

	public static final String GOOGLE_AUTH_COOKIE = "authCookie";
	public static final String GOOGLE_ACCOUNT_NAME = "accountName";
	public static final String GOOGLE_DEVICE_REGISTRATION_ID = "deviceRegistrationID";
	public static final String NOTIFICATION_ID = "notificationId";

	public static final String WIDGET_QUERY_PREFIX = "widget_query_";
	public static final String WIDGET_PROJECT_ID_PREFIX = "widget_projectId_";
	public static final String WIDGET_CONTEXT_ID_PREFIX = "widget_contextId_";

	public static final String ABOUT_US = "about_us";
	public static final String SHAREONWEIBO = "shareonweibo";
	public static final String LOGON_ADD_PREFERENCES = "logon_add_preferences";
	public static final String LOGON_PREFERENCES = "logon_preferences";
	public static final String LOGON_ACCOUNT_PREFERENCES = "logon_account_preferences";
	public static final String CEMESTER_START_PREFERENCES = "cemester_start_preferences";
	public static final String CEMESTER_END_PREFERENCES = "cemester_end_preferences";
	public static final String CEMESTER_WEEKS_NUM = "cemester_weeks_num";
	public static final String CEMESTER_INDEX_PREFERENCES = "curr_cemester_index_preferences";
	public static final String SCORE_CEMESTER_INDEX_PREFERENCES = "score_cemester_index_preferences";
	public static final String WEEKVIEW_ENABLED = "weekview_enabled";
	public static final String WEEKVIEW_UNLOCKED_STATUS = "weekview_unlocked_status";
	public static final String SCORE_ENABLED = "score_enabled";
	public static final String SCORE_UNLOCKED_STATUS = "score_unlocked_status";
	public static final String CLOSE_AD = "close_ad";
	public static final String CLOSE_AD_STATUS = "close_ad_status";
	public static final String CURRICULUM_TO_SHOW = "curriculum_to_show";
	public static final String CURRICULUM_TO_DOWNLOAD = "curriculum_semester_name";
	public static final String ENTRACETEAR = "entranceyear";
	public static final String SCORE_SEMESTER_NAME = "score_semester_name";
	public static final String SCORE_SPINNER_SELECTION = "score_spinner_selection";
	public static final String SCORE_TIP_SHOW = "score_tip_show";
	public static final String CURRICULUM_TIP_SHOW = "curriculum_tip_show";
	public static final String WEEK_VIEW_TIP_SHOW = "week_view_tip_show";
	public static final String SCHOOLMAP_TIP_SHOW = "schoolmap_tip_show";
	public static final String SOCRE_DOWNLOAD_TIP = "score_download_tip";
	public static final String CURRICULUM_DOWNLOAD_TIP = "curriculum_download_tip";
	public static final String RU_GUO_ZHAI = "ruguozhai";

	public static final String NEVER_OCCUR_UPDATE_TIP = "neverOccurTip";
	public static final String LAST_UPDATE_TIME = "lastUpdateTime";
	public static final String TOKEN = "myToken";
	public static final String HAD_SEND_USERNO = "hadSendUserNo";
	public static final String USER_NAME = "username";
	public static final String Unusual="isUnusual";


	public static boolean validateTracksSettings(Context context) {
		String url = getTracksUrl(context);
		String password = getTracksPassword(context);
		String user = getTracksUser(context);
		return user.length() != 0 && password.length() != 0
				&& url.length() != 0;
	}

	public static int getEntranceyear(Context context) {
		return getSharedPreferences(context).getInt(ENTRACETEAR, 0);
	}

	public static int getTracksInterval(Context context) {
		return getSharedPreferences(context).getInt(LOGON_INTERVAL, 0);
	}

	public static int getLastVersion(Context context) {
		return getSharedPreferences(context).getInt(LAST_VERSION, 0);
	}

	public enum DeleteCompletedPeriod {
		hourly, daily, weekly, never
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static boolean isFirstTime(Context context) {
		return getSharedPreferences(context).getBoolean(FIRST_TIME, true);
	}

	public static boolean isAnalyticsEnabled(Context context) {
		return getSharedPreferences(context)
				.getBoolean(ANALYTICS_ENABLED, true);
	}

	public static boolean isWeekViewUnlocked(Context context) {
		return getSharedPreferences(context).getBoolean(
				WEEKVIEW_UNLOCKED_STATUS, false);
	}

	public static boolean isScoreUnlocked(Context context) {
		return getSharedPreferences(context).getBoolean(SCORE_UNLOCKED_STATUS,
				false);
	}

	public static boolean isAdClosed(Context context) {
		return getSharedPreferences(context).getBoolean(CLOSE_AD_STATUS, false);
	}

	// 2012-09-29 basilwang add url from taconfig
	public static String getTracksUrl(Context context) {
		return getSharedPreferences(context).getString(
				LOGON_URL,
				SAXParse.getTAConfiguration().getSelectedCollege().getServers()
						.get(0).getIp());
	}

	public static String getTracksUser(Context context) {
		return getSharedPreferences(context).getString(LOGON_USER, "");
	}

	public static String getTracksPassword(Context context) {
		return getSharedPreferences(context).getString(LOGON_PASSWORD, "");
	}

	public static int getNotificationId(Context context) {
		return getSharedPreferences(context).getInt(NOTIFICATION_ID, 0);
	}

	public static void incrementNotificationId(Context context) {
		int notificationId = getNotificationId(context);
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putInt(NOTIFICATION_ID, ++notificationId % 32);
		editor.commit();
	}

	public static String getGoogleAuthCookie(Context context) {
		return getSharedPreferences(context)
				.getString(GOOGLE_AUTH_COOKIE, null);
	}

	public static String getGoogleAccountName(Context context) {
		return getSharedPreferences(context).getString(GOOGLE_ACCOUNT_NAME,
				null);
	}

	public static String getGooglDeviceRegistrationId(Context context) {
		return getSharedPreferences(context).getString(
				GOOGLE_DEVICE_REGISTRATION_ID, null);
	}

	public static Boolean isTracksSelfSignedCert(Context context) {
		return getSharedPreferences(context).getBoolean(LOGON_SELF_SIGNED_CERT,
				false);
	}

	public static int getDefaultReminderMinutes(Context context) {
		String durationString = getSharedPreferences(context).getString(
				Preferences.DEFAULT_REMINDER_KEY, "0");
		return Integer.parseInt(durationString);
	}

	public static int[] getTopLevelCounts(Context context) {
		String countString = getSharedPreferences(context).getString(
				Preferences.TOP_LEVEL_COUNTS_KEY, null);
		int[] result = null;
		if (countString != null) {
			String[] counts = countString.split(",");
			result = new int[counts.length];
			for (int i = 0; i < counts.length; i++) {
				result[i] = Integer.parseInt(counts[i]);
			}
		}
		return result;
	}

	public static int getCalendarId(Context context) {
		int id = 1;
		String calendarIdStr = getSharedPreferences(context).getString(
				CALENDAR_ID_KEY, null);
		if (calendarIdStr != null) {
			try {
				id = Integer.parseInt(calendarIdStr, 10);
			} catch (NumberFormatException e) {
				Log.e(cTag, "Failed to parse calendar id: " + e.getMessage());
			}
		}
		return id;
	}

	public static String getWidgetQueryKey(int widgetId) {
		return WIDGET_QUERY_PREFIX + widgetId;
	}

	public static String getWidgetProjectIdKey(int widgetId) {
		return WIDGET_PROJECT_ID_PREFIX + widgetId;
	}

	public static String getWidgetContextIdKey(int widgetId) {
		return WIDGET_CONTEXT_ID_PREFIX + widgetId;
	}

	public static String getWidgetQuery(Context context, String key) {
		return getSharedPreferences(context).getString(key, null);
	}

	public static SharedPreferences.Editor getEditor(Context context) {
		return getSharedPreferences(context).edit();
	}

}
