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
                iii) string ccntaining "\n"
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
        boolean exist;
        int mNumPressed;
        
        mEmail = (EditText) findViewById(R.id.reset_password_email_editText);
        mSendButton = (Button) findViewById(R.id.reset_password_send_button);
        mResendButton = (Button) findViewById(R.id.reset_password_resend_button);

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();

        //  1
        assertNotNull(mEmail);
        assertNotNull(mSendButton);
        assertNotNull(mResendButton);

        //  2
        assertNotNull(mAuth);

        //  3
        mEmail.setText(null);

        try {
            mEmail.performClick();
            assert(true);
        } catch (Exception | Error e){
            e.printStackTrace();
            Log.e("Email_null", "Test failed. Condition(s): mEmail.getText() == null");
            assert(false);
        }

    }
}