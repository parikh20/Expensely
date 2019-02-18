package team16.cs307.expensetracker;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)

public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;
    Instrumentation.ActivityMonitor  monitor ;
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }
    @Test
    public void testLaunch(){
        View upload = mActivity.findViewById(R.id.MainActivity_photoupload);
        View budget = mActivity.findViewById(R.id.MainActivity_select_budg);
        assertNotNull(upload);
        assertNotNull(budget);

    }

    @Test
    public void testLaunchImageActivity(){
        monitor = getInstrumentation().addMonitor(ImageActivity.class.getName(),null,false);

        assertNotNull(mActivity.findViewById(R.id.MainActivity_photoupload));

        onView(withId(R.id.MainActivity_photoupload)) .perform(click());

        Activity imageActivity = getInstrumentation().waitForMonitorWithTimeout(monitor,5000);

        assertNotNull(imageActivity);
        imageActivity.finish();
    }

    @Test
    public void testBudgetDownload(){
        monitor = getInstrumentation().addMonitor(BudgetDownload.class.getName(),null,false);
        assertNotNull(mActivity.findViewById(R.id.MainActivity_select_budg));

        onView(withId(R.id.MainActivity_select_budg)).perform(click());

        Activity budgetDownload = getInstrumentation().waitForMonitorWithTimeout(monitor,5000);

        assertNotNull(budgetDownload);
        budgetDownload.finish();
    }
    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}