package com.rayoforms.survey.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rayoforms.survey.R;
import com.rayoforms.survey.activities.FormsListActivity;

/**
 * bind forms details data set to views that are displayed within recycler view
 */
public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.ViewHolder> {
    private JsonArray formsList;
    private Context context;
    public FormsAdapter(JsonArray list, Context context) {
        this.context =context;
        this.formsList = list;
    }

    @NonNull
    @Override
    public FormsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_forms, parent, false);
        return new ViewHolder(v);
    }

    /**
     *
     * @param holder as ViewHolder class instance
     * @param position as position of item in list
     */
    @Override
    public void onBindViewHolder(@NonNull FormsAdapter.ViewHolder holder, final int position) {
        final JsonObject object = formsList.get(position).getAsJsonObject();
        holder.tvTitle.setText(object.get("title").getAsString());
        holder.clForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, FormsListActivity.class);
                in.putExtra("form_id",object.get("id").getAsInt());
                in.putExtra("slug",object.get("slug").getAsString());
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return formsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ConstraintLayout clForms;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            clForms = itemView.findViewById(R.id.cl_form);
        }
    }
}