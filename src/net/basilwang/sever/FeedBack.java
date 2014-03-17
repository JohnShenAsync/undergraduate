package net.basilwang.sever;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import android.os.AsyncTask;

public class FeedBack extends
		AsyncTask<Object, Object, Object> {

	private int messageId;
	private String token;
	@Override
	protected ArrayList<MessageContent> doInBackground(Object... params) {
		String url = (String) params[0];
		token=(String)params[1];
		messageId=(Integer)params[2];
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("messageId", ""+messageId));
		try {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters,HTTP.UTF_8);
			post.setEntity(formEntity);
			post.addHeader("X-Token", token);
			client.execute(post);
		} catch (Exception e) {

		}
		return null;
	}
}
