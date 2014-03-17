package net.basilwang;

import static net.basilwang.dao.Preferences.CEMESTER_START_PREFERENCES;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.dao.ClassIndexAdapter;
import net.basilwang.dao.CurriculumAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import net.basilwang.R;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class CurriculumFragment extends SherlockFragment {
	private List<ListView> list;
	private static final int ROW_ID_REQUEST = 0x10000;
	// private final int REAL_LIST_NUMS=7;
	private ViewPager weekPager;
	// private UITableView mTableView;
	private WeekViewPagerAdapter weekAdapter;
	private int focusedPage = 0;
	// final private int thisweek=4;
	private int mCurrentWeek;
	// 2012-06-07 basilwang change when tapped
	private Time timeSelected = new Time();
	// 2012-06-07 basilwang won't change
	private Time todayTime = new Time();
	private Time showFromTime = new Time();

	// private int weekSpan;
	private class WeekViewPagerAdapter extends PagerAdapter {

		ViewPager container;

		private View addWeekViewAt(int position, int week) {
			// final View mv = new View(CurriculumViewPagerActivity.this);
			// mv.setLayoutParams(new ViewSwitcher.LayoutParams(
			// android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			// android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			// mv.setSelectedTime(time);
			View v = CurriculumFragment.this.getActivity().getLayoutInflater()
					.inflate(R.layout.mycurriculum, container, false);
			v.setTag(week);
			container.addView(v, position);
			if (week == mCurrentWeek) {
				reloadData(v);

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
			// int day=0;
			final Time time = new Time();
			time.set(System.currentTimeMillis());
			time.weekDay += (position - 1); // add the offset from the center
											// time
			// day += (position - 1); // add the offset from the center time
			int week = mCurrentWeek + (position - 1);
			return addWeekViewAt(position, week);
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
		SubMenu sub = menu.addSubMenu("周/天");
		// sub.add(0, R.style.Theme_Sherlock, 0, R.string.weekview);
		sub.add(0, R.style.Theme_Sherlock_Light, 0, R.string.dayview);
		// sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0,
		// "Light (Dark Action Bar)");
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// timeSelected.setToNow();
		todayTime.setToNow();
		showFromTime.setToNow();
		long millis = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getLong(CEMESTER_START_PREFERENCES, 0);
		if (millis != 0) {
			showFromTime.set(millis);
		}
		mCurrentWeek = net.basilwang.utils.DateUtils.getWeekSpan(showFromTime,
				todayTime);

		View v = inflater.inflate(R.layout.curriculum_viewpager, container,
				false);
		// curriculumService=new CurriculumService(this.getActivity());
		weekAdapter = new WeekViewPagerAdapter();
		weekPager = (ViewPager) v.findViewById(R.id.pager);
		weekPager.setAdapter(weekAdapter);
		weekPager.setCurrentItem(1, false);
		weekPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE) {

					final int oldLeftDay = Integer.valueOf(((View) weekPager
							.getChildAt(0)).getTag().toString());

					final int oldCenterDay = Integer.valueOf(((View) weekPager
							.getChildAt(1)).getTag().toString());
					int oldRightDay;
					if (weekPager.getChildAt(2) != null)
						oldRightDay = Integer.valueOf(((View) weekPager
								.getChildAt(2)).getTag().toString());
					else
						oldRightDay = 0;
					Log.v("test", oldLeftDay + "" + oldCenterDay + ""
							+ oldRightDay);

					if (focusedPage == 0) {
						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldTopTime));
						int week = oldLeftDay;
						// day=(day==1?REAL_LIST_NUMS:day-1);
						week = week - 1;
						// TODO: load and switch shown events
						((View) weekPager.getChildAt(0)).setTag(week);
						((View) weekPager.getChildAt(1)).setTag(oldLeftDay);
						((View) weekPager.getChildAt(2)).setTag(oldCenterDay);

					} else if (focusedPage == 2) {

						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldBottomTime));
						int week = oldRightDay;
						// day=(day%REAL_LIST_NUMS==0?1:day+1);
						week = week + 1;
						// TODO: load and switch shown events
						((View) weekPager.getChildAt(0)).setTag(oldCenterDay);
						((View) weekPager.getChildAt(1)).setTag(oldRightDay);
						((View) weekPager.getChildAt(2)).setTag(week);

					}

					// always set to middle page to continue to be able to
					// scroll up/down
					weekPager.setCurrentItem(1, false);

					reloadData(weekPager.getChildAt(1));

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

		loadClassIndex(fragment);

		int week = Integer.valueOf(fragment.getTag().toString());
		TextView tv = (TextView) (fragment.findViewById(R.id.weeknumber));
		tv.setText("第" + week + "周");
		// 2012-06-04 basilwang onCreateView will be called on every attach,so
		// we initialize list every time.
		list = new ArrayList<ListView>();
		list.add((ListView) fragment.findViewById(R.id.day1));
		list.add((ListView) fragment.findViewById(R.id.day2));
		list.add((ListView) fragment.findViewById(R.id.day3));
		list.add((ListView) fragment.findViewById(R.id.day4));
		list.add((ListView) fragment.findViewById(R.id.day5));
		list.add((ListView) fragment.findViewById(R.id.day6));
		list.add((ListView) fragment.findViewById(R.id.day7));

		// OnItemLongClickListener listener = new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// long t = parent.getAdapter().getItemId(position);
		// // Toast.makeText(getApplicationContext(),
		// // String.valueOf(t), Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(
		// CurriculumFragment.this.getActivity(),
		// EditCurriculumActivity.class);
		// intent.putExtra("g", t);
		// startActivityForResult(intent, ROW_ID_REQUEST);
		// return false;
		// }
		// };
		// for (int i = 0; i < 7; i++) {
		// CurriculumAdapter adapter = new CurriculumAdapter(week, i + 1,
		// this.getActivity());
		// list.get(i).setAdapter(adapter);
		// list.get(i).setOnItemLongClickListener(listener);
		// }

		for (int i = 0; i < 7; i++) {
			CurriculumAdapter adapter = new CurriculumAdapter(week, i + 1,
					this.getActivity());
			list.get(i).setAdapter(adapter);
		}
	}

	private void loadClassIndex(View fragment) {
		ListView classindexView = (ListView) fragment
				.findViewById(R.id.classindex);
		classindexView.setAdapter(new ClassIndexAdapter(this.getActivity()));

	}
	// public void onActivityResult(int request, int result, Intent intent) {
	// switch (request) {
	// case ROW_ID_REQUEST:
	// reloadData();
	// break;
	// }
	// }
	//
	//
	//
	//
	// // 2012-04-12 basilwang refresh when click tab
	// public void onResume() {
	// super.onResume();
	// reloadData();
	// }

}
