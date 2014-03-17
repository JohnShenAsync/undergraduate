package net.basilwang.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.basilwang.config.CurriculumConfig;
import net.basilwang.entity.Curriculum;

/**
 * 2012-12-9 WeiXiaoXing create this class. Use this class parse all info about
 * curriculum
 * 
 * @author Star
 * 
 */
public class ParseCurriculumInfo {
	private static final int CURRICULUM_HTML_COLUMNS_COUNT = 9;
	private CurriculumConfig curriculumConfig;

	public ParseCurriculumInfo(CurriculumConfig curriculumConfig) {
		this.curriculumConfig = curriculumConfig;
	}

	public Curriculum[] getCurriculums(String str) {
		// 请求了错误的学年和学期，返回空
		if (str.contains("<br>") == false) {
			Curriculum[] tds = new Curriculum[1];
			return tds;
		}

		final int classperday = getClassPerDay();
		final int curriculmCount = getCurriculumCount(classperday, false) + 1;
		Curriculum[] tds = new Curriculum[curriculmCount];
		for (int i = 1; i < curriculmCount; i++) {
			tds[i] = new Curriculum();
		}

		Pattern pattern = Pattern.compile(curriculumConfig.getTr());// 取出每一行
		Matcher matcher = pattern.matcher(str);
		Matcher rowMatcher = null;

		// Discard the first two rows
		matcher.find();
		matcher.find();

		int i = 1;
		while (matcher.find()) {
			String rowInfo = matcher.group();
			pattern = Pattern.compile(curriculumConfig.getTd());
			rowMatcher = pattern.matcher(rowInfo);
			while (rowMatcher.find()) {
				String singleGrid = rowMatcher.group();
				collocateGridContent(singleGrid, tds, i);
				while (i < curriculmCount && !(tds[i].getName() == null)) {
					i++;
				}
			}
		}
		return setCurriculumsIndex(fetchNewTds(tds, classperday));
	}

	public int getClassPerDay() {
		return Integer.valueOf(curriculumConfig.getClassesperday());
	}

	private int getCurriculumCount(int classperday, boolean IsCutFirstTwoColumns) {

		if (IsCutFirstTwoColumns) {
			return (CURRICULUM_HTML_COLUMNS_COUNT - 2) * classperday;
		}

		return CURRICULUM_HTML_COLUMNS_COUNT * classperday;
	}

	private void setCurriculumProperty(String gridContent, Curriculum c) {
		String name = fetchCurriculumInfo(gridContent);
		String rawInfo = fecthCurriculumRawInfo(gridContent);

		c.setName(name);
		c.setRawInfo(rawInfo);
		c.setSemesterPeriod(getCurriculumSemesterPeriod(rawInfo));
		c.setIntervalType(getIntervalType(name));
	}

	private String getCurriculumSemesterPeriod(String rawInfo) {
		if (rawInfo == null || rawInfo.equals("")) {
			return null;
		}
		// 2012-12-09 2013年1月22日(08:30-10:10) cause problem
		Pattern semsterPeriodPattern = Pattern.compile("第(\\d+-\\d+)周");
		Matcher matcher = semsterPeriodPattern.matcher(rawInfo);
		String semesterPeriod = "";
		if (matcher.find()) {
			semesterPeriod = matcher.group(1);
		}

		while (matcher.find()) {
			semesterPeriod = semesterPeriod + "|" + matcher.group(1);
		}

		return semesterPeriod;
	}

	private void collocateGridContent(String singleGridContent,
			Curriculum[] tds, int i) {
		Pattern pattern = Pattern.compile(curriculumConfig.getTdClass());
		Matcher contentMatcher = pattern.matcher(singleGridContent);
		int columns = CURRICULUM_HTML_COLUMNS_COUNT;

		if (contentMatcher.find()) {
			int rowspan = Integer.valueOf(contentMatcher.group(1));
			tds[i].setTimeSpan(rowspan);
			for (int j = 0; j < rowspan; j++) {
				String gridContent = contentMatcher.group(2);
				setCurriculumProperty(gridContent, tds[i + j * columns]);
			}
		} else {
			Pattern noRowSpanPattern = Pattern.compile(curriculumConfig
					.getTdNoClass());
			Matcher noRowSpanMatcher = noRowSpanPattern
					.matcher(singleGridContent);
			tds[i].setTimeSpan(1);
			if (noRowSpanMatcher.find()) {
				if (IsNoRowSpanHasBr(noRowSpanMatcher.group(1)) == false) {
					tds[i].setName(noRowSpanMatcher.group(1));
				} else {
					setNoRowSpanCurriculumProperty(noRowSpanMatcher.group(1),
							tds[i]);
				}
			}
		}
	}

	private String getIntervalType(String name) {
		String[] names = name.split("\\;");
		StringBuilder intervalType = new StringBuilder(names.length * 2);
		for (int i = 0; i < names.length; i++) {
			addSeparator(intervalType, i);
			if (names[i].contains("单周")) {
				intervalType.append('1');
			} else if (names[i].contains("双周")) {
				intervalType.append('2');
			} else {
				intervalType.append('0');
			}
		}
		return intervalType.toString();
	}

	/**
	 * Example: intervalType 单周|双周； 单周|双周|单周|双周; |""|单周
	 */
	private void addSeparator(StringBuilder bulider, int num) {
		if (num >= 1) {
			bulider.append("|");
		}
	}

	private void setNoRowSpanCurriculumProperty(String gridContent,
			Curriculum curriculum) {
		String rawInfo = gridContent.replace("<br>", "\n");
		String name = getCurriculumNameInfo(gridContent);
		curriculum.setName(name);
		curriculum.setRawInfo(rawInfo);
		curriculum.setSemesterPeriod(getCurriculumSemesterPeriod(rawInfo));
		curriculum.setIntervalType(getIntervalType(name));
	}

