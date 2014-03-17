package net.basilwang.sever;

public class Message {

	private int Id;
	private String CreateTime;
	private MessageContent Content;
	public void setId(int id){
		this.Id=id;
	}
	public int getId(){
		return this.Id;
	}
	public void setCreateTime(String time){
		this.CreateTime=time;
	}
	public String getCreateTime(){
		return this.CreateTime;
	}
	public void setMessageContent(MessageContent con){
		this.Content=con;
	}
	public MessageContent getMessageContent(){
		return this.Content;
	}
}
