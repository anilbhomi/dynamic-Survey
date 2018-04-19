package com.rayoforms.survey.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiInputFragment extends Fragment {

       private LinearLayout parentLayout;

    private String fieldName;
    private String label;
    private JsonObject answerObject, remoteAnswerObject;
    private int surveyId;

    private JsonArray questionArray;

    public com.rayoforms.survey.interfaces.onTabsChangeListener onTabsChangeListener;

    public MultiInputFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddressFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        AddressFragment addressFragment = new AddressFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_LABEL, objQuestion.get("label").getAsString());
        args.putString(Constants.EXTRA_NAME, objQuestion.get("name").getAsString());
        args.putInt(Constants.EXTRA_ISREQUIRED, objQuestion.get("required").getAsInt());
        args.putInt(Constants.EXTRA_POSITION, position);
        args.putInt(Constants.EXTRA_SIZE, size);
        args.putInt(Constants.EXTRA_SURVEY_ID, surveyId);
        JsonObject propertiesObject = (JsonObject) new JsonParser().parse(objQuestion.get("properties").getAsString()).getAsJsonObject();
        JsonArray questionArray = (JsonArray) new JsonParser().parse(propertiesObject.get("questions").getAsString()).getAsJsonArray();
        args.putString(Constants.EXTRA_QUS_ARRAY, questionArray.toString());
        if (ansObject != null) {
            args.putString(Constants.EXTRA_ANS_OBJECT, ansObject.toString());
        }
        args.putBoolean(Constants.EXTRA_IS_FROM_EDIT, isFromEdit);
        addressFragment.setArguments(args);
        return addressFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multi_input, container, false);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        parentLayout = view.findViewById(R.id.parentLayout);
        questionArray = new JsonParser().parse(getArguments().getString(Constants.EXTRA_QUS_ARRAY)).getAsJsonArray();
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        label = getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);

        for (int i = 0; i < questionArray.size(); i++) {
            TextView text = new TextView(getContext());
            text.setText(questionArray.get(i).getAsString());
            EditText field = new EditText(getContext());
            field.setMaxLines(1);
            parentLayout.addView(text);
            parentLayout.addView(field);
        }

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            for (int i = 0; i < parentLayout.getChildCount(); i++) {
                if (parentLayout.getChildAt(i) instanceof EditText) {
                    EditText editText = (EditText) parentLayout.getChildAt(i);
                    editText.setText(new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsString());
                }
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
                ArrayList<String> ansList = new ArrayList<>();
                answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();

                for (int i = 0; i < parentLayout.getChildCount(); i++) {
                    if (parentLayout.getChildAt(i) instanceof EditText) {
                        EditText editText = (EditText) parentLayout.getChildAt(i);
                        answerObject.addProperty(fieldName, editText.getText().toString());
                        remoteAnswerObject.addProperty(label, editText.getText().toString());
                    }
                }

                DatabaseManager.getInstance(getContext()).setData(surveyId, answerObject.toString(), remoteAnswerObject.toString());
                if (((FillFormActivity) getActivity()).mViewPager.getCurrentItem() + 1 == getArguments().getInt(Constants.EXTRA_SIZE)) {
                    Intent in = new Intent(getContext(), LuncherActivity.class);
                    startActivity(in);
                } else {
                    onTabsChangeListener.onTabChanged(((FillFormActivity) getActivity()).mViewPager.getCurrentItem(), "LTR");
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
