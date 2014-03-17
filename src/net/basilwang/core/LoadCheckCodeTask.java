package net.basilwang.core;

import net.basilwang.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadCheckCodeTask extends AsyncTask<String, Integer, Bitmap> {
	private Context mContext;

	public LoadCheckCodeTask(Context context) {
		mContext = context;

	}

	@Override
	protected Bitmap doInBackground(String... params) {
		return TAHelper.Instance().getCheckCode();
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(Bitmap bitmapCheckCode) {
		ImageView mImageCheckCode = (ImageView) (((Activity) mContext)
				.findViewById(R.id.score_checkcode_image));
		if (mImageCheckCode != null) {
			if (bitmapCheckCode != null)
				mImageCheckCode.setImageBitmap(bitmapCheckCode);
			else {
				// 2012-07-09 basilwang sometimes we can't see verify code
				Toast.makeText(mContext, "教务系统无法登录，请稍后再试", Toast.LENGTH_SHORT)
						.show();

			}
		}
	}

	public void setContext(Context context) {
		mContext = context;

	}

}
