package com.rayoforms.survey.modals;

import io.realm.RealmObject;

/**
 * Created by anil on 4/13/18.
 */

public class DataModel extends RealmObject {
    public int form_id;
    public int id;
    public String data;
    public String remote_data;



    @Override
    public String toString() {
        return "DataModel{" +
                "id=" + id +
                "form_id=" + form_id +
                ", data='" + data + '\'' +
                ", remote_data='" + remote_data + '\'' +
                '}';
    }


    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }

    public String getRemote_data() {
        return remote_data;
    }

    public void setRemote_data(String remote_data) {
        this.remote_data = remote_data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
