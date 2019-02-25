package team16.cs307.expensetracker;

public class Note {




    private String imgurl;



    private String Description;

    public Note() {
        //empty constructor needed
    }

    public Note( String imgurl,String Description) {
        this.imgurl = imgurl;
        this.Description=Description;

    }

    public String getimgurl() {
        return imgurl;
    }
    public String getDescription() {
        return Description;
    }


}