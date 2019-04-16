package team16.cs307.expensetracker;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.SetOptions;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Get all attributes from the Login Activity
    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgotPasswordClickable;
    private TextView mCreateNewAccountClickable;
    private Button mLoginButton;
    private SignInButton mGoogleSignInButton;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private TextView mTryClickable;
    private String log;
    FirebaseFirestore db;
    //private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get attributes
        mUsername = (EditText) findViewById(R.id.username_exitText);
        mPassword = (EditText) findViewById(R.id.password_editText);
        mForgotPasswordClickable = (TextView) findViewById(R.id.forgot_password_clickable);
        mCreateNewAccountClickable = (TextView) findViewById(R.id.create_new_account_clickable);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mTryClickable = (TextView) findViewById(R.id.mTryClickable);
        //pd = new ProgressDialog(this);
        //pd.setMessage("Logging in...");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        /*check if user is already signed in.  Firebase should save authentication details,
        but because loginactivity is our first activity by default, we need to check if
        we must redirect them to main.  No need to login again.
        NOTE: For debugging purposes before we implement a logout button, feel free to
        comment this following if statement out, to force a fresh login every time the app
        is started, or test something on the loginactivity page.  -LH
        */
        if (mAuth.getCurrentUser() != null) {
            //Toast.makeText(LoginActivity.this,"already logged in, redirecting", Toast.LENGTH_SHORT).show();
            //Check if user is already in the DB, if not, set up DB (to be moved to on login and on createacct)
            DocumentReference user = db.collection("users").document(mAuth.getCurrentUser().getUid());
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //user exists in db
                            //Toast.makeText(LoginActivity.this, "User is in db!", Toast.LENGTH_SHORT).show();

                        } else {
                            //user does not exist in db
                            //Toast.makeText(LoginActivity.this, "User does not exist in db", Toast.LENGTH_SHORT).show();
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("email", mAuth.getCurrentUser().getEmail());
                            db.collection("users").document(mAuth.getUid()).set(newUser, SetOptions.merge());
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Failure to check db", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
            //Toast.makeText(LoginActivity.this,"user path = " + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
          Intent i = new Intent(LoginActivity.this, MainActivity.class);
          startActivity(i);
            finish();
        }
        //configure google signin setup
        configureGoogleSignIn();
        // OnClickListener for when the login button is pressed
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (username.equals("")) {
                    mUsername.setError("Username cannot be empty");
                    return;
                }
                if (password.equals("")) {
                    mPassword.setError("Password cannot be empty");
                    return;
                }

                mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //pd.show();
                            if(!task.isSuccessful()) {
                                //TODO Launch login failure dialog or use toast
                                //pd.dismiss();
                                Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            } else {
                                //pd.dismiss();
                                //check if user exists in the db, then add if they do not
                                DocumentReference user = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                //user exists in db
                                                //Toast.makeText(LoginActivity.this, "User is in db!", Toast.LENGTH_SHORT).show();

                                            } else {
                                                //user does not exist in db
                                                Toast.makeText(LoginActivity.this, "User does not exist in db", Toast.LENGTH_SHORT).show();
                                                Map<String, Object> newUser = new HashMap<>();
                                                newUser.put("email", mAuth.getCurrentUser().getEmail());
                                                db.collection("users").document(mAuth.getUid()).set(newUser, SetOptions.merge());
                                                Preferences defPref = new Preferences();
                                                Map<String,Object> userPref = new HashMap<>();
                                                userPref.put("darkMode",defPref.isDarkMode());
                                                userPref.put("fontSize",defPref.getFontSize());
                                                userPref.put("colorScheme",defPref.getColorScheme());
                                                userPref.put("defaultGraph",defPref.getDefaultGraph());
                                                userPref.put("defaultBudgetNum",defPref.getDefaultBudgetNum());
                                                db.collection("users").document(mAuth.getUid()).collection("Preference").document("userPreference").set(userPref);
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failure to check db", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }
                        }
                    });
            }
        });

        // OnClickListener for when the Google sign in button is pressed
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                LoginActivity.this.startActivityForResult(signInIntent, 1);
            }
        });

        // OnClickListener for when the Forgot Password text is clicked
        mForgotPasswordClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        // OnClickListener for when the Try the app is clicked
        mTryClickable.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Due to alerts being generally long term, alerts are to be turned off by default for trial users
                 //No alerts are to be set up here, or on future login!  will need to check if a user is a trial user when they log in, and avoid enabling alerts
                 //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                 // Create an anonymous user
                 mAuth.signInAnonymously().
                         addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 if (!task.isSuccessful()) {
                                     Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                 } else {

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
                                     Toast.makeText(LoginActivity.this, "moving to financial info", Toast.LENGTH_SHORT).show();
                                     LoginActivity.alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                                     Intent financialInfoIntent = new Intent(getApplicationContext(), FinancialInfo.class);
                                     startActivity(financialInfoIntent);
                                     finish();
                                 }
                             }

                         });
                 alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                 Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                 LoginActivity.this.startActivity(intent2);
                 finish();
             }
         });
        // OnClickListener for when the Create New Account text is clicked
        mCreateNewAccountClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNewAccountIntent = new Intent(getApplicationContext(), CreateNewAccountActivity.class);
                startActivity(createNewAccountIntent);
            }
        });
    }

    public void configureGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1000541586546-kcq736a6s7fvon8cvi08oiuog7a36l7i.apps.googleusercontent.com").requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(LoginActivity.this, this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //pd.show();
        if (requestCode == 1) {

            Task<GoogleSignInAccount>  task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    //pd.dismiss();
                                    //check for db exist, add if they are not in db
                                    DocumentReference user = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                    user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    //user exists in db
                                                    Toast.makeText(LoginActivity.this, "User is in db!", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    //user does not exist in db
                                                    Toast.makeText(LoginActivity.this, "User does not exist in db", Toast.LENGTH_SHORT).show();
                                                    Map<String, Object> newUser = new HashMap<>();
                                                    newUser.put("email", mAuth.getCurrentUser().getEmail());
                                                    db.collection("users").document(mAuth.getUid()).set(newUser, SetOptions.merge());
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Failure to check db", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    alertSet(mAuth, db, getApplicationContext(), (AlarmManager) getSystemService(Context.ALARM_SERVICE));
                                    Toast.makeText(LoginActivity.this, "Google Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } catch (ApiException e) {
                //pd.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    //onStart Placeholder for Firebase Authentication on startup.  Figure we'll replace this with login key functionality soon.
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
   }


    // onClick to get rid of error message. Does nothing
    public void onClick(View view) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password) {
        if (password == null) {
            return false;
        } else if (password.length() < 8) {
            return false;
        } else if (password.contains(" ")) {
            return false;
        } else if (!password.matches("[A-Z0-9]")) {
            return false;
        } else if (password.contains(" ")) {
            return false;
        } else if (!password.matches("[!@#$%^&*]")) {
            return false;
        }
        return true;
    }

    public static void alertSet(final FirebaseAuth mAuth, final FirebaseFirestore db, final Context context, final AlarmManager alarmManager) {
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
                            Map<String,String> alerts = new HashMap<>();
                            alerts.put("alertsSetUp", "false");
                            alerts.put("alertsTurnedOff","false");
                            alerts.put("email", mAuth.getCurrentUser().getEmail());
                            db.collection("users").document(mAuth.getUid()).set(alerts, SetOptions.merge());
                            //set up alerts (achieved in function)

                        }
                        Boolean setup = Boolean.parseBoolean(document.getString("alertsSetUp"));
                        Boolean exempt = Boolean.parseBoolean(document.getString("alertsTurnedOff"));
                        if (setup  == null || exempt == null) {
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
                                //Here we make a sample notification, and set an alert one day from now. (TODO: change to repeating alert)
                                //We will specify what the notification is looking for, and get fresh data, when the alert receiver is called
                                //
                                ComponentName receiver = new ComponentName(context, AlertReceiver.class);
                                PackageManager pm = context.getPackageManager();

                                pm.setComponentEnabledSetting(receiver,
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP);
                                Toast.makeText(context, "Setting up alerts", Toast.LENGTH_SHORT).show();
/*
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,intent,0);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000,pendingIntent);
*/

                                Intent notificationIntent = new Intent(context,AlertReceiver.class);
                                notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ID,1);
                                Notification n;
                                Intent budgRedirect = new Intent(context,LoginActivity.class);
                                PendingIntent mainIntent = PendingIntent.getActivity(context,1,budgRedirect,PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"BudgetAlert");
                                builder.setContentTitle("Budget Checkup");
                                builder.setContentText("placeholder info about budget here");
                                builder.setSmallIcon(R.drawable.ic_launcher_background);
                                builder.setContentIntent(mainIntent);
                                builder.setAutoCancel(true);
                                n = builder.build();
                                notificationIntent.putExtra(AlertReceiver.NOTIFICATION,n);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                                //!!!!!!!!!!!!FOR TESTING NOTIFICATIONS==== SET FUTUREMILLIS TO elapsed time + 10000 for a ten second notification
                                long futureMillis = SystemClock.elapsedRealtime() + TimeUnit.DAYS.toMillis(1);//10000;
                                //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);
                                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureMillis, AlarmManager.INTERVAL_DAY,pendingIntent);//,pendingIntent);10000
                                System.out.println("Set up alarm for " + (SystemClock.elapsedRealtime() + TimeUnit.DAYS.toMillis(1)));//10000));//

                                Map<String,String> alerts = new HashMap<>();
                                alerts.put("alertsSetUp", "true");
                                alerts.put("alertsTurnedOff","false"); //7:55 test time
                                alerts.put("email", mAuth.getCurrentUser().getEmail());
                                db.collection("users").document(mAuth.getUid()).set(alerts, SetOptions.merge());

                            }
                        }





                    }
                }
            }
        });



    }

}
