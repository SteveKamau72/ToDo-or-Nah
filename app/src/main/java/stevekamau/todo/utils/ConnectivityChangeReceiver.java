package stevekamau.todo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.ExecutionException;

/**
 * Created by steve on 4/19/17.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            // Send here
            new Thread(new Runnable() {
                public void run() {
                    VersionChecker versionChecker = new VersionChecker(context);
                    try {
                        String latestVersion = versionChecker.execute().get();
                        PackageManager manager = context.getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(
                                    context.getPackageName(), 0);
                            String version = info.versionName;
                            if (latestVersion != null) {
                                if (latestVersion.equals(version)) {
                                    updateStatus(context, false);
                                } else {
                                    updateStatus(context, true);
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            // Do nothing or notify user somehow
        }
    }

    private void updateStatus(Context context, Boolean newVersion) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences("general_settings", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("new_update", newVersion);
        editor.apply();
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}