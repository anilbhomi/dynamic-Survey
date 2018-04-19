package com.rayoforms.survey.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckBoxFragment extends Fragment {

    private String fieldName,label;
    private JsonObject answerObject,remoteAnswerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    LinearLayout llCheckboxes;

    public CheckBoxFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CheckBoxFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        CheckBoxFragment checkBoxFragment = new CheckBoxFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_LABEL, objQuestion.get("label").getAsString());
        args.putString(Constants.EXTRA_NAME, objQuestion.get("name").getAsString());
        args.putInt(Constants.EXTRA_ISREQUIRED, objQuestion.get("required").getAsInt());
        args.putInt(Constants.EXTRA_POSITION, position);
        args.putInt(Constants.EXTRA_SIZE, size);
        args.putInt(Constants.EXTRA_SURVEY_ID, surveyId);
        JsonObject object = (JsonObject) new JsonParser().parse(objQuestion.get("properties").getAsString()).getAsJsonObject();
        args.putString(Constants.EXTRA_OPTIONS, object.get("options").getAsString());
        if (ansObject != null) {
            args.putString(Constants.EXTRA_ANS_OBJECT, ansObject.toString());
        }
        args.putBoolean(Constants.EXTRA_IS_FROM_EDIT, isFromEdit);
        checkBoxFragment.setArguments(args);
        return checkBoxFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkbox, container, false);
        llCheckboxes = view.findViewById(R.id.ll_checkboxes);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        final Spinner spinnerOption = view.findViewById(R.id.spinner_options);
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        String options = getArguments().getString(Constants.EXTRA_OPTIONS);
        label=getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);

        for (int i = 0; i < options.split(",").length; i++) {
            CheckBox checkbox = new CheckBox(getContext());
            checkbox.setId(i);
            checkbox.setText(options.split(",")[i].trim());
            llCheckboxes.addView(checkbox);
        }

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            String ans = new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsString();
            try {
                JSONArray jsonArray = new JSONArray(ans);
                for (int i = 0; i < jsonArray.length(); i++) {
                    CheckBox checkBox = (CheckBox) llCheckboxes.getChildAt(Integer.parseInt(jsonArray.get(i).toString()));
                    checkBox.setChecked(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            surveyId = getArguments().getInt(Constants.EXTRA_SURVEY_ID);
        } else {
            surveyId = PrefManager.getInstance().getId();
        }

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                onTabsChangeListener.onTabChanged(((FillFormActivity) getActivity()).mViewPager.getCurrentItem(), "RTL");
            }

            public void onSwipeLeft() {
                if (getArguments().getInt(Constants.EXTRA_ISREQUIRED) == 1) {
                    Toast.makeText(getContext(), "This field is required.", Toast.LENGTH_SHORT).show();
                } else {

                    ArrayList<Integer> checkedlist = new ArrayList<>();
                    for (int i = 0; i < llCheckboxes.getChildCount(); i++) {
                        View nextChild = llCheckboxes.getChildAt(i);
                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            checkedlist.add(check.getId());
                        }
                    }
                    answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                    remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();

                    answerObject.addProperty(fieldName, String.valueOf(checkedlist));
                    remoteAnswerObject.addProperty(label, String.valueOf(checkedlist));
                    DatabaseManager.getInstance(getContext()).setData(surveyId, answerObject.toString(),remoteAnswerObject.toString());
                    if (((FillFormActivity) getActivity()).mViewPager.getCurrentItem() + 1 == getArguments().getInt(Constants.EXTRA_SIZE)) {
                        Intent in = new Intent(getContext(), LuncherActivity.class);
                        startActivity(in);
                    } else {
                        onTabsChangeListener.onTabChanged(((FillFormActivity) getActivity()).mViewPager.getCurrentItem(), "LTR");
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
