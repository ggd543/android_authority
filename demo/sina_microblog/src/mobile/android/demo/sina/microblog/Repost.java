package mobile.android.demo.sina.microblog;

import weibo4j.OnStatusListener;
import weibo4j.Status;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Repost extends Activity implements OnClickListener, TextWatcher
{
	private EditText metRepost;
	private TextView mtvRemainWordCount;
	private long mStatusId;
	private String mText;
	private String mStatusUserName;
	private ProgressDialog mProgressDialog;

	private Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mProgressDialog.cancel();
			switch (msg.what)
			{
				case 0:

					Util.showMsg(Repost.this, "转发成功.");
					finish();
					break;

				default:

					Util.showMsg(Repost.this, "转发失败");
					break;
			}

		}

	};

	@Override
	public void afterTextChanged(Editable s)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		int remainWordCount = Util.getRemainWordCount(metRepost.getText()
				.toString());
		if (remainWordCount >= 0)
		{
			mtvRemainWordCount.setText("可输入字数：" + remainWordCount);
			mtvRemainWordCount.setTextColor(Color.WHITE);
		}

		else
		{
			remainWordCount = -remainWordCount;
			mtvRemainWordCount.setText("超出字数：" + remainWordCount);
			mtvRemainWordCount.setTextColor(Color.RED);
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repost);
		Button btnRepost = (Button) findViewById(R.id.btnRepost);
		metRepost = (EditText) findViewById(R.id.etRepost);
		mtvRemainWordCount = (TextView) findViewById(R.id.tvRemainWordCount);
		metRepost.addTextChangedListener(this);
		btnRepost.setOnClickListener(this);
		mStatusId = getIntent().getExtras().getLong("statusId");
		boolean haveRetweetDetails = getIntent().getExtras().getBoolean(
				"haveRetweetDetails");
		if (haveRetweetDetails)
		{
			
			mText = getIntent().getExtras().getString("text");
			mStatusUserName = getIntent().getExtras().getString("username");
			metRepost.setText("//@" + mStatusUserName + ":" + mText);
		}
	}

	@Override
	public void onClick(View view)
	{
		String msg = metRepost.getText().toString().trim();
		if ("".equals(msg))
			msg = "转发微博";

		mProgressDialog = Util.showSpinnerProgressDialog(this, "正在转发...");

		MicroBlog.mWeibo.updateStatusAsync(msg, mStatusId,
				new OnStatusListener()
				{

					@Override
					public void onSuccess(Status status)
					{

						mHandler.sendEmptyMessage(0);

					}

					@Override
					public void onException(Exception e)
					{
						Message message = new Message();
						message.obj = e.getMessage();
						message.what = 1;
						mHandler.sendMessage(message);
					}
				});

	}
}
