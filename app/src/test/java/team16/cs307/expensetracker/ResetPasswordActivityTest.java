package team16.cs307.expensetracker;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResetPasswordActivityTest {

    @Test
    public void onCreate() {
        /*
        1) Test all findViewById for NULL or unexpected value
        2) Test FirebaseAuth.getInstance for NULL or unexpected value
        3) Test ValidateInputs
            a) mEmail, mConfirmEmail
                i) ""
                ii) null
                iii) string ccntaining "\n"
                iv) string with special chars
                v) normal [A-Za-z0-9] string
                vi) invalid email address pattern
                vii) s p a c e s
                viii) mEmail.equals(mConfirmEmail)
                ix) !mEmail.equals(mConfirmEmail)
        4) Test validateEmail
            a) auth == NULL
            b) auth != NULL
            c) !task.isSuccessful
            d) task.isSuccessful
         */
    }
}