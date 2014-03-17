package net.basilwang;

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.basilwang.config.SAXParse;
import net.basilwang.core.TAHelper;
import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.entity.Account;
import net.basilwang.entity.ValidateResult;
import net.basilwang.utils.NetworkUtils;
import net.basilwang.utils.PreferenceUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class EditLongonPreferenceActivity extends SherlockActivity implements
		OnClickListener, OnFocusChangeListener {

	private EditText mAccountTextbox;
	private EditText mUrlTextbox;
	private EditText mUserTextbox;
	private EditText mPassTextbox;
	private EditText mOldPsdTextbox;
	private EditText mNewPsdTextbox;
	private EditText mNewPsdSureTextbox;
	private TextView mModifyPsdTextView;
	private TextView mBindUserNoTextView;
	private LinearLayout mModifyPassword;
	private LinearLayout mBindUserNo;
	private Account account;
	TAHelper taHelper;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			EditLongonPreferenceActivity.this.finish();
		}
		if (item.getTitle().equals("保存")) {
			String message = confirmTextBoxIsOK();
			if (message.equals("OK")) {
				updateUserPassword();
			} else {
				Toast.makeText(EditLongonPreferenceActivity.this, message,
						Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		// Drawable dr =
		// getResources().getDrawable(R.drawable.actionbar_bg_shape);
		Drawable dr = getResources().getDrawable(R.drawable.actionbar_bgc);
		getSupportActionBar().setIcon(R.drawable.ic_menu_cancel_holo_light);
		getSupportActionBar().setBackgroundDrawable(dr);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.modify_persional);
		initView();
		if (!NetworkUtils.isConnect(this)) {
			// 如果没有网络连接，则提示用户“没有网络连接”，并且不能进行跳转
			Toast.makeText(EditLongonPreferenceActivity.this,
					this.getResources().getString(R.string.nonetwork_toast),
					Toast.LENGTH_SHORT).show();
		}
		Bundle bundle = getIntent().getExtras();
		String accountName = bundle.getString("name");
		AccountService service = new AccountService(this);
		account = service.getAccountById(PreferenceUtils.getPreferAccountId(this));

		mAccountTextbox.setText(accountName);

		String tracksUrl = account.getUrl();
		mUrlTextbox.setText(tracksUrl);
		// select server portion of URL
		int startIndex = 0;
		int index = tracksUrl.indexOf("://");
		if (index > 0) {
			startIndex = index + 3;
		}
		mUrlTextbox.setSelection(startIndex, tracksUrl.length());

		mUserTextbox.setText(account.getUserno());
		mPassTextbox.setText(account.getPassword());
	}

	private void initView() {
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		mAccountTextbox = (EditText) findViewById(R.id.user_identifier);
		mAccountTextbox.setEnabled(false);
		mAccountTextbox.setTextColor(Color.GRAY);
		mUrlTextbox = (EditText) findViewById(R.id.url);
		mUrlTextbox.setVisibility(View.INVISIBLE);
		mUserTextbox = (EditText) findViewById(R.id.user);
		mPassTextbox = (EditText) findViewById(R.id.pass);
		/** 2012-12-6 mini star :entrance year will be invisible **/
		EditText mEntranceyearbox = (EditText) findViewById(R.id.entranceyear);
		mEntranceyearbox.setVisibility(View.INVISIBLE);
		TextView entracneYear = (TextView) findViewById(R.id.entranceyear_label);
		entracneYear.setVisibility(View.INVISIBLE);
		initModifyPersionalView();
	}

	private void initModifyPersionalView() {
		mOldPsdTextbox = (EditText) findViewById(R.id.origin_password);
		mOldPsdTextbox.setOnFocusChangeListener(this);
		mNewPsdTextbox = (EditText) findViewById(R.id.new_password);
		mNewPsdTextbox.setOnFocusChangeListener(this);
		mNewPsdSureTextbox = (EditText) findViewById(R.id.new_password_sure);
		mNewPsdSureTextbox.setOnFocusChangeListener(this);
		mModifyPsdTextView = (TextView) findViewById(R.id.modify_password_tv);
		mModifyPsdTextView.setOnClickListener(this);
		mBindUserNoTextView = (TextView) findViewById(R.id.bind_userNo_tv);
		mBindUserNoTextView.setOnClickListener(this);
		mModifyPassword = (LinearLayout) findViewById(R.id.modify_password);
		mBindUserNo = (LinearLayout) findViewById(R.id.bind_userNo);
	}

	private void updateUserPassword() {
		String token = PreferenceUtils.getPreferToken(this);
		String oldPsd = getEditContent(mOldPsdTextbox).trim();
		String newpsd = getEditContent(mNewPsdSureTextbox).trim();
		if (!oldPsd.equals("")) {

			UpdatePsdTask updatePsd = new UpdatePsdTask();
			updatePsd.execute(token, oldPsd, newpsd);
		} else if (!PreferenceUtils.getPreferHadSendUserNo(this)) {
			savePrefs();
			SharedPreferences.Editor ed = Preferences
					.getEditor(EditLongonPreferenceActivity.this);
			ed.putInt(LOGON_ACCOUNT_ID, account.getId());
			ed.commit();
			Toast.makeText(this, "学号保存成功", Toast.LENGTH_SHORT).show();
			EditLongonPreferenceActivity.this.finish();
		}
	}

	public String confirmTextBoxIsOK() {
		if (!NetworkUtils.isConnect(this))
			return "好像没有联网哦!";
		if (getEditContent(mAccountTextbox).trim().equals(""))
			return "帐号标识 不能为空";
		if (getEditContent(mUserTextbox).trim().equals(""))
			return "学号不能为空";
		if (getEditContent(mPassTextbox).equals(""))
			return "密码不能为空";
		if (!isPsdTextBoxEmpty(mOldPsdTextbox)) {
			if (isPsdTextBoxEmpty(mNewPsdTextbox))
				return "请填写新密码";
			if (isPsdTextBoxEmpty(mNewPsdSureTextbox))
				return "请确认新密码";
			if (!isNewPsdEquals()) {
				mNewPsdSureTextbox.setText("");
				return "两次输入密码的不同，请确认！";
			}
		}
		clearModifyPsdEditText();
		return "OK";
	}

	private boolean savePrefs() {

		URI uri = null;
		try {
			uri = new URI(getEditContent(mUrlTextbox));
		} catch (URISyntaxException ignored) {

		}
		account.setName(getEditContent(mAccountTextbox));
		account.setUrl(uri.toString());
		account.setUserno(getEditContent(mUserTextbox));
		account.setPassword(getEditContent(mPassTextbox));
		AccountService service = new AccountService(this);
		service.update(account);

		return true;
	}

	public Activity getActivity() {
		return EditLongonPreferenceActivity.this;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_password_tv:
			setViewVisibility(mModifyPassword);
			break;
		case R.id.bind_userNo_tv:
			setViewVisibility(mBindUserNo);
			break;

		default:
			break;
		}

	}

	private void setViewVisibility(LinearLayout view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			setBindUserNoEnabled(view);
			getEditTextFocus(view);
		} else
			view.setVisibility(View.GONE);
		clearModifyPsdEditText();

	}

	private void getEditTextFocus(LinearLayout view) {
		view.getChildAt(1).requestFocus();

	}

	private void clearModifyPsdEditText() {
		if (isPsdTextBoxEmpty(mOldPsdTextbox)) {
			mNewPsdTextbox.setText("");
			mNewPsdSureTextbox.setText("");
		}
	}

	private void setBindUserNoEnabled(LinearLayout view) {
		if (view.equals(mBindUserNo)
				&& PreferenceUtils.getPreferHadSendUserNo(this)) {
			mAccountTextbox.setEnabled(false);
			mAccountTextbox.setTextColor(Color.GRAY);
			mUserTextbox.setEnabled(false);
			mUserTextbox.setTextColor(Color.GRAY);
			mPassTextbox.setEnabled(false);
			mPassTextbox.setTextColor(Color.GRAY);
			Toast.makeText(this, "您已绑定学号", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isPsdTextBoxEmpty(EditText editText) {
		return getEditContent(editText).equals("") ? true : false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.origin_password:
			if (!isPsdTextBoxEmpty(mOldPsdTextbox)
					&& !isPsdLengthOk(mOldPsdTextbox)) {
				Toast.makeText(this, "请输入6-18位密码", Toast.LENGTH_SHORT).show();
				mOldPsdTextbox.setText("");
			}
			break;
		case R.id.new_password:
			if (!isPsdLengthOk(mNewPsdTextbox)) {
				Toast.makeText(this, "请输入6-18位密码", Toast.LENGTH_SHORT).show();
				mNewPsdTextbox.setText("");
				mNewPsdSureTextbox.setText("");
			}
			break;
		case R.id.new_password_sure:
			if (!isNewPsdEquals()) {
				Toast.makeText(this, "请填写相同的新密码", Toast.LENGTH_SHORT).show();
				mNewPsdSureTextbox.setText("");
			}
			break;

		default:
			break;
		}
	}

	private boolean isNewPsdEquals() {
		return getEditContent(mNewPsdSureTextbox).trim().equals(
				getEditContent(mNewPsdTextbox).trim()) ? true : false;
	}

	private boolean isPsdLengthOk(EditText editText) {
		return getEditContent(editText).trim().length() > 5 ? true : false;
	}

	private String getEditContent(EditText editText) {
		return editText.getText().toString();
	}

	private class UpdatePsdTask extends
			AsyncTask<String, Object, ValidateResult> {

		@Override
		protected void onPostExecute(ValidateResult result) {
			Toast.makeText(EditLongonPreferenceActivity.this,
					result.getMessage(), Toast.LENGTH_SHORT).show();
			if (result.getSuccess().equals("true"))
				EditLongonPreferenceActivity.this.finish();
			super.onPostExecute(result);
		}

		@Override
		protected ValidateResult doInBackground(String... params) {
			ValidateResult v_result = new ValidateResult();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.ruguozhai.me/api/EduStudent/ResetPsd");
			post.addHeader("X-Token", params[0]);
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("oldPsd", params[1]));
			postParameters.add(new BasicNameValuePair("newPsd", params[2]));
			try {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
						postParameters, HTTP.UTF_8);
				post.setEntity(formEntity);
				HttpResponse response = httpClient.execute(post);
				Log.v("result", "code="
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					v_result = jsonData(filter(EntityUtils.toString(response
							.getEntity())));
					Log.v("result", "result1=" + v_result.getSuccess());
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.v("result1", "result=" + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.v("result2", "result=" + e.toString());
			}

			return v_result;
		}

		public String filter(String s) {
			String s1 = s.replace("\\r", "");
			String s2 = s1.replace("\\n", "");
			String s3 = s2.substring(1, s2.length() - 1);
			String s4 = s3.replace(" ", "");
			String result = s4.replace("\\", "");
			return result;
		}

		public ValidateResult jsonData(String str) {
			ValidateResult result = JSON.parseObject(str,
					new TypeReference<ValidateResult>() {
					});
			return result;
		}

	}

}
