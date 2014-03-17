package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class College {
	// public
	public String name;
	public String gnmkdm;
	public String cookieless;
	public String hascheckcode;
	public List<Server> servers;
	public List<UrlMap> urlMaps;
	public CurriculumConfig curriculumConfig;
	public ScoreConfig scoreConfig;
	// private
	private Server server;
	private String parentTagName = "college";

	private enum tagName {// college的子节点
		name, gnmkdm, cookieless, hascheckcode
	};

	private enum hasChildrenTag {// 有子节点的节点
		server, urlmap, curriculumconfig, scoreconfig
	}

	public College(List<UrlMap> urlMaps) {
		servers = new ArrayList<Server>();
		this.urlMaps = new ArrayList<UrlMap>(urlMaps);
	}

	public College() {
		servers = new ArrayList<Server>();
	}

	public void setProperty(String nodeName, String content) {
		if (parentTagName == "college") {// 当各个节点的父节点都是college时
			try {
				switch (hasChildrenTag.valueOf(nodeName)) {
				case server:
					parentTagName = "server";
					server = new Server();
					break;
				case curriculumconfig:
					curriculumConfig = new CurriculumConfig();
					parentTagName = "curriculumconfig";
					break;
				case scoreconfig:
					parentTagName = "scoreconfig";
					scoreConfig = new ScoreConfig();
				default:
					break;
				}
			} catch (IllegalArgumentException e) {
				setTag(nodeName, content);
			}
		} else {
			MyChildrenSetTag(parentTagName, nodeName, content);
		}
	}

	public void MyChildrenSetTag(String parentName, String nodeName,
			String content) {
		try {
			switch (hasChildrenTag.valueOf(parentName)) {
			case server:
				server.setProperty(nodeName, content);
				break;
			case curriculumconfig:
				curriculumConfig.setProperty(nodeName, content);
				break;
			case scoreconfig:
				scoreConfig.setProperty(nodeName, content);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public void addUrlMapNode(UrlMap urlMap) {
		urlMaps.add(urlMap);
	}

	public void setTag(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case name:
				setName(content);
				break;
			case gnmkdm:
				setGnmkdm(content);
			case cookieless:
				setCookieless(content);
				break;
			case hascheckcode:
				setHascheckcode(content);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			return;
		}
	}

	public void setGnmkdm(String gnmkdm) {
		this.gnmkdm = gnmkdm;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCookieless(String cookieless) {
		this.cookieless = cookieless;
	}

	public void setHascheckcode(String hascheckcode) {
		this.hascheckcode = hascheckcode;
	}

	public void addServerNode() {
		servers.add(server);
		parentTagName = "college";
		server = new Server();
	}

	public void addListNode(String nodeName) {
		try {
			switch (hasChildrenTag.valueOf(nodeName)) {
			case server:
				addServerNode();
				break;
			case curriculumconfig:
				parentTagName = "college";
			case scoreconfig:
				parentTagName = "college";
			}
		} catch (IllegalArgumentException e) {
			myChildrenAddListNode(nodeName);
			return;
		}
	}

	public void myChildrenAddListNode(String nodeName) {
		try {
			switch (hasChildrenTag.valueOf(parentTagName)) {
			case scoreconfig:
				scoreConfig.addListNode(nodeName);
				break;
			case curriculumconfig:
				curriculumConfig.addListNode(nodeName);
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public String getName() {
		return name;
	}

	public String getGnmkdm() {
		return gnmkdm;
	}

	public String getCookieless() {
		return cookieless;
	}

	public String getHascheckcode() {
		return hascheckcode;
	}

	public List<Server> getServers() {
		return servers;
	}

	public List<UrlMap> getUrlMaps() {
		return urlMaps;
	}

	public CurriculumConfig getCurriculumConfig() {
		return curriculumConfig;
	}

	public ScoreConfig getScoreConfig() {
		return scoreConfig;
	}

}
