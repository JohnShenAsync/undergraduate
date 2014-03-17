package net.basilwang.migrations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.basilwang.entity.Curriculum;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class V11Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS curriculum_temp("
				+ "_id integer primary key autoincrement,"
				+ "name varchar(50)," + "curriculumindex integer,"
				+ "teachername varchar(20),"
				+ /* new semesterid field */"semesterid integer,"
				+ "classroom varchar(20)," + "intervaltype varchar(20),"
				+ "dayofweek integer," + "semesterindex varchar(20),"
				+ "timespan integer," + "severity integer," + "myid integer,"
				+ "semesterperiod varchar(20)," + "rawinfo text )");
		// copy old curriculum to curriculum_temp
		db.execSQL("INSERT INTO curriculum_temp(_id,name, semesterid,dayofweek,"
				+ "timespan,myid,rawinfo)"
				+ "SELECT c._id, c.name, c.semesterid, c.dayofweek, c.timespan, c.myid, c.rawinfo FROM curriculum c");
		db.execSQL("drop table curriculum");
		db.execSQL("ALTER TABLE curriculum_temp RENAME TO curriculum");
		modfiyCurriculums(db);
	}

	public void modfiyCurriculums(SQLiteDatabase db) {
		String sql = "SELECT * FROM curriculum";
		Cursor result = db.rawQuery(sql, null);
		List<Curriculum> curriculums = new ArrayList<Curriculum>(
				result.getCount());
		while (result.moveToNext()) {
			Curriculum c = new Curriculum();
			c.setId(result.getInt(result.getColumnIndex("_id")));
			c.setName(result.getString(result.getColumnIndex("name")));
			c.setRawInfo(result.getString(result.getColumnIndex("rawinfo")));
			curriculums.add(c);
		}
		result.close();

		for (int i = 0; i < curriculums.size(); i++) {
			Curriculum c = curriculums.get(i);
			c.setSemesterPeriod(getCurriculumSemesterPeriod(c.getRawInfo()));
			c.setIntervalType(getIntervalType(c.getName()));
		}

		sql = "UPDATE curriculum SET intervaltype = ?,semesterperiod = ? WHERE _id = ?";
		for (int i = 0; i < curriculums.size(); i++) {
			Curriculum c = curriculums.get(i);
			Object[] bindArgs = { c.getIntervalType(), c.getSemesterPeriod(),
					c.getId() };
			db.execSQL(sql, bindArgs);
		}
	}

	private String getCurriculumSemesterPeriod(String rawInfo) {
		if (rawInfo == null || rawInfo.equals("")) {
			return null;
		}
		Pattern semsterPeriodPattern = Pattern.compile("\\d+-\\d+");
		Matcher matcher = semsterPeriodPattern.matcher(rawInfo);
		String semesterPeriod = "";
		if (matcher.find()) {
			return matcher.group();
		}
		while (matcher.find()) {
			semesterPeriod = semesterPeriod + "|" + matcher.group();
		}
		return semesterPeriod;
	}

	private String getIntervalType(String name) {
		if (name.contains("单周")) {
			if (name.contains("双周")) {
				return "单周|双周";
			} else {
				return "单周";
			}
		} else if (name.contains("双周")) {
			if (name.contains("单周")) {
				return "单周|双周";
			} else {
				return "双周";
			}
		}
		return "";
	}
}
