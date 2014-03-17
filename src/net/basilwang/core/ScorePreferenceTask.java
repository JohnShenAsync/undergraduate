package net.basilwang.core;

import net.basilwang.CheckCodeDialog;
import net.basilwang.dao.ScoreService;
import net.basilwang.enums.TAHelperDownloadPhrase;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
 
public class ScorePreferenceTask extends AsyncTask<Integer, Integer, String> {
	static final String TAG = "ScorePreferenceTask";
	private Context mContext;
	String title;
	private int accountId;
	private ScoreService scoreService;
	private String cemesterIndex;
	private String cemesterYear;
	private String semesterValue;
	int lastMileStone=15;
	private int mileStoneInterval=0;
	public ScorePreferenceTask(Context context) {
		mContext = context;
		scoreService = new ScoreService(context);
		semesterValue = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString("score_semester_name", "");
		

	}

	@Override
	protected void onPreExecute() {

		// ((Activity) mContext).setProgressBarVisibility(true);
		title = ((Activity) mContext).getTitle().toString();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Integer... params) {
		accountId = params[0];
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

	private String HttpClient(Integer... params) {
		OnDownloadProgressListener listener = new OnDownloadProgressListener() {

			@Override
			public void onDownloadProgress(int percent, int downloadedData,
					int total,TAHelperDownloadPhrase phrase) {

				publishProgress(percent, downloadedData, total,phrase.getValue());
			}

		};
		String[] cemesterSettings = semesterValue.split("\\|");
		cemesterYear = cemesterSettings[0];
		cemesterIndex = cemesterSettings[1];
		cemesterYear = cemesterYear.replace('–', '-');
		return TAHelper.Instance().getScore(listener, cemesterYear,
				cemesterIndex);
	}

	protected void onProgressUpdate(Integer... progress) {
		// ProgressBar
		// mBar=(ProgressBar)(((Activity)mContext).findViewById(R.id.progressBar));
		// mBar.setProgress(progress[0]);
		// 2012-04-14 basilwang Window.FEATURE_PROGRESS max progress is 10000,so
		// we mutiply by 100
		Log.i(TAG,"lastMileStone is "+String.valueOf(lastMileStone));
		if(progress[3]>lastMileStone)
		{
			mileStoneInterval=progress[3]-lastMileStone;
			Log.i(TAG,String.valueOf(mileStoneInterval));
		}
		int progressInterval=(int)(progress[0] * mileStoneInterval/100);
		((CheckCodeDialog) mContext).getProgressBar().setProgress(lastMileStone+progressInterval);
		if(progress[0]==100){
			lastMileStone=progress[3];
			Log.i(TAG,"percent is 100 and lastMileStone is "+ String.valueOf(lastMileStone));
		}
		// ((Activity) mContext).setTitle(title + "  " + lp + "K/" + rp + "K");
	}
 
	protected void onPostExecute(String scoreString) {
//		Intent i = new Intent("net.basilwang.intents.progress").putExtra("progress", 60);
//	    this.mContext.sendBroadcast(i); 
//	    Log.i(TAG, "progress is 60");
		String semesterName = cemesterYear + "|" + cemesterIndex;
		scoreService.formatAndSaveScoreById(semesterName,
				scoreString, accountId);
		// Button mScoreUpdateButton = (Button) ((Activity) mContext)
		// .findViewById(R.id.scheduling_score_update);
		// mScoreUpdateButton.setEnabled(true);
		// Button mCurriculumUpdateButton = (Button) ((Activity) mContext)
		// .findViewById(R.id.scheduling_update);
		// mCurriculumUpdateButton.setEnabled(true);
		// Toast.makeText(mContext,
		// TAUtils.getCemesterIndex(mContext,cemesterValue) + "成绩下载成功，请到成绩页面查看",
		// Toast.LENGTH_SHORT).show();
		// Update the progress bar
//		i.putExtra("progress", 100);
//	    this.mContext.sendBroadcast(i);
//	    Log.i(TAG, "progress is 100");
		((CheckCodeDialog) mContext).finish();
		((CheckCodeDialog) mContext).getProgressBar().dismiss();
 

		
	}

}
