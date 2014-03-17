package net.basilwang;

import static net.basilwang.dao.Preferences.ABOUT_US;
import static net.basilwang.dao.Preferences.CEMESTER_END_PREFERENCES;
import static net.basilwang.dao.Preferences.CEMESTER_START_PREFERENCES;
import static net.basilwang.dao.Preferences.CEMESTER_WEEKS_NUM;
import static net.basilwang.dao.Preferences.CLOSE_AD;
import static net.basilwang.dao.Preferences.CLOSE_AD_STATUS;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ADD_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_PREFERENCES;
import static net.basilwang.dao.Preferences.SHAREONWEIBO;
import static net.basilwang.dao.Preferences.WEEKVIEW_ENABLED;

import java.util.List;

import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.entity.Account;
import net.basilwang.utils.NetworkUtils;
import net.youmi.android.appoffers.CheckStatusNotifier;
import net.youmi.android.appoffers.YoumiOffersManager;
import net.youmi.android.appoffers.YoumiPointsManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class MyPreferenceActivity extends SherlockPreferenceActivity implements
		OnPreferenceClickListener, CheckStatusNotifier {
	static final String TAG = "MyPreferenceActivity";
	private PreferenceCategory logonPreference;
	private PreferenceScreen logonAddPreference, cemesterStartPreference,
			cemesterEndPreference;
	private PreferenceScreen aboutusPreference;
	private PreferenceScreen shareOnWeiboPreference;
	// un used fields
	// private CheckBoxPreference weekViewCheckboxPreference;
	// private CheckBoxPreference scoreCheckboxPreference;
	private CheckBoxPreference adCheckboxPreference;
	private Time mShowFromTime;
	private Time mDueTime;
	SubMenu subMenuForNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setHomeButtonEnabled(true);
		mShowFromTime = new Time();
		mDueTime = new Time();

		logonAddPreference = (PreferenceScreen) findPreference(LOGON_ADD_PREFERENCES);
		logonAddPreference.setOnPreferenceClickListener(this);
		logonPreference = (PreferenceCategory) findPreference(LOGON_PREFERENCES);

		cemesterStartPreference = (PreferenceScreen) findPreference(CEMESTER_START_PREFERENCES);
		cemesterEndPreference = (PreferenceScreen) findPreference(CEMESTER_END_PREFERENCES);
		aboutusPreference = (PreferenceScreen) findPreference(ABOUT_US);
		shareOnWeiboPreference = (PreferenceScreen)findPreference(SHAREONWEIBO);

		// weekViewCheckboxPreference = (CheckBoxPreference)
		// findPreference(WEEKVIEW_ENABLED);
		// scoreCheckboxPreference = (CheckBoxPreference)
		// findPreference(SCORE_ENABLED);
		adCheckboxPreference = (CheckBoxPreference) findPreference(CLOSE_AD);

		// 2012-09-26 basilwang if weekview already enabled, we set
		// WeekViewCheckboxPreference enable status is false
		// if (Preferences.isWeekViewUnlocked(this)) {
		// weekViewCheckboxPreference.setChecked(true);
		// weekViewCheckboxPreference.setEnabled(false);
		// } else {
		// weekViewCheckboxPreference.setChecked(false);
		// weekViewCheckboxPreference.setEnabled(true);
		// }
		// if (Preferences.isScoreUnlocked(this)) {
		// scoreCheckboxPreference.setChecked(true);
		// scoreCheckboxPreference.setEnabled(false);
		// }
		// else
		// {
		// scoreCheckboxPreference.setChecked(false);
		// scoreCheckboxPreference.setEnabled(true);
		// }
		if (Preferences.isAdClosed(this)) {
			adCheckboxPreference.setChecked(true);
			adCheckboxPreference.setEnabled(false);
		} else {
			adCheckboxPreference.setChecked(false);
			adCheckboxPreference.setEnabled(true);
		}
		long millis = PreferenceManager.getDefaultSharedPreferences(this)
				.getLong(CEMESTER_START_PREFERENCES, 0);
		if (millis != 0) {
			mShowFromTime.set(millis);
			setDate(cemesterStartPreference, millis);
		}
		millis = PreferenceManager.getDefaultSharedPreferences(this).getLong(
				CEMESTER_END_PREFERENCES, 0);
		if (millis != 0) {
			mDueTime.set(millis);
			setDate(cemesterEndPreference, millis);
		}
		cemesterStartPreference
				.setOnPreferenceClickListener(new DateClickListener(
						mShowFromTime, cemesterStartPreference));
		cemesterEndPreference
				.setOnPreferenceClickListener(new DateClickListener(mDueTime,
						cemesterEndPreference));
		aboutusPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(MyPreferenceActivity.this,
								About_us.class);
						startActivity(intent);
						return false;
					}
				});
        
		shareOnWeiboPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(MyPreferenceActivity.this,
						About_us.class);
				startActivity(intent);
				return false;
			}
		});
		
		YoumiOffersManager.init(this, "2fc95b356bb979ae", "8b94f727980f7158");
		YoumiOffersManager.checkStatus(MyPreferenceActivity.this,
				MyPreferenceActivity.this);
		final Builder builder = new AlertDialog.Builder(this);
		adCheckboxPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						adCheckboxPreference.setChecked(false);

						builder.setTitle("关闭广告");
						// 装载/res/layout/login.xml界面布局
						View viewUnlock = (View) getLayoutInflater().inflate(
								R.layout.week_view_unlock, null);
						TextView title = (TextView) viewUnlock
								.findViewById(R.id.title);
						title.setText("关闭广告需要30积分，您现在有"
								+ YoumiPointsManager
										.queryPoints(MyPreferenceActivity.this)
								+ "积分,点击赚取更多积分按钮马上免费获得足够积分");
						builder.setView(viewUnlock);
						builder.setPositiveButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
						builder.setNeutralButton("赚取更多积分",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										// YoumiPointsManager.awardPoints(
										// MyPreferenceActivity.this, 10);
										YoumiOffersManager
												.showOffers(
														MyPreferenceActivity.this,
														YoumiOffersManager.TYPE_REWARD_OFFERS);
									}
								});
						builder.setNegativeButton("解锁功能",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										int score = YoumiPointsManager
												.queryPoints(MyPreferenceActivity.this);
										if (score >= 30) {
											if (YoumiPointsManager.spendPoints(
													MyPreferenceActivity.this,
													30)) {

												adCheckboxPreference
														.setChecked(true);
												adCheckboxPreference
														.setEnabled(false);

												SharedPreferences.Editor ed = Preferences
														.getEditor(MyPreferenceActivity.this);
												ed.putBoolean(CLOSE_AD_STATUS,
														true);
												ed.commit();
												Toast.makeText(
														MyPreferenceActivity.this,
														"关闭广告成功",
														Toast.LENGTH_SHORT)
														.show();
											} else {

												Toast.makeText(
														MyPreferenceActivity.this,
														"关闭广告失败，请重试",
														Toast.LENGTH_SHORT)
														.show();
											}

										} else {

											Toast.makeText(
													MyPreferenceActivity.this,
													"对不起，您的积分不够，请尝试赚取更多积分",
													Toast.LENGTH_SHORT).show();

										}
									}
								});
						builder.create().show();

						return false;
					}
				});
		// weekViewCheckboxPreference
		// .setOnPreferenceClickListener(new OnPreferenceClickListener() {
		//
		// @Override
		// public boolean onPreferenceClick(Preference preference) {
		//
		// weekViewCheckboxPreference.setChecked(false);
		//
		// builder.setTitle("周视图解锁");
		// // 装载/res/layout/login.xml界面布局
		// View viewUnlock = (View) getLayoutInflater().inflate(
		// R.layout.week_view_unlock, null);
		// TextView title = (TextView) viewUnlock
		// .findViewById(R.id.title);
		// title.setText("周视图解锁需要30积分，您现在有"
		// + YoumiPointsManager
		// .queryPoints(MyPreferenceActivity.this)
		// + "积分,点击赚取更多积分按钮马上免费获得足够积分");
		// builder.setView(viewUnlock);
		// builder.setPositiveButton("取消", new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		//
		// }
		// });
		// builder.setNeutralButton("赚取更多积分",
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		//
		// // YoumiPointsManager.awardPoints(
		// // MyPreferenceActivity.this, 10);
		// YoumiOffersManager
		// .showOffers(
		// MyPreferenceActivity.this,
		// YoumiOffersManager.TYPE_REWARD_OFFERS);
		// }
		// });
		// builder.setNegativeButton("解锁功能",
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// int score = YoumiPointsManager
		// .queryPoints(MyPreferenceActivity.this);
		// if (score >= 30) {
		// if (YoumiPointsManager.spendPoints(
		// MyPreferenceActivity.this,
		// 30)) {
		//
		// weekViewCheckboxPreference
		// .setChecked(true);
		// weekViewCheckboxPreference
		// .setEnabled(false);
		//
		// SharedPreferences.Editor ed = Preferences
		// .getEditor(MyPreferenceActivity.this);
		// ed.putBoolean(
		// WEEKVIEW_UNLOCKED_STATUS,
		// true);
		// ed.commit();
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "周视图解锁成功",
		// Toast.LENGTH_SHORT)
		// .show();
		// } else {
		//
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "周视图解锁失败，请重试",
		// Toast.LENGTH_SHORT)
		// .show();
		// }
		//
		// } else {
		//
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "对不起，您的积分不够，请尝试赚取更多积分",
		// Toast.LENGTH_SHORT).show();
		//
		// }
		// }
		// });
		// builder.create().show();
		//
		// return false;
		// }
		// });

		// scoreCheckboxPreference
		// .setOnPreferenceClickListener(new OnPreferenceClickListener() {
		//
		// @Override
		// public boolean onPreferenceClick(Preference preference) {
		//
		// scoreCheckboxPreference.setChecked(false);
		//
		// builder.setTitle("成绩视图解锁");
		// // 装载/res/layout/login.xml界面布局
		// View viewUnlock = (View) getLayoutInflater().inflate(
		// R.layout.week_view_unlock, null);
		// TextView title = (TextView) viewUnlock
		// .findViewById(R.id.title);
		// title.setText("成绩解锁需要30积分，您现在有"
		// + YoumiPointsManager
		// .queryPoints(MyPreferenceActivity.this)
		// + "积分");
		// builder.setView(viewUnlock);
		// builder.setPositiveButton("取消", new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// try {
		// Field field = dialog.getClass()
		// .getSuperclass()
		// .getDeclaredField("mShowing");
		// field.setAccessible(true);
		// // 设置mShowing值，欺骗android系统
		// field.set(dialog, true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
		// builder.setNeutralButton("赚取更多积分",
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// try {
		// Field field = dialog
		// .getClass()
		// .getSuperclass()
		// .getDeclaredField(
		// "mShowing");
		// field.setAccessible(true);
		// // 设置mShowing值，欺骗android系统
		// field.set(dialog, false);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// // YoumiPointsManager.awardPoints(
		// // MyPreferenceActivity.this, 10);
		// YoumiOffersManager
		// .showOffers(
		// MyPreferenceActivity.this,
		// YoumiOffersManager.TYPE_REWARD_OFFERS);
		// }
		// });
		// builder.setNegativeButton("解锁功能",
		// new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// int score = YoumiPointsManager
		// .queryPoints(MyPreferenceActivity.this);
		// if (score >= 30) {
		// if (YoumiPointsManager.spendPoints(
		// MyPreferenceActivity.this,
		// 30)) {
		// try {
		// Field field = dialog
		// .getClass()
		// .getSuperclass()
		// .getDeclaredField(
		// "mShowing");
		// field.setAccessible(true);
		// // 设置mShowing值，欺骗android系统
		// field.set(dialog, true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// scoreCheckboxPreference
		// .setChecked(true);
		// scoreCheckboxPreference
		// .setEnabled(false);
		//
		// SharedPreferences.Editor ed = Preferences
		// .getEditor(MyPreferenceActivity.this);
		// ed.putBoolean(SCORE_UNLOCKED_STATUS,
		// true);
		// ed.commit();
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "成绩视图解锁成功",
		// Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// try {
		// Field field = dialog
		// .getClass()
		// .getSuperclass()
		// .getDeclaredField(
		// "mShowing");
		// field.setAccessible(true);
		// // 设置mShowing值，欺骗android系统
		// field.set(dialog, false);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "成绩视图解锁失败，请重试",
		// Toast.LENGTH_SHORT)
		// .show();
		// }
		//
		// } else {
		// try {
		// Field field = dialog
		// .getClass()
		// .getSuperclass()
		// .getDeclaredField(
		// "mShowing");
		// field.setAccessible(true);
		// // 设置mShowing值，欺骗android系统
		// field.set(dialog, false);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Toast.makeText(
		// MyPreferenceActivity.this,
		// "对不起，您的积分不够，请尝试赚取更多积分",
		// Toast.LENGTH_SHORT).show();
		//
		// }
		// }
		// });
		// builder.create().show();
		//
		// return false;
		// }
		// });
		checkNetwork();
		reloadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// menu.add("Search")
		// .setIcon( R.drawable.ic_search)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// menu.add("Refresh")
		// .setIcon( R.drawable.ic_refresh)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// SubMenu sub = menu.addSubMenu("登录");
		// sub.add(0, R.style.Theme_Sherlock, 0, R.string.weekview);
		// sub.add(0, R.style.Theme_Sherlock_Light, 0, R.string.dayview);
		// // sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0,
		// // "Light (Dark Action Bar)");
		// sub.getItem().setShowAsAction(
		// MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		subMenuForNetwork = menu.addSubMenu(R.string.networkavailable);
		subMenuForNetwork.add(0, R.style.Theme_Sherlock, 0,
				R.string.checknetwork);
		subMenuForNetwork.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		subMenuForNetwork.getItem().setVisible(true);

		// subMenuWithoutNetwork = menu.addSubMenu(R.string.nonetwork);
		// subMenuWithoutNetwork.add(0, R.style.Theme_Sherlock, 0,
		// R.string.checknetwork);
		// subMenuWithoutNetwork.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		// subMenuWithoutNetwork.getItem().setVisible(false);
		checkNetwork();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Intent intent = new Intent(this, StaticAttachmentActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// 判断是哪个Preference改变了
		if (preference.getKey().equals(LOGON_ADD_PREFERENCES)) {
			Log.e(TAG, getString(R.string.logon_add_preferences));
			// if(NetworkUtils.isConnect(this))
			// {
			// 这里即使不联网，但是由于没有访问网络的动作，所以不会出问题
			Intent intent = new Intent();
			intent.setClass(this, LogonPreferenceActivity.class);
			startActivity(intent);
			// }
			// else
			// {
			// Toast.makeText(MyPreferenceActivity.this, "好像没有联网哦",
			// Toast.LENGTH_SHORT).show();
			// }

		} else if (preference.getKey().equals(LOGON_ACCOUNT_PREFERENCES)) {
			String account = preference.getTitle().toString();
			Log.e(TAG, account);
			Intent intent = new Intent();
			intent.putExtra("name", account);
			intent.setClass(this, EditLongonPreferenceActivity.class);
			startActivity(intent);

		} else if (preference.getKey().equals(CEMESTER_START_PREFERENCES)) {

		} else if (preference.getKey().equals(WEEKVIEW_ENABLED)) {

		}

		// 返回true表示允许改变
		return true;
	}

	private void checkNetwork() {
		if (NetworkUtils.isConnect(this)) {
			if (subMenuForNetwork != null)
				subMenuForNetwork.getItem().setTitle(R.string.networkavailable);
			// subMenuWithoutNetwork.getItem().setVisible(false);
		} else {
			if (subMenuForNetwork != null)
				subMenuForNetwork.getItem().setTitle(R.string.nonetwork);
		}
	}

	private void reloadData() {
		logonPreference.removeAll();
		logonPreference.addPreference(logonAddPreference);
		SharedPreferences myPrefs = this.getPreferences(MODE_PRIVATE);
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		AccountService service = new AccountService(this);
		List<Account> list = service.getAccounts();
		for (Account account : list) {
			PreferenceScreen preferenceItem = getPreferenceManager()
					.createPreferenceScreen(this);
			// CheckBoxPreference checkBoxPreference = new
			// CheckBoxPreference(this);
			// make sure each key is unique
			preferenceItem.setKey(LOGON_ACCOUNT_PREFERENCES);
			preferenceItem.setTitle(account.getName());
			if (accountId == account.getId())
				preferenceItem.setSummary(R.string.already_checked);
			// preferenceItem.setChecked(false);
			// checkBoxPreference.setDisableDependentsState(disableDependentsState)
			// checkBoxPreference.setSelectable(false);
			preferenceItem.setOrder(0);
			preferenceItem.setOnPreferenceClickListener(this);
			logonPreference.addPreference(preferenceItem);

		}
	}

	public void onResume() {
		super.onResume();
		checkNetwork();
		reloadData();
	}

	private void setDate(PreferenceScreen screen, long millis) {
		CharSequence value;
		int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
				| DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
				| DateUtils.FORMAT_ABBREV_WEEKDAY;
		value = DateUtils.formatDateTime(MyPreferenceActivity.this, millis,
				flags);
		screen.setSummary(value);
	}

	private void updateToDefault(Time displayTime) {
		displayTime.setToNow();
		displayTime.second = 0;
		int minute = displayTime.minute;
		if (minute > 0 && minute <= 30) {
			displayTime.minute = 30;
		} else {
			displayTime.minute = 0;
			displayTime.hour += 1;
		}
	}

	private class DateListener implements DatePickerDialog.OnDateSetListener {
		// View mView;
		PreferenceScreen mScreen;

		public DateListener(PreferenceScreen screen) {
			// mView = view;
			mScreen = screen;
		}

		public void onDateSet(DatePicker view, int year, int month, int monthDay) {
			// Cache the member variables locally to avoid inner class overhead.
			Time showFromTime = mShowFromTime;
			Time dueTime = mDueTime;
			Time innerTime;
			if (mScreen == cemesterStartPreference)
				innerTime = mShowFromTime;
			else
				innerTime = mDueTime;
			// Cache the show from and due millis so that we limit the number
			// of calls to normalize() and toMillis(), which are fairly
			// expensive.
			long showFromMillis;
			long dueMillis;
			long innerMillis;
			// The show from date was changed.

			if (Time.isEpoch(innerTime)) {
				// time wasn't set - set to default to pick up default time
				// values
				updateToDefault(innerTime);
			}
			// 2012-06-06 basilwang use for checking time period

			// int yearDuration = dueTime.year - showFromTime.year;
			// int monthDuration = dueTime.month - showFromTime.month;
			// int monthDayDuration = dueTime.monthDay - showFromTime.monthDay;

			innerTime.year = year;
			innerTime.month = month;
			innerTime.monthDay = monthDay;
			innerMillis = innerTime.normalize(true);

			// mShowFromDateVisible = true;

			// if (mDueDateVisible) {
			// // Also update the end date to keep the duration constant.
			// dueTime.year = year + yearDuration;
			// dueTime.month = month + monthDuration;
			// dueTime.monthDay = monthDay + monthDayDuration;
			// }

			int dueWeekNumber;
			int showFromWeekNumber;
			SharedPreferences.Editor ed = Preferences
					.getEditor(MyPreferenceActivity.this);
			if (mScreen == cemesterStartPreference) {
				// showFromTime=innerTime;
				if (Time.compare(showFromTime, dueTime) > 0) {
					dueTime.set(showFromTime.monthDay, showFromTime.month,
							showFromTime.year);
				}

			} else {
				// dueTime=innerTime;
				if (Time.compare(showFromTime, dueTime) > 0) {
					showFromTime.set(dueTime.monthDay, dueTime.month,
							dueTime.year);
				}

			}
			showFromMillis = showFromTime.normalize(true);
			dueMillis = dueTime.normalize(true);
			ed.putLong(CEMESTER_START_PREFERENCES, showFromMillis);
			ed.putLong(CEMESTER_END_PREFERENCES, dueMillis);
			dueWeekNumber = dueTime.getWeekNumber();
			showFromWeekNumber = showFromTime.getWeekNumber();
			int span = net.basilwang.utils.DateUtils.getWeekSpan(showFromTime,
					dueTime);

			Log.v(TAG, String.valueOf(dueWeekNumber));
			Log.v(TAG, String.valueOf(showFromWeekNumber));
			Log.v(TAG, String.valueOf(dueWeekNumber - showFromWeekNumber + 1));
			Log.v(TAG, String.valueOf(span));
			Toast.makeText(MyPreferenceActivity.this, String.valueOf(span),
					1000).show();

			ed.putInt(CEMESTER_WEEKS_NUM, dueWeekNumber - showFromWeekNumber
					+ 1);
			ed.commit();
			setDate(cemesterStartPreference, showFromMillis);
			setDate(cemesterEndPreference, dueMillis);

		}
	}

	private class DateClickListener implements OnPreferenceClickListener {
		private Time mTime;
		private PreferenceScreen mScreen;

		public DateClickListener(Time time, PreferenceScreen screen) {
			mTime = time;
			mScreen = screen;
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Time displayTime = mTime;
			if (Time.isEpoch(displayTime)) {
				displayTime = new Time();
				updateToDefault(displayTime);
			}
			new DatePickerDialog(MyPreferenceActivity.this, new DateListener(
					mScreen), displayTime.year, displayTime.month,
					displayTime.monthDay).show();
			return false;
		}
	}

	@Override
	public void onCheckStatusResponse(Context context, boolean isAppInvalid,
			boolean isInTestMode, boolean isDeviceInvalid) {
		// // 检查App及当前设备状态回调
		// try {
		// ((TextView) findViewById(R.id.tv_tip))
		// .setText(new StringBuilder(256)
		// .append("检查App及当前设备状态成功\n=>>App状态:")
		// .append(isAppInvalid ? "[异常]" : "[正常]")
		// .append("\n=>>是否为测试模式:")
		// .append(isInTestMode ? "[测试模式]" : "[正常模式]")
		// .append("\n=>>当前设备状态:")
		// .append(isDeviceInvalid ? "[异常]" : "[正常]")
		// .append("\n只有三个状态都为正常时，才可以获得收入。但无论状态是否异常，用户完成积分墙模式下的Offer后都可以获得积分。")
		// .append("\n\n如果您使用的是积分墙模式并且希望所有设备都可以获得积分，可以不调用该检查接口或不处理检查结果。")
		// .append("\n如果您使用的是积分墙模式并且希望在保证有收入的情况下用户才能够获得相应的积分，那么您应该在使用积分墙前，先调用此接口进行状态判断，如果状态都为正常时才启用积分墙。")
		// .append("\n\n如果App状态不正常或为\"测试模式\"，请确认是否已经上传应用到有米主站并通过审核，上传应用前，请先忽略该状态检查接口，正常调用积分墙，以配合审核人员进行审核。")
		// .append("\n\n在调用状态检查接口前，请务必先进行初始化。该接口成功调用一次即可，不需要多次调用。")
		// .toString());
		// } catch (Exception e) {
		// //
		// }
		Log.v("youmi",
				new StringBuilder(256)
						.append("检查App及当前设备状态成功\n=>>App状态:")
						.append(isAppInvalid ? "[异常]" : "[正常]")
						.append("\n=>>是否为测试模式:")
						.append(isInTestMode ? "[测试模式]" : "[正常模式]")
						.append("\n=>>当前设备状态:")
						.append(isDeviceInvalid ? "[异常]" : "[正常]")
						.append("\n只有三个状态都为正常时，才可以获得收入。但无论状态是否异常，用户完成积分墙模式下的Offer后都可以获得积分。")
						.append("\n\n如果您使用的是积分墙模式并且希望所有设备都可以获得积分，可以不调用该检查接口或不处理检查结果。")
						.append("\n如果您使用的是积分墙模式并且希望在保证有收入的情况下用户才能够获得相应的积分，那么您应该在使用积分墙前，先调用此接口进行状态判断，如果状态都为正常时才启用积分墙。")
						.append("\n\n如果App状态不正常或为\"测试模式\"，请确认是否已经上传应用到有米主站并通过审核，上传应用前，请先忽略该状态检查接口，正常调用积分墙，以配合审核人员进行审核。")
						.append("\n\n在调用状态检查接口前，请务必先进行初始化。该接口成功调用一次即可，不需要多次调用。")
						.toString());
	}

	@Override
	public void onCheckStatusConnectionFailed(Context context) {
		// //
		//
		// ((TextView) findViewById(R.id.tv_tip))
		// .setText("检查App及当前设备状态失败，请检查网络配置并重新调用检查接口");
		//
		// } catch (Exception e) {
		// //
		// }
		Log.v("youmi", "请检查网络配置是否开启,并重新运行该程序");
	}

}
