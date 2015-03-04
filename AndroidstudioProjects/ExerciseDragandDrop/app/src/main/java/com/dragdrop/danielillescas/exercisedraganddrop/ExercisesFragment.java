package com.dragdrop.danielillescas.exercisedraganddrop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dragdrop.danielillescas.exercisedraganddrop.helper.Database;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Exercise;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Workout;

import java.util.ArrayList;

import listviewdragginganimation.DynamicListView;
import listviewdragginganimation.StableArrayAdapter;

public class ExercisesFragment extends Fragment {
	
    private long workoutId=0;
    private boolean isSharedWorkout = false;
    private WorkoutTools wTools;

    private DynamicListView mListView;
    
    ArrayList<Exercise> exercises = new ArrayList<Exercise>();

    public ExercisesFragment(){
    }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View rootView = inflater.inflate(R.layout.fragment_edit_exercises, container, false);
    	if(getArguments()==null) return rootView;

    	workoutId = 1;
		isSharedWorkout = false;

        try {
            wTools = (WorkoutTools) getActivity();
        	exercises = wTools.getExercises();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnListUpdatedListener");
        }
        

		mListView = (DynamicListView) rootView.findViewById(R.id.drag_listview);
		update(false);

        return rootView;

    }
    
    
    public void update(boolean isNewItem){  	
        try {
            wTools = (WorkoutTools) getActivity();
        	exercises = wTools.getExercises();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnListUpdatedListener");
        }
    	if(workoutId != -1 && exercises !=null){	
	    	/* Drag and drop stuff */
	        StableArrayAdapter adapter = new StableArrayAdapter(getActivity(), R.layout.drag_item, exercises);

	        Log.d("ExercisesFragment","Number of exercises is " + exercises.size());
	        mListView.setExerciseList(exercises);
	        mListView.setWorkoutId(workoutId);
	        mListView.setAdapter(adapter);
	        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        if(isNewItem) {
	        	mListView.setSelection(mListView.getCount());
	        	mListView.post(new Runnable() {
	        	    @Override
	        	    public void run() {
	    	        	mListView.blinkLastItem();
	        	    }
	        	});
	        }
	        mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					Bundle args = new Bundle();
				    DialogFragment dialog = new OptionsDialogFragment(); 
					args.putLong("workoutId", workoutId);
			 		args.putInt("exerciseNum", position);
				    dialog.setArguments(args);
				    dialog.show(getFragmentManager(), "ExersiceDialogFragment");
				}
	        	
	        	});
	        
    	} 
    	
    }
    
    // Container Activity must implement this interface
    public interface OnListUpdatedListener {
        public void onListUpdated();
    }
    
    // Container Activity must implement this interface
    public interface WorkoutTools {
        public ArrayList<Exercise> getExercises();
        public Workout getWorkout();
    }
    
    public static class OptionsDialogFragment extends DialogFragment {
    	
    	private long workoutId;
    	private int position;
        private OnListUpdatedListener mCallback;

    	public OptionsDialogFragment(){
    	}
    	
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            
            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (OnListUpdatedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnHeadlineSelectedListener");
            }
        }
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	workoutId = 1;
        	position = getArguments().getInt("exerciseNum");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.exercise_options, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                		   Database db = new Database(getActivity());

                    	   switch(which){
                        	   case 0:
                            	   DialogFragment dFragment = new EditDialogFragment();
                            	   Bundle args = new Bundle();
                            	   args.putLong("workoutId", workoutId);
                            	   args.putInt("exerciseNum", position);
                            	   dFragment.setArguments(args);
                            	   dFragment.show(getFragmentManager(), "ExersiceDialogFragment");
    	                        	   break;
	                    	   case 1:
	                    		   db.deleteExercise(workoutId, position);
	                    		   mCallback.onListUpdated();
	                    		   break;
	                    	   case 2:
	                    		   Exercise dbE = db.getExercise(workoutId, position);
	                    		   db.insertExercise(dbE);
	                    		   db.close();
	                    		   ((WorkoutActivity) getActivity()).setAddedNewItem();
	                    		   mCallback.onListUpdated();
	                    		   break;
	                    	   case 3:
	                    		   dialog.dismiss();
	                    		   break;
                    	   }
                       }
                });
           return builder.create();
            
        }
    }
}
