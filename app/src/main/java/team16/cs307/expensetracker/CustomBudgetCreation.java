package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;

public class CustomBudgetCreation extends AppCompatActivity {

    private EditText mName;
    private EditText mMonthly;
    private EditText mWeekly;
    private EditText mYearly;
    private TextView mLimits;
    private Button mFinish;
    private Button mAddLimit;
    private ArrayList<Limit> limits;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_budget_creation);
        mName = (EditText) findViewById(R.id.custom_budget_name);
        mMonthly = (EditText) findViewById(R.id.custom_budget_monthly);
        mWeekly = (EditText) findViewById(R.id.custom_budget_weekly);
        mYearly = (EditText) findViewById(R.id.custom_budget_yearly);
        mFinish = (Button) findViewById(R.id.custom_budget_finish);
        mAddLimit = (Button) findViewById(R.id.custom_budget_add_limit);
        mLimits = (TextView) findViewById(R.id.custom_budget_current_limits);
        limits = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeekly.getText() == null || mYearly.getText() == null || mMonthly.getText() == null) {
                    Toast.makeText(CustomBudgetCreation.this, "Please enter your budget's limits", Toast.LENGTH_SHORT).show();
                    return;
                }
                String weekly = mWeekly.getText().toString();
                String yearly = mYearly.getText().toString();
                String monthly = mMonthly.getText().toString();
                double w, m, y;
                try
                {
                    w = Double.parseDouble(weekly);
                    m = Double.parseDouble(monthly);
                    y = Double.parseDouble(yearly);
                }
                catch(NumberFormatException nfe)
                {
                   Toast.makeText(CustomBudgetCreation.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                Budget budget = new Budget(mName.getText().toString(), w, m, y, limits);
                Toast.makeText(CustomBudgetCreation.this, "Budget Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                db.collection("users").document(mAuth.getUid()).collection("Budgets").document(mName.getText().toString()).set(budget);
                /*HashMap<String, Object> map = new HashMap<>();
                map.put("current budget", budget);*/
                db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget").set(budget);
                finish();
            }
        });

    }
    public void addLimit(Limit l) {
        limits.add(l);
    }
}
