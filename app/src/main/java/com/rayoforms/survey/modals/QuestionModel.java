package com.rayoforms.survey.modals;

import io.realm.RealmObject;

/**
 * Created by anil on 4/16/18.
 */

public class QuestionModel extends RealmObject{
    public int id;
    public String data;

    @Override
    public String toString() {
        return "DataModel{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
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
