package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Get all attributes from the Login Activity
    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgotPasswordClickable;
    private TextView mCreateNewAccountClickable;
    private Button mLoginButton;
    private SignInButton mGoogleSignInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get attributes
        mUsername = (EditText) findViewById(R.id.username_exitText);
        mPassword = (EditText) findViewById(R.id.password_editText);
        mForgotPasswordClickable = (TextView) findViewById(R.id.forgot_password_clickable);
        mCreateNewAccountClickable = (TextView) findViewById(R.id.create_new_account_clickable);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);

        mAuth = FirebaseAuth.getInstance();


        // OnClickListener for when the login button is pressed
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (username.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                //TODO Launch login failure dialog or use toast
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }
                        }
                    });

            }
        });

        // OnClickListener for when the Google sign in button is pressed
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GoogleSignInActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        // OnClickListener for when the Forgot Password text is clicked
        mForgotPasswordClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // OnClickListener for when the Create New Account text is clicked
        mCreateNewAccountClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
   }


    // onClick to get rid of error message. Does nothing
    public void onClick(View view) {
    }
}
