package com.dragdrop.danielillescas.exercisedraganddrop.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dragdrop.danielillescas.exercisedraganddrop.R;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Exercise;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Workout;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper{

	private static String DATABASE_NAME="hanbgoard.db";
	private static final int DATABASE_VERSION = 1;
	private Context c;
	
	private static final String tbl_workout="tbl_workout";
	private static final String tbl_exercise="tbl_exercise";
	private static final String tbl_user="tbl_user";
	private static final String tbl_history="tb_history";
	
	
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c=context;	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Workout Table set the owner == "Hangboard_Trainer" for originals
		db.execSQL("create table " +tbl_workout +
				   "(workoutId INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   "owner CHAR(20) NOT NULL, " +
				   "name CHAR(30), " +
				   "description VARCHAR(100), " +
				   "hangboardType CHAR(30), " +
				   "difficulty VARCHAR(20), " +
				   "rating FLOAT, " +
				   "dateAdded DATETIME DEFAULT CURRENT_TIMESTAMP," +
				   "isPublished INTEGER DEFAULT 0," + 
				   "isDownloaded INTEGER DEFAULT 0," +
				   "serverId INTEGER DEFAULT -1)");
		//User Table
		db.execSQL("create table "+ tbl_user +
				   "(username CHAR(20) UNIQUE, " +
				   "password CHAR(20), " +
				   "name VARCHAR(50), " +
				   "harderstClimb CHAR(10), " +
				   "rating FLOAT, " +
				   "email VARCHAR(20), " +
  				   "PRIMARY KEY (username))");
		//Exercise Table
		db.execSQL("create table " + tbl_exercise +
				   "(workoutId INTEGER, "+
				   "exerciseNum INTEGER, " +
				   "type VARCHAR(25), " +
				   "description VARCHAR(250), "+
				   "time INTEGER DEFAULT 0, " +
				   "amount INTEGER DEFAULT 0, " + 
				   "leftHold VARCHAR(30), " + 
				   "rightHold VARCHAR(30), " + 
				   "sound VARCHAR(25), " +
				   "FOREIGN KEY (workoutId) REFERENCES tbl_workout(workoutId))");
		//History Table
		db.execSQL("create table " + tbl_history + 
				   "(workoutId INTEGER, " +
				   "worktoutName VARCHAR(30), " +
				   "dateCompleted VARCHAR(10))");
		
		//TRIGGERS
		db.execSQL("CREATE TRIGGER insert_exercise AFTER INSERT ON tbl_workout" +
				   "  WHEN new.owner = 'You' " +
				   "  BEGIN" +
				   "    INSERT INTO tbl_exercise(workoutId,exerciseNum,type,time,leftHold,rightHold)" +
				   "       VALUES(new.workoutID,0,'Hang',30,'Jug','Jug');" + 
				   "  END");
		
		String[] names = c.getResources().getStringArray(R.array.workout_names);
		String[] descriptions = c.getResources().getStringArray(R.array.descriptions);
		String[] difficulties = c.getResources().getStringArray(R.array.database_wo_difficulties);
		
		for(int i=0; i<names.length; i++) {
			db.execSQL("INSERT INTO tbl_workout (owner,name,description,difficulty,isPublished,serverId) " +
					"values('Hangboard_Trainer'," +
					"'" + names[i] +"'," +
					"'" + descriptions[i] + "'," + 
					"'" + difficulties[i] + "'," +
					"1,-1)");
					
		}
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
		Cursor data = database.query(table, columns, selection, null, null, null, order);
		return data;
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

	public boolean updateWorkout(long workoutId, String name, String description, String difficulty,
			String user, int isPublished, int isDownloaded, long l){
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("description", description);
		cv.put("difficulty", difficulty);
		cv.put("owner", user);
		cv.put("isPublished", isPublished);
		cv.put("isDownloaded", isDownloaded);
		cv.put("owner", user);
		cv.put("serverId", l);
		 
		this.close();
		return updateDB(tbl_workout, cv, "workoutId="+ workoutId);
	}
	
	/**
	 * Searches through all the local workouts on the phone.
	 * @return ArrayList<Workout>
	 */
	public ArrayList<Workout> getWorkouts(String where){
		Cursor cursor= getdata(tbl_workout,null,where,null);
		if(cursor.isClosed()) return null;
		cursor.moveToFirst();
		ArrayList<Workout> workouts = new ArrayList<Workout>();
		
		while(!cursor.isAfterLast()){
			int indexDownloaded = cursor.getColumnIndex("isDownloaded");
			int indexPublished = cursor.getColumnIndex("isPublished");
			
			Workout w = new Workout();
			w.setId(cursor.getInt(0));
			w.setOwner(cursor.getString(1));
			w.setName(cursor.getString(2));
			w.setDescription(cursor.getString(3));
			w.setHangboardType(cursor.getString(4));
			w.setDifficulty(cursor.getString(5));
			w.setRating(cursor.getFloat(6));
			w.setDateAdded(cursor.getString(7));
			w.setIsPublished(cursor.getInt(indexPublished));
			w.setIsDownloaded(cursor.getInt(indexDownloaded));
			w.setServerId(cursor.getInt(cursor.getColumnIndex("serverId")));

			workouts.add(w);
				
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		return workouts;
	}
	
	public Workout getWorkout(long workoutId){
		Cursor cursor= getdata(tbl_workout,null,"workoutId="+workoutId,null);
		if(cursor.isClosed()) return null;
		cursor.moveToFirst();
		Workout w = null;
		
		if(cursor.isFirst()){
			int isDownloaded = cursor.getColumnIndex("isDownloaded");
			int isPublished = cursor.getColumnIndex("isPublished");

			w=new Workout();
			w.setId(cursor.getInt(0));
			w.setOwner(cursor.getString(1));
			w.setName(cursor.getString(2));
			w.setDescription(cursor.getString(3));
			w.setHangboardType(cursor.getString(4));
			w.setDifficulty(cursor.getString(5));
			w.setRating(cursor.getFloat(6));
			w.setDateAdded(cursor.getString(7));
			w.setIsPublished(cursor.getInt(isPublished));
			w.setIsDownloaded(cursor.getInt(isDownloaded));
			w.setServerId(cursor.getInt(cursor.getColumnIndex("serverId")));
		} 
		cursor.close();
		this.close();
		return w;
	}
	
	
	/**
	 * Gets the username of the account created. 
	 * @return username
	 *         null if account does not exist
	 */
	public String getAccountName(){
		Cursor cursor= getdata(tbl_user,null,null,null);
		if(cursor.isClosed()) return null;
		cursor.moveToFirst();
		String name=null;
		
		if(!cursor.isAfterLast()){
			name=cursor.getString(0);
		}
		cursor.close();
		this.close();
		return name;
	}
	
	/**
	 * Adds a new Workout to the database
	 * @param name 
	 * @param description
	 * @param difficulty
	 * @return @true if succeeded, @false otherwise
	 */
	public long insertWorkout(String name, String description, String difficulty,
			String user, int isPublished, int isDownloaded,long serverId){
		
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("description", description);
		cv.put("difficulty", difficulty);
		cv.put("owner", user);
		cv.put("isPublished", isPublished);
		cv.put("isDownloaded", isDownloaded);
		cv.put("serverId", serverId);
		if(user != null) cv.put("owner", user);
		else cv.put("owner", "You");
		
		return insertRow(tbl_workout, cv);	
	}
	
	/**
	 * Checks if the workoutId is in the list of shared Id's
	 * @param id
	 */
	public boolean workoutAlreadyDownloaded(long id) {
		Cursor cursor= getdata(tbl_workout,null,"serverId="+id+"",null);
		if(cursor.isClosed()) return false;
		cursor.moveToFirst();
		cursor.close();
		return !cursor.isAfterLast();
	}


	
	/**
	 * Adds a new User to the database
	 * @param uname username
	 * @param name First Last
	 * @param password
	 * @return @true if succeeded, @false otherwise
	 */
	public long addUser(String uname, String name, String password){
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt(11));

		ContentValues cv = new ContentValues();
		cv.put("username", uname);
		cv.put("name", name);
		cv.put("password", hashed);
		return insertRow(tbl_user, cv);	
	}
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}


	public boolean updateExercise(Exercise e) {
		ContentValues cv = new ContentValues();
		cv.put("workoutId", e.getWorkoutId());
		cv.put("exerciseNum", e.getExerciseNumber());
		cv.put("type", e.getExerciseType());
		cv.put("time", e.getTime());
		cv.put("amount", e.getAmount());
		cv.put("leftHold", e.getLeftHand());
		cv.put("rightHold", e.getRightHand());
		
		return updateDB(tbl_exercise, cv, "workoutId="+ e.getWorkoutId()+" and exerciseNum="+e.getExerciseNumber());
	}

	public ArrayList<Exercise> getExercises(long workoutId) {
		return getExercisesFrom(workoutId, 0);
	}	
	
	public ArrayList<Exercise> getExercisesFrom(long workoutId, int from) {
		Cursor cursor= getdata(tbl_exercise,null,
				"workoutId=" + workoutId + " and exerciseNum > " + (from-1),
				"exerciseNum ASC");
		if(cursor.isClosed()) return null;
		cursor.moveToFirst();
		ArrayList<Exercise> exercises = new ArrayList<Exercise>();
		
		while(!cursor.isAfterLast()){
			int indexWorkoutId = cursor.getColumnIndex("workoutId");
			int indexExerciseNum = cursor.getColumnIndex("exerciseNum");
			int indexType = cursor.getColumnIndex("type");
			int indexTime = cursor.getColumnIndex("time");
			int indexAmount = cursor.getColumnIndex("amount");
			int leftIndex = cursor.getColumnIndex("leftHold");
			int rightIndex = cursor.getColumnIndex("rightHold");
			
			Exercise e = new Exercise();
			e.setWorkoutId(cursor.getLong(indexWorkoutId));
			e.setExerciseNumber(cursor.getInt(indexExerciseNum));
			e.setExerciseType(cursor.getString(indexType));
			
			e.setTime(cursor.getInt(indexTime));
			e.setAmount(cursor.getInt(indexAmount));


			if(!cursor.isNull(leftIndex)) e.setLeftHand(cursor.getString(leftIndex));
			if(!cursor.isNull(rightIndex)) e.setRightHand(cursor.getString(rightIndex));

			exercises.add(e);				
			cursor.moveToNext();
		}
		cursor.close();
		this.close();
		return exercises;
	}	
	
	public Exercise getExercise(long workoutId, int exerciseNum) {
		Cursor cursor= getdata(tbl_exercise,null,
				"workoutId="+workoutId+" AND exerciseNum = "+exerciseNum,null);
		if(cursor.isClosed()) return null;
		if(!cursor.moveToFirst()) return null;
		if(cursor.isAfterLast()) return null;
		
		int indexWorkoutId = cursor.getColumnIndex("workoutId");
		int indexExerciseNum = cursor.getColumnIndex("exerciseNum");
		int indexType = cursor.getColumnIndex("type");
		int indexTime = cursor.getColumnIndex("time");
		int indexAmount = cursor.getColumnIndex("amount");
		int leftIndex = cursor.getColumnIndex("leftHold");
		int rightIndex = cursor.getColumnIndex("rightHold");
		
		Exercise e = new Exercise();
		e.setWorkoutId(cursor.getLong(indexWorkoutId));
		e.setExerciseNumber(cursor.getInt(indexExerciseNum));
		e.setExerciseType(cursor.getString(indexType));
		
		e.setTime(cursor.getInt(indexTime));
		e.setAmount(cursor.getInt(indexAmount));


		if(!cursor.isNull(leftIndex)) e.setLeftHand(cursor.getString(leftIndex));
		if(!cursor.isNull(rightIndex)) e.setRightHand(cursor.getString(rightIndex));

		cursor.close();
		this.close();
		return e;
	}

	public long insertExercise(Exercise e) {	
		ContentValues cv = new ContentValues();
		cv.put("workoutId", e.getWorkoutId());
		cv.put("exerciseNum", getExercises(e.getWorkoutId()).size());
		cv.put("type", e.getExerciseType());
		cv.put("time", e.getTime());
		cv.put("amount", e.getAmount());
		cv.put("leftHold", e.getLeftHand());
		cv.put("rightHold", e.getRightHand());

		return insertRow(tbl_exercise, cv);	
		
	}

	public void swapExerciseNumbers(long workoutId, int index1, int index2) {
		SQLiteDatabase db = this.getWritableDatabase();;                  
        String query1="UPDATE " + tbl_exercise +
        		     " SET exerciseNum = -1" +
        		     " WHERE exerciseNum = " + index1 +
        		     " AND workoutId = " + workoutId;
        String query2="UPDATE " + tbl_exercise +
      		     " SET exerciseNum = " + index1 +
      		     " WHERE exerciseNum = " + index2 +
      		     " AND workoutId = " + workoutId;
        String query3="UPDATE " + tbl_exercise +
   		     " SET exerciseNum = " + index2 +
   		     " WHERE exerciseNum = - 1" +
   		     " AND workoutId = " + workoutId;
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.close();
	}

	/**
	 * Delete all the exercises and workouts from the database
	 * @param workouts
	 */
	public void deleteWorkouts(ArrayList<Workout> workouts){
		SQLiteDatabase db = this.getWritableDatabase();;                  
		int size = workouts.size();
        String query1="DELETE FROM " + tbl_exercise + " WHERE ";
        String query2="DELETE FROM " + tbl_workout + " WHERE ";
        String idsToDelete = "";
        for(int i=0;i<size;i++){
        	idsToDelete += "workoutId = " + workouts.get(i).getId();
        	if(i != size-1) idsToDelete += " OR ";
        }

        db.execSQL(query1+idsToDelete);
        db.execSQL(query2+idsToDelete);
        db.close();
	}
	
	public void deleteExercise(long workoutId,int exerciseNum ) {
		SQLiteDatabase db = this.getWritableDatabase();;                  
        String query="DELETE FROM " + tbl_exercise + " WHERE " +
        			 "workoutId = " + workoutId + " AND " + 
        			 "exerciseNum = " + exerciseNum;
        String updateQuery=
        	 "UPDATE " + tbl_exercise +
   		     " SET exerciseNum = exerciseNum - 1" +
   		     " WHERE exerciseNum > " + exerciseNum +
        	 "  AND  workoutId = " + workoutId;

        
        db.execSQL(query);
        db.execSQL(updateQuery);
        db.close();
	}

	public void updateExerciseNumber(long workoutId, int old, int newPosition) {
		SQLiteDatabase db = this.getWritableDatabase();;                  
 
		String updateQuery=
    		  "UPDATE " + tbl_exercise +
		     " SET exerciseNum = " + newPosition +
		     " WHERE exerciseNum = " + old;
		 db.execSQL(updateQuery);
		 db.close();
	}


}
