package team16.cs307.expensetracker;

public class epTime {

    private long epochSecond;
    private long nano;

    public epTime() {

    }

    public epTime(long epochSecond, long nano) {
        this.epochSecond = epochSecond;
        this.nano = nano;
    }

    public long getEpochSecond() {
        return epochSecond;
    }

    public void setEpochSecond(long epochSecond) {
        this.epochSecond = epochSecond;
    }

    public long getNano() {
        return nano;
    }

    public void setNano(long nano) {
        this.nano = nano;
    }
}
