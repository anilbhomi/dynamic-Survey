package com.rayoforms.survey.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.rayoforms.survey.R;
import com.rayoforms.survey.fragments.EdittextFragment;

/**
 * Created by anil on 4/15/18.
 */

public class ViewPagerManager {

    public static Fragment Pager(int surveyId, JsonObject individualQuestion, JsonObject answerObject, int position, int size, Boolean isFromEdit) {
        switch (individualQuestion.get("type").getAsString()) {
            case "text": {
                return EdittextFragment.newInstance(surveyId, individualQuestion, answerObject, position, size, isFromEdit);
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
