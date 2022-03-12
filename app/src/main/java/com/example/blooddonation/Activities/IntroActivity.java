package com.example.blooddonation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.blooddonation.Adapters.IntroAdapter;
import com.example.blooddonation.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 myViewPager;
    private IntroAdapter adapter;

    private TextView tv_skip_slider;
    private Button btn_next_slider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myViewPager=findViewById(R.id.vp_slide);
        btn_next_slider=findViewById(R.id.btn_nextF1_slide);
        tv_skip_slider=findViewById(R.id.tv_skip_slider);

        adapter=new IntroAdapter(this);
        myViewPager.setAdapter(adapter);



        tv_skip_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });



    }

}

