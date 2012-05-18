package models;

public class User {
	private String UserEmail;
	private String UserName;
	
	public User() { }
	
	public String getUserEmail() {
		return UserEmail;
	}
	public User setUserEmail(String userEmail) {
		UserEmail = userEmail;
		return this;
	}
	public String getUserName() {
		return UserName;
	}
	public User setUserName(String userName) {
		UserName = userName;
		return this;
	}
}
