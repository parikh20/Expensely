package team16.cs307.expensetracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CreateNewAccountActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mCreateButton;
    private Button mTransfer;
    private FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        // Get attributes from the screen
        mEmail = (EditText) findViewById(R.id.email_exitText);
        mPassword = (EditText) findViewById(R.id.new_account_password_editText);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_password_editText);
        mCreateButton = (Button) findViewById(R.id.create_new_account_button);
        mTransfer = findViewById(R.id.transfer_button);
        mTransfer.setVisibility(View.INVISIBLE);

        // Get Firebase instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        //Check whether it's a permanent user or an anonymous user
//        Bundle bundle = getIntent().getExtras();
//        String message = bundle.getString("message");
//        assert message != null;
//        if(message.equals("1"))
//        {
//            mCreateButton.setVisibility(View.INVISIBLE);
//            mTransfer.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            mTransfer.setVisibility(View.INVISIBLE);
//            mCreateButton.setVisibility(View.VISIBLE);
//        }

        // When a user clicks the create button, create a new account
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the inputs are valid before proceeding
                if (validateInputs(mEmail, mPassword, mConfirmPassword)) {
                    // Create a new user with the email and password
                    mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                           throw Objects.requireNonNull(task.getException());

                                        } catch(FirebaseAuthUserCollisionException exception) {
                                            mEmail.setError("Account already exists");
                                        } catch (Exception ignored) {
                                        }
                                    } else {

                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("email", mAuth.getCurrentUser().getEmail());



                                        db.collection("users").document(mAuth.getUid()).set(newUser, SetOptions.merge());
                                        //default user preference
                                        Preferences defPref = new Preferences();
                                        Map<String,Object> userPref = new HashMap<>();
                                        userPref.put("darkMode",defPref.isDarkMode());
                                        userPref.put("fontSize",defPref.getFontSize());
                                        userPref.put("colorScheme",defPref.getColorScheme());
                                        userPref.put("defaultGraph",defPref.getDefaultGraph());
                                        userPref.put("defaultBudgetNum",defPref.getDefaultBudgetNum());
                                        db.collection("users").document(mAuth.getUid()).collection("Preference").document("userPreference").set(userPref);
                                        Toast.makeText(CreateNewAccountActivity.this, "moving to financial info", Toast.LENGTH_SHORT).show();
                                        LoginActivity.alertSet(mAuth,db,getApplicationContext(),(AlarmManager)getSystemService(Context.ALARM_SERVICE));
                                        Intent financialInfoIntent = new Intent(getApplicationContext(), FinancialInfo.class);
                                        startActivity(financialInfoIntent);
                                        finish();
                                    }
                                }
                            });

                }
            }
        });
        //When the anonymous user changes his temp account to permanent account
        mTransfer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (validateInputs(mEmail, mPassword, mConfirmPassword)) {
                    AuthCredential credential = EmailAuthProvider.getCredential(mEmail.getText().toString(), mPassword.getText().toString());
                    mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(CreateNewAccountActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("email", mAuth.getCurrentUser().getEmail());
                                db.collection("users").document(mAuth.getUid()).set(newUser, SetOptions.merge());
                                //default user preference
                                Preferences defPref = new Preferences();
                                Map<String, Object> userPref = new HashMap<>();
                                userPref.put("darkMode", defPref.isDarkMode());
                                userPref.put("fontSize", defPref.getFontSize());
                                userPref.put("colorScheme", defPref.getColorScheme());
                                userPref.put("defaultGraph", defPref.getDefaultGraph());
                                userPref.put("defaultBudgetNum", defPref.getDefaultBudgetNum());
                                db.collection("users").document(mAuth.getUid()).collection("Preference").document("userPreference").set(userPref);
                                Toast.makeText(CreateNewAccountActivity.this, "moving to financial info", Toast.LENGTH_SHORT).show();
                                LoginActivity.alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
//                                mCreateButton.setVisibility(View.INVISIBLE);
//                                mTransfer.setVisibility(View.VISIBLE);
                                Intent CreateNewAccountActivity = new Intent(getApplicationContext(), CreateNewAccountActivity.class);
                                startActivity(CreateNewAccountActivity);
                                finish();
                            } else {
                                Toast.makeText(CreateNewAccountActivity.this, "Failure to check db", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * @param mEmail EditText field for the username
     * @param mPassword EditText field for the password
     * @param mConfirmPassword EditText field for the confirm password field
     * @return a boolean true if inputs are valid of false if not
     */
    private boolean validateInputs(EditText mEmail, EditText mPassword, EditText mConfirmPassword) {
            // If email is empty, prompt user with an error
           if (mEmail.getText() == null) {
               mEmail.setError("Email Address must not be empty!");
               return false;
           // If the password is empty, prompt the user with an error
           } else if (mPassword.getText() == null) {
               mPassword.setError("Password must not be empty!");
               return false;
           // If the confirm password field is empty, prompt the user with an error
           } else if (mPassword.getText() == null) {
               mConfirmPassword.setError("Password must not be empty!");
               return false;
           // If the email address is not valid, prompt the user with an error
           } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()) {
               mEmail.setError("Not a valid Email Address!");
               return false;
           // If the password does not equal the confirm password, prompt the user with an error
           } else if (!(mPassword.getText().toString().equals(mConfirmPassword.getText().toString()))) {
               mPassword.setError("Passwords do not match!");
               return false;
           }
           // Otherwise return true
           return true;
    }

    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password, String confirmPassword) {
        if (password == null) {
            return false;
        } else if (password.length() < 8 || confirmPassword.length() < 8) {
            return false;
        } else if (password.length() > 32 || confirmPassword.length() > 32) {
            return false;
        } else if (password.contains(" ") || confirmPassword.contains(" ")) {
            return false;
        } else if (!password.matches("[A-Z0-9]") || !confirmPassword.matches("[A-Z0-9]")) {
            return false;
        } else if (password.contains(" ") || confirmPassword.contains(" ")) {
            return false;
        } else if (!password.equals(confirmPassword)) {
            return false;
        } else if (!password.matches("[!@#$%^&*]") || !confirmPassword.matches("[!@#$%^&*]")) {
            return false;
        }
        return true;
    }

    public void alertSet() {//DISABLED 4-13-19 DUE TO REPLACEMENT BY STATIC LOGIN ACTIVITY FUNCTION!  No need to re-enable
        /*alertSet is called on every log in, it will
        1. check if alerts are already configured and running
            A. if they are, nothing to see here, all operating as normal.  continues out of function to login
            B. if not, we check if the user has turned off alerts in preferences
                a. if they did, all operating as normal, proceeds to login
                b. if they didn't, we know alerts are not configured yet, continues to point 2
            C. if the fields for "alertsSetUp" or "alertsTurnedOff" are missing in firebase, we know that alerts have not yet been configured for this user
                a. continues to point 2, creating the fields with values of "false"
        2. if we're here, it means alerts are not configured, or turned off by something other than the user
            A. schedule a repeating budget alert
                a. details: triggers every day, at the time which it was configured (we want to avoid exact time alerts, they're generally bad practice)
                b. The alert will trigger a call to BudgetNotification, which checks if the user is over their projected budget and/or total budget
                c. the alert then displays this information, allowing the user to click it and enter the app (main screen redirect)

        */
        DocumentReference docRef = db.collection("users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        if (document.get("alertsSetUp") == null || document.get("alertsTurnedOff") == null) {
                            Map<String, Boolean> alerts = new HashMap<>();
                            alerts.put("alertsSetUp", false);
                            alerts.put("alertsTurnedOff", false);
                            db.collection("users").document(mAuth.getUid()).set(alerts, SetOptions.merge());
                            //set up alerts

                        }
                        Boolean setup = document.getBoolean("alertsSetUp");
                        Boolean exempt = document.getBoolean("alertsTurnedOff");
                        if (setup == null || exempt == null) {
                            //this is just here to prevent the android studio warning about nullpointer.  These variables should never result in null, as they are only placed as a boolean
                            //if we ever reach this statement, it is likely due to one of these variables being updated incorrectly, as a non-boolean
                            return;
                        }
                        if (setup || exempt) {
                            //continue as planned
                            return;
                        } else {
                            if (!exempt) {
                                //set up alerts
                                Toast.makeText(getApplicationContext(), "Setting up alerts", Toast.LENGTH_SHORT).show();
/*
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,intent,0);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000,pendingIntent);
*/

                                Intent notificationIntent = new Intent(getApplicationContext(),AlertReceiver.class);
                                notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ID,1);
                                Notification n;
                                Intent budgRedirect = new Intent(getApplicationContext(),LoginActivity.class);
                                PendingIntent mainIntent = PendingIntent.getActivity(getApplicationContext(),1,budgRedirect,PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"BudgetAlert");
                                builder.setContentTitle("Budget Checkup");
                                builder.setContentText("placeholder info about budget here");
                                builder.setSmallIcon(R.drawable.ic_launcher_background);
                                builder.setContentIntent(mainIntent);
                                builder.setAutoCancel(true);
                                n = builder.build();
                                notificationIntent.putExtra(AlertReceiver.NOTIFICATION,n);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                //!!!!!!!!!!!!FOR TESTING NOTIFICATIONS==== SET FUTUREMILLIS TO elapsed time + 10000 for a ten second notification
                                long futureMillis = SystemClock.elapsedRealtime() + TimeUnit.DAYS.toMillis(1);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);
                                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureMillis, AlarmManager.INTERVAL_DAY,pendingIntent);
                                System.out.println("Set up alarm for " + (SystemClock.elapsedRealtime() + TimeUnit.DAYS.toMillis(1)));

                                Map<String,String> alerts = new HashMap<>();
                                alerts.put("alertsSetUp", "true");//TODO set back to true
                                alerts.put("alertsTurnedOff","false");
                                alerts.put("email", mAuth.getCurrentUser().getEmail());
                                db.collection("users").document(mAuth.getUid()).set(alerts, SetOptions.merge());
                                return;
                            }


                        }
                    }
                }
            }


        });
    }
}
