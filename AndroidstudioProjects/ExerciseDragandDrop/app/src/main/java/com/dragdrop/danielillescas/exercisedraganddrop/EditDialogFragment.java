package com.dragdrop.danielillescas.exercisedraganddrop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dragdrop.danielillescas.exercisedraganddrop.helper.Database;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Exercise;


public class EditDialogFragment extends DialogFragment {

	private long workoutId;	
	private int position;
    private ExercisesFragment.OnListUpdatedListener mCallback;
    
    private Exercise mExercise;

    
    //Views
    private View dialogView ;
    private NumberPicker nPickerSecs;
    private NumberPicker nPickerMins;
    private NumberPicker nPickerAmount;
    private Spinner spinner;
    private Spinner spRight;
    private Spinner spLeft;
    private TextView tvRight;
    private TextView tvLeft;
    private LinearLayout llAmount;
    private TableLayout tlTime;
	
	public EditDialogFragment(){
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ExercisesFragment.OnListUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
    	workoutId = getArguments().getLong(WorkoutActivity.WORKOUT_ID);
    	position = getArguments().getInt("exerciseNum");
    	
		Database db = new Database(getActivity());
		mExercise=db.getExercise(workoutId, position);
		
    	String[] seconds = range(60,'s');
    	String[] minutes = range(10,'m');
    	String[] amounts = range(50,' ');
    	
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    dialogView = inflater.inflate(R.layout.dialog_edit_exercise, null);
	    
	    //Find all views
	    initializeViews();
	    
	    // Populate Spinners
	    assignSpinnerAdapters();
	    
	    // Select spinner correct value
    	spinner.setSelection(getTypeSpinnerPosition());
    	spRight.setSelection(getHandPosition(true)); //true is right hand
    	spLeft .setSelection(getHandPosition(false)); //false is left hand

	    nPickerMins.setMinValue(0);
	    nPickerMins.setMaxValue(minutes.length-1);
	    nPickerMins.setDisplayedValues(minutes);
	    nPickerMins.setValue((int) mExercise.getMinutes());
	    
	    nPickerSecs.setMinValue(0);
	    nPickerSecs.setMaxValue(seconds.length-1);
	    nPickerSecs.setDisplayedValues(seconds);
	    nPickerSecs.setValue((int) mExercise.getFormattedSeconds());
	    
	    nPickerAmount.setMinValue(0);
	    nPickerAmount.setMaxValue(amounts.length-1);
	    nPickerAmount.setDisplayedValues(amounts);
	    nPickerAmount.setValue(mExercise.getAmount());
	    
	    
	    /* Hide all the vies depending on the category of the exercise*/
	    hideViewsBasedOnType();
	    
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View spinner, int position, long id) {
				mExercise.setExerciseType(getActivity().getResources().getStringArray(R.array.exercise_types)[position]);
				hideViewsBasedOnType();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
	    });
	    
	    
	    
	    builder.setTitle(dialogView.getContext().getResources().getString(R.string.edit_exercise));
	    builder.setView(dialogView)
	    // Add action buttons
	           .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   Database db = new Database(getActivity());
    	               mExercise=getUpdatedExercise();                 
	                   db.updateExercise(mExercise);
	                   mCallback.onListUpdated();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   EditDialogFragment.this.getDialog().cancel();
	               }
	           });      
	    return builder.create();
	}
	
	private Exercise getUpdatedExercise() {
		Exercise e = mExercise;
		e.setExerciseType(spinner.getSelectedItem().toString());
		e.setTime(nPickerMins.getValue()*60 + nPickerSecs.getValue());
		e.setAmount(nPickerAmount.getValue());
		e.setLeftHand(spLeft.getSelectedItem().toString());
		e.setRightHand(spRight.getSelectedItem().toString());
		return e;
	}

	private void hideViewsBasedOnType() {
		int position = getTypeSpinnerPosition();
		switch(position){
		case 0 : //Rest 
			//Hide handHolds and numberPickerAmount 
			tvRight.setVisibility(View.GONE); tvLeft.setVisibility(View.GONE);
			spRight.setVisibility(View.GONE); spLeft.setVisibility(View.GONE);
			llAmount.setVisibility(View.GONE);
			//Show numberPickerSeconds/Minutes
			tlTime.setVisibility(View.VISIBLE);
			break;
		case 1: //Hang
			//hide npAmount
			llAmount.setVisibility(View.GONE);
			//show holds npMinutes/Seconds 
			tvRight.setVisibility(View.VISIBLE); tvLeft.setVisibility(View.VISIBLE);
			spRight.setVisibility(View.VISIBLE); spLeft.setVisibility(View.VISIBLE);
			tlTime.setVisibility(View.VISIBLE);
			break;
		case 2: //Pull Up
			//hide npPickerMinutes/Seconds
			tlTime.setVisibility(View.GONE);
			//show npTime show holds
			tvRight.setVisibility(View.VISIBLE); tvLeft.setVisibility(View.VISIBLE);
			spRight.setVisibility(View.VISIBLE); spLeft.setVisibility(View.VISIBLE);
			llAmount.setVisibility(View.VISIBLE);
			break;
		case 3: //Leg Lift
			//hide npPickerMinutes/Seconds
			tlTime.setVisibility(View.GONE);
			//show npTime show holds
			tvRight.setVisibility(View.VISIBLE); tvLeft.setVisibility(View.VISIBLE);
			spRight.setVisibility(View.VISIBLE); spLeft.setVisibility(View.VISIBLE);
			llAmount.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void assignSpinnerAdapters() {
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
    	        R.array.exercise_types, R.layout.spinner_item);
	    ArrayAdapter<CharSequence> adapterRight = ArrayAdapter.createFromResource(getActivity(),
    	        R.array.holds, R.layout.spinner_item);
	    ArrayAdapter<CharSequence> adapterLeft = ArrayAdapter.createFromResource(getActivity(),
    	        R.array.holds, R.layout.spinner_item);
	    
	    adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
	    adapterRight.setDropDownViewResource(R.layout.spinner_item_dropdown);
	    adapterLeft.setDropDownViewResource(R.layout.spinner_item_dropdown);	    
    	
    	spinner.setAdapter(adapter);
    	spRight.setAdapter(adapterRight);
    	spLeft.setAdapter(adapterLeft);
    	
	}

	private void initializeViews() {
	    nPickerSecs = (NumberPicker) dialogView.findViewById(R.id.np_seconds);
	    nPickerMins = (NumberPicker) dialogView.findViewById(R.id.np_minutes);
	    nPickerAmount = (NumberPicker) dialogView.findViewById(R.id.np_amount);
	    spinner = (Spinner) dialogView.findViewById(R.id.sp_exercise_type);
	    spRight = (Spinner) dialogView.findViewById(R.id.sp_right);
	    spLeft = (Spinner) dialogView.findViewById(R.id.sp_left);
	    tvRight = (TextView) dialogView.findViewById(R.id.right);
	    tvLeft = (TextView) dialogView.findViewById(R.id.left);
	    llAmount = (LinearLayout) dialogView.findViewById(R.id.ll_amount);
	    tlTime = (TableLayout) dialogView.findViewById(R.id.tl_time);
	}

	private int getTypeSpinnerPosition() {
		String type = mExercise.getExerciseType();
		String[] types = dialogView.getResources().getStringArray(R.array.exercise_types);
		for(int i=0;i<types.length;i++)
			if(type.equals(types[i]))
				return i;
		return 0;
	}
	
	private int getHandPosition(boolean isRight) {
		String hold = mExercise.getRightHand();
		if(!isRight)  
			hold = mExercise.getLeftHand();
		String[] holds = dialogView.getResources().getStringArray(R.array.holds);
		for(int i=0;i<holds.length;i++)
			if(hold.equals(holds[i]))
				return i;
		return 0;
	}
	
	private String[] range(int size, char c){
    	String[] array = new String[size];
    	for(int i = 0;i<size;i++)
    		array[i] = String.valueOf(i) + c;
    	return array;
	}
}