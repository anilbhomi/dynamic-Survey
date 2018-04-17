package com.rayoforms.survey.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.adapters.EditAdapter;

import java.util.ArrayList;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private RecyclerView rvQuestionAnswer;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        id = getIntent().getIntExtra("id",0);
        rvQuestionAnswer = findViewById(R.id.rv_question_answer);
        rvQuestionAnswer.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    private void getData() {
        JsonObject answerObject = (JsonObject) new JsonParser().parse(DatabaseManager.getInstance(this).getData(id)).getAsJsonObject();
        ArrayList<String> questionList = new ArrayList<>();
        ArrayList<String> answerList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> e : answerObject.entrySet()) {
            questionList.add(e.getKey());
            answerList.add(answerObject.get(e.getKey()).getAsString());
        }
        RecyclerView.Adapter adapter = new EditAdapter(id,questionList,answerList, this);
        rvQuestionAnswer.setAdapter(adapter);
    }
}
