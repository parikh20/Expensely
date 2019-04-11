package team16.cs307.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.SetOptions;

import android.widget.AdapterView.OnItemSelectedListener;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.lang.reflect.Array;
import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_expense);


        mLocation = findViewById(R.id.custom_expense_location);
        mName = findViewById(R.id.custom_expense_name);
        mRepeating = findViewById(R.id.custom_expense_repeating);
        mFinish = findViewById(R.id.custom_expense_finish);
        mFrequency = findViewById(R.id.custom_expense_frequency);
        mAmount = findViewById(R.id.custom_expense_amount);
        mCategory = findViewById(R.id.custom_expense_category);
        mPriority = findViewById(R.id.custom_expense_priority);
        mOutlier = findViewById(R.id.custom_expense_outlier);
        tags = new ArrayList<>();

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
                String name = mName.getText().toString();
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
                long time = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
                Expense e = new Expense(name, location, repeating.equals("Y"), time, amount, tags, priority, outlierM, outlierW);
                db.collection("users").document(mAuth.getUid()).collection("Expenses").document(name).set(e);


                    //TODO: update total monthly/weekly/yearly, update category totals m/y/w



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




                //TODO: update total monthly/weekly/yearly, update category totals m/y/w

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
