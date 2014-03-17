package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class CurriculumConfig {
	public String classesperday;
	public String tr;
	public String td;
	public String tdClass;
	public String tdNoClass;
	public String tdContent;
	public String classRoomIndex;
	public List<ItemInTD> itemintds;
	public List<ClassIndex> classindexs;
	public List<TdContentToCurriculum> tdContentToCurriculums;

	public CurriculumConfig() {
		classindexs = new ArrayList<ClassIndex>();
		itemintds = new ArrayList<ItemInTD>();
		tdContentToCurriculums = new ArrayList<TdContentToCurriculum>();
		itemintd = new ItemInTD();
		tdContentToCurriculum = new TdContentToCurriculum();
		classindex = new ClassIndex();
	}

	// private
	private String parentTagName = "curriculumconfig";
	private ItemInTD itemintd;
	private ClassIndex classindex;
	private TdContentToCurriculum tdContentToCurriculum;

	private enum tagName {
		classesperday, tr, td, tdClass, tdNoClass, tdContent, classroomindex
	}

	private enum hasChildrentag {
		tdContentToCurriculum, classindex, itemintd
	};

	public void setProperty(String nodeName, String content) {
		if (parentTagName == "curriculumconfig") {
			try {
				switch (hasChildrentag.valueOf(nodeName)) {
				case tdContentToCurriculum:
					parentTagName = "tdContentToCurriculum";
					break;
				case classindex:
					parentTagName = "classindex";
					break;
				case itemintd:
					parentTagName = "itemintd";
					break;
				default:
					break;
				}
			} catch (IllegalArgumentException e) {
				setTag(nodeName, content);
			}
		} else {
			MyChildrenSetTag(parentTagName, nodeName, content);
		}
	}

	public void MyChildrenSetTag(String parentTagName, String nodeName,
			String content) {
		try {
			switch (hasChildrentag.valueOf(parentTagName)) {
			case tdContentToCurriculum:
				tdContentToCurriculum.setProperty(nodeName, content);
				break;
			case classindex:
				classindex.setProperty(nodeName, content);
				break;
			case itemintd:
				itemintd.setProperty(nodeName, content);
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public void setTag(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case classesperday:
				setClassesperday(content);
				break;
			case tr:
				setTr(content);
				break;
			case td:
				setTd(content);
				break;
			case tdClass:
				setTdClass(content);
				break;
			case tdNoClass:
				setTdNoClass(content);
				break;
			case tdContent:
				setTdContent(content);
				break;
			case classroomindex:
				setClassRoomIndex(content);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}

	}

	public void addListNode(String nodeName) {
		try {
			switch (hasChildrentag.valueOf(nodeName)) {
			case tdContentToCurriculum:
				addtdContentToCurriculumNode();
				break;
			case classindex:
				addclassindexNode();
				break;
			case itemintd:
				additemintdNode();
				break;
			}

		} catch (IllegalArgumentException e) {
			myChildrenAddListNode(nodeName);
			return;
		}
	}

	public void myChildrenAddListNode(String nodeName) {
		try {
			switch (hasChildrentag.valueOf(parentTagName)) {
			case itemintd:
				itemintd.addListNode(nodeName);
				break;
			}
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	// add node method
	private void addtdContentToCurriculumNode() {
		tdContentToCurriculums.add(tdContentToCurriculum);
		parentTagName = "curriculumconfig";
		tdContentToCurriculum = new TdContentToCurriculum();
	}

	private void addclassindexNode() {
		classindexs.add(classindex);
		parentTagName = "curriculumconfig";
		classindex = new ClassIndex();
	}

	private void additemintdNode() {
		itemintds.add(itemintd);
		parentTagName = "curriculumconfig";
		itemintd = new ItemInTD();
	}

	// set method
	public void setClassesperday(String classesperday) {
		this.classesperday = classesperday;
	}

	public void setTr(String tr) {
		this.tr = tr;
	}

	public void setTd(String td) {
		this.td = td;
	}

	public void setTdClass(String tdClass) {
		this.tdClass = tdClass;
	}

	public void setTdNoClass(String tdNoClass) {
		this.tdNoClass = tdNoClass;
	}

	public void setTdContent(String tdContent) {
		this.tdContent = tdContent;
	}

	// set method ends

	// get method
	public String getClassesperday() {
		return classesperday;
	}

	public String getTr() {
		return tr;
	}

	public String getTd() {
		return td;
	}

	public String getTdClass() {
		return tdClass;
	}

	public String getTdNoClass() {
		return tdNoClass;
	}

	public String getTdContent() {
		return tdContent;
	}

	public List<ItemInTD> getItemintds() {
		return itemintds;
	}

	public List<ClassIndex> getClassindexs() {
		return classindexs;
	}

	public List<TdContentToCurriculum> getTdContentToCurriculums() {
		return tdContentToCurriculums;
	}

	public String getClassRoomIndex() {
		return classRoomIndex;
	}

	public void setClassRoomIndex(String classRoomIndex) {
		this.classRoomIndex = classRoomIndex;
	}
}
