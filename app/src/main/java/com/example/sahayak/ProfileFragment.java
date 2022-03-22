package com.example.sahayak;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    String TAG="ProfileFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        TextInputEditText first_name = (TextInputEditText) itemView.findViewById(R.id.profileFirstName);
        TextInputEditText last_name = (TextInputEditText) itemView.findViewById(R.id.profileLastName);
        TextInputEditText email = (TextInputEditText) itemView.findViewById(R.id.profileEmailAddress);
        RadioGroup userType = (RadioGroup) itemView.findViewById(R.id.profileUserType);
        Button logoutButton = (Button) itemView.findViewById(R.id.profileLogoutButton);
        logoutButton.setOnClickListener(this);
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        first_name.setText((String) document.get("first_name"));
                        last_name.setText((String) document.get("last_name"));
                        email.setText((String) document.get("email"));
                        String typeOfUser = (String) document.get("type");
                        if(typeOfUser.equals("Regular User")){
                            userType.check(R.id.profileRegularUser);
                        }else if(typeOfUser.equals("Social Worker")){
                            userType.check(R.id.profileSocialWorker);
                        }else if(typeOfUser.equals("NGO")){
                            userType.check(R.id.profileNgo);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return itemView;
    }

    @Override
    public void onClick(View view) {
        int clickedId = view.getId();
        if(clickedId == R.id.profileLogoutButton){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}