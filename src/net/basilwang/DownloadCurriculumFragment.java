package net.basilwang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.basilwang.dao.AccountService;
import net.basilwang.dao.CurriculumService;
import net.basilwang.dao.Preferences;
import net.basilwang.entity.Semester;
import net.basilwang.listener.ActionModeListener;
import net.basilwang.listener.AddSubMenuListener;
import net.basilwang.listener.SendStudentNumListener;
import net.basilwang.listener.ShowTipListener;
import net.basilwang.sever.SendStudentNumberTask;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class DownloadCurriculumFragment extends SherlockFragment implements
		ActionModeListener, AddSubMenuListener, ShowTipListener,
		SendStudentNumListener {

	private View downCurriculumView;
	private UITableView tableView;
	private ActionMode mode;
	private String semesterToSelected;
	private List<Semester> semesters;

	@Override
	public void onPause() {
		if (mode != null) {
			mode.finish();
			mode = null;
		}
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
	public void onResume() {
		refresh();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		AddSubMenu subMenu = new AddSubMenu(menu, getActivity(), this);
		subMenu.setTipPhotoAndPreferKey(R.drawable.curriculumdownload_tip,
				Preferences.CURRICULUM_DOWNLOAD_TIP);
		subMenu.setSubMenuItemListener(new OnItemSelectedListenerImpl(),
				new DownloadOnClickListener(this, "task", "curriculum"));

		super.onCreateOptionsMenu(menu, inflater);
	}

	public int getSpinnerDefaultPosition() {
		return -1;
	}

	public void onActivityResult(int request, int result, Intent intent) {
		switch (request) {
		default:
			refresh();
			break;
		}
	}

	private void refresh() {
		populateList(tableView, downCurriculumView);
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		downCurriculumView = inflater.inflate(
				R.layout.download_curriculm_fragment, container, false);
		tableView = (UITableView) downCurriculumView
				.findViewById(R.id.downloaded_curriculum_view);
	}

	private List<Semester> getDownloadedSemesters() {
		return new CurriculumService(getActivity()).getDownloaedSemesters();
	}

	private void setSemesterToSelected(List<Semester> semesters) {
		semesterToSelected = PreferenceUtils.getPreferSemester(getActivity());
		if (semesters.size() == 1) {
			semesterToSelected = semesters.get(0).getName();
			setPerferSemester();
		}
	}

	public String getSubTitle(Semester semester) {
		if (semester.getBeginDate() != 0 && semester.getEndDate() != 0) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
			String subtitle = getStringBeginDate(dateFormat, semester) + "-"
					+ getStringEndDate(dateFormat, semester);
			return subtitle;
		}

		return "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		showTipIfNecessary();
		initView(inflater, container);
		populateList(tableView, downCurriculumView);
		return downCurriculumView;
	}

	private void populateList(UITableView tableView, View parent) {
		tableView.clear();
		semesters = getDownloadedSemesters();
		setSemesterToSelected(semesters);
		if (semesters.size() == 0) {
			tableView.addBasicItem(new BasicItem(getResources().getString(
					R.string.nocurriculum), "", false));
		} else {
			sendSteudentNum();
		}
		tableView.setClickListener(new TableViewClickListener());
		for (Semester s : semesters) {
			CurriculumItem item = new CurriculumItem(s.getName());
			if (s.getName().equals(semesterToSelected)) {
				item.setSubtitle(getSubTitle(s));
				item.setMark();
			}
			RelativeLayout view = item.getRelativeLayout();
			ViewItem viewItem = new ViewItem(view);
			tableView.addViewItem(viewItem);
		}
		tableView.commit();
	}

	private Date getBeginDate(long beginDate) {
		return new Date(beginDate);
	}

	private Date getEndDate(long endDate) {
		return new Date(endDate);
	}

	private String getStringBeginDate(DateFormat dateFormat, Semester semester) {
		return dateFormat.format(getBeginDate(semester.getBeginDate()));
	}

	private String getStringEndDate(DateFormat dateFormat, Semester semester) {
		return dateFormat.format(getEndDate(semester.getEndDate()));
	}

	/**
	 * Example:Modify 2009-2010学年第二学期 to 2009-2010|2
	 * 
	 * 
	 */
	public String convertSemesterFormatBack(String value) {
		value = value.replace("第", "|");
		value = value.replace("学期", "");
		return value;
	}

	/**
	 * Example:Modify 2009-2010|2 to 2009-2010学年第二学期
	 */
	public String ModifySemesterName(String value) {
		if (value.equals(""))
			return "";

		value = value.replace("|", "第");
		value += "学期";
		return value;
	}

	public void modeAction() {
		String[] titles = { "选中", "编辑" };
		if (mode == null) {
			mode = this.getSherlockActivity().startActionMode(
					new AddActionMode(titles, DownloadCurriculumFragment.this));
		}
	}

	public void onActionItemClickedListener(String title) {
		if (title.equals("编辑")) {
			Intent intent = new Intent();
			intent.setClass(getSherlockActivity(),
					EditSemesterBeginAndEndDateActivity.class);
			intent.putExtra("semester", semesterToSelected);
			startActivity(intent);
		} else if (title.equals("选中")) {
			setPerferSemester();
			refresh();
			mode.finish();
		}
	}

	public void setSemesterToShow(String semesterName) {
		this.semesterToSelected = semesterName;
	}

	public void setPerferSemester() {
		PreferenceUtils.modifyStringValueInPreferences(getActivity(),
				Preferences.CURRICULUM_TO_SHOW, semesterToSelected);
	}

	public void finishActionMode() {
		mode = null;
	}

	@Override
	public void showTipIfNecessary() {
		int curriculumTip = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(Preferences.CURRICULUM_TIP_SHOW, 0);
		if (curriculumTip == 0) {
			TipUtils.showTipIfNecessary(this.getActivity(),
					R.drawable.curriculumsetting_tip, this);
		}
	}

	@Override
	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(getActivity(),
				Preferences.CURRICULUM_TIP_SHOW, 1);
	}

	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			String value = parent.getItemAtPosition(position).toString();
			value = convertSemesterFormatBack(value);
			PreferenceUtils.modifyStringValueInPreferences(getActivity(),
					Preferences.CURRICULUM_TO_DOWNLOAD, value);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private class TableViewClickListener implements ClickListener {
		@Override
		public void onClick(int index) {
			String semester = semesters.get(index).getName();
			setSemesterToShow(semester);
			modeAction();
		}
	}

	private class CurriculumItem {
		private TextView title;
		private TextView subTitle;
		private ImageView mark;
		private RelativeLayout v;
		private String semester;

		public CurriculumItem(String semester) {
			initTextViews();
			this.semester = semester;
			this.title.setText(ModifySemesterName(semester));
		}

		private void initTextViews() {
			LayoutInflater mInflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = (RelativeLayout) mInflater.inflate(
					R.layout.download_curriculum_item, null);
			this.title = (TextView) v.findViewById(R.id.item_title);
			this.subTitle = (TextView) v.findViewById(R.id.item_subTitle);
		}

		public void setMark() {
			this.mark = (ImageView) v.findViewById(R.id.item_mark);
			this.mark.setImageResource(R.drawable.mark);
		}

		public void setSubtitle(String subTitle) {
			this.subTitle.setText(subTitle);
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
