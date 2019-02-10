package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mConfirmEmail;
    private Button mSendButton;
    private Button mResendButton;
    FirebaseAuth mAuth;
    private boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        // Get attributes from the screen
        mEmail = (EditText) findViewById(R.id.email_editText);
        mConfirmEmail = (EditText) findViewById(R.id.confirm_email_editText);
        mSendButton = (Button) findViewById(R.id.Send_button);
        mResendButton = (Button) findViewById(R.id.ReSend_button);
        exist = true;

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // When a user clicks the send button, a password reset email is sent to the user
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the inputs are valid before proceeding
                if (validateInputs(mEmail, mConfirmEmail, mAuth)) {
                    // Send the user a password reset email
                    mAuth.sendPasswordResetEmail(mConfirmEmail.getText().toString());
                    mResendButton.setVisibility(View.VISIBLE);
                }
            }
        });
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mConfirmEmail.getText().toString());
            }
        });

    }

    /**
     *
     * @param mEmail EditText field for the username
     * @param mConfirmEmail EditText field for email confirmation
     * @param auth Firebase auth
     * @return a boolean true if inputs are valid of false if not
     */
    private boolean validateInputs(EditText mEmail, EditText mConfirmEmail, FirebaseAuth auth) {
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
        else if (!(mEmail.getText().toString().equals(mConfirmEmail.getText().toString()))) {
            mConfirmEmail.setError("Emails do not match!");
            return false;
        }
        else if(!validateEmail(mConfirmEmail,auth))
        {
            return false;
        }
        // Otherwise return true
        return true;
    }
    /**
     *
     * @param mConfirmEmail EditText field for email confirmation
     * @param auth Firebase auth
     * @return a boolean true if email exists, false if it doesn't
     */
    private boolean validateEmail(final EditText mConfirmEmail, FirebaseAuth auth) {
        mAuth.createUserWithEmailAndPassword(mConfirmEmail.getText().toString(), "Testifdummyworks12345").
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            mConfirmEmail.setError("Email doesn't exist");
                            exist = false;
                        }
                    }
                });
        return exist;
    }
}
