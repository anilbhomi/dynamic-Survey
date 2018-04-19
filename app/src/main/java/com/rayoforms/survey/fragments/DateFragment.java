package com.rayoforms.survey.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.Constants;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.Utils.PrefManager;
import com.rayoforms.survey.Utils.ToastManager;
import com.rayoforms.survey.activities.FillFormActivity;
import com.rayoforms.survey.activities.LuncherActivity;
import com.rayoforms.survey.customs.CustomDatePicker;
import com.rayoforms.survey.customs.OnSwipeTouchListener;
import com.rayoforms.survey.interfaces.onTabsChangeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFragment extends Fragment {

    private CustomDatePicker datePicker;
    private String dateAndTime;
    private String fieldName;
    private String label;
    private JsonObject answerObject, remoteAnswerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    public DateFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DateFragment newInstance(int surveyId, JsonObject objQuestion, JsonObject ansObject, int position, int size, Boolean isFromEdit) {
        DateFragment dateFragment = new DateFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_LABEL, objQuestion.get("label").getAsString());
        args.putString(Constants.EXTRA_NAME, objQuestion.get("name").getAsString());
        args.putInt(Constants.EXTRA_ISREQUIRED, objQuestion.get("required").getAsInt());
        args.putInt(Constants.EXTRA_POSITION, position);
        args.putInt(Constants.EXTRA_SIZE, size);
        args.putInt(Constants.EXTRA_SURVEY_ID, surveyId);
        if (ansObject != null) {
            args.putString(Constants.EXTRA_ANS_OBJECT, ansObject.toString());
        }
        args.putBoolean(Constants.EXTRA_IS_FROM_EDIT, isFromEdit);
        dateFragment.setArguments(args);
        return dateFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        label = getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);
        dateAndTime = new Date().getTime() + "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                int mon = month + 1;
                final java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date d = dateFormat.parse(year + "-" + mon + "-" + dayOfMonth);
                    dateAndTime = d.getTime() + "";
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            String date = new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsString();
            if (!TextUtils.isEmpty(date)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = new Date(Long.parseLong(date));
                try {
                    Date d = dateFormat.parse((String) DateFormat.format("yyyy-MM-dd", parsedDate));
                    datePicker.updateDate(Integer.parseInt((String) DateFormat.format("yyyy", d)), Integer.parseInt((String) DateFormat.format("M", d)) - 1, Integer.parseInt((String) DateFormat.format("dd", d)));
                } catch (ParseException e) {
                    ToastManager.ShowToast(getContext(), e.getLocalizedMessage(), false);
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
                answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();
                answerObject.addProperty(fieldName, dateAndTime);
                remoteAnswerObject.addProperty(label, dateAndTime);
                DatabaseManager.getInstance(getContext()).setData(surveyId, answerObject.toString(), remoteAnswerObject.toString());
                if (((FillFormActivity) getContext()).mViewPager.getCurrentItem() + 1 == getArguments().getInt(Constants.EXTRA_SIZE)) {
                    Intent in = new Intent(getContext(), LuncherActivity.class);
                    startActivity(in);
                } else {
                    onTabsChangeListener.onTabChanged(((FillFormActivity) getContext()).mViewPager.getCurrentItem(), "LTR");
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
