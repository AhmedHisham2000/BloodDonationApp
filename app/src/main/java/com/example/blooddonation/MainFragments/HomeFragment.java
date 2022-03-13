package com.example.blooddonation.MainFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG ="HomeFragment.Class";
    private static final int TAKE_IMAGE_CODE =1;
    private Button btn_request, btn_donate_blood;
    private TextView tv_user_name;

    private ConstraintLayout parentLayout;


    private DatabaseReference mReference;

    //to upload an image into Firebase storage
    private FirebaseAuth mAuth;
    private CircleImageView user_img;
    private FirebaseUser mUser;
    private StorageReference storageRef;
    private FirebaseStorage firebaseStorage;
    private Uri imgUri;
    private String myUri="";
    private StorageTask uploadTask;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        getUserName();

    }
    private void initViews(View view){
        btn_request = view.findViewById(R.id.btn_request_blood);
        btn_donate_blood = view.findViewById(R.id.btn_donate_blood);
        tv_user_name = view.findViewById(R.id.tv_user_name_Home);
        parentLayout=view.findViewById(R.id.parent_home_fragemnt_layout);
        user_img=view.findViewById(R.id.profile_image);

        //Firebase Storage
        mAuth=FirebaseAuth.getInstance();
        mReference=FirebaseDatabase.getInstance().getReference();
        firebaseStorage =FirebaseStorage.getInstance();
        storageRef=FirebaseStorage.getInstance().getReference().child("Profile images");
        //Firebase User..to get the current user
        mUser=FirebaseAuth.getInstance().getCurrentUser();


        //clicks
        btn_donate_blood.setOnClickListener(this);
        btn_request.setOnClickListener(this);
        user_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_donate_blood:
                DonateFragment fragment = new DonateFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


                break;
            case R.id.btn_request_blood:
                RequestBloodFragment fragment2 = new RequestBloodFragment();
                 fragmentManager = getFragmentManager();
                 fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case R.id.profile_image:
                //TODO Add func to upload an image
                break;
        }
    }


    private void getUserName() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference(RegisterActivity.DBPATH);
        String userID = mUser.getUid();

        mReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel != null) {
                    tv_user_name.setText(userModel.name + "!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });

    }
}