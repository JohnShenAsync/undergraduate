package net.basilwang.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class V14Migration implements Migration {

	@Override
	public void migrate(SQLiteDatabase db) {
		Log.v("tag", "db14");
		db.execSQL("CREATE TABLE IF NOT EXISTS PointOfSegment(" + "id integer,"
				+ "latitude double," + "longitude double,"
				+ "segmentId integer," + "mapId integer)");
	}
}
