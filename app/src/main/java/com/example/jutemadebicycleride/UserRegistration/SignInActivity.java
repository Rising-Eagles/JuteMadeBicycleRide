package com.example.jutemadebicycleride.UserRegistration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.jutemadebicycleride.R;

public class SignInActivity extends AppCompatActivity {

    //Define all the fields
    private EditText regd_usr_mobile, regd_usr_verification_code;
    private Button give_verify_code, verify_regd_usr;
    private ProgressBar sign_in_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setTitle("Log In");


    }
}
