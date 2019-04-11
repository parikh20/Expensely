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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//ALERTRECEIVER: to be used for Budget daily notifications
public class AlertReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        System.out.println("triggered!!!!!!!!!!!!!!!!!!!!");
        //called when alarm is triggered
        final Context fcontext = context;
        final Intent fintent = intent;
        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String content = "";
                String title = "";
                Budget currBudget = documentSnapshot.toObject(Budget.class);
                if (currBudget == null) {
                    title = "You don't have an Expensely budget selected";
                    content = "Create or select a budget in the Expensely app and begin monitoring your expenses";
                } else {

                }

                NotificationManager notificationManager = (NotificationManager) fcontext.getSystemService(Context.NOTIFICATION_SERVICE);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                        notificationManager.getNotificationChannel("BudgetAlert") == null) {
                    System.out.println("CORRECTING FOR OREO AND ABOVE");
                    notificationManager.createNotificationChannel(new NotificationChannel("BudgetAlert",
                            "BudgetAl", NotificationManager.IMPORTANCE_DEFAULT));
                }
                //not using below line because we're creating a custom notification here, with up to date data
                //Notification notification = intent.getParcelableExtra(NOTIFICATION);
                Notification n;

                Intent budgRedirect = new Intent(fcontext, LoginActivity.class);
                PendingIntent mainIntent = PendingIntent.getActivity(fcontext, 1, budgRedirect, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(fcontext, "BudgetAlert");
                builder.setContentTitle(title);
                builder.setContentText(content);
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setContentIntent(mainIntent);
                builder.setAutoCancel(true);
                n = builder.build();
                int id = fintent.getIntExtra(NOTIFICATION_ID, 0);
                notificationManager.notify(id, n);

            }
        });
        }
}
