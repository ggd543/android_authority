package mobile.android.demo.sinamb;

public interface Const
{
	public String PREFERENCE_NAME = "sinawb";
	public String ACCOUNT = "account";
	public String PASSWORD = "password";
	public String USER_NAME = "username";
	public String OFFLINE_LOGIN = "offlinelogin";

	public String DATABASE_DEFAULT_DIR = "sina_micro_blog/";
	public String DATABASE_DEFAULT_FILENAME = "sinamb.db";

	public int PROCESS_TYPE_GET_ACCOUNT_PASSWORD = 1;
	public int PROCESS_TYPE_LOAD_MICROBLOG_VIEW = 2;

	// 1: 主页，2：逛逛，3：评论，4：我的，5:私信
	public int SHOW_TYPE_HOME = 11;
	public int SHOW_TYPE_STROLL = 12;
	public int SHOW_TYPE_COMMENTS = 13;
	public int SHOW_TYPE_ME = 14;
	public int SHOW_TYPE_PRIVATE_LETTER = 15;
	public int SHOW_TYPE_ME_BLOG = 16;

	//  选项菜单id
	public int OPTIONS_MENUITEM = 100;
	public int  OPTIONS_MENUITEM_LOGOUT = OPTIONS_MENUITEM + 1;
	public int OPTIONS_MENUITEM_ABOUT = OPTIONS_MENUITEM + 2;
	
	
	// 上下文菜单id
	public int CONTEXT_MENUITEM = 50;
	public int CONTEXT_MENUITEM_COMMENTS = CONTEXT_MENUITEM + 1;
	public int CONTEXT_MENUITEM_REPOST = CONTEXT_MENUITEM + 2;
	public int CONTEXT_MENUITEM_SHOW_BIGPICTURE = CONTEXT_MENUITEM + 3;
	public int CONTEXT_MENUITEM_REFRESH_PHOTO = CONTEXT_MENUITEM + 4;
	public int CONTEXT_MENUITEM_REFRESH_REPOST_COMMENT_COUNT = CONTEXT_MENUITEM + 5;
	public int CONTEXT_MENUITEM_COLLAPSE = CONTEXT_MENUITEM + 6;
	public int CONTEXT_MENUITEM_EDIT_IMAGE = CONTEXT_MENUITEM + 7;
	public int CONTEXT_MENUITEM_DELETE_IMAGE = CONTEXT_MENUITEM + 8;

}
