package net.basilwang;

import static net.basilwang.dao.Preferences.CLOSE_AD;
import static net.basilwang.dao.Preferences.CLOSE_AD_STATUS;
import static net.basilwang.dao.Preferences.WEEKVIEW_ENABLED;
import static net.basilwang.dao.Preferences.WEEKVIEW_UNLOCKED_STATUS;
import net.basilwang.dao.Preferences;
import net.youmi.android.appoffers.YoumiOffersManager;
import net.youmi.android.appoffers.YoumiPointsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AlertDialogFactory {

	public static Builder getYoumiOfferDialog(final Activity activity,
			final CheckBoxPreference checkBoxPreference) {
		String prefix = null;
		Builder builder = new AlertDialog.Builder(activity);
		String closeAd = activity.getResources().getString(R.string.close_ad);
		String logOff = activity.getResources().getString(R.string.log_off);
		final String checkBoxPreferenceKey = checkBoxPreference.getKey();
		if (checkBoxPreferenceKey.equals(WEEKVIEW_ENABLED)) {
			prefix = "周视图解锁";
			builder.setTitle(Html.fromHtml(logOff));

		}
		if (checkBoxPreferenceKey.equals(CLOSE_AD)) {
			prefix = "关闭广告";
			builder.setTitle(Html.fromHtml(closeAd));

		}
//		builder.setTitle(prefix);
		final String successMsg = prefix + "成功";
		final String failedMsg = prefix + "失败,请重试";
		checkBoxPreference.setChecked(false);
		// 装载/res/layout/login.xml界面布局
		View viewUnlock = (View) activity.getLayoutInflater().inflate(
				R.layout.week_view_unlock, null);
		TextView message = (TextView) viewUnlock.findViewById(R.id.title);
		message.setText(prefix + "需要30积分，您现在有"
				+ YoumiPointsManager.queryPoints(activity)
				+ "积分,点击赚取更多积分按钮马上免费获得足够积分");
		builder.setView(viewUnlock);
		builder.setPositiveButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.setNeutralButton("赚取更多积分", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// YoumiPointsManager.awardPoints(
				// MyPreferenceActivity.this, 10);
				YoumiOffersManager.showOffers(activity,
						YoumiOffersManager.TYPE_REWARD_OFFERS);
			}
		});
		builder.setNegativeButton(prefix, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int score = YoumiPointsManager.queryPoints(activity);
				if (score >= 30) {
					if (YoumiPointsManager.spendPoints(activity, 30)) {

						checkBoxPreference.setChecked(true);
						checkBoxPreference.setEnabled(false);

						SharedPreferences.Editor ed = Preferences
								.getEditor(activity);
						// 2013-01-23 basilwang fix a severity bug in 2.0 that
						// can't close ad cause I don't save the status at all,
						// damn it
						if (checkBoxPreferenceKey.equals(WEEKVIEW_ENABLED)) {
							ed.putBoolean(WEEKVIEW_UNLOCKED_STATUS, true);
						}
						if (checkBoxPreferenceKey.equals(CLOSE_AD)) {
							ed.putBoolean(CLOSE_AD_STATUS, true);

						}
						ed.commit();
						Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT)
								.show();
					} else {

						Toast.makeText(activity, failedMsg, Toast.LENGTH_SHORT)
								.show();
					}

				} else {

					Toast.makeText(activity, "对不起，您的积分不够，请尝试赚取更多积分",
							Toast.LENGTH_SHORT).show();

				}
			}
		});
		return builder;
	}
}
