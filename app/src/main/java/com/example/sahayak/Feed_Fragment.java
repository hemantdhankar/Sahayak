package com.example.sahayak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Feed_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
class feedItem implements Serializable {
    String title;
    String description;
    int pincode;

    public feedItem(String title, String description, int pincode) {
        this.title = title;
        this.description = description;
        this.pincode = pincode;
    }

    @Override
    public String toString() {
        return "feedItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pincode=" + pincode +
                '}';
    }
}
public class Feed_Fragment extends Fragment {

    ArrayList<feedItem> feed_arr=new ArrayList<>();
    View view;
    RecyclerView.Adapter adapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Feed_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Feed_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Feed_Fragment newInstance(String param1, String param2) {
        Feed_Fragment fragment = new Feed_Fragment();
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
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_feed, container, false);
        feed_arr.add(new feedItem("dog is missing","Description-1",112233));
        feed_arr.add(new feedItem("cow is missing","Description-2",112442));
        feed_arr.add(new feedItem("monkey is missing","Description-3",152431));
        feed_arr.add(new feedItem("cat is missing","Description-4",323232));
        feed_arr.add(new feedItem("Yash is missing","Description-5",110011));

        RecyclerView rec_view=view.findViewById(R.id.recycler_view_feed);
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        else{
            adapter=new FeedAdapter(feed_arr);
        }
        rec_view.setAdapter(adapter);
        rec_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feed_arr.add(new feedItem("nitesh is missing","Description-5",115511));
            }
        },new IntentFilter("New feed Found"));
        return view;
    }
}