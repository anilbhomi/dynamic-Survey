package com.rayoforms.survey.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends Fragment {

    private EditText etdStreet, etdState, etdPostCode;
    private Spinner spinnerCountries;

    private String fieldName;
    private String label;
    private JsonObject answerObject,remoteAnswerObject;
    private int surveyId;

    public onTabsChangeListener onTabsChangeListener;

    public AddressFragment() {
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
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        TextView tvLabel = view.findViewById(R.id.tv_label);
        etdStreet = view.findViewById(R.id.et_street);
        etdPostCode = view.findViewById(R.id.et_postcode);
        etdState = view.findViewById(R.id.et_state);
        spinnerCountries = view.findViewById(R.id.spinnerCountries);
        fieldName = getArguments().getString(Constants.EXTRA_NAME);
        label=getArguments().getString(Constants.EXTRA_LABEL);
        tvLabel.setText(label);

        setCountriesToSpinner();

        if (getArguments().getBoolean(Constants.EXTRA_IS_FROM_EDIT)) {
            String ans = new JsonParser().parse(getArguments().getString(Constants.EXTRA_ANS_OBJECT)).getAsJsonObject().get(fieldName).getAsString();
            try {
                JSONArray jsonArray = new JSONArray(ans);
                    etdStreet.setText(jsonArray.getString(0));
                    etdPostCode.setText(jsonArray.getString(1));
                    etdState.setText(jsonArray.getString(2));
                    spinnerCountries.setSelection(Integer.parseInt(jsonArray.getString(4)));
            } catch (JSONException e) {
                e.printStackTrace();
            }surveyId = getArguments().getInt(Constants.EXTRA_SURVEY_ID);
        } else {
            surveyId = PrefManager.getInstance().getId();
        }

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                onTabsChangeListener.onTabChanged(((FillFormActivity)getActivity()).mViewPager.getCurrentItem(),"RTL");
            }

            public void onSwipeLeft() {
                if (getArguments().getInt(Constants.EXTRA_ISREQUIRED)==1) {
                    if(TextUtils.isEmpty(etdState.getText().toString()) || TextUtils.isEmpty(etdStreet.getText().toString()) || TextUtils.isEmpty(etdPostCode.getText().toString()) || spinnerCountries.getSelectedItemPosition()==0 )
                    Toast.makeText(getContext(), "This field is required.", Toast.LENGTH_SHORT).show();
                } else {
                    answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getData(surveyId)).getAsJsonObject();
                    remoteAnswerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(getContext()).getRemoteAnsData(surveyId)).getAsJsonObject();
                    ArrayList<String> ansList = new ArrayList<>();
                    ansList.add(etdStreet.getText().toString().trim());
                    ansList.add(etdPostCode.getText().toString().trim());
                    ansList.add(etdState.getText().toString().trim());
                    ansList.add(String.valueOf(spinnerCountries.getSelectedItem()));
                    ansList.add(String.valueOf(spinnerCountries.getSelectedItemPosition()));
                    answerObject.addProperty(fieldName,String.valueOf(ansList));
                    remoteAnswerObject.addProperty(label, String.valueOf(ansList));
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

    private void setCountriesToSpinner() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        countries.add("Select country");
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountries.setAdapter(countryAdapter);
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
