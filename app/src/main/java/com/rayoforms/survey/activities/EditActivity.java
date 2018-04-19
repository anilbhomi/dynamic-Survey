package com.rayoforms.survey.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.adapters.EditAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private RecyclerView rvQuestionAnswer;

    private int form_id;
    private int survey_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        form_id = getIntent().getIntExtra("form_id", 0);
        survey_id = getIntent().getIntExtra("survey_id", 0);
        rvQuestionAnswer = findViewById(R.id.rv_question_answer);
        rvQuestionAnswer.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    private void getData() {
        JsonObject answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(this).getData(survey_id)).getAsJsonObject();
        JsonArray questionArray = (JsonArray) new JsonParser().parse(DatabaseManager.getInstance(this).getQuestions(form_id)).getAsJsonArray();
        ArrayList<String> answerList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> e : answerObject.entrySet()) {
            answerList.add(answerObject.get(e.getKey()).getAsString());
        }
        RecyclerView.Adapter adapter = new EditAdapter(form_id, survey_id, questionArray, answerList, this);
        rvQuestionAnswer.setAdapter(adapter);
    }
}
