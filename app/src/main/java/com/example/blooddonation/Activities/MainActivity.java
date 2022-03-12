package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.blooddonation.MainFragments.HomeFragment;
import com.example.blooddonation.MainFragments.NotificationFragment;
import com.example.blooddonation.MainFragments.ProfileFragment;
import com.example.blooddonation.MainFragments.RequestBloodFragment;
import com.example.blooddonation.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab_donate_blood;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab_donate_blood=findViewById(R.id.fab);
        bottomNavigationView.setBackground(null);

        fab_donate_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate the user to @DonateBloodActivity
                startActivity(new Intent(getApplicationContext(),DonateBloodActivity.class));
            }
        });

        //Selected Fragment as a default
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout,new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment=null;
            switch (item.getItemId()) {


                case R.id.m_home:
                    //hendel home icon press
                    selectedFragment=new HomeFragment();
                    break;
                case R.id.m_request:
                    //hendel home request press
                    selectedFragment=new RequestBloodFragment();
                    break;
                case R.id.m_notification:
                    //hendel notification icon press
                    selectedFragment=new NotificationFragment();
                    break;
                case R.id.m_prorile:
                    //hendel profile icon press
                    selectedFragment=new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,selectedFragment).commit();

            return true;

        });
    }
}