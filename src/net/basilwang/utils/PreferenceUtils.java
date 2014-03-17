package net.basilwang.utils;

import net.basilwang.dao.Preferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

	public static String getPreferSemester(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.CURRICULUM_TO_SHOW, "");
	}

	public static int getPreferAccountId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				Preferences.LOGON_ACCOUNT_ID, 0);
	}

	public static Boolean getPreferUpdateTip(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(Preferences.NEVER_OCCUR_UPDATE_TIP, false);
	}

	public static Boolean getPreferHadSendUserNo(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(Preferences.HAD_SEND_USERNO, false);
	}

	public static String getPreferLastVersion(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.LAST_VERSION, "1.00");
	}

	public static String getPreferUserName(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.USER_NAME, "绑定学号,亲");
	}

	public static String getPreferToken(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.TOKEN, null);
	}

	public static int getPreferenceUnusual(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getInt(Preferences.Unusual, 0);
	}
	public static void modifyIntValueInPreferences(Context context, String key,
			int value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void modifyStringValueInPreferences(Context context,
			String key, String value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void modifyBooleanValueInPreferences(Context context,
			String key, Boolean value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
