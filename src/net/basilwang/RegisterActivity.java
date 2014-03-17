package net.basilwang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class RegisterActivity extends Activity implements
		OnFocusChangeListener, OnClickListener {
	private EditText r_name, r_email, r_psw, r_rpsw, r_phone, r_answer;
	private List<EditText> edit = new ArrayList<EditText>();
	private Spinner r_question;
	private Button r_register;
	ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		init();
	}

	public void init() {
		r_name = (EditText) findViewById(R.id.register_user);
		edit.add(r_name);
		r_name.setOnFocusChangeListener(this);
		r_email = (EditText) findViewById(R.id.register_email);
		edit.add(r_email);
		r_email.setOnFocusChangeListener(this);
		r_psw = (EditText) findViewById(R.id.register_password);
		edit.add(r_psw);
		r_psw.setOnFocusChangeListener(this);
		r_rpsw = (EditText) findViewById(R.id.register_verify_password);
		r_rpsw.setOnFocusChangeListener(this);
		r_phone = (EditText) findViewById(R.id.register_phone);
		r_question = (Spinner) findViewById(R.id.register_spinner);
		r_answer = (EditText) findViewById(R.id.register_answer);
		edit.add(r_answer);
		r_register = (Button) findViewById(R.id.register_button);
		r_register.setOnClickListener(this);
		progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setVisibility(View.INVISIBLE);
	}

	private boolean isNetAvailable() {
		return NetworkUtils.isConnect(this) ? true : false;
	}

	public boolean checkEditText() {
		for (int i = 0; i < edit.size(); i++) {
			if (edit.get(i).getText().toString().equals("")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == false) {
			switch (v.getId()) {
			case R.id.register_user:
				if (r_name.getText().length() < 2
						|| r_name.getText().length() > 10)
					Toast.makeText(this, "用户名应为2-10个字符", Toast.LENGTH_SHORT);
				break;
			case R.id.register_password:
				if (r_psw.getText().length() < 6
						|| r_psw.getText().length() > 18) {
					Toast.makeText(this, "密码应为6-18个字符", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case R.id.register_verify_password:
				if (!r_rpsw.getText().toString()
						.equals(r_psw.getText().toString())) {
					Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case R.id.register_email:
				isEmail(r_email.getText().toString().trim());
				break;
			}
		}
	}

	// 判断email格式是否正确
	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		if (!m.matches()) {
			Toast.makeText(this, "邮箱格式不正确！", Toast.LENGTH_SHORT).show();
		}
		return m.matches();
	}

	@Override
	public void onClick(View v) {
		if (checkEditText() && isNetAvailable()) {
			Register register = new Register();
			register.execute(r_name.getText().toString().trim(), r_psw
					.getText().toString().trim(), r_email.getText().toString()
					.trim(), r_phone.getText().toString().trim(), r_question
					.getSelectedItem().toString().trim(), r_answer.getText()
					.toString().trim());
		} else if (!isNetAvailable()) {
			Toast.makeText(RegisterActivity.this, "请连接网络", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(RegisterActivity.this, "注册信息不完整，*为必填项",
					Toast.LENGTH_LONG).show();
		}
	}

	class Register extends AsyncTask<Object, Object, ValidateResult> {

		private String name, psw, email, phone, question, answer;

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(ValidateResult result) {
			progress.setVisibility(View.INVISIBLE);
			String str = result.getSuccess();
			if (str.equals("true")) {
				RegisterActivity.this.finish();
				LoginActivity.instance.finish();
				PreferenceUtils.modifyStringValueInPreferences(
						RegisterActivity.this,
						net.basilwang.dao.Preferences.TOKEN, result.getToken());
				PreferenceUtils.modifyStringValueInPreferences(
						RegisterActivity.this, Preferences.USER_NAME, name);
				Intent intent = new Intent(RegisterActivity.this,
						StaticAttachmentActivity.class);
				startActivity(intent);
			} else {
				String toast = result.getMessage().replace("<br/>", "\n");
				Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_LONG)
						.show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected ValidateResult doInBackground(Object... params) {
			ValidateResult v_result = new ValidateResult();
			name = (String) params[0];
			psw = (String) params[1];
			email = (String) params[2];
			phone = (String) params[3];
			question = (String) params[4];
			answer = (String) params[5];
			publishProgress();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.ruguozhai.me/api/users/Register");

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("loginName", name));
			postParameters.add(new BasicNameValuePair("password", psw));
			postParameters.add(new BasicNameValuePair("email", email));
			postParameters.add(new BasicNameValuePair("telPhone", phone));
			postParameters.add(new BasicNameValuePair("question", question));
			postParameters.add(new BasicNameValuePair("answer", answer));
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
