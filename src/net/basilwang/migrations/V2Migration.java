package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;


public class V2Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS accounts("
                + "_id integer primary key autoincrement,"
                + "name varchar(50)," + "userno varchar(20)," + "password varchar(20),"+"url varchar(200),"+"ishttps integer)");
	}
	
	
}
