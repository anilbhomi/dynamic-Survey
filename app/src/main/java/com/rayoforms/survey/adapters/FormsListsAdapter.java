package com.rayoforms.survey.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rayoforms.survey.modals.DataModel;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.activities.EditActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * bind filled forms details data set to views that are displayed within recycler view
 */
public class FormsListsAdapter extends RecyclerView.Adapter<FormsListsAdapter.ViewHolder> {
    private List<DataModel> list;
    private Context context;

    public FormsListsAdapter(List<DataModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FormsListsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_forms_list, parent, false);
        return new FormsListsAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FormsListsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final DataModel listItem = list.get(position);
        holder.tvTitle.setText("Survey ID : " + listItem.getId());
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, EditActivity.class);
                in.putExtra("id", listItem.getId());
                context.startActivity(in);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DatabaseManager.getInstance(context).deleteRecord(listItem.getId())) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,list.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private CircleImageView ivEdit;
        private CircleImageView ivDelete;

        private ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}

