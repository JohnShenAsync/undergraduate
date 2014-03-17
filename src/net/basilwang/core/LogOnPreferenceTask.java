package net.basilwang.core;

import net.basilwang.CheckCodeDialog;
import net.basilwang.R;
import net.basilwang.enums.TAHelperDownloadPhrase;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class LogOnPreferenceTask extends AsyncTask<String, Integer, Boolean> {
	static final String TAG = "LogOnPreferenceTask";
	private Context mContext;
	private AsyncTask<Integer, Integer, String> mTask;
	String title;
	private int accountId;
	int lastMileStone=0;
	private int mileStoneInterval=0;
	public LogOnPreferenceTask(Context context,
			AsyncTask<Integer, Integer, String> task) {
		// TAConfiguration config = TAConfiguration.Instance();
		// TAHelper.initWithTAConfiguration(config);
		mContext = context;
		mTask = task;
	}

	@Override
	protected void onPreExecute() {

		// ((Activity) mContext).setProgressBarVisibility(true);
		title = ((Activity) mContext).getTitle().toString();
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		accountId = Integer.valueOf(params[3]);
		// 2012-04-21 basilwang honeycomb request network access on new thread
		// try {
		// TAHelper.Instance().setSessionID();
		// } catch (SessionIDNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Log.v("Undergraduate", TAContext.Instance().getSessionid());
		return HttpClient(params);

	}

	private Boolean HttpClient(String... params) {
		OnDownloadProgressListener listener = new OnDownloadProgressListener() {

			@Override
			public void onDownloadProgress(int percent, int downloadedData,
					int total,TAHelperDownloadPhrase phrase) {

				publishProgress(percent, downloadedData, total,phrase.getValue());
			}

		};
		return TAHelper.Instance().logOn(params[0], params[1], params[2],
				listener);
	}

	protected void onProgressUpdate(Integer... progress) {
		// ProgressBar
		// mBar=(ProgressBar)(((Activity)mContext).findViewById(R.id.progressBar));
		// mBar.setProgress(progress[0]);
		// 2012-04-14 basilwang Window.FEATURE_PROGRESS max progress is 10000,so
		// we mutiply by 100
		// 2012-11-24 basilwang 

		if(progress[3]>lastMileStone)
		{
			mileStoneInterval=progress[3]-lastMileStone;
		}
		int progressInterval=(int)(progress[0] * mileStoneInterval/100);
		((CheckCodeDialog) mContext).getProgressBar().setProgress(lastMileStone+progressInterval);
		if(progress[0]==100){
			lastMileStone+=mileStoneInterval;
		}
		// ((Activity) mContext).setProgress(progress[0] * 100);
		int lp = progress[1] / 1000;
		int rp = progress[2] / 1000;
		// ((Activity) mContext).setTitle(title + "  " + lp + "K/" + rp + "K");
	}

	protected void onPostExecute(Boolean isLogOn) {
		if (isLogOn) {
//			Intent i = new Intent("net.basilwang.intents.progress").putExtra("progress", 30);
//			Log.i(TAG, "progress is 30");
//		    this.mContext.sendBroadcast(i); 
			mTask.execute(accountId);
		} else {
//	        Intent i = new Intent("net.basilwang.intents.progress").putExtra("progress", 100);
//	        this.mContext.sendBroadcast(i);
			// 2012-11-8 WeiXiaoXing 
			((Activity) mContext).finish();
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.errorpasswordmaybe), Toast.LENGTH_LONG)
					.show();
			
		}
	}
}
