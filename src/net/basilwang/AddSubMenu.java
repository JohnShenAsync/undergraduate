package net.basilwang;

import java.util.List;

import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Semester;
import net.basilwang.listener.AddSubMenuListener;
import net.basilwang.listener.ShowTipListener;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import net.basilwang.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.view.SubMenu;

public class AddSubMenu implements ShowTipListener {
	private SubMenu sub;
	private Context context;
	private AddSubMenuListener listener;
	private int tipPhotoId;
	private String preferKey;

	public void initSub(Menu menu) {
		sub = menu.addSubMenu("下载设置");
		sub.getItem()
				.setIcon(R.drawable.btn_download_setting)
				.setActionView(R.layout.collapsible_spinner)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	}

	public void setSubMenuItemListener(
			final OnItemSelectedListener spinnerListener,
			final View.OnClickListener buttonListener) {
		SubMenuListener menuListener = new SubMenuListener(spinnerListener,
				buttonListener);
		sub.getItem().setOnActionExpandListener(menuListener);
	}

	public AddSubMenu(Menu menu, Context context, AddSubMenuListener listener) {
		this.context = context;
		initSub(menu);
		this.listener = listener;
	}

	public SubMenu getSubMenu() {
		return sub;
	}

	public void setTipPhotoAndPreferKey(int tipPhotoId, String key) {
		this.tipPhotoId = tipPhotoId;
		this.preferKey = key;
	}

	@Override
	public void showTipIfNecessary() {
		int tip = PreferenceManager.getDefaultSharedPreferences(context)
				.getInt(preferKey, 0);
		if (tip == 0) {
			TipUtils.showTipIfNecessary(context, tipPhotoId, this);
		}
	}

	@Override
	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(context, preferKey, 1);
	}

	private class SubMenuListener implements OnActionExpandListener {

		private final OnItemSelectedListener spinnerListener;
		private final View.OnClickListener buttonListener;

		public SubMenuListener(final OnItemSelectedListener spinnerListener,
				final View.OnClickListener buttonListener) {
			this.spinnerListener = spinnerListener;
			this.buttonListener = buttonListener;
		}

		public void initSpinner(MenuItem item) {
			Spinner semester = (Spinner) item.getActionView().findViewById(
					R.id.semester_spinner);
			SemesterArrayAdapter semesterAdapter = new SemesterArrayAdapter(
					context, R.layout.semester_spinner_textview);
			semesterAdapter
					.setDropDownViewResource(android.R.layout.select_dialog_item);
			semester.setAdapter(semesterAdapter);
			semester.setOnItemSelectedListener(spinnerListener);
			if (listener.getSpinnerDefaultPosition() != -1) {
				setSpinnerSelectedPosition(semester,
						listener.getSpinnerDefaultPosition());
			}
		}

		public void setSpinnerSelectedPosition(Spinner spinner, int position) {
			spinner.setSelection(position);
		}

		public void initButton(MenuItem item) {
			Button download = (Button) item.getActionView().findViewById(
					R.id.download_semester_button);
			download.setOnClickListener(buttonListener);
		}

		public boolean onMenuItemActionExpand(MenuItem item) {
			initSpinner(item);
			initButton(item);
			showTipIfNecessary();
			return true;
		}

		@Override
		public boolean onMenuItemActionCollapse(MenuItem arg0) {
			return true;
		}
	}

	private class SemesterArrayAdapter extends ArrayAdapter<String> {
		private SemesterService semesterService;
		private String[] semesters;

		public SemesterArrayAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			semesterService = new SemesterService(context);
			List<Semester> listSemester = semesterService.getSemesters();
			if (listSemester.size() > 0) {
				semesters = new String[listSemester.size()];
				for (int i = 0; i < semesters.length; i++) {
					semesters[i] = listSemester.get(i).getName();
				}
			} else {
				semesters = new String[] { "请添加帐号、" };
			}
		}

		@Override
		public int getCount() {
			return semesters.length;
		}

		@Override
		public String getItem(int position) {
			return semesters[position].replace("|", "第") + "学期";
		}

		@Override
		public long getItemId(int index) {
			return index;
		}
	}
}
