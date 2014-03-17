package net.basilwang;

import static net.basilwang.dao.Preferences.CURRICULUM_TO_SHOW;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import net.basilwang.ui.widget.BlockView;
import net.basilwang.ui.widget.BlocksLayout;
import net.basilwang.ui.widget.HorizontalScrollViewListener;
import net.basilwang.ui.widget.ObservableHorizontalScrollView;
import net.basilwang.ui.widget.TimeRulerView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class WeekViewFragment extends SherlockFragment implements
		HorizontalScrollViewListener {

	static final String TAG = "WeekViewFragment";
	// private ScrollView mScrollView;
	private BlocksLayout mBlocks;
	private View mNowView;
	private ObservableHorizontalScrollView mHeaderScroll = null;
	private ObservableHorizontalScrollView mClassScroll = null;
	private TimeRulerView mTimeRuler = null;
	private FrameLayout mWeekDayHeaderFrameLayout;

	CurriculumService curriculumService;
	List<Curriculum> curriculumList;

	private List<Curriculum> getCurriculumList() {
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getString(CURRICULUM_TO_SHOW, "");
		int accountId = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(LOGON_ACCOUNT_ID, 0);
		return this.curriculumService.getCurriculumList(semesterValue,
				accountId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 2012-07-11 basilwang has its own menu
		this.setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// if (Preferences.isWeekViewUnlocked(this.getActivity())) {
		SubMenu sub = menu.addSubMenu("周/天");
		// sub.add(0, R.style.Theme_Sherlock, 0, R.string.weekview);
		// sub.add(0, R.style.Theme_Sherlock, 0, R.string.dayview);
		// sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0,
		// "Light (Dark Action Bar)");
		sub.setIcon(R.drawable.viewswitch);
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// }
		// SubMenu sub1 = menu.addSubMenu("分享课表");
		// sub1.setIcon(R.drawable.icon_share);
		// sub1.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	// 分享课表的部分
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (item.getTitle().equals("分享课表")) {
	// Log.v("分享课表", "sdadsfdsfdgfdhg");
	// // int width=mHeaderScroll.getWidth()-mTimeRuler.getWidth();
	// // Bitmap bitmap = Bitmap.createBitmap(width,
	// // mClassScroll.getHeight(), Bitmap.Config.ARGB_8888);
	// // Canvas c = new Canvas(bitmap);
	// // mClassScroll.draw(c);
	// Bitmap bitmap = Bitmap.createBitmap(1000,
	// mHeaderScroll.getHeight(), Bitmap.Config.ARGB_8888);
	// Canvas c = new Canvas(bitmap);
	// mHeaderScroll.draw(c);
	// // Bitmap bitmap = Bitmap.createBitmap(mTimeRuler.getWidth(),
	// // mTimeRuler.getHeight(), Bitmap.Config.ARGB_8888);
	// // Canvas c = new Canvas(bitmap);
	// // mTimeRuler.draw(c);
	//
	// File file = new File(StaticAttachmentActivity.savePath);
	// if (!file.exists()) {
	// file.mkdir();
	// }
	// File shareFile = new File(StaticAttachmentActivity.sharePicture);
	// if (shareFile.exists())
	// shareFile.delete();
	// FileOutputStream fos = null;
	// try {
	// fos = new FileOutputStream(shareFile);
	// bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// Log.v("error", e.toString());
	// }
	//
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.weekview_fragment, container, false);
		mTimeRuler = (TimeRulerView) v.findViewById(R.id.blocks_ruler);
		mBlocks = (BlocksLayout) v.findViewById(R.id.blocks);
		mNowView = v.findViewById(R.id.blocks_now);

		mBlocks.setDrawingCacheEnabled(true);
		mBlocks.setAlwaysDrawnWithCacheEnabled(true);

		mHeaderScroll = (ObservableHorizontalScrollView) v
				.findViewById(R.id.header_scroll);
		mHeaderScroll.setHorizontalScrollViewListener(this);
		mClassScroll = (ObservableHorizontalScrollView) v
				.findViewById(R.id.horizontal_scroll);
		mClassScroll.setHorizontalScrollViewListener(this);

		mWeekDayHeaderFrameLayout = (FrameLayout) v
				.findViewById(R.id.week_day_container);

		curriculumService = new CurriculumService(this.getActivity());
		curriculumList = getCurriculumList();
		Log.v(TAG, "curriculumList length is" + curriculumList.size());
		for (Curriculum c : curriculumList) {

			Integer column = c.getDayOfWeek() - 1;
			Log.v(TAG, "column is" + column);

			final String blockId = String.valueOf(c.getId());
			final String title = c.getName();
			final int start = c.getCurriculumIndex();
			final int end = c.getCurriculumIndex() + c.getTimeSpan();
			final boolean containsStarred = true;

			final BlockView blockView = new BlockView(this.getActivity(), c,
					blockId, title, start, end, containsStarred, column);

			// final int sessionsCount =
			// cursor.getInt(BlocksQuery.SESSIONS_COUNT);
			// if (sessionsCount > 0) {
			// blockView.setOnClickListener(this);
			// } else {
			// blockView.setFocusable(false);
			// blockView.setEnabled(false);
			// LayerDrawable buttonDrawable = (LayerDrawable)
			// blockView.getBackground();
			// buttonDrawable.getDrawable(0).setAlpha(DISABLED_BLOCK_ALPHA);
			// buttonDrawable.getDrawable(2).setAlpha(DISABLED_BLOCK_ALPHA);
			// }

			mBlocks.addBlock(blockView);
		}
		return v;
	}

	@Override
	public void onScrollChanged(ObservableHorizontalScrollView scrollView,
			int x, int y, int oldx, int oldy) {
		if (scrollView == mHeaderScroll) {
			mClassScroll.scrollTo(x, y);
		} else if (scrollView == mClassScroll) {
			mHeaderScroll.scrollTo(x, y);
		}

	}

}
