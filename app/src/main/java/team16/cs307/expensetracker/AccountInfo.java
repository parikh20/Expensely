package team16.cs307.expensetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class AccountInfo extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button logout,removal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        mAuth = FirebaseAuth.getInstance();
        logout = (Button)findViewById(R.id.AccountInfo_logout);
        removal = (Button)findViewById(R.id.AccountInfo_removal);




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        removal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAccount();
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
                                            mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(AccountInfo.this,"Account removed",Toast.LENGTH_SHORT).show();
                                                    logout();
                                                }
                                            });
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


}
