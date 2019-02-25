package team16.cs307.expensetracker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class UserPreferenceActivity extends AppCompatActivity {
    private Switch mdarkMode;
    private Button mfontSize;
    private Button mcolorScheme;

    private int mNumPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get attributes from the screen
        mdarkMode = (Switch)findViewById(R.id.darkmode_switch);
        mfontSize = (Button) findViewById(R.id.font_button);
        mcolorScheme = (Button) findViewById(R.id.color_scheme_button);


        mcolorScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs(mEmail, mAuth)) {
                    // Send the user a password reset email
                    if (mNumPressed >= 3) {
                        Toast.makeText(getApplicationContext(),"An email has been sent 3 times, please check your inbox", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.sendPasswordResetEmail(mEmail.getText().toString());
                        mResendButton.setVisibility(View.VISIBLE);
                        mNumPressed++;
                        Toast.makeText(getApplicationContext(), "An email has been sent to you!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // TODO: Make a popup that says that email has been sent. Make it so that the resent email button goes on this popup
        mfontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNumPressed >= 3) {
                    Toast.makeText(getApplicationContext(),"An email has been sent 3 times, please check your inbox", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(mEmail.getText().toString());
                    mNumPressed++;
                    Toast.makeText(getApplicationContext(), "An email has been sent to you!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     *
     * @param mEmail EditText field for the username
     * @param auth Firebase auth
     * @return a boolean true if inputs are valid of false if not
     */
    private boolean validateInputs(EditText mEmail, FirebaseAuth auth) {
        // If email is empty, prompt user with an error
        if (mEmail.getText() == null) {
            mEmail.setError("Email Address must not be empty!");
            return false;
        }
        // If the email address is not valid, prompt the user with an error
        else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()) {
            mEmail.setError("Not a valid Email Address!");
            return false;
            // If the password does not equal the confirm password, prompt the user with an error
        }
        else {
            return validateEmail(mEmail, auth);
        }
    }

    /**
     *
     * @param mEmail EditText field for email confirmation
     * @param auth Firebase auth
     * @return a boolean true if email exists, false if it doesn't
     */
    private boolean validateEmail(final EditText mEmail, FirebaseAuth auth) {
        //Try to create a dummy account using user input email and check if adding is possible without exceptions
        auth.fetchSignInMethodsForEmail(mEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getSignInMethods().isEmpty()) {
                                exist = false;
                                mEmail.setError("Email does not exist");
                            } else {
                                exist = true;
                            }
                        }
                    }
                });
        return exist;
    }

    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }
}

