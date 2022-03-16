package com.example.sahayak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class RaiseFeature extends AppCompatActivity {
    String[] items =  {"Transport","Electricity","Food","bla bla","bla bla"};
    protected AutoCompleteTextView autoCompleteTxt;
    protected ArrayAdapter<String> adapterItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_feature);

        // setting the drop down for category
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item_dropdown,items);
        autoCompleteTxt.setAdapter(adapterItems);

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
}