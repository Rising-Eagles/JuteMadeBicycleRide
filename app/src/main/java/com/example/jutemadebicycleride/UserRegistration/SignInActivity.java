package com.example.jutemadebicycleride.UserRegistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jutemadebicycleride.R;
import com.example.jutemadebicycleride.UserActivities.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    //Define all the fields
    private ImageView regd_usr_verification_code_icon;
    private EditText regd_usr_mobile, regd_usr_verification_code;
    private Button give_verification_code, verify_regd_usr;
    private TextView usr_req_to_register;
    private ProgressBar sign_in_loader;
    private String regd_usr_verification_code_id;
    private FirebaseAuth logFbAuth;
    private FirebaseUser registered_usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(SignInActivity.this);
        getSupportActionBar().setTitle("Log In Page");


        // Initialize all the fields
        regd_usr_verification_code_icon = (ImageView) findViewById(R.id.icon_regd_user_verify_code);
        regd_usr_mobile = (EditText) findViewById(R.id.regd_user_mobile);
        regd_usr_verification_code = (EditText) findViewById(R.id.regd_user_verify_code);
        give_verification_code = (Button) findViewById(R.id.continue_sign_in);
        verify_regd_usr = (Button) findViewById(R.id.verify_sign_in);
        usr_req_to_register = (TextView) findViewById(R.id.registration_request);
        sign_in_loader = (ProgressBar) findViewById(R.id.sign_in_progbar);
        logFbAuth = FirebaseAuth.getInstance();
        registered_usr = logFbAuth.getCurrentUser();

        // Hide progress bar
        sign_in_loader.setVisibility(View.INVISIBLE);

        // Hide verifciation section
        regd_usr_verification_code_icon.setVisibility(View.INVISIBLE);
        regd_usr_verification_code.setVisibility(View.INVISIBLE);
        verify_regd_usr.setVisibility(View.INVISIBLE);

        // User doen't have any account
        Intent registration_page = new Intent(SignInActivity.this, RegistrationActivity.class);
        startActivity(registration_page);

        // Send verification number if the number is registered
        give_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide verification code sending button
                give_verification_code.setVisibility(View.INVISIBLE);

                // Show progress bar
                sign_in_loader.setVisibility(View.VISIBLE);

                // Cast phone number field
                final String regd_mobile_no = regd_usr_mobile.getText().toString();

                // Validate phone number field
                if(regd_mobile_no.isEmpty() || !Patterns.PHONE.matcher(regd_mobile_no).matches()){
                    Toast.makeText(getApplicationContext(), "Please write your valid mobile number.", Toast.LENGTH_SHORT).show();
                    give_verification_code.setVisibility(View.VISIBLE);
                    sign_in_loader.setVisibility(View.INVISIBLE);
                    return;
                }

                // Send verification code to the user
                PhoneAuthProvider.getInstance().verifyPhoneNumber(("+880"+regd_mobile_no), 60,
                        TimeUnit.SECONDS, SignInActivity.this, regd_user_phone_callback);
                Toast.makeText(getApplicationContext(), "You have been sent a verification code to " +
                        ("+880"+regd_mobile_no) + " number.", Toast.LENGTH_SHORT).show();

                // Show verification section and
                // Hide progress bar
                regd_usr_verification_code_icon.setVisibility(View.VISIBLE);
                regd_usr_verification_code.setVisibility(View.VISIBLE);
                verify_regd_usr.setVisibility(View.VISIBLE);
                sign_in_loader.setVisibility(View.INVISIBLE);
            }
        });

        // Verify already registered user
        verify_regd_usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide verification button
                verify_regd_usr.setVisibility(View.INVISIBLE);

                // SHow progress bar
                sign_in_loader.setVisibility(View.VISIBLE);

                // Cast verificaiton code field
                String regd_verification_code = regd_usr_verification_code.getText().toString();

                // Validate verification field
                if( regd_verification_code.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your verification code.", Toast.LENGTH_SHORT).show();
                    verify_regd_usr.setVisibility(View.VISIBLE);
                    sign_in_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if( regd_verification_code.length() <6 ){
                    Toast.makeText(getApplicationContext(), "Please enter a valid verification code.", Toast.LENGTH_SHORT).show();
                    verify_regd_usr.setVisibility(View.VISIBLE);
                    sign_in_loader.setVisibility(View.INVISIBLE);
                    return;
                }

                // Verification code requirements have been filled
                else{
                    userPhoneNoVerification(regd_verification_code);
                }
            }
        });
    }

    // Verification state changes
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            regd_user_phone_callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String regd_auto_verification_code = phoneAuthCredential.getSmsCode();
            if( regd_auto_verification_code != null ){
                regd_usr_verification_code.setText(regd_auto_verification_code);
                userPhoneNoVerification(regd_auto_verification_code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if( e instanceof FirebaseAuthInvalidCredentialsException){
                Toast.makeText(getApplicationContext(), "You have entered a wrong code", Toast.LENGTH_LONG).show();
            }
            else if( e instanceof FirebaseTooManyRequestsException){
                Toast.makeText(getApplicationContext(), "Sorry, you can't register anymore. Golden J-Ride has reached at its maximum numbr of user requests", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            regd_usr_verification_code_id = s;
        }
    };

    // User verification
    public void userPhoneNoVerification(String regd_usra_verification_code){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(regd_usr_verification_code_id, regd_usra_verification_code);
        logFbAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // User has entered the correct code
                if(task.isSuccessful()){
                    Intent home_page= new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(home_page);
                    finish();
                    Toast.makeText(getApplicationContext(), "Welcome to Golden J-Ride", Toast.LENGTH_SHORT).show();
                }

                // User has entered a wrong code
                else {
                    Toast.makeText(getApplicationContext(), "You have enter the wrong code. Please check again to Log In", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // If user has already logged in, then go to the home page

    @Override
    protected void onStart() {
        super.onStart();
        if( registered_usr != null ){
            Intent home_page= new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(home_page);
            finish();
        }
    }
}
