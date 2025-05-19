package com.example.f1blog;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationJobService extends android.app.job.JobService {
    private static final String TAG = "NotificationJobService";
    private static final String CHANNEL_ID = "F1BlogChannel";


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "F1BlogChannel";
            String description = "Csatorna az F1 blog értesítésekhez";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public boolean onStartJob(android.app.job.JobParameters params) {
        Log.d(TAG, "Job started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "POST_NOTIFICATIONS permission not granted. Skipping notification.");
                jobFinished(params, false);
                return false;
            }
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.f1) // Győződj meg róla, hogy ez a drawable létezik
                .setContentTitle("F1 Blog Emlékeztető")
                .setContentText("Nézd meg a legújabb blogbejegyzéseket!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());

        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(android.app.job.JobParameters params) {
        Log.d(TAG, "Job stopped before completion");
        return true;
    }
}
