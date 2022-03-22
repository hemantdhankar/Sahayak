package com.example.sahayak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "LoginActivity";
    Button loginButton;
    Button check_button;
    Button check_leaderboard_button;
    Button loginToSignUpButton;
    TextView loginError;
    String email, password;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginButton = (Button) findViewById(R.id.loginButton);
        loginToSignUpButton = (Button) findViewById(R.id.loginToSignUpButton);
        check_button=(Button)findViewById(R.id.check_raise);
        check_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),RaiseFeature.class);
                startActivity(intent);
            }
        });
        check_leaderboard_button=(Button) findViewById(R.id.check_leader);
        check_leaderboard_button.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent leader_intent=new Intent(getApplicationContext(),LeaderBoard.class);
                startActivity(leader_intent);



            }
        }));

        loginButton.setOnClickListener(this);
        loginToSignUpButton.setOnClickListener(this);
        loginError = (TextView) findViewById(R.id.loginError);
        loginError.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int clickedId = view.getId();
        if(clickedId == R.id.loginButton){
            email = ((TextInputEditText) findViewById(R.id.loginEmailAddress)).getText().toString();
            password = ((TextInputEditText) findViewById(R.id.loginPassword)).getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                String errorMsg = "Incorrect Email or Password!";
                                loginError.setText(errorMsg);
                                loginError.setVisibility(View.VISIBLE);
                            }
                        }
                    });

        }else if(clickedId == R.id.loginToSignUpButton){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

    }
}