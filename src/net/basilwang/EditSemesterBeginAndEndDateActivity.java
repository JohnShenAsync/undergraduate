package net.basilwang;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Semester;
import net.basilwang.listener.ActionModeListener;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;

public class EditSemesterBeginAndEndDateActivity extends SherlockActivity
		implements ActionModeListener {
	private UITableView tableView;
	private ActionMode mode;
	private Calendar cal;
	private Semester selectedSemester;
	private SemesterService semesterService;
	DateFormat dateFormat;
	private List<BasicItem> items;

	private void tableViewShowList() {
		tableView.clear();
		tableView.setClickListener(new DateTableViewOnClickListener());
		for (int i = 0; i < items.size(); i++) {
			tableView.addBasicItem(items.get(i));
		}
		tableView.commit();
	}

	public void initItem() {
		semesterService = new SemesterService(getActivity());
		selectedSemester = semesterService
				.getSemesterByName(getSemsterNameInIntent());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		BasicItem beginDateItem = new BasicItem("单击设置学期起始时间");
		if (selectedSemester.getBeginDate() != 0) {
			beginDateItem.setSubtitle(getStringValueOfBeginDate());
		}
		BasicItem endDateItem = new BasicItem("单击设置学期结束时间");
		if (selectedSemester.getEndDate() != 0) {
			endDateItem.setSubtitle(getStringValueOfEndDate());
		}
		items = new ArrayList<BasicItem>(2);
		items.add(beginDateItem);
		items.add(endDateItem);
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

	private String getStringValueOfBeginDate() {
		return dateFormat.format(new Date(selectedSemester.getBeginDate()));
	}

	private String getStringValueOfEndDate() {
		return dateFormat.format(new Date(selectedSemester.getEndDate()));
	}

	private void initView() {
		cal = Calendar.getInstance();
		setContentView(R.layout.edit_semester);
		TextView textviewTitle = (TextView) findViewById(R.id.edit_semester_textView);
		textviewTitle.setText(ModifySemesterName(getSemsterNameInIntent()));
		tableView = (UITableView) findViewById(R.id.edit_semester_tableView);
		String[] titles = { "保存" };
		mode = startActionMode(new AddActionMode(titles, this));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initItem();
		initView();
		tableViewShowList();
	}

	private String getSemsterNameInIntent() {
		return this.getIntent().getExtras().getString("semester");
	}

	public void setBeginDate(int year, int monthOfYear, int dayOfMonth,
			int index) {
		cal.set(year, monthOfYear, dayOfMonth);
		selectedSemester.setBeginDate(cal.getTime().getTime());
		items.get(index).setSubtitle(getStringValueOfBeginDate());
		refreshTableView();
	}

	public void refreshTableView() {
		tableViewShowList();
	}

	public void setEndDate(int year, int monthOfYear, int dayOfMonth, int index) {
		cal.set(year, monthOfYear, dayOfMonth);
		selectedSemester.setEndDate(cal.getTime().getTime());
		items.get(index).setSubtitle(getStringValueOfEndDate());
		refreshTableView();
	}

	private Activity getActivity() {
		return EditSemesterBeginAndEndDateActivity.this;
	}

	@Override
	public void onActionItemClickedListener(String title) {
		if (title.equals("保存")) {
			if (updateBeginAndEndDate() == true) {
				mode.finish();
			}
		}
	}

	public boolean updateBeginAndEndDate() {
		if (selectedSemester.getBeginDate() == 0
				|| selectedSemester.getEndDate() == 0) {
			Toast.makeText(getActivity(), "没有设置起始或结束日期.不能保存..",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (selectedSemester.getBeginDate() > selectedSemester
				.getEndDate()) {
			Toast.makeText(getActivity(), "起始日期大于结束日期.不能保存..",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			semesterService.updateBeginAndEndDataOfSemester(selectedSemester);
		}
		return true;
	}

	@Override
	public void finishActionMode() {
		EditSemesterBeginAndEndDateActivity.this.finish();
	}

	private class DateTableViewOnClickListener implements ClickListener {
		private int year;
		private int monthOfYear;
		private int dayOfMonth;

		public DateTableViewOnClickListener() {
			this.initDate();
		}

		public void initDate() {
			year = cal.get(Calendar.YEAR);
			monthOfYear = cal.get(Calendar.MONTH);
			dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		}

		@Override
		public void onClick(int index) {
			new DatePickerDialog(getActivity(), new DatePickerDialogListener(
					index), year, monthOfYear, dayOfMonth).show();
		}
	}

	class DatePickerDialogListener implements OnDateSetListener {
		private int index;

		public DatePickerDialogListener(int index) {
			this.index = index;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (index == 0) {
				setBeginDate(year, monthOfYear, dayOfMonth, index);
			} else if (index == 1) {
				setEndDate(year, monthOfYear, dayOfMonth, index);
			}
		}
	}
}
