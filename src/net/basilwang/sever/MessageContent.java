package net.basilwang.sever;

public class MessageContent {

	private String content;
	private int messageId;
	private int Id;
	private String CreateTime;
	private int isRead;

	public void setContent(String message) {
		content = message;
	}

	public String getContent() {
		return content;
	}

	public void setMessageId(int id){
		this.messageId=id;
	}
	
	public void setId(int id) {
		this.Id = id;
	}

	public int getId() {
		return this.Id;
	}

	public int getMessageId() {
		return messageId;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		this.CreateTime = createTime;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	
}
