package com.example.blooddonation.IntroFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blooddonation.Activities.LoginActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.Activities.RegisterActivity;


public class ThirdFragment extends Fragment {
    private Button btn_next_slider;
    private ViewPager2 viewPager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_third, container, false);


        btn_next_slider=view.findViewById(R.id.btn_getStarted_slide);
        viewPager=getActivity().findViewById(R.id.vp_slide);


        btn_next_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });



        return view;
    }
}