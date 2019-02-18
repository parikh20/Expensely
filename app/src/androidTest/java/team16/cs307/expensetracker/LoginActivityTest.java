package team16.cs307.expensetracker;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);
    private LoginActivity logActivity = null;
    Instrumentation.ActivityMonitor  monitor ;
    @Before
    public void setUp() throws Exception {
        logActivity = loginActivityRule.getActivity();
    }
    @Test
    public void testLaunch(){
        View mUsername =  logActivity.findViewById(R.id.username_exitText);
        View mPassword =  logActivity.findViewById(R.id.password_editText);
        View mForgotPasswordClickable = logActivity.findViewById(R.id.forgot_password_clickable);
        View mCreateNewAccountClickable =  logActivity.findViewById(R.id.create_new_account_clickable);
        View mLoginButton =  logActivity.findViewById(R.id.login_button);
        View mGoogleSignInButton = logActivity.findViewById(R.id.google_sign_in_button);

        assertNotNull(mUsername);
        assertNotNull(mPassword);
        assertNotNull(mForgotPasswordClickable);
        assertNotNull(mCreateNewAccountClickable);
        assertNotNull(mLoginButton);
        assertNotNull(mGoogleSignInButton);

    }

    @After
    public void tearDown() throws Exception {
        logActivity = null;
    }
}