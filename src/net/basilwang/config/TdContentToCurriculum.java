package net.basilwang.config;

public class TdContentToCurriculum {
	public String name;
	public String itemcount;
	public String itemseries;

	private enum tagName {
		name, itemcount, itemseries
	};

	public void setProperty(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case name:
				setName(content);
				break;
			case itemcount:
				setItemcount(content);
				break;
			case itemseries:
				setItemseries(content);
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItemcount(String itemcount) {
		this.itemcount = itemcount;
	}

	public void setItemseries(String itemseries) {
		this.itemseries = itemseries;
	}

	public String getName() {
		return name;
	}

	public String getItemcount() {
		return itemcount;
	}

	public String getItemseries() {
		return itemseries;
	}
	

}
