package com.example.sahayak;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    private java.util.ArrayList<feedItem> anotherList;
    RadioGroup rg;
    RadioButton radioButton_feed, radioButton_ngo;

    public  SearchAdapter(ArrayList<feedItem> list, RadioGroup rg, RadioButton radioButton_feed, RadioButton radioButton_ngo)
    {
        this.anotherList = list;
        this.radioButton_ngo = radioButton_ngo;
        this.radioButton_feed = radioButton_feed;
        this.rg = rg;
    }
    @NonNull
    @Override

    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("in adapter","------checkcheck-----");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        SearchViewHolder  holder=new SearchViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.i("in adapter","-------checkcheck-----");

        //getting current item from the list,recyler view adapter need.
        feedItem item=anotherList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //reading users table to get username associated with current user email.
        DocumentReference docRef = db.collection("users").document(""+item.email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name=document.getString("first_name");
                        holder.postwriter.setText(""+name);

                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

        //taking data from firebase to show on feed using id from another list.
        DocumentReference docRefissue = db.collection("Issue_detail").document(""+item.id);
        docRefissue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String, Object> current_issue = (HashMap<String, Object>) document.getData();
                        holder.titleFeed.setText(""+current_issue.get("description"));
                        holder.titlepin.setText(""+current_issue.get("pin_code"));
                        holder.numberoflikes.setText(" "+current_issue.get("number_of_likes"));
                        holder.currentissuestatus.setText(""+current_issue.get("Status"));
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });


        if(rg.getCheckedRadioButtonId()==radioButton_feed.getId()) {
            //traanfer of feeditem package using bundle to main post.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("adapter", "onclickcheck");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("pos", item);
                    AppCompatActivity ac = (AppCompatActivity) v.getContext();
                    Main_Post ff = new Main_Post();
                    ff.setArguments(bundle);
                    ac.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, ff)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
    @Override

    public int getItemCount() {
        return anotherList.size();
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder {

        //public TextView numberNews;
        public TextView titleFeed;
        public TextView titlepin;
        public TextView postwriter;
        public TextView numberoflikes;
        public TextView currentissuestatus;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            //numberNews=itemView.findViewById(R.id.feed_id);
            titleFeed=itemView.findViewById(R.id.feed_desc);
            titlepin= itemView.findViewById(R.id.pincode);
            postwriter= itemView.findViewById(R.id.post_writer);
            numberoflikes= itemView.findViewById(R.id.numberoflikes);
            currentissuestatus=itemView.findViewById(R.id.currentstatustxtview);
        }
    }
}