package com.example.karim.troupia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
public class LoginAct extends AppCompatActivity {
    Button BtnRegistter;
    Button BtnLogin;
    EditText mPhoneText,mPasswordText;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallsBack;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcalls;
    private FirebaseAuth mAuth;
    UserSessionManager session;
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
    ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getBaseContext());
        setContentView(R.layout.activity_login);
        session=new UserSessionManager(this);
        mAuth=FirebaseAuth.getInstance();
        BtnRegistter=findViewById(R.id.BtnRigister);
        BtnLogin=findViewById(R.id.btnLogin);
        mPasswordText=findViewById(R.id.EditTextPassword);
        mPhoneText=findViewById(R.id.EditTextPhoneNumber);
        //facebook login
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(
                    "com.example.karim.troupia",
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Signature signature : info.signatures) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
        //
        loginButton=findViewById(R.id.FBlogin_button);
        callbackManager=CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                session.CreatUserLoginSession("fb",loginResult.getAccessToken().getUserId());
                startActivity(new Intent(getBaseContext(),MapsActivity.class));
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginAct.this, "canseld", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        progressbar=findViewById(R.id.progress_bar);
        //
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(getBaseContext(),MapsActivity.class));
                String PhoneNumber=mPhoneText.getText().toString();
                String Password=mPasswordText.getText().toString();
                if(PhoneNumber.isEmpty()){
                    mPhoneText.setError("please enter the phone number");
                    mPhoneText.findFocus();
                }
                if (Password.isEmpty()){
                    mPasswordText.setError("Enter the password");
                    mPasswordText.findFocus();
                }
                progressbar.setVisibility(View.VISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        PhoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginAct.this,               // Activity (for callback binding
                        mcalls );

            }
        });
        mcalls=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                // ...
            }

        };
        BtnRegistter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }
    private void openDialog() {
        AlertDialog.Builder RegisterDialog=new AlertDialog.Builder(this);
        AlertDialog RegisterAlert=RegisterDialog.create();
        LayoutInflater inflater=this.getLayoutInflater();
        RegisterAlert.setView(inflater.inflate(R.layout.rigister_layout,null));
        RegisterAlert.show();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);
                            String s=mPhoneText.getText().toString();
                            // Sign in success, update UI with the signed-in user's information
                         //   session.CreatUserLoginSession(s,"00");
                            FirebaseUser user = task.getResult().getUser();
                            session.CreatUserLoginSession(user.getPhoneNumber(),mPasswordText.getText().toString());
                            Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getBaseContext(),task.getException().getMessage()+"حدث خطأ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
