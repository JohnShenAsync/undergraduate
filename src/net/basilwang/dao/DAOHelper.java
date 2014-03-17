package net.basilwang.dao;

import android.content.Context;
import android.database.Cursor;

public class DAOHelper {
	private TASQLiteOpenHelper openHelper;
	private Cursor result;

	public DAOHelper(Context context) {
		openHelper = new TASQLiteOpenHelper(context);
	}

	public void insert(String sql, Object bindArgs[]) {
		exeSQLWithObjectArgs(sql, bindArgs);
	}

	public void update(String sql, Object bindArgs[]) {
		exeSQLWithObjectArgs(sql, bindArgs);
	}

	public void delete(String sql) {
		this.openHelper.getWritableDatabase().execSQL(sql);
		this.openHelper.close();
	}

	public void delete(String sql, Object[] bindArgs) {
		this.openHelper.getWritableDatabase().execSQL(sql, bindArgs);
		this.openHelper.close();
	}

	public void deleteListByAccountId(String sql, int accountId) {
		Object[] bindArgs = { accountId };
		exeSQLWithObjectArgs(sql, bindArgs);
	}
	
	public Cursor query(String sql, String[] bindArgs) {
		result = this.openHelper.getWritableDatabase().rawQuery(sql, bindArgs);
		return result;
	}

	public void closeDB() {
		if (result != null) {
			result.close();
		}

		if (openHelper.getWritableDatabase().isOpen()) {
			this.openHelper.getWritableDatabase().close();
		}
	}

	private void exeSQLWithObjectArgs(String sql, Object[] bindArgs) {
		this.openHelper.getWritableDatabase().execSQL(sql, bindArgs);
		this.openHelper.getWritableDatabase().close();
	}
}
