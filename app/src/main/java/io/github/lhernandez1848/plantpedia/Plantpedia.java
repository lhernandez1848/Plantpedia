package io.github.lhernandez1848.plantpedia;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Plantpedia extends Application {

    private final String TAG = "Plantpedia Notification";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Plantpedia", "App started");

        // call method to set alarm
        setAlarm();
    }

    private void setAlarm() {
        Log.d(TAG, "Alarm set-up started.");

        // set time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 30);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.d(TAG,"Alarm set - 9:30AM every day.");
        }
    }
}
