package net.basilwang;

import java.util.List;

import net.basilwang.dao.SemesterService;
import net.basilwang.entity.Semester;
import android.content.Context;
import android.widget.ArrayAdapter;

public class SemesterArrayAdapter extends ArrayAdapter<String> {
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
