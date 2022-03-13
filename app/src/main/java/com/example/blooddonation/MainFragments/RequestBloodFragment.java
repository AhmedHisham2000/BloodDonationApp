package com.example.blooddonation.MainFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonation.Activities.MainActivity;
import com.example.blooddonation.Activities.RegisterActivity;
import com.example.blooddonation.Classes.RequestModel;
import com.example.blooddonation.Classes.UserModel;
import com.example.blooddonation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestBloodFragment extends Fragment {

    private static final String TAG="RequestBloodFragment";
    private static final String DB_REQUEST="Requests";
    //views
    private AutoCompleteTextView act_bloodTypes;
    private Spinner sp_units_num,sp_hospitalName;
    private EditText et_message;
    private Button btn_submit;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;

    //dialog views
    private Dialog dialog;
    private ImageView iv_dialog;
    private Button btn_dialog;


    //request details
    private String name;
    private String no_units;
    private String hospitalName;
    private String bloodType;
    private String message;


    public RequestBloodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_blood, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniViews(view);
        intiSpinner();
        getUserDetails();
    }

    private void iniViews(View view){
        act_bloodTypes=view.findViewById(R.id.autoCompleteTVBloodTypeRequest);
        sp_units_num=view.findViewById(R.id.sp_no_of_units);
        sp_hospitalName=view.findViewById(R.id.sp_hospital_location);
        et_message=view.findViewById(R.id.et_info_RBlood);
        //views of the dialog



        btn_submit=view.findViewById(R.id.btn_submitRequest);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequestBlood();
            }
        });



    }
    private void intiSpinner(){
        //two sp (no of units and the hospital name );
        //get the string array
        String units_no []=getResources().getStringArray(R.array.num_of_units);

        ArrayAdapter adapter_units=new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,units_no);
        sp_units_num.setAdapter(adapter_units);
        //get the selected item
        sp_units_num.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                no_units=adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getContext(), "select units number.", Toast.LENGTH_SHORT).show();
            }
        });


        /*
              Hospital name
         */
        String hos_name[]=getResources().getStringArray(R.array.hospital_location);
        ArrayAdapter adapter_hospital=new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,hos_name);
        sp_hospitalName.setAdapter(adapter_hospital);
        //get selected items
        sp_hospitalName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hospitalName=adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(getContext(), "selected "+hos_name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Toast.makeText(getContext(), "select the hospital .", Toast.LENGTH_SHORT).show();
            }
        });

        /*
            Blood type
         */

        String [] blood=getResources().getStringArray(R.array.blood_type);
        ArrayAdapter<String> bloodAdapter=new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,blood);
        act_bloodTypes.setAdapter(bloodAdapter);
        act_bloodTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bloodType=adapterView.getItemAtPosition(i).toString();
            }
        });

        //get the message from the user


    }

    private void submitRequestBlood() {

        //create a new database for the requests
        if (name==null && hospitalName==null && no_units==null){
            Toast.makeText(getContext(), "Filed ..", Toast.LENGTH_SHORT).show();
        }else {
            message=et_message.getText().toString();
            RequestModel requestModel=new RequestModel(name,hospitalName,bloodType,message,no_units);

            FirebaseDatabase.getInstance().getReference(DB_REQUEST).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(requestModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        setDialog();
                        Log.d(TAG, "onComplete: Request is done " );
                        //Toast.makeText(getContext(), "Request is done", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Filed to create a request ", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void getUserDetails(){
        //get the name of the user from the database
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mReference=FirebaseDatabase.getInstance().getReference(RegisterActivity.DBPATH);
        String userID=mUser.getUid();

        mReference.child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel=snapshot.getValue(UserModel.class);
                        if (userModel!=null){
                            name=userModel.name;
                            Log.d(TAG, "onDataChange: done to get data)");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: Filed to get the user data from the database (Request)");
                    }
                });

    }

    //
    private void setDialog(){
        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.request_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        iv_dialog=dialog.findViewById(R.id.iv_req_dialog);
        btn_dialog=dialog.findViewById(R.id.btn_ok_req_dialog);
        dialog.show();

        try {
            btn_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            });

        }catch (Exception e){
            Log.d(TAG, "setDialog: "+ e);
        }

    }
}