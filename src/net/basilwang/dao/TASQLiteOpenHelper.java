package net.basilwang.dao;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.basilwang.migrations.Migration;
import net.basilwang.migrations.V10Migration;
import net.basilwang.migrations.V12Migration;
import net.basilwang.migrations.V13Migration;
import net.basilwang.migrations.V14Migration;
import net.basilwang.migrations.V15Migration;
import net.basilwang.migrations.V16Migration;
import net.basilwang.migrations.V1Migration;
import net.basilwang.migrations.V2Migration;
import net.basilwang.migrations.V8Migration;
import net.basilwang.migrations.V9Migration;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TASQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "TASQLiteOpenHelper";
	private static final String name = "undergraduate"; // 数据库名称
	private static final int version = 16; // 数据库版本

	private static final SortedMap<Integer, Migration> ALL_MIGRATIONS = new TreeMap<Integer, Migration>();

	static {
		ALL_MIGRATIONS.put(1, new V1Migration());
		ALL_MIGRATIONS.put(2, new V2Migration());
		ALL_MIGRATIONS.put(8, new V8Migration());
		ALL_MIGRATIONS.put(9, new V9Migration());
		ALL_MIGRATIONS.put(10, new V10Migration());
		// v11 has never released
		ALL_MIGRATIONS.put(12, new V12Migration());
		ALL_MIGRATIONS.put(13, new V13Migration());
		ALL_MIGRATIONS.put(14, new V14Migration());
		ALL_MIGRATIONS.put(15, new V15Migration());
		ALL_MIGRATIONS.put(16, new V16Migration());
	}

	public TASQLiteOpenHelper(Context context) {
		/**
		 * CursorFactory指定在执行查询时获得一个游标实例的工厂类。 设置为null，则使用系统默认的工厂类。
		 */
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		executeMigrations(db, ALL_MIGRATIONS.keySet());
	}

	private void executeMigrations(SQLiteDatabase db,
			Set<Integer> migrationVersions) {
		for (Integer version : migrationVersions) {
			Log.i(TAG, "Migrating to version " + version);

			ALL_MIGRATIONS.get(version).migrate(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion);
		SortedMap<Integer, Migration> migrations = ALL_MIGRATIONS.subMap(
				oldVersion, newVersion);
		executeMigrations(db, migrations.keySet());
	}
}