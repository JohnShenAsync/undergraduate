package net.basilwang;

public class UserAndPassUtil
{
    
	private static UserAndPassUtil instance;

	private String username;
	private String password;
	private UserAndPassUtil()
	{
		// TODO Auto-generated constructor stub
	} 
		
	

	public static UserAndPassUtil getInstance() {
		if (instance == null) {
			instance = new UserAndPassUtil();
		}
		return instance;
	}

	public String getUsername() {
		return username;
	}
    public  String getPassword()
    {
    	return password;
    }

	public void setUsername(String username) {
		this.username = username;
	}
    
	public void setPassword(String password)
	{
		this.password = password;
	}


}
