package com.example.sahayak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RaiseFeature extends AppCompatActivity {
    String[] items =  {"Transport","Electricity","Food","Water","Sewage","Roads","Animals","Old Citizens","Garbage","Donations"};
    protected AutoCompleteTextView autoCompleteTxt;
    protected ArrayAdapter<String> adapterItems;
    protected EditText desc_view,pincode_view;
    protected FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_feature);

        // setting the drop down for category
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        desc_view=(EditText)findViewById(R.id.desc_box);
        pincode_view=(EditText)findViewById(R.id.pincode_box);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item_dropdown,items);
        autoCompleteTxt.setAdapter(adapterItems);

        //database instance
        database = FirebaseFirestore.getInstance();


        // to get the id clicked item in dropdown
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+item, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void camera_click(View v){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
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

    }
}