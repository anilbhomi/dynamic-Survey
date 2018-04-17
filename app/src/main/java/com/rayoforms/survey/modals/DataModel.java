package com.rayoforms.survey.modals;

import io.realm.RealmObject;

/**
 * Created by anil on 4/13/18.
 */

public class DataModel extends RealmObject {
    public int form_id;
    public int id;
    public String data;
    public String question;

    @Override
    public String toString() {
        return "DataModel{" +
                "id=" + id +
                "form_id=" + form_id +
                ", data='" + data + '\'' +
                ", question='" + question + '\'' +
                '}';
    }


    public int getForm_id() {
        return form_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
