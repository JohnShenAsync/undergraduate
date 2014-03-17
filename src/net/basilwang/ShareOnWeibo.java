package net.basilwang;

import java.io.IOException;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShareOnWeibo extends Activity
{

	EditText editText;
	Button btnShare;
	StatusesAPI api = new StatusesAPI(StaticAttachmentActivity.accessToken);

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareonweibo);

		editText = (EditText) findViewById(R.id.text_to_share);
		btnShare = (Button) findViewById(R.id.btnShare);

		btnShare.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				// Toast.makeText(ShareOnWeibo.this,
				// editText.getText().toString(), Toast.LENGTH_LONG).show();
                
				if(editText.getText().toString().replaceAll(" ", "").equals(""))
				{
					Toast.makeText(ShareOnWeibo.this, "跟我们说点什么吧~", Toast.LENGTH_SHORT).show();
				}
				
				else
				{
					api.update(
							" @山财大学生助手:\t\t\t"
									+ editText.getText().toString()
									+ "  \t\t\r\n   下载地址： http://www.mumayi.com/android-120469.html",
							"", // 这两个参数貌似是地图的位置信息，暂时使用空
							"", // 这两个参数貌似是地图的位置信息,暂时使用空
							new RequestListener()
							{

								@Override
								public void onIOException(IOException arg0)
								{
									// TODO Auto-generated method stub

								}

								@Override
								public void onError(WeiboException arg0)
								{
									// TODO Auto-generated method stub

								}

								@Override
								public void onComplete(String arg0)
								{
									// TODO Auto-generated method stub

								}
							});
					
					Toast.makeText(ShareOnWeibo.this, "感谢您的评价，我们将会继续努力",
							Toast.LENGTH_LONG).show();
					ShareOnWeibo.this.finish();
				}
			}
		});

	}

}
