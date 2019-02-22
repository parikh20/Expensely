package team16.cs307.expensetracker;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
    public void testNullEmail() {
        assertFalse(logActivity.validateEmail(null));
    }

    @Test
    public void testEmptyEmail() {
        assertFalse(logActivity.validateEmail(""));
    }

    @Test
    public void testStringEmail() {
        assertFalse(logActivity.validateEmail("string"));
    }

    @Test
    public void testStringWithAt() {
        assertFalse(logActivity.validateEmail("string@"));
    }

    @Test
    public void testValidEmail() {
        assertTrue(logActivity.validateEmail("string@string.com"));
    }

    @Test
    public void testValidSubDomainEmail() {
        assertTrue(logActivity.validateEmail("string@string.co.uk"));
    }

    @Test
    public void testEmailWithSpace() {
        assertFalse(logActivity.validateEmail("string @string.com"));
    }

    @Test
    public void testEmailWithEscapeCharacter() {
        assertFalse(logActivity.validateEmail("string\n@string.com"));
    }

    @Test
    public void testEmailWithSpecialCharacter() {
        assertFalse(logActivity.validateEmail("string@string.com#"));
    }

    @Test
    public void testNullPassword() {
        assertFalse(logActivity.validatePassword(null));
    }

    @Test
    public void testEmptyStringPassword() {
        assertFalse(logActivity.validatePassword(""));
    }

    @Test
    public void testShortPassword() {
        assertFalse(logActivity.validatePassword("aaa"));
    }

    @Test
    public void testPasswordWithNoCapital() {
        assertFalse(logActivity.validatePassword("aaaaaaaa1@aa"));
    }

    @Test
    public void testPasswordWithNoNumber() {
        assertFalse(logActivity.validatePassword("aaaaaaaaaaAA@"));
    }

    @Test
    public void testPasswordWithNoSpecialCharacter() {
        assertFalse(logActivity.validatePassword("aaaaaaaaA1"));
    }

    @Test
    public void testValidPassword() {
        assertFalse(logActivity.validatePassword("AAaaCC1@@aaC"));
    }

    @Test
    public void testPasswordWithSpace() {
        assertFalse(logActivity.validatePassword("aaaa aaaa1@A"));
    }

    @Test
    public void testPasswordWithEscapeCharacter() {
        assertFalse(logActivity.validatePassword("aAA\n1@CaaTc"));
    }

    @Test
    public void testPasswordWithInvalidSpecialCharacter() {
        assertFalse(logActivity.validatePassword("aaaaAAACC@))"));
    }

    @Test
    public void testValidPassword2() {
        assertTrue(logActivity.validatePassword("aA1@!vgGtOpLS!"));
    }

    @After
    public void tearDown() throws Exception {
        logActivity = null;
    }
}