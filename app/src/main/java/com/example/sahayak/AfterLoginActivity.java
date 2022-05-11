package com.example.sahayak;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AfterLoginActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    DashboardFragment dashboardFragment=new DashboardFragment();
    AddIssueFragment addIssueFragment=new AddIssueFragment();
    LeaderBoardFragment leaderBoardFragment = new LeaderBoardFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    Feed_Fragment feed_fragment = new Feed_Fragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterloginlayout);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            startActivity(new Intent(AfterLoginActivity.this, LoginActivity.class));
        }else{
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            bottomNavigationView.setSelectedItemId(R.id.dashboard_bi);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard_bi:
                Log.d("check", "onNavigationItemSelected: dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, feed_fragment).commit();
                return true;
            case R.id.search_bi:
                Log.d("check", "onNavigationItemSelected: search");
                this.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, searchFragment).commit();
                return true;
            case R.id.addpost_bi:
                Log.d("check", "onNavigationItemSelected: addpost");
                this.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, addIssueFragment).commit();
                return true;
            case R.id.leaderboard_bi:
                Log.d("check", "onNavigationItemSelected: leaderboard ");
                this.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, leaderBoardFragment).commit();
                return true;
            case R.id.more_option_bi:
                Log.d("check", "onNavigationItemSelected: more option");
                this.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, profileFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
