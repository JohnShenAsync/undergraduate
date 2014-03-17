package net.basilwang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.basilwang.dao.Preferences;
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

import com.actionbarsherlock.view.SubMenu;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	EditText name, psw;
	Button login, register, forget;
	SubMenu subMenuForNetwork;
	ProgressBar progressBar;
	public static LoginActivity instance = null;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);
		init();
		checkNet();
		instance=this;
	}

	public void init() {
		name = (EditText) findViewById(R.id.log_name);
		psw = (EditText) findViewById(R.id.log_psw);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		register = (Button) findViewById(R.id.log_register);
		register.setOnClickListener(this);
		forget = (Button) findViewById(R.id.log_forget);
		forget.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);
	}

	public void checkNet() {
		if (!isNetAvailable())
			Toast.makeText(this, "好像没有联网哦", Toast.LENGTH_LONG).show();
	}

	private boolean isNetAvailable() {
		return NetworkUtils.isConnect(this) ? true : false;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.login:
			checkLogin();
			break;
		case R.id.log_register:
			intent = new Intent(this, RegisterActivity.class);
			break;
		case R.id.log_forget:
			intent = new Intent(this, ForgetPswActivity.class);
			break;
		}
		if (intent != null) {
//			this.finish();
			startActivity(intent);
		}
	}

	public void checkLogin() {
		String lname = name.getText().toString().trim();
		String lpsw = psw.getText().toString().trim();
		if (lname.equals("") || lpsw.equals("")) {
			Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
		} else if (isNetAvailable()) {
			Login login = new Login();
			login.execute(name.getText().toString().trim(), psw.getText()
					.toString().trim());
		} else {
			Toast.makeText(this, "请链接网络", Toast.LENGTH_SHORT).show();
		}
	}

	class Login extends AsyncTask<Object, Object, ValidateResult> {

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			progressBar.setVisibility(View.VISIBLE);
		}

		private String name, psw;

		@Override
		protected void onPostExecute(ValidateResult result) {
			progressBar.setVisibility(View.INVISIBLE);
			if (result.getSuccess().equals("true")) {
				LoginActivity.this.finish();
				PreferenceUtils.modifyStringValueInPreferences(
						LoginActivity.this,
						net.basilwang.dao.Preferences.TOKEN, result.getToken());
				PreferenceUtils.modifyStringValueInPreferences(
						LoginActivity.this, Preferences.USER_NAME, name);
				Intent intent = new Intent(LoginActivity.this,
						StaticAttachmentActivity.class);
				startActivity(intent);
			} else {
			String toast = result.getMessage().replace("<br/>", "\n");
				Toast.makeText(LoginActivity.this, toast, Toast.LENGTH_LONG)
						.show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected ValidateResult doInBackground(Object... params) {
			name = (String) params[0];
			psw = (String) params[1];
			publishProgress();
			ValidateResult v_result = new ValidateResult();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.ruguozhai.me/api/users/Login");
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("loginName", name));
			postParameters.add(new BasicNameValuePair("password", psw));
			try {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
						postParameters, HTTP.UTF_8);
				post.setEntity(formEntity);
				HttpResponse response = client.execute(post);
				Log.v("result", "code="
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					String result = filter(EntityUtils.toString(response
							.getEntity()));
					Log.v("result", "result=" + result);
					v_result = jsonData(result);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
