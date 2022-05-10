package com.example.sahayak;

import android.graphics.Color;
import android.media.JetPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Main_Post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_Post extends Fragment {


    View v;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Main_Post() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main_Post.
     */
    // TODO: Rename and change types and number of parameters
    public static Main_Post newInstance(String param1, String param2) {
        Main_Post fragment = new Main_Post();
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_main_post, container, false);
        TextView title= v.findViewById(R.id.post_title);
        TextView desc= v.findViewById(R.id.post_description);
        TextView pin= v.findViewById(R.id.post_pin);
        TextView numberoflikes= v.findViewById(R.id.numberoflikesmainpost);

        Bundle bundle= getArguments();
        feedItem fc= (feedItem) bundle.getSerializable("pos");

        title.setText(fc.title);
        desc.setText(fc.description);
        pin.setText(""+fc.pincode);
        Log.i("innoc",fc.id);

        final HashMap<String, Object>[] current_issue = new HashMap[]{new HashMap<String, Object>()};
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Issue_detail").document(""+fc.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("innoc","we are here");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        current_issue[0] = (HashMap<String, Object>) document.getData();
                        numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

        numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
        Button likeButton = v.findViewById(R.id.like_button);
        final boolean[] already_liked = {false};
        likeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                ArrayList<String> current_likers= (ArrayList<String>) current_issue[0].get("likers");
                if (current_likers.contains(user.getEmail())){
                    already_liked[0]=true;
                }

            if (!already_liked[0]){

                //adding current id to likers list of this issue
                current_likers.add(user.getEmail());
                current_issue[0].replace("likers",current_likers);

                //likeButton.setBackgroundColor(Color.RED);
                Log.i("likes",""+current_issue[0].get("number_of_likes"));
                int likenumbers=Integer.parseInt((String) current_issue[0].get("number_of_likes"));
                likenumbers++;
                fc.likenumbers=""+likenumbers;
                current_issue[0].replace("number_of_likes",String.valueOf(likenumbers));
                numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                Log.i("likes",fc.likenumbers);

                db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(getActivity(), "You have liked this post..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    // inside on failure method we are
                    // displaying a failure message.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                already_liked[0]=false;
                //likeButton.setBackgroundColor(Color.GREEN);
                current_likers.remove(user.getEmail());
                current_issue[0].replace("likers",current_likers);

                //likeButton.setBackgroundColor(Color.RED);
                Log.i("likes",""+current_issue[0].get("number_of_likes"));
                int likenumbers=Integer.parseInt((String) current_issue[0].get("number_of_likes"));
                likenumbers--;
                fc.likenumbers=""+likenumbers;
                current_issue[0].replace("number_of_likes",String.valueOf(likenumbers));
                numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                Log.i("likes",fc.likenumbers);

                db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        Toast.makeText(getActivity(), "You  succesfully unliked the post", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    // inside on failure method we are
                    // displaying a failure message.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                    }
                });


                //Toast.makeText(getActivity(),"You have already liked",Toast.LENGTH_LONG).show();
            }

            }
        });

        return  v;
    }
}