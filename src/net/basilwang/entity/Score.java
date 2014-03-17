package net.basilwang.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Score {
	private int id;
	private String semesterName;

	private String semesterId;
	private String courseName;
	private String courseCode;
	private String courseType;
	private String courseBelongTo;
	private float scoreLevel;
	private float scorePoint;
	private int score;
	private int secondMajorFlag;
	private int secondScore;
	private int thirdScore;
	private String department;
	private String memo;
	private int isthirdscore;
	private int myid;

	public Score() {

	}

	public Score(String semesterName) {
		this.semesterName = semesterName;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public String getSemesterId() {
		return semesterId;
	}

	public void setSemesterId(String semesterId) {
		this.semesterId = semesterId;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public String getCourseBelongTo() {
		return courseBelongTo;
	}

	public void setCourseBelongTo(String courseBelongTo) {
		this.courseBelongTo = courseBelongTo;
	}

	public float getScoreLevel() {
		return scoreLevel;
	}

	public void setScoreLevel(String scoreLevel) {

		this.scoreLevel = Float.valueOf(scoreLevel);
	}

	public float getScorePoint() {
		return scorePoint;
	}

	public void setScorePoint(String scorePoint) {
		this.scorePoint = Float.valueOf(scorePoint);
	}

	public int getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = Integer.valueOf(score);
	}

	public int getSecondMajorFlag() {
		return secondMajorFlag;
	}

	public void setSecondMajorFlag(int secondMajorFlag) {
		this.secondMajorFlag = secondMajorFlag;
	}

	public int getSecondScore() {
		return secondScore;
	}

	public void setSecondScore(String secondScore) {
		this.secondScore = Integer.valueOf(secondScore);
	}

	public int getThirdScore() {

		return thirdScore;
	}

	public void setThirdScore(String thirdScore) {
		this.thirdScore = Integer.valueOf(thirdScore);
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getIsthirdscore() {
		return isthirdscore;
	}

	public void setIsthirdscore(int isthirdscore) {
		this.isthirdscore = isthirdscore;
	}

	public int getMyid() {
		return myid;
	}

	public void setMyid(int myid) {
		this.myid = myid;
	}

	public void setter(String field, String value) {
		try {
			String methodName = "set" + field;
			Method method = this.getClass().getMethod(methodName, String.class);
			method.invoke(this, value);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public String getter(String field) {
		Object value = null;
		try {
			String methodName = "get" + field;
			Method method = this.getClass().getMethod(methodName);
			value = method.invoke(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value.toString();
	}
}
