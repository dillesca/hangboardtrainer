package com.gxb.iwill.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gxb.iwill.model.Alert;
import com.gxb.iwill.model.Goal;
import com.gxb.iwill.model.History;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

	private static String DATABASE_NAME="goals.db";
	private static final int DATABASE_VERSION = 1;
	private Context c;
	
	private static final String tbl_goal="tbl_goal";
	private static final String tbl_alert="tbl_alert";
	private static final String tbl_history="tb_history";
	
	
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c=context;	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//goal table which stores goals the user has created
		db.execSQL("create table " +tbl_goal +
				   "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "description VARCHAR(100) NOT NULL, " +
				   "type CHAR(4))");
		//Alert table which assigns a date to the goals
		db.execSQL("create table "+ tbl_alert +
				   "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "id_goal INTEGER FOREIGN KEY, " +
				   "created_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                   "repeat_value INTEGER DEFAULT 0 " +
                   "text_color CHAR(6) NULL DEFAULT '000000', " +
				   "FOREIGN KEY(id_goal) REFERENCES tbl_goal(id) " +
                    ")");
		//History Table - which saves the alerts the user has accomplished
		db.execSQL("create table " + tbl_history + 
				   "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "goal_description VARCHAR(100), " +
				   "date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                   "status INTEGER DEFAULT 0" +
                    ")");

	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

	/**
	 * Opens and gets data directly from the database
	 * @param table The name of the table to get data from. Use Tables enum to get valid
	 * table names. 
	 * @param columns The columns to select from
	 * @param selection A string representing the SQLite where clause for the query
	 * @param order The column name to order the query by
	 *        <p>Example order = "exerciseNum ASC"
	 * @return A Cursor pointing to the beginning of the data returned by the query.
	 */
	private Cursor getdata(String table, String[] columns, String selection, String order){
		SQLiteDatabase database = this.getWritableDatabase();
		return database.query(table, columns, selection, null, null, null, order);
	}
	
	/**
	 * Inserts a row into the given table with the given ContentValues. 
	 * @param table The name of the table to insert a row into. Use Tables enum to get valid
	 * table names. 
	 * @param values The ContentValues to add to the given table
	 * @return true if succeeded, false otherwise. 
	 */
	public long insertRow(String table, ContentValues values) {
		SQLiteDatabase database = this.getWritableDatabase();
		long check = database.insert(table, null, values);
		this.close();
		return check;
	}
	
	/**
	 * Updates the database with the values of the query
	 * @param table The name of the table to update. Use Tables enum to get valid table names.
	 * @param values The content values for the update.
	 * @param whereClause The whereclause for the update
	 * @return true if update succeeded, false otherwise.
	 */
	protected boolean updateDB(String table, ContentValues values, String whereClause) {
		SQLiteDatabase database = this.getWritableDatabase();
		long check = database.update(table, values, whereClause, null);
		this.close();
		return (check != -1);
	}
	
	/**
	 * Updates the budget in the database with the values of the passed-in Budget. 
	 * @param goalId the primary id of the goal.
	 * @return @true if succeeded, @false otherwise. 
	 */
	public boolean updateGoal(long goalId, String description, String type){
		ContentValues cv = new ContentValues();
		cv.put("id", goalId);
		cv.put("description", description);
		cv.put("type", type);
		 
		this.close();
		return updateDB(tbl_goal, cv, "id="+ goalId);
	}

    /**
     *
     * @param goalId key for tbl_goal entry
     * @return Goal
     */
    public Goal getGoal(long goalId){
        Cursor cursor= getdata(tbl_goal,null,"id="+goalId,null);
        if(cursor.isClosed()) return null;
        cursor.moveToFirst();
        Goal goal = null;

        if(cursor.isFirst()){
            int indexDescription = cursor.getColumnIndex("description");
            int indexType = cursor.getColumnIndex("type");

            goal=new Goal();
            goal.setId(goalId);
            goal.setDescription(cursor.getString(indexDescription));
            goal.setType(cursor.getString(indexType));

        }
        cursor.close();
        this.close();
        return goal;
    }


    /**
	 * Searches through all the alerts for a certain day.
     * @param   where date you want to search
     *          <p>eg "created_date=somesearchdate"
 	 * @return ArrayList<Alert>
	 */
	public ArrayList<Alert> getAlerts(String where){
		Cursor cursor= getdata(tbl_alert,null,where,null);
		if(cursor.isClosed()) return null;
		cursor.moveToFirst();
		ArrayList<Alert> alerts = new ArrayList<>();
		
		while(!cursor.isAfterLast()){

            int indexId = cursor.getColumnIndex("id");
            int indexGoalId = cursor.getColumnIndex("id_goal");
            int indexCreatedDate = cursor.getColumnIndex("created_date");
            int indexRepeatValue = cursor.getColumnIndex("repeat_value");
            int indexTextColor = cursor.getColumnIndex("text_color");

			
			Alert alert = new Alert();
			alert.setId(cursor.getLong(indexId));
			alert.setGoalId(cursor.getLong(indexGoalId));
			alert.setCreatedDate(cursor.getString(indexCreatedDate));
			alert.setRepeatValue(cursor.getInt(indexRepeatValue));
			alert.setTextColor(cursor.getString(indexTextColor));

			alerts.add(alert);
				
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		return alerts;
	}


    /**
     * Searches through the history for a certain day.
     * @param   where date you want to search
     *          <p>eg "date=somesearchdate"
     * @return ArrayList<History>
     */
    public ArrayList<History> getHistory(String where){
        Cursor cursor= getdata(tbl_history,null,where,null);
        if(cursor.isClosed()) return null;
        cursor.moveToFirst();
        ArrayList<History> historys = new ArrayList<>();

        while(!cursor.isAfterLast()){

            int indexId = cursor.getColumnIndex("id");
            int indexGoalDescription = cursor.getColumnIndex("goal_description");
            int indexDate = cursor.getColumnIndex("date");
            int indexStatus = cursor.getColumnIndex("status");

            History history = new History();
            history.setId(cursor.getLong(indexId));
            history.setGoalDescription(cursor.getString(indexGoalDescription));
            history.setDate(cursor.getString(indexDate));
            history.setStatus(cursor.getInt(indexStatus));

            historys.add(history);

            cursor.moveToNext();
        }
        cursor.close();
        this.close();
        return historys;
    }
	
	/**
	 * Adds a new goal to the database
	 * @param description What is the goal
	 * @param type Year or Day
	 * @return @true if succeeded, @false otherwise
	 */
	public long insertGoal(String description, String type){
		
		ContentValues cv = new ContentValues();
		cv.put("description", description);
		cv.put("type", type);
		
		return insertRow(tbl_goal, cv);
	}

    /**
     * Adds a new alert to the database
     * @param goalId foreign key to tbl_goal
     * @param createdDate //TODO figure out format needed for DATETIME
     * @param repeatValue 0 or 1
     * @param textColor hex value
     * @return @true if succeeded, @false otherwise
     */
    public long insertAlert(int goalId, String createdDate, int repeatValue, String textColor){

        ContentValues cv = new ContentValues();
        cv.put("id_goal", goalId);
        cv.put("created_date", createdDate);
        cv.put("repeat_value", repeatValue);
        cv.put("text_color", textColor);

        return insertRow(tbl_goal, cv);
    }

    /**
     * Adds a new goal to the database
     * @param goalDescription goalsDescription
     * @param date date the goal should have been accomplished
     * @param status if use succeeded or not
     * @return @true if succeeded, @false otherwise
     */
    public long insertHistory(String goalDescription, String date, int status){

        ContentValues cv = new ContentValues();
        cv.put("goal_description", goalDescription);
        cv.put("status", status);

        return insertRow(tbl_goal, cv);
    }

    public boolean updateGoal(Goal goal) {
        ContentValues cv = new ContentValues();
        cv.put("goal_description", goal.getDescription());
        cv.put("type", goal.getType());

        return updateDB(tbl_goal, cv, "id="+ goal.getId());
    }

	public boolean updateAlert(Alert alert) {
		ContentValues cv = new ContentValues();
		cv.put("id_goal", alert.getGoalId());
		cv.put("created_date", alert.getCreatedDate());
		cv.put("repeat_value", alert.getRepeat());
		cv.put("text_color", alert.getTextColor());
		
		return updateDB(tbl_alert, cv, "id="+ alert.getId());
	}

    public boolean updateHistory(History history) {
        ContentValues cv = new ContentValues();
        cv.put("goal_description", history.getGoalDescription());
        cv.put("date", history.getDate());
        cv.put("status", history.getStatus());

        return updateDB(tbl_history, cv, "id="+ history.getId());
    }
	
	public void deleteGoal(long id ) {
		SQLiteDatabase db = this.getWritableDatabase();
        String query="DELETE FROM " + tbl_goal + " WHERE " +
        			 "id = " + id;
        db.execSQL(query);
        db.close();
	}
    public void deleteAlert(long id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query="DELETE FROM " + tbl_alert + " WHERE " +
                "id = " + id;
        db.execSQL(query);
        db.close();
    }
    public void deleteHistory(long id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query="DELETE FROM " + tbl_history + " WHERE " +
                "id = " + id;
        db.execSQL(query);
        db.close();
    }
}
