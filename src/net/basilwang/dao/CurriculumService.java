package net.basilwang.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.basilwang.entity.Curriculum;
import net.basilwang.entity.Semester;
import android.content.Context;
import android.database.Cursor;

public class CurriculumService implements IDAOService {
	private DAOHelper daoHelper;

	public CurriculumService(Context context) {
		daoHelper = new DAOHelper(context);
	}

	/**
	 * 保存
	 * */
	public void save(Curriculum curriculum) {
		curriculum.setSemesterid(getSemesterId(curriculum.getSemestername()));
		String sql = "INSERT INTO curriculum (name,curriculumindex,teachername,classroom,intervaltype,dayofweek,timeSpan,myid,rawinfo,semesterid,semesterperiod) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		Object[] bindArgs = { curriculum.getName(),
				curriculum.getCurriculumIndex(), curriculum.getTeachername(),
				curriculum.getClassroom(), curriculum.getIntervalType(),
				curriculum.getDayOfWeek(), curriculum.getTimeSpan(),
				curriculum.getMyid(), curriculum.getRawInfo(),
				curriculum.getSemesterid(), curriculum.getSemesterPeriod() };
		daoHelper.insert(sql, bindArgs);
	}

	// 2012-04-21 basilwang temporarily to use for not downloading curriculum
	// again
	public Boolean needDownloadCurriculum() {
		String sql = "select count(*) as cnt from curriculum";
		Cursor result = daoHelper.query(sql, null);

		int cnt = 0;
		if (result.moveToNext()) {
			cnt = result.getInt(result.getColumnIndex("cnt"));
		}
		daoHelper.closeDB();
		return cnt == 0 ? true : false;
	}

	public void delete(int accountId, String semesterName) {
		String sql = "delete from curriculum where myid=? and semesterid=?";
		Object[] bindArgs = { accountId, getSemesterId(semesterName) };
		daoHelper.delete(sql, bindArgs);
	}

	public void update(Curriculum curriculum) {
		String sql = "update curriculum set severity=? where _id=?";
		Object[] bindArgs = { curriculum.getSeverity(), curriculum.getId() };
		daoHelper.update(sql, bindArgs);
	}

	public List<Map<String, Object>> getCurriculumByDay(String semesterValue,
			int day, int accountId) {
		String semesterid = String.valueOf(getSemesterId(semesterValue));
		String sql = "select _id,name,timespan,severity,teachername,classroom,intervaltype,semesterperiod from curriculum where dayofweek=? and timespan !=0 and myid=? and semesterid=?";
		String[] bindArgs = { String.valueOf(day), String.valueOf(accountId),
				semesterid };
		Cursor result = daoHelper.query(sql, bindArgs);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (result.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", result.getString(result.getColumnIndex("_id")));
			map.put("name", result.getString(result.getColumnIndex("name")));
			map.put("timespan",
					result.getString(result.getColumnIndex("timespan")));
			int severity = result.getString(result.getColumnIndex("severity")) != null ? Integer
					.valueOf(result.getString(result.getColumnIndex("severity")))
					: -1;
			map.put("severity", String.valueOf(severity));
			map.put("teachername",
					result.getString(result.getColumnIndex("teachername")));
			map.put("classroom",
					result.getString(result.getColumnIndex("classroom")));
			map.put("intervaltype",
					result.getInt(result.getColumnIndex("intervaltype")));
			map.put("semesterperiod",
					result.getString(result.getColumnIndex("semesterperiod")));
			list.add(map);
		}
		daoHelper.closeDB();
		return list;
	}

	public Cursor getCurriculumCursorByDay(String semesterValue, int day,
			int accountId) {
		String semesterid = getSemesterId(semesterValue);
		String sql = "select _id,name,timespan,severity,teachername,classroom,intervaltype,rawinfo,semesterid from curriculum where dayofweek=? and timespan !=0 and name !='' and myid=? and semesterid=?";
		String[] bindArgs = { String.valueOf(day), String.valueOf(accountId),
				semesterid };
		Cursor result = daoHelper.query(sql, bindArgs);
		daoHelper.closeDB();
		return result;
	}

	public List<Curriculum> getCurriculumListByDay(String semesterValue,
			int day, int accountId) {
		String semesterid = getSemesterId(semesterValue.replace("–", "-"));
		String sql = "select _id,name,timespan,severity,teachername,semesterid,classroom,intervaltype,rawinfo,curriculumindex,dayofweek,semesterperiod from curriculum where dayofweek=? and timespan !=0 and name !='' and myid=? and semesterid=?";
		String[] bindArgs = { String.valueOf(day), String.valueOf(accountId),
				semesterid };
		Cursor result = daoHelper.query(sql, bindArgs);
		/**
		 * WeiXiaoXing: I advice that when we new a ArrayList, we'd better give
		 * a size. Or maybe we can use a LinkedList
		 **/
		List<Curriculum> list = new ArrayList<Curriculum>(result.getCount());
		while (result.moveToNext()) {
			Curriculum c = new Curriculum();
			c.setId(result.getInt(result.getColumnIndex("_id")));
			c.setName(result.getString(result.getColumnIndex("name")));
			c.setTimeSpan(result.getInt(result.getColumnIndex("timespan")));
			int severity = result.getInt(result.getColumnIndex("severity"));
			c.setSeverity(severity);
			c.setTeachername(result.getString(result
					.getColumnIndex("teachername")));
			c.setClassroom(result.getString(result.getColumnIndex("classroom")));
			c.setIntervalType(result.getString(result
					.getColumnIndex("intervaltype")));
			c.setRawInfo(result.getString(result.getColumnIndex("rawinfo")));
			c.setSemesterid(result.getString(result
					.getColumnIndex("semesterid")));
			c.setCurriculumIndex(result.getInt(result
					.getColumnIndex("curriculumindex")));
			c.setDayOfWeek(result.getInt(result.getColumnIndex("dayofweek")));
			c.setSemesterPeriod(result.getString(result
					.getColumnIndex("semesterperiod")));
			list.add(c);
		}
		daoHelper.closeDB();
		return list;
	}

	public List<Curriculum> getCurriculumList(String semesterValue,
			int accountId) {
		String semesterid = getSemesterId(semesterValue.replace("–", "-"));
		String sql = "select _id,name,timespan,severity,teachername,semesterid,classroom,intervaltype,rawinfo,curriculumindex,dayofweek,semesterperiod from curriculum where timespan !=0 and name !='' and myid=? and semesterid=?";
		String[] bindArgs = { String.valueOf(accountId), semesterid };
		Cursor result = daoHelper.query(sql, bindArgs);
		List<Curriculum> list = new ArrayList<Curriculum>();
		while (result.moveToNext()) {
			Curriculum c = new Curriculum();
			c.setId(result.getInt(result.getColumnIndex("_id")));
			c.setName(result.getString(result.getColumnIndex("name")));
			c.setTimeSpan(result.getInt(result.getColumnIndex("timespan")));
			int severity = result.getInt(result.getColumnIndex("severity"));
			c.setSeverity(severity);
			c.setTeachername(result.getString(result
					.getColumnIndex("teachername")));
			c.setClassroom(result.getString(result.getColumnIndex("classroom")));
			c.setIntervalType(result.getString(result
					.getColumnIndex("intervaltype")));
			c.setRawInfo(result.getString(result.getColumnIndex("rawinfo")));
			c.setSemesterid(result.getString(result
					.getColumnIndex("semesterid")));
			c.setCurriculumIndex(result.getInt(result
					.getColumnIndex("curriculumindex")));
			c.setDayOfWeek(result.getInt(result.getColumnIndex("dayofweek")));

			c.setSemesterPeriod(result.getString(result
					.getColumnIndex("semesterperiod")));
			list.add(c);
		}
		daoHelper.closeDB();
		return list;
	}

	public Curriculum getCurriculumById(int id) {
		String sql = "select _id,name,timespan,severity from curriculum where _id=?";
		String[] bindArgs = { String.valueOf(id) };
		Cursor result = daoHelper.query(sql, bindArgs);
		Curriculum curriculum = new Curriculum();
		if (result.moveToNext()) {
			curriculum.setId(Integer.parseInt(result.getString(result
					.getColumnIndex("_id"))));
			curriculum.setName(result.getString(result.getColumnIndex("name")));
			curriculum.setTimeSpan(Integer.parseInt(result.getString(result
					.getColumnIndex("timespan"))));
			// 2012-04-21 basilwang 0 is important -1 is nothing
			int severity = result.getString(result.getColumnIndex("severity")) != null ? Integer
					.valueOf(result.getString(result.getColumnIndex("severity")))
					: -1;
			curriculum.setSeverity(severity);
		}
		daoHelper.closeDB();
		return curriculum;
	}

	/**
	 * We will select the semesters which its curriculum has downloaded
	 * 
	 */
	public List<Semester> getDownloaedSemesters() {
		String[] bindArgs = getDownloadedSemesterIds();
		if (bindArgs == null)
			return new ArrayList<Semester>(0);

		String sql = "SELECT _id, semestername,beginningdate,endingdate FROM semesters WHERE _id IN";
		sql = constructSQL(sql, bindArgs);
		Cursor result = daoHelper.query(sql, bindArgs);
		List<Semester> semesters = new ArrayList<Semester>(result.getCount());
		while (result.moveToNext()) {
			Semester semester = new Semester();
			semester.setAccountId(result.getInt(result.getColumnIndex("_id")));
			semester.setName(result.getString(result
					.getColumnIndex("semestername")));
			semester.setBeginDate(result.getString(result
					.getColumnIndex("beginningdate")));
			semester.setEndDate(result.getString(result
					.getColumnIndex("endingdate")));
			semesters.add(semester);
		}
		daoHelper.closeDB();
		return semesters;
	}

	/**
	 * We will generate sql like that: SELECT * FROM table WHERE id IN(?,?,?)
	 * 
	 */
	public String constructSQL(String sql, String[] agrs) {
		sql += "(?";
		for (int i = 1; i < agrs.length; i++) {
			sql += ",?";
		}
		sql += ")";

		return sql;
	}

	public String[] getSemesterNames() {
		String[] bindArgs = getDownloadedSemesterIds();

		String sql = "SELECT semestername FROM semesters WHERE _id IN ";
		sql = constructSQL(sql, bindArgs);
		Cursor result = daoHelper.query(sql, bindArgs);
		String semesters[] = new String[result.getCount()];
		int i = 0;
		while (result.moveToNext()) {
			semesters[i] = result.getString(result
					.getColumnIndex("semestername"));
			i++;
		}
		daoHelper.closeDB();
		return semesters;
	}

	/**
	 * We will select the semesters' id which its curriculum has downloaded
	 * 
	 * @return ids[]
	 */
	public String[] getDownloadedSemesterIds() {
		String sql = "SELECT semesterid FROM curriculum GROUP BY semesterid ";
		Cursor result = daoHelper.query(sql, null);
		String[] ids = null;
		if (result.getCount() != 0) {
			ids = new String[result.getCount()];
			int i = 0;
			while (result.moveToNext()) {
				ids[i] = result.getString(result.getColumnIndex("semesterid"));
				i++;
			}
		}
		daoHelper.closeDB();
		return ids;
	}

	private String getSemesterId(String semestername) {
		String sql = "SELECT _id FROM semesters WHERE semestername = ?";
		String[] bindArgs = { semestername };
		Cursor result = daoHelper.query(sql, bindArgs);
		String id = "0";
		if (result.moveToNext()) {
			id = result.getString(result.getColumnIndex("_id"));
		}
		daoHelper.closeDB();
		return id;
	}

	public void deleteAccount() {
		String sql = "DELETE  FROM curriculum";
		daoHelper.delete(sql);
	}
}
