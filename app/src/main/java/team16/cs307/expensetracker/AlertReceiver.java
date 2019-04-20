package team16.cs307.expensetracker;

import android.app.AlarmManager;
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
import com.google.firebase.firestore.SetOptions;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Year;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


//ALERTRECEIVER: to be used for Budget daily notifications
public class AlertReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("REceived");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mAuth.getUid() == null ) {
            //not logged in yet
            return;
        }
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            final Context c = context;
            DocumentReference ref = db.collection("users").document(mAuth.getUid());
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.get("alertsTurnedOff") != null && documentSnapshot.getString("alertsTurnedOff").equals("false")) {
                        Map<String, String> alerts = new HashMap<>();
                        alerts.put("alertsSetUp", "false");
                        db.collection("users").document(mAuth.getUid()).set(alerts, SetOptions.merge());
                    }


                    LoginActivity.alertSet(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance(),c,(AlarmManager)c.getSystemService(Context.ALARM_SERVICE));
                }
            });
            return;
        }
        final Context fcontext = context;
        final Intent fintent = intent;
        if(intent.getStringExtra("expense") != null) {




            final String name = intent.getStringExtra("expense");
            System.out.println("Looking at an expense" + name);
            DocumentReference ref =  db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs");
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    IDTracker ids = documentSnapshot.toObject(IDTracker.class);
                    if (ids == null) {
                        //not supposed to be getting an alert, user has no pending expenses
                        return;
                    }
                    String title = "Remember to pay " + name + " today!";
                    String content = "Find more info in the Expensely app!";
                    final NotificationManager notificationManager = (NotificationManager) fcontext.getSystemService(Context.NOTIFICATION_SERVICE);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                            notificationManager.getNotificationChannel("ExpenseAlerts: " + name) == null) {
                        System.out.println("CORRECTING FOR OREO AND ABOVE");
                        notificationManager.createNotificationChannel(new NotificationChannel(name,
                                "ExpenseChannel", NotificationManager.IMPORTANCE_DEFAULT));


                    }
                    //not using below line because we're creating a custom notification here, with up to date data
                    //Notification n = fintent.getParcelableExtra(NOTIFICATION);
                    Notification n;

                    Intent ExRedirect = new Intent(fcontext, LoginActivity.class);
                    PendingIntent mainIntent = PendingIntent.getActivity(fcontext, Integer.valueOf(ids.IDindexOf(name)), ExRedirect, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(fcontext, name);
                    builder.setContentTitle(title);
                    builder.setContentText(content);
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                    builder.setSmallIcon(R.drawable.ic_launcher_background);
                    builder.setContentIntent(mainIntent);
                    builder.setAutoCancel(true);
                    n = builder.build();
                    final int id = fintent.getIntExtra(NOTIFICATION_ID, 0);
                    System.out.println("ABOUT TO NOTIFY " + name);
                    final Notification fn = n;
                    DocumentReference exemptCheck = db.collection("users").document(mAuth.getUid());
                    exemptCheck.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("expenseAlertsTurnedOff") != null && documentSnapshot.getString("expenseAlertsTurnedOff").equals("false")
                                && (documentSnapshot.get("EmailEnabled") == null || documentSnapshot.getString("EmailEnabled").equals("false"))
                                && (documentSnapshot.get("SMSEnabled") == null || documentSnapshot.getString("SMSEnabled").equals("false"))) {
                                notificationManager.notify(id, fn);
                            }



                        }
                    });







                    if (name != null && ids != null) {
                        ids.remove(name);
                        db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs").set(ids);
                        DocumentReference refAu = db.collection("users").document(mAuth.getUid());
                        refAu.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.get("Monthly Total") != null);
                                    final double total = Double.valueOf(documentSnapshot.getString("Monthly Total"));
                                    DocumentReference refEx = db.collection("users").document(mAuth.getUid()).collection("Expenses").document(name);
                                    refEx.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshotEx) {
                                            Expense e = documentSnapshotEx.toObject(Expense.class);
                                            final double newTotal = total + e.getAmount();
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("Monthly Total", String.valueOf(newTotal));
                                            db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                                        }
                                    });


                            }
                        });


                    }



                }
            });
            return;






        }




        System.out.println("triggered!!!!!!!!!!!!!!!!!!!!");
        //called when alarm is triggered

        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String content = "";
                String title = "";
                final Budget currBudget = documentSnapshot.toObject(Budget.class);
                if (currBudget == null) {
                    title = "You don't have an Expensely budget selected";
                    content = "Create or select a budget in the Expensely app and begin monitoring your expenses";
                    final NotificationManager notificationManager = (NotificationManager) fcontext.getSystemService(Context.NOTIFICATION_SERVICE);


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
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                    builder.setSmallIcon(R.drawable.ic_launcher_background);
                    builder.setContentIntent(mainIntent);
                    builder.setAutoCancel(true);
                    n = builder.build();
                    final int id = fintent.getIntExtra(NOTIFICATION_ID, 0);

                    final Notification fn = n;
                    DocumentReference exemptCheck = db.collection("users").document(mAuth.getUid());
                    exemptCheck.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("alertsTurnedOff") != null && documentSnapshot.getString("alertsTurnedOff").equals("false")) {
                                notificationManager.notify(id, fn);
                            }



                        }
                    });

                } else {

                    DocumentReference ref = db.collection("users").document(mAuth.getUid());
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            NumberFormat formatter = NumberFormat.getCurrencyInstance();
                            String Etitle = "You're [under/over] budget";
                            String Econtent = "You should have spent [budgetpace] by now for your monthly budget [budgetname], but you've spent [amount].  Find more details in the Expensely app.";
                            double monthlyTotal = 0;
                            if (documentSnapshot.get("Monthly Total") != null) {
                                monthlyTotal = Double.valueOf(documentSnapshot.getString("Monthly Total"));
                            }
                            int numDays = LocalDateTime.now().getDayOfMonth();
                            double budgetPerDay = (currBudget.getLimitMonthly() / LocalDateTime.now().getMonth().length( Year.of(LocalDateTime.now().getYear()).isLeap()));
                            double pace = numDays * budgetPerDay;
                            if (pace < monthlyTotal) {
                                Etitle = "You're over budget";
                                Econtent = "You should have spent " + formatter.format(pace) + " by now for your monthly budget \"" + currBudget.getName() + "\", but you've spent " + formatter.format(monthlyTotal) + ".\nFind more details in the Expensely app.";
                            } else {
                                Etitle = "You're under budget";
                                Econtent = "You should have spent " + formatter.format(pace) + " by now for your monthly budget \"" + currBudget.getName() + "\", but you've only spent " + formatter.format(monthlyTotal) + ".\nWell done budgeting!  \nCheck out your progress in the Expensely app.";
                            }





                            final NotificationManager notificationManager = (NotificationManager) fcontext.getSystemService(Context.NOTIFICATION_SERVICE);


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
                            builder.setContentTitle(Etitle);
                            builder.setContentText(Econtent);
                            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Econtent));
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setContentIntent(mainIntent);
                            builder.setAutoCancel(true);
                            n = builder.build();
                            final int id = fintent.getIntExtra(NOTIFICATION_ID, 0);


                            final Notification fn = n;
                            DocumentReference exemptCheck = db.collection("users").document(mAuth.getUid());
                            exemptCheck.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.get("alertsTurnedOff") != null && documentSnapshot.getString("alertsTurnedOff").equals("false")
                                            && (documentSnapshot.get("EmailEnabled") == null || documentSnapshot.getString("EmailEnabled").equals("false"))
                                            && (documentSnapshot.get("SMSEnabled") == null || documentSnapshot.getString("SMSEnabled").equals("false"))) {
                                        notificationManager.notify(id, fn);
                                    }



                                }
                            });
                        }
                    });

                }



            }
        });
        }
}
