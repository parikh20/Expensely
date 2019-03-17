package team16.cs307.expensetracker;

public class Note {



    private String date;
    private String imgurl;


    private String tag;


    public Note() {
        //empty constructor needed
    }

    public Note( String date,String imgurl,String tag) {
        this.imgurl = imgurl;
        this.date=date;
        this.tag=tag;

    }

    public String getImgurl() {
        return imgurl;
    }
    public String getDate() {
        return date;
    }
    public String getTag() {
        return tag;
    }




}