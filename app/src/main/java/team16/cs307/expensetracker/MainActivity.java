package team16.cs307.expensetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button uploadpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadpic = (Button) findViewById(R.id.photoupload);


        //upload photo
        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }


        private void uploadImage(){
            Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
            startActivityForResult(intent,1);
        }
    }

