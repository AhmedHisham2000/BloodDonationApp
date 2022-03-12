package com.example.blooddonation.MainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blooddonation.Activities.DonateBloodActivity;
import com.example.blooddonation.Activities.LoginActivity;
import com.example.blooddonation.Activities.MainActivity;
import com.example.blooddonation.Activities.RegisterActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.Classes.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Button btn_signOut,btn_donateBlood,btn_request;
    private TextView tv_userName;
    private ImageView iv_back;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intiViews(view);
        getUserDetails();


    }

    private void intiViews(View view){
        //btn_signOut = view.findViewById(R.id.btn_signOut);
        btn_signOut = view.findViewById(R.id.btn_signOut);
        btn_donateBlood=view.findViewById(R.id.btn_Profile_donateBlood);
        btn_request=view.findViewById(R.id.btn_profile_requestBlood);
        tv_userName=view.findViewById(R.id.tv_user_name_profile);
        iv_back=view.findViewById(R.id.iv_back_profile);

        //
        mAuth=FirebaseAuth.getInstance();

        //clicks
        btn_signOut.setOnClickListener(this);
        btn_donateBlood.setOnClickListener(this);
        btn_request.setOnClickListener(this);
        iv_back.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_Profile_donateBlood:
                startActivity(new Intent(getActivity(), DonateBloodActivity.class));
                break;
            case R.id.btn_signOut:
                userSignOut();
                break;
            case R.id.btn_profile_requestBlood:
                RequestBloodFragment fragment2 = new RequestBloodFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.iv_back_profile:
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                break;
        }
    }
    private void userSignOut() {
        mAuth.signOut();
        Intent i=new Intent(getActivity(), LoginActivity.class);
        //clear the Stack = finish().
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    private void getUserDetails(){
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        mReference= FirebaseDatabase.getInstance().getReference(RegisterActivity.DBPATH);
        String userID=mUser.getUid();


        mReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel userModel=snapshot.getValue(UserModel.class);
                if (userModel!=null){
                    tv_userName.setText(userModel.name);
                    //tv_useEmail.setText.UserModel.email);
                    //the details of user.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}