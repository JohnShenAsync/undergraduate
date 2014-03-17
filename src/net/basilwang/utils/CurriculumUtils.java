package net.basilwang.utils;

import java.util.List;

import net.basilwang.config.ClassIndex;
import net.basilwang.config.SAXParse;
import net.basilwang.entity.Curriculum;

public class CurriculumUtils {
	private static FilterCurriculums filter;

	public static String formatCurriculumIndex(int curriculumIndex, int timespan) {
		String curriculumStr = "";
		for (int i = 1; i <= timespan; i++) {
			curriculumStr += String.valueOf(curriculumIndex) + " ";
			curriculumIndex += 1;
		}
		curriculumStr = curriculumStr.substring(0, curriculumStr.length() - 1);
		return String.format(" 第%s节", curriculumStr);
	}

	public static String substrCurriculum(String curriculum) {
		String name = "";
		if (curriculum.split("\\,").length >= 2) {
			name = curriculum.split("\\,")[0];
		}
		String newName = name.length() > 8 ? name.substring(0, 8) + "..."
				: name;

		return String.format("%s %s", newName,
				curriculum.replace(name, "").replace(",", " "))
				.replace(";", "");
	}

	public static List<ClassIndex> getClassIndexList() {
		return SAXParse.getTAConfiguration().getSelectedCollege()
				.getCurriculumConfig().getClassindexs();
	}

	public static int getClassIndexCount() {
		int c = Integer.valueOf(SAXParse.getTAConfiguration()
				.getSelectedCollege().getCurriculumConfig().getClassesperday());
		return c;
	}

	/**
	 * 把不符合当前周的课程删除掉
	 * 
	 * @param week
	 *            第几周
	 * @param curriculums
	 *            查找的原始课程列表
	 */
	public static List<Curriculum> filterCurriclumsByWeek(int week,
			List<Curriculum> curriculums) {
		if (filter == null) {
			filter = new FilterCurriculums();
		}
		return filter.filterCurriculum(week, curriculums);
	}
}
