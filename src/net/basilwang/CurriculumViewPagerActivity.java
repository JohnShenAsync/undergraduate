package net.basilwang;

import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.util.List;

import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.basilwang.R;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

public class CurriculumViewPagerActivity extends StaticAttachmentActivity {
	private final int REAL_LIST_NUMS = 7;
	private ViewPager dayPager;
	// private UITableView mTableView;
	private DayViewPagerAdapter dayAdapter;
	private int focusedPage = 0;
	final private int today = 4;
	private int mDay;

	private CurriculumService curriculumService;

	private class DayViewPagerAdapter extends PagerAdapter {

		ViewPager container;

		private View addDayViewAt(int position, int day) {
			// final View mv = new View(CurriculumViewPagerActivity.this);
			// mv.setLayoutParams(new ViewSwitcher.LayoutParams(
			// android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			// android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			// mv.setSelectedTime(time);
			View v = CurriculumViewPagerActivity.this.getLayoutInflater()
					.inflate(R.layout.dayview, container, false);
			v.setTag(day);
			container.addView(v, position);
			if (day == today) {
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
			time.month += (position - 1); // add the offset from the center time
			// day += (position - 1); // add the offset from the center time
			int day = today + (position - 1);
			return addDayViewAt(position, day);
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curriculum_viewpager);
		curriculumService = new CurriculumService(this);
		dayAdapter = new DayViewPagerAdapter();
		dayPager = (ViewPager) findViewById(R.id.pager);
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

					final int oldLeftDay = Integer.valueOf(((View) dayPager
							.getChildAt(0)).getTag().toString());

					final int oldCenterDay = Integer.valueOf(((View) dayPager
							.getChildAt(1)).getTag().toString());
					int oldRightDay;
					if (dayPager.getChildAt(2) != null)
						oldRightDay = Integer.valueOf(((View) dayPager
								.getChildAt(2)).getTag().toString());
					else
						oldRightDay = 0;
					Log.v("test", oldLeftDay + "" + oldCenterDay + ""
							+ oldRightDay);

					if (focusedPage == 0) {
						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldTopTime));
						int day = oldLeftDay;
						day = (day == 1 ? REAL_LIST_NUMS : day - 1);

						// TODO: load and switch shown events
						((View) dayPager.getChildAt(0)).setTag(day);
						((View) dayPager.getChildAt(1)).setTag(oldLeftDay);
						((View) dayPager.getChildAt(2)).setTag(oldCenterDay);

					} else if (focusedPage == 2) {

						// HistoryMonthActivity.this.setTitle(Utils
						// .formatMonthYear(HistoryMonthActivity.this,
						// oldBottomTime));
						int day = oldRightDay;
						day = (day % REAL_LIST_NUMS == 0 ? 1 : day + 1);

						// TODO: load and switch shown events
						((View) dayPager.getChildAt(0)).setTag(oldCenterDay);
						((View) dayPager.getChildAt(1)).setTag(oldRightDay);
						((View) dayPager.getChildAt(2)).setTag(day);

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

	}

	private void reloadData(View fragment) {
		mDay = Integer.valueOf(fragment.getTag().toString());
		TextView tv = (TextView) fragment.findViewById(R.id.dayofweek);
		switch (mDay) {
		case 1:
			tv.setText(R.string.day1);
			break;
		case 2:
			tv.setText(R.string.day2);
			break;
		case 3:
			tv.setText(R.string.day3);
			break;
		case 4:
			tv.setText(R.string.day4);
			break;
		case 5:
			tv.setText(R.string.day5);
			break;
		case 6:
			tv.setText(R.string.day6);
			break;
		case 7:
			tv.setText(R.string.day7);
			break;
		}
		// TextView tv = (TextView) fragment.findViewById(R.id.test);
		// tv.setText(fragment.getTag().toString());
		// ListView list = (ListView) fragment
		// .findViewById(R.id.dayviewlist);
		// CurriculumAdapter adapter = new CurriculumAdapter(Integer
		// .valueOf(fragment.getTag().toString()),
		// CurriculumViewPagerActivity.this);
		// list.setAdapter(adapter);

		UITableView mTableView = (UITableView) fragment
				.findViewById(R.id.tableView);
		populateList(mTableView, fragment);
		mTableView.commit();
	}

	// protected UITableView getUITableView() {
	// return mTableView;
	// }
	protected void populateList(UITableView mTableView, View parent) {
		// 2012-5-27 basilwang must clear list when change view pager
		mTableView.clear();
		// mTableView.addBasicItem("Example 1", "Summary text 1");
		// mTableView.addBasicItem("Example 1");
		// mTableView.addBasicItem("Example 2", "Summary text 2");
		// mTableView.addBasicItem(new BasicItem("Disabled item",
		// "this is a disabled item", false));
		// mTableView.addBasicItem("Example 3", "Summary text 3");
		// mTableView.addBasicItem(new BasicItem("Disabled item",
		// "this is a disabled item", false));
		//
		//
		// LayoutInflater mInflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// RelativeLayout view = (RelativeLayout)
		// mInflater.inflate(R.layout.custom_view3, null);
		// ViewItem viewItem = new ViewItem(view);
		// mTableView.addViewItem(viewItem);
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		List<Curriculum> curriculumList = this.curriculumService
				.getCurriculumListByDay("don't use", mDay, accountId);

		if (curriculumList.size() == 0) {
			mTableView.addBasicItem(new BasicItem(getResources().getString(
					R.string.norecord), "", false));

		} else {
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (Curriculum c : curriculumList) {
				RelativeLayout v = (RelativeLayout) mInflater.inflate(
						R.layout.custom_view3, null);
				TextView txtView = (TextView) v.findViewById(R.id.title);
				txtView.setText(c.getName());
				ViewItem v2 = new ViewItem(v);
				v2.setClickable(false);
				mTableView.addViewItem(v2);

			}
		}

	}
}
