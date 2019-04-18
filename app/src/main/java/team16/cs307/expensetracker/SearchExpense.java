package team16.cs307.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;



import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class SearchExpense extends AppCompatActivity {
    private EditText mSearch;
    private Button btnsortName,btnSortLocation,btnSortAmount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private boolean isName = true;
    private boolean isLocation=false;
    private boolean isAmount=false;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_expense);
         mSearch = (EditText)findViewById(R.id.Search_text);
        btnsortName=(Button) findViewById(R.id.Search_sortName);
        btnSortLocation=(Button) findViewById(R.id.Search_sortLocation);
        btnSortAmount=(Button) findViewById(R.id.Search_sortAmount);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mAuth.getUid()).collection("Expenses");
        firebaseSearch(mSearch.getText().toString());

        btnsortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isName=!isName;
                if(isName){
                    isLocation=false;
                    isAmount=false;
                }
            }
        });
        btnSortLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocation=!isLocation;
                if(isLocation){
                    isName=false;
                    isAmount=false;
                }
            }

        });
        btnSortAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAmount=!isAmount;
                if(isAmount){
                    isName=false;
                    isLocation=false;
                }
            }
        });



    }
    private void firebaseSearch(String result){
        Query query=mRef.orderBy("name").startAt(result).endAt(result+"uf8ff");;
        if(isLocation){
            query=mRef.orderBy("location").startAt(result).endAt(result+"uf8ff");
        }else if(isAmount){
            query=mRef.orderBy("amount").startAt(result).endAt(result+"uf8ff");
        }

        FirestoreRecyclerOptions<SearchItem> options = new FirestoreRecyclerOptions.Builder<SearchItem>().setQuery(query,SearchItem.class).build();
        adapter = new SearchAdapter(options);
        recyclerView = findViewById(R.id.Search_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onStart(){
        super.onStart();

        adapter.startListening();

    }
    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }



    @Override
    public void onBackPressed(){
        onSupportNavigateUp();

    }
}
