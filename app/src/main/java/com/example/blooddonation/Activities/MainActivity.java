package com.example.blooddonation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.blooddonation.MainFragments.HomeFragment;
import com.example.blooddonation.MainFragments.DonateFragment;
import com.example.blooddonation.MainFragments.NotificationFragment;
import com.example.blooddonation.MainFragments.ProfileFragment;
import com.example.blooddonation.MainFragments.RequestBloodFragment;
import com.example.blooddonation.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
//                startActivity(new Intent(getApplicationContext(),DonateBloodActivity.class));

                //TODO Change DonateBloodActivity To My Fragment
                DonateFragment fragment=new DonateFragment();
                FragmentManager manager=getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.frameLayout,fragment,null);
                transaction.commit();

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