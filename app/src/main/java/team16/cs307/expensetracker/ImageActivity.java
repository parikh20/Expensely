package team16.cs307.expensetracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.String.*;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageActivity extends AppCompatActivity {
    private Button choose,upload,camera;
    private ImageView imageview;
    private Uri filePath;
    ProgressDialog pd;
    private String timeStamp;
    private Date date;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private final int PICK_IMAGE_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mAuth = FirebaseAuth.getInstance();
        //Initialize
        db = FirebaseFirestore.getInstance();
        choose = (Button) findViewById(R.id.ImageActivity_Choose);
        upload = (Button) findViewById(R.id.ImageActivity_Upload);
        camera = (Button) findViewById(R.id.ImageActivity_Camera);
        imageview = (ImageView) findViewById(R.id.ImageActivity_imgView);
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
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCamera();
            }
        });
    }
        //choose image from gallery
        private void chooseImage(){
             //choose images from gallery
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            //startActivityForResult(intent,PICK_IMAGE_REQUEST);

            //Choose multiple images from file explorer
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            //intent.putExtra(, 5);
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        }

        //camera
        private void imageCamera(){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);

        }
        @Override
        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
                if(data.getClipData()!=null){
                    Toast.makeText(ImageActivity.this,"multiple",Toast.LENGTH_SHORT).show();
                }else if(data.getData() != null){
                    filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        imageview.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

            }
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK ){
                Toast.makeText(ImageActivity.this,"camera test",Toast.LENGTH_SHORT).show();
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imageview.setImageBitmap(bmp);
                //filePath=getImageUri(getApplicationContext(),bmp);

            }
        }

        private void uploadImage(){
                if(filePath!=null){
                    pd.show();
                    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    final StorageReference ref = storageReference.child(mAuth.getUid()+"/"+timeStamp);
                    final UploadTask uploadTask = ref.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task <Uri> downloadUriTask = ref.getDownloadUrl();
                            while (!downloadUriTask.isSuccessful());
                            Uri downloadUri = downloadUriTask.getResult();
                            if (downloadUri !=null) {
                                String imgurl = downloadUri.toString().substring(downloadUri.toString().lastIndexOf('/') + 1);;
                                Map<String, Object> map = new HashMap<>();
                                map.put("imgurl", imgurl);
                                map.put("date",timeStamp);
                                db.collection("users").document(mAuth.getUid()).collection("images").document(imgurl).set(map);

                            }

                            pd.dismiss();
                            Toast.makeText(ImageActivity.this, "Uploaded" , Toast.LENGTH_SHORT).show();
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
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //delete photo


}



