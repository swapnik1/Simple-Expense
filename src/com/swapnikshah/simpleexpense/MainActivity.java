package com.swapnikshah.simpleexpense;

import java.sql.Date;
import java.util.Dictionary;
import java.util.List;

import com.swapnikshah.simpleexpense.db.ExpenseDataSource;

import android.os.Bundle;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {
	protected Expense Exp;
	private AutoCompleteTextView name;
	private DatePicker date;
	private EditText amt;
	private AutoCompleteTextView details;
	private ToggleButton togglebtn;
	private RadioGroup method;
	public String msg;
	private Button submit;
	private Button deleteAll;
	private ExpandableListView Monthlist;
	//private Spinner CurrencySelector;
	//private Button add_exp, reports;
	protected static SQLiteDatabase db;
	
	ExpenseDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		datasource = new ExpenseDataSource(this);
		initializeTabs();
		initializeProps();
		this.displayMonthlyExpenses();
	}
	
	private void initializeTabs() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabSpec addExp = tabs.newTabSpec("AddExp");
		addExp.setContent(R.id.tab1);
		addExp.setIndicator("Add Expense");
		tabs.addTab(addExp);
		TabSpec reports = tabs.newTabSpec("reports");
		reports.setContent(R.id.tab2);
		reports.setIndicator("Reports");
		tabs.addTab(reports);
		TabSpec prefrences = tabs.newTabSpec("settings");
		prefrences.setContent(R.id.tab3);
		prefrences.setIndicator("Prefrences");
		tabs.addTab(prefrences);
	}

	private void initializeProps() {
		this.Exp = new Expense();
		this.Monthlist = (ExpandableListView) findViewById(R.id.expandableExpView);
		this.name = (AutoCompleteTextView) findViewById(R.id.name_tv);
		this.date = (DatePicker) findViewById(R.id.datePicker1);
		this.amt = (EditText) findViewById(R.id.amt_tv);
		this.amt.setText("");
		this.details = (AutoCompleteTextView) findViewById(R.id.details_tv);
		this.togglebtn = (ToggleButton) findViewById(R.id.toggleButton1);
		this.togglebtn.setOnClickListener(this);
		this.togglebtn.setChecked(false);
		this.togglebtn.setText(null);
		this.togglebtn.setTextOn(null);
		this.togglebtn.setTextOff(null);
		this.method = (RadioGroup) findViewById(R.id.method_radio);
		this.msg = "";
		this.submit = (Button) findViewById(R.id.submit_btn);
		this.submit.setOnClickListener(this);
		this.deleteAll = (Button) findViewById(R.id.delete_all);
		this.deleteAll.setOnClickListener(this);
		List<String> names = datasource.getMercNames();
		ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, names);
		this.name.setThreshold(1);
		this.name.setAdapter(namesAdapter);
	}
	
	public void displayMonthlyExpenses(){
		Log.d("DISPLAYMEX","DisplayMEx called");
		Dictionary<Date,List<Expense>> MonthExps = datasource.getMonthlyExpenses();
		Log.d("DISPLAYMEX","MonthExps.size="+MonthExps.size());
		TextView AmtSpent = (TextView) findViewById(R.id.spentAmt_tv);
		TextView AmtEarned = (TextView) findViewById(R.id.earnedAmt_tv);
		AmtSpent.setText("$"+Double.toString(Expense.amtSpent));
		AmtEarned.setText("$"+Double.toString(Expense.amtEarned));
		this.Monthlist.setAdapter(new MonthListAdapter(this, R.layout.monthly_list_item, R.id.MonthName, MonthExps));
	}
	
	public void displaySettings(){
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.submit_btn){
			java.sql.Date d = new java.sql.Date((this.date.getYear()-1900),this.date.getMonth(), this.date.getDayOfMonth());
			RadioButton exp_type = (RadioButton) findViewById(this.method.getCheckedRadioButtonId());
			if(this.togglebtn.isChecked()){
				Exp.credit(Double.parseDouble(this.amt.getText().toString()),d , this.name.getText().toString(), this.details.getText().toString(), exp_type.getText().toString());
				datasource.addEntry(Exp);
			}else{
				Exp.spend(Double.parseDouble(this.amt.getText().toString()),d , this.name.getText().toString(), this.details.getText().toString(), exp_type.getText().toString());
				datasource.addEntry(Exp);
			}
			msg = "Expense successfully added. Current Balance :"+this.Exp.getBalance();
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			
			Log.d("FEILDS", "Name :"+this.name.getText().toString());
			Log.d("FEILDS", "Amt :"+Double.parseDouble(this.amt.getText().toString()));
			Log.d("FEILDS", "Date :"+d.toString());
			Log.d("FEILDS", "Detials :"+this.details.getText().toString());
			Log.d("FEILDS", "Method :"+exp_type.getText().toString());
			Log.d("FEILDS", "ToggleBtn : "+this.togglebtn.isChecked());
			this.name.setText("");
			this.amt.setText("");
			this.details.setText("");
			this.displayMonthlyExpenses();
		}else if(v.getId()==R.id.toggleButton1){
			if(this.togglebtn.isChecked()){
				this.togglebtn.setBackgroundResource(R.drawable.toggle_income);
			}else{
				this.togglebtn.setBackgroundResource(R.drawable.toggle_spending);
			}
		}else if(v.getId() == R.id.delete_all){
			Log.d("ONCLICK","Exp.deleteAll();");
			datasource.deleteAll();
			this.displayMonthlyExpenses();
		}
	}

}