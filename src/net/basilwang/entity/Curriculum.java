package net.basilwang.entity;

public class Curriculum {
	private int id;
	private String name;
	private String teachername;
	private String semestername;
	private String semesterid;
	private String classroom;
	private String intervalType;
	private int dayOfWeek;
	private int timeSpan;
	private int severity;
	private String rawInfo;
	private int curriculumIndex;
	private String semesterPeriod;

	public Curriculum() {
	}

	public Curriculum(String name, String rawInfo, String semesterPeriod,
			String intervalType) {
		this.name = name;
		this.rawInfo = rawInfo;
		this.semesterPeriod = semesterPeriod;
		this.intervalType = intervalType;
	}

	public String getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(String semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public int getCurriculumIndex() {
		return curriculumIndex;
	}

	public void setCurriculumIndex(int curriculumIndex) {
		this.curriculumIndex = curriculumIndex;
	}

	private int myid;

	public String getRawInfo() {
		return rawInfo;
	}

	public void setRawInfo(String rawInfo) {
		this.rawInfo = rawInfo;
	}

	public String getSemesterid() {
		return semesterid;
	}

	public void setSemesterid(String semesterid) {
		this.semesterid = semesterid;
	}

	public int getTimeSpan() {
		return timeSpan;
	}

	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeachername() {
		return teachername;
	}

	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}

	public String getSemestername() {
		return semestername;
	}

	public void setSemestername(String semestername) {
		this.semestername = semestername;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public String getIntervalType() {
		return intervalType;
	}

	public void setIntervalType(String intervalType) {
		this.intervalType = intervalType;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public int getMyid() {
		return myid;
	}

	public void setMyid(int myid) {
		this.myid = myid;
	}
}
