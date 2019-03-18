package team16.cs307.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TextView mStats;

    private Button selectbudg;
    private Button addExp;
    private Button imageAccess;
    private Button logout;
    private FirebaseAuth mAuth;
    private GraphView mGraph;
    private FirebaseFirestore db;
    private String statBlock;
    private PieChart mChart;
    private Button mSwap;


    private Budget curr_budg;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStats = findViewById(R.id.quick_stats);
        mGraph = findViewById(R.id.main_graph);
        selectbudg = findViewById(R.id.MainActivity_select_budg);
        addExp = findViewById(R.id.main_new_expense);
        imageAccess = findViewById(R.id.MainActivity_ImageAccess);
        logout = findViewById(R.id.main_logout);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mChart = findViewById(R.id.main_chart);
        mSwap = findViewById(R.id.main_swapchart);




        //set up mchart
        mChart.setVisibility(View.INVISIBLE); //Invisible at start, to be added here: check user settings for default graph, make that one visible
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Brown"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));


        PieDataSet set = new PieDataSet(entries, "Your Monthly Categories");
        int [] colors = new int[]{Color.GREEN,  Color.rgb(128, 100, 10), Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN};
        set.setColors(colors);
        PieData data = new PieData(set);
        mChart.setData(data);
        mChart.invalidate();//This will refresh the chartview





        //mGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        statBlock = "";
        //get current budget
        DocumentReference ref = db.collection("users").document(mAuth.getUid()).collection("Preferences").document("Current Budget");
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                curr_budg = documentSnapshot.toObject(Budget.class);
                if (curr_budg != null) {
                    Toast.makeText(MainActivity.this, "found a current budget: " + curr_budg.getName(), Toast.LENGTH_SHORT).show();

                    statBlock += "Current Budget Limits:\n";
                    statBlock += "Weekly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitWeekly());
                    statBlock += "\nMonthly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitMonthly());
                    statBlock += "\nYearly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitYearly());
                    if (!curr_budg.getCustomLimits().isEmpty()) {
                        statBlock += "\nCustom Limits: \n";

                        StringBuilder statB = new StringBuilder();
                        for (Limit l : curr_budg.getCustomLimits()) {

                            statB.append(l.getCategory());
                            statB.append(": $" + NumberFormat.getNumberInstance().format((int) l.getLimitWeekly()) + " Weekly\n");
                        }

                        statBlock += statB;
                    }
                    mGraph.setTitle("Your Weekly Budget: " + curr_budg.getName());
                    mStats.setText(statBlock);
                    //all changes to the graph here will take effect after first interaction: Might have to move whole graph element in here

                } else {
                    Toast.makeText(MainActivity.this, "No current budget", Toast.LENGTH_LONG).show();
                    mStats.setText("No Current Budget: Try creating one or downloading a template!\n" +
                            "Current Expense Weekly Total: Unimplemented\n" +
                            "Current Expense Monthly Total: Unimplemented");

                }
            }
        });

        CollectionReference refE = db.collection("users").document(mAuth.getUid()).collection("Expenses");
        refE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                int x = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    x++;
                }
                DataPoint[] jd = new DataPoint[x];
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Expense e = doc.toObject(Expense.class);
                    //add expense data point on date

                }


                //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(jd);
                //mGraph.addSeries(series);

            }
         });

        //graph settings
        GridLabelRenderer glr = mGraph.getGridLabelRenderer();
        glr.setPadding(32);
        mGraph.getViewport().setScalable(true);
        mGraph.getViewport().setScalableY(true);




        //test graph for purposes of messing with graph API:

        long d1 = ZonedDateTime.now().toEpochSecond();
        long d2 = ZonedDateTime.now().plusDays(7).toEpochSecond();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 2)
        });
        mGraph.addSeries(series);
        mGraph.getViewport().setMinX(ZonedDateTime.now().withDayOfMonth(1).toEpochSecond() );
        mGraph.getViewport().setMaxX(d2);
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(3);

        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    long l = (long) value;
                    Instant i = Instant.ofEpochSecond(l);
                    ZonedDateTime dateTime = i.atZone(ZoneId.systemDefault());

                    return dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear();
                }
                return super.formatLabel(value, isValueX);


            }
        });

        mGraph.getViewport().setXAxisBoundsManual(true);






        selectbudg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBudget();
            }
        });

        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        imageAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessImage();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        mSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGraph.getVisibility() == View.INVISIBLE) {
                    mGraph.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.INVISIBLE);
                } else {
                    mGraph.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.VISIBLE);
                }
                mChart.invalidate();

            }
        });










    }


    private void addExpense() {
            Intent intent = new Intent(getApplicationContext(), CustomExpense.class);
            startActivity(intent);
        }

        private void selectBudget() {
            Intent intent = new Intent(getApplicationContext(), BudgetDownloadActivity.class);
            startActivity(intent);

        }
        private void accessImage(){
            Intent intent = new Intent(getApplicationContext(),ImageShow.class);
            startActivityForResult(intent,1);
        }
        private void logout() {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

