package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;

public class V8Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS scores("
				+ "_id integer primary key autoincrement,"
				+ "coursename varchar(50)," + "coursecode varchar(20),"
				+ "coursetype varchar(20)," + "coursebelongto varchar(20),"
				+ "cemesteryear varchar(20)," + "cemesterindex integer,"
				+ "scorelevel number," + "scorepoint number,"
				+ "score integer," + "secondmajorflag integer,"
				+ "secondscore integer," + "thirdscore integer,"
				+ "department varchar(50)," + "memo varchar(20),"
				+ "isthirdscore integer," + "myid integer)");
	}

}
