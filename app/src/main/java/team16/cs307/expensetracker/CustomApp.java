package team16.cs307.expensetracker;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class CustomApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}