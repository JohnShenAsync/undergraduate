package net.basilwang.config;

public class Result {
	public String type;
	public String index;
	public String content;
	public Result(String type, String index){
		this.setType(type);
		this.setIndex(index);
	}
	public void setType(String type) {
		this.type = type;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public String getIndex() {
		return index;
	}
	public String getContent() {
		return content;
	}
	
}
