package com.rayoforms.survey.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.rayoforms.survey.R;
import com.rayoforms.survey.fragments.AddressFragment;
import com.rayoforms.survey.fragments.CheckBoxFragment;
import com.rayoforms.survey.fragments.DateFragment;
import com.rayoforms.survey.fragments.EdittextFragment;
import com.rayoforms.survey.fragments.EmailFragment;
import com.rayoforms.survey.fragments.MultiInputFragment;
import com.rayoforms.survey.fragments.NumberFragment;
import com.rayoforms.survey.fragments.RadioFragment;
import com.rayoforms.survey.fragments.SpinnerFragment;
import com.rayoforms.survey.fragments.TextareaFragment;

public class ViewPagerManager {

    public static Fragment Pager(int surveyId, JsonObject individualQuestion, JsonObject answerObject, int position, int size, Boolean isFromEdit) {
        switch (individualQuestion.get("type").getAsString()) {
            case "text": {
                return EdittextFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "select": {
                return SpinnerFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "radio": {
                return RadioFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "checkbox": {
                return CheckBoxFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "textarea": {
                return TextareaFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "number": {
                return NumberFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "email": {
                return EmailFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "date": {
                return DateFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "address": {
                return AddressFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
            case "multi-input": {
                return MultiInputFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
            }
        }
        return new PlaceholderFragment();
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }
}