	private String fetchCurriculumInfo(String gridContent) {
		String curriculumInfo = "";
		// One grid has two curriculums
		if (gridContent.contains("<br><br>")) {
			String[] tdContentsTemp = splitGridContent(gridContent);
			for (int i = 0; i < tdContentsTemp.length; i++) {
				curriculumInfo += getCurriculumNameInfo(tdContentsTemp[i]);
				// Maybe curriculum contains 单周 or 双周
				if (isHaveOddWeekOrEvenWeek(tdContentsTemp[i])) {
					curriculumInfo += ",";
					curriculumInfo += getOddWeekOrEvenWeek(tdContentsTemp[i]);
				}
				curriculumInfo += ";";
			}
			return curriculumInfo;
		}
		// gridContent contains 单周 or 双周
		if (isHaveOddWeekOrEvenWeek(gridContent)) {
			curriculumInfo += getCurriculumNameInfo(gridContent);
			curriculumInfo += ",";
			curriculumInfo += getOddWeekOrEvenWeek(gridContent);
			curriculumInfo += ";";
			return curriculumInfo;
		}
		// The simplest state
		curriculumInfo += getCurriculumNameInfo(gridContent);
		curriculumInfo += ";";

		return curriculumInfo;
	}

	private String[] splitGridContent(String gridContent) {
		return gridContent.split("<br><br>");
	}

	private String getCurriculumRawInfo(String content) {
		return content.replace("<br>", "\n");
	}

	public String fecthCurriculumRawInfo(String gridContent) {

		String[] temp = splitGridContent(gridContent);
		StringBuilder rawInfo = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			if (i > 0) {
				rawInfo = rawInfo.append(";");
			}
			rawInfo = rawInfo.append(getCurriculumRawInfo(temp[i]));
		}
		return rawInfo.toString();
	}

	private boolean IsNoRowSpanHasBr(String content) {
		if (content.contains("<br>")) {
			return true;
		}
		return false;
	}

	private String getCurriculumNameInfo(String content) {
		String[] contentChilds = content.split("<br>");
		int classroomIndex = Integer.valueOf(curriculumConfig
				.getClassRoomIndex());

		if (contentChilds.length < 2) {
			return "";
		}
		// 劳动实践课程无教室
		if (contentChilds.length <= classroomIndex) {
			return contentChilds[0] + contentChilds[1];
		}
		return contentChilds[0] + "," + contentChilds[classroomIndex];
	}

	private boolean isHaveOddWeekOrEvenWeek(String gridContent) {
		if (gridContent.contains("单周") || gridContent.contains("双周")) {
			return true;
		}
		return false;
	}

	private String getOddWeekOrEvenWeek(String content) {

		if (content.contains("双周")) {
			return "双周";
		}
		return "单周";
	}

	// /*2012-08-23 basilwang there are two purpuses in the below code
	// 1 change the array from
	// 1 2 3 4 5 6 7 8 9
	// 10 11 12 13 14 15 16 17 18
	// 19 20 21 22 23 24 25 26 27
	// 28 29 30 31 32 33 34 35 36
	// 37 38 39 40 41 42 43 44 45
	// 46 47 48 49 50 51 52 53 54
	// 55 56 57 58 59 60 61 62 63
	// 64 65 66 67 68 69 70 71 72
	// 73 74 75 76 77 78 79 80 81
	// 82 83 84 85 86 87 88 89 90
	// 91 92 93 94 95 96 97 98 99
	// then remove the first two columns and reindex the array order by row
	// 1 12 23 34 45 56 67
	// 2 13 24 35 46 57 68
	// 3 14 25 36 47 58 69
	// 4 15 26 37 48 59 70
	// 5 16 27 38 49 60 71
	// 6 17 28 39 50 61 72
	// 7 18 29 40 51 62 73
	// 8 19 30 41 52 63 74
	// 9 20 31 42 53 64 75
	// 10 21 32 43 54 65 76
	// 11 22 33 44 55 66 77
	// and be sure we need minus 1 for all, cause of the first element in array
	// is 0
	// 2 when we reindex the array order by row we need set order id.
	// */
	private Curriculum[] fetchNewTds(Curriculum[] tds, int classperday) {

		Curriculum[] newtds = new Curriculum[getCurriculumCount(classperday,
				true) + 1];
		int columnCount = CURRICULUM_HTML_COLUMNS_COUNT;
		int colnum = 0;
		int rownum = 0;
		for (int i = 3; i < tds.length; i++) {

			if ((i - 1) % columnCount == 0) {
				i += 2;
			}
			rownum = (i - 1) / columnCount;
			colnum = ((i % columnCount) == 0 ? columnCount : i % columnCount) - 2;
			newtds[rownum + (colnum - 1) * classperday + 1] = tds[i];
			newtds[rownum + (colnum - 1) * classperday + 1]
					.setDayOfWeek(colnum);
			newtds[rownum + (colnum - 1) * classperday + 1].setName(tds[i]
					.getName().equals("&nbsp;") ? "" : tds[i].getName());
		}
		return newtds;
	}

	public Curriculum[] setCurriculumsIndex(Curriculum[] tds) {
		for (int i = 1; i < tds.length; i++) {
			tds[i].setCurriculumIndex(getCurriculumindex(i));
		}
		return tds;
	}

	public int getCurriculumindex(int i) {
		int index = i % getClassPerDay();
		return index == 0 ? getClassPerDay() : index;
	}
}
