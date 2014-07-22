package com.swapnikshah.simpleexpense;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MonthListAdapter extends BaseExpandableListAdapter{
	Context context;
	private Dictionary<Date, List<Expense>> MonthExps;
	private List<String[]> Groups;	// Will have (Month, Year) and Amount
	private List<List<Expense>> Children;	// Will have Name, Date and Amount. In future change to Expense objects
	private String months[] = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};
	
	public MonthListAdapter(Context context, int resource,
	int textViewResourceId, Dictionary<Date,List<Expense>> MonthExps) {
		super();
		this.context = context;
		this.MonthExps = MonthExps;
		this.Groups = new ArrayList<String[]>();
		this.Children = new ArrayList<List<Expense>>();
		// Initialize Groups and Children
		Enumeration<Date> itr = this.MonthExps.keys();
		while(itr.hasMoreElements()){
			double monthAmt = 0;
			Date crD = itr.nextElement();
			List<Expense> crExps = this.MonthExps.get(crD);	// Get List of Expenses for the current month
			Log.d("MListAdp","crD for the main Dict :"+crD.toString());
			Log.d("MListAdp","No. of Exp in "+crD.toString()+"="+crExps.size());
			List<Expense> childExps = new ArrayList<Expense>();	// Variable to convert the Expenses to list of Strings 
			for(int i=0; i < crExps.size(); i++){
				Expense crExp = crExps.get(i);
				monthAmt += crExp.amt;
				childExps.add(crExp);
			}
			this.Children.add(childExps);
			String[] grpElement = {months[crD.getMonth()]+", "+Integer.toString(crD.getYear()),Double.toString(monthAmt)};
			Log.d("MListAdp","Year from db : "+Integer.toString(crD.getYear()));
			this.Groups.add(grpElement);
		}
		
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {		
		return null;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View ExpView = inflater.inflate(R.layout.expenses_list, null);
		
		// Create TextViews
		TextView eName = (TextView) ExpView.findViewById(R.id.exp_name);
		TextView eDate = (TextView) ExpView.findViewById(R.id.exp_date);
		TextView eAmt = (TextView) ExpView.findViewById(R.id.exp_amt);
		TextView eDetails = (TextView) ExpView.findViewById(R.id.exp_details);
		TextView eMethod = (TextView) ExpView.findViewById(R.id.exp_method);
		
		// Set texts
		eName.setText(this.Children.get(groupPosition).get(childPosition).name);
		eDate.setText(this.Children.get(groupPosition).get(childPosition).d.toString());
		eAmt.setText(Double.toString(this.Children.get(groupPosition).get(childPosition).amt));
		eDetails.setText(this.Children.get(groupPosition).get(childPosition).details);
		eMethod.setText("Method : "+this.Children.get(groupPosition).get(childPosition).method);
		return ExpView;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		Log.d("MNTHLIST","Child Count for grppos :"+groupPosition+" = "+this.Children.get(groupPosition).size());
		return this.Children.get(groupPosition).size();
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.Groups.size();
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View monthView = inflater.inflate(R.layout.monthly_list_item, null);
		TextView mName = (TextView) monthView.findViewById(R.id.MonthName);
		TextView mAmt = (TextView) monthView.findViewById(R.id.MonthAmt);
		mName.setText(this.Groups.get(groupPosition)[0]);
		mAmt.setText(this.Groups.get(groupPosition)[1]);
		return monthView;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
}