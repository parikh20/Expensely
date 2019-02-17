package team16.cs307.expensetracker;


//import java.time.Clock;
//import java.time.ZonedDateTime;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.threeten.bp.Instant;

/*Expense:
    An expense or purpose serves as a singular entry within a user's finances, one set of purchases or payment.
    An expense consists of:
        - Repeating Bit : True if this is a repeating expense, for classification
        - Date/Time : when this purchase or payment was made or is scheduled to be made
        - Amount : amount of purchase in total, in USD
        - Category : A string custom category for the user to define upon entering the expense, for classification
        - Priority : An integer labelling the priority of this payment for alerts and budgets
        - outlierMonthly : If true, the monthly budget will not factor this payment in when determining if you are over or under budget.  (To be used only on massively large one-time payments the user wishes to remain on a yearly scale
        - outlierWeekly : If true, the weekly budget will not factor this payment in when determining if the user is over budget
        Functions:
            - Get/Set for all
            - modifyCategories : allows the user to add/remove additional categories


 */
public class Expense {
    private Boolean repeating;
    private Instant time;
    private double amount;
    private ArrayList<String> tags;
    private int priority;
    private Boolean outlierMonthly;
    private Boolean outlierWeekly;


    //default constructor
    public Expense() {
        this.repeating = false;
        this.time = Instant.now();
        this.amount = 0;
        this.tags = new ArrayList<>();
        this.priority = 0;
        this.outlierMonthly = false;
        this.outlierWeekly = false;
    }
    //non-default Constructor
    public Expense (Boolean repeating, Instant time, double amount, ArrayList<String> tags, int priority, Boolean outlierMonthly, Boolean outlierWeekly) {
        this.repeating = repeating;
        this.time = time;
        this. amount = amount;
        this.tags = tags;
        this.priority = priority;
        this.outlierMonthly = outlierMonthly;
        this.outlierWeekly = outlierWeekly;
    }

    public Boolean getRepeating() {
        return repeating;
    }

    public void setRepeating(Boolean repeating) {
        this.repeating = repeating;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean getOutlierMonthly() {
        return outlierMonthly;
    }

    public void setOutlierMonthly(Boolean outlierMonthly) {
        this.outlierMonthly = outlierMonthly;
    }

    public Boolean getOutlierWeekly() {
        return outlierWeekly;
    }

    public void setOutlierWeekly(Boolean outlierWeekly) {
        this.outlierWeekly = outlierWeekly;
    }


    //modifyCategories : Takes in an array list of tags, adds those that are not currently in the tag list.
    public void modifyCategories(ArrayList<String> str) {
        for (String s : str) {
            if (!this.tags.contains(s)) {
                this.tags.add(s);
            }
        }
    }
    public void removeCategories(ArrayList<String> str) {
        for (String s : str) {
            if (this.tags.contains(s)) {
                this.tags.remove(s);
            }
        }
    }
}
