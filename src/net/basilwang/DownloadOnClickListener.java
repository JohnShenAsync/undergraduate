package net.basilwang;

import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.StringUtils;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

/**
 * 2013-09-13 OnCliCkListener For Download Curriculum and Score
 * 
 * @author WeiXiaoXing
 * 
 */
public class DownloadOnClickListener implements View.OnClickListener {
	private Fragment fragment;
	private String taskName;
	private String taskValue;

	public DownloadOnClickListener(Fragment fragment, String taskName,
			String taskValue) {
		this.fragment = fragment;
		this.taskName = taskName;
		this.taskValue = taskValue;
	}

	@Override
	public void onClick(View v) {
		Context context = v.getContext();
		if (isLogOn(context)) {
			Intent i = new Intent(v.getContext(), CheckCodeDialog.class);
			i.putExtra(taskName, taskValue);
			fragment.startActivityForResult(i, 0x1);
		} else {
			Toast.makeText(context, getTip(context), Toast.LENGTH_SHORT).show();
		}
	}

	private String getTip(Context context) {
		return context.getResources().getString(R.string.no_account_tip);
	}

	private boolean isLogOn(Context context) {
		int accountId = PreferenceUtils.getPreferAccountId(context);
		boolean flag = true;
		if (accountId == 0) {
			flag = false;
		}
		return flag;
	}
}
