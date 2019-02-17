package team16.cs307.expensetracker;

import android.app.AppComponentFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginActivityTest extends AppCompatActivity {



    @Test
    public void onCreate() {
        //Tests:
        /*
        1) Test all findViewById's to make sure they are not NULL
        2) Test FirebaseAuth.getInstance for NULL
        3) Test mLoginButton --> username, password getText() for NULL
        4) Test mLoginButton --> username, password with several strings, including ""
        5) Test Auth.GoogleSigninApi.getSignInIntent(mGoogleApiClient) for NULL or unexpected return
        6) Test when username has s p a c e s
        7) Test when username has special characters
        8) Test password.equals("")
        9) Test password.equals(NULL)
        10) Test when password has s p a c e s
        11) Test when password hsa special characters
        12) Test both if string contains "\n"
        13) Test !task.isSuccessful (mAuth.signInWithEmailAndPassword)
        14) Test task.isSuccessful (mAuth.signInWithEmailAndPassword)
         */

        //  Setup
        EditText mUsername_t = (EditText) findViewById(R.id.username_exitText);
        EditText mPassword_t = (EditText) findViewById(R.id.password_editText);
        TextView mForgotPasswordClickable_t = (TextView) findViewById(R.id.forgot_password_clickable);
        TextView mCreateNewAccountClickable_t = (TextView) findViewById(R.id.create_new_account_clickable);
        Button mLoginButton_t = (Button) findViewById(R.id.login_button);
        SignInButton mGoogleSignInButton_t = (SignInButton) findViewById(R.id.google_sign_in_button);
        FirebaseAuth mAuth_t = FirebaseAuth.getInstance();

        //  1
        assertNotNull(mUsername_t);
        assertNotNull(mPassword_t);
        assertNotNull(mForgotPasswordClickable_t);
        assertNotNull(mCreateNewAccountClickable_t);
        assertNotNull(mLoginButton_t);
        assertNotNull(mGoogleSignInButton_t);

        //  2
        assertNotNull(mAuth_t);

        //  3
        mUsername_t.setText(null);

        try {
           mLoginButton_t.performClick();
           assert(true);
        } catch (Exception | Error e){
            e.printStackTrace();
            Log.e("username_null", "Test failed. Condition(s): mUsername.getText() == null");
            assert(false);
        }

        mPassword_t.setText(null);

        try {
            mLoginButton_t.performClick();
            assert(true);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("password_null", "Test failed. Condition(s): mPassword.getText() == null");
            assert(false);
        }

        //  4

        //username is empty string
        mUsername_t.setText("");
        mPassword_t.setText("");
        try {
            mLoginButton_t.performClick();
            //TODO how to tell if toast happens? Need to check Toast text for appropriate message
            /*
            if(//Toast happens and message is "Please Enter Your Email") {
                assert(true);
            } else {
                Log.e("username_empty_string", "Test failed. Condition(s): String username == \"\"");
                assert(false);
            }
             */
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("username_empty_string", "Test failed. Condition(s): String username == \"\"\nError or Exception");
            assert(false);
        }

        //  good username, empty string password
        mUsername_t.setText("dummyUsername123");
        try {
            mLoginButton_t.performClick();
            //TODO how to tell if toast happens? Need to check Toast text for appropriate message
            /*
            if(//Toast happens and message is "Please Enter Your Password") {
                assert(true);
            } else {
                Log.e("password_empty_string", "Test failed. Condition(s): String password == \"\"");
                assert(false);
            }
             */
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("password_empty_string", "Test failed. Condition(s): String password == \"\"\nError or Exception");
            assert(false);
        }

        //good username, good password
        mPassword_t.setText("dummyPassword987");
        try {
            mLoginButton_t.performClick();
            //TODO figure out what needs to be tested if both username and password are good
            assert(true);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Log.e("user_and_pass_good", "Test failed. Condition(s): String username =" +
                    " dummyUsername123\nString password = dummyPassword987\nError or Exception");
            assert(false);
        }

        //  5


    }

    @Test
    public void configureGoogleSignIn() {
    }

    @Test
    public void onActivityResult() {
        /*
        1) requestCode == 1
        2) requestCode != 1
        3) Test with !task.isSuccessful
         */
    }

    @Test
    public void onStart() {
        /*
        1) Test getCurrentUser() for NULL or unexpected return
         */
    }

    @Test
    public void onClick() {
    }

    @Test
    public void onConnectionFailed() {
    }
}