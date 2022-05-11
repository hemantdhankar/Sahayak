package com.example.sahayak;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddIssueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddIssueFragment extends Fragment {
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
    View root_view;
    Button Camera_Button,Upload_Button,Raise_Button;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddIssueFragment() {
        // Required empty public constructor
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
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
                                    getContext().getContentResolver(),
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
            path=pincode+"/"+ UUID.randomUUID().toString();
            ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                            Toast.makeText(getContext(),"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String description = desc_view.getText().toString();
        String pincode = pincode_view.getText().toString();
        String category = autoCompleteTxt.getText().toString();
        String error_msg = Validator.validate_issue_raised_data(category,description,pincode);
        if(error_msg!=null)
        {
            Toast.makeText(getContext(),error_msg,Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", description);
        map.put("pin_code", pincode);
        map.put("category", category);
        map.put("image_path",path+".JPEG");
        map.put("email",user.getEmail());
        map.put("number_of_likes",String.valueOf(0));
        map.put("Status","Unclaimed");
        ArrayList<String> likers_initial= new ArrayList<>();
        likers_initial.add("initial");
        map.put("likers",likers_initial);


        database.collection("Issue_detail").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("IssuesRaised", "DocumentSnapshot added with ID: " + documentReference.getId());
                Toast.makeText(getContext(),"Issue raised successfully!",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("fialedd", "Error adding document", e);
                    }
                });

        //llikers collection , add issue id for likers list



        desc_view.setText("");
        pincode_view.setText("");
        path="";
        no_of_post++;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddIssueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddIssueFragment newInstance(String param1, String param2) {
        AddIssueFragment fragment = new AddIssueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root_view=inflater.inflate(R.layout.fragment_add_issue, container, false);
        Camera_Button=(Button) root_view.findViewById(R.id.camera_button_id);
        Upload_Button=(Button)root_view.findViewById(R.id.upload_button_id);
        Raise_Button=(Button)root_view.findViewById(R.id.raise_issue_button) ;
        autoCompleteTxt = root_view.findViewById(R.id.auto_complete_txt);
        desc_view=(EditText)root_view.findViewById(R.id.desc_box);
        pincode_view=(EditText)root_view.findViewById(R.id.pincode_box);
        image_view=(ImageView)root_view.findViewById(R.id.image_box);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item_dropdown,items);
        autoCompleteTxt.setAdapter(adapterItems);

        //database instance
        database = FirebaseFirestore.getInstance();
        storage_ref = FirebaseStorage.getInstance().getReference();

        // to get the id clicked item in dropdown
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(),"Item: "+item, Toast.LENGTH_SHORT).show();
            }
        });
        Camera_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera_click(root_view);



            }
        });
        Upload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_image(root_view);

            }
        });
        Raise_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                raise_issue(root_view);

            }
        });

        return root_view;
    }
}