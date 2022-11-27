package com.example.firebasebookapp_authen.Adapters;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

//// an Application Class runs before the Luncher Activity
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /// Create static string convert time to time format so, we can call it everytime on the app
    public  static final String formatTimestamp(long timestamp) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        /// format timestamp to dd/MM/yyyy;
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return  date;

    }

}
