package team16.cs307.expensetracker;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.InputFilter;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

public class SearchExpense extends AppCompatActivity {
    private EditText mSearch;
    private Button btnsortName,btnSortLocation,btnSortPriority;
    private Switch swAscending;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private boolean isAscending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_expense);
         mSearch = (EditText)findViewById(R.id.Search_text);
        btnsortName=(Button) findViewById(R.id.Search_sortName);
        btnSortLocation=(Button) findViewById(R.id.Search_sortLocation);
        btnSortPriority=(Button) findViewById(R.id.Search_sortPriority);
        swAscending = (Switch)findViewById(R.id.Search_Switch);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mAuth.getUid()).collection("Expenses");
        /*firebaseSearch(mSearch.getText().toString());
        Query query=mRef.orderBy("name").startAt(mSearch.getText().toString()).endAt(mSearch.getText().toString()+"uf8ff");
        firebaseSearch(query);*/


        btnsortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swAscending.isChecked()==false) {
                    Query query = mRef.orderBy("name", Query.Direction.ASCENDING).startAt(mSearch.getText().toString()).endAt(mSearch.getText().toString() + "uf8ff");
                    firebaseSearch(query);
                }else{
                    Query query = mRef.orderBy("name", Query.Direction.DESCENDING).startAt(mSearch.getText().toString() + "uf8ff").endAt(mSearch.getText().toString());
                    firebaseSearch(query);
                }

            }
        });
        btnSortLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swAscending.isChecked()==false) {
                    Query query = mRef.orderBy("location", Query.Direction.ASCENDING).startAt(mSearch.getText().toString() ).endAt(mSearch.getText().toString()+ "uf8ff");
                    firebaseSearch(query);
                }else{
                    Query query = mRef.orderBy("location", Query.Direction.DESCENDING).startAt(mSearch.getText().toString() + "uf8ff").endAt(mSearch.getText().toString());
                    firebaseSearch(query);
                }


            }

        });
        btnSortPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int priority = 0;
                try{
                    priority=Integer.parseInt(mSearch.getText().toString());
                }catch (Exception e){
                    Toast.makeText(SearchExpense.this,"Priority should be a number!",Toast.LENGTH_SHORT).show();
                }

                if(swAscending.isChecked()==false) {
                    Query query=mRef.orderBy("priority", Query.Direction.ASCENDING).whereEqualTo("priority",priority);
                    firebaseSearch(query);
                }else{
                    Query query=mRef.orderBy("priority", Query.Direction.DESCENDING).whereEqualTo("priority",priority);
                    firebaseSearch(query);
                }

            }
        });



    }
    private void firebaseSearch(Query sortQuery){

        /*if(isLocation){
            query=mRef.orderBy("location").startAt(result).endAt(result+"uf8ff");
        }else if(isAmount){
            query=mRef.orderBy("amount").startAt(result).endAt(result+"uf8ff");
        }*/

        FirestoreRecyclerOptions<SearchItem> options = new FirestoreRecyclerOptions.Builder<SearchItem>().setQuery(sortQuery,SearchItem.class).build();
        adapter = new SearchAdapter(options);
        recyclerView = findViewById(R.id.Search_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        adapter.setLongClickListener(new SearchAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, final int position) {

                new AlertDialog.Builder( SearchExpense.this )
                        .setTitle( "txt" )
                        .setMessage( "Downloading Expense?" )
                        .setPositiveButton( "Download", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                File dir = new File(Environment.getDataDirectory(),documentSnapshot.get("name").toString()+"txt");

                                try{
                                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(Environment.getDataDirectory().toString()+documentSnapshot.get("name").toString()+"txt",true));
                                    fileWriter.append("asdsada");

                                    fileWriter.close();
                                    Toast.makeText(SearchExpense.this,Environment.getDataDirectory().toString(),Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    Toast.makeText(SearchExpense.this,"Failed",Toast.LENGTH_SHORT).show();
                                }



                            }
                        })
                        .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        } )
                        .show();
            }
        });

    }


    /*@Override
    protected void onStart(){
        super.onStart();

        adapter.startListening();

    }
    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }*/



    @Override
    public void onBackPressed(){
        onSupportNavigateUp();

    }
}
