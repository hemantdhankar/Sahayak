package com.example.sahayak;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder>{

    private java.util.ArrayList<feedItem> anotherList;

    public  FeedAdapter(ArrayList<feedItem> list) {
        anotherList = list;
    }
    @NonNull
    @Override

    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("in adapter","------checkcheck-----");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        FeedViewHolder  holder=new FeedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.i("in adapter","-------checkcheck-----");

        feedItem item=anotherList.get(position);
        holder.titleFeed.setText(""+item.title);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("adapter","onclickcheck");
                Bundle bundle= new Bundle();
                bundle.putSerializable("pos", item);
                AppCompatActivity ac= (AppCompatActivity) v.getContext();
                Main_Post ff= new Main_Post();
                ff.setArguments(bundle);
                ac.getSupportFragmentManager().beginTransaction().replace(R.id.feed_layout,ff)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    @Override

    public int getItemCount() {
        return anotherList.size();
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder {

        //public TextView numberNews;
        public TextView titleFeed;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            //numberNews=itemView.findViewById(R.id.feed_id);
            titleFeed=itemView.findViewById(R.id.feed_title);
        }
    }
}