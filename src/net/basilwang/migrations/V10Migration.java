package net.basilwang.migrations;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class V10Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		createTableSemesters(db);
		updateAllCurriculums(db);
		// 2012-10-25 create curriculum_temp and scores_temp table with modifed
		// fields
		db.execSQL("CREATE TABLE IF NOT EXISTS scores_temp("
				+ "_id integer primary key autoincrement,"
				+ "coursename varchar(50)," + "coursecode varchar(20),"
				+ "coursetype varchar(20)," + "coursebelongto varchar(20),"
				+ /* new semesterid field */"semesterid integer,"
				+ "scorelevel number," + "scorepoint number,"
				+ "score integer," + "secondmajorflag integer,"
				+ "secondscore integer," + "thirdscore integer,"
				+ "department varchar(50)," + "memo varchar(20),"
				+ "isthirdscore integer," + "myid integer)");
		db.execSQL("CREATE TABLE IF NOT EXISTS curriculum_temp("
				+ "_id integer primary key autoincrement,"
				+ "name varchar(50)," + "curriculumindex integer,"
				+ "teachername varchar(20),"
				+ /* new semesterid field */"semesterid integer,"
				+ "classroom varchar(20)," + "intervaltype  varchar(20),"
				+ "dayofweek integer," + "semesterindex varchar(20),"
				+ "timespan integer," + "severity integer," + "myid integer,"
				+ "semesterperiod varchar(20)," + "rawinfo text )");
		// 2012-10-25 copy old data to temp table
		// copy old score to score_temp
		db.execSQL("INSERT INTO scores_temp(coursename,coursecode,coursetype,coursebelongto,scorelevel,"
				+ "scorepoint,score,secondmajorflag,secondscore,thirdscore,department,memo,isthirdscore,myid,semesterid)"
				+ "SELECT sc.coursename, sc.coursecode, sc.coursetype,sc.coursebelongto,"
				+ "sc.scorelevel, sc.scorepoint, sc.score, sc.secondmajorflag, sc.secondscore,"
				+ "sc.thirdscore, sc.department, sc.memo, sc.isthirdscore, sc.myid, se._id "
				+ " FROM scores sc, semesters se WHERE se.semestername = "
				+ "sc.cemesteryear || '|' || sc.cemesterindex");
		// // copy old curriculum to curriculum_temp
		// db.execSQL("INSERT INTO curriculum_temp(name, semesterid,dayofweek,"
		// + "timespan,myid,rawinfo)"
		// +
		// "SELECT c.name, s._id, c.dayofweek, c.timespan, c.myid, c.rawinfo FROM curriculum c, semesters s "
		// + "WHERE c.cemesterindex = s.semestername ");
		// //2012-10-25 delete curriculum and scores
		db.execSQL("drop table curriculum");
		db.execSQL("drop table scores");
		// 2012-10-25 rename curriculum_temp and scores_temp
		db.execSQL("ALTER TABLE scores_temp RENAME TO scores");
		db.execSQL("ALTER TABLE curriculum_temp RENAME TO curriculum");
	}

	public void createTableSemesters(SQLiteDatabase db) {
		// 2012-10-25 add semesters
		db.execSQL("CREATE TABLE IF NOT EXISTS semesters("
				+ "_id integer primary key autoincrement,"
				+ "semestername varchar(50)," + "beginningdate varchar(20),"
				+ "endingdate varchar(20)," + "curriculumstatus integer,"
				+ "scorestatus integer," + "isenabled integer,"
				+ "accountid integer)");
		// 2012-10-25 add some semesters
		String[] accounts = getAccountsId(db);
		if (accounts.length != 0) {
			String[] bindArgs = { accounts[0] };
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2009-2010|1',? )", bindArgs);
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2009-2010|2',? )", bindArgs);
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2010-2011|1',? )", bindArgs);
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2010-2011|2',? )", bindArgs);
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2011-2012|1',? )", bindArgs);
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2011-2012|2',? )", bindArgs);
			db.execSQL(
					"Insert INTO semesters(semestername,accountid,beginningdate,endingdate)"
							+ "VALUES('2012-2013|1',?,'1349096936258','1362057088994' )",
					bindArgs);
		} else {
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2009-2010|1',1 )");
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2009-2010|2',1 )");
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2010-2011|1',1 )");
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2010-2011|2',1 )");
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2011-2012|1',1 )");
			db.execSQL("Insert INTO semesters(semestername,accountid)"
					+ "VALUES('2011-2012|2',1)");
			db.execSQL("Insert INTO semesters(semestername,accountid,beginningdate,endingdate)"
					+ "VALUES('2012-2013|1',1,'1349096936258','1362057088994' )");
		}
	}

	/**
	 * Because curriculum's semester's "-" is different
	 * 
	 * @param db
	 */
	public void updateAllCurriculums(SQLiteDatabase db) {
		db.execSQL("UPDATE curriculum SET cemesterindex = REPLACE(cemesterindex, '–','-')");
	}

	public String[] getAccountsId(SQLiteDatabase db) {
		Cursor result = db.rawQuery("SELECT * FROM accounts", null);
		String[] ids = new String[result.getCount()];
		while (result.moveToNext()) {
			int i = 0;
			ids[i] = result.getString(result.getColumnIndex("_id"));
		}
		// 2012-11-22 basilwang don't forget to close cursor
		result.close();
		return ids;
	}
}
