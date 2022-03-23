package com.example.sahayak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "RegisterActivity";
    Button registerButton;
    String email, password;
    TextView error;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        error = (TextView) findViewById(R.id.registerError);
        error.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this, AfterLoginActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        int clickedId = view.getId();
        email = String.valueOf(((EditText) findViewById(R.id.registerEmailAddress)).getText());
        password = String.valueOf(((EditText) findViewById(R.id.registerPassword)).getText());
        String first_name = String.valueOf(((EditText) findViewById(R.id.registerFirstName)).getText());
        String last_name = String.valueOf(((EditText) findViewById(R.id.registerLastName)).getText());
        RadioGroup userType = (RadioGroup) findViewById(R.id.userType);
        if(clickedId == R.id.registerButton){
            String errorMsg = Validator.validate_registration_data(first_name, last_name,email, password);
            if(errorMsg!=null){
                error.setText(errorMsg);
                error.setVisibility(View.VISIBLE);
            }else{
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Map<String, Object> userProfile = new HashMap<>();
                                    userProfile.put("email", email);
                                    String userTypeName = ((RadioButton)findViewById(userType.getCheckedRadioButtonId())).getText().toString();
                                    userProfile.put("type", userTypeName);
                                    userProfile.put("first_name", first_name);
                                    userProfile.put("last_name", last_name);

                                    db.collection("users")
                                            .document(email)
                                            .set(userProfile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });


                                    startActivity(new Intent(RegisterActivity.this, AfterLoginActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    error.setText(task.getException().getMessage());
                                    error.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }

        }
    }

}