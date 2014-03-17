package net.basilwang;

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.util.List;

import net.basilwang.dao.CurriculumService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Curriculum;
import net.basilwang.entity.Semester;
import net.basilwang.listener.ShowTipListener;
import net.basilwang.utils.CurriculumUtils;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.basilwang.R;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class CurriculumViewPagerFragment extends SherlockFragment implements
		ShowTipListener {
	static final String TAG = "CurriculumViewPagerFragment";
	public static final String TimerSet = null;
	private ViewPager dayPager;
	// private UITableView mTableView;
	private DayViewPagerAdapter dayAdapter;
	private int focusedPage = 0;
	// private int today;
	private int mWeekDay;
	// 2012-06-07 basilwang change when tapped
	// private Time timeSelected = new Time();
	// 2012-06-07 basilwang won't change
	private Time todayTime = new Time();
	private CurriculumService curriculumService;
	private Time showFromTime = new Time();
	private Time dueTime = new Time();
	private int weekSpan;
	private int allSemesterWeekSpan;

	private class DayViewPagerAdapter extends PagerAdapter {

		ViewPager container;

		private View addDayViewAt(int position, Time time) {
			View v = CurriculumViewPagerFragment.this.getActivity()
					.getLayoutInflater()
					.inflate(R.layout.dayview, container, false);
			v.setTag(time);
			container.addView(v, position);
			if (net.basilwang.utils.DateUtils.isSameDay(time, todayTime)) {
				reloadData(dayPager.getChildAt(1));
			}
			return v;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeViewAt(position);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			this.container = (ViewPager) container;
			// 2012-12-01 basilwang use timeSelected temporarily and COPY
			// todayTime
			Time timeSelected = new Time(todayTime);
			timeSelected.monthDay = todayTime.monthDay + (position - 1);
			// add the offset from the center time
			timeSelected.normalize(true);
			// day += (position - 1); // add the offset from the center time
			// int day=today+(position-1);
			return addDayViewAt(position, timeSelected);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		private View removeDayViewAt(ViewPager container, int position) {
			final View mv = (View) container.getChildAt(position);
			container.removeViewAt(position);
			return mv;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// if (Preferences.isWeekViewUnlocked(this.getActivity())) {
		SubMenu sub = menu.addSubMenu("周/天");
		sub.setIcon(R.drawable.viewswitch);
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// }
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		showTipIfNecessary();
		// setContentView(R.layout.curriculum_viewpager);
		// 2012-06-07 basilwang get current week number;

		// timeSelected.setToNow();
		todayTime.setToNow();
		mWeekDay = todayTime.weekDay;
		// 2012-12-01 basilwang load from db
		Semester semester = new SemesterService(getActivity())
				.getSemesterByName(PreferenceUtils.getPreferSemester(this
						.getActivity()));

		showFromTime.setToNow();
		long millis = semester.getBeginDate();
		if (millis != 0) {
			showFromTime.set(millis);
		}
		long duemillis = semester.getEndDate();
		if (duemillis != 0) {
			dueTime.set(duemillis);
		}
		allSemesterWeekSpan = net.basilwang.utils.DateUtils.getWeekSpan(
				showFromTime, dueTime);
		View v = inflater.inflate(R.layout.curriculum_viewpager, container,
				false);

		if (!Preferences.isAdClosed(this.getActivity())) {
			// 应用Id 应用密码 广告请求间隔(s) 测试模式
			AdManager.init(this.getActivity(), "2fc95b356bb979ae",
					"8b94f727980f7158", 30, false);
			LinearLayout adViewLayout = (LinearLayout) v
					.findViewById(R.id.adViewLayout);
			adViewLayout.addView(new AdView(this.getActivity()),
					new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
		}

		curriculumService = new CurriculumService(this.getActivity());
		dayAdapter = new DayViewPagerAdapter();
		dayPager = (ViewPager) v.findViewById(R.id.pager);
		dayPager.setAdapter(dayAdapter);
		dayPager.setCurrentItem(1, false);
		dayPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE) {

					final Time oldLeftTime = (Time) (((View) dayPager
							.getChildAt(0)).getTag());

					final Time oldCenterTime = (Time) (((View) dayPager
							.getChildAt(1)).getTag());
					Time oldRightTime;
					if (dayPager.getChildAt(2) != null)
						oldRightTime = (Time) (((View) dayPager.getChildAt(2))
								.getTag());
					else
						oldRightTime = new Time();
					// Log.v("test", oldLeftDay + "" + oldCenterDay + ""
					// + oldRightDay);

					if (focusedPage == 0) {
						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldTopTime));
						Time mTime = new Time(oldLeftTime);
						mTime.monthDay -= 1;
						mTime.normalize(true);

						// : load and switch shown events
						((View) dayPager.getChildAt(0)).setTag(mTime);
						((View) dayPager.getChildAt(1)).setTag(oldLeftTime);
						((View) dayPager.getChildAt(2)).setTag(oldCenterTime);

					} else if (focusedPage == 2) {

						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldBottomTime));
						Time mTime = new Time(oldRightTime);
						mTime.monthDay += 1;
						mTime.normalize(true);

						// : load and switch shown events
						((View) dayPager.getChildAt(0)).setTag(oldCenterTime);
						((View) dayPager.getChildAt(1)).setTag(oldRightTime);
						((View) dayPager.getChildAt(2)).setTag(mTime);

					}

					// always set to middle page to continue to be able to
					// scroll up/down
					dayPager.setCurrentItem(1, false);

					reloadData(dayPager.getChildAt(1));

				}
			}

			@Override
			public void onPageSelected(int position) {
				focusedPage = position;
				// View fragment=dayPager.getChildAt(position);
				// TextView tv=(TextView)fragment.findViewById(R.id.test);
				// tv.setText(fragment.getTag().toString());
				// ListView
				// list=(ListView)fragment.findViewById(R.id.dayviewlist);
				// CurriculumAdapter adapter = new
				// CurriculumAdapter(Integer.valueOf(fragment.getTag().toString()),
				// CurriculumViewPagerActivity.this);
				// list.setAdapter(adapter);
			}
		});
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 2012-07-11 basilwang has its own menu
		this.setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	private void reloadData(View fragment) {
		CharSequence value;
		Time mTime = (Time) (fragment.getTag());
		weekSpan = net.basilwang.utils.DateUtils.getWeekSpan(showFromTime,
				mTime);
		int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
				| DateUtils.FORMAT_ABBREV_MONTH;
		value = DateUtils.formatDateTime(this.getActivity(),
				mTime.toMillis(true), flags);
		mWeekDay = mTime.weekDay;
		String content = null;

		TextView tv = (TextView) fragment.findViewById(R.id.dayofweek);
		switch (mWeekDay) {
		case 1:
			content = this.getActivity().getString(R.string.day1);
			break;
		case 2:
			content = this.getActivity().getString(R.string.day2);
			break;
		case 3:
			content = this.getActivity().getString(R.string.day3);
			break;
		case 4:
			content = this.getActivity().getString(R.string.day4);
			break;
		case 5:
			content = this.getActivity().getString(R.string.day5);
			break;
		case 6:
			content = this.getActivity().getString(R.string.day6);
			break;
		case 0:
			content = this.getActivity().getString(R.string.day7);
			mWeekDay = 7;
			break;
		}
		// 2012-12-01 basilwang judge if in vacation
		UITableView mTableView = (UITableView) fragment
				.findViewById(R.id.tableView);
		if (!isVacation()) {
			tv.setText(value + " " + content + "第" + weekSpan + "周");
			populateList(mTableView, fragment,
					filterCurriculum(getCurriculumsByWeek()));
		} else {
			tv.setText(value + " " + content + "假期中");
			populateList(mTableView, fragment, getCurriculumsByWeek());
		}
		mTableView.commit();
	}

	protected void populateList(UITableView mTableView, View parent,
			List<Curriculum> curriculumList) {
		// 2012-5-27 basilwang must clear list when change view pager
		mTableView.clear();
		LayoutInflater mInflater = (LayoutInflater) this.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (curriculumList.size() == 0) {
			addViewItem(mTableView, mInflater,
					getResources().getString(R.string.norecord));
		} else {
			for (Curriculum c : curriculumList) {
				addViewItem(mTableView, mInflater, c.getRawInfo());
			}
		}
	}

	private List<Curriculum> filterCurriculum(List<Curriculum> curriculumList) {
		if (curriculumList.size() != 0) {
			CurriculumUtils.filterCurriclumsByWeek(weekSpan, curriculumList);
		}
		return curriculumList;
	}

	/**
	 * Base on current week, get this week's curriculums
	 * 
	 */
	private List<Curriculum> getCurriculumsByWeek() {
		int accountId = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(LOGON_ACCOUNT_ID, 0);
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getString(Preferences.CURRICULUM_TO_SHOW,
				"");
		List<Curriculum> curriculumList = this.curriculumService
				.getCurriculumListByDay(semesterValue, mWeekDay, accountId);
		return curriculumList;
	}

	private void addViewItem(UITableView mTableView, LayoutInflater mInflater,
			String text) {
		RelativeLayout v = (RelativeLayout) mInflater.inflate(
				R.layout.custom_view3, null);
		TextView txtView = (TextView) v.findViewById(R.id.title);
		txtView.setText(text);
		// 2012-12-01 basilwang set gray if in vacation
		if (isVacation()) {

			txtView.setTextColor(Color.GRAY);
		}
		ViewItem v2 = new ViewItem(v);
		v2.setClickable(false);
		mTableView.addViewItem(v2);
	}

	private boolean isVacation() {
		return (weekSpan < 1 || weekSpan > allSemesterWeekSpan);
	}

	@Override
	public void showTipIfNecessary() {
		int dayViewTip = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(Preferences.WEEK_VIEW_TIP_SHOW, 0);
		if (dayViewTip == 0) {
			TipUtils.showTipIfNecessary(this.getActivity(),
					R.drawable.viewswitch_tip, this);
		}
	}

	@Override
	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(getActivity(),
				Preferences.WEEK_VIEW_TIP_SHOW, 1);
	}

}
