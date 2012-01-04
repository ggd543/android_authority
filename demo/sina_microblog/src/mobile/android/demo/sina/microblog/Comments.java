package mobile.android.demo.sina.microblog;


import weibo4j.Comment;
import weibo4j.OnUpdateCommentListener;
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

public class Comments extends Activity implements OnClickListener, TextWatcher
{
	private EditText metComment;
	private TextView mtvRemainWordCount;
	private String mStatusId;
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

					Util.showMsg(Comments.this, "评论发布成功.");
					finish();
					break;

				default:
					Util.showMsg(Comments.this, "评论发布失败.");
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
		int remainWordCount = Util.getRemainWordCount(metComment.getText()
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
		setContentView(R.layout.comments);
		Button btnPostComment = (Button) findViewById(R.id.btnPostComment);
		metComment = (EditText) findViewById(R.id.etComment);
		mtvRemainWordCount = (TextView) findViewById(R.id.tvRemainWordCount);
		metComment.addTextChangedListener(this);
		btnPostComment.setOnClickListener(this);

		mStatusId = getIntent().getExtras().getString("statusId");
	}

	@Override
	public void onClick(View view)
	{
		String msg = metComment.getText().toString().trim();
		if ("".equals(msg))
			return;

		mProgressDialog = Util.showSpinnerProgressDialog(this, "正在发布评论...");

		MicroBlog.mWeibo.updateCommentAsync(msg, mStatusId, null,
				new OnUpdateCommentListener()
				{

					@Override
					public void onSuccess(Comment comment)
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
