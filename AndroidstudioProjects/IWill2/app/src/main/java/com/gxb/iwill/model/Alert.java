package com.gxb.iwill.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alert {

	
	private long id,goalId;
	private int repeat;
	private String createdDate,textColor;
	
	public Alert(long id, long goalId, String createdDate, int repeat, String textColor){
		this.id=id;
		this.goalId=goalId;
		this.createdDate=createdDate;
		this.repeat=repeat;
		this.textColor=textColor;
	}
	public Alert(){
	}

	public long getId(){
		return id;
	}
	public long getGoalId(){
		return goalId;
	}

    public String getTextColor() { return textColor; }
    /**
     * Will return 1 if the alert is to be repeated weekly
     * @return 1 if alert is to repeat next week
     */
    public int getRepeat() { return repeat; }

    public void setId(long id) { this.id=id; }
	public void setGoalId(long goalId) { this.goalId=goalId; }
	public void setCreatedDate(String date) {
		this.createdDate=date;
	}
    /**
     * Set the Hex value of the text color to display
     * @param value - 1=repeat 0=no repeat
     */
    public void setRepeatValue(int value) {
		this.repeat=value;
	}
    /**
     * Set the Hex value of the text color to display
     * @param color - String hex value
     */
	public void setTextColor(String color) { this.textColor=color; }
    /**
     * Returns the SQL DATETIME value
     * @return  the string of DATETIME
     */
    public String getCreatedDate(){ return createdDate;}
    /**
     * Use this function to display
     * @return  the formatted date time
     */
    public String getFormattedDate(){
        SimpleDateFormat f;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = sf.parse(createdDate);
            f = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return ""+f.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
