package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class V15Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		Log.v("tag", "db15");
		db.execSQL("CREATE TABLE IF NOT EXISTS PointOfStructure("
				+ "id integer," + "name varchar(50)," + "latitude double,"
				+ "longitude double," + "width double," + "height double,"
				+ "mode integer," + "mapid integer," + "color integer)");
	}
}
