package mobile.android.demo.sina.microblog;

import mobile.android.demo.sinamb.Const;
import weibo4j.OnStatusListener;
import weibo4j.Status;
import weibo4j.http.ImageItem;
import android.app.Activity;
import android.app.ProgressDialog;  
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteMicroBlog extends Activity implements OnItemClickListener,
		OnClickListener, TextWatcher, OnMenuItemClickListener, Const
{
	private Gallery mgyMicroBlogFun;
	private TextView mtvRemainWordCount;
	private int mOldPosition = 8;
	private View mOldView;
	private WriteMBFunImageAdapter mWriteMBFunImageAdapter;
	private EditText metWriteMicroBlog;
	private ImageView mivMicroBlogImage;
	private String mImageFilename;
	private ProgressDialog mProgressDialog;
	public static Bitmap mCameraBitmap;
	private Cache mCache;
	private Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mProgressDialog.cancel();
			switch (msg.what)
			{
				case 1:
					Util.showMsg(WriteMicroBlog.this, "发布成功.");
					mCache.clearMicroBlogData();
					mCameraBitmap = null;
					metWriteMicroBlog.setText("");
					finish();
					break;

				default:

					Util.showMsg(WriteMicroBlog.this, "发布失败.");
					break;
			}
		}

	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, view, menuInfo);
		MenuItem mnuEditImage = menu.add(Const.CONTEXT_MENUITEM,
				Const.CONTEXT_MENUITEM_EDIT_IMAGE, 1, "编辑图像");
		mnuEditImage.setOnMenuItemClickListener(this);

		if (mCameraBitmap != null)
		{
			MenuItem mnuDeleteImage = menu.add(Const.CONTEXT_MENUITEM,
					Const.CONTEXT_MENUITEM_DELETE_IMAGE, 2, "删除图像");

			mnuDeleteImage.setOnMenuItemClickListener(this);
		}

	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		Intent intent = null;
		switch (item.getItemId())
		{
			case Const.CONTEXT_MENUITEM_EDIT_IMAGE:
				intent = new Intent(this, EditImage.class);
				startActivityForResult(intent, 4);
				break;
			case Const.CONTEXT_MENUITEM_DELETE_IMAGE:
				mCameraBitmap = null;
				mivMicroBlogImage.setImageBitmap(mCameraBitmap);

				break;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_microblog);
		mCache = new Cache(this);
		mgyMicroBlogFun = (Gallery) findViewById(R.id.gyWriteMBFun);

		mWriteMBFunImageAdapter = new WriteMBFunImageAdapter(this);
		mgyMicroBlogFun.setAdapter(mWriteMBFunImageAdapter);

		mgyMicroBlogFun.setOnItemClickListener(this);
		mgyMicroBlogFun.setSelection(9);

		metWriteMicroBlog = (EditText) findViewById(R.id.etWriteMicroBlog);

		mtvRemainWordCount = (TextView) findViewById(R.id.tvRemainWordCount);
		metWriteMicroBlog.addTextChangedListener(this);
		Button btnPostNewMicroBlog = (Button) findViewById(R.id.btnPostNewMicroBlog);
		btnPostNewMicroBlog.setOnClickListener(this);
		mivMicroBlogImage = (ImageView) findViewById(R.id.ivMicroBlogImage);

		if (mCameraBitmap != null)
		{
			mivMicroBlogImage.setImageBitmap(mCameraBitmap);
		}

		metWriteMicroBlog.setText(mCache.getStatus());
		mCameraBitmap = mCache.getBitmap();
		mivMicroBlogImage.setImageBitmap(mCameraBitmap);
		registerForContextMenu(mivMicroBlogImage);

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mCache.saveMicroBlogData(metWriteMicroBlog.getText().toString(),
				mCameraBitmap);
	}

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
		int remainWordCount = Util.getRemainWordCount(metWriteMicroBlog
				.getText().toString());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
		{
			switch (requestCode)
			{
				case 1:
					mCameraBitmap = (Bitmap) data.getExtras().get("data");
					try
					{

						mivMicroBlogImage.setImageBitmap(mCameraBitmap);

					}
					catch (Exception e)
					{

					}

					break;
				case 2:
					Uri uri = data.getData();

					// ContentResolver cr = getContentResolver();
					try
					{
						Cursor cursor = getContentResolver().query(uri, null,
								null, null, null);
						cursor.moveToFirst();
						String imageFilePath = cursor.getString(1);
						cursor.close();
						Options options = new Options();
						options.inSampleSize = Util
								.getSampleSize(imageFilePath);
						if (options.inSampleSize > 0)
							mCameraBitmap = BitmapFactory.decodeFile(
									imageFilePath, options);
						else
							mCameraBitmap = BitmapFactory
									.decodeFile(imageFilePath);
						mivMicroBlogImage.setImageBitmap(mCameraBitmap);

					}
					catch (Exception e)
					{
						setTitle(e.getMessage());
					}
					break;
				case 3:
					// 选择表情
					String face = data.getStringExtra("face");
					StringBuilder sb = new StringBuilder(metWriteMicroBlog
							.getText().toString());
					int start = metWriteMicroBlog.getSelectionStart();
					sb.insert(start, face);

					metWriteMicroBlog.setText(sb.toString());
					metWriteMicroBlog.setSelection(start + face.length());

					break;
				case 4:
					mivMicroBlogImage.setImageBitmap(mCameraBitmap);
					break;
				default:
					break;
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id)
	{
		if (mOldPosition > -1)
		{

			mWriteMBFunImageAdapter.setImage(view, position, mOldView,
					mOldPosition);
			mOldPosition = position;
			mOldView = view;
		}
		Intent intent = null;
		switch (mWriteMBFunImageAdapter.getIndex(position))
		{
			case R.drawable.camera:

				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
				break;
			case R.drawable.album:
				intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 2);
				break;
			case R.drawable.smile:
				intent = new Intent(this, FaceList.class);
				startActivityForResult(intent, 3);
				break;
			case R.drawable.edit_image:
				intent = new Intent(this, EditImage.class);
				startActivityForResult(intent, 4);
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btnPostNewMicroBlog:

				String content = metWriteMicroBlog.getText().toString().trim();
				if (mCameraBitmap != null && "".equals(content))
				{
					content = "分享图片.";
				}
				if (!"".equals(content))
				{

					mProgressDialog = Util.showSpinnerProgressDialog(this,
							"正在发布微博...");

					try
					{
						if (mCameraBitmap == null)
						{
							MicroBlog.mWeibo.updateStatusAsync(content,
									new OnStatusListener()
									{

										@Override
										public void onSuccess(Status status)
										{

											mHandler.sendEmptyMessage(1);
										}

										@Override
										public void onException(Exception e)
										{

											mHandler.sendEmptyMessage(10);
										}
									});
						}
						else
						{

							byte[] buffer = Util
									.bitmapToByteArray(mCameraBitmap);
							ImageItem pic = new ImageItem("pic", buffer);
							MicroBlog.mWeibo.uploadStatusAsync(content,
									pic, new OnStatusListener()
									{

										@Override
										public void onSuccess(Status status)
										{
											mHandler.sendEmptyMessage(1);

										}

										@Override
										public void onException(Exception e)
										{
											mHandler.sendEmptyMessage(10);

										}
									});

						}

					}
					catch (Exception e)
					{
						mHandler.sendEmptyMessage(10);

					}

				}
				break;

			default:
				break;
		}

	}
}
