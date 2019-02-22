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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {
    private Button uploadpic;
    private Button selectbudg;
    private Button addExp;
    private Button imageAccess;
    private Button logout;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadpic = (Button) findViewById(R.id.MainActivity_photoupload);
        selectbudg = findViewById(R.id.MainActivity_select_budg);
        addExp = findViewById(R.id.main_new_expense);
        imageAccess=findViewById(R.id.MainActivity_ImageAccess);
        logout = findViewById(R.id.main_logout);
        mAuth = FirebaseAuth.getInstance();

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

