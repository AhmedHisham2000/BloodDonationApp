package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blooddonation.Classes.CheckInternetConnection;
import com.example.blooddonation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {
    private EditText et_new_password,et_old_password;
    private Button btn_update_password;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        et_new_password=findViewById(R.id.et_password_update);
        et_old_password=findViewById(R.id.et_oldPassword);
        btn_update_password=findViewById(R.id.btn_updatePassword);

        mAuth=FirebaseAuth.getInstance();

        btn_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        //check of the internet connected or not first.
        if(!CheckInternetConnection.isConnected(this)){
            //show the dialog
            CheckInternetConnection.showCustomDialog(this);
        }

        //get the data from et

        String newPassword=et_new_password.getText().toString().trim();
        String oldPassword=et_old_password.getText().toString().trim();
        if (oldPassword.isEmpty()){
            et_old_password.setError("Required");
            et_old_password.requestFocus();
            return;
        }
        if (oldPassword.length()<6){
            et_old_password.setError("Min password length should be 6 characters!");
            et_old_password.requestFocus();
            return;
        }

        //check if it is empty or not
        if (newPassword.isEmpty()){
            et_new_password.setError("Required");
            et_new_password.requestFocus();
            return;
        }
        if (newPassword.length()<6){
            et_new_password.setError("Min password length should be 6 characters!");
            et_new_password.requestFocus();
        }

        user=FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
       // String email=user.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                showDialog();
                                //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Authentication Filed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void showDialog(){
        Button btn_ok=findViewById(R.id.btn_ok_dialog);

        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext(),R.style.fullScreenDialog);
        View view=getLayoutInflater().inflate(R.layout.full_screen_dialog,null);
        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.show();


    }
}