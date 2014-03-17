package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class UrlMap {
	public String key;
	public String detail;
	public String referer;
	public String requestType;
	public String successType;
	public String pattern;
	public String usercurriculumconfig;
	public String userscoreconfig;
	public List<Result> results;

	public UrlMap() {
		results = new ArrayList<Result>();
		referer = "";
	}

	// private
	private Result result;

	private enum tagName {
		key, detail, referer, successType, requestType, usercurriculumconfig, userscoreconfig, result, pattern
	};

	public void setProperty(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case key:
				setKey(content);
				break;
			case detail:
				setDetail(content);
				break;
			case referer:
				setReferer(content);
				break;
			case successType:
				setSuccessType(content);
				break;
			case requestType:
				setRequestType(content);
				break;
			case usercurriculumconfig:
				setUsercurriculumconfig(content);
				break;
			case userscoreconfig:
				setUserscoreconfig(content);
				break;
			case result:
				setResultContent(content);
				break;
			case pattern:
				setPattern(content);
			default:
				break;
			}
		} catch (IllegalArgumentException e) {// 处理不在枚举类型中的节点
			return;
		}

	}

	public void setResultNode(String type, String index) {
		result = new Result(type, index);
	}

	public void setResultContent(String content) {
		result.setContent(content);
	}

	public void addResultNode() {
		if(result == null){
			return;
		}
		results.add(result);
		result = null;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setSuccessType(String successType) {
		this.successType = successType;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setUsercurriculumconfig(String usercurriculumconfig) {
		this.usercurriculumconfig = usercurriculumconfig;
	}

	public void setUserscoreconfig(String userscoreconfig) {
		this.userscoreconfig = userscoreconfig;
	}

	public String getKey() {
		return key;
	}

	public String getDetail() {
		return detail;
	}

	public String getReferer() {
		return referer;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getSuccessType() {
		return successType;
	}

	public String getPattern() {
		return pattern;
	}

	public String getUsercurriculumconfig() {
		return usercurriculumconfig;
	}

	public String getUserscoreconfig() {
		return userscoreconfig;
	}

	public List<Result> getResults() {
		return results;
	}
}
