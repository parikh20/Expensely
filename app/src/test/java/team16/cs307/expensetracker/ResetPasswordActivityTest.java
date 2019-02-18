package team16.cs307.expensetracker;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResetPasswordActivityTest {

    @Test
    public void onCreate() {
        /*
        1) Test all findViewById for NULL or unexpected value
        2) Test FirebaseAuth.getInstance for NULL or unexpected value
        3) Test ValidateInputs
            a) mEmail, mConfirmEmail
                i) ""
                ii) null
                iii) string containing "\n"
                iv) string with special chars
                v) normal [A-Za-z0-9] string
                vi) invalid email address pattern
                vii) s p a c e s
                viii) mEmail.equals(mConfirmEmail)
                ix) !mEmail.equals(mConfirmEmail)
        4) Test validateEmail
            a) auth == NULL
            b) auth != NULL
            c) !task.isSuccessful
            d) task.isSuccessful
         */
        //  Setup
        EditText mEmail;
        Button mSendButton;
        Button mResendButton;
        FirebaseAuth mAuth;

        mEmail = (EditText) findViewById(R.id.reset_password_email_editText);
        mSendButton = (Button) findViewById(R.id.reset_password_send_button);
        mResendButton = (Button) findViewById(R.id.reset_password_resend_button);

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // Assert fields are not null
        assertNotNull(mEmail);
        assertNotNull(mSendButton);
        assertNotNull(mResendButton);

        //  Asset mAuth not null
        assertNotNull(mAuth);

        //  ValidateInput for null mEmail
        mEmail.setText(null);

        try {
            mSendButton.performClick();
            assert(true);
        } catch (Exception | Error e){
            e.printStackTrace();
            Log.e("Email_null", "Test failed. Condition(s): mEmail.getText() == null");
            assert(false);
        }

        //   ValidateInput for "" mEmail
        mEmail.setText("");
        try {
            mSendButton.performClick();
            //TODO how to tell if toast happens? Need to check Toast text for appropriate message
            /*
            if(//Toast happens and message is "Please Enter Your Email") {
                assert(true);
            } else {
                Log.e("Email_empty_string", "Test failed. Condition(s): String username == \"\"");
                assert(false);
            }
             */
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("Email_empty_string", "Test failed. Condition(s): String username == \"\"\nError or Exception");
            assert(false);
        }

        //   ValidateInput for string containing "\n"
        mEmail.setText("hello\n");

        try {
            mSendButton.performClick();
            //TODO how to tell if toast happens? Need to check Toast text for appropriate message
            /*
            if(//Toast happens and message is "Please Enter Your Email") {
                assert(true);
            } else {
                Log.e("Email_empty_string", "Test failed. Condition(s): String username == \"\"");
                assert(false);
            }
             */
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("Email_newline_string", "Test failed. Condition(s): String username == \"\"\nError or Exception");
            assert(false);
        }

        //    ValidateInput for string containing special chars
        mEmail.setText("∑∑");
        try {
            mSendButton.performClick();
            //TODO how to tell if toast happens? Need to check Toast text for appropriate message
            /*
            if(//Toast happens and message is "Please Enter Your Email") {
                assert(true);
            } else {
                Log.e("Email_empty_string", "Test failed. Condition(s): String username == \"\"");
                assert(false);
            }
             */
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("Email_special_string", "Test failed. Condition(s): String username == \"\"\nError or Exception");
            assert(false);
        }

        //


    }
}