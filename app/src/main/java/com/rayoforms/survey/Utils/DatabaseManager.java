package com.rayoforms.survey.Utils;

import android.content.Context;
import android.util.Log;

import com.rayoforms.survey.modals.DataModel;
import com.rayoforms.survey.modals.FormsModel;
import com.rayoforms.survey.modals.QuestionModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DatabaseManager  {
    private Realm realm = null;
    public static DatabaseManager instance;
    private Context context;

    private DatabaseManager(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            return new DatabaseManager(context);
        }
        return instance;
    }

    private void closeRealm() {
        if (realm != null) {
            realm.close();
        }
    }

    private void errorHandling(Exception e) {
        ToastManager.ShowToast(context,Constants.ERROR_REALM,false);
        Log.i("debug",e.toString());
    }

    public void setForms(String data) {
        FormsModel  model;
        try {
            realm.beginTransaction();
            if(checkForms()==1){
                model = realm.where(FormsModel.class).equalTo("id", 1).findFirst();
                model.setData(data);
            }else {
                model = new FormsModel();
                model.setId(1);
                model.setData(data);
                realm.insertOrUpdate(model);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
    }

    private long checkForms(){
        return realm.where(FormsModel.class).equalTo("id",1).count();
    }

    public String getForms(){
        String data ="";
        try {
            realm.beginTransaction();
            FormsModel realmObj = realm.where(FormsModel.class).equalTo("id",1).findFirst();
            data =realmObj.getData();
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
        return data;
    }

    public void setNewData(int formId,int surveyId,String questionArray, String answerObject) {
        try {
            realm.beginTransaction();
            DataModel dataModel = new DataModel();
            dataModel.setId(surveyId);
            dataModel.setData(answerObject);
            dataModel.setForm_id(formId);
            dataModel.setQuestion(questionArray);
            realm.insertOrUpdate(dataModel);
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
    }

    public String getData(int id) {
        String data = "";
        try {
            realm.beginTransaction();
            DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
            DataModel model = realm.copyFromRealm(dataModel);
            data = model.getData();
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
        return data;
    }

    public void setData(int id, String data) {
        try {
            realm.beginTransaction();
            DataModel dataModel = realm.where(DataModel.class).equalTo("id", id).findFirst();
            dataModel.setData(data);
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
    }

    public long checkQuestion(int id){
        return realm.where(QuestionModel.class).equalTo("id",id).count();
    }

    public void setQuestion(int id, String data) {
        try {
            realm.beginTransaction();
            QuestionModel model = new QuestionModel();
            model.setId(id);
            model.setData(data);
            realm.insertOrUpdate(model);
            realm.commitTransaction();
            ToastManager.ShowToast(context,Constants.SUCCESS_MESSAGE,false);
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
    }

    public String getQuestions(int id) {
        String data = "";
        try {
            realm.beginTransaction();
            QuestionModel dataModel = realm.where(QuestionModel.class).equalTo("id", id).findFirst();
            QuestionModel model = realm.copyFromRealm(dataModel);
            data = model.getData();
            realm.commitTransaction();
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
        return data;
    }

    public Boolean deleteRecord(int id) {
        Boolean status;
        try {
            realm.beginTransaction();
            RealmResults<DataModel> models = realm.where(DataModel.class).equalTo("id", id).findAll();
            models.deleteAllFromRealm();
            realm.commitTransaction();
            status = true;
        } catch (Exception e) {
            status = false;
            errorHandling(e);
        } finally {
            closeRealm();
        }
        return status;
    }

    public List<DataModel> getFilledData(int form_id) {
        List<DataModel> list = new ArrayList<>();
        try {
            RealmResults<DataModel> realmObj = realm.where(DataModel.class).equalTo("form_id",form_id).findAll();
            list = realm.copyFromRealm(realmObj);
        } catch (Exception e) {
            errorHandling(e);
        } finally {
            closeRealm();
        }
        return list;
    }
}
