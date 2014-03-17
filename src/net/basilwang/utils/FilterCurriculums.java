package net.basilwang.utils;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.entity.Curriculum;

public class FilterCurriculums {
	public List<Curriculum> filterCurriculum(int week,
			List<Curriculum> curriculums) {
		if (curriculums.size() == 0) {
			return curriculums;
		}

		removeByWeekType(curriculums, week);
		removeBySemesterPeriod(curriculums, week);
		fixCurriculumList(curriculums);

		return curriculums;
	}

	/**
	 * Example：1234;abcd;;hijk ---> 1234;abcd;hijk
	 * 
	 * @param names
	 * @return
	 */
	private String[] removeBlankSemicolon(String name) {
		if (name.contains(";;")) {
			return name.replace(";;", ";").split("\\;");
		}
		return name.split("\\;");
	}

	private List<Curriculum> getModifiedCurriculumList(Curriculum c) {
		String[] names = removeBlankSemicolon(c.getName());
		String[] rawInfos = c.getRawInfo().split("\\;");
		String[] intervalTypes = c.getIntervalType().split("\\|");
		String[] semesterPeriods = c.getSemesterPeriod().split("\\|");
		List<Curriculum> list = new ArrayList<Curriculum>(names.length);

		for (int i = 0; i < names.length; i++) {
			Curriculum temp = new Curriculum(names[i], rawInfos[i],
					semesterPeriods[i], intervalTypes[i]);
			list.add(temp);
		}

		return list;
	}

	/**
	 * 
	 * If week is odd week, return even week
	 */
	private String getOppositeWeek(int week) {
		if (week % 2 == 0) {
			return "1";
		} else {
			return "2";
		}
	}

	/**
	 * 修改不符合单双周情况的课程：例如：单周|双周|单周 -> 单周|单周
	 */
	private void modfiyCurriculumByWeek(Curriculum c, int week) {
		List<Curriculum> modifiedCurriculums = getModifiedCurriculumList(c);
		String weekMark = getOppositeWeek(week);

		int num = 0;
		while (num < modifiedCurriculums.size()) {
			Curriculum temp = modifiedCurriculums.get(num);
			if (temp.getIntervalType().equals(weekMark) == true) {
				modifiedCurriculums.remove(num);
				continue;
			}
			num++;
		}
		setCurriculumProerty(c, modifiedCurriculums);
	}

	public void setCurriculumProerty(Curriculum c, List<Curriculum> list) {
		c.setName(getCuriiculumNameByList(list));
		c.setRawInfo(getCurriculumRawInfoByList(list));
		c.setIntervalType(getCurriculumIntervalTypeByList(list));
		c.setSemesterPeriod(getCurriculumSemesterPeriodByList(list));
	}

	public String getCurriculumSemesterPeriodByList(List<Curriculum> list) {
		StringBuilder semesterPeroid = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				semesterPeroid.append("|");
			}
			semesterPeroid.append(list.get(i).getSemesterPeriod());
		}

		return semesterPeroid.toString();
	}

	public String getCurriculumIntervalTypeByList(List<Curriculum> list) {
		StringBuilder intervalType = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				intervalType.append("|");
			}
			intervalType.append(list.get(i).getIntervalType());
		}

		return intervalType.toString();
	}

	public String getCurriculumRawInfoByList(List<Curriculum> list) {
		StringBuilder rawInfo = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				rawInfo.append(";");
			}
			rawInfo.append(list.get(i).getRawInfo());
		}

		return rawInfo.toString();
	}

	public String getCuriiculumNameByList(List<Curriculum> list) {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				name.append(";");
			}
			name.append(list.get(i).getName());
		}

		return name.toString();
	}

	private void removeByWeekType(List<Curriculum> curriculums, int week) {

		for (int index = 0; index < curriculums.size(); index++) {
			Curriculum c = curriculums.get(index);
			if (c.getIntervalType().equals("")) {
				continue;
			}
			modfiyCurriculumByWeek(c, week);
		}
	}

	private void removeBySemesterPeriod(List<Curriculum> curriculums, int week) {
		for (int index = 0; index < curriculums.size(); index++) {
			Curriculum c = curriculums.get(index);
			if (c.getSemesterPeriod().equals("")) {
				continue;
			}
			modfiyCurriculumBySemesterPeroid(c, week);
		}
	}

	private void modfiyCurriculumBySemesterPeroid(Curriculum c, int week) {
		List<Curriculum> modifiedCurriculums = getModifiedCurriculumList(c);
		int num = 0;
		while (num < modifiedCurriculums.size()) {
			Curriculum temp = modifiedCurriculums.get(num);
			int beginWeek = getBeginWeek(temp);
			int endWeek = getEndWeek(temp);
			if (week < beginWeek || week > endWeek) {
				modifiedCurriculums.remove(num);
				continue;
			}
			num++;
		}
		setCurriculumProerty(c, modifiedCurriculums);
	}

	private int getBeginWeek(Curriculum c) {
		String[] weeks = c.getSemesterPeriod().split("\\-");
		return Integer.valueOf(weeks[0]);
	}

	private int getEndWeek(Curriculum c) {
		String[] weeks = c.getSemesterPeriod().split("\\-");
		return Integer.valueOf(weeks[1]);
	}

	/**
	 * delete empty curriculum and fix rawInfo
	 * 
	 */
	private void fixCurriculumList(List<Curriculum> list) {
		int num = 0;
		while (num < list.size()) {
			Curriculum temp = list.get(num);
			if (temp.getName().equals("")) {
				list.remove(num);
				continue;
			}

			if (temp.getRawInfo().contains(";")) {
				temp.setRawInfo(temp.getRawInfo().replace(";", "\n"));
			}
			num++;
		}
	}
}
