package com.example.blooddonation.IntroFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.blooddonation.R;


public class SecondFragment extends Fragment {

    private TextView tv_skip_slider;
    private Button btn_next_slider;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_second, container, false);


        tv_skip_slider=view.findViewById(R.id.tv_skip_slider);
        btn_next_slider=view.findViewById(R.id.btn_nextF2_slide);
        viewPager=getActivity().findViewById(R.id.vp_slide);

        btn_next_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
            }
        });



        return view;
    }
}