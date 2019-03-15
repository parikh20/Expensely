package team16.cs307.expensetracker;

public class Note {



    private String date;
    private String imgurl;


    public Note() {
        //empty constructor needed
    }

    public Note( String date,String imgurl) {
        this.imgurl = imgurl;
        this.date=date;

    }

    public String getImgurl() {
        return imgurl;
    }
    public String getDate() {
        return date;
    }


}