package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;


public class V9Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		db.execSQL("alter table curriculum add column rawinfo text");
	}
	
}
