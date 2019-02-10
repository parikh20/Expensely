package team16.cs307.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateNewAccountActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        mUsername = (EditText) findViewById(R.id.username_exitText);
        mPassword = (EditText) findViewById(R.id.password_editText);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password_editText);
        mCreateButton = (Button) findViewById(R.id.create_new_account_button);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs(mUsername, mPassword, mConfirmPassword)) {
                    
                }
            }
        });
    }

    private boolean validateInputs(EditText mUsername, EditText mPassword, EditText mConfirmPassword) {
           if (mUsername.getText() == null) {
               mUsername.setError("Email Address must not be empty!");
               return false;
           } else if (!Patterns.EMAIL_ADDRESS.matcher(mUsername.getText()).matches()) {
               mUsername.setError("Not a valid Email Address!");
               return false;
           } else if (!mPassword.getText().equals(mConfirmPassword.getText())) {
               mPassword.setError("Passwords do not match!");
               return false;
           }
           return true;
    }
}
