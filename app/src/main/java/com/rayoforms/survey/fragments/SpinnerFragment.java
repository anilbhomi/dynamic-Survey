package com.rayoforms.survey.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.Constants;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.Utils.PrefManager;
import com.rayoforms.survey.activities.FillFormActivity;
import com.rayoforms.survey.activities.LuncherActivity;
import com.rayoforms.survey.customs.OnSwipeTouchListener;
import com.rayoforms.survey.interfaces.onTabsChangeListener;

import java.util.ArrayList;
import java.util.Arrays;

public class SpinnerFragment extends Fragment {

    private String fieldName;
    private String label;
    private JsonObject answerObject,remoteAnswerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    public SpinnerFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpinnerFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        SpinnerFragment spinnerFragment = new SpinnerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_LABEL, objQuestion.get("label").getAsString());
        args.putString(Constants.EXTRA_NAME, objQuestion.get("name").getAsString());
        args.putInt(Constants.EXTRA_ISREQUIRED, objQuestion.get("required").getAsInt());
        args.putInt(Constants.EXTRA_POSITION, position);
        args.putInt(Constants.EXTRA_SIZE, size);
        args.putInt(Constants.EXTRA_SURVEY_ID, surveyId);
        JsonObject object =(JsonObject) new JsonParser().parse(objQuestion.get("properties").getAsString()).getAsJsonObject();
        args.putString(Constants.EXTRA_OPTIONS,object.get("options").getAsString());
        if (ansObject != null) {
            args.putString(Constants.EXTRA_ANS_OBJECT, ansObject.toString());
        }
        args.putBoolean(Constants.EXTRA_IS_FROM_EDIT, isFromEdit);
        spinnerFragment.setArguments(args);
        return spinnerFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spinner, container, false);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        final Spinner spinnerOption = view.findViewById(R.id.spinner_options);
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        String options = getArguments().getString(Constants.EXTRA_OPTIONS);
        label=getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);
        ArrayList<String> option = new ArrayList<>();
        option.add("Please Select");
        String[] optionsArray = options.split(",");
        option.addAll(Arrays.asList(optionsArray));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, option);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOption.setAdapter(spinnerArrayAdapter);

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            spinnerOption.setSelection(new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsInt());
            surveyId = getArguments().getInt(Constants.EXTRA_SURVEY_ID);
        } else {
             surveyId = PrefManager.getInstance().getId();
        }

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                onTabsChangeListener.onTabChanged(((FillFormActivity)getActivity()).mViewPager.getCurrentItem(),"RTL");
            }

            public void onSwipeLeft() {
                if (spinnerOption.getSelectedItemPosition()==0 && getArguments().getInt(Constants.EXTRA_ISREQUIRED)==1) {
                    Toast.makeText(getContext(), "This field is required.", Toast.LENGTH_SHORT).show();
                } else {
                    answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                    remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();
                    answerObject.addProperty(fieldName, spinnerOption.getSelectedItemPosition());
                    remoteAnswerObject.addProperty(label, spinnerOption.getSelectedItemPosition());
                    DatabaseManager.getInstance(getContext()).setData(surveyId, answerObject.toString(),remoteAnswerObject.toString());
                    if (((FillFormActivity) getActivity()).mViewPager.getCurrentItem() + 1 == getArguments().getInt(Constants.EXTRA_SIZE)) {
                        Intent in = new Intent(getContext(), LuncherActivity.class);
                        startActivity(in);
                    } else {
                        onTabsChangeListener.onTabChanged(((FillFormActivity) getActivity()).mViewPager.getCurrentItem(),"LTR");
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onTabsChangeListener = (onTabsChangeListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
