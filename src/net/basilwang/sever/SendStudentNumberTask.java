package net.basilwang.sever;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class SendStudentNumberTask extends AsyncTask<String, Object, Object> {

	@Override
	protected Object doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(
				"http://www.ruguozhai.me/api/EduStudent/SaveEduStudent?studentNum="
						+ params[1]);
		get.addHeader("X-Token", params[0]);
		try {
			httpClient.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
