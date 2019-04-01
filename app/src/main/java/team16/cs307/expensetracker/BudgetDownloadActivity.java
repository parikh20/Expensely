package team16.cs307.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

import org.w3c.dom.Text;

public class BudgetDownloadActivity extends AppCompatActivity {

    //budg_curr displays the name of the currently selected budget
    //budg_curr_details displays the stats of this budget (i.e. limits, source, etc)
    //User should be able to browse current budgets TODO
    //User should be able to input details for a custom budget from this page TODO


    private TextView mBudg_curr;
    private TextView mBudg_curr_details;
    private Button mBudg_browse;
    private Button mBudg_custom;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public Budget curr_budg;
    private Button mBudg_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_download);
        mBudg_upload = findViewById(R.id.budg_upload_budget);
        mBudg_browse =  findViewById(R.id.budg_browse);
        mBudg_curr =  findViewById(R.id.budg_curr);
        mBudg_curr_details = findViewById(R.id.budg_curr_details);
        mBudg_custom = (Button) findViewById(R.id.budg_custom);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();




        /*

        if (user already has a budget attached to their account & active)
            get name of budget
            get details of budget
            get type of budget: i.e. custom, template, external
            then, set the values of curr and curr details appropriately
                this will also refresh when the user returns from picking a new budget
         */

        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Defaults
                String budg_curr = "Current Budget: No Active Budget";
                String budg_curr_details = "Budget Type: None \nLimits: \n\t Weekly: $0 \n\t Monthly: $0 \n\t Yearly: $0 \n ";
                curr_budg = documentSnapshot.toObject(Budget.class);
                if (curr_budg != null) {
                    Toast.makeText(BudgetDownloadActivity.this, "found a current budget" + curr_budg.getName(), Toast.LENGTH_SHORT).show();

                    budg_curr = "Current Budget: " + curr_budg.getName();

                    budg_curr_details = "Budget Type: Custom \nLimits: \n\t Weekly: $"+ curr_budg.getLimitWeekly() +" \n\t Monthly: $"+ curr_budg.getLimitMonthly() +" \n\t Yearly: $"+ curr_budg.getLimitYearly() +" \n Additional Limits: " + curr_budg.getCustomLimits().size();

                } else {
                    Toast.makeText(BudgetDownloadActivity.this, "No current budget", Toast.LENGTH_LONG).show();
                }

                mBudg_curr.setText(String.valueOf(budg_curr));
                mBudg_curr_details.setText(String.valueOf(budg_curr_details));



            }
        });

        mBudg_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseBudgets();
            }
        });

        mBudg_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomBudget();
            }
        });

        mBudg_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curr_budg == null) {
                    Toast.makeText(getApplicationContext(), "No Current Budget", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference ref = db.collection("PublicBudgets").document(curr_budg.getName());
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            //Defaults

                            Budget b = documentSnapshot.toObject(Budget.class);
                            if (b != null) {
                                Toast.makeText(BudgetDownloadActivity.this, "A Budget already exists for that name.  Try renaming it." + curr_budg.getName(), Toast.LENGTH_LONG).show();



                            } else {
                                
                                db.collection("PublicBudgets").document(curr_budg.getName()).set(curr_budg);
                                Toast.makeText(getApplicationContext(), "Uploaded publicly!", Toast.LENGTH_SHORT);
                            }





                        }
                    });
                }

            }
        });


    }

    private void browseBudgets() {
        /* TODO: redirect to budget browse screen
        Intent intent = new Intent(getApplicationContext(), BrowseBudgets.class);
        startActivity(intent);
        */
    }
    private void createCustomBudget() {

        Intent intent = new Intent(getApplicationContext(), CustomBudgetCreation.class);
        startActivity(intent);
        finish();
    }
}
