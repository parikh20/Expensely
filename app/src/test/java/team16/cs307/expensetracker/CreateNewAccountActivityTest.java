package team16.cs307.expensetracker;

import org.junit.Test;

import static org.junit.Assert.*;

public class CreateNewAccountActivityTest {

    @Test
    public void onCreate() {
        /*
        1) Test all findViewById for NULL or unexpected value
        2) Test FirebaseAuth.getInstance for NULL or unexpected value
        3) Test ValidateInputs
            a) mEmail
                i) ""
                ii) null
                iii) string ccntaining "\n"
                iv) string with special chars
                v) normal [A-Za-z0-9] string
                vi) invalid email address pattern
                vii) s p a c e s
            b) mPassword, mConfirmPassword
                i) (a) i - v, vii
                ii) mPassword.equals(mConfirmPassword)
                iii) !mPassword.equals(mConfirmPassword)
        4) Test onComplete
            a) Create a new account
            b) Create an account that already exists
         */
    }
}