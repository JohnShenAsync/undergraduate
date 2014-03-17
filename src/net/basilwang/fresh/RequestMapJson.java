package net.basilwang.fresh;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class RequestMapJson extends AsyncTask<Object, Object, ArrayList<SegmentOfMap>> {

	PointOfSegmentService service;
	int mapId;
	@Override
	protected void onPostExecute(ArrayList<SegmentOfMap> result) {
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.get(i).getPointOfSegments().size(); j++) {
				result.get(i).getPointOfSegments().get(j).setSegmentId(i);
				result.get(i).getPointOfSegments().get(j).setMapId(mapId);
				service.save(result.get(i).getPointOfSegments().get(j));
			}
		}
		super.onPostExecute(result);
	}

	@Override
	protected ArrayList<SegmentOfMap> doInBackground(Object... params) {
		ArrayList<SegmentOfMap> list1 = new ArrayList<SegmentOfMap>();
		String result;
		String url = (String) params[0];
		service=(PointOfSegmentService)params[1];
		mapId=(Integer)params[2];
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				String s = (EntityUtils.toString(response.getEntity()));
				String s1=s.replace("\\", "");
				String s2=s1.substring(1, s1.length()-1);
				String s3=s2.replace(" ", "");
				result=s3.replace("rn", "");
				list1 = jsonData(result);
			}
		} catch (Exception e) {

		}
		return list1;
	}

	public ArrayList<SegmentOfMap> jsonData(String str) {
		ArrayList<SegmentOfMap> list2 = JSON.parseObject(str,
				new TypeReference<ArrayList<SegmentOfMap>>() {
				});
		return list2;
	}

}
