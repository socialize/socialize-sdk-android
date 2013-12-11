package com.socialize.testapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import com.socialize.ui.dialog.DialogRegister;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EmptyActivity extends Activity implements DialogRegister {

    private Set<Dialog> dialogs = new HashSet<Dialog>();

    @Override
    public void register(Dialog dialog) {
        dialogs.add(dialog);
    }

    @Override
    public Collection<Dialog> getDialogs() {
        return dialogs;
    }

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);

        // Force async tasks static handler to be created on the main UI thread
        // This becomes an issue if AsyncTasks are created in unit tests.
        try {
            Class.forName("android.os.AsyncTask");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(dialogs != null) {
            for (Dialog dialog : dialogs) {
                try {
                    dialog.dismiss();
                }
                catch (Throwable ignore) {}
            }
            dialogs.clear();
        }
        super.onDestroy();
    }


    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.savedInstanceState = outState;
        super.onSaveInstanceState(outState);
    }
}