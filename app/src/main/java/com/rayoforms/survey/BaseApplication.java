package com.rayoforms.survey;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.rayoforms.survey.Utils.PrefManager;
import com.rayoforms.survey.realm.Migration;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefManager.getInstance().initSharedPreference(this);
        initRealm();
        initStetho();
    }

    /**
     * Initializing {@link Realm}
     */
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(0) // Must be bumped when the schema changes
                .migration(new Migration()) // Migration to run instead of throwing an exception
                .build();
        Realm.compactRealm(config);
        Realm.setDefaultConfiguration(config);
    }

    private void initStetho(){
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}
