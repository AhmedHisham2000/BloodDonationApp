package com.example.blooddonation.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonation.Classes.CheckInternetConnection;
import com.example.blooddonation.R;
import com.example.blooddonation.Classes.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    public static final String DBPATH = "Users";
    private static final int RC_SIGN_IN = 9001;

    private ConstraintLayout layout;
    private TextView tv_login;
    private Button btn_createAnAccount,btn_signup_google,btn_signFacebook;

    private EditText et_fullName, et_email, et_phone, et_password;
    private ProgressBar pb_reg;
    private UserModel user;
    private String email, phone;
    //private Matcher mobileMatcher;
    private ProgressDialog pDialog;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        createRequest();
        mAuth = FirebaseAuth.getInstance();


    }

    //Configure google sign-in..
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_string))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initViews() {
        layout = findViewById(R.id.parentReg);
        tv_login = findViewById(R.id.tv_login);

        btn_createAnAccount = findViewById(R.id.btn_create_an_account);
        btn_signup_google=findViewById(R.id.btn_signUp_with_google);
        btn_signFacebook=findViewById(R.id.btn_signWithFacebook);

        et_email = findViewById(R.id.et_email_reg);
        et_fullName = findViewById(R.id.et_fullname_reg);
        et_password = findViewById(R.id.et_password_reg);
        et_phone = findViewById(R.id.et_phone_reg);
        pb_reg = findViewById(R.id.pb_reg);
        pDialog = new ProgressDialog(this);

        //clicks
        tv_login.setOnClickListener(this);
        btn_createAnAccount.setOnClickListener(this);
        btn_signup_google.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_an_account:
                registerUser();
                break;
            case R.id.tv_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.btn_signUp_with_google:
                signupGoogle();
                break;
            case R.id.btn_signWithFacebook:
                signWithFacebook();
                break;
        }
    }

    private void signWithFacebook(){

    }

    private void signupGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                showSnack("Google sign in filed ");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"onSuccess:logged In");

                        //get logged-in user
                        FirebaseUser user=mAuth.getCurrentUser();
                        //get user details(info)
                        String uID=user.getUid();
                        String email=user.getEmail();

                        //check if the user new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            /*if true : user is a new > Account is created ..
                                and add the details in the realtime DB
                             */
                            showSnack("Account Created");
                            //go to the Home Activity
                        }
                        else {
                            showSnack("Existing user.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnack("Filed , Try Again!");
            }
        });
    }

    private void registerUser() {
        // check of the internet connected or not first.
        if (!CheckInternetConnection.isConnected(this)) {
            CheckInternetConnection.showCustomDialog(this);
        }


        //get the data from the EditTexts:
        String name = et_fullName.getText().toString().trim();
        email = et_email.getText().toString().trim();
        phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();


        //check if the et is empty or not
        if (name.isEmpty()) {
            et_fullName.setError("Full name Required");
            et_fullName.requestFocus();
            return;
        }
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

        if (phone.isEmpty()) {
            et_phone.setError("Phone is Required");
            et_phone.requestFocus();
            return;
        }
        if (phone.length() != 11) {
            et_phone.setError("Phone is not Valid");
            et_phone.requestFocus();
            return;
        }
//        if (!mobileMatcher.find()){
//            et_phone.setError("Enter a valid mobile number!");
//            et_phone.requestFocus();
//            return;
//        }
        if (password.isEmpty()) {
            et_password.setError("Password Required");
            et_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Min password length should be 6 characters!");
            et_password.requestFocus();
        }

        //pb_reg.setVisibility(View.VISIBLE);
        pDialog.setMessage("Registered Now..");
        pDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //check if the user Registered or not
                //isUserAlreadySignUp(email);
                checkEmail(email);
                if (task.isSuccessful()) {
//                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
//                    //update Display name of a user
//                    UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.
//                            Builder().setDisplayName(name).build();
//                    firebaseUser.updateProfile(profileChangeRequest);
                    //do not forget to delete the name from User Model Constructors


                    user = new UserModel(name, email, phone, password);
                    FirebaseDatabase.getInstance().getReference(DBPATH)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Data is set in the realtime data base");
                                sendEmailVerification();
                                showSnack("User has been Registered Successfully.");
                            } else {
                                showSnack("Filed To Register,Try Again!");
                                Log.d(TAG, "Filed to  set the data  in the realtime data base");
                            }
                        }
                    });
                    pDialog.dismiss();
                    //sendEmailVerification();
                } else {
                    try {
                         throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        et_password.setError("your password is too week");
                        et_password.requestFocus();
                        pDialog.dismiss();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        showSnack("Your email is invalid or already in use. Kindly re-enter");
                        //et_email.setError("Your email is invalid or already in use. Kindly re-enter.");
                        et_email.requestFocus();
                        pDialog.dismiss();
                    }catch (FirebaseAuthUserCollisionException e){
                        //showSnack("User is already registered with this email. Use another email!");
                        //et_email.setError("User is already registered with this email. Use another email.");
                        Log.d(TAG, "onComplete: exception");
                        et_email.requestFocus();
                        pDialog.dismiss();
                    }
                    catch (Exception e) {
                        Log.d(TAG,e.getMessage());
                        showSnack("Filed to register, Try Again!");
                        pDialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * to send email verification before he login
     */
    private void sendEmailVerification() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showSnack("Check your Email for Verification");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    } else {
                        Log.d(TAG, "onComplete: Filed to verify email.");
                    }
                }
            });
        }
    }

    /*
        to check if the email already registered or not
     */
    private void isUserAlreadySignUp(String email) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference(DBPATH).child(email)
                .orderByKey().equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = snapshot.exists();
                if (isExist) {
                    Toast.makeText(getApplicationContext(), "the Email is Already Registered", Toast.LENGTH_SHORT).show();
                } else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_SHORT).show();

            }
        });
        // after getting reference of child, checked orderbykey so we get if phone is key we can get ref in dataSnapshot...


    }

    /**
     * check if the email already registered before or not.
     * @param email
     *  checkEmail()
     */
    private void checkEmail(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: "+ "already registered ");
                    showSnack("Email is already registered.");
//                    boolean isExist = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();
//                    if (isExist) {
//                        showSnack("Email Already is registered before,Go to login");
//                            Log.d(TAG, "onComplete: "+"old user");
////                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
////                        finish();
//                    }else {
//                        Log.d(TAG, "onComplete: " + "user is a new user ");
//                    }
                }else {
                    Log.d(TAG, "onComplete: "+"new user ..");
                }
            }
        });
    }

    private void showSnack(String msg) {
        Snackbar.make(layout, msg, Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                })
                .show();
    }
}