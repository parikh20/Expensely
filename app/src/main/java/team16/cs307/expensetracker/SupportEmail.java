package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SupportEmail extends AppCompatActivity {
    private EditText textsubject,textcontent;
    private Button btnsend;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_email);
        textsubject = (EditText)findViewById(R.id.SupportEmail_Subject);
        textsubject .setFilters(new InputFilter[]{ new InputFilter.LengthFilter(78) });
        textcontent = (EditText)findViewById(R.id.SupportEmail_Content);
        textcontent.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(998) });
        btnsend = (Button) findViewById(R.id.SupportEmail_Send);
        mAuth = FirebaseAuth.getInstance();



        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToExpensly();
            }
        });

    }


    private void sendEmailToExpensly(){
            if(textcontent.getText().toString().equals("")){
                Toast.makeText(SupportEmail.this,"Message can't be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            String email[] = {"expenselycs307@gmail.com"};
            String subject = "Expensely user ("+mAuth.getCurrentUser().getEmail()+"): "+textsubject.getText().toString();
            String message = textcontent.getText().toString();

            Intent mEmailIntent = new Intent(Intent.ACTION_SEND);
            mEmailIntent.putExtra(Intent.EXTRA_EMAIL,email);
            mEmailIntent.putExtra(Intent.EXTRA_SUBJECT ,subject);
            mEmailIntent.putExtra(Intent.EXTRA_TEXT,message);
            mEmailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(mEmailIntent,"Choose a emaill app"));







    }

}
