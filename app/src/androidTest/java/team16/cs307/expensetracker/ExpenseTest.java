package team16.cs307.expensetracker;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExpenseTest {
    @Rule
    public ActivityTestRule<CustomExpense> financialInfoActivityTestRule = new ActivityTestRule<>(CustomExpense.class);
    private CustomExpense ce = null;
    @Before
    public void setUp() throws Exception {
        ce = financialInfoActivityTestRule.getActivity();
    }


}
