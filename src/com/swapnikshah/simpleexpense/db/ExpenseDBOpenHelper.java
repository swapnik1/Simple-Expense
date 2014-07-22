package com.swapnikshah.simpleexpense.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDBOpenHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	protected static final String DB_NAME = "SimpleExpense";
	protected static final String TABLE_NAME = "expenses";
	public static final String KEY_ID = "id";
	public static final String KEY_AMT = "amount";
	public static final String KEY_NAME = "name";
	public static final String KEY_DATE = "date";
	public static final String KEY_DETAILS = "details";
	public static final String KEY_METHOD = "method";
	private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+
			"("+KEY_ID+" INT(11) PRIMARY KEY, "+
			KEY_NAME+" VARCHAR(255), "+
			KEY_AMT+" DOUBLE(11), "+
			KEY_DATE+" DATE, "+
			KEY_DETAILS+" VARCHAR(255), "+
			KEY_METHOD+" VARCHAR(10)); "; 
	
	public ExpenseDBOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override 
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);

	}

}
