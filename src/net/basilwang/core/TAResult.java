package net.basilwang.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class TAResult {
	private boolean isOK = false;
	private String content;
	private byte[] imageContent;
	private Matcher successCheckMatcher;
	private Map<String, Object> fetchDataMap = new HashMap<String, Object>();

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	public String getContent() {
		return content;
	}

	public byte[] getImageContent() {
		return imageContent;
	}

	public void setImageContent(byte[] imageContent) {
		this.imageContent = imageContent;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Matcher getSuccessCheckMatcher() {
		return successCheckMatcher;
	}

	public void setSuccessCheckMatcher(Matcher successCheckMatcher) {
		this.successCheckMatcher = successCheckMatcher;
	}

	public void addFetchData(String key, Object obj) {
		fetchDataMap.put(key, obj);
	}
}
