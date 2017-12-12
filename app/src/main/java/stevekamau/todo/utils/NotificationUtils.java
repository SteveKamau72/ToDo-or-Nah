package stevekamau.todo.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.Date;

import stevekamau.todo.R;
import stevekamau.todo.activity.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by steve on 10/18/17.
 */

public class NotificationUtils {
    Context applicationContext;

    public NotificationUtils(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void showNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) applicationContext.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(applicationContext, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);
        builder.setAutoCancel(true);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notif);
            builder.setColor(applicationContext.getResources().getColor(R.color.colorPrimary));
        } else {
            builder.setSmallIcon(R.drawable.notif);
        }
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        Notification myNotication = builder.getNotification();
        myNotication.defaults |= Notification.DEFAULT_VIBRATE;
        myNotication.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + applicationContext.getPackageName() + "/" + R.raw.new_message);
        myNotication.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
        int notifID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        manager.notify(notifID, myNotication);
    }

    public void showCustomNofication(String title, String message) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(applicationContext.getPackageName(),
                R.layout.custom_notification);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(applicationContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext);
        // Set Icon
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notif);
            builder.setColor(applicationContext.getResources().getColor(R.color.colorPrimary));
        } else {
            builder.setSmallIcon(R.drawable.notif);
        }
        // Set Ticker Message
        builder.setTicker(title);
        // Dismiss Notification
        builder.setAutoCancel(true);
        // Set PendingIntent into Notification
        builder.setContentIntent(pIntent);
        // Set RemoteViews into Notification
        builder.setContent(remoteViews);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.notif);
//        remoteViews.setImageViewResource(R.id.imagenotiright, R.drawable.ic_add_notif);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) applicationContext.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        assert notificationmanager != null;
        notificationmanager.notify(0, builder.build());
    }
}
