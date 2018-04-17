package com.rayoforms.survey.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rayoforms.survey.R;
import com.rayoforms.survey.activities.FillFormActivity;

import java.util.ArrayList;

/**
 * Created by anil on 4/13/18.
 */

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> questionList;
    private ArrayList<String> answerList;
    private int id;

    public EditAdapter(int id, ArrayList<String> questionList, ArrayList<String> answerList, Context context) {
        this.id=id;
        this.context = context;
        this.questionList = questionList;
        this.answerList = answerList;
    }

    @Override
    public EditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_edit, parent, false);
        return new EditAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(EditAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvQus.setText(questionList.get(position));
        holder.tvAns.setText(answerList.get(position));
        holder.clContainner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context,FillFormActivity.class);
                in.putExtra("isFromEdit",true);
                in.putExtra("position",position);
                in.putExtra("id",id);
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

