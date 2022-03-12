package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blooddonation.Classes.DonorUserModel;
import com.example.blooddonation.Classes.UserModel;
import com.example.blooddonation.MainFragments.HomeFragment;
import com.example.blooddonation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonateBloodActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "DonateBloodActivity";
    private Button  btn_submit;

    private Dialog dialog;
    private AutoCompleteTextView act_bloodType;
    private ArrayAdapter<String> arrayAdapter;
    public static final String DB_USERS_DONOR= "donorUsers";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;



    //Donor details.
    private String name;
    private String phone;
    private String bloodType="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_blood);

        intiViews();
        intiSpinner();
    }

    private void intiViews() {


        btn_submit=findViewById(R.id.btn_submitDonor);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDonate();
            }
        });

        dialog=new Dialog(this);
    }

    @Override
    public void onClick(View view) {

    }
    private void intiSpinner(){
        String[] item =getResources().getStringArray(R.array.blood_type);
        act_bloodType=findViewById(R.id.autoCompleteTVBloodType);

        arrayAdapter=new ArrayAdapter<>(this,R.layout.dropdown_item,item);
        act_bloodType.setAdapter(arrayAdapter);

        act_bloodType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=adapterView.getItemAtPosition(i).toString();
                bloodType=item;

              //  Toast.makeText(getApplicationContext(), bloodType, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void submitDonate() {
        //get the name and phone from the user database
        mUser=FirebaseAuth.getInstance().getCurrentUser();
        mReference=FirebaseDatabase.getInstance().getReference(RegisterActivity.DBPATH);
        String userID=mUser.getUid();

        mReference.child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel=snapshot.getValue(UserModel.class);
                        if (userModel !=null){
                            //get the details
                            Log.d(TAG, "onDataChange:  done to get the data" );
                            name=userModel.name;
                            phone=userModel.phone;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.d(TAG, "onCancelled: Filed to get the data from the database ");
                    }
                });

        //create a new db which hold the donor details
        if (name==null && bloodType==null && phone==null){
            Toast.makeText(getApplicationContext(), "Filed ", Toast.LENGTH_SHORT).show();
            //  Snackbar.make(R.layout.activity_donate_blood,"choose your blood first").show();
        }else {
            DonorUserModel donor=new DonorUserModel(name,phone,bloodType);

            FirebaseDatabase.getInstance().getReference(DB_USERS_DONOR)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(donor).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Show the dialog
                        Log.d(TAG, "onComplete: add a new donor is successful");
                        //Toast.makeText(getApplicationContext(), "Your Donate is done , thank u.", Toast.LENGTH_SHORT).show();
                        setDialog();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onComplete: add a new donor is filed");
                    Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void setDialog(){
        dialog.setContentView(R.layout.donate_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //content of the dialog
        ImageView iv_=dialog.findViewById(R.id.iv_donate_dialog);
        Button btn_ok=dialog.findViewById(R.id.go_donate_dialog);
        dialog.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dialog.dismiss();
               startActivity(new Intent(getApplicationContext(),MainActivity.
               class));
               finish();
            }
        });
    }
}