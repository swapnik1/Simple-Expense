package com.swapnikshah.simpleexpense.db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swapnikshah.simpleexpense.Expense;

public class ExpenseDataSource {
	
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase db;
	/*public static final int CI_ID = 0;
	public static final int CI_NAME = 1;
	public static final int CI_AMT = 2;
	public static final int CI_DATE = 3;
	public static final int CI_DETAILS = 4;
	public static final int CI_METHOD = 5;*/
	
	public ExpenseDataSource(Context context){
		dbhelper = new ExpenseDBOpenHelper(context);
		db = dbhelper.getWritableDatabase();
	}
	
	public void open(){
		Log.d("ExpDataSource","DB Opened");
		db = dbhelper.getWritableDatabase();
	}
	
	public void close(){
		Log.d("ExpDataSource","DB Closed");
		dbhelper.close();
	}
	
	public List<String> getMercNames(){
		List<String> names = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT DISTINCT "+ExpenseDBOpenHelper.KEY_NAME+" FROM "+ExpenseDBOpenHelper.TABLE_NAME,null);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				if(!names.contains(c.getString(0))){
					names.add(c.getString(0));
				}
			}
		}
		//c.close();
		if(names.isEmpty()){
			names.add("");
		}
		return names;
	}
	
	public Expense addEntry(Expense exp){
		ContentValues DBEntry = new ContentValues();
		DBEntry.put(ExpenseDBOpenHelper.KEY_NAME, exp.name);
		DBEntry.put(ExpenseDBOpenHelper.KEY_AMT, exp.amt);
		DBEntry.put(ExpenseDBOpenHelper.KEY_DATE, exp.d.toString());
		if(!exp.details.isEmpty()){
			DBEntry.put(ExpenseDBOpenHelper.KEY_DETAILS, exp.details);
		}
		DBEntry.put(ExpenseDBOpenHelper.KEY_METHOD, exp.method);
		Log.d("EXPCLASS", "Amount :"+exp.amt);
		exp.id = db.insert(ExpenseDBOpenHelper.TABLE_NAME, null, DBEntry);
		return exp;
	}
	
	public List<Expense> getExpenses(){
		List<Expense> exps = new ArrayList<Expense>();
		String sql = "SELECT * FROM "+ExpenseDBOpenHelper.TABLE_NAME;
		Cursor c = db.rawQuery(sql , null);
		Log.d("QUERY",sql);
		Log.d("QUERY","No. of Rows :"+Integer.toString(c.getCount()));
		if(c.getCount() > 0){
			// Reset Total Spent and Earned
			Expense.amtSpent = 0;
			Expense.amtEarned = 0;
			while(c.moveToNext()){
				Expense exp = new Expense();
				Log.d("QUERY","ColID "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_ID)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_ID)));
				Log.d("QUERY","ColName "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_NAME)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_NAME)));
				Log.d("QUERY","ColDate "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_DATE)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DATE)));
				Log.d("QUERY","ColAmt "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_AMT)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_AMT)));
				Log.d("QUERY","ColDetails "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_DETAILS)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DETAILS)));
				Log.d("QUERY","ColMethod "+c.getColumnIndexOrThrow(ExpenseDBOpenHelper.KEY_METHOD)+" :"+c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_METHOD)));
				exp.id = c.getLong(c.getColumnIndex(ExpenseDBOpenHelper.KEY_ID));
				exp.name = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_NAME));
				exp.amt = c.getDouble(c.getColumnIndex(ExpenseDBOpenHelper.KEY_AMT));
				exp.d = Date.valueOf(c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DATE)));
				exp.details = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DETAILS));
				exp.method = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_METHOD));
				if(exp.amt>0){
					Expense.amtEarned+=exp.amt;
				}else{
					Expense.amtSpent+=(exp.amt*(-1));
				}
				exps.add(exp);
			}
		}
		Log.d("QUERY","No. of ExpElements :"+exps.size());
		return exps;
	}
	
	public List<Expense> getExpenses(Date start, Date end){
		List<Expense> exps = new ArrayList<Expense>();
		// Might take in dates as the argument
		if(start==null){
			start=new Date(1);
		}
		if(end==null){
			end = new Date(Long.MAX_VALUE);
		}
		String sql = "SELECT * FROM `"+ExpenseDBOpenHelper.TABLE_NAME+"` WHERE `"+ExpenseDBOpenHelper.KEY_DATE+"`>=\""+start+"\" AND `"+ExpenseDBOpenHelper.KEY_DATE+"`< \""+end+"\" ";
		Cursor c = db.rawQuery(sql, null);
		Log.d("QUERY",sql);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				Expense exp = new Expense();
				exp.id = c.getLong(c.getColumnIndex(ExpenseDBOpenHelper.KEY_ID));
				exp.name = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_NAME));
				exp.amt = c.getDouble(c.getColumnIndex(ExpenseDBOpenHelper.KEY_AMT));
				exp.d = Date.valueOf(c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DATE)));
				exp.details = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_DETAILS));
				exp.method = c.getString(c.getColumnIndex(ExpenseDBOpenHelper.KEY_METHOD));
				exps.add(exp);
			}
		}
		return exps;
	}
	
	public List<Expense> getExpenses(int month,int year){
		year-=1900;
		if((month<13) && (month>0)){
			Date start = new Date(year, month, 1);
			Date end;
			if(month==12){
				end = new Date(year+1,1,1);
			}else{
				end = new Date(year, month+1, 1);
			}
			return this.getExpenses(start, end);
		}
		return null;
	}
	
	public List<Expense> getSpendings(){
		List<Expense> exps = new ArrayList<Expense>();
		Cursor c = db.rawQuery("SELECT * FROM "+ExpenseDBOpenHelper.TABLE_NAME+" WHERE "+ExpenseDBOpenHelper.KEY_AMT+">=0", null);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				Expense exp = new Expense();
				exp.id = c.getLong(0);
				exp.name = c.getString(1);
				exp.amt = c.getDouble(2);
				exp.d = Date.valueOf(c.getString(3));
				exp.details = c.getString(4);
				exp.method = c.getString(5);
				exps.add(exp);
			}
		}
		return exps;
	}
	
	public List<Expense> getCredits(){
		List<Expense> exps = new ArrayList<Expense>();
		Cursor c = db.rawQuery("SELECT * FROM "+ExpenseDBOpenHelper.TABLE_NAME+" WHERE "+ExpenseDBOpenHelper.KEY_AMT+"<0", null);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				Expense exp = new Expense();
				exp.id = c.getLong(0);
				exp.name = c.getString(1);
				exp.amt = c.getDouble(2);
				exp.d = Date.valueOf(c.getString(3));
				exp.details = c.getString(4);
				exp.method = c.getString(5);
				exps.add(exp);
			}
		}
		return exps;
	}
	
	/**
	 * Gets all the expenses from the database and organizes it into monthly expenses
	 * @return Dictionary containing Months as keys and Expense lists as values
	 */
	public Dictionary<Date,List<Expense>> getMonthlyExpenses() {
		List<Expense> AllExps = this.getExpenses();
		List<Expense> Exps = new ArrayList<Expense>();
		Dictionary<Date,List<Expense>> MonthExps = new Hashtable<Date, List<Expense>>();
		Date prevD = new Date(0);
		Date crD = new Date(0);
		for(int i=0; i < AllExps.size(); i++){
			Expense exp = AllExps.get(i);
			crD = exp.d;
			Exps.add(exp);
			if((prevD.getMonth()!=crD.getMonth()) || (prevD.getYear()!=crD.getYear())){
				// The first expense of the new month so create a new key in the Dict to add expenses of the old month 
				MonthExps.put(crD, Exps);
				Log.d("getMEx","crD for the main Dict :"+crD.toString());
				Log.d("getMEx","No. of Exp in "+crD.toString()+"="+Exps.size());
				Exps = new ArrayList<Expense>();
			}
			prevD = (Date) crD.clone();
		}
		return MonthExps;
	}
	
	public void deleteExpense(long id){
		db.execSQL("DELETE FROM `"+ExpenseDBOpenHelper.TABLE_NAME+"` WHERE id="+id);
	}
	
	public void deleteAll(){
		db.execSQL("DELETE FROM `"+ExpenseDBOpenHelper.TABLE_NAME+"` WHERE 1=1");
		// Update Balance
	}
}
