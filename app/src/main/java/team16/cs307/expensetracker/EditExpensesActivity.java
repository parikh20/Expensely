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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jjoe64.graphview.series.DataPoint;

import android.widget.AdapterView.OnItemSelectedListener;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.MONTHS;
import static org.threeten.bp.temporal.ChronoUnit.WEEKS;
import static org.threeten.bp.temporal.ChronoUnit.YEARS;

public class EditExpensesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    private EditText mLocation;
    private EditText mName;
    private EditText mRepeating;
    private Button mFinish;

    private EditText mAmount;
    private Button mCategory;
    private Spinner mPriority;
    private Spinner mOutlier;
    private Spinner mExpense;
    private int priority;
    private int outlier;
    private ArrayList<String> tags;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Expense expense;
    private long oldtime;
    private double oldAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenses);
        oldAmount = 0;
        mLocation = findViewById(R.id.custom_expense_location);
        mName = findViewById(R.id.custom_expense_name);
        mRepeating = findViewById(R.id.custom_expense_repeating);
        mFinish = findViewById(R.id.custom_expense_finish);

        mAmount = findViewById(R.id.custom_expense_amount);
        mCategory = findViewById(R.id.custom_expense_category);
        mPriority = findViewById(R.id.custom_expense_priority);
        mOutlier = findViewById(R.id.custom_expense_outlier);
        mExpense = findViewById(R.id.edit_expense_selector);
        mExpense.setOnItemSelectedListener(this);
        mLocation.setVisibility(View.INVISIBLE);
        mName.setVisibility(View.INVISIBLE);
        mRepeating.setVisibility(View.INVISIBLE);
        mFinish.setVisibility(View.INVISIBLE);

        mCategory.setVisibility(View.INVISIBLE);
        mPriority.setVisibility(View.INVISIBLE);
        mOutlier.setVisibility(View.INVISIBLE);
        mAmount.setVisibility(View.INVISIBLE);



        //set to visible and populated when user selects an expense


        tags = new ArrayList<>();

        mOutlier.setOnItemSelectedListener(this);

        mPriority.setOnItemSelectedListener(this);

        priority = 0;
        outlier = 0;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //populate expense spinner from db
        CollectionReference refE = db.collection("users").document(mAuth.getUid()).collection("Expenses");
        refE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                int x = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    x++;
                }

                ArrayList<Expense> ei = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {

                    Expense e = doc.toObject(Expense.class);
                    ei.add(e);

                }
                String[] arraySpinner = new String[x + 1];
                arraySpinner[0] = "Choose Your Expense";
                int itr = 1;
                for (Expense e : ei) {

                    arraySpinner[itr] = e.getName();
                    itr++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mExpense.setAdapter(adapter);
            }
        });


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
                    Toast.makeText(getApplicationContext(), "Please enter your purchase's values", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(priority == 0) {
                    Toast.makeText(getApplicationContext(), "Please select your purchase's priority", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = mName.getText().toString();
                String location = mLocation.getText().toString();
                String repeating = mRepeating.getText().toString();
                final double amount = Double.parseDouble(mAmount.getText().toString());


                boolean outlierW = outlier == 1;
                boolean outlierM = outlier == 2;

                Expense e = new Expense(name, location, repeating.equals("Y"), oldtime, amount, tags, priority, outlierM, outlierW);
                final double difference = amount - oldAmount;
                db.collection("users").document(mAuth.getUid()).collection("Expenses").document(name).set(e);

                if (LocalDateTime.now().getMonth() == LocalDateTime.ofInstant(Instant.ofEpochSecond(oldtime), ZoneId.systemDefault()).getMonth() &&
                        LocalDateTime.now().getYear() == LocalDateTime.ofInstant(Instant.ofEpochSecond(oldtime), ZoneId.systemDefault()).getYear()) {

                    //TODO: update total monthly/weekly/yearly, update category totals m/y/w



                    DocumentReference ref = db.collection("users").document(mAuth.getUid());
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                              if (documentSnapshot.get("Monthly Total") != null) {
                                  Map<String,String> userinfo = new HashMap<>();
                                  userinfo.put("Monthly Total", String.valueOf(Double.valueOf(documentSnapshot.getString("Monthly Total")) + difference));
                                  userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                                  db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                              } else {
                                  Map<String,String> userinfo = new HashMap<>();
                                  userinfo.put("Monthly Total", String.valueOf(amount));
                                  userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                                  db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                              }
                        }
                    });
                } else {
                    System.out.println("New Month");
                    System.out.println(LocalDateTime.now().getMonth());
                    System.out.println(LocalDateTime.ofInstant(Instant.ofEpochSecond(oldtime), ZoneId.systemDefault()).getMonth());
                    System.out.println(oldtime);
                }


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);


                finish();

            }
        });


        mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CustomCategoryCreation.class);
                startActivityForResult(intent, 100);

            }
        });















    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();
        if (parent.getId() == R.id.edit_expense_selector && !item.equals("Choose Your Expense")) {
            //make all else visible and populate it with retrieved values
            DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Expenses").document(item);
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    expense = documentSnapshot.toObject(Expense.class);
                    if (expense != null) {
                        for (String tag : expense.getTags()) {
                            tags.add(tag);
                        }
                        oldtime = expense.getTime();
                        mLocation.setText(expense.getLocation());
                        mLocation.setVisibility(View.VISIBLE);
                        mName.setText(expense.getName());
                        mName.setVisibility(View.VISIBLE);
                        String rep;
                        if (expense.getRepeating()) {
                            rep = "Y";
                        } else {
                            rep = "N";
                        }
                        mRepeating.setText(rep);
                        mRepeating.setVisibility(View.VISIBLE);

                        mFinish.setVisibility(View.VISIBLE);




                        mCategory.setVisibility(View.VISIBLE);
                        mPriority.setSelection(expense.getPriority());
                        mPriority.setVisibility(View.VISIBLE);
                        if (expense.getOutlierMonthly()) {
                            mOutlier.setSelection(2);
                        } else if (expense.getOutlierWeekly()) {
                            mOutlier.setSelection(1);
                        } else {
                            mOutlier.setSelection(3);
                        }


                        mOutlier.setVisibility(View.VISIBLE);
                        oldAmount = expense.getAmount();
                        mAmount.setText(String.valueOf(expense.getAmount()));
                        mAmount.setVisibility(View.VISIBLE);
                    }







                }
            });



        } else if (parent.getCount() > 5) {
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
