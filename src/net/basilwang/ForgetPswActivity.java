package net.basilwang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.basilwang.entity.ValidateResult;
import net.basilwang.utils.NetworkUtils;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class ForgetPswActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {

	View get, answer;
	Button get_question, answerCommit;
	TextView textQuestion;
	EditText editUser, editEmail, editAnswer, editNewPsw, editNewPswVeri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_psw_activity);
		initView();
		get_question.setOnClickListener(this);
		answerCommit.setOnClickListener(this);

	}

	private void initView() {

		editUser = (EditText) findViewById(R.id.forget_get_user);
		editEmail = (EditText) findViewById(R.id.forget_get_email);
		editAnswer = (EditText) findViewById(R.id.forget_psw_answer);
		editNewPsw = (EditText) findViewById(R.id.forget_psw_newpsw);
		editNewPswVeri = (EditText) findViewById(R.id.forget_psw_verify_newpsw);
		textQuestion = (TextView) findViewById(R.id.forget_psw_question);
		get_question = (Button) findViewById(R.id.forget_get_question);
		answerCommit = (Button) findViewById(R.id.forget_psw_answer_commit);
		get = (View) findViewById(R.id.get_question);
		answer = (View) findViewById(R.id.forget_answer);
	}

	private boolean isNetAvailable() {
		return NetworkUtils.isConnect(this) ? true : false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_get_question:
			if(isEmail(editEmail.getText().toString())){
				getQuestion();
				get.setVisibility(View.GONE);
				answer.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.forget_psw_answer_commit:
			commitNewPsw();
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == false) {
			switch (v.getId()) {
			case R.id.forget_get_email:
				if (!isEmail(editEmail.getText().toString().trim())) {
					Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.forget_psw_newpsw:
				if (editNewPsw.getText().length() > 18
						|| editNewPsw.getText().length() < 6) {
					Toast.makeText(this, "密码为6-18个字符", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			}
		}

	}

	public void getQuestion() {
		if (isNetAvailable()) {
			GetQuestion question = new GetQuestion();
			question.execute(editUser.getText().toString().trim(), editEmail
					.getText().toString().trim());
		}
	}

	public void commitNewPsw() {
		if (isNetAvailable()) {
			NewPsw psw = new NewPsw();
			psw.execute(editUser.getText().toString().trim(), editAnswer
					.getText().toString().trim(), editNewPsw.getText()
					.toString().trim());
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

	class GetQuestion extends AsyncTask<Object, Object, ValidateResult> {

		String user, email;

		@Override
		protected void onPostExecute(ValidateResult result) {
			if (result.getSuccess().equals("true")) {
				textQuestion.setText(result.message);
			} else {
				Toast.makeText(ForgetPswActivity.this, result.message,
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected ValidateResult doInBackground(Object... params) {
			user = (String) params[0];
			email = (String) params[1];
			ValidateResult v_result = new ValidateResult();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.ruguozhai.me/api/users/GetQuestion");
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("loginName", user));
			postParameters.add(new BasicNameValuePair("email", email));
			UrlEncodedFormEntity formEntity;
			try {
				formEntity = new UrlEncodedFormEntity(postParameters,
						HTTP.UTF_8);
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

	class NewPsw extends AsyncTask<Object, Object, ValidateResult> {

		@Override
		protected void onPostExecute(ValidateResult result) {
			if (result.getSuccess().equals("true")) {
				ForgetPswActivity.this.finish();
				LoginActivity.instance.finish();
				Intent intent = new Intent(ForgetPswActivity.this,
						LoginActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(ForgetPswActivity.this, result.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

		String user, answer, newPsw;

		@Override
		protected ValidateResult doInBackground(Object... params) {
			user = (String) params[0];
			answer = (String) params[1];
			newPsw = (String) params[2];
			ValidateResult v_result = new ValidateResult();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.ruguozhai.me/api/users/ResetPsdByQu");
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("loginName", user));
			postParameters.add(new BasicNameValuePair("answer", answer));
			postParameters.add(new BasicNameValuePair("newPsd", newPsw));
			UrlEncodedFormEntity formEntity;
			try {
				formEntity = new UrlEncodedFormEntity(postParameters,
						HTTP.UTF_8);
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
