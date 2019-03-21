package team16.cs307.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CustomCategoryCreation extends AppCompatActivity {
    private EditText mType;
    private Button mFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_category_creation);

        mFinish = findViewById(R.id.custom_category_finish);
        mType = findViewById(R.id.custom_category_type);



        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.getText() == null) {
                    Toast.makeText(CustomCategoryCreation.this, "Please enter your category", Toast.LENGTH_SHORT).show();
                    return;
                }
                String type = mType.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("type",type);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
