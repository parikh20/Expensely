package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FinancialInfo extends AppCompatActivity {
    private double salary;
    private int dependants;
    private TextView financial_info_intro;
    private EditText mDependants;
    private Button mContinue;
    private EditText mSalary;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_info);

        financial_info_intro = findViewById(R.id.financial_info_intro);
        mDependants = findViewById(R.id.financial_info_dependants);
        mContinue = findViewById(R.id.financial_info_continue);
        mSalary = findViewById(R.id.salary);

        salary = 0;
        dependants = 0;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Salary");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //returning user (from settings page
                        financial_info_intro.setText("Enter your new salary and number of dependants:");

                    } else {
                        //new user

                    }
                } else {
                    Toast.makeText(FinancialInfo.this, "Failure to check db", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSalary.getText().toString().equals("") || mDependants.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(), "Please input your salary and dependants", Toast.LENGTH_SHORT).show();
                    return;
                }
                salary = Double.valueOf(mSalary.getText().toString());
                dependants = Integer.valueOf(mDependants.getText().toString());

                HashMap<String, Object> map  = new HashMap<>();
                map.put("Amount", salary);
                db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Salary").set(map);
                map.remove("Amount");
                map.put("Number", dependants);
                db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Dependants").set(map);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);


            }
        });


    }
}
