package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Get all attributes from the Login Activity
    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgotPasswordClickable;
    private TextView mCreateNewAccountClickable;
    private Button mLoginButton;
    private SignInButton mGoogleSignInButton;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;




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

        configureGoogleSignIn();
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
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                LoginActivity.this.startActivityForResult(signInIntent, 1);
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
                Intent intent = new Intent(getApplicationContext(), CreateNewAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public void configureGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1000541586546-kcq736a6s7fvon8cvi08oiuog7a36l7i.apps.googleusercontent.com").requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(LoginActivity.this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount>  task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Google Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //onStart Placeholder for Firebase Authentication on startup.  Figure we'll replace this with login key functionality soon.
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
   }


    // onClick to get rid of error message. Does nothing
    public void onClick(View view) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
