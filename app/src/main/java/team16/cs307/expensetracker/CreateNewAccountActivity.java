package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNewAccountActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mCreateButton;
    private FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();
        // When a user clicks the create button, create a new account
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the inputs are valid before proceeding
                if (validateInputs(mEmail, mPassword, mConfirmPassword)) {
                    // Create a new user with the email and password
                    mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                           throw Objects.requireNonNull(task.getException());

                                        } catch(FirebaseAuthUserCollisionException exception) {
                                            mEmail.setError("Account already exists");
                                        } catch (Exception ignored) {
                                        }
                                    } else {

                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("email", mAuth.getCurrentUser().getEmail());



                                        db.collection("users").document(mAuth.getUid()).set(newUser);
                                        //default user preference
                                        Preferences defPref = new Preferences();
                                        Map<String,Object> userPref = new HashMap<>();
                                        userPref.put("darkMode",defPref.isDarkMode());
                                        userPref.put("fontSize",defPref.getFontSize());
                                        userPref.put("colorScheme",defPref.getColorScheme());
                                        userPref.put("defaultGraph",defPref.getDefaultGraph());
                                        userPref.put("defaultBudgetNum",defPref.getDefaultBudgetNum());
                                        db.collection("users").document(mAuth.getUid()).collection("Preference").document("userPreference").set(userPref);
                                        Toast.makeText(CreateNewAccountActivity.this, "moving to financial info", Toast.LENGTH_SHORT).show();
                                        Intent financialInfoIntent = new Intent(getApplicationContext(), FinancialInfo.class);
                                        startActivity(financialInfoIntent);
                                        finish();
                                    }
                                }
                            });

                }
            }
        });
    }

    /**
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

    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password, String confirmPassword) {
        if (password == null) {
            return false;
        } else if (password.length() < 8 || confirmPassword.length() < 8) {
            return false;
        } else if (password.length() > 32 || confirmPassword.length() > 32) {
            return false;
        } else if (password.contains(" ") || confirmPassword.contains(" ")) {
            return false;
        } else if (!password.matches("[A-Z0-9]") || !confirmPassword.matches("[A-Z0-9]")) {
            return false;
        } else if (password.contains(" ") || confirmPassword.contains(" ")) {
            return false;
        } else if (!password.equals(confirmPassword)) {
            return false;
        } else if (!password.matches("[!@#$%^&*]") || !confirmPassword.matches("[!@#$%^&*]")) {
            return false;
        }
        return true;
    }
}
