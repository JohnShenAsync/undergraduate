package net.basilwang.config;

public class ClassIndex {
	public String name;

	public void setProperty(String nodeName, String content) {
		if (nodeName == "name") {
			setName(content);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
