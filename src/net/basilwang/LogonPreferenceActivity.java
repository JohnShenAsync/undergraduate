package net.basilwang;

import static net.basilwang.dao.Preferences.ENTRACETEAR;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.basilwang.config.SAXParse;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Account;
import net.basilwang.entity.Semester;
import net.basilwang.utils.PreferenceUtils;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LogonPreferenceActivity extends SherlockActivity {

	private EditText mAccountTextbox;
	private EditText mUrlTextbox;
	private EditText mUserTextbox;
	private EditText mPassTextbox;
	private EditText mEntranceyear;
	// private Spinner mInterval;
	SharedPreferences preference = null;
	ActionMode mMode;

	/** Un used filed **/
	// private Button mCheckSettings;

	public void initView() {
		mAccountTextbox = (EditText) findViewById(R.id.user_identifier);
		mAccountTextbox.setText(PreferenceUtils.getPreferUserName(this));
		mAccountTextbox.setEnabled(false);
		mAccountTextbox.setTextColor(Color.GRAY);
		mUrlTextbox = (EditText) findViewById(R.id.url);
		mUrlTextbox.setVisibility(View.INVISIBLE);
		mUserTextbox = (EditText) findViewById(R.id.user);
		mPassTextbox = (EditText) findViewById(R.id.pass);
		mEntranceyear = (EditText) findViewById(R.id.entranceyear);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synchronize_settings);
		// Drawable dr =
		// getResources().getDrawable(R.drawable.actionbar_bg_shape);
		getSupportActionBar().setIcon(R.drawable.ic_menu_cancel_holo_light);
		Drawable dr = getResources().getDrawable(R.drawable.actionbar_bgc);
		getSupportActionBar().setBackgroundDrawable(dr);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		initView();
		String tracksUrl = Preferences.getTracksUrl(this);
		mUrlTextbox.setText(tracksUrl);
		// select server portion of URL
		int startIndex = 0;
		int index = tracksUrl.indexOf("://");
		if (index > 0) {
			startIndex = index + 3;
		}
		mUrlTextbox.setSelection(startIndex, tracksUrl.length());

		mUserTextbox.setText(Preferences.getTracksUser(this));
		mPassTextbox.setText(Preferences.getTracksPassword(this));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			LogonPreferenceActivity.this.finish();
		}
		if (item.getTitle().equals("保存")) {
			if (saveNewUser()) {
				savePrefs();
				LogonPreferenceActivity.this.finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public Activity getActivity() {
		return this;
	}

	public String confirmTextBoxIsNotNull() {
		if (mAccountTextbox.getText().toString().trim().equals(""))
			return "帐号标识 不能为空";
		if (mUserTextbox.getText().toString().trim().equals(""))
			return "学号不能为空";
		if (mPassTextbox.getText().toString().equals(""))
			return "密码不能为空";
		if (mEntranceyear.getText().toString().equals(""))
			return "入学年份不能为空";

		int entracneYear = getEntranceyear();
		if (entracneYear < 2005 || entracneYear > 2020)
			return "入学年份格式不正确";

		return "OK";
	}

	private boolean savePrefs() {
		SharedPreferences.Editor ed = Preferences.getEditor(this);
		URI uri = null;
		try {
			uri = new URI(mUrlTextbox.getText().toString());
		} catch (URISyntaxException ignored) {

		}
		// 2012-05-18 王华杰 去掉空格
		Account account = new Account();
		account.setName(mAccountTextbox.getText().toString().trim());
		account.setUrl(uri.toString());
		account.setUserno(mUserTextbox.getText().toString().trim());
		account.setPassword(mPassTextbox.getText().toString());
		AccountService service = new AccountService(this);
		service.save(account);

		UserAndPassUtil.getInstance().setUsername(
				mUserTextbox.getText().toString());
		UserAndPassUtil.getInstance().setPassword(
				mPassTextbox.getText().toString());

		// 2012-09-29 basilwang save and set at the same time
		account = service.getAccountByName(account.getName());
		ed.putInt(LOGON_ACCOUNT_ID, account.getId());
		saveEntranceyearPrefer(ed);
		AddNewSemestersForAccount(account.getId());
		ed.commit();
		return true;
	}

	public int getEntranceyear() {
		return Integer.valueOf(mEntranceyear.getText().toString());
	}

	public void saveEntranceyearPrefer(SharedPreferences.Editor ed) {
		ed.putInt(ENTRACETEAR, getEntranceyear());
	}

	public void AddNewSemestersForAccount(int accountId) {
		new SemesterInit(getEntranceyear(), accountId);
	}

	public int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.YEAR);
	}

	public boolean saveNewUser() {
		String message = confirmTextBoxIsNotNull();
		if (message.equals("OK") == false) {

			Toast.makeText(LogonPreferenceActivity.this, message,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		Toast.makeText(LogonPreferenceActivity.this, "保存成功", Toast.LENGTH_SHORT)
				.show();
		return true;
	}

	public class SemesterInit {
		private SemesterService semesterService;

		public SemesterInit() {

		}

		public SemesterInit(int entranceYear, int accountId) {
			this.semesterService = new SemesterService(getActivity());
			addNewSemesters(entranceYear, accountId);
			updateBeginAndEndDate();
		}

		public void updateBeginAndEndDate() {
			String arrays[][] = { { "2012-09-01", "2013-01-28" },
					{ "2013-03-04", "2013-07-15" } };
			String semesters[] = { "2012-2013|1", "2012-2013|2" };
			DateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < arrays.length; i++) {
				try {
					long beginDate = dateFormate.parse(arrays[i][0]).getTime();
					long endDate = dateFormate.parse(arrays[i][1]).getTime();
					Semester semester = new Semester(semesters[i], beginDate,
							endDate);
					semesterService.updateBeginAndEndDataOfSemester(semester);
				} catch (ParseException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		public void addNewSemesters(int entranceYear, int accountId) {
			if (accountHasSemestersOrNot(semesterService, accountId) == true) {
				// true, account has semesters
				semesterService.deleteSemestersByAccountId(accountId);
			}
			semesterService.saveSemesterList(produceSemesters(entranceYear,
					accountId));
		}

		/**
		 * If account has semesters,drop them and add new semesters
		 * 
		 * @return if account has semesters ,return true,otherwise，return false
		 */
		public boolean accountHasSemestersOrNot(
				SemesterService semesterService, int accountId) {
			if (semesterService.getSemestersByAccountId(accountId) == null) {
				return false;
			}
			return true;
		}

		public List<Semester> produceSemesters(int entranceYear, int accountId) {
			int index = getColleageYear();
			String[] semesterNames = new String[index * 2];
			int count = 0;
			for (int i = entranceYear; i < index + entranceYear; i++) {
				for (int j = 1; j <= 2; j++) {
					semesterNames[count] = i + "-" + (i + 1) + "|" + j;
					count++;
				}
			}

			List<Semester> semesters = new ArrayList<Semester>(
					semesterNames.length);

			for (int i = 0; i < semesterNames.length; i++) {
				Semester semester = new Semester(semesterNames[i], accountId);
				semesters.add(semester);
			}
			return semesters;
		}

		/**
		 * @return The max years you may need to study in school
		 */
		public int getColleageYear() {
			return 5;
		}
	}
}
