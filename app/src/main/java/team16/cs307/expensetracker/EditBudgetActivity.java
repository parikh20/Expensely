package team16.cs307.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class EditBudgetActivity extends AppCompatActivity {

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
    private Budget curr_budg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);
        mName = (EditText) findViewById(R.id.edit_budget_name);
        mMonthly = (EditText) findViewById(R.id.edit_budget_monthly);
        mWeekly = (EditText) findViewById(R.id.edit_budget_weekly);
        mYearly = (EditText) findViewById(R.id.edit_budget_yearly);
        mFinish = (Button) findViewById(R.id.edit_budget_finish);
        mAddLimit = (Button) findViewById(R.id.edit_budget_add_limit);
        mLimits = (TextView) findViewById(R.id.edit_budget_current_limits);
        limits = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //get current budget
        //set starting values of text fields to current budget values
        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                curr_budg = documentSnapshot.toObject(Budget.class);
                if (curr_budg != null) {
                    Toast.makeText(EditBudgetActivity.this, "found a current budget: " + curr_budg.getName(), Toast.LENGTH_SHORT).show();
                    //setting quick stat block here
                    mName.setText(curr_budg.getName());
                    mMonthly.setText(String.valueOf(curr_budg.getLimitMonthly()));
                    mWeekly.setText(String.valueOf(curr_budg.getLimitWeekly()));
                    mYearly.setText(String.valueOf(curr_budg.getLimitYearly()));
                    //add all already created limits to the limit tracker
                    for (Limit l : curr_budg.getCustomLimits()) {
                        limits.add(l);
                    }
                    //add limits to limit list
                    String listLimits = "Current Limits: ";
                    for (Limit lim : limits) {
                        listLimits += lim.getCategory();
                        listLimits += ", ";
                    }
                    if (!listLimits.equals("")) {
                        mLimits.setText(listLimits.substring(0, listLimits.lastIndexOf(",")));
                    }


                } else {
                    Toast.makeText(EditBudgetActivity.this, "No current budget", Toast.LENGTH_LONG).show();


                }
            }
        });



        //perform otherwise as a custom budget activity

        mAddLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to custom limit creation with result fetch to pick up new limit and update
                Toast.makeText(EditBudgetActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CustomLimitCreation.class);
                startActivityForResult(intent, 100);

            }
        });
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeekly.getText() == null || mYearly.getText() == null || mMonthly.getText() == null) {
                    Toast.makeText(EditBudgetActivity.this, "Please enter your budget's limits", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditBudgetActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                Budget budget = new Budget(mName.getText().toString(), w, m, y, limits);
                Toast.makeText(EditBudgetActivity.this, "Budget Created", Toast.LENGTH_SHORT).show();

                db.collection("users").document(mAuth.getUid()).collection("Budgets").document(mName.getText().toString()).set(budget);
                /*HashMap<String, Object> map = new HashMap<>();
                map.put("current budget", budget);*/
                db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget").set(budget);

                //restarting main activity to update graphs and quickstats page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void addLimit(Limit l) {
        limits.add(l);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                String type = data.getStringExtra("type");
                String w = data.getStringExtra("w");
                String m = data.getStringExtra("m");
                String y = data.getStringExtra("y");

                Limit l = new Limit(type, Double.parseDouble(w), Double.parseDouble(m), Double.parseDouble(y));
                addLimit(l);
                String listLimits = "Current Limits: ";
                for (Limit lim : limits) {
                    listLimits += lim.getCategory();
                    listLimits += ", ";
                }
                mLimits.setText(listLimits.substring(0, listLimits.lastIndexOf(",")));
            }

        }
    }
    @Override
    public void onBackPressed(){
        onSupportNavigateUp();

    }
}
