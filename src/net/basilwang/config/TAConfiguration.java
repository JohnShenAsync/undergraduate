package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class TAConfiguration {
	public List<UrlMap> urlMaps;
	public  List<College> colleges;
	private  College selectedCollege;

	public TAConfiguration() {
		urlMaps = new ArrayList<UrlMap>();
		colleges = new ArrayList<College>();
	}

	public void addUrlMapNode(UrlMap urlMap) {
		urlMaps.add(urlMap);
	}

	public void addColleageNode(College c) {
		colleges.add(c);
	}

	public List<UrlMap> getUrlMaps() {
		return urlMaps;
	}

	public List<College> getColleges() {
		return colleges;
	}

	public  College getSelectedCollege() {
		if (selectedCollege == null) {
			selectedCollege = colleges.get(0);
		}
		return selectedCollege;

	}

	public String getSelectedHost() {
		return getSelectedCollege().getServers().get(0).getIp();
	}

	public UrlMap getUrlMap(String key) {
		UrlMap urlMap = null;
		for (int i = 0; i < selectedCollege.getUrlMaps().size(); i++) {
			String tempKey = selectedCollege.getUrlMaps().get(i).getKey();
			if (tempKey.equals(key)) {
				urlMap = selectedCollege.getUrlMaps().get(i);
				break;
			}
		}

		return urlMap;
	}
}
