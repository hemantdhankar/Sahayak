package com.example.sahayak;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LeaderBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v;
    int curri;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderBoardFragment newInstance(String param1, String param2) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_leader_board, container, false);
        HashMap<String,Integer> top_3_user = new HashMap<>();
        HashMap<String, Integer> top_3_ngo = new HashMap<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String user_id = document.getId();
                                HashMap<String, Object> temp = (HashMap<String, Object>) document.getData();
                                if (temp.get("type").equals("NGO")) {
                                    int size = ((ArrayList<String>) temp.get("issue_resolved")).size();
                                    top_3_ngo.put((String) temp.get("first_name")+" "+(String) temp.get("last_name"),size);
                                }
                            }

                            List<String> ngo_keys = top_3_ngo.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
                            for(String name:ngo_keys)
                            {
                                if(ngo_keys.indexOf(name)==0)
                                {
                                    TextView ngo1=(TextView) v.findViewById(R.id.ngo_1);
                                    ngo1.setText(name);
                                }
                                if(ngo_keys.indexOf(name)==1)
                                {
                                    TextView ngo2=(TextView) v.findViewById(R.id.ngo_2);
                                    ngo2.setText(name);
                                }
                                if(ngo_keys.indexOf(name)==2)
                                {
                                    TextView ngo3=(TextView) v.findViewById(R.id.ngo_3);
                                    ngo3.setText(name);
                                }
                            }
                            Log.d("top3ngo", "onComplete: "+ngo_keys.toString()+top_3_ngo);
                        } else{
                            Log.w("Feed_Fragment", "Error getting documents.", task.getException());
                        }
                    }
                });
        database.collection("Issue_detail")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String issue_id = document.getId();
                                HashMap<String,Object> temp = (HashMap<String, Object>) document.getData();
                                String email=(String) temp.get("email");
                                int number_of_likes=Integer.parseInt((String)temp.get("number_of_likes"));
                                if(top_3_user.containsKey(email))
                                {
                                    top_3_user.put(email,top_3_user.get(email)+number_of_likes);
                                }
                                else
                                {
                                    top_3_user.put(email,number_of_likes);

                                }

                                Log.d("read successful", document.getId() + " => " + document.getData());
                            }
                            List<String> keys = top_3_user.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
                            Log.d("top3", "onComplete: "+keys.toString()+top_3_user);

                            List<String> keys_name=new ArrayList<>();

                            for(String email:keys)
                            {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                //reading users table to get username associated with current user email.
                                DocumentReference docRef = db.collection("users").document(""+email);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String name=document.getString("first_name")+" "+document.getString("last_name");
                                                Log.d("top_name", "onComplete: "+keys.indexOf(email)+name);
                                                if(keys.indexOf(email)==0)
                                                {
                                                    TextView ir1=(TextView) v.findViewById(R.id.issue_r_1);
                                                    ir1.setText(name);
                                                }
                                                if(keys.indexOf(email)==1)
                                                {
                                                    TextView ir2=(TextView) v.findViewById(R.id.issue_r_2);
                                                    ir2.setText(name);

                                                }
                                                if(keys.indexOf(email)==2)
                                                {
                                                    TextView ir3=(TextView) v.findViewById(R.id.issue_r_3);
                                                    ir3.setText(name);

                                                }



                                                Log.d("tag", "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d("tag", "No such document");
                                            }
                                        } else {
                                            Log.d("tag", "get failed with ", task.getException());
                                        }
                                    }
                                });





                            }
                        } else {
                            Log.d("read failue", "Error getting documents.", task.getException());
                        }
                    }
                });




//        database.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("read successful", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d("read failue", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//        v=inflater.inflate(R.layout.fragment_leader_board, container, false);
        return v;
    }
}