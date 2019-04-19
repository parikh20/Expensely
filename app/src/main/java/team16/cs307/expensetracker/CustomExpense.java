package team16.cs307.expensetracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import android.widget.AdapterView.OnItemSelectedListener;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.MONTHS;
import static org.threeten.bp.temporal.ChronoUnit.WEEKS;
import static org.threeten.bp.temporal.ChronoUnit.YEARS;

public class CustomExpense extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText mLocation;
    private EditText mName;
    private EditText mRepeating;
    private Button mFinish;
    private EditText mFrequency;
    private EditText mAmount;
    private Button mCategory;
    private Spinner mPriority;
    private Spinner mOutlier;
    private int priority;
    private int outlier;
    private ArrayList<String> tags;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DatePicker mDate;
    private TimePicker mTime;
    private Button mSetTime;
    private boolean timeChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_expense);

        timeChanged = false;
        mLocation = findViewById(R.id.custom_expense_location);
        mDate = findViewById(R.id.simpleDatePicker);
        mSetTime = findViewById(R.id.custom_expense_setDate);
        mName = findViewById(R.id.custom_expense_name);
        mRepeating = findViewById(R.id.custom_expense_repeating);
        mFinish = findViewById(R.id.custom_expense_finish);
        mFrequency = findViewById(R.id.custom_expense_frequency);
        mAmount = findViewById(R.id.custom_expense_amount);
        mCategory = findViewById(R.id.custom_expense_category);
        mPriority = findViewById(R.id.custom_expense_priority);
        mOutlier = findViewById(R.id.custom_expense_outlier);
        tags = new ArrayList<>();

        mDate.setEnabled(false);
        mDate.setVisibility(View.INVISIBLE);


        mOutlier.setOnItemSelectedListener(this);

        mPriority.setOnItemSelectedListener(this);



        priority = 0;
        outlier = 0;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        List<String> priorities = new ArrayList<String>();
        priorities.add("How important was this purchase?");
        priorities.add("1 (Splurge, Not Important");
        priorities.add("2");
        priorities.add("3");
        priorities.add("4");
        priorities.add("5");
        priorities.add("6");
        priorities.add("7");
        priorities.add("8");
        priorities.add("9");
        priorities.add("10 (Very Important!)");
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorities);
        mPriority.setAdapter(priorityAdapter);

        List<String> outliers = new ArrayList<String>();
        outliers.add("Was this purchase an outlier?");
        outliers.add("Yes, do not count this purchase in weekly totals");
        outliers.add("Yes, do not count this purchase in monthly totals");
        outliers.add("No");
        ArrayAdapter<String> outlierAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, outliers);
        mOutlier.setAdapter(outlierAdapter);


        mSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDate.isEnabled()) {
                    timeChanged = true;
                    mLocation.setVisibility(View.INVISIBLE);
                    mDate.setVisibility(View.VISIBLE);
                    //mSetTime = findViewById(R.id.custom_expense_setDate);
                    mName.setVisibility(View.INVISIBLE);
                    mRepeating.setVisibility(View.INVISIBLE);
                    mFinish.setVisibility(View.INVISIBLE);
                    mFrequency.setVisibility(View.INVISIBLE);
                    mAmount.setVisibility(View.INVISIBLE);
                    mCategory.setVisibility(View.INVISIBLE);
                    mPriority.setVisibility(View.INVISIBLE);
                    mOutlier.setVisibility(View.INVISIBLE);
                    mDate.setEnabled(true);


                } else {
                    mDate.setEnabled(false);
                    mLocation.setVisibility(View.VISIBLE);
                    mDate.setVisibility(View.INVISIBLE);
                    //mSetTime = findViewById(R.id.custom_expense_setDate);
                    mName.setVisibility(View.VISIBLE);
                    mRepeating.setVisibility(View.VISIBLE);
                    mFinish.setVisibility(View.VISIBLE);
                    mFrequency.setVisibility(View.VISIBLE);
                    mAmount.setVisibility(View.VISIBLE);
                    mCategory.setVisibility(View.VISIBLE);
                    mPriority.setVisibility(View.VISIBLE);
                    mOutlier.setVisibility(View.VISIBLE);
                }
            }
        });



        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mName.getText() == null || mRepeating.getText() == null || mAmount.getText() == null || mLocation.getText() == null) {
                    Toast.makeText(CustomExpense.this, "Please enter your purchase's values", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(priority == 0) {
                    Toast.makeText(CustomExpense.this, "Please select your purchase's priority", Toast.LENGTH_SHORT).show();
                    return;
                }
                long time = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
                if (timeChanged) {
                    int month = mDate.getMonth() + 1; //We need to add one because DatePicker starts at 0 with months (all hail lack of consistency in java packages)
                    int year = mDate.getYear();
                    int day = mDate.getDayOfMonth();
                    time = LocalDateTime.of(year, month, day, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute()).atZone(ZoneId.systemDefault()).toEpochSecond();



                }

                final String name = mName.getText().toString();
                String location = mLocation.getText().toString();
                String repeating = mRepeating.getText().toString();
                final double amount = Double.parseDouble(mAmount.getText().toString());
                boolean weekly = false;
                boolean monthly = false;
                boolean yearly = false;
                if (repeating.equals("Y")) {
                    if (mFrequency.getText() == null) {
                        Toast.makeText(CustomExpense.this, "Please select your repeating purchase's frequency", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String freq = mFrequency.getText().toString();
                    if (freq.equals("weekly")) {
                        weekly = true;
                    } else if (freq.equals("monthly") ) {
                        monthly = true;
                    } else {
                        yearly = true;
                    }

                }
                boolean outlierW = outlier == 1;
                boolean outlierM = outlier == 2;

                Expense e = new Expense(name, location, repeating.equals("Y"), time, amount, tags, priority, outlierM, outlierW);
                db.collection("users").document(mAuth.getUid()).collection("Expenses").document(name).set(e);


                    //TODO: update total monthly/weekly/yearly, update category totals m/y/w


                if (!outlierM && (!timeChanged || mDate.getMonth() + 1 == LocalDateTime.now().getMonthValue() && mDate.getDayOfMonth() <= LocalDateTime.now().getDayOfMonth()) ) {

                    DocumentReference ref = db.collection("users").document(mAuth.getUid());
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("LastUpdated") != null && documentSnapshot.get("LastUpdated").equals(LocalDateTime.now().getMonth().toString())) {


                                if (documentSnapshot.get("Monthly Total") != null) {
                                    Map<String, String> userinfo = new HashMap<>();
                                    userinfo.put("Monthly Total", String.valueOf(Double.valueOf(documentSnapshot.getString("Monthly Total")) + amount));
                                    userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                                    db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                                } else {
                                    Map<String, String> userinfo = new HashMap<>();
                                    userinfo.put("Monthly Total", String.valueOf(amount));
                                    userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                                    db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                                }
                            } else {
                                Map<String, String> userinfo = new HashMap<>();
                                userinfo.put("Monthly Total", String.valueOf(amount));
                                userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                                db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                            }
                        }
                    });
                }
                final int month = mDate.getMonth() + 1;
                final int day = mDate.getDayOfMonth();
                final int year = mDate.getYear();
                final int currMonth = LocalDateTime.now().getMonthValue() ;
                final int currDay = LocalDateTime.now().getDayOfMonth();
                final int currYear = LocalDateTime.now().getYear();
                if (timeChanged && ((month > currMonth && currYear == year) || (day > currDay && month == currMonth && currYear == year) || year > currYear)) {
                    DocumentReference ref =   db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs");
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            IDTracker tracker = documentSnapshot.toObject(IDTracker.class);
                            int last = 0;
                            if (tracker == null) {

                                tracker = new IDTracker(name);
                                db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs").set(tracker);
                            } else {
                                last = tracker.getLastID();
                                ArrayList<String> ids = tracker.getIDs();
                                System.out.println(ids);
                                if (last == -1) {

                                    //no pending expenses
                                    tracker.addID(name);
                                    //ID is 0
                                    last = 0;
                                    db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs").set(tracker);
                                } else {

                                    //one or more pending expense
                                    tracker.addID(name);
                                    last = tracker.getLastID();
                                    db.collection("users").document(mAuth.getUid()).collection("Preferences").document("PendingExpenseIDs").set(tracker);

                                }


                            }
                            //last now points to the desired broadcast id of the current expense : 0 if it has been just created, or is the only entry, otherwise the index of the entry
                            //now we will send an alert with an extra string "name" containing the expense's name, scheduled for the day of the expense.

                            final Context context = getApplicationContext();
                            ComponentName receiver = new ComponentName(context, AlertReceiver.class);
                            PackageManager pm = context.getPackageManager();

                            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                            Toast.makeText(context, "Setting up expense alert for: " + name, Toast.LENGTH_SHORT).show();

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent notificationIntent = new Intent(context, AlertReceiver.class);
                            notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ID, last + 100);
                            notificationIntent.putExtra("expense", name);
                            Notification n;
                            Intent exRedirect = new Intent(context, LoginActivity.class); //We will send the user to the login activity page directly, to let them see their graphs.
                            PendingIntent mainIntent = PendingIntent.getActivity(context, last + 100, exRedirect, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ExpenseAlerts: " + name);
                            builder.setContentTitle("Expense Triggered");
                            builder.setContentText("placeholder info about Expense here: " + name);
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setContentIntent(mainIntent);
                            builder.setAutoCancel(true);
                            builder.setStyle(new NotificationCompat.BigTextStyle().bigText("placeholder info about Expense here"));


                            n = builder.build();
                            notificationIntent.putExtra(AlertReceiver.NOTIFICATION, n);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, last + 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Calendar cal = Calendar.getInstance();
                            //cal.set(year, month, day, 0, 0, 1);
                            cal.set(2019,3,18,20,13,0);  //TESTING PURPOSES : set specific time
                            final int id = (int) System.currentTimeMillis();
                            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pendingIntent);
                            System.out.println("Set up alarm for " + cal.getTimeInMillis());






                        }
                    });

                }




                //TODO: mark these payments as future payments

                if(weekly) {

                    LocalDateTime nextTime = LocalDateTime.now().plus(7, DAYS);
                    Expense next = new Expense("next " + name, location, repeating.equals("Y"), nextTime.atZone(ZoneId.systemDefault()).toEpochSecond(), amount, tags, priority, outlierM, outlierW);
                    db.collection("users").document(mAuth.getUid()).collection("Expenses").document("next " + name).set(next);

                }
                if(monthly) {

                    LocalDateTime nextTime = LocalDateTime.now().plus(1, MONTHS);
                    Expense next = new Expense("next " + name, location, repeating.equals("Y"), nextTime.atZone(ZoneId.systemDefault()).toEpochSecond(), amount, tags, priority, outlierM, outlierW);
                    db.collection("users").document(mAuth.getUid()).collection("Expenses").document("next " + name).set(next);

                }
                if(yearly) {

                    LocalDateTime nextTime = LocalDateTime.now().plus(1, YEARS);
                    Expense next = new Expense("next " + name, location, repeating.equals("Y"), nextTime.atZone(ZoneId.systemDefault()).toEpochSecond(), amount, tags, priority, outlierM, outlierW);
                    db.collection("users").document(mAuth.getUid()).collection("Expenses").document("next " + name ).set(next);

                }
                Intent intent = new Intent(CustomExpense.this, MainActivity.class);
                CustomExpense.this.startActivity(intent);


                finish();

            }
        });


        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CustomExpense.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CustomCategoryCreation.class);
                startActivityForResult(intent, 100);

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        String item = parent.getItemAtPosition(position).toString();
        if (parent.getCount() > 5) {
            priority = position;
            Toast.makeText(getApplicationContext(), "id = " + priority, Toast.LENGTH_SHORT).show();
        } else {
            outlier = position;
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                String type = data.getStringExtra("type");
                if (!type.equals("")) {
                    tags.add(type);
                }

                /*String listLimits = "Current Limits: ";
                for (Limit lim : limits) {
                    listLimits += lim.getCategory();
                    listLimits += ", ";
                }
                mLimits.setText(listLimits.substring(0, listLimits.lastIndexOf(",")));*/
            }

        }
    }
    
}
