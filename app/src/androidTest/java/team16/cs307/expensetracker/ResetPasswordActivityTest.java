package team16.cs307.expensetracker;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ResetPasswordActivityTest {
    @Rule
    public ActivityTestRule<ResetPasswordActivity> resetPasswordActivityActivityTestRule = new ActivityTestRule<>(ResetPasswordActivity.class);
    private ResetPasswordActivity rpa = null;
    @Before
    public void setUp() throws Exception {
        rpa = resetPasswordActivityActivityTestRule.getActivity();
    }

    @Test
    public void testNullEmail() {
        assertFalse(rpa.validateEmail(null));
    }

    @Test
    public void testEmptyEmail() {
        assertFalse(rpa.validateEmail(""));
    }

    @Test
    public void testStringEmail() {
        assertFalse(rpa.validateEmail("string"));
    }

    @Test
    public void testStringEmailWithAt() {
        assertFalse(rpa.validateEmail("string@"));
    }

    @Test
    public void testValidEmail() {
        assertTrue(rpa.validateEmail("string@string.com"));
    }

    @Test
    public void testValidEmailWithSubDomain() {
        assertTrue(rpa.validateEmail("string@string.co.uk"));
    }

    @Test
    public void testEmailWithSpace() {
        assertFalse(rpa.validateEmail("string@ string.com"));
    }

    @Test
    public void testEmailWithSpecialCharacters() {
        assertFalse(rpa.validateEmail("stirng@string.com!"));
    }
}

