package team16.cs307.expensetracker;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {
    private Button uploadpic;
    private Button selectbudg;
    private Button addExp;
    private Button imageAccess;
    private Button logout;
    private FirebaseAuth mAuth;
    private GraphView mGraph;
    private FirebaseFirestore db;

    private Budget curr_budg;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGraph = findViewById(R.id.main_graph);
        uploadpic = (Button) findViewById(R.id.MainActivity_photoupload);
        selectbudg = findViewById(R.id.MainActivity_select_budg);
        addExp = findViewById(R.id.main_new_expense);
        imageAccess=findViewById(R.id.MainActivity_ImageAccess);
        logout = findViewById(R.id.main_logout);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        //get current budget
        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                curr_budg = documentSnapshot.toObject(Budget.class);
                if (curr_budg != null) {
                    Toast.makeText(MainActivity.this, "found a current budget: " + curr_budg.getName(), Toast.LENGTH_SHORT).show();
                        //space reserved for getting budget quick info when implemented
                    mGraph.setTitle("Your Weekly Budget: " + curr_budg.getName());
                    //all changes to the graph here will take effect after first interaction: Might have to move whole graph element in here

                } else {
                    Toast.makeText(MainActivity.this, "No current budget", Toast.LENGTH_LONG).show();
                }
            }
        });


        //graph settings
        GridLabelRenderer glr = mGraph.getGridLabelRenderer();
        glr.setPadding(32);
        mGraph.getViewport().setScalable(true);
        mGraph.getViewport().setScalableY(true);



        //test graph for purposes of messing with graph API:
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 2),
                new DataPoint(2, 4),
                new DataPoint(200, 100)
        });
        mGraph.addSeries(series);




        //upload photo

        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        selectbudg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBudget();
            }
        });

        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        imageAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessImage();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });











    }


    private void addExpense() {
            Intent intent = new Intent(getApplicationContext(), CustomExpense.class);
            startActivity(intent);
        }
        private void uploadImage(){
            Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
            startActivityForResult(intent,1);
        }
        private void selectBudget() {
            Intent intent = new Intent(getApplicationContext(), BudgetDownloadActivity.class);
            startActivity(intent);

        }
        private void accessImage(){
            Intent intent = new Intent(getApplicationContext(),ImageShow.class);
            startActivityForResult(intent,1);
        }
        private void logout() {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

