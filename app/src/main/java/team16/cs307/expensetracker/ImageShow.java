package team16.cs307.expensetracker;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class ImageShow extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private NoteAdapter adapter;
    private StorageReference mStore;
    private FloatingActionButton add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_access);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mAuth.getUid()).collection("images");
        setUpRecyclerView();
        add = (FloatingActionButton) findViewById(R.id.image_access_addButton);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }

    private void setUpRecyclerView() {
        Query query = mRef;
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        adapter = new NoteAdapter(options);
        recyclerView = findViewById(R.id.image_access_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.TagClickLisentner(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, final int position) {
                final EditText edittext = new EditText(ImageShow.this);
                edittext.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
                new AlertDialog.Builder( ImageShow.this )
                        .setTitle( "Tag" )
                        .setMessage( "Change your tag (Max Length: 6)" )
                        .setView(edittext)
                        .setPositiveButton( "Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                documentSnapshot.getReference().update("tag",edittext.getText().toString());
                            }
                        })
                        .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        } )
                        .show();
            }
        });
        adapter.setLongClickListener(new NoteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, final int position) {

                new AlertDialog.Builder( ImageShow.this )
                        .setTitle( "Image Delete" )
                        .setMessage( "Are you sure?" )
                        .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String uri = "https://firebasestorage.googleapis.com/v0/b/expensely-cs307.appspot.com/o/"+documentSnapshot.toObject(Note.class).getImgurl();
                                adapter.deleteItem(position);
                                mStore = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                                mStore.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ImageShow.this,"Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });

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

    private void uploadImage(){
        Intent intent = new Intent(ImageShow.this,ImageActivity.class);
        startActivity(intent);


    }



}


