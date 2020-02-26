package com.example.jutemadebicycleride.UserRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.jutemadebicycleride.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    //Define all the fields
    private ImageView regd_usr_verification_code_icon;
    private EditText regd_usr_mobile, regd_usr_verification_code;
    private Button give_verify_code, verify_regd_usr;
    private ProgressBar sign_in_loader;
    private FirebaseAuth logFbAuth;
    private FirebaseUser registered_usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(SignInActivity.this);
        getSupportActionBar().setTitle("Log In");


        // Initialize all the fields
        regd_usr_verification_code_icon = (ImageView) findViewById(R.id.icon_regd_user_verify_code);
        regd_usr_mobile = (EditText) findViewById(R.id.regd_user_mobile);
        regd_usr_verification_code = (EditText) findViewById(R.id.regd_user_verify_code);
        give_verify_code = (Button) findViewById(R.id.continue_sign_in);
        verify_regd_usr = (Button) findViewById(R.id.verify_sign_in);
        sign_in_loader = (ProgressBar) findViewById(R.id.sign_in_progbar);
        logFbAuth = FirebaseAuth.getInstance();
        registered_usr = logFbAuth.getCurrentUser();

        // Hide progress bar
        sign_in_loader.setVisibility(View.INVISIBLE);

        // Hide verifciation section
        regd_usr_verification_code_icon.setVisibility(View.INVISIBLE);
        regd_usr_verification_code.setVisibility(View.VISIBLE);
        verify_regd_usr.setVisibility(View.INVISIBLE);

        // Send verification number if the number is registered


    }
}
