/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listviewdragginganimation;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dragdrop.danielillescas.exercisedraganddrop.R;
import com.dragdrop.danielillescas.exercisedraganddrop.models.Exercise;

import java.util.ArrayList;


public class StableArrayAdapter extends ArrayAdapter<Exercise> {

    final int INVALID_ID = -1;
    private LayoutInflater mInflater;
    private ArrayList<Exercise> mExercises;
    int resourceId;

    public StableArrayAdapter(Context context, int layoutViewResourceId, ArrayList<Exercise> exercises) {
        super(context,layoutViewResourceId,exercises);
        mExercises = exercises;
    	mInflater = LayoutInflater.from(context);
    	resourceId=layoutViewResourceId;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if(convertView==null)
	    	convertView = mInflater.inflate(resourceId, null);
            
        TextView tvType =(TextView) convertView.findViewById(R.id.tv_exercise_type);
        TextView tvValue =(TextView) convertView.findViewById(R.id.tv_exercise_time);
        TextView tvLeft =(TextView) convertView.findViewById(R.id.tv_left_hold);
        TextView tvRight =(TextView) convertView.findViewById(R.id.tv_right_hold);
        TableLayout tbl = (TableLayout) convertView.findViewById(R.id.tl_exercise_holds);

        Resources resources = convertView.getContext().getResources();
        Exercise e = mExercises.get(position);
        String type = e.getExerciseType();
        
        tvType.setText(type);
        
        if(type.equals(resources.getStringArray(R.array.exercise_types)[0])){
        	tvType.setTextColor(convertView.getContext().getResources().getColor(R.color.android_green));
        	tvValue.setText(e.getTime() + "s");
        	tbl.setVisibility(View.GONE);
        } else if(type.equals(resources.getStringArray(R.array.exercise_types)[1])) {
        	tvType.setTextColor(convertView.getContext().getResources().getColor(R.color.android_blue));
        	tbl.setVisibility(View.VISIBLE);
        	tvValue.setText(e.getTime() + "s");
        	tvLeft.setText(" "+e.getLeftHand());
        	tvRight.setText(" "+e.getRightHand());
        } else if(type.equals(resources.getStringArray(R.array.exercise_types)[2])) {
        	tvType.setTextColor(convertView.getContext().getResources().getColor(R.color.android_yellow));
        	tbl.setVisibility(View.VISIBLE);
        	tvValue.setText(String.valueOf(e.getAmount()));
        	tvLeft.setText(" "+e.getLeftHand());
        	tvRight.setText(" "+e.getRightHand());
        } else {
        	tvType.setTextColor(convertView.getContext().getResources().getColor(R.color.android_red));
        	tbl.setVisibility(View.VISIBLE);
        	tvValue.setText(String.valueOf(e.getAmount()));
        	tvLeft.setText(" "+e.getLeftHand());
        	tvRight.setText(" "+e.getRightHand());
        }
        
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mExercises.size()) {
            return INVALID_ID;
        }
//        String item = getItem(position);
        return mExercises.get(position).getExerciseNumber();
//        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    

}
