package com.gxb.iwill.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History {


	private long id;
    private int status;
	private String date,goalDescription;

    /**
     *
     * @param id long
     * @param goalDescription String
     * @param date String
     * @param status int
     */
	public History(long id, String goalDescription, String date, int status){
		this.id=id;
		this.goalDescription=goalDescription;
		this.date=date;
		this.status=status;
	}

    public History() {}
	public long getId(){
		return id;
	}
	public String getGoalDescription(){
		return goalDescription;
	}
    public String getDate() { return date; }
    public int getStatus() { return status; }

	
	public void setGoalDescription(String goalDescription) { this.goalDescription=goalDescription; }
	public void setDate(String date) {
		this.date=date;
	}
    public void setId(long id) {
        this.id=id;
    }

    /**
     * Set the Hex value of the text color to display
     * @param value - 1=accomplished goal 0=did not accomplish
     */
    public void setStatus(int value) {
		this.status=value;
	}

    /**
     * Use this function to display
     * @return  the formatted date time
     */
    public String getFormattedDate(){
        SimpleDateFormat f;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date d = sf.parse(date);
            f = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return ""+f.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
