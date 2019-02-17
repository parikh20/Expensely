package team16.cs307.expensetracker;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImageActivityTest {

    @Test
    public void onCreate() {
        /*
        1) Test all findViewById for NULL
        2) Test FirebaseStorage.getInstance() for NULL or unexpected value
        3) Test storage.getReference for NULL or unexpected value
         */
    }

    @Test
    public void onActivityResult() {
        /*
        1) requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null
        2) requestCode != PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null
        3) requestCode == PICK_IMAGE_REQUEST && resultCode != RESULT_OK && data != null && data.getData() != null
        4) requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data == null && data.getData() != null
        5) requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() == null
         */
    }
}