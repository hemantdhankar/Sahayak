package com.example.sahayak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    View view;
    HashMap<String,feedItem> map = new HashMap<>();
    RecyclerView.Adapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        view=inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList<feedItem> feed_arr=new ArrayList<>();
        feed_arr.add(new feedItem("bhaskar","bhaskar is missing",110020,"bhaskar@email","check id"));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Issue_detail")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String issue_id = document.getId();
                                HashMap<String,Object> temp = (HashMap<String, Object>) document.getData();
                                String c = temp.get("image_path")==null?"": (String) temp.get("image_path");
                                feedItem ff = new feedItem((String)temp.get("category"),(String)temp.get("description"),Integer.parseInt((String) temp.get("pin_code")), (String) temp.get("email"),issue_id);
                                Log.i("issue id",issue_id);
                                map.put(issue_id,ff);
                                feed_arr.add(ff);
                                if(adapter!=null)
                                    adapter.notifyDataSetChanged();
                                else
                                {
                                    adapter = new FeedAdapter(feed_arr);
                                    adapter.notifyDataSetChanged();
                                }
                                Log.i("Feed_fragment",""+ff.toString());
                            }
                        } else {
                            Log.w("Feed_Fragment", "Error getting documents.", task.getException());
                        }
                    }
                });


        RecyclerView rec_view=view.findViewById(R.id.recycler_view_feed_search);
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        else{
            Log.i("Feed_fragment",""+feed_arr.size());
            adapter=new FeedAdapter(feed_arr);
            adapter.notifyDataSetChanged();
        }
        rec_view.setAdapter(adapter);
        rec_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feed_arr.add(new feedItem("nitesh is missing","Description-5",115511,"Nitesh@email","nitesh_id"));
            }
        },new IntentFilter("New feed Found"));
        return view;
    }
}