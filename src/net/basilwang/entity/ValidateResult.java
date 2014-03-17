package net.basilwang.entity;

public class ValidateResult {
	public String isSuccess;
	public String message;
	public String Token;
	public String getSuccess() {
		return isSuccess;
	}
	public void setSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String Message) {
		this.message = Message;
	}
	public String getToken() {
		return Token;
	}
	public void setToken(String token) {
		Token = token;
	}
}
