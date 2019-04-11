package team16.cs307.expensetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
        //not using below line because we're creating a custom notification here, with up to date data
        //Notification notification = intent.getParcelableExtra(NOTIFICATION);
        Notification n;

        Intent budgRedirect = new Intent(context, LoginActivity.class);
        PendingIntent mainIntent = PendingIntent.getActivity(context, 1, budgRedirect, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "BudgetAlert");
        builder.setContentTitle("Budget Checkup BUT CHANGED");
        builder.setContentText("placeholder info about budget here BUT CHANGED.");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentIntent(mainIntent);
        builder.setAutoCancel(true);
        n = builder.build();
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, n);
    }
}
