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
    private CreateNewAccountActivity createNewAccount = null;
    Instrumentation.ActivityMonitor  monitor ;
    @Before
    public void setUp() throws Exception {
        createNewAccount = newActivityRule.getActivity();
    }

    @Test
    public void testNullEmail() {
        assertFalse(createNewAccount.validateEmail(null));
    }

    @Test
    public void testEmptyEmail() {
        assertFalse(createNewAccount.validateEmail(""));
    }

    @Test
    public void testStringEmail() {
        assertFalse(createNewAccount.validateEmail("sssssss"));
    }

    @Test
    public void testStringEmailWithSpace() {
        assertFalse(createNewAccount.validateEmail("ssss ssss sss"));
    }

    @Test
    public void testStringWithAt() {
        assertFalse(createNewAccount.validateEmail("ssss@"));
    }

    @Test
    public void testValidEmail() {
        assertTrue(createNewAccount.validateEmail("test@google.com"));
    }

    @Test
    public void testValidSubDomainEmail() {
        assertTrue(createNewAccount.validateEmail("test@google.co.uk"));
    }

    @Test
    public void testNullPassword() {
        assertFalse(createNewAccount.validatePassword(null, null));
    }

    @Test
    public void testEmptyPassword() {
        assertFalse(createNewAccount.validatePassword(" ", " "));
    }

    @Test
    public void testPasswordWithSpace() {
        assertFalse(createNewAccount.validatePassword("asdfdf sdf", "ertsdfsdv"));
    }

    @Test
    public void testMismatchedPasswords() {
        assertFalse(createNewAccount.validatePassword("asrsdaZ1@1", "sdfsdfA@31"));
    }

    @Test
    public void testPasswordWithNoCapital() {
        assertFalse(createNewAccount.validatePassword("asdfasdf1@", "asfdsafd1@"));
    }

    @Test
    public void testPasswordWithNoSpecialCharacter() {
        assertFalse(createNewAccount.validatePassword("asdfasdf1A", "asff21!asdf@"));
    }

    @Test
    public void testPasswordLessThan8Characters() {
        assertFalse(createNewAccount.validatePassword("Aa1!", "Aa1!"));
    }

    @Test
    public void testValidPassword() {
        assertFalse(createNewAccount.validatePassword("Abcdef1@", "Abcdef1!"));
    }

    @Test
    public void testPasswordMoreThan32Characters() {
        assertFalse(createNewAccount.validatePassword("asdfsadfsafdsadfsafasfasfsdfsafdsafd!1A", "asdfsadfsafdsadfsafasfasfsdfsafdsafd!1A"));
    }

    @After
    public void tearDown() throws Exception {
        createNewAccount = null;
    }
}