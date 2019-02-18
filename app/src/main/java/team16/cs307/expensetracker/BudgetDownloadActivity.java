package team16.cs307.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BudgetDownloadActivity extends AppCompatActivity {

    //budg_curr displays the name of the currently selected budget
    //budg_curr_details displays the stats of this budget (i.e. limits, source, etc)
    //User should be able to browse current budgets TODO
    //User should be able to input details for a custom budget from this page TODO


    private TextView mBudg_curr;
    private TextView mBudg_curr_details;
    private Button mBudg_browse;
    private Button mBudg_custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_download);
        mBudg_browse =  findViewById(R.id.budg_browse);
        mBudg_curr =  findViewById(R.id.budg_curr);
        mBudg_curr_details = findViewById(R.id.budg_curr_details);
        mBudg_custom = (Button) findViewById(R.id.budg_custom);



        //Defaults
        String budg_curr = "Current Budget: No Active Budget";
        String budg_curr_details = "Budget Type: None \nLimits: \n\t Weekly: $0 \n\t Monthly: $0 \n\t Yearly: $0 \n ";

        /*
        TODO:
        if (user already has a budget attached to their account & active)
            get name of budget
            get details of budget
            get type of budget: i.e. custom, template, external
            then, set the values of curr and curr details appropriately
                this will also refresh when the user returns from picking a new budget
         */

        mBudg_curr.setText(String.valueOf(budg_curr));
        mBudg_curr_details.setText(String.valueOf(budg_curr_details));


        mBudg_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseBudgets();
            }
        });

        mBudg_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomBudget();
            }
        });


    }

    private void browseBudgets() {
        /* TODO: redirect to budget browse screen
        Intent intent = new Intent(getApplicationContext(), BrowseBudgets.class);
        startActivity(intent);
        */
    }
    private void createCustomBudget() {
        /* TODO: redirect to custom budget creation screen
        Intent intent = new Intent(getApplicationContext(), BrowseBudgets.class);
        startActivity(intent);
        */
    }
}
