package net.basilwang.entity;

public class Account {
	private int id;
	private String name;
	private String userno;
	private String password;
	private String url;
	private boolean ishttps;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean getIshttps() {
		return ishttps;
	}

	public void setIshttps(boolean ishttps) {
		this.ishttps = ishttps;
	}

}
