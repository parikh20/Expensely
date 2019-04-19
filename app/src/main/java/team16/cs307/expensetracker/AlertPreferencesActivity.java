package team16.cs307.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AlertPreferencesActivity extends AppCompatActivity {
    private boolean isExemptExpenses;
    private boolean isExemptBudgets;
    private Button mIntervalChange;
    private EditText mInterval;
    private int interval;
    private Button mBudgetAlerts;
    private Button mExpenseAlerts;
    private boolean setup;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button mFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_preferences);
        mInterval = findViewById(R.id.alert_interval);
        mIntervalChange = findViewById(R.id.alert_update_interval);
        mBudgetAlerts = findViewById(R.id.alert_set_budget);
        mExpenseAlerts = findViewById(R.id.alert_set_expense);
        mFinish = findViewById(R.id.alert_finish);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setup = false;
        DocumentReference ref = db.collection("users").document(mAuth.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("alertsTurnedOff")!= null) {
                    isExemptBudgets = Boolean.parseBoolean(documentSnapshot.getString("alertsTurnedOff"));

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("alertsTurnedOff", "false");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptBudgets = false;
                }
                if (documentSnapshot.get("expenseAlertsTurnedOff")!= null) {
                    isExemptExpenses = Boolean.parseBoolean(documentSnapshot.getString("expenseAlertsTurnedOff"));

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("expenseAlertsTurnedOff", "false");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptExpenses = false;
                }
                if (documentSnapshot.get("budg_interval")!= null) {
                    interval = Integer.valueOf(documentSnapshot.getString("budg_interval"));
                    mInterval.setText(String.valueOf(interval));

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("budg_interval", "1");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    interval = 1;
                    mInterval.setText("1");
                }

                if (isExemptExpenses) {
                    mExpenseAlerts.setText("Turn on Expense Alerts");

                }
                if (isExemptBudgets) {
                    mBudgetAlerts.setText("Turn on Budget Alerts");
                }
                setup = true;

            }
        });


        mIntervalChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setup) {
                    return;
                }
                if (mInterval.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Enter a number of days", Toast.LENGTH_SHORT).show();
                    return;
                }
                interval = Integer.valueOf(mInterval.getText().toString());
                if ( interval < 1 ||  interval > 20) {
                    Toast.makeText(getApplicationContext(), "Enter a valid number of days", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("budg_interval", String.valueOf(interval));

                db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                Toast.makeText(getApplicationContext(), "Interval Changed", Toast.LENGTH_SHORT).show();


            }
        });

        mExpenseAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setup) {
                    return;
                }
                if (isExemptExpenses) {
                    Map<String, String> map = new HashMap<>();
                    map.put("expenseAlertsTurnedOff", "false");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptExpenses = false;
                    mExpenseAlerts.setText("Turn off Expense Alerts");
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("expenseAlertsTurnedOff", "true");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptExpenses = true;
                    mExpenseAlerts.setText("Turn on Expense Alerts");
                }
            }
        });

        mBudgetAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setup) {
                    return;
                }
                if (isExemptBudgets) {
                    Map<String, String> map = new HashMap<>();
                    map.put("alertsTurnedOff", "false");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptBudgets = false;
                    mBudgetAlerts.setText("Turn off Budget Alerts");
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("alertsTurnedOff", "true");
                    db.collection("users").document(mAuth.getUid()).set(map, SetOptions.merge());
                    isExemptBudgets = true;
                    mBudgetAlerts.setText("Turn on Budget Alerts");
                }
            }
        });










        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
