package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class CreateNewAccountActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mCreateButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        // Get attributes from the screen
        mEmail = (EditText) findViewById(R.id.email_exitText);
        mPassword = (EditText) findViewById(R.id.new_account_password_editText);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password_editText);
        mCreateButton = (Button) findViewById(R.id.create_new_account_button);

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // When a user clicks the create button, create a new account
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the inputs are valid before proceeding
                if (validateInputs(mEmail, mPassword, mConfirmPassword)) {
                    // Create a new user with the email and password
                    mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString());
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            }
        });
    }

    /**
     *
     * @param mEmail EditText field for the username
     * @param mPassword EditText field for the password
     * @param mConfirmPassword EditText field for the confirm password field
     * @return a boolean true if inputs are valid of false if not
     */
    private boolean validateInputs(EditText mEmail, EditText mPassword, EditText mConfirmPassword) {
            // If email is empty, prompt user with an error
           if (mEmail.getText() == null) {
               mEmail.setError("Email Address must not be empty!");
               return false;
           // If the password is empty, prompt the user with an error
           } else if (mPassword.getText() == null) {
               mPassword.setError("Password must not be empty!");
               return false;
           // If the confirm password field is empty, prompt the user with an error
           } else if (mPassword.getText() == null) {
               mConfirmPassword.setError("Password must not be empty!");
               return false;
           // If the email address is not valid, prompt the user with an error
           } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()) {
               mEmail.setError("Not a valid Email Address!");
               return false;
           // If the password does not equal the confirm password, prompt the user with an error
           } else if (!(mPassword.getText().toString().equals(mConfirmPassword.getText().toString()))) {
               mPassword.setError("Passwords do not match!");
               return false;
           }
           // Otherwise return true
           return true;
    }
}
