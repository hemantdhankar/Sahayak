package com.example.sahayak;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class RaiseFeature extends AppCompatActivity {
    String[] items =  {"Transport","Electricity","Food","Water","Sewage","Roads","Animals","Old Citizens","Garbage","Donations"};
    protected AutoCompleteTextView autoCompleteTxt;
    protected ArrayAdapter<String> adapterItems;
    protected EditText desc_view,pincode_view;
    protected FirebaseFirestore database;
    protected ImageView image_view;
    protected StorageReference storage_ref;
    protected byte[] bb;
    protected String path;
    protected Uri filePath;
    public static int no_of_post=0;
    private final int PICK_IMAGE_REQUEST = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_feature);

        // setting the drop down for category
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        desc_view=(EditText)findViewById(R.id.desc_box);
        pincode_view=(EditText)findViewById(R.id.pincode_box);
        image_view=(ImageView)findViewById(R.id.image_box);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item_dropdown,items);
        autoCompleteTxt.setAdapter(adapterItems);

        //database instance
        database = FirebaseFirestore.getInstance();
        storage_ref = FirebaseStorage.getInstance().getReference();

        // to get the id clicked item in dropdown
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void select_image(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."),PICK_IMAGE_REQUEST);
    }
    public void camera_click(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode==101){
                onCaptureimage(data);
                uploadtobase();
            }
            else if(requestCode==PICK_IMAGE_REQUEST){
                filePath = data.getData();
                try {
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    image_view.setImageBitmap(bitmap);
                    upload_from_gallery();
                }
                catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        }
    }
    public void upload_from_gallery(){
        if (filePath != null) {
            String pincode = pincode_view.getText().toString();
            // Code for showing progressDialog while uploading
            path=pincode+"/"+UUID.randomUUID().toString();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storage_ref.child(path);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(RaiseFeature.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            progressDialog.dismiss();
                            Toast.makeText(RaiseFeature.this, "Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    public void onCaptureimage(Intent data){
            Bitmap thumbnail=(Bitmap)data.getExtras().get("data");
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG,90,bytes);
            bb=bytes.toByteArray();
            image_view.setImageBitmap(thumbnail);
    }
    public void uploadtobase(){
        String pincode = pincode_view.getText().toString();
        path=pincode+"/"+UUID.randomUUID().toString();
        StorageReference mountainImagesRef = storage_ref.child(path);
        UploadTask uploadTask = mountainImagesRef.putBytes(bb);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    //to save issue to database
    public void raise_issue(View v){
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String description = desc_view.getText().toString();
            String pincode = pincode_view.getText().toString();
            String category = autoCompleteTxt.getText().toString();
            HashMap<String, String> map = new HashMap<>();
            map.put("description", description);
            map.put("pin_code", pincode);
            map.put("category", category);
            map.put("image_path",path+".JPEG");
            //map.put("id",user.getEmail());
            database.collection("Issue_detail").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("donee", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("fialedd", "Error adding document", e);
                        }
                    });
            desc_view.setText("");
            pincode_view.setText("");
            path="";
            no_of_post++;
    }
}