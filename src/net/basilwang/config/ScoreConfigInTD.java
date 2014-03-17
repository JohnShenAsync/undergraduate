package net.basilwang.config;

public class ScoreConfigInTD {
	public String dbfield;
	public String visible;
	public String index;
	public String datatype;
	public String title;

	private enum tagName {
		dbfield, visible, index, datatype, title
	};

	public void setProperty(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case dbfield:
				setDbfield(content);
				break;
			case visible:
				setVisible(content);
				break;
			case index:
				setIndex(content);
				break;
			case datatype:
				setDatatype(content);
				break;
			case title:
				setTitle(content);
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDbfield() {
		return dbfield;
	}

	public void setDbfield(String dbfield) {
		this.dbfield = dbfield;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

}
