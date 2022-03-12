package com.example.blooddonation.MainFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonation.Activities.DonateBloodActivity;
import com.example.blooddonation.Activities.RegisterActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.Classes.UserModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG ="HomeFragment.Class";
    private static final int TAKE_IMAGE_CODE =1;
    private Button btn_request, btn_donate_blood;
    private TextView tv_user_name;
    private ImageView user_img;
    private ConstraintLayout parentLayout;

    private FirebaseUser mUser;
    private DatabaseReference mReference;

    private StorageReference storageRef;
    private FirebaseStorage firebaseStorage;
    Uri uri;




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
        firebaseStorage =FirebaseStorage.getInstance();
        storageRef=firebaseStorage.getReference();
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
                startActivity(new Intent(getActivity(), DonateBloodActivity.class));
                break;
            case R.id.btn_request_blood:
                RequestBloodFragment fragment2 = new RequestBloodFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case R.id.profile_image:
                picImg();
                break;
        }
    }

    private void picImg() {
        // use it to open & get an img from the phone(Gallery).
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,TAKE_IMAGE_CODE);

    }

    /**
     * to get an image from a Gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==getActivity().RESULT_OK){
            switch (requestCode){
                case TAKE_IMAGE_CODE:
                    uri=data.getData();
                    try {
                        Bitmap bitmap= MediaStore.
                                Images.
                                Media.getBitmap(getActivity().getContentResolver(),uri);
                        user_img.setImageBitmap(bitmap);
                        uploadImage(bitmap);
                    }catch (IOException e){
                        Log.d(TAG, "onActivityResult: "+ " Some exception.");
                    }
                    break;
            }
        }

    }

    private void uploadImage(Bitmap bitmap) {
        if (bitmap!=null){
            //showing pb for upload
            ProgressDialog progressDialog
                    = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref=
                    storageRef.child("UsersImages"+UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image

            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast
                            .makeText(getActivity(),
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT)
                            .show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast
                            .makeText(getActivity(),
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress
                            = (100.0
                            * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded "
                                    + (int)progress + "%");
                }
            });
        }




//        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//
//        //create a reference from the Storage.
//        String userID=user.getUid();
//        storageRef=firebaseStorage.getReference()
//                .child("ProfileImages")
//                .child(userID+".JPEG");
//
//        storageRef.putBytes(byteArrayOutputStream.toByteArray())
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(getActivity(), "Success to upload", Toast.LENGTH_SHORT).show();
//                         getDownloadUri(storageRef);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG,"onFailure upload Img");
//            }
//        });
    }
    private void getDownloadUri(StorageReference storageRef){
        storageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: "+uri);
                        setUserProfile(uri);
                    }
                });
    }

    private void setUserProfile(Uri uri) {


        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        mUser.updateProfile(request).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: "+ " img updated." );
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + " filed to update ");
            }
        });


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