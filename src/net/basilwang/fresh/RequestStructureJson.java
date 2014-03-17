package net.basilwang.fresh;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class RequestStructureJson extends
		AsyncTask<Object, Object, ArrayList<PointOfStructure>> {

	private PointOfStructureService service;
	int mapId;

	@Override
	protected void onPostExecute(ArrayList<PointOfStructure> result) {
		for (int i = 0; i < result.size(); i++) {
			result.get(i).setMapId(mapId);
			service.save(result.get(i));
		}
		super.onPostExecute(result);
	}

	@Override
	protected ArrayList<PointOfStructure> doInBackground(Object... params) {
		ArrayList<PointOfStructure> list1 = new ArrayList<PointOfStructure>();
		String result;
		String url = (String) params[0];
		service = (PointOfStructureService) params[1];
		mapId = (Integer) params[2];
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				String s = (EntityUtils.toString(response.getEntity()));
				String s1 = s.replace("\\", "");
				String s2 = s1.substring(1, s1.length() - 1);
				String s3 = s2.replace(" ", "");
				result = s3.replace("rn", "");
				Log.v("result", result);
				list1 = jsonData(result);
			}
		} catch (Exception e) {

		}
		return list1;
	}

	public ArrayList<PointOfStructure> jsonData(String str) {
		ArrayList<PointOfStructure> list2 = JSON.parseObject(str,
				new TypeReference<ArrayList<PointOfStructure>>() {
				});
		Log.v("result", "list2Size=" + list2.size());
		return list2;
	}

}
