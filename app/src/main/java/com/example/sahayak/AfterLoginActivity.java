package com.example.sahayak;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AfterLoginActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterloginlayout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.dashboard_bi);



    }
    DashboardFragment dashboardFragment=new DashboardFragment();
    AddIssueFragment addIssueFragment=new AddIssueFragment();
    LeaderBoardFragment leaderBoardFragment = new LeaderBoardFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard_bi:
                Log.d("check", "onNavigationItemSelected: dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, dashboardFragment).commit();
                return true;
            case R.id.search_bi:
                Log.d("check", "onNavigationItemSelected: search");
                this.getSupportFragmentManager().beginTransaction().replace(R.id.MainContentFragment, dashboardFragment).commit();
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
}
