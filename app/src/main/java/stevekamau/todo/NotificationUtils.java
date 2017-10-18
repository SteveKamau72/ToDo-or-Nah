package stevekamau.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by steve on 10/18/17.
 */

public class NotificationUtils {
    Context applicationContext;

    public NotificationUtils(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void showNotification(String todo) {
        NotificationManager manager = (NotificationManager) applicationContext.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(applicationContext, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);
        builder.setAutoCancel(true);
        builder.setTicker("Your todo is up!");
        builder.setContentTitle(applicationContext.getString(R.string.app_name));
        builder.setContentText(todo);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(todo));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        Notification myNotication = builder.getNotification();
        myNotication.defaults |= Notification.DEFAULT_VIBRATE;
        myNotication.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + applicationContext.getPackageName() + "/" + R.raw.new_message);
        myNotication.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);
    }
}
