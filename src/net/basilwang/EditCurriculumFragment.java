package net.basilwang;

import net.basilwang.dao.ArrayWheelAdapter;
import net.basilwang.dao.NumericWheelAdapter;
import net.basilwang.entity.Curriculum;
import net.basilwang.listener.OnWheelChangedListener;
import net.basilwang.ui.widget.WheelView;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditCurriculumFragment extends Fragment {

	private Curriculum curriculum;
	private EditText className;
	private EditText teacherName;
	private TextView semesterPeriod;
	private RadioGroup intervaltype;
	private RelativeLayout classTime;
	private TextView classWeekTime;
	private TextView classNumTime;
	private EditText classLocation;
	private LinearLayout timePicker;
	private LinearLayout weekPicker;
	private final String[] weekDays = { "周一", "周二", "周三", "周四", "周五", "周六",
			"周日" };
	private View mView;
	private int classStart, classEnd;
	private int week_start, week_end;
	private WheelView wv_week;
	private WheelView wv_start;
	private WheelView wv_end;
	private WheelView wv_week_start;
	private WheelView wv_week_end;
	private PickerOnFocusChangeListener pickerOnFocusChange;

	public EditCurriculumFragment(Curriculum c) {
		this.curriculum = c;
	}

	public EditCurriculumFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.editcurriculum, container, false);
		initView();
		initTextView();
		initEditText();
		closePicker();
		return mView;
	}

	private void initEditText() {
		if (pickerOnFocusChange == null) {
			pickerOnFocusChange = new PickerOnFocusChangeListener();
		}
		className = (EditText) mView.findViewById(R.id.classname_edt);
		className.setText(getClassname());
		className.setOnFocusChangeListener(pickerOnFocusChange);
		className.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				timePicker.setVisibility(View.GONE);
				weekPicker.setVisibility(View.GONE);
				return false;
			}
		});
		teacherName = (EditText) mView.findViewById(R.id.teachername_edt);
		teacherName.setText(getTeacherName());
		teacherName.setOnFocusChangeListener(pickerOnFocusChange);
		classLocation = (EditText) mView.findViewById(R.id.class_location_edt);
		classLocation.setText(getClassLocation());
		teacherName.setOnFocusChangeListener(pickerOnFocusChange);
	}

	private void initTextView() {
		semesterPeriod = (TextView) mView.findViewById(R.id.week_to_week_edt);
		setSemesterPeriodText();
		semesterPeriod.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				className.requestFocus();
				getInpuMethodManager().hideSoftInputFromWindow(
						getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				timePicker.setVisibility(View.GONE);
				showWeekPicker();
			}
		});
		classWeekTime = (TextView) mView.findViewById(R.id.class_week_tv);
		classWeekTime.setText(getDayOfWeek());
		classNumTime = (TextView) mView.findViewById(R.id.class_num_tv);

		classStart = curriculum.getCurriculumIndex();
		classEnd = classStart + curriculum.getTimeSpan() - 1;
		setClassNumTime();
	}

	private InputMethodManager getInpuMethodManager() {
		return (InputMethodManager) this.getActivity().getSystemService(
				Activity.INPUT_METHOD_SERVICE);
	}

	private int getWeekStart() {
		return Integer.valueOf(curriculum.getSemesterid().split("-")[0]);
	}

	private int getWeekEnd() {
		return Integer.parseInt(curriculum.getSemesterPeriod().split("-")[1]);
	}

	private void initView() {
		timePicker = (LinearLayout) mView.findViewById(R.id.timePicker);
		weekPicker = (LinearLayout) mView.findViewById(R.id.weekPicker);
		week_start = getWeekStart();
		week_end = getWeekEnd();
		intervaltype = (RadioGroup) mView.findViewById(R.id.intervaltype);
		intervaltype.check(getCheckedRadio());
		classTime = (RelativeLayout) mView.findViewById(R.id.class_time);
		classTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				className.requestFocus();
				getInpuMethodManager().hideSoftInputFromWindow(
						getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				weekPicker.setVisibility(View.GONE);
				showTimePicker();
				Log.v("classTime", "" + classTime.hasFocus());
				Log.v("className", "" + className.hasFocus());
			}
		});
	}

	private void showTimePicker() {
		initTimePickerDate();
		timePicker.setVisibility(View.VISIBLE);

	}

	private void showWeekPicker() {
		initWeekPickerDate();
		weekPicker.setVisibility(View.VISIBLE);

	}

	private void closePicker() {
		mView.findViewById(R.id.class_time_out).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						timePicker.setVisibility(View.GONE);
						weekPicker.setVisibility(View.GONE);
						return false;
					}

				});
	}

	private void initTimePickerDate() {
		// 周
		wv_week = (WheelView) mView.findViewById(R.id.week);
		wv_week.setAdapter(new ArrayWheelAdapter<String>(weekDays));// 设置"周"的显示数据
		// wv_year.setCyclic(true);// 可循环滚动
		// wv_year.setLabel("年");// 添加文字
		// wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 开始节数
		wv_start = (WheelView) mView.findViewById(R.id.start);
		wv_start.setAdapter(new NumericWheelAdapter(1, 16, "第%d节"));
		// wv_start.setCyclic(true);
		// wv_month.setLabel("月");
		wv_start.setCurrentItem(classStart - 1);

		// 结束节树
		wv_end = (WheelView) mView.findViewById(R.id.end);
		wv_end.setAdapter(new NumericWheelAdapter(1, 16, "到%d节"));
		// wv_month.setCyclic(true);
		// wv_month.setLabel("月");
		wv_end.setCurrentItem(classEnd - 1);
		wv_week.TEXT_SIZE = getTextSizeForWheel();
		wv_start.TEXT_SIZE = getTextSizeForWheel();
		wv_end.TEXT_SIZE = getTextSizeForWheel();

		OnWheelChangedListener wheelListener_week = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				classWeekTime.setText(weekDays[wv_week.getCurrentItem()]);
			}
		};
		OnWheelChangedListener wheelListener_start = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				classStart = wv_start.getCurrentItem() + 1;
				if (wv_start.getCurrentItem() >= wv_end.getCurrentItem()) {
					wv_end.setCurrentItem(wv_start.getCurrentItem() + 1);
				}
				setClassNumTime();
			}
		};
		OnWheelChangedListener wheelListener_end = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wv_start.getCurrentItem() > wv_end.getCurrentItem()) {
					wv_end.setCurrentItem(wv_start.getCurrentItem());
				}
				classEnd = wv_end.getCurrentItem() + 1;
				setClassNumTime();
			}
		};
		wv_week.addChangingListener(wheelListener_week);
		wv_start.addChangingListener(wheelListener_start);
		wv_end.addChangingListener(wheelListener_end);

	}

	private void initWeekPickerDate() {
		// 开始周
		wv_week_start = (WheelView) mView.findViewById(R.id.week_start);
		wv_week_start.setAdapter(new NumericWheelAdapter(1, 24, "第%d周"));// 设置"周"的显示数据
		// wv_year.setCyclic(true);// 可循环滚动
		// wv_year.setLabel("年");// 添加文字
		wv_week_start.setCurrentItem(week_start - 1);// 初始化时显示的数据

		// 结束周
		wv_week_end = (WheelView) mView.findViewById(R.id.week_end);
		wv_week_end.setAdapter(new NumericWheelAdapter(1, 24, "到%d周"));
		// wv_month.setCyclic(true);
		// wv_month.setLabel("月");
		wv_week_end.setCurrentItem(week_end - 1);

		wv_week_start.TEXT_SIZE = getTextSizeForWheel();
		wv_week_end.TEXT_SIZE = getTextSizeForWheel();

		OnWheelChangedListener wheelListener_week_start = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				week_start = wv_week_start.getCurrentItem() + 1;
				if (wv_week_start.getCurrentItem() >= wv_week_end
						.getCurrentItem()) {
					wv_week_end
							.setCurrentItem(wv_week_start.getCurrentItem() + 1);
				}
				setSemesterPeriodText();
			}
		};
		OnWheelChangedListener wheelListener_week_end = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (wv_week_start.getCurrentItem() > wv_week_end
						.getCurrentItem()) {
					wv_week_end.setCurrentItem(wv_week_start.getCurrentItem());
				}
				week_end = wv_week_end.getCurrentItem() + 1;
				setSemesterPeriodText();
			}
		};
		wv_week_start.addChangingListener(wheelListener_week_start);
		wv_week_end.addChangingListener(wheelListener_week_end);

	}

	private void setClassNumTime() {
		if (classStart == classEnd)
			classNumTime.setText("第" + classEnd + "节");
		else
			classNumTime.setText(classStart + "-" + classEnd + "节");
	}

	private void setSemesterPeriodText() {
		if (week_start == week_end)
			semesterPeriod.setText("第" + week_end + "周");
		else
			semesterPeriod.setText(week_start + "-" + week_end + "周");
	}

	private int getCheckedRadio() {
		Log.v("selectedClass", curriculum.getIntervalType());
		int selectedClass = Integer.parseInt(curriculum.getIntervalType());
		switch (selectedClass) {
		case 0:
			return R.id.both;
		case 1:
			return R.id.even;
		case 2:
			return R.id.odd;
		default:
			break;
		}
		return 0;
	}

	private String getDayOfWeek() {
		switch (curriculum.getDayOfWeek()) {
		case 1:
			return "周一";
		case 2:
			return "周二";
		case 3:
			return "周三";
		case 4:
			return "周四";
		case 5:
			return "周五";
		case 6:
			return "周六";
		case 7:
			return "周日";
		default:
			break;
		}
		return null;
	}

	private String[] getRawInfos() {
		return curriculum.getRawInfo().split("\\n");
	}

	private String getClassname() {
		return getRawInfos()[0];
	}

	private String getClassTime() {
		return getRawInfos()[1];
	}

	private String getTeacherName() {
		return getRawInfos()[2];
	}

	private String getClassLocation() {
		return getRawInfos()[3];
	}

	private int getTextSizeForWheel() {
		return 30;
	}

	private class PickerOnFocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			Log.v("className", String.valueOf(className.hasFocus()));
			timePicker.setVisibility(View.GONE);
			weekPicker.setVisibility(View.GONE);
		}
	}

}
