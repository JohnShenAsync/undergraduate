package net.basilwang.utils;

import net.basilwang.R;
import android.content.Context;

public class TAUtils {
	public static String getSemesterIndex(Context context,
			String cemesterIndexValue) {
		String[] cemesterIndexs = context.getResources().getStringArray(
				R.array.semesterindex);
		String[] cemesterIndexValues = context.getResources().getStringArray(
				R.array.semesterindexvalue);
		// 2012-09-27 basilwang use i to record index
		int i;
		for (i = 0; i < cemesterIndexValues.length; i++) {
			if (cemesterIndexValues[i].equals(cemesterIndexValue)) {
				break;
			}
		}
		return i < cemesterIndexValues.length ? cemesterIndexs[i] : "";
	}

	public static String getSemesterIndexValue(Context context,
			String cemesterIndex) {
		String[] cemesterIndexs = context.getResources().getStringArray(
				R.array.semesterindex);
		String[] cemesterIndexValues = context.getResources().getStringArray(
				R.array.semesterindexvalue);

		int i = 0;
		for (; i < cemesterIndexs.length; i++) {
			if (cemesterIndexs[i].equals(cemesterIndex)) {
				break;
			}
		}
		return i < cemesterIndexs.length ? cemesterIndexValues[i] : "";

	}
}
