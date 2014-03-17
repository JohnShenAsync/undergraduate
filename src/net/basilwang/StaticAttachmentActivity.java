/*
 * Copyright (C) 2011 Jake Wharton
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
package net.basilwang;

import static net.basilwang.dao.Preferences.ABOUT_US;
import static net.basilwang.dao.Preferences.CLOSE_AD;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ADD_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_PREFERENCES;
import static net.basilwang.dao.Preferences.RU_GUO_ZHAI;
import static net.basilwang.dao.Preferences.SHAREONWEIBO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.basilwang.PreferenceFragmentPlugin.OnPreferenceAttachedListener;
import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Account;
import net.basilwang.map.UnZipAssetsOfMap;
import net.basilwang.sever.GetVersionTask;
import net.basilwang.sever.MessageService;
import net.basilwang.sever.RequestNewMessage;
import net.basilwang.utils.NetworkUtils;
import net.basilwang.utils.PreferenceUtils;
import net.youmi.android.appoffers.CheckStatusNotifier;
import net.youmi.android.appoffers.YoumiOffersManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.sharesdk.framework.AbstractWeibo;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.sso.SsoHandler;

public class StaticAttachmentActivity extends BaseActivity implements
		OnPreferenceAttachedListener, OnPreferenceChangeListener,
		OnPreferenceClickListener, CheckStatusNotifier {

	// private String[] viewtypes;//is not used after add slidingMenu

	public static StaticAttachmentActivity instance = null;   
	static final String TAG = "StaticAttachmentActivity";
	SubMenu subMenuForNetwork;
	// SubMenu subMenuWithoutNetwork;
	private Class dayorweekClass = CurriculumViewPagerFragment.class;
	private int accountId;
	private AccountService accountService;
	private Boolean isExiting = false;
	// 微博实例
	private Weibo mWeibo;
	/* 下载包安装路径 */
	public static final String savePath = Environment
			.getExternalStorageDirectory().getPath() + "/Undergraduate/";
	public static final String saveFileName = savePath + "Undergraduate.apk";
	public static final String sharePicture = savePath + "weekView.png";

	private static final String CONSUMER_KEY = "3430810380";// 替换为开发者的appkey，例�1646212860";
	private static final String REDIRECT_URL = "http://www.baidu.com";// = //
																		// "http://www.sina.com";
	public static Oauth2AccessToken accessToken;
	public static final String SINATAG = "sinasdk";
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	SsoHandler mSsoHandler;
	private Fragment mContent;
	private WeekViewFragment weekView;
	private CurriculumViewPagerFragment curriculumView;
	SliderMenuFragment sliderMenu = new SliderMenuFragment();
	// 离线地图解压缩目录设置
	public static String OUTPUT_DIRECTORY = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/amap";

	public StaticAttachmentActivity() {
		super(R.string.changing_fragments);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		instance=this;
		weekView = new WeekViewFragment();
		curriculumView = new CurriculumViewPagerFragment();
		requestMessages();// 获取服务器端最新消息；
		// requestPointsJson();// 获取地图数据
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		getSherlock().setContentView(R.layout.main_container);

		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);

		Context context = getSupportActionBar().getThemedContext();

		accountService = new AccountService(this);
		refreshActionBarTitle();
		unZipMapDate();

		// add slidingMenu
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new CurriculumViewPagerFragment();

		// set the Above View
		setContentView(R.layout.main_container);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainContainer, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, sliderMenu).commit();

		// add slidingmenu over

		YoumiOffersManager
				.init(context, "2fc95b356bb979ae", "8b94f727980f7158");

		AbstractWeibo.initSDK(this);
		int flag = getIntent().getIntExtra("flag", 0);
		if (flag == SliderMenuFragment.EXIT_APPLICATION) {
			isExiting = true;
			finish();
		} else {
			isExiting = false;
		}

		// newUserOrNot();// 判断是否为新用户
		if (isNetAvailable()) {
			autoCheckUpdate();// 自动检测是否有新版本更新
		}

	}

	private void autoCheckUpdate() {
		// 判断是不是第一次使用新版本，如果是将不在提示的标志设成false
		if (getLastVersionName() < (getVersionName())) {
			PreferenceUtils.modifyBooleanValueInPreferences(this,
					Preferences.NEVER_OCCUR_UPDATE_TIP, false);
			PreferenceUtils.modifyStringValueInPreferences(this,
					Preferences.LAST_VERSION, String.valueOf(getVersionName()));
		}
		// 获取最新版本号的网站，网站为木蚂蚁
		String versionURL = "http://www.mumayi.com/android-120469.html";

		// 拉取版本号的异步Task
		GetVersionTask getVersionTask = new GetVersionTask(this);
		getVersionTask.execute(versionURL, getVersionName(), getOccurTip());

	}

	// 获取不再提示的标志
	private Boolean getOccurTip() {
		return PreferenceUtils.getPreferUpdateTip(this);
	}

	// 获取package的versionName
	private Double getVersionName() {
		try {
			return Double
					.valueOf(getPackageManager().getPackageInfo(
							"net.basilwang", PackageManager.GET_CONFIGURATIONS).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 1.00;
		}
	}

	private Double getLastVersionName() {
		return Double.valueOf(PreferenceUtils.getPreferLastVersion(this));
	}

	// private void requestPointsJson() {
	// PointOfSegmentService segment = new PointOfSegmentService(this);
	// PointOfStructureService structure = new PointOfStructureService(this);
	// if (segment.getSegmentId(2).size() == 0) {
	// RequestMapJson mapJson = new RequestMapJson();
	// mapJson.execute(
	// "http://120.192.31.164:8994/api/SegmentOfMaps/SelectSegmentOfMaps?mapId=2",
	// segment, 2);
	// }
	// if (structure.getPoint(2).size() == 0) {
	// RequestStructureJson structureJson = new RequestStructureJson();
	// structureJson
	// .execute(
	// "http://120.192.31.164:8994/api/PointOfMaps/SelectPointOfMaps?mapId=2",
	// structure, 2);
	// }
	// if (segment.getSegmentId(3).size() == 0) {
	// RequestMapJson mapJson = new RequestMapJson();
	// mapJson.execute(
	// "http://120.192.31.164:8994/api/SegmentOfMaps/SelectSegmentOfMaps?mapId=3",
	// segment, 3);
	// }
	// if (structure.getPoint(3).size() == 0) {
	// RequestStructureJson structureJson = new RequestStructureJson();
	// structureJson
	// .execute(
	// "http://120.192.31.164:8994/api/PointOfMaps/SelectPointOfMaps?mapId=3",
	// structure, 3);
	// }
	// if (segment.getSegmentId(4).size() == 0) {
	// RequestMapJson mapJson = new RequestMapJson();
	// mapJson.execute(
	// "http://120.192.31.164:8994/api/SegmentOfMaps/SelectSegmentOfMaps?mapId=4",
	// segment, 4);
	// }
	// if (structure.getPoint(4).size() == 0) {
	// RequestStructureJson structureJson = new RequestStructureJson();
	// structureJson
	// .execute(
	// "http://120.192.31.164:8994/api/PointOfMaps/SelectPointOfMaps?mapId=4",
	// structure, 4);
	// }
	// if (segment.getSegmentId(5).size() == 0) {
	// RequestMapJson mapJson = new RequestMapJson();
	// mapJson.execute(
	// "http://120.192.31.164:8994/api/SegmentOfMaps/SelectSegmentOfMaps?mapId=5",
	// segment, 5);
	// }
	// if (structure.getPoint(5).size() == 0) {
	// RequestStructureJson structureJson = new RequestStructureJson();
	// structureJson
	// .execute(
	// "http://120.192.31.164:8994/api/PointOfMaps/SelectPointOfMaps?mapId=5",
	// structure, 5);
	// }
	// }

	// 消息推送
	private void requestMessages() {
		Log.v("result",PreferenceUtils.getPreferToken(this));
		MessageService messageService = new MessageService(this);
		SemesterService semester = new SemesterService(this);
		try {
			String url = "http://www.ruguozhai.me/api/message/GetUnReadMessages";
			RequestNewMessage request = new RequestNewMessage(sliderMenu);
			request.execute(url, messageService, semester,PreferenceUtils.getPreferToken(this));
		} catch (Exception e) {

		}
	}

	/**
	 * If this is a new user,please add account
	 */
	// private void newUserOrNot() {
	// String data = "";
	// if (data.equals("") && !isExiting) {
	// Intent intent = new Intent(this, LoginActivity.class);
	// startActivity(intent);
	// }
	// }

	/*
	 * In order to receive these events you need to implement an interface from
	 * ActionBarSherlock so it knows to dispatch to this callback. There are
	 * three possible interface you can implement, one for each menu event.
	 * 
	 * Remember, there are no superclass implementations of these methods so you
	 * must return a value with meaning.
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		checkNetwork();
		return true;
	}

	@Override
	protected void onResume() {
		checkNetwork();
		super.onResume();
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

	// @曹洪�自己改写的，用于返回是否联网
	private boolean isNetAvailable() {
		return NetworkUtils.isConnect(this) ? true : false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If this callback does not handle the item click,
		// onPerformDefaultAction
		// of the ActionProvider is invoked. Hence, the provider encapsulates
		// the
		// complete functionality of the menu item.
		// if (item.getTitle() == "设置") {
		// // if(NetworkUtils.isConnect(this))
		// // {
		// Intent intent = new Intent();
		// intent.setClass(this, MyPreferenceActivity.class);
		// startActivity(intent);
		// // }
		// // else
		// // {
		// // Toast.makeText(StaticAttachmentActivity.this, "请检查网�,
		// // Toast.LENGTH_SHORT).show();
		// // }
		//
		// }
		if (item.getTitle().equals("周/天")) {
			// Toast.makeText(this, "周视图暂时关闭，下个版本炫丽呈现",
			// Toast.LENGTH_SHORT).show();
			// 2012-12-5 basilwang there seems a bug if we change other
			// tablistenser in one tab
			// which will create a lot of item option , SCARED!!.
			// So we use another way. delete and add tab
			if (dayorweekClass == CurriculumViewPagerFragment.class) {

				// if(dayorweekClass==CurriculumViewPagerFragment.class)
				// {
				// getSupportActionBar().getSelectedTab().setTabListener(
				// new TabListener(this, "curriculum",
				// WeekViewFragment.class));
				// getSupportActionBar().selectTab(
				// getSupportActionBar().getSelectedTab());
				// dayorweekClass = WeekViewFragment.class;
				// }
				// else if(dayorweekClass==WeekViewFragment.class)
				// {
				// getSupportActionBar().getSelectedTab().setTabListener(
				// new TabListener(this, "curriculum",
				// CurriculumViewPagerFragment.class));
				// getSupportActionBar().selectTab(
				// getSupportActionBar().getSelectedTab());
				// dayorweekClass = CurriculumViewPagerFragment.class;
				// }

				/*
				 * ActionBar.Tab tab = getSupportActionBar().newTab();
				 * tab.setText("课表"); tab.setTabListener(new TabListener(this,
				 * "week", WeekViewFragment.class));
				 * getSupportActionBar().addTab(tab, 0, true);
				 * getSupportActionBar().removeTabAt(1);
				 */
				switchContent(new WeekViewFragment(), 0);
				dayorweekClass = WeekViewFragment.class;
			} else if (dayorweekClass == WeekViewFragment.class) {

				/*
				 * ActionBar.Tab tab = getSupportActionBar().newTab();
				 * tab.setText("课表"); tab.setTabListener(new TabListener(this,
				 * "day", CurriculumViewPagerFragment.class));
				 * getSupportActionBar().addTab(tab, 0, true);
				 * getSupportActionBar().removeTabAt(1);
				 */
				switchContent(new CurriculumViewPagerFragment(), 0);
				dayorweekClass = CurriculumViewPagerFragment.class;

			}
		}
		if (item.getTitle() == getResources().getString(R.string.checknetwork)) {
			checkNetwork();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		return false;
	}

	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		if (root == null)
			return; // for whatever reason in very rare cases this is null
		if (xmlId == R.xml.preferencesfragment) {
			PreferenceCategory logonPreference;
			PreferenceScreen logonAddPreference;
			PreferenceScreen aboutusPreference;
			PreferenceScreen shareonweiboPreference;
			// PreferenceScreen ruguozhaiPreference;
			// The filelds we have deleted
			// CheckBoxPreference weekViewCheckboxPreference;
			// CheckBoxPreference scoreCheckboxPreference;
			CheckBoxPreference adCheckboxPreference;
			logonAddPreference = (PreferenceScreen) root
					.findPreference(LOGON_ADD_PREFERENCES);
			if (logonAddPreference != null) {
				logonAddPreference.setOnPreferenceClickListener(this);
			}
			logonPreference = (PreferenceCategory) root
					.findPreference(LOGON_PREFERENCES);

			aboutusPreference = (PreferenceScreen) root
					.findPreference(ABOUT_US);
			shareonweiboPreference = (PreferenceScreen) root
					.findPreference(SHAREONWEIBO);

			// ruguozhaiPreference = (PreferenceScreen) root
			// .findPreference(RU_GUO_ZHAI);

			adCheckboxPreference = (CheckBoxPreference) root
					.findPreference(CLOSE_AD);
			// weekViewCheckboxPreference = (CheckBoxPreference) root
			// .findPreference(WEEKVIEW_ENABLED);
			// 2012-09-26 basilwang if weekview already enabled, we set
			// WeekViewCheckboxPreference enable status is false
			// if (Preferences.isWeekViewUnlocked(this)) {
			// weekViewCheckboxPreference.setChecked(true);
			// weekViewCheckboxPreference.setEnabled(false);
			// } else {
			// weekViewCheckboxPreference.setChecked(false);
			// weekViewCheckboxPreference.setEnabled(true);
			// }
			if (Preferences.isAdClosed(this)) {
				adCheckboxPreference.setChecked(true);
				adCheckboxPreference.setEnabled(false);
			} else {
				adCheckboxPreference.setChecked(false);
				adCheckboxPreference.setEnabled(true);
			}
			aboutusPreference.setOnPreferenceClickListener(this);
			shareonweiboPreference.setOnPreferenceClickListener(this);
			// ruguozhaiPreference.setOnPreferenceClickListener(this);
			// weekViewCheckboxPreference.setOnPreferenceClickListener(this);
			adCheckboxPreference.setOnPreferenceClickListener(this);
			YoumiOffersManager.init(this, "2fc95b356bb979ae",
					"8b94f727980f7158");
			YoumiOffersManager.checkStatus(StaticAttachmentActivity.this,
					StaticAttachmentActivity.this);
			reloadData(logonPreference, logonAddPreference,
					root.getPreferenceManager());
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(ABOUT_US)) {
			Intent intent = new Intent(StaticAttachmentActivity.this,
					About_us.class);
			startActivity(intent);
			return false;
		}

		if (preference.getKey().equals(SHAREONWEIBO)) {

			if (isNetAvailable()) {
				// 先读取accessToken,看是否还在期限内
				StaticAttachmentActivity.accessToken = AccessTokenKeeper
						.readAccessToken(this);

				// 如果还在期限内，给出提示，直接跳�
				if (StaticAttachmentActivity.accessToken.isSessionValid()) {
					String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(new java.util.Date(
									StaticAttachmentActivity.accessToken
											.getExpiresTime()));
					Toast.makeText(
							StaticAttachmentActivity.this,
							"access_token 仍在有效期内,无需再次登录: \naccess_token:"
									+ StaticAttachmentActivity.accessToken
											.getToken() + "\n有效期：" + date,
							Toast.LENGTH_SHORT).show();

					// 认证完毕，跳转页�
					Intent intent = new Intent(StaticAttachmentActivity.this,
							ShareOnWeibo.class);
					startActivity(intent);

				}

				// 如果accessToken为空或已过期，进行认证授�
				else {
					// mWeibo.authorize(StaticAttachmentActivity.this,
					// new AuthDialogListener());
					mSsoHandler = new SsoHandler(StaticAttachmentActivity.this,
							mWeibo);
					mSsoHandler.authorize(new AuthDialogListener());
				}

			}

			else {
				Toast.makeText(StaticAttachmentActivity.this, "请检查网络状态，好像没联网哦",
						Toast.LENGTH_LONG).show();

			}

			return false;

		}

		// if (preference.getKey().equals(RU_GUO_ZHAI)) {
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.VIEW");
		// Uri content_url = Uri.parse("http://m.ruguozhai.me");
		// intent.setData(content_url);
		// startActivity(intent);
		// return false;
		// }
		// if (preference.getKey().equals(WEEKVIEW_ENABLED)) {
		//
		// AlertDialogFactory
		// .getYoumiOfferDialog(this, (CheckBoxPreference) preference)
		// .create().show();
		// return false;
		// }
		if (preference.getKey().equals(CLOSE_AD)) {
			AlertDialogFactory
					.getYoumiOfferDialog(this, (CheckBoxPreference) preference)
					.create().show();
			return false;
		}
		if (preference.getKey().equals(LOGON_ADD_PREFERENCES)) {
			Intent intent = new Intent();
			intent.setClass(this, LogonPreferenceActivity.class);
			startActivity(intent);

		} else if (preference.getKey().equals(LOGON_ACCOUNT_PREFERENCES)) {
			String account = PreferenceUtils.getPreferUserName(this);
			Intent intent = new Intent();
			intent.putExtra("name", account);
			intent.setClass(this, EditLongonPreferenceActivity.class);
			startActivity(intent);
		}
		return true;
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			StaticAttachmentActivity.accessToken = new Oauth2AccessToken(token,
					expires_in);

			// 将认证信息保存起�
			AccessTokenKeeper.keepAccessToken(StaticAttachmentActivity.this,
					accessToken);

			Toast.makeText(StaticAttachmentActivity.this, "认证成功",
					Toast.LENGTH_LONG).show();

			// 认证完毕，跳转页�
			Intent intent = new Intent(StaticAttachmentActivity.this,
					ShareOnWeibo.class);
			startActivity(intent);
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	// 使用sso认证方式，需要重写onActivityResult()方法，并调用authorizeCallBack()方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	private void refreshActionBarTitle() {
		/*
		 * accountId =
		 * PreferenceManager.getDefaultSharedPreferences(this).getInt(
		 * LOGON_ACCOUNT_ID, 0); Account account =
		 * accountService.getAccountById(accountId); if (account.getName() !=
		 * null) { getSupportActionBar() .setTitle( account.getName() +
		 * this.getResources().getString( R.string.myaccount)); } else {
		 * getSupportActionBar() .setTitle( this.getResources().getString(
		 * R.string.pleasecreateaccount)); }
		 */
		String title = PreferenceUtils.getPreferUserName(this);
		if (!title.equals("绑定学号,亲")) {
			getSupportActionBar().setTitle(
					title + this.getResources().getString(R.string.myaccount));
		} else {
			getSupportActionBar().setTitle(title);
		}
	}

	private void reloadData(PreferenceCategory logonPreference,
			PreferenceScreen logonAddPreference,
			PreferenceManager preferenceManager) {
		// 2012-11-23 basilwang refresh tabbar title
		refreshActionBarTitle();

		logonPreference.removeAll();
		logonPreference.addPreference(logonAddPreference);
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		AccountService service = new AccountService(this);
		List<Account> list = service.getAccounts();
		if (list.size() == 0) {
			logonAddPreference.setEnabled(true);

		} else {
			logonAddPreference.setEnabled(false);
			for (Account account : list) {
				PreferenceScreen preferenceItem = preferenceManager
						.createPreferenceScreen(this);
				// CheckBoxPreference checkBoxPreference = new
				// CheckBoxPreference(this);
				// make sure each key is unique
				preferenceItem.setKey(LOGON_ACCOUNT_PREFERENCES);
				preferenceItem.setTitle(PreferenceUtils.getPreferUserName(this));
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

	}

	@Override
	public void onCheckStatusResponse(Context context, boolean isAppInvalid,
			boolean isInTestMode, boolean isDeviceInvalid) {

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

		Log.v("youmi", "请检查网络配置是否开�并重新运行该程序");
	}

	private Exit exit = new Exit();

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& getSlidingMenu().isMenuShowing()) {
			pressAgainExit();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getSupportFragmentManager().getBackStackEntryCount() < 1)
				getSlidingMenu().showMenu();
			else
				getSupportFragmentManager().popBackStack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void pressAgainExit() {

		if (exit.isExit()) {
			this.finish();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	private class Exit {
		private boolean isExit = false;
		private Runnable task = new Runnable() {
			@Override
			public void run() {
				isExit = false;
			}
		};

		public void doExitInOneSecond() {
			isExit = true;
			HandlerThread thread = new HandlerThread("doTask");
			thread.start();
			new Handler(thread.getLooper()).postDelayed(task, 1000);
		}

		public boolean isExit() {
			return isExit;
		}
	}

	public void switchContent(Fragment fragment, int flag) {
		mContent = fragment;
		if (flag == 1) {

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.mainContainer, mContent).addToBackStack(null)
					.commit();
		} else {
			// 如果Fragment是由滑动菜单切换的，则将BackStack清空
			getSupportFragmentManager().popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.mainContainer, mContent).commit();
		}
		getSlidingMenu().showContent();
	}

	public static String actionToString(int action) {
		switch (action) {
		case AbstractWeibo.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case AbstractWeibo.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case AbstractWeibo.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case AbstractWeibo.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case AbstractWeibo.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case AbstractWeibo.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case AbstractWeibo.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}

	protected void onDestroy() {
		AbstractWeibo.stopSDK(this);
		super.onDestroy();
	}

	public void exit() {
		StaticAttachmentActivity.this.finish();
	}

	private void unZipMapDate() {
		new Thread() {
			public void run() {
				// 在新线程中以同名覆盖方式解压
				try {
					UnZipAssetsOfMap.unZip(StaticAttachmentActivity.this,
							"mini_mapv3.zip", OUTPUT_DIRECTORY, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
