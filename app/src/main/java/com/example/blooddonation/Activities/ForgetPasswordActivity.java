package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonation.Classes.CheckInternetConnection;
import com.example.blooddonation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText et_email;
    private Button btn_send;
    private TextView tv_backToLogin;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        intiViews();
        mAuth=FirebaseAuth.getInstance();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }



    private void intiViews() {
        et_email=findViewById(R.id.et_email_forgetPass);
        tv_backToLogin=findViewById(R.id.tv_backToLogin);
        btn_send=findViewById(R.id.btn_send_forgetPass);

        tv_backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
    private void resetPassword() {
        //check of the internet connected or not first.
        if(!CheckInternetConnection.isConnected(this)){
            //show the dialog
            CheckInternetConnection.showCustomDialog(this);
        }


        String email=et_email.getText().toString().trim();

        if (email.isEmpty()) {
            et_email.setError("Required");
            et_email.requestFocus();
            return;
        }
        //To check on the valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please Provide valid email");
            et_email.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "check your email", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),UpdatePasswordActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! Reset Link is Not sent, Try Again" +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}