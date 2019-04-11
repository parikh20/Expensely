package team16.cs307.expensetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("triggered!!!!!!!!!!!!!!!!!!!!");
        //called when alarm is triggered
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);



        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
                notificationManager.getNotificationChannel("BudgetAlert")==null) {
            System.out.println("CORRECTING FOR OREO AND ABOVE");
            notificationManager.createNotificationChannel(new NotificationChannel("BudgetAlert",
                    "BudgetAl", NotificationManager.IMPORTANCE_DEFAULT));
        }
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}
