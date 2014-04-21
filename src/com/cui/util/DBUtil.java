package com.cui.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
	private static DBUtil instance = null;
	private static SQLiteDatabase db = null;
	private static String dbPath = "/sdcard/XGPushDemoV2/xgpush.db";
	private Context mContext;

	private DBUtil(Context context) {
		this.mContext = context;
		db=SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	public static DBUtil getInstance(Context context) {
		if (instance == null) {
			instance = new DBUtil(context);
		}
		return instance;
	}


	public boolean insert(String sql,Object[] objs) {
		db.execSQL(sql, objs);
		return true;
	}

	public boolean delete(String sql,Object[] objs) {
		db.execSQL(sql, objs);
		return true;
	}

	public boolean update(String sql,Object[] objs) {
		db.execSQL(sql, objs);
		return true;
	}
	public Cursor query(String sql,String[] objs) {
		Cursor cursor=db.rawQuery(sql, objs);
		
		return cursor;
	}
	public void closeDatabase() {
		db.close();
	}
}
