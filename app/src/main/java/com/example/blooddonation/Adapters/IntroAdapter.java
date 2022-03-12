package com.example.blooddonation.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.blooddonation.IntroFragments.FirstFragment;
import com.example.blooddonation.IntroFragments.SecondFragment;
import com.example.blooddonation.IntroFragments.ThirdFragment;

public class IntroAdapter extends FragmentStateAdapter {


    public IntroAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FirstFragment();
            case 1:
                return new SecondFragment();
            case 2:
                return new ThirdFragment();
            default:
                return null;
        }
    }



    @Override
    public int getItemCount() {
        return 3;
    }
}
