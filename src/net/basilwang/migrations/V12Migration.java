package net.basilwang.migrations;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class V12Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM semesters WHERE semestername='2012-2013|2'",
				null);
		if (cursor.moveToNext()) {
			db.execSQL("UPDATE semester SET begindate=1362326400000,enddate=1373817600000"
					+ "WHERE semestername=2012-2013|2");
		}
		cursor.close();
		/***
		  2013-01-24 basilwang  don't close db(db.close())  VERY IMPORTANT!!!!!!!
		 * **/
	}
}
