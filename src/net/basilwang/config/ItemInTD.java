package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class ItemInTD {
	public String pattern;
	public List<String> dbfields;

	private String dbfield;

	public ItemInTD() {
		dbfields = new ArrayList<String>();
	}

	public void setProperty(String nodeName, String content) {
		if (nodeName == "pattern") {
			setPattern(content);
		} else if (nodeName == "dbfield") {
			dbfield = content;
		}
	}

	public void addListNode(String nodeName) {
		if (nodeName == "dbfield") {
			dbfields.add(dbfield);
			dbfield = null;
		}
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public List<String> getDbfields() {
		return dbfields;
	}

}
