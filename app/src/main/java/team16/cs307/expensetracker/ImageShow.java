package team16.cs307.expensetracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ImageShow extends AppCompatActivity {
    RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private NoteAdapter adapter;
    private RecyclerView rv;
    private ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_access);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        imageList = new ArrayList<>();
        mRef = db.collection("users").document(mAuth.getUid()).collection("images");
        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot d : querySnapshot) {
                    String s = d.getId();
                    imageList.add("https://firebasestorage.googleapis.com/v0/b/expensely-cs307.appspot.com/o/" + s);
                    System.out.println("-----------------------------------------------\n https://firebasestorage.googleapis.com/v0/b/expensely-cs307.appspot.com/o/" + s);
                    adapter = new NoteAdapter(getApplicationContext());
                    adapter.setUrls(imageList);
                    RecyclerView recyclerView = findViewById(R.id.image_access_recyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                //rv = findViewById(R.id.image_access_recyclerView);


            }





        });



    }
    private void setUpRecyclerView() {
        //Query query = mRef;


    }
    @Override
    protected void onStart(){
        super.onStart();
        //adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }



    }


