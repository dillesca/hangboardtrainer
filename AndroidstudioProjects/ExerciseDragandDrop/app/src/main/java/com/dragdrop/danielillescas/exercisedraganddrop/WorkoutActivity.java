package com.dragdrop.danielillescas.exercisedraganddrop;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dragdrop.danielillescas.exercisedraganddrop.ExercisesFragment.WorkoutTools;
import com.dragdrop.danielillescas.exercisedraganddrop.helper.Database;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Exercise;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Workout;

import java.util.ArrayList;





public class WorkoutActivity extends FragmentActivity implements ExercisesFragment.OnListUpdatedListener, WorkoutTools{
	
	private Workout workout;
	private ArrayList<Exercise> exercises;

    private long workoutId=-1;
    private ActionBar actionBar;
    private boolean newWorkout=false;
    public static String WORKOUT_ID = "workoutId";
    public boolean isView = true;
    ViewPager mViewPager;
	private boolean addButtonPressed;
	
	private ProgressDialog progressDialog;


    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
	    	super.setTheme(R.style.AppTheme);
	    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
	    	super.setTheme(R.style.AppThemeLandscape);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        
        

        Database db = new Database(this);
    	workout = db.getWorkout(1);
    	exercises = db.getExercises(workoutId);
        Log.d("WorkoutActivity","Workout name is " + workout.getName());

    	addButtonPressed = false;
        
		// Set up the action bar to show a dropdown list.
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setTitle("Drag Exercises");
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    
		//Tab Stuff
	    Tab tabExercise = actionBar.newTab().setText(getResources().getString(R.string.exercises));
	    tabExercise.setTag("exercise_tab");
	    tabExercise.setTabListener(new TabListener(new ExercisesFragment()));
	    actionBar.addTab(tabExercise,0,true);
	    
    }
    

	private OnClickListener bottomBarClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {

		}	
	};	


    public class TabListener implements ActionBar.TabListener {

        public TabListener(Fragment fragment) {
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {

        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
//            ft.remove(fragment);
        }
    }
    
    public void setIsView(boolean value){
    	isView = value;
    }
    
    public void setWorkoutId(long id) {
    	workoutId=id;
    }
    
    public void setAddedNewItem(){
    	addButtonPressed=true;
    }
    
    public void updateViewPager(int index){
		Database db = new Database(this);
		workout = db.getWorkout(workoutId);
		exercises = db.getExercises(workoutId);
	    mViewPager.removeAllViews();
	    mViewPager.setCurrentItem(index);
        invalidateOptionsMenu();
    }
    
    @Override
    public void onBackPressed(){
    	if(!isView){
    		isView=true;
    		updateViewPager(actionBar.getSelectedNavigationIndex());
    	} else {
    		
    		super.onBackPressed();
    	}
    }
    
    @Override
    public void finish(){
    	Intent intent = new Intent();
    	int blinkValue=-1;
    	if(newWorkout) {
    		blinkValue=0;
    	}
    	intent.putExtra("newWorkout",blinkValue);
    	setResult(RESULT_OK, intent);
    	super.finish();
    }
    /**
     * Set a flag that a new workout has been created
     */
    public void isNewWorkout(){
    	newWorkout=true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

   		menu.findItem(R.id.action_add).setVisible(true);
   	   	menu.findItem(R.id.action_edit).setVisible(false);
   	   	menu.findItem(R.id.action_cancel).setVisible(false);
   	   	menu.findItem(R.id.action_download).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }
    
    /**
     * Called to populate actionbar item
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actionbar_edit, menu);
	    
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_edit:
                isView=false;
                int index1 = actionBar.getSelectedNavigationIndex();
                updateViewPager(index1);
                return true;
            case R.id.action_add:
                insertNewExercise();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void insertNewExercise() {
		Database db = new Database(this);
		String[] holds = getResources().getStringArray(R.array.holds);
		String[] types = getResources().getStringArray(R.array.exercise_types);
		
		Exercise e = new Exercise();
		e.setExerciseType(types[0]);
		e.setLeftHand(holds[0]);
		e.setRightHand(holds[0]);
		e.setWorkoutId(workoutId);
		e.setTime(10);
		
		db.insertExercise(e);
		db.close();

		addButtonPressed = true;
		exercises = db.getExercises(workoutId);
	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	}
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.popup_menu_exercise, menu);
	}

	@Override
	public void onListUpdated() {
		Database db = new Database(this);
		workout = db.getWorkout(workoutId);
		exercises = db.getExercises(workoutId);
	}
    

	@Override
	public ArrayList<Exercise> getExercises() {
		return exercises;
	}

	@Override
	public Workout getWorkout() {
		return workout;
	}
	
	public void saveWorkout(){
    	Database db = new Database(WorkoutActivity.this);
		workoutId=db.insertWorkout(workout.getName(), 
				workout.getDescription(), 
				workout.getDifficulty(),
				workout.getOwner(),1,1,workout.getId());
		workout.setId(workoutId);
		for(Exercise e : exercises){
			e.setWorkoutId(workoutId);
			db.insertExercise(e);
		}
	}
	


}
 