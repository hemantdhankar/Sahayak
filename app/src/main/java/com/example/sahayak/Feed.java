package com.example.sahayak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Feed extends AppCompatActivity {
    FragmentManager f_manager;
    FragmentTransaction f_transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        f_manager=getSupportFragmentManager();
        home_feed();
    }
    private void home_feed(){
        f_transaction=f_manager.beginTransaction();
        //Fragment fr=f_manager.findFragmentById(R.id.feed_layout);
        f_transaction.replace(R.id.feed_layout,new Feed_Fragment())
                .setReorderingAllowed(true)
                .commit();
//        if (fr==null){
//            Log.i("info","fragment is null");
//            fr=new Feed_Fragment();
//            f_transaction.add(R.id.recycler_view_feed,fr);
//            f_transaction.commit();
//        }
    }
}