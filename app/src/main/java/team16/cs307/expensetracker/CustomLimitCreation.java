package team16.cs307.expensetracker;

import android.app.Activity;
import android.content.Intent;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomLimitCreation extends AppCompatActivity {

    private EditText mType;
    private EditText mMonthly;
    private EditText mWeekly;
    private EditText mYearly;

    private Button mFinish;


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_limit_creation);
        mType = (EditText) findViewById(R.id.custom_limit_type);
        mMonthly = (EditText) findViewById(R.id.custom_limit_monthly);
        mWeekly = (EditText) findViewById(R.id.custom_limit_weekly);
        mYearly = (EditText) findViewById(R.id.custom_limit_yearly);
        mFinish = (Button) findViewById(R.id.custom_limit_finish);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeekly.getText() == null || mYearly.getText() == null || mMonthly.getText() == null) {
                    Toast.makeText(CustomLimitCreation.this, "Please enter your limit's values", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CustomLimitCreation.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                String type = mType.getText().toString();
                Limit l = new Limit(type, w, m, y);

                /*DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Working Limits");
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = documentSnapshot.getData();
                        int size = map.size();
                        map.put(String.valueOf(size), l);
                    }
                });*/
                Intent intent = new Intent();
                intent.putExtra("type",type);
                intent.putExtra("w", String.valueOf(w));
                intent.putExtra("m", String.valueOf(m));
                intent.putExtra("y", String.valueOf(y));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

}
