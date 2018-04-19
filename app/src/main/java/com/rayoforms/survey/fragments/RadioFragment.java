package com.rayoforms.survey.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RadioFragment extends Fragment {

    private RadioButton radioButton;
    private RadioGroup rgOptions;
    private String label,fieldName;
    private JsonObject answerObject,remoteAnswerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    public RadioFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RadioFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        RadioFragment radioFragment = new RadioFragment();
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
        radioFragment.setArguments(args);
        return radioFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);
        TextView tvLabel = view.findViewById(R.id.tv_label);

        rgOptions = view.findViewById(R.id.rg_options);
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        String options = getArguments().getString(Constants.EXTRA_OPTIONS);
        label=getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);

        for (String anOptionsArray : options.split(",")) {
            radioButton = new RadioButton(getContext());
            radioButton.setText(anOptionsArray);
            rgOptions.addView(radioButton);
        }

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            if (!TextUtils.isEmpty(new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsString())) {
                RadioButton radioButton = (RadioButton) rgOptions.getChildAt(new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsInt());
                radioButton.setChecked(true);
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
                if (rgOptions.getCheckedRadioButtonId() == -1 && getArguments().getInt(Constants.EXTRA_ISREQUIRED) == 1) {
                    Toast.makeText(getContext(), "This field is required.", Toast.LENGTH_SHORT).show();
                } else {
                    int radioButtonID = rgOptions.getCheckedRadioButtonId();
                    View radioButton = rgOptions.findViewById(radioButtonID);
                    String idx = String.valueOf(rgOptions.indexOfChild(radioButton));
                    if (idx.equals("-1"))
                        idx = "";
                    answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                    remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();
                    answerObject.addProperty(fieldName, idx);
                    remoteAnswerObject.addProperty(label, idx);
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
