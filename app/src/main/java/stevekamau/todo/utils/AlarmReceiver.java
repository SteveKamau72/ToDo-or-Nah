package stevekamau.todo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by steve on 10/18/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        new NotificationUtils(context).showNotification(title, message);

        /*ToDoDB toDoDB = new ToDoDB(context);
        if (toDoDB.getTodayToDoItems("active").size() > 0) {
            if (title.equals(context.getString(R.string.morning_welcome_title))) {
                new NotificationUtils(context).showNotification(title, "You have "
                        + toDoDB.getTodayToDoItems("active").size() + " todos for today");
            } else if (title.equals(context.getString(R.string.done_with_day_title))) {
                new NotificationUtils(context).showNotification(context.getString(R.string.done_with_day_title), "Tick off done todos");
            }
        } else {
            new NotificationUtils(context).showNotification(title, message);
        }*/
    }
}