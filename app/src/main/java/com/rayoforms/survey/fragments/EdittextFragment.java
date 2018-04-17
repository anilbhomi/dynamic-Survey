package com.rayoforms.survey.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.activities.LuncherActivity;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.Utils.PrefManager;
import com.rayoforms.survey.activities.FillFormActivity;
import com.rayoforms.survey.customs.OnSwipeTouchListener;

public class EdittextFragment extends Fragment {

    private static final String EXTRA_LABEL = "label";
    private static final String EXTRA_TYPE = "inputType";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_ISREQUIRED = "isRequired";
    private static final String EXTRA_POSITION = "position";
    private static final String EXTRA_SIZE = "size";
    private static final String EXTRA_IS_FROM_EDIT = "isFromEdit";
    private static final String EXTRA_SURVEY_ID = "surveyId";
    private static final String EXTRA_ANS_OBJECT = "ansObject";

    public EditText etdEdittext;

    private String fieldName;
    private JsonObject answerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    public EdittextFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EdittextFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        EdittextFragment edittextFragment = new EdittextFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_LABEL, objQuestion.get("label").getAsString());
        args.putString(EXTRA_TYPE, objQuestion.get("type").getAsString());
        args.putString(EXTRA_NAME, objQuestion.get("name").getAsString());

        args.putInt(EXTRA_ISREQUIRED, objQuestion.get("required").getAsInt());
        args.putInt(EXTRA_POSITION, position);
        args.putInt(EXTRA_SIZE, size);

        args.putInt(EXTRA_SURVEY_ID, surveyId);
        if (ansObject != null) {
            args.putString(EXTRA_ANS_OBJECT, ansObject.toString());
        }
        args.putBoolean(EXTRA_IS_FROM_EDIT, isFromEdit);
        edittextFragment.setArguments(args);
        return edittextFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edittext, container, false);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        etdEdittext = view.findViewById(R.id.et_edittext);

        fieldName = getArguments().getString(EXTRA_NAME);
        tvLabel.setText(getArguments().getString(EXTRA_LABEL));
        checkInputType(getArguments().getString(EXTRA_TYPE));

        if (getArguments().getBoolean(EXTRA_IS_FROM_EDIT)) {
            etdEdittext.setText(new JsonParser().parse(getArguments().getString(EXTRA_ANS_OBJECT)).getAsJsonObject().get(getArguments().getString(EXTRA_LABEL)).getAsString());
            surveyId = getArguments().getInt(EXTRA_SURVEY_ID);
        } else {
            surveyId = PrefManager.getInstance().getId();
        }

        assert ((FillFormActivity) getActivity()) != null;
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                onTabsChangeListener.onTabChanged(((FillFormActivity)getActivity()).mViewPager.getCurrentItem(),"RTL");
            }

            public void onSwipeLeft() {
                if (TextUtils.isEmpty(etdEdittext.getText().toString()) && getArguments().getInt(EXTRA_ISREQUIRED)==1) {
                    Toast.makeText(getContext(), "This field is required.", Toast.LENGTH_SHORT).show();
                } else {
                    answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                    answerObject.addProperty(fieldName, etdEdittext.getText().toString());
                    DatabaseManager.getInstance(getContext()).setData(surveyId, answerObject.toString());
                    if (((FillFormActivity) getActivity()).mViewPager.getCurrentItem() + 1 == getArguments().getInt(EXTRA_SIZE)) {
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

    private void checkInputType(String inputType) {
        switch (inputType) {
            case "email": {
                etdEdittext.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                break;
            }
            case "number": {
                etdEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            }
            case "number_decimal": {
                etdEdittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            }
            case "text": {
                etdEdittext.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            }
        }
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

    public interface onTabsChangeListener {
        void onTabChanged(int tabPosition, String swipeDirection);
    }
}
