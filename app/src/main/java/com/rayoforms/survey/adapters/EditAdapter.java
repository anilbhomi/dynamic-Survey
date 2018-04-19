package com.rayoforms.survey.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.activities.FillFormActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by anil on 4/13/18.
 */

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {
    private Context context;
    private JsonArray questionArray;
    private ArrayList<String> answerList;
    private int form_id;
    private int survey_id;
    private StringBuffer buffer;

    public EditAdapter(int form_id, int survey_id, JsonArray questionArray, ArrayList<String> answerList, Context context) {
        this.survey_id = survey_id;
        this.form_id = form_id;
        this.context = context;
        this.questionArray = questionArray;
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public EditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_edit, parent, false);
        return new EditAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(EditAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvQus.setText(questionArray.get(position).getAsJsonObject().get("label").getAsString());
        String ansText = answerList.get(position);
        String type = questionArray.get(position).getAsJsonObject().get("type").getAsString();
        JsonObject optionProperties = (JsonObject) new JsonParser().parse(questionArray.get(position).getAsJsonObject().get("properties").getAsString()).getAsJsonObject();
        switch (type) {
            case "checkbox": {
                buffer = new StringBuffer();
                String[] optionArray = optionProperties.get("options").getAsString().split(",");
                for (int i = 0; i < optionArray.length; i++) {
                    try {
                        JSONArray answerJson = new JSONArray(ansText);
                        buffer.append(optionArray[Integer.parseInt(answerJson.getString(i))]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ansText = buffer.toString();
                break;
            }
            case "radio": {
                String[] optionArray = optionProperties.get("options").getAsString().split(",");
                if (!TextUtils.isEmpty(ansText))
                    ansText = optionArray[Integer.parseInt(ansText)];
                else {
                    ansText = "";
                }
                break;
            }
            case "select": {
                String[] optionArray = optionProperties.get("options").getAsString().split(",");
                if (!TextUtils.isEmpty(ansText))
                    if (Integer.parseInt(ansText) == 0) {
                        ansText = "";
                    } else {
                        ansText = optionArray[Integer.parseInt(ansText) - 1];
                    }
                else {
                    ansText = "";
                }
                break;
            }
            case "address": {
                buffer = new StringBuffer();
                try {
                    JSONArray answerJson = new JSONArray(ansText);
                    buffer.append(answerJson.getString(0)).append(",");
                    buffer.append(answerJson.getString(1)).append(",");
                    buffer.append(answerJson.getString(2)).append(",");
                    buffer.append(answerJson.getString(3)).append(",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ansText = buffer.toString();
                break;
            }
            case "multi-input": {
                buffer = new StringBuffer();
                try {
                    JSONArray answerJson = new JSONArray(ansText);
                    for(int i=0;i<answerJson.length();i++){
                        buffer.append(answerJson.getString(i)).append(",");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ansText = buffer.toString();
                break;
            }
        }
        holder.tvAns.setText(ansText);
        holder.clContainner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, FillFormActivity.class);
                in.putExtra("isFromEdit", true);
                in.putExtra("position", position);
                in.putExtra("form_id", form_id);
                in.putExtra("survey_id", survey_id);
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQus;
        private TextView tvAns;
        private ConstraintLayout clContainner;

        private ViewHolder(View itemView) {
            super(itemView);
            tvQus = itemView.findViewById(R.id.tv_qus);
            tvAns = itemView.findViewById(R.id.tv_ans);
            clContainner = itemView.findViewById(R.id.containner);
        }
    }
}

