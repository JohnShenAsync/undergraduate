package net.basilwang.core;

import java.util.HashMap;
import java.util.Map;

public class TAContext {
	private static TAContext _TAContext = new TAContext();
	private Map<String, String> contextMap = new HashMap<String, String>();

	// private String sessionid;
	// private String studentNum;
	// private String name;
	private TAContext() {
	}

	public Map<String, String> getContextMap() {
		return contextMap;
	}

	public static TAContext Instance() {
		return _TAContext;
	}

	public String getSessionid() {
		return contextMap.get("sessionid");
	}

	public void setSessionid(String sessionid) {
		contextMap.put("sessionid", sessionid);
	}

	public String getStudentNum() {
		return contextMap.get("studentNum");
	}

	public void setStudentNum(String studentNum) {
		contextMap.put("studentNum", studentNum);
	}

	public String getName() {
		return contextMap.get("name");
	}

	public void setName(String name) {
		contextMap.put("name", name);
	}

	public String getCurriculumViewStateForPost() {
		return contextMap.get("curriculumViewStateForPost");
	}

	public void setCurriculumViewStateForPost(String curriculumViewStateForPost) {
		contextMap
				.put("curriculumViewStateForPost", curriculumViewStateForPost);
	}

	public String getScoreViewStateForPost() {
		return contextMap.get("scoreViewStateForPost");
	}

	public void setScoreViewStateForPost(String scoreViewStateForPost) {
		contextMap.put("scoreViewStateForPost", scoreViewStateForPost);
	}

	public void setCemesterYear(String cemesterYear) {
		contextMap.put("cemesterYear", cemesterYear);
	}

	public String getCemesterYear() {
		return contextMap.get("cemesterYear");
	}

	public void setCemesterIndex(String cemesterIndex) {
		contextMap.put("cemesterIndex", cemesterIndex);
	}

	public String getCemesterIndex() {
		return contextMap.get("cemesterIndex");
	}

	public void setCurrentCurriculumStr(String currentCurriculumStr) {
		contextMap.put("currentCurriculumStr", currentCurriculumStr);
	}

	public String getCurrentCurriculumStr() {
		return contextMap.get("currentCurriculumStr");
	}

	public void setGNMKDM(String gnmkdm) {
		contextMap.put("gnmkdm", gnmkdm);
	}

	public String getGNMKDM() {
		return contextMap.get("gnmkdm");
	}

	public String getLogonViewStateForPost() {
		return contextMap.get("logonViewStateForPost");
	}

	public void setLogonViewStateForPost(String logonViewStateForPost) {
		contextMap.put("logonViewStateForPost", logonViewStateForPost);
	}
}
