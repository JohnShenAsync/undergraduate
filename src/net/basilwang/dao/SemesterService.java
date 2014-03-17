package net.basilwang.dao;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.entity.Semester;
import android.content.Context;
import android.database.Cursor;

public class SemesterService implements IDAOService {
	private DAOHelper daoHelper;

	public SemesterService(Context context) {
		daoHelper = new DAOHelper(context);
	}

	public void saveSemesterList(List<Semester> semesters) {
		String sql = "INSERT INTO semesters(semestername, accountid) VALUES(?,?)";

		for (int i = 0; i < semesters.size(); i++) {
			Object bindArgs[] = { semesters.get(i).getName(),
					semesters.get(i).getAccountId() };
			daoHelper.insert(sql, bindArgs);
		}
	}

	public void updateBeginAndEndDataOfSemester(Semester semester) {
		Object[] bindArgs = { semester.getBeginDate(), semester.getEndDate(),
				semester.getName() };
		String sql = "UPDATE semesters SET beginningdate = ?,endingdate = ? "
				+ "WHERE semestername = ?";
		daoHelper.update(sql, bindArgs);
	}
	
	public void updateBeginDataOfSemester(Semester semester) {
		Object[] bindArgs = { semester.getBeginDate()};
		String sql = "UPDATE semesters SET beginningdate = ?";
		daoHelper.update(sql, bindArgs);
	}

	public List<Semester> getSemestersByAccountId(int accountId) {
		String sql = "SELECT * FROM semesters WHERE accountid = ?";
		String[] bindArgs = { String.valueOf(accountId) };

		Cursor result = daoHelper.query(sql, bindArgs);
		List<Semester> semesters = null;
		if (result.getCount() > 0) {
			semesters = new ArrayList<Semester>(result.getCount());
		}
		while (result.moveToNext()) {
			Semester semester = new Semester();
			semester.setName(result.getString(result
					.getColumnIndex("semestername")));
			semester.setAccountId(accountId);
			semesters.add(semester);
		}
		daoHelper.closeDB();
		return semesters;
	}

	public List<Semester> getSemesters() {
		String sql = "SELECT * FROM semesters";
		Cursor result = daoHelper.query(sql, null);
		List<Semester> semesters = new ArrayList<Semester>();
		while (result.moveToNext()) {
			Semester semester = new Semester();
			semester.setName(result.getString(result
					.getColumnIndex("semestername")));
			semesters.add(semester);
		}
		daoHelper.closeDB();
		return semesters;
	}

	public void deleteSemestersByAccountId(int accountId) {
		String sql = "DELETE  FROM semesters WHERE accountid = ?";
		daoHelper.deleteListByAccountId(sql, accountId);
	}

	@Override
	public void deleteAccount() {
		String sql = "DELETE  FROM semesters";
		daoHelper.delete(sql);
	}

	public Semester getSemesterByName(String semesterName) {
		String sql = "SELECT * FROM semesters WHERE semestername = ?";
		String[] bingArgs = { semesterName };
		Cursor result = daoHelper.query(sql, bingArgs);
		Semester semester = new Semester();
		while (result.moveToNext()) {
			semester.setName(result.getString(result
					.getColumnIndex("semestername")));
			semester.setBeginDate(result.getString(result
					.getColumnIndex("beginningdate")));
			semester.setEndDate(result.getString(result
					.getColumnIndex("endingdate")));
		}
		daoHelper.closeDB();
		return semester;
	}
}
