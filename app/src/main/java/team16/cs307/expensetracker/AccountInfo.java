package team16.cs307.expensetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AccountInfo extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference mDoc;
    private Button logout,removal;
    private TextView emailText;
    private Button emailChange,resetPassword,supportEmail,mAlert;
    private Spinner graphSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        getSupportActionBar().setTitle("Account Info");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        logout = (Button)findViewById(R.id.AccountInfo_logout);
        removal = (Button)findViewById(R.id.AccountInfo_removal);
        emailChange = (Button)findViewById(R.id.AccountInfo_Email);
        resetPassword = (Button)findViewById(R.id.AccountInfo_resetPassword);
        supportEmail = (Button)findViewById(R.id.AccountInfo_SupportEmail);
        emailText = (TextView)findViewById(R.id.AccountInfo_text);
        emailText.setText(mAuth.getCurrentUser().getEmail());
        graphSpinner = (Spinner)findViewById(R.id.graph_spinner);
        mAlert = (Button)findViewById(R.id.AccountInfo_Alert);

        List<String> graphs = new ArrayList<>();
        graphs.add("Line Graph");
        graphs.add("Pie Chart");
        ArrayAdapter<String> graphAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, graphs);
        graphSpinner.setAdapter(graphAdapter);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts();
            }
        });

        removal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAccount();
            }
        });
        emailChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    resetPassword();
            }
        });
        supportEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountInfo.this,SupportEmail.class));
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void removeAccount(){
        final EditText edittext = new EditText(AccountInfo.this);
        new AlertDialog.Builder( AccountInfo.this )
                .setTitle( "Account Removal" )
                .setMessage( "Are you sure?" )
                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder( AccountInfo.this )
                                .setTitle( "Account Removal" )
                                .setMessage( "Type: delete" )
                                .setView(edittext)
                                .setPositiveButton( "Remove", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(edittext.getText().toString().toLowerCase().equals("delete")){

                                            mDoc = db.collection("users").document(mAuth.getUid());

                                            mDoc.delete();
                                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(AccountInfo.this,"Account removed",Toast.LENGTH_SHORT).show();
                                                        logout();
                                                    }else{
                                                        Toast.makeText(AccountInfo.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }) ;
                                        }else{
                                            Toast.makeText(AccountInfo.this,"Incorrect!",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                } )
                                .show();

                    }
                })
                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .show();

    }


    private void changeEmail(){
        final EditText emailtext = new EditText(AccountInfo.this);
        emailtext.setHint("Enter your new emaill address");
        new AlertDialog.Builder( AccountInfo.this )
                .setTitle( "Email change" )
                .setMessage( "Current email:"+mAuth.getCurrentUser().getEmail().toString() )
                .setView(emailtext)
                .setPositiveButton( "Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       mAuth.getCurrentUser().updateEmail(emailtext.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){

                                   mDoc = db.collection("users").document(mAuth.getUid());
                                    mDoc.update("email",emailtext.getText().toString());
                                   Toast.makeText(AccountInfo.this,"Email updated",Toast.LENGTH_SHORT).show();
                                   emailText.setText(mAuth.getCurrentUser().getEmail().toString());
                               }else{
                                   Toast.makeText(AccountInfo.this,"Fail to update your email: "+task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                               }
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


    private void resetPassword(){
        final EditText emailtext = new EditText(AccountInfo.this);
        LinearLayout layout = new LinearLayout(AccountInfo.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText newpswd = new EditText(this);
        newpswd.setHint("New password");
        newpswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText confirmpswd = new EditText(this);
        confirmpswd.setHint("Confirm your password");
        confirmpswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newpswd);
        layout.addView(confirmpswd);
        new AlertDialog.Builder( AccountInfo.this )
                .setTitle( "Password reset" )
                .setMessage( "Please enter your new password" )
                .setView(layout)
                .setPositiveButton( "Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(newpswd.getText().toString().equals(confirmpswd.getText().toString())){
                            if(newpswd.getText().toString().length()<8){
                                Toast.makeText(AccountInfo.this,"Password length less than 8",Toast.LENGTH_SHORT).show();
                            }else{
                                mAuth=FirebaseAuth.getInstance();
                                mAuth.getCurrentUser().updatePassword(newpswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(AccountInfo.this,"Password updated",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(AccountInfo.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }


                        }else{
                            Toast.makeText(AccountInfo.this,"Different password",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .show();



    }

    void alerts() {
        Intent intent = new Intent(getApplicationContext(), AlertPreferencesActivity.class);
        startActivity(intent);
    }


}
