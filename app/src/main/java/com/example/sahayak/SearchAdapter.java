package com.example.sahayak;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchFeedViewHolder>{

    private ArrayList<feedItem> feeds;
    public SearchAdapter(ArrayList<feedItem> list)
    {
        this.feeds = list;
    }

    @NonNull
    @Override
    public SearchFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        SearchFeedViewHolder holder=new SearchFeedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFeedViewHolder holder, int position) {
        //getting current item from the list,recyler view adapter need.
        feedItem item=feeds.get(position);
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
                        holder.numberoflikes.setText("-"+current_issue.get("number_of_likes"));
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


        //traanfer of feeditem package using bundle to main post.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("adapter","onclickcheck");
                Bundle bundle= new Bundle();
                bundle.putSerializable("pos", item);
                AppCompatActivity ac= (AppCompatActivity) v.getContext();
                Main_Post ff= new Main_Post();
                ff.setArguments(bundle);
                ac.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment,ff)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SearchFeedViewHolder extends RecyclerView.ViewHolder {

        //public TextView numberNews;
        public TextView titleFeed;
        public TextView titlepin;
        public TextView postwriter;
        public TextView numberoflikes;
        public TextView currentissuestatus;

        public SearchFeedViewHolder(@NonNull View itemView) {
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
