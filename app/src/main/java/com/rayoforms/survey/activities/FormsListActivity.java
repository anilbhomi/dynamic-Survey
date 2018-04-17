package com.rayoforms.survey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;;import com.rayoforms.survey.modals.DataModel;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.adapters.FormsListsAdapter;

import java.util.List;

/**
 * An activity to display filled up forms {@link FormsListActivity#readRealm()}
 * allow user to fill new form {@link FormsListActivity#setNewForm()}
 * */
public class FormsListActivity extends AppCompatActivity {

    private RecyclerView rvFormsList;
    private FloatingActionButton fab;
    private int form_id;
    private String slug;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lists);
        setTitle("Form Lists");

        form_id = getIntent().getIntExtra("form_id",0);
        slug = getIntent().getStringExtra("slug");

        fab = findViewById(R.id.fab);
        rvFormsList = findViewById(R.id.rv_forms_list);
        rvFormsList.setLayoutManager(new LinearLayoutManager(this));
        rvFormsList.setItemAnimator(new DefaultItemAnimator());

        readRealm();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setNewForm();
            }
        });

        rvFormsList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0 ) {
                    fab.show();
                }
            }
        });
    }

    /**
     * read filled forms from database and set adapter
     */
    public void readRealm() {
        List<DataModel> listItems = DatabaseManager.getInstance(this).getFilledData(form_id);
        RecyclerView.Adapter adapter = new FormsListsAdapter(listItems, this);
        rvFormsList.setAdapter(adapter);
    }

    /**
     * function to fill new forms
     */
    public void setNewForm() {
        Intent intent = new Intent(FormsListActivity.this,FillFormActivity.class);
        intent.putExtra("form_id",form_id);
        intent.putExtra("slug",slug);
        startActivity(intent);
    }
}
