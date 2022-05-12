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
import android.widget.SearchView;
import android.widget.Toast;

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
    SearchView searchView;
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
    public class LockMatch {

        public void main(String arg[]) {
            //---Provide source and target strings to lock_match function to compare--//
            System.out.println("Your Strings are Matched="+lock_match("The warlock","The warlock powered by WTPL")+"%");
        }

        public  int lock_match(String s, String t) {



            int totalw = word_count(s);
            int total = 100;
            int perw = total / totalw;
            int gotperw = 0;

            if (!s.equals(t)) {

                for (int i = 1; i <= totalw; i++) {
                    if (simple_match(split_string(s, i), t) == 1) {
                        gotperw = ((perw * (total - 10)) / total) + gotperw;
                    } else if (front_full_match(split_string(s, i), t) == 1) {
                        gotperw = ((perw * (total - 20)) / total) + gotperw;
                    } else if (anywhere_match(split_string(s, i), t) == 1) {
                        gotperw = ((perw * (total - 30)) / total) + gotperw;
                    } else {
                        gotperw = ((perw * smart_match(split_string(s, i), t)) / total) + gotperw;
                    }
                }
            } else {
                gotperw = 100;
            }
            return gotperw;
        }

        public  int anywhere_match(String s, String t) {
            int x = 0;
            if (t.contains(s)) {
                x = 1;
            }
            return x;
        }

        public  int front_full_match(String s, String t) {
            int x = 0;
            String tempt;
            int len = s.length();

            //----------Work Body----------//
            for (int i = 1; i <= word_count(t); i++) {
                tempt = split_string(t, i);
                if (tempt.length() >= s.length()) {
                    tempt = tempt.substring(0, len);
                    if (s.contains(tempt)) {
                        x = 1;
                        break;
                    }
                }
            }
            //---------END---------------//
            if (len == 0) {
                x = 0;
            }
            return x;
        }

        public  int simple_match(String s, String t) {
            int x = 0;
            String tempt;
            int len = s.length();


            //----------Work Body----------//
            for (int i = 1; i <= word_count(t); i++) {
                tempt = split_string(t, i);
                if (tempt.length() == s.length()) {
                    if (s.contains(tempt)) {
                        x = 1;
                        break;
                    }
                }
            }
            //---------END---------------//
            if (len == 0) {
                x = 0;
            }
            return x;
        }

        public  int smart_match(String ts, String tt) {

            char[] s = new char[ts.length()];
            s = ts.toCharArray();
            char[] t = new char[tt.length()];
            t = tt.toCharArray();


            int slen = s.length;
            //number of 3 combinations per word//
            int combs = (slen - 3) + 1;
            //percentage per combination of 3 characters//
            int ppc = 0;
            if (slen >= 3) {
                ppc = 100 / combs;
            }
            //initialising an integer to store the total % this class genrate//
            int x = 0;
            //declaring a temporary new source char array
            char[] ns = new char[3];
            //check if source char array has more then 3 characters//
            if (slen < 3) {
            } else {
                for (int i = 0; i < combs; i++) {
                    for (int j = 0; j < 3; j++) {
                        ns[j] = s[j + i];
                    }
                    if (cross_full_match(ns, t) == 1) {
                        x = x + 1;
                    }
                }
            }
            x = ppc * x;
            return x;
        }

        /**
         *
         * @param s
         * @param t
         * @return
         */
        public  int  cross_full_match(char[] s, char[] t) {
            int z = t.length - s.length;
            int x = 0;
            if (s.length > t.length) {
                return x;
            } else {
                for (int i = 0; i <= z; i++) {
                    for (int j = 0; j <= (s.length - 1); j++) {
                        if (s[j] == t[j + i]) {
                            // x=1 if any charecer matches
                            x = 1;
                        } else {
                            // if x=0 mean an character do not matches and loop break out
                            x = 0;
                            break;
                        }
                    }
                    if (x == 1) {
                        break;
                    }
                }
            }
            return x;
        }

        public  String split_string(String s, int n) {

            int index;
            String temp;
            temp = s;
            String temp2 = null;

            int temp3 = 0;

            for (int i = 0; i < n; i++) {
                int strlen = temp.length();
                index = temp.indexOf(" ");
                if (index < 0) {
                    index = strlen;
                }
                temp2 = temp.substring(temp3, index);
                temp = temp.substring(index, strlen);
                temp = temp.trim();

            }
            return temp2;
        }

        public  int word_count(String s) {
            int x = 1;
            int c;
            s = s.trim();
            if (s.isEmpty()) {
                x = 0;
            } else {
                if (s.contains(" ")) {
                    for (;;) {
                        x++;
                        c = s.indexOf(" ");
                        s = s.substring(c);
                        s = s.trim();
                        if (s.contains(" ")) {
                        } else {
                            break;
                        }
                    }
                }
            }
            return x;
        }
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
        searchView = (SearchView) view.findViewById(R.id.searchView);

        ArrayList<feedItem> feed_arr=new ArrayList<>();
        LockMatch lc=new LockMatch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search_str) {

                Toast.makeText(getContext(), search_str, Toast.LENGTH_SHORT).show();
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
                                        int match_score=lc.lock_match((String)temp.get("description")+" "+(String)temp.get("category"),search_str);
                                        Log.d("match_score", "onComplete: "+match_score);
                                        if(match_score>0)
                                        {
                                            feed_arr.add(ff);

                                        }

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


                return false;
            }

            @Override
            public boolean onQueryTextChange(String search_str) {

                return false;
            }
        });

        feed_arr.add(new feedItem("bhaskar","bhaskar is missing",110020,"bhaskar@email","check id"));

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