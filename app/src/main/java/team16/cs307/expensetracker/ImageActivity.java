package team16.cs307.expensetracker;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private TextView tag;
    private ImageView imageview;
    private Uri filePath;
    ProgressDialog pd;
    private String timeStamp;
    private Date date;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String textTag= "";




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
        tag = (TextView) findViewById(R.id.ImageActivity_Tag);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        tag.setText("Tag: None");


        //camera need




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
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTag();
            }
        });
    }



        //change tag
        private void changeTag(){
            final EditText edittext = new EditText(ImageActivity.this);
            edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            new AlertDialog.Builder( ImageActivity.this )
                    .setTitle( "Tag" )
                    .setMessage( "Change your tag (Max Length: 6)" )
                    .setView(edittext)
                    .setPositiveButton( "Change", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            textTag = edittext.getText().toString();
                            tag.setText("Tag:"+textTag);

                        }
                    })
                    .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    } )
                    .show();


        }
        //choose image from gallery
        private void chooseImage(){
             //choose images from gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            //startActivityForResult(intent,PICK_IMAGE_REQUEST);

            //Choose multiple images from file explorer
            //Intent intent = new Intent();
            //intent.setType("image/*");
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            //intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
        }

        //camera
        private void imageCamera(){
                /*value = new ContentValues();
                value.put(MediaStore.Images.Media.TITLE, "New Picture");
                value.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                cameraImgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,value);*/

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if(cameraFile !=null){
                    //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cameraFile));

                startActivityForResult(cameraIntent, CAMERA_REQUEST);



                //}


        }
        @Override
        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
                /*if(data.getClipData()!=null){
                    Toast.makeText(ImageActivity.this,"multiple",Toast.LENGTH_SHORT).show();
                }else */
                if(data.getData() != null){
                    filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        imageview.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

            }
            else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK ){
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                imageview.setImageBitmap(bmp);
                //filePath = getImageUri(getApplicationContext(),bmp);
                //imageview.setImageURI(filePath);
                //imageview.setImageBitmap(bmp);
                //filePath=getRealPathFromURI()
                //filePath=getImageUri(getApplicationContext(),bmp);


            }
        }

        private void uploadImage(){
                if(filePath!=null){
                    pd.show();
                    timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
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
                                map.put("tag",textTag);
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
    /*public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //delete photo


    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }*/


}



