package  com.dragdrop.danielillescas.exercisedraganddrop.models;

import android.content.Context;

import com.dragdrop.danielillescas.exercisedraganddrop.R;
import com.dragdrop.danielillescas.exercisedraganddrop.helper.Database;

import java.util.ArrayList;


public class Workout {
	
	private ArrayList<Exercise> exercises;
	
	private long id,downloads,serverId;
	private int isPublished,isDownloaded;
	private float rating;
	private String owner, name, dateAdded,difficulty,hangboard,description;
	private int time;
	
	public Workout(int id, float rating, String owner, String name, 
			String createdOn, String difficulty, String hangboard, 
			String description,long downloads,int isPublished,int isDownloaded){
		this.id=id;
		this.rating=rating;
		this.name=name;
		this.owner=owner;
		this.dateAdded=createdOn;
		this.difficulty=difficulty;
		this.hangboard=hangboard;
		this.description=description;
		this.isPublished=isPublished;
		this.isDownloaded=isDownloaded;
	}
	public Workout(){
		time = 0;
	}

	public long getId(){
		return id;
	}
	public long getServerId(){
		return serverId;
	}
	
	public long getDownloads(){
		return downloads;
	}
	public ArrayList<String> getChildren(){
		ArrayList<String> children=new ArrayList<String>();
		children.add(dateAdded);
		children.add(difficulty);
		children.add(hangboard);
		children.add(description);
		return children;
	}
	
	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
	
	public int isDownloaded() {
		return isDownloaded;
	}
	
	public int isPublished() {
		return isPublished;
	}
	
	public void setDifficulty(String string) {
		this.difficulty=string;
		
	}
	public void setDownloads(long downloads) {
		this.downloads=downloads;
	}
	public void setHangboardType(String string) {
		this.hangboard=string;
	}
	public void setDateAdded(String string) {
		this.dateAdded=string;
		
	}
	public void setRating(float double1) {
		this.rating=double1;
		
	}
	public void setDescription(String string) {
		this.description=string;
		
	}
	public void setOwner(String string) {
		this.owner=string;
		
	}
	public void setName(String string) {
		this.name=string;
		
	}
	public void setId(long int1) {
		this.id=int1;
		
	}
	public void setIsDownloaded(int value) {
		isDownloaded=value;
	}	
	public void setIsPublished(int value) {
		isPublished=value;
	}
	public void setTime(int time){
		this.time = time;
	}
	public void setExercises(ArrayList<Exercise> exercises){
		this.exercises=exercises;
	}
	public float getRating() {
		return rating;
	}
	public String getHangboard() {
		return hangboard;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public String getDescription() {
		return description;
	}
	public String getDate() {
		return dateAdded;
	}
	public void setServerId(long id) {
		serverId=id;
	}
	/**
	 * Needs the context because this queries the database
	 * Important to note that the this will return the total time
	 * of exercises for this workout. But if time was previously set 
	 * then it will return that time. This is in cases when time must be set
	 * when getting a workout from the server
	 * @param context
	 * @return
	 */
	public int getTime(Context context){
    	int length=0;
		if(time==0){
	    	Database db = new Database(context);
	    	ArrayList<Exercise> exercises = db.getExercises(id);
	    	String[] types = context.getResources().getStringArray(R.array.exercise_types);
        	for(Exercise e:exercises){
        		if(e.getExerciseType().equals(types[2]) || e.getExerciseType().equals(types[3]))
        			length+=e.getAmount()*3;
        		else
        			length+=e.getTime()+e.getAmount()*4; // Four being the number of seconds a pull up or leg lift takes
        	}
		} else if(isPublished == 1)
			length = time;
		return length;
	}
	public ArrayList<Exercise> getExercises(){
		return exercises;
	}
}
