/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.basilwang;

import static net.basilwang.dao.Preferences.CURRICULUM_TO_SHOW;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.util.HashMap;
import java.util.List;

import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import net.basilwang.ui.widget.BlockView;
import net.basilwang.ui.widget.BlocksLayout;
import net.basilwang.utils.Maps;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

/**
 * {@link Activity} that displays a high-level view of a single day of
 * {@link Blocks} across the conference. Shows them lined up against a vertical
 * ruler of times across the day.
 */
public class BlocksActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "BlocksActivity";

	// TODO: these layouts and views are structured pretty weird, ask someone to
	// review them and come up with better organization.

	// TODO: show blocks that don't fall into columns at the bottom

	public static final String EXTRA_TIME_START = "com.google.android.iosched.extra.TIME_START";
	public static final String EXTRA_TIME_END = "com.google.android.iosched.extra.TIME_END";

	// private ScrollView mScrollView;
	private BlocksLayout mBlocks;
	private View mNowView;

	private long mTimeStart = -1;
	private long mTimeEnd = -1;

	CurriculumService curriculumService;
	List<Curriculum> curriculumList;

	// private NotifyingAsyncQueryHandler mHandler;

	private static final int DISABLED_BLOCK_ALPHA = 160;

	private static final HashMap<String, Integer> sTypeColumnMap = buildTypeColumnMap();

	private static HashMap<String, Integer> buildTypeColumnMap() {
		final HashMap<String, Integer> map = Maps.newHashMap();
		map.put("food", 0);
		map.put("session", 1);
		map.put("hours", 2);
		return map;
	}

	private List<Curriculum> getCurriculumList() {
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				this).getString(CURRICULUM_TO_SHOW, "");
		int accountId = PreferenceManager.getDefaultSharedPreferences(this)
				.getInt(LOGON_ACCOUNT_ID, 0);
		return this.curriculumService.getCurriculumList(semesterValue,
				accountId);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weekview);

		mTimeStart = 1274252400000L;
		mTimeEnd = 1274338800000L;

		// mScrollView = (ScrollView) findViewById(R.id.blocks_scroll);
		mBlocks = (BlocksLayout) findViewById(R.id.blocks);
		mNowView = findViewById(R.id.blocks_now);

		mBlocks.setDrawingCacheEnabled(true);
		mBlocks.setAlwaysDrawnWithCacheEnabled(true);

		curriculumService = new CurriculumService(this);
		curriculumList = getCurriculumList();
		Log.v("test", "curriculumList length is" + curriculumList.size());
		for (Curriculum c : curriculumList) {

			Integer column = c.getDayOfWeek();
			column = (column - 1 + 7) % 7;
			Log.v("test", "column is" + column);

			final String blockId = String.valueOf(c.getId());
			final String title = c.getName();
			final int start = c.getCurriculumIndex();
			final int end = c.getCurriculumIndex() + c.getTimeSpan();
			final boolean containsStarred = true;

			final BlockView blockView = new BlockView(this, c, blockId, title,
					start, end, containsStarred, column);

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
		// updateNowView(true);
		// mBlocks.requestLayout();
		// mHandler = new NotifyingAsyncQueryHandler(getContentResolver(),
		// this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Since we build our views manually instead of using an adapter, we
		// need to manually requery every time launched.
		// final Uri blocksUri = getIntent().getData();
		// mHandler.startQuery(blocksUri, BlocksQuery.PROJECTION,
		// Blocks.DEFAULT_SORT);

		// Start listening for time updates to adjust "now" bar. TIME_TICK is
		// triggered once per minute, which is how we move the bar over time.
		// final IntentFilter filter = new IntentFilter();
		// filter.addAction(Intent.ACTION_TIME_TICK);
		// filter.addAction(Intent.ACTION_TIME_CHANGED);
		// filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		// registerReceiver(mReceiver, filter, null, new Handler());
		//
		// mNowView.post(new Runnable() {
		// public void run() {
		// updateNowView(true);
		// }
		// });
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(mReceiver);
	}

	public void onHomeClick(View v) {
		// UIUtils.goHome(this);
	}

	public void onRefreshClick(View v) {
	}

	public void onSearchClick(View v) {
		// UIUtils.goSearch(this);
	}

	/** {@inheritDoc} */
	public void onClick(View view) {
		if (view instanceof BlockView) {
			final String blockId = ((BlockView) view).getBlockId();
			// final Uri sessionsUri = Blocks.buildSessionsUri(blockId);
			// startActivity(new Intent(Intent.ACTION_VIEW, sessionsUri));
		}
	}

	/**
	 * Update position and visibility of "now" view.
	 */
	// private void updateNowView(boolean forceScroll) {
	// //final long now = System.currentTimeMillis();
	// final long now= 1274259200000L;
	// final boolean visible = now >= mTimeStart && now <= mTimeEnd;
	// Log.v("updateNowView","updateNowView's visible is" + visible);
	// mNowView.setVisibility(visible ? View.VISIBLE : View.GONE);
	//
	// if (visible && forceScroll) {
	// // Scroll to show "now" in center
	// final int offset = mScrollView.getHeight() / 2;
	// mNowView.requestRectangleOnScreen(new Rect(0, offset, 0, offset), true);
	// }
	//
	// mBlocks.requestLayout();
	// }
	//
	// private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Log.d(TAG, "onReceive time update");
	// updateNowView(false);
	// }
	// };

}
