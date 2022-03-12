package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.blooddonation.Classes.CheckInternetConnection;
import com.example.blooddonation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG="LOGIN";

    private EditText et_email, et_password;
    private TextView tv_forget_pass, tv_signUp;
    private Button btn_login;
    private ProgressBar pb_login;
    private ProgressDialog pDialog;
    private ConstraintLayout layout;
    private Snackbar snackbar;

    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intiViews();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        keepUserLoggedIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        keepUserLoggedIn();
    }

    private void keepUserLoggedIn() {
        if (user != null) {
            if (user.isEmailVerified()) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                return;
            }
        }

    }

    private void intiViews() {
        layout=findViewById(R.id.parentLogin);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        btn_login = findViewById(R.id.btn_login);
        tv_forget_pass = findViewById(R.id.tv_forgetPassword);
        tv_signUp = findViewById(R.id.tv_signUp);
        pb_login = findViewById(R.id.pb_login);
        pDialog=new ProgressDialog(this);
        //clicks
        tv_signUp.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_forget_pass.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_signUp:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            case R.id.btn_login:
                userLogin();
                break;
            case R.id.tv_forgetPassword:
                //redirect to forget password activity
                startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
                break;

        }

    }

    private void userLogin() {
        //check of the internet connected or not first.
        if (!CheckInternetConnection.isConnected(this)) {
            //show the dialog
            CheckInternetConnection.showCustomDialog(this);
        }


        //get the data from the EditTexts
        String password = et_password.getText().toString().trim();
        String email = et_email.getText().toString().trim();


        if (email.isEmpty()) {
            et_email.setError("Email is Required");
            et_email.requestFocus();
            return;
        }
        // a valid mail or not
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please Provide valid email");
            et_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            et_password.setError("Password Required");
            et_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Min password length should be 6 characters!");
            et_password.requestFocus();
        }

        //pb_login.setVisibility(View.VISIBLE);
        pDialog.setMessage("Loading");
        pDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    pDialog.dismiss();
                    showSnack("Filed to Login");
                } else {
                    //pb_login.setVisibility(View.GONE);
                    checkIfEmailVerified();
                    pDialog.dismiss();
                }
            }
        });
    }

    private void checkIfEmailVerified() {
        //get the user
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified = mUser.isEmailVerified();
        if (!emailVerified) {
            showSnack("Your Email is not Verified yet!");

        } else {
            showSnack("Email is verified and login is Successfully.");
            //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    private void showSnack(String msg){
        Snackbar.make(layout,msg,Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .show();
    }
}