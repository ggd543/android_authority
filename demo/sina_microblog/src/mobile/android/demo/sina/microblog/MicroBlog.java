package mobile.android.demo.sina.microblog;


import java.util.List;

import mobile.android.demo.sina.microblog.interfaces.OnLoadBitmapFromNetListener;
import mobile.android.demo.sinamb.Const;
import weibo4j.GetTimelineListener;
import weibo4j.MyAsyncWeibo;
import weibo4j.Paging;
import weibo4j.Status;
import weibo4j.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MicroBlog extends Activity implements Const, OnItemClickListener,
		OnItemSelectedListener, OnClickListener, OnMenuItemClickListener,
		OnItemLongClickListener
{
	private static int mFirstVisibleItem = -1;
	private static int mFirstVisibleItem_Stroll = -1;
	private EditText metAccount;
	private EditText metPassword;
	private CheckBox mcbOfflineLogin;
	private static ListView mlvMicroBlog;
	private static ListView mlvStrollMicroBlog;
	private static ListView mlvMeMicroBlog;
	private static ListView mlvComments;
	private View mMicroBlogView;
	private Button mbtnLogin;
	private Gallery mgyFunction;
	public static MyAsyncWeibo mWeibo;

	public static Database mDatabase;
	private Cache mCache;
	private User mUser;
	private Util mUtil;
	// 1: 主页，2：逛逛，3：评论，4：我的，5:私信
	private static int mShowType = 0;
	private static MicroBlogAdapter mMicroBlogAdapter;

	private static MicroBlogAdapter mStrollMicroBlogAdapter;
	private static MicroBlogAdapter mMeMicroBlogAdapter;
	private static MicroBlogAdapter mCommentAdapter;
	private FunImageAdapter mFunImageAdapter;
	private int mOldPosition = 5;
	private View mOldView;
	private ProgressDialog mProgressDialog;

	private String mAccount;
	private String mPassword;

	private int mCurrentPosition;

	public static Bitmap mBitmap;
	private static View mCurrentView;
	private static int mCurrentFunctionPosition = -1;

	private Handler mHandler = new Handler()
	{
		class Scroll implements OnScrollListener
		{

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				switch (mShowType)
				{
					case SHOW_TYPE_HOME:

						mFirstVisibleItem = firstVisibleItem;
						break;

					case SHOW_TYPE_STROLL:
						mFirstVisibleItem_Stroll = firstVisibleItem;
						break;
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{

			}
		}

		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				// TODO 加载微博首页
				case Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW:

					onItemSelected(null, mgyFunction.getSelectedView(),
							mFunImageAdapter.getFunCount(), 0);
					if (mCurrentFunctionPosition > -1)
						mgyFunction.setSelection(mCurrentFunctionPosition);
					ImageView ivWrite = (ImageView) mMicroBlogView
							.findViewById(R.id.ivWrite);

					TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);

					int background = typedArray.getResourceId(
							R.styleable.Gallery_android_galleryItemBackground,
							0);
					ivWrite.setBackgroundResource(background);

					ivWrite.setOnClickListener(MicroBlog.this);

					setContentView(mMicroBlogView);
					break;
				case Const.SHOW_TYPE_HOME:

					mlvMicroBlog.setOnItemClickListener(MicroBlog.this);
					mlvMicroBlog.setAdapter(mMicroBlogAdapter);
					mlvMicroBlog.setSelection(mFirstVisibleItem);
					mlvMicroBlog.setOnScrollListener(new Scroll());
					hideAllListView();

					mlvMicroBlog.setVisibility(View.VISIBLE);

					break;
				case Const.SHOW_TYPE_STROLL:

					mlvStrollMicroBlog.setOnItemClickListener(MicroBlog.this);

					mlvStrollMicroBlog.setAdapter(mStrollMicroBlogAdapter);

					mlvStrollMicroBlog.setSelection(mFirstVisibleItem_Stroll);
					mlvStrollMicroBlog.setOnScrollListener(new Scroll());
					hideAllListView();
					mlvStrollMicroBlog.setVisibility(View.VISIBLE);

					break;
				case Const.SHOW_TYPE_ME:

					mlvMeMicroBlog.setOnItemClickListener(MicroBlog.this);

					mlvMeMicroBlog.setAdapter(mMeMicroBlogAdapter);
					hideAllListView();
					mlvMeMicroBlog.setVisibility(View.VISIBLE);

					// mlvStrollMicroBlog.setSelection(mFirstVisibleItem);
					// mlvStrollMicroBlog.setOnScrollListener(new Scroll());
					break;
				case Const.SHOW_TYPE_COMMENTS:

					mlvComments.setOnItemClickListener(MicroBlog.this);

					mlvComments.setAdapter(mCommentAdapter);
					hideAllListView();
					mlvComments.setVisibility(View.VISIBLE);

					// mlvStrollMicroBlog.setSelection(mFirstVisibleItem);
					// mlvStrollMicroBlog.setOnScrollListener(new Scroll());
					break;
				default:
					break;
			}

			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}

	};

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view,
			int position, long id)
	{
		mCurrentPosition = position;
		mCurrentView = view;
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo)
	{
		// TODO 创建上下文菜单
		super.onCreateContextMenu(menu, view, menuInfo);
		MicroBlogAdapter adapter = getAdapter();
		if (mCurrentPosition < adapter.getStatusCount())
		{
			MenuItem mnuComments = menu.add(CONTEXT_MENUITEM,
					CONTEXT_MENUITEM_COMMENTS, 1, "评论");
			MenuItem mnuRepost = menu.add(CONTEXT_MENUITEM,
					CONTEXT_MENUITEM_REPOST, 2, "转发");
			if (adapter.havePicture(mCurrentPosition))
			{
				MenuItem mnuShowBitPicture = menu.add(CONTEXT_MENUITEM,
						CONTEXT_MENUITEM_SHOW_BIGPICTURE, 3, "显示大图");
				mnuShowBitPicture.setOnMenuItemClickListener(this);
			}
			if (getAdapter().isExpanded(mCurrentView, mCurrentPosition))
			{
				MenuItem mnuCollapse = menu.add(CONTEXT_MENUITEM,
						CONTEXT_MENUITEM_COLLAPSE, 0, "收起");
				mnuCollapse.setOnMenuItemClickListener(this);
			}
			// MenuItem mnuRefreshPhoto = menu.add(CONTEXT_MENUITEM,
			// CONTEXT_MENUITEM_REFRESH_PHOTO, 4, "刷新头像");
			MenuItem mnuRefreshRepostCommentCount = menu.add(CONTEXT_MENUITEM,
					CONTEXT_MENUITEM_REFRESH_REPOST_COMMENT_COUNT, 5,
					"刷新转发/评论数");

			mnuComments.setOnMenuItemClickListener(this);
			mnuRepost.setOnMenuItemClickListener(this);
			// mnuRefreshPhoto.setOnMenuItemClickListener(this);
			mnuRefreshRepostCommentCount.setOnMenuItemClickListener(this);

		}

	}

	private void setShowType(final int showType)
	{
		mShowType = showType;

		final String title = mUtil.getString(USER_NAME, "") + " - 微博";
		mHandler.post(new Runnable()
		{

			@Override
			public void run()
			{
				switch (showType)
				{
					case Const.SHOW_TYPE_HOME:
						setTitle("首页 - " + title);
						break;
					case Const.SHOW_TYPE_STROLL:
						setTitle("逛逛 - " + title);
						break;
					case Const.SHOW_TYPE_ME:
						setTitle("我的 - " + title);
						break;
					case Const.SHOW_TYPE_COMMENTS:
						setTitle("评论 - " + title);
						break;
					default:
						break;
				}

			}
		});

	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			handleLoadSinaMicroBlog();
		}

	};

	private void hideAllListView()
	{

		if (mlvMicroBlog != null)
			mlvMicroBlog.setVisibility(View.GONE);
		if (mlvStrollMicroBlog != null)
			mlvStrollMicroBlog.setVisibility(View.GONE);
		if (mlvMeMicroBlog != null)
			mlvMeMicroBlog.setVisibility(View.GONE);
		if (mlvComments != null)
			mlvComments.setVisibility(View.GONE);

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

	}

	private void loadExceptionProcess()
	{

		finish();
	}

	// TODO 根据mShowType获得ListView对象
	private ListView getListView()
	{
		switch (mShowType)
		{
			case Const.SHOW_TYPE_HOME:
				return mlvMicroBlog;
			case Const.SHOW_TYPE_STROLL:

				return mlvStrollMicroBlog;
			case Const.SHOW_TYPE_ME:
				return mlvMeMicroBlog;
			case Const.SHOW_TYPE_COMMENTS:
				return mlvComments;
		}
		return null;
	}

	public static MicroBlogAdapter getAdapter()
	{
		switch (mShowType)
		{
			case Const.SHOW_TYPE_HOME:
				return mMicroBlogAdapter;
			case Const.SHOW_TYPE_STROLL:

				return mStrollMicroBlogAdapter;
			case Const.SHOW_TYPE_ME:
				return mMeMicroBlogAdapter;
			case Const.SHOW_TYPE_COMMENTS:
				return mCommentAdapter;
		}
		return null;
	}

	private void handleLoadSinaMicroBlog()
	{
		try
		{

			mAccount = metAccount.getText().toString();
			mPassword = metPassword.getText().toString();
			mWeibo = new  MyAsyncWeibo(mAccount, mPassword);
			mUser =mWeibo.verifyCredentials();
			mUtil.saveString(ACCOUNT, mAccount);
			mUtil.saveString(PASSWORD, mPassword);
			mUtil.saveString(USER_NAME, mUser.getName());
			loadSinaMicroBlogView(null);


		}
		catch (Exception e)
		{
			
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		Intent intent = null;

		switch (item.getItemId())
		{

			case OPTIONS_MENUITEM_LOGOUT:
				mCache.clear();
				mWeibo = null;
				mMicroBlogAdapter = null;
				mStrollMicroBlogAdapter = null;
				mMeMicroBlogAdapter = null;
				mCommentAdapter = null;
				mDatabase = null;
				finish();
				break;
			case OPTIONS_MENUITEM_ABOUT:

				intent = new Intent(this, About.class);
				startActivity(intent);
				break;
			case CONTEXT_MENUITEM_COMMENTS:
				// TODO 菜单单击事件
				intent = new Intent(this, Comments.class);

				intent.putExtra("statusId", String.valueOf(getAdapter()
						.getItemId(mCurrentPosition)));
				startActivity(intent);
				break;
			case CONTEXT_MENUITEM_REPOST:

				intent = new Intent(this, Repost.class);
				intent.putExtra("haveRetweetDetails", getAdapter()
						.haveRetweetDetails(mCurrentPosition));
				intent.putExtra("text", getAdapter().getText(mCurrentPosition));
				intent.putExtra("username",
						getAdapter().getStatusUserName(mCurrentPosition));
				intent.putExtra("statusId",
						getAdapter().getItemId(mCurrentPosition));
				startActivity(intent);
				break;
			case CONTEXT_MENUITEM_SHOW_BIGPICTURE:

				mProgressDialog = mUtil.showSpinnerProgressDialog("正在下载大图...");
				getAdapter().getBigPictureAsync(mCurrentPosition,
						new OnLoadBitmapFromNetListener()
						{

							@Override
							public void onObtainBitmap(Bitmap bitmap,
									ImageView imageView)
							{
								mHandler.sendEmptyMessage(Integer.MAX_VALUE);
								mBitmap = bitmap;

								Intent intent = new Intent(MicroBlog.this,
										BigPicture.class);
								startActivity(intent);

							}
						});

				break;
			case CONTEXT_MENUITEM_REFRESH_REPOST_COMMENT_COUNT:

				getAdapter().clearCount(mCurrentPosition);
				getAdapter().setCount(mCurrentView, mCurrentPosition);
				break;
			case CONTEXT_MENUITEM_COLLAPSE:
				getAdapter().collapse(mCurrentView, mCurrentPosition);
				break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem mnuLogout = menu.add(OPTIONS_MENUITEM,
				OPTIONS_MENUITEM_LOGOUT, 1, "注销");
		MenuItem mnuAbout = menu.add(OPTIONS_MENUITEM, OPTIONS_MENUITEM_ABOUT,
				2, "关于");
		mnuLogout.setOnMenuItemClickListener(this);
		mnuAbout.setOnMenuItemClickListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	class LoginOnClick implements OnClickListener
	{
		@Override
		public void onClick(View view)
		{

			try
			{

				mProgressDialog = mUtil.showSpinnerProgressDialog("正在登录...");

				handleLoadSinaMicroBlog();

			}
			catch (Exception e)
			{

			}
		}
	}

	@Override
	public void onClick(View view)
	{
		Intent intent = null;
		switch (view.getId())
		{
			case R.id.ivWrite:
				intent = new Intent(this, WriteMicroBlog.class);

				startActivity(intent);
				break;

			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id)
	{
		final ProgressDialog progressDialog;
		final MicroBlogAdapter microBlogAdapter = (MicroBlogAdapter) getListView()
				.getAdapter();
		mCurrentFunctionPosition = position;
		switch (adapterView.getId())
		{
			case R.id.lvMicroBlog:
			case R.id.lvStrollMicroBlog:
			case R.id.lvMeMicroBlog:
			case R.id.lvComments:

				if (microBlogAdapter.getCount() == position + 1
						&& adapterView.getId() != R.id.lvStrollMicroBlog)
				{
					try
					{
						long minStatusId = microBlogAdapter.getMinStatusId();

						if (minStatusId > 0)
						{
							Paging paging = new Paging();

							paging.setMaxId(minStatusId - 1);
							paging.setCount(20);
							progressDialog = mUtil
									.showSpinnerProgressDialog("正在获得更多微博...");
							
							mWeibo.getTimelineAsync(paging,
									new GetTimelineListener()
									{

										@Override
										public void ok(List<Status> statusList)
										{
											final List<Status> statuses = statusList;
											mHandler.post(new Runnable()
											{

												@Override
												public void run()
												{
													microBlogAdapter
															.addMoreMicroBlog(
																	statuses,
																	true);
													progressDialog.dismiss();
												}
											});

										}

										@Override
										public void error(Exception e)
										{
											// mUtil.showMsg("无法获得更多微博.");

										}
									}, mShowType);
						}
					}
					catch (Exception e)
					{
					}
				}
				else
				{
					// TODO 单击listview的事件
					try
					{
						getAdapter().expand(mCurrentView, mCurrentPosition);
					}
					catch (Exception e)
					{
						// TODO: handle exception
					}
					microBlogAdapter.setImage(view, position);
					microBlogAdapter.setCount(view, position);
					microBlogAdapter.setText(view, position);

				}

				break;
			case R.id.gyFunction:

				if (mOldPosition > -1)
				{

					
					mOldPosition = position;
					mOldView = view;
				}

				switch (mFunImageAdapter.getIndex(position))
				{
					case R.drawable.home:
						setShowType(Const.SHOW_TYPE_HOME);
						if (mMicroBlogAdapter != null)
						{
							hideAllListView();
							getListView().setAdapter(mMicroBlogAdapter);
							getListView().setOnItemClickListener(this);
							getListView().setVisibility(View.VISIBLE);
						}
						break;
					case R.drawable.stroll:
						// TODO 单击逛逛按钮时触发的事件
						setShowType(Const.SHOW_TYPE_STROLL);

						if (mStrollMicroBlogAdapter == null)
						{
							List<Status> statusList = (List<Status>) mCache
									.getPublicTimeline();
							if (statusList != null)
							{

								hideAllListView();
								mStrollMicroBlogAdapter = new MicroBlogAdapter(
										this, statusList, mWeibo, mCache,
										false);
								mStrollMicroBlogAdapter
										.loadCacheProfileImageForPublicTimeline();

								getListView().setAdapter(
										mStrollMicroBlogAdapter);

								getListView().setOnItemClickListener(this);
								getListView().setVisibility(View.VISIBLE);
							}
							else
							{
								clickFunButton(Const.SHOW_TYPE_STROLL,
										mStrollMicroBlogAdapter);
							}
						}
						else
						{

							clickFunButton(Const.SHOW_TYPE_STROLL,
									mStrollMicroBlogAdapter);
						}
						break;
					case R.drawable.me:
						clickFunButton(Const.SHOW_TYPE_ME, mMeMicroBlogAdapter);
						break;
					case R.drawable.comments:
						clickFunButton(Const.SHOW_TYPE_COMMENTS,
								mCommentAdapter);
						break;
					case R.drawable.refresh:
						refreshMicroBlog();

						break;
					default:
						break;
				}
				break;
		}
	}

	private void clickFunButton(final int showType,
			MicroBlogAdapter microBlogAdapter)
	{

		setShowType(showType);

		if (microBlogAdapter != null)
		{
			hideAllListView();
			getListView().setAdapter(microBlogAdapter);
			getListView().setOnItemClickListener(this);
			getListView().setVisibility(View.VISIBLE);
		}
		else
		{
			mProgressDialog = mUtil.showSpinnerProgressDialog("正在获得微博...");

			try
			{
				mWeibo.getTimelineAsync(new Paging(),
						new GetTimelineListener()
						{

							@Override
							public void ok(final List<Status> statusList)
							{
								mHandler.post(new Runnable()
								{

									@Override
									public void run()
									{

										MicroBlogAdapter mba = new MicroBlogAdapter(
												MicroBlog.this,
												statusList,
												mWeibo,
												mCache,
												(showType == Const.SHOW_TYPE_STROLL) ? false
														: true);

										mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
										try
										{
											switch (showType)
											{
												case Const.SHOW_TYPE_STROLL:
													mStrollMicroBlogAdapter = mba;
													// mba
													// .downloadCacheProfileImageForPublicTimeline(statusList);
													mHandler.sendEmptyMessage(Const.SHOW_TYPE_STROLL);
													mStrollMicroBlogAdapter
															.downloadCacheProfileImageForPublicTimeline(
																	statusList,
																	false);
													mCache.savePublicTimeline(statusList);
													break;
												case Const.SHOW_TYPE_ME:
													mMeMicroBlogAdapter = mba;
													mHandler.sendEmptyMessage(Const.SHOW_TYPE_ME);
													break;
												case Const.SHOW_TYPE_COMMENTS:
													mCommentAdapter = mba;
													mHandler.sendEmptyMessage(Const.SHOW_TYPE_COMMENTS);
													break;
												default:
													break;
											}
										}
										catch (Exception e)
										{

										}

									}
								});

							}

							@Override
							public void error(Exception e)
							{
								mHandler.post(new Runnable()
								{

									@Override
									public void run()
									{
										// progressDialog.cancel();
									}
								});
								//

							}
						}, showType);
			}
			catch (Exception e)
			{

			}

		}
	}

	private void refreshMicroBlog()
	{
		final ProgressDialog progressDialog;
		final MicroBlogAdapter microBlogAdapter = (MicroBlogAdapter) getListView()
				.getAdapter();
		try
		{
			long maxStatusId = microBlogAdapter.getMaxStatusId();
			if (maxStatusId < Long.MAX_VALUE)
			{
				Paging paging = new Paging();
				paging.setSinceId(maxStatusId + 1);
				Log.d("id", String.valueOf(maxStatusId));
				paging.setCount(20);
				progressDialog = mUtil.showSpinnerProgressDialog("正在刷新微博...");
				mWeibo.getTimelineAsync(paging, new GetTimelineListener()
				{

					@Override
					public void ok(List<Status> statusList)
					{
						final List<Status> statuses = statusList;
						mHandler.post(new Runnable()
						{

							@Override
							public void run()
							{
								List<Status> myStatusList = microBlogAdapter
										.addMoreMicroBlog(statuses, false);

								try
								{
									try
									{
										switch (mShowType)
										{
											case Const.SHOW_TYPE_HOME:

												microBlogAdapter
														.downloadCacheProfileImageForHomeTimeline(
																statuses, false);
												mCache.saveHomeTimeline(myStatusList);
												break;
											case Const.SHOW_TYPE_STROLL:
												// TODO 刷新
												microBlogAdapter
														.downloadCacheProfileImageForPublicTimeline(
																statuses, false);
												mCache.savePublicTimeline(myStatusList);
												break;
											case Const.SHOW_TYPE_ME:

												break;
											case Const.SHOW_TYPE_COMMENTS:

												break;
											default:
												break;
										}
									}
									catch (Exception e)
									{

									}
								}
								catch (Exception e)
								{

								}
								progressDialog.dismiss();
							}
						});

					}

					@Override
					public void error(Exception e)
					{
						// mUtil.showMsg("无法刷新微博.");

					}
				}, mShowType);
			}
		}
		catch (Exception e)
		{
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id)
	{

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mUtil = new Util(this);
		mCache = new Cache(this);
		mMicroBlogView = getLayoutInflater().inflate(R.layout.sina_microblog,
				null);
		mlvMicroBlog = (ListView) mMicroBlogView.findViewById(R.id.lvMicroBlog);
		mlvStrollMicroBlog = (ListView) mMicroBlogView
				.findViewById(R.id.lvStrollMicroBlog);
		mlvMeMicroBlog = (ListView) mMicroBlogView
				.findViewById(R.id.lvMeMicroBlog);
		mlvComments = (ListView) mMicroBlogView.findViewById(R.id.lvComments);

		mlvMicroBlog.setOnItemLongClickListener(this);
		mlvStrollMicroBlog.setOnItemLongClickListener(this);
		mlvMeMicroBlog.setOnItemLongClickListener(this);
		mlvComments.setOnItemLongClickListener(this);
		mgyFunction = (Gallery) mMicroBlogView.findViewById(R.id.gyFunction);
		mFunImageAdapter = new FunImageAdapter(MicroBlog.this);
		mgyFunction.setAdapter(mFunImageAdapter);
		mgyFunction.setOnItemSelectedListener(MicroBlog.this);
		mgyFunction.setOnItemClickListener(MicroBlog.this);
		mgyFunction.setSelection(mFunImageAdapter.getFunCount());

		registerForContextMenu(mlvMicroBlog);
		registerForContextMenu(mlvComments);
		registerForContextMenu(mlvMeMicroBlog);
		registerForContextMenu(mlvStrollMicroBlog);
		// if (mDatabase == null)
		// mDatabase = new Database(this);
		try
		{

			if (mWeibo != null)
			{
				loadSinaMicroBlogView(null);
			}
			else
			{
				List<Status> statusList = (List<Status>) mCache
						.getHomeTimeline();

				if (statusList != null)
				{

					loadSinaMicroBlogView(statusList);
				}
				else
				{
					loadLoginView();
				}
			}

		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	private void loadLoginView()
	{
		setContentView(R.layout.login);
		setTitle("新浪微博客户端");
		metAccount = (EditText) findViewById(R.id.etAccount);
		metPassword = (EditText) findViewById(R.id.etPassword);
		metAccount.setText(mUtil.getString(ACCOUNT));
		metPassword.setText(mUtil.getString(PASSWORD));
		mbtnLogin = (Button) findViewById(R.id.btnLogin);
		mbtnLogin.setOnClickListener(new LoginOnClick());
	}

	private void loadView(final List<Status> statusList) throws Exception
	{
		setShowType(Const.SHOW_TYPE_HOME);

		switch (mShowType)
		{
			case SHOW_TYPE_HOME:
				try
				{
					// TODO 创建mMicroBlogAdapter对象
					mHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							mMicroBlogAdapter = new MicroBlogAdapter(
									MicroBlog.this, statusList, mWeibo,
									mCache);

						}
					});

					mCache.saveHomeTimeline(statusList);
					mMicroBlogAdapter
							.downloadCacheProfileImageForHomeTimeline();

					mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
					mHandler.sendEmptyMessage(Const.SHOW_TYPE_HOME);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				break;

			case SHOW_TYPE_STROLL:
				mHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						mStrollMicroBlogAdapter = new MicroBlogAdapter(
								MicroBlog.this, statusList, mWeibo, mCache,
								false);

					}
				});

				// mCache.saveHomeTimeline (statusList);
				// mMicroBlogAdapter.downloadCacheProfileImageForHomeTimeline();
				mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
				mHandler.sendEmptyMessage(Const.SHOW_TYPE_STROLL);
				break;
			case SHOW_TYPE_ME:
				mMeMicroBlogAdapter = new MicroBlogAdapter(MicroBlog.this,
						statusList, mWeibo, mCache, false);
				// mCache.saveHomeTimeline (statusList);
				// mMicroBlogAdapter.downloadCacheProfileImageForHomeTimeline();
				mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
				mHandler.sendEmptyMessage(Const.SHOW_TYPE_ME);
				break;
			case SHOW_TYPE_COMMENTS:
				mCommentAdapter = new MicroBlogAdapter(MicroBlog.this,
						statusList, mWeibo, mCache, false);
				// mCache.saveHomeTimeline (statusList);
				// mMicroBlogAdapter.downloadCacheProfileImageForHomeTimeline();
				mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
				mHandler.sendEmptyMessage(Const.SHOW_TYPE_COMMENTS);
				break;
			default:
				break;
		}

		mProgressDialog.dismiss();
	}

	private void loadSinaMicroBlogView(List<Status> statusList)
			throws Exception
	{
		if (mShowType == 0)
			setShowType(Const.SHOW_TYPE_HOME);
		MicroBlogAdapter microBlogAdapter = getAdapter();

		if (microBlogAdapter != null)
		{
			setShowType(mShowType);
			
			mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
			mHandler.sendEmptyMessage(mShowType);

		}
		else
		{

			if (statusList == null)
			{

				mWeibo.getTimelineAsync(new Paging(),
						new GetTimelineListener()
						{
							@Override
							public void ok(List<Status> statusList)
							{
								try
								{

									loadView(statusList);

								}
								catch (Exception e)
								{
									
								}

							}

							@Override
							public void error(Exception e)
							{

								loadExceptionProcess();

							}
						}, Const.SHOW_TYPE_HOME);

				// TODOast.makeText(this, "net", Toast.LENGTH_LONG).show();
			}
			else
			{

				mWeibo = new MyAsyncWeibo(mUtil.getString(ACCOUNT),
						mUtil.getString(PASSWORD));

				mMicroBlogAdapter = new MicroBlogAdapter(this, statusList,
						mWeibo, mCache);
				if (mShowType == SHOW_TYPE_HOME)
					mMicroBlogAdapter.loadCacheProfileImageForHomeTimeline();
				// TODO 从Cache中读取数据
				mHandler.sendEmptyMessage(Const.PROCESS_TYPE_LOAD_MICROBLOG_VIEW);
				mHandler.sendEmptyMessage(Const.SHOW_TYPE_HOME);

			}
		}

	}
}