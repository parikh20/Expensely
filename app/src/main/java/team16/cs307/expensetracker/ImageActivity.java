package team16.cs307.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageActivity extends AppCompatActivity {
    private Button choose,upload;
    private ImageView imageview;
    private Uri filePath;
    ProgressDialog pd;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Initialize
        choose = (Button) findViewById(R.id.Choose);
        upload = (Button) findViewById(R.id.Upload);
        imageview = (ImageView) findViewById(R.id.imgView);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

        private void chooseImage(){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            //intent.setType("imgae/*");
            //intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
        }
        @Override
        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }

        private void uploadImage(){
                if(filePath!=null){
                    pd.show();
                    StorageReference ref = storageReference.child("test image");
                    UploadTask uploadTask = ref.putFile(filePath);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(ImageActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ImageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });}
                    else{
                    Toast.makeText(ImageActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }

    }

}



