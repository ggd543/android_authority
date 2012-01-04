package mobile.android.demo.sinamb;

import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.User;

public class MicroBlog
{
	private Weibo mTwitter;
	private User currentUser;

	public MicroBlog(String account, String password) throws WeiboException
	{
		mTwitter = new Weibo(account, password);
		currentUser = mTwitter.verifyCredentials();
	}

	public User getCurrentUser()
	{
		return currentUser;
	}

}
