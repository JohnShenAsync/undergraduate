package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class V13Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v("tag", "db13");
		db.execSQL("CREATE TABLE IF NOT EXISTS messages("
				+ "content varchar(200))");
	}
}
