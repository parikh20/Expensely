package team16.cs307.expensetracker;

public class Preferences {

    private boolean darkMode;
    private int fontSize;
    private String colorScheme;
    private String defaultGraph;
    private int defaultBudgetNum;

    Preferences(){
        this(false,12,"Green","Pie",1);

    }
    Preferences(Boolean darkMode,int fontSize,String colorScheme,String defaultGraph,int defaultBudgetNum){
        this.darkMode = darkMode;
        this.fontSize = fontSize;
        this.colorScheme = colorScheme;
        this.defaultGraph = defaultGraph;
        this.defaultBudgetNum = defaultBudgetNum;


    }
    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    public String getDefaultGraph() {
        return defaultGraph;
    }

    public void setDefaultGraph(String defaultGraph) {
        this.defaultGraph = defaultGraph;
    }

    public int getDefaultBudgetNum() {
        return defaultBudgetNum;
    }

    public void setDefaultBudgetNum(int defaultBudgetNum) {
        this.defaultBudgetNum = defaultBudgetNum;
    }



}
