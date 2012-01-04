package weibo4j;

import java.util.List;

import mobile.android.demo.sinamb.Const;
import weibo4j.http.ImageItem;


public class MyAsyncWeibo extends Weibo
{
	private static final long serialVersionUID = -1486360080128882436L;

	public MyAsyncWeibo(String id, String password)
	{
		super(id, password);
	}

	public void verifyCredentialsAsync(final VerifyCredentialsListener listener)
			throws Exception
	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				try
				{
					User user = verifyCredentials();

					listener.ok(user);

				}
				catch (Exception e)
				{
					listener.error(e);
				}
			}
		});
		thread.start();
	}

	public void getHomeTimelineAsync(final GetTimelineListener listener)
			throws Exception
	{
		
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					List<Status> statusList =  getHomeTimeline();
					listener.ok(statusList);

				}
				catch (Exception e)
				{
					listener.error(e);
				}

			}
		});
		thread.start();

	}

	public void getPublicTimelineAsync(final GetTimelineListener listener)

	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					List<Status> statusList = getPublicTimeline();
					listener.ok(statusList);

				}
				catch (Exception e)
				{
					listener.error(e);
				}

			}
		});
		thread.start();

	}

	public void getHomeTimelineAsync(final Paging paging,
			final GetTimelineListener listener) throws Exception
	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					List<Status> statusList = getHomeTimeline(paging);
					listener.ok(statusList);

				}
				catch (Exception e)
				{
					listener.error(e);
				}

			}
		});
		thread.start();

	}

	public void getPublicTimelineAsync(final Paging paging,
			final GetTimelineListener listener) throws Exception
	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					List<Status> statusList = getPublicTimeline(paging);
					listener.ok(statusList);

				}
				catch (Exception e)
				{
					listener.error(e);
				}

			}
		});
		thread.start();

	}

	public void getTimelineAsync(final Paging paging,
			final GetTimelineListener listener, final int showType)
			throws Exception
	{
		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					List<Status> statusList = null;
					switch (showType)
					{
						case Const.SHOW_TYPE_HOME:
							statusList = getHomeTimeline(paging);
							break;
						case Const.SHOW_TYPE_STROLL:
							statusList = getPublicTimeline();
							break;
						case Const.SHOW_TYPE_ME:
							statusList = getMentions(paging);

							break;
						case Const.SHOW_TYPE_ME_BLOG:
							statusList = getUserTimeline(paging);
							break;
					}

					listener.ok(statusList);

				}
				catch (Exception e)
				{
					listener.error(e);
				}

			}
		});
		thread.start();

	}

	public void updateCommentAsync(final String msg, final String id,
			final String cid, final OnUpdateCommentListener listener)
	{

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Comment comment = updateComment(msg, id, cid);
					listener.onSuccess(comment);

				}
				catch (Exception e)
				{
					listener.onException(e);
				}

			}
		});
		thread.start();
	}
	public void updateStatusAsync(final String msg, final long inReplyToStatusId, final OnStatusListener listener)
	{

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Status status = updateStatus(msg, inReplyToStatusId);
					listener.onSuccess(status);

				}
				catch (Exception e)
				{
					listener.onException(e);
				}

			}
		});
		thread.start();
	}
	public void updateStatusAsync(final String msg, final OnStatusListener listener)
	{

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Status status = updateStatus(msg);
					listener.onSuccess(status);

				}
				catch (Exception e)
				{
					listener.onException(e);
				}

			}
		});
		thread.start();
	}
	public void uploadStatusAsync(final String msg,final ImageItem item, final OnStatusListener listener)
	{

		Thread thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					Status status = uploadStatus(msg, item);
					listener.onSuccess(status);

				}
				catch (Exception e)
				{
					listener.onException(e);
				}

			}
		});
		thread.start();
	}
}
