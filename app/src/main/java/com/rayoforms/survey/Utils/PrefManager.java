package com.rayoforms.survey.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static String PREF_NAME = "prefrenceManager";
    public static PrefManager instance;

    private SharedPreferences pref;
    private String ID="id";

    private PrefManager(){

    }

    public static PrefManager getInstance(){
        if(instance == null){
         instance = new PrefManager();
        }
        return instance;
    }

    public void initSharedPreference(Context context){
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor(){
        return pref.edit();
    }

    public void setId(int id){
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(ID,id);
        editor.commit();
    }

    public int getId(){
        return pref.getInt(ID,0);
    }
}
