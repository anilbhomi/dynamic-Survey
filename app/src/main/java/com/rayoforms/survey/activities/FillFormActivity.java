package com.rayoforms.survey.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.Constants;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.Utils.PrefManager;
import com.rayoforms.survey.Utils.ToastManager;
import com.rayoforms.survey.Utils.ViewPagerManager;
import com.rayoforms.survey.customs.CustomViewPager;
import com.rayoforms.survey.fragments.EdittextFragment;

import org.json.JSONArray;

/**
 * An Activity to fill up new form
 */
public class FillFormActivity extends AppCompatActivity implements EdittextFragment.onTabsChangeListener {

    private JsonArray questionsArray;

    private Boolean isFromEdit = false;
    private int form_id;
    private String slug;
    private int qusPosition;

    public SectionsPagerAdapter mSectionsPagerAdapter;

    public CustomViewPager mViewPager;

    /**
     *get slug, form_id, isFromEdit from {@link FormsListActivity}
     * slug used to retrive forms field from server
     * if isFromEdit is true get data from database using form_id
     * else
     *      if there is question to specific form_id then retrive question from database and start new form fillup
     *      else get question from server and start new form fill up
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_form);
        slug = getIntent().getStringExtra("slug");
        form_id = getIntent().getIntExtra("form_id", 0);
        isFromEdit = getIntent().getBooleanExtra("isFromEdit", false);

        PrefManager.getInstance().setId(PrefManager.getInstance().getId() == 0 ? 1 : PrefManager.getInstance().getId() + 1);

        if (!isFromEdit) {
            if (DatabaseManager.getInstance(this).checkQuestion(form_id) > 0) {
                getQuestionFromDatabase();
                setAdapter();
                DatabaseManager.getInstance(FillFormActivity.this).setNewData(form_id,PrefManager.getInstance().getId(),String.valueOf(questionsArray), String.valueOf(addAnsToAnswerObject()));
            } else {
                getQuestionFromServer();
            }
        } else {
            qusPosition = getIntent().getIntExtra("position", 0);
            getQuestionFromDatabase();
            setAdapter();
            mViewPager.setCurrentItem(qusPosition);
        }
    }

    /**
     * get question from server using slug
     */
    private void getQuestionFromServer() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://192.168.1.10:8000/api/form/" + slug + "/fields", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        DatabaseManager.getInstance(FillFormActivity.this).setQuestion(form_id, String.valueOf(jsonArray));
                        getQuestionFromDatabase();
                        setAdapter();
                        DatabaseManager.getInstance(FillFormActivity.this).setNewData(form_id,PrefManager.getInstance().getId(),String.valueOf(questionsArray), String.valueOf(addAnsToAnswerObject()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            ToastManager.ShowToast(FillFormActivity.this, Constants.ERROR_NO_CONNECTION, true);
                        } else if (error instanceof ServerError) {
                            ToastManager.ShowToast(FillFormActivity.this, Constants.ERROR_SERVER, true);
                        } else if (error instanceof NetworkError) {
                            ToastManager.ShowToast(FillFormActivity.this, Constants.ERROR_NETWORK, true);
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * @return answerObject as default answer to all questions
     */
    private JsonObject addAnsToAnswerObject() {
        JsonObject answerObject = new JsonObject();
        for (int i = 0; i < questionsArray.size(); i++) {
            answerObject.addProperty(questionsArray.get(i).getAsJsonObject().get("name").getAsString(), "");
        }
        return answerObject;
    }

    /**
     * retrive question array from database
     */
    private void getQuestionFromDatabase() {
        questionsArray = new JsonParser().parse(DatabaseManager.getInstance(getApplicationContext()).getQuestions(form_id)).getAsJsonArray();
    }

    private void setAdapter() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * @param tabPosition as viewpager position
     * @param swipeDirection as swipe direction
     */
    @Override
    public void onTabChanged(int tabPosition, String swipeDirection) {
        if (swipeDirection.equals("LTR")) {
            mViewPager.setCurrentItem(tabPosition + 1);
        } else if (swipeDirection.equals("RTL") && tabPosition > 0) {
            tabPosition--;
            mViewPager.setCurrentItem(tabPosition);
        } else if (swipeDirection.equals("RTL") && tabPosition == 0) {
            mViewPager.setCurrentItem(tabPosition);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (!isFromEdit) {
                return ViewPagerManager.Pager(0, (JsonObject) questionsArray.get(position), null, position + 1, questionsArray.size(), false);
            } else {
                JsonObject ansObject = new JsonParser().parse(DatabaseManager.getInstance(getApplicationContext()).getData(form_id)).getAsJsonObject();
                return ViewPagerManager.Pager(form_id, (JsonObject) questionsArray.get(position), ansObject, qusPosition, questionsArray.size(), true);
            }
        }

        @Override
        public int getCount() {
            return questionsArray.size();
        }
    }
}
