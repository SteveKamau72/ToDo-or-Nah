package stevekamau.todo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import stevekamau.todo.utils.NotificationUtils;

/**
 * Created by steve on 10/18/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone
        String todo = intent.getStringExtra("todo");
        new NotificationUtils(context).showNotification(todo);
    }
}