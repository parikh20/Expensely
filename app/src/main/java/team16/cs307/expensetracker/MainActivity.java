package team16.cs307.expensetracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private TextView mStats;

    private Button selectbudg;
    private Button editExpenses;
    private Button addExp;
    private Button imageAccess;
    private Button account;
    private FirebaseAuth mAuth;
    private GraphView mGraph;
    private FirebaseFirestore db;
    private String statBlock;
    private PieChart mChart;
    private Button mSwap;
    private Button editBudget;
    private Button becomeUser;


    private Budget curr_budg;

    private double perday;
    private boolean isAboveLimit;
    private double amt;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStats = findViewById(R.id.quick_stats);
        mGraph = findViewById(R.id.main_graph);
        selectbudg = findViewById(R.id.MainActivity_select_budg);
        editBudget = findViewById(R.id.MainActivity_edit_budget);
        addExp = findViewById(R.id.main_new_expense);
        imageAccess = findViewById(R.id.MainActivity_ImageAccess);
        account = findViewById(R.id.main_account);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mChart = findViewById(R.id.main_chart);
        mSwap = findViewById(R.id.main_swapchart);
        perday = 0;
        amt = 0;
        editExpenses = findViewById(R.id.MainActivity_edit_expenses);
        becomeUser = findViewById(R.id.becomeUser);
        message = "0";





        //update Time and monthly totals
        DocumentReference refM = db.collection("users").document(mAuth.getUid());
        refM.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //NOTE:  THIS IMPLEMENTATION RELIES ON A ONCE A YEAR LOGIN
                //IF THE  USER DOES NOT LOG IN FOR A YEAR, MONTHLY TOTALS WILL NOT UPDATE UNTIL THE FIRST EXPENSE OF THAT YEAR  on a different month IS RECORDED
                //todo: fix this - add a year to lastupdated
                if (documentSnapshot.get("LastUpdated") == null || (!documentSnapshot.get("LastUpdated").equals(LocalDateTime.now().getMonth().toString()))) {


                    Map<String, String> userinfo = new HashMap<>();
                    userinfo.put("Monthly Total", String.valueOf(0));
                    userinfo.put("LastUpdated", LocalDateTime.now().getMonth().toString());
                    db.collection("users").document(mAuth.getUid()).set(userinfo, SetOptions.merge());
                }
            }
        });


        //set up mchart
        mChart.setVisibility(View.INVISIBLE); //Invisible at start, to be added here: check user settings for default graph, make that one visible

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
                    //setting quick stat block here
                    statBlock += "Current Budget Limits:\n";
                    statBlock += "Weekly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitWeekly());
                    statBlock += "\nMonthly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitMonthly());
                    statBlock += "\nYearly: $" + NumberFormat.getNumberInstance().format((int) curr_budg.getLimitYearly());
                    if (!curr_budg.getCustomLimits().isEmpty()) {
                        statBlock += "\nCustom Limits: \n";

                        StringBuilder statB = new StringBuilder();
                        for (Limit l : curr_budg.getCustomLimits()) {

                            statB.append(l.getCategory());
                            statB.append(": $" + NumberFormat.getNumberInstance().format((int) l.getLimitMonthly()) + " Monthly\n");
                        }

                        statBlock += statB;
                    }
                    //Now setting up line graph to display monthly budget
                    mGraph.setTitle("Your Monthly Budget: " + curr_budg.getName());
                    mStats.setText(statBlock);
                    //all changes to the graph here will take effect after first interaction

                    int endday = LocalDate.now().lengthOfMonth();
                    double monthlim = curr_budg.getLimitMonthly();
                    perday = monthlim / endday;
                    ArrayList<DataPoint> points = new ArrayList<>();
                    for (int x = 0; x < endday; x++) {
                        points.add(new DataPoint(ZonedDateTime.now().withDayOfMonth(x + 1).toEpochSecond(), perday * (x + 1)));
                    }
                    DataPoint[] parray = new DataPoint[points.size()];
                    LineGraphSeries<DataPoint> currBudget = new LineGraphSeries<>(points.toArray(parray));
                    currBudget.setTitle("Your Budget");
                    mGraph.addSeries(currBudget);
                    //Performing a double check here in case the other series completed first.  (usual case)
                    if (amt > perday * LocalDate.now().getDayOfMonth()) {
                        currBudget.setColor(Color.RED);
                        currBudget.setTitle("Your Budget (Over Budget!)");

                    } else if (amt != 0) {
                        currBudget.setColor(Color.GREEN);
                        currBudget.setTitle("Your Budget (Under Budget)");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No current budget", Toast.LENGTH_LONG).show();
                    mStats.setText("No Current Budget: Try creating one or downloading a template!\n" +
                            "Current Expense Weekly Total: Unimplemented\n" +
                            "Current Expense Monthly Total: Unimplemented");

                }
            }
        });

        //Get expenses, and add them to the line graph and pie chart
        CollectionReference refE = db.collection("users").document(mAuth.getUid()).collection("Expenses");
        refE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                int x = 0;
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    x++;
                }
                isAboveLimit = false;  //Tracks if you're currently above your limit (depends on other task finishing first, checked in both and acted upon)

                amt = 0;  //Total amount spent monthly to date
                ArrayList<DataPoint> ie = new ArrayList<>();
                ArrayList<Expense> ei = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    //Adding each expense to an expense array, so we can sort the array by date before even worrying about adding it to graphs
                    Expense e = doc.toObject(Expense.class);
                    ei.add(e);

                }
                Collections.sort(ei);  //Sorting by custom compare in Expense.java (by date, not by amount!!!)
                ArrayList<String> categoryList = new ArrayList<>(); //list of monthly categories
                ArrayList<Double> categoryValues = new ArrayList<>(); //list of monthly totals
                //Note: We only care about the primary category of each expense for the purposes of the pie chart.  Limit calculation and comparison might produce different results

                boolean FutureThisMonth = false;
                for (Expense e : ei) {

                    Instant inst = Instant.ofEpochSecond(e.getTime());
                    ZonedDateTime zdt = ZonedDateTime.ofInstant(inst, ZonedDateTime.now().getZone());
                    //if it's in the current month and isn't an outlier (Behaved weirdly when i tried just checking getMonthvalue == getMonthValue.  This wasn't much harder)
                    if (zdt.isAfter(ZonedDateTime.now().withDayOfMonth(1)) && !e.getOutlierMonthly() && zdt.isBefore(ZonedDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).plusDays(1))) {
                        //Add it to the line graph
                        if (zdt.isAfter(ZonedDateTime.now())) {
                            FutureThisMonth = true;
                        }

                        double am = e.getAmount();
                        amt += am;
                        long tim = e.getTime();
                        //add expense data point on date
                        ie.add(new DataPoint(tim, amt));
                        Instant i = Instant.ofEpochSecond(tim);
                        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZonedDateTime.now().getZone());
                        double budgetcheck = perday * (z.getDayOfMonth() - 1);
                        isAboveLimit = (budgetcheck < amt) && budgetcheck != 0;

                        //Add it to the category list
                        if (e.getTags() == null || e.getTags().isEmpty()) {
                            boolean found = false;
                            int itr = 0;
                            for (String str : categoryList) {
                                if (str.equals("Unassigned")) {
                                    categoryValues.set(itr, categoryValues.get(itr) + am);
                                    found = true;
                                }
                                itr++;
                            }
                            if (!found) {
                                categoryList.add("Unassigned");
                                categoryValues.add(am);
                            }
                        } else {
                            String cat = e.getTags().get(0);
                            boolean found = false;
                            int itr = 0;
                            for (String str : categoryList) {
                                if (cat.equals(str)) {
                                    categoryValues.set(itr, categoryValues.get(itr) + am);
                                    found = true;
                                }
                                itr++;
                            }
                            if (!found) {
                                categoryList.add(cat);
                                categoryValues.add(am);
                            }
                        }
                    }
                }
                //set up and complete series for line graph
                //add additional points for current day and first day of month.

                if (!FutureThisMonth) {
                    ie.add(new DataPoint(ZonedDateTime.now().toEpochSecond(), amt));
                }

                ie.add(new DataPoint(ZonedDateTime.now().withDayOfMonth(1).toEpochSecond(),0));

                ie.add(new DataPoint(ZonedDateTime.now().toEpochSecond(), amt));

                DataSort(ie);
                DataPoint[] de = new DataPoint[ie.size()];
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(ie.toArray(de));
                //series.setColor(Color.GREEN);

                //watch for being above limit, set graph line to red if so.  If isabovelimit is true here, we know this task finished second (edge case, not usual)
                if (isAboveLimit) {
                    series.setColor(Color.RED);
                } else if (perday != 0) {
                    series.setColor(Color.GREEN);
                }
                series.setTitle("Your Spending");
                if (!FutureThisMonth) {
                    series.setTitle("Your Spending (Projected)");
                }
                mGraph.addSeries(series);

                //set up and complete series for pie chart
                List<PieEntry> entries = new ArrayList<>();
                int itr = 0;
                for (String str : categoryList) {
                    entries.add(new PieEntry((float) ((categoryValues.get(itr)) / amt), str));
                    itr++;
                }


                //colors used are on a rotating array designed for a maximum of 6 categories.  We can add other colors to the array to prevent color clash on larger sets
                PieDataSet set = new PieDataSet(entries, "Your Monthly Categories");
                int[] colors = new int[]{Color.GREEN, Color.rgb(128, 100, 10), Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN};
                if (categoryList.contains("Unassigned")) {
                    colors[categoryList.indexOf("Unassigned") % 6] = Color.GRAY;
                }
                set.setColors(colors);
                PieData data = new PieData(set);
                mChart.setData(data);
                mChart.invalidate();//This will refresh the chartview


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

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(d1, 1),
                new DataPoint(d2, 2)
        });
        //mGraph.addSeries(series);
        mGraph.getViewport().setMinX(ZonedDateTime.now().withDayOfMonth(1).toEpochSecond());
        mGraph.getViewport().setMaxX(d2);
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(0);
        mGraph.getGridLabelRenderer().setTextSize(12);
        mGraph.getLegendRenderer().setVisible(true);
        mGraph.getLegendRenderer().setFixedPosition(0, 0);
        mGraph.getLegendRenderer().setWidth(500);

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


        editExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditExpensesActivity.class);
                startActivity(intent);

            }
        });


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

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountInfo();
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


        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditBudgetActivity.class);
                startActivity(intent);

            }
        });

        //Become a new user for anonymous user


        becomeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              message = "1";
              Intent intent = new Intent(getApplicationContext(), CreateNewAccountActivity.class);
              intent.putExtra("message", message);
              startActivity(intent);
              finish();
              message = "0";
            }
        });
    }

    private void addExpense() {
            Intent intent = new Intent(getApplicationContext(), CustomExpense.class);
            startActivity(intent);


        }
        private void accessImage(){
            Intent intent = new Intent(getApplicationContext(),ImageShow.class);
            startActivity(intent);
        }
        private void accountInfo(){
            Intent intent = new Intent(getApplicationContext(),AccountInfo.class);
            startActivity(intent);
        }



        public ArrayList<DataPoint> DataSort (ArrayList<DataPoint> ie ) {

            for (int i = 0; i < ie.size(); i++) {
                for (int j = 0; j < ie.size() - i - 1; j++) {
                    if (ie.get(j).getX() > ie.get(j + 1).getX()) {
                        DataPoint temp = ie.get(j);
                        ie.set(j, ie.get(j + 1));
                        ie.set(j + 1, temp);
                    }
                }
            }

            return ie;
        }

        private void selectBudget()
        {
            Intent intent = new Intent(getApplicationContext(), BudgetDownloadActivity.class);
            startActivity(intent);
        }

   // add option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Menu_Add_New_Expense:
                addExpense();
                return true;
            case R.id.Menu_Budget_Select:
                selectBudget();
                return true;
            case R.id.Menu_Current_Budget:
                editCurrentBudget();
                return true;
            case R.id.Menu_Past_Expense:
                editPastExpense();
                return true;
            case R.id.Menu_Image_Acess:
                accessImage();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    void editCurrentBudget(){
        Intent intent = new Intent(getApplicationContext(), EditBudgetActivity.class);
        startActivity(intent);

    }
    void editPastExpense(){
        Intent intent = new Intent(getApplicationContext(), EditExpensesActivity.class);
        startActivity(intent);

    }
}



