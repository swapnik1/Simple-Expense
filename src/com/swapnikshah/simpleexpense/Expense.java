/**
 * 
 */
package com.swapnikshah.simpleexpense;

import java.sql.Date;

/**
 * @author SWAPNIK
 *
 */
public class Expense {
	public String name, details, method;
	public Date d;
	public double amt;
	public long id;
	public static final String CURRENCY = "$";
	public static double amtSpent=0;
	public static double amtEarned=0;
	private double balance;
	
	public Expense() {
		this.name = this.details = this.method = "";
		this.d = new Date(System.currentTimeMillis());
	}
	
	public void spend(double spending, Date d, String name, String details, String method){
		setBalance(getBalance() - spending);
		this.name = name; 
		this.details = details;
		this.method = method;
		this.d = d;
		this.amt = -spending;
	}
	
	public void credit(double income, Date d, String name, String details, String method){
		setBalance(getBalance() + income);
		this.name = name;
		this.details = details;
		this.method = method;
		this.d = d;
		this.amt = income;
	}
	
	/*private void toString() {
		Log.d("EXPCLASS", "name :"+this.name);
		Log.d("EXPCLASS", "date :"+this.d.toString());
		Log.d("EXPCLASS", "details :"+this.details);
		Log.d("EXPCLASS", "method :"+this.method);
		Log.d("EXPCLASS", "balance :"+this.balance);
	}*/

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return this.balance;
	}
	
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
}