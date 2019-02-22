package team16.cs307.expensetracker;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FinancialInfoTest {
    @Rule
    public ActivityTestRule<FinancialInfo> financialInfoActivityTestRule = new ActivityTestRule<>(FinancialInfo.class);
    private FinancialInfo fi = null;
    @Before
    public void setUp() throws Exception {
        fi = financialInfoActivityTestRule.getActivity();
    }

    @Test
    public void testNegativeSalary() {
        assertFalse(fi.validateSalary(-100000));
    }

    @Test
    public void testTooLargeSalary() {
        assertFalse(fi.validateSalary(1000000000));
    }

    @Test
    public void testNegativeDependants() {
        assertFalse(fi.validateDependants(-10));
    }

    @Test
    public void testTooLargeDependants() {
        assertFalse(fi.validateDependants(100));
    }

    @Test
    public void testZeroSalary() {
        assertTrue(fi.validateSalary(0));
    }

    @Test
    public void testValidSalary() {
        assertTrue(fi.validateSalary(53691));
    }

    @Test
    public void testValidSalary2() {
        assertTrue(fi.validateSalary(158232));
    }

    @Test
    public void testValidSalary3() {
        assertTrue(fi.validateSalary(24056));
    }

    @Test
    public void testValidSalary4() {
        assertTrue(fi.validateSalary(608325));
    }

    @Test
    public void testValidSalary5() {
        assertTrue(fi.validateSalary(1036472));
    }

    @Test
    public void testValidDependant() {
        assertTrue(fi.validateDependants(0));
    }

    @Test
    public void testValidDependants2() {
        assertTrue(fi.validateDependants(3));
    }
}
