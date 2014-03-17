package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;

public class V1Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS curriculum("
				+ "_id integer primary key autoincrement,"
				+ "name varchar(50)," + "teachername varchar(20),"
				+ "curriculumindex integer," + "cemesterperiod varchar(50),"
				+ "classroom varchar(20)," + "intervaltype integer,"
				+ "dayofweek integer," + "cemesterindex varchar(20),"
				+ "timespan integer," + "severity integer," + "myid integer)");

	}

}
