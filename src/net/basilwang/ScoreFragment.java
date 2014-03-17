package net.basilwang;

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.util.List;

import net.basilwang.dao.AccountService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.ScoreService;
import net.basilwang.entity.Score;
import net.basilwang.listener.AddSubMenuListener;
import net.basilwang.listener.SendStudentNumListener;
import net.basilwang.listener.ShowTipListener;
import net.basilwang.sever.SendStudentNumberTask;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class ScoreFragment extends SherlockFragment implements
		AddSubMenuListener, ShowTipListener ,SendStudentNumListener{
	private ScoreService scoreService;
	private UITableView mTableView;
	private View scoreView;
	private String semesterIndex;

	public void refresh() {
		/*
		 * If you encounter a bug in this method,you can try to add this line
		 * below mTableView.clear(); mTableView.commit();
		 */
		populateList(mTableView, scoreView);
	}

	public void onActivityResult(int request, int result, Intent intent) {
		switch (request) {
		default:
			refresh();
			break;
		}
	}

	public void initView(LayoutInflater inflater, ViewGroup container) {
		semesterIndex = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getString(Preferences.SCORE_SEMESTER_NAME,
				"2011-2012|1");
		scoreService = new ScoreService(this.getActivity());
		scoreView = inflater.inflate(R.layout.score_fragment, container, false);
		mTableView = (UITableView) scoreView.findViewById(R.id.scoreView);
		SemesterArrayAdapter semesterAdapter = new SemesterArrayAdapter(
				this.getActivity(), android.R.layout.simple_spinner_item);
		semesterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 2012-07-11 basilwang has its own menu
		this.setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		if (new AccountService(getActivity()).getAccounts().size() <= 0) {
			Toast.makeText(getActivity(), "请绑定您的学号", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getActivity(),
					LogonPreferenceActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		AddSubMenu subMeun = new AddSubMenu(menu, getActivity(), this);
		subMeun.setTipPhotoAndPreferKey(R.drawable.curriculumdownload_tip,
				Preferences.SOCRE_DOWNLOAD_TIP);

		subMeun.setSubMenuItemListener(new OnItemSelectedListenerImpl(),
				new DownloadOnClickListener(this, "task", "score"));

		super.onCreateOptionsMenu(menu, inflater);
	}

	public int getSpinnerDefaultPosition() {
		return PreferenceManager
				.getDefaultSharedPreferences(this.getActivity()).getInt(
						Preferences.SCORE_SPINNER_SELECTION, -1);
	}

	public void showTipIfNecessary() {
		int scoreTip = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(Preferences.SCORE_TIP_SHOW, 0);
		if (scoreTip == 0) {
			TipUtils.showTipIfNecessary(this.getActivity(),
					R.drawable.downloadsetting_tip, this);
		}
	}

	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(getActivity(),
				Preferences.SCORE_TIP_SHOW, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		showTipIfNecessary();
		initView(inflater, container);
		if (!Preferences.isAdClosed(this.getActivity())) {
			// 应用Id 应用密码 广告请求间隔(s) 测试模式
			AdManager.init(this.getActivity(), "2fc95b356bb979ae",
					"8b94f727980f7158", 30, false);
			LinearLayout adViewLayout = (LinearLayout) scoreView
					.findViewById(R.id.adViewLayout);
			adViewLayout.addView(new AdView(this.getActivity()),
					new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		populateList(mTableView, scoreView);
		return scoreView;
	}

	public void setSemesterIndex(String semesterIndex) {
		this.semesterIndex = semesterIndex;
	}

	protected void populateList(UITableView mTableView, View parent) {
		mTableView.clear();
		int accountId = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(LOGON_ACCOUNT_ID, 0);
		String[] semesterSettings = semesterIndex.split("|");
		if (semesterSettings.length == 1) {
			mTableView.addBasicItem(new BasicItem(getResources().getString(
					R.string.noaccount), "", false));
		} else {
			List<Score> scoreList = this.scoreService.getScores(semesterIndex,
					accountId);

			if (scoreList.size() == 0) {
				mTableView.addBasicItem(new BasicItem(getResources().getString(
						R.string.noscore), "", false));
			} else {
				sendSteudentNum();
				for (Score c : scoreList) {
					RelativeLayout v = new ScoreItem(c.getScore(),
							c.getCourseName(), c.getScorePoint())
							.getRelativeLayout();
					ViewItem v2 = new ViewItem(v);
					v2.setClickable(false);
					mTableView.addViewItem(v2);
				}
			}
		}
		mTableView.commit();
	}

	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String value = parent.getItemAtPosition(position).toString();
			value = value.replace("第", "|");
			value = value.replace("学期", "");
			PreferenceUtils.modifyIntValueInPreferences(getActivity(),
					Preferences.SCORE_SPINNER_SELECTION, position);
			PreferenceUtils.modifyStringValueInPreferences(getActivity(),
					Preferences.SCORE_SEMESTER_NAME, value);
			setSemesterIndex(value);
			refresh();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private class ScoreItem {
		private TextView name;
		private TextView point;
		private TextView score;
		private RelativeLayout v;

		public ScoreItem(int score, String name, float scorePoint) {
			initTextViews();
			this.name.setText(name);
			this.point.setText("绩点:" + String.valueOf(scorePoint));
			this.score.setText("成绩:" + String.valueOf(score));
		}

		public void initTextViews() {
			LayoutInflater mInflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = (RelativeLayout) mInflater.inflate(
					R.layout.custom_view_for_score, null);
			this.name = (TextView) v.findViewById(R.id.score_name);
			this.point = (TextView) v.findViewById(R.id.point);
			this.score = (TextView) v.findViewById(R.id.score);
		}

		public RelativeLayout getRelativeLayout() {
			return v;
		}
	}

	@Override
	public void sendSteudentNum() {
		if (!PreferenceUtils.getPreferHadSendUserNo(getActivity())) {
			String token = PreferenceUtils.getPreferToken(getActivity());
			String userno = getStudentNum();
			SendStudentNumberTask sendTask = new SendStudentNumberTask();
			sendTask.execute(token, userno);
			PreferenceUtils.modifyBooleanValueInPreferences(getActivity(),
					Preferences.HAD_SEND_USERNO, true);
		}
	}

	@Override
	public String getStudentNum() {
		return new AccountService(getActivity()).getAccountById(
				PreferenceUtils.getPreferAccountId(getActivity())).getUserno();
	}
}
