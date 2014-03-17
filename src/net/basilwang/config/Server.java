package net.basilwang.config;

public class Server {
	public String ip;
	public String port;

	public void setProperty(String nodeName, String content) {
		if (nodeName == "ip")
			setIp(content);

		if (nodeName == "port")
			setPort(content);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
