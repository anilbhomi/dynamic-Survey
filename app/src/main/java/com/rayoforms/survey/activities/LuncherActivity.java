package com.rayoforms.survey.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import com.google.gson.JsonParser;
import com.rayoforms.survey.R;
import com.rayoforms.survey.Utils.Constants;
import com.rayoforms.survey.Utils.DatabaseManager;
import com.rayoforms.survey.Utils.ToastManager;
import com.rayoforms.survey.adapters.FormsAdapter;

import org.json.JSONArray;

/**
 * An Activity to list out the available projects and forms.
 * {@link LuncherActivity#retriveFormsList()} to retrive available forms from server.
 */
public class LuncherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String URL = "http://192.168.1.10:8000/api/forms";

    private RecyclerView rvFormsList;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        rvFormsList = findViewById(R.id.rv_forms_list);
        rvFormsList.setLayoutManager(new LinearLayoutManager(this));
        rvFormsList.setItemAnimator(new DefaultItemAnimator());

        if (DatabaseManager.getInstance(this).checkForms() == 1) {
            readForms();
        } else {
            retriveFormsList();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * function to retrive available forms from server.
     * if success store forms to database and read forms by {@link LuncherActivity#readForms()}
     */
    private void retriveFormsList() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        DatabaseManager.getInstance(LuncherActivity.this).setForms(String.valueOf(jsonArray));
                        readForms();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            ToastManager.ShowToast(LuncherActivity.this, Constants.ERROR_NO_CONNECTION, true);
                        } else if (error instanceof TimeoutError) {
                            ToastManager.ShowToast(LuncherActivity.this, Constants.ERROR_TIMEOUT, true);
                        } else if (error instanceof ServerError) {
                            ToastManager.ShowToast(LuncherActivity.this, Constants.ERROR_SERVER, true);
                        } else if (error instanceof NetworkError) {
                            ToastManager.ShowToast(LuncherActivity.this, Constants.ERROR_NETWORK, true);
                        }
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * function to retrive forms from database and set Adapter to recyclerview. {@link FormsAdapter}
     */
    private void readForms() {
        JsonArray formsArray = (JsonArray) new JsonParser().parse(DatabaseManager.getInstance(this).getForms()).getAsJsonArray();
        adapter = new FormsAdapter(formsArray, this);
        rvFormsList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.luncher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
