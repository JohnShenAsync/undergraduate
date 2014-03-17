package net.basilwang.entity;

public class Semester {
	private int id;
	private String name;
	private int accountId;
	private long beginDate;
	private long endDate;

	public Semester() {
	}

	public Semester(String name, int accountId) {
		this.accountId = accountId;
		this.name = name;
	}

	public Semester(String name, long beginDate, long endDate) {
		this.name = name;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

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

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public long getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		if (beginDate == null || beginDate.equals("")) {
			return;
		}
		this.beginDate = Long.parseLong(beginDate);
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		if (endDate == null || endDate.equals("")) {
			return;
		}
		this.endDate = Long.parseLong(endDate);
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public void setBeginDate(long beginDate) {
		this.beginDate = beginDate;
	}
}
