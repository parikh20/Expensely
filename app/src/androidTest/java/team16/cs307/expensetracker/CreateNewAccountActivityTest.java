package team16.cs307.expensetracker;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateNewAccountActivityTest {
    @Rule
    public ActivityTestRule<CreateNewAccountActivity> newActivityRule = new ActivityTestRule<CreateNewAccountActivity>(CreateNewAccountActivity.class);
    private CreateNewAccountActivity newActivity = null;
    Instrumentation.ActivityMonitor  monitor ;
    @Before
    public void setUp() throws Exception {
        newActivity= newActivityRule.getActivity();
    }
    @Test
    public void testLaunch(){
        View mEmail = newActivity.findViewById(R.id.email_exitText);
        View mPassword = newActivity.findViewById(R.id.new_account_password_editText);
        View mConfirmPassword = newActivity.findViewById(R.id.confirm_password_editText);
        View mCreateButton = newActivity.findViewById(R.id.create_new_account_button);

        assertNotNull(mEmail);
        assertNotNull(mPassword);
        assertNotNull(mConfirmPassword);
        assertNotNull(mCreateButton);

    }

    @After
    public void tearDown() throws Exception {
        newActivity = null;
    }
}