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
import com.example.jutemadebicycleride.ResetPasswordActivity;
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
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    //Define all the fields
    private EditText regd_usr_email, regd_usr_password;
    private Button sign_in_regd_usr;
    private TextView usr_req_to_reset_password, usr_req_to_register;
    private ProgressBar sign_in_loader;
    private static Pattern Usr_Allowed_Password = Pattern.compile("^(?=.*?[A-Z])(?=.*?[0-9]).{6,}$");
    private int sign_in_attempts;
    private FirebaseAuth logFbAuth;
    private FirebaseUser registered_usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(SignInActivity.this);
        getSupportActionBar().setTitle("Log In Page");


        // Initialize all the fields
        regd_usr_email = (EditText) findViewById(R.id.regd_user_email);
        regd_usr_password = (EditText) findViewById(R.id.regd_user_password);
        sign_in_regd_usr = (Button) findViewById(R.id.regd_user_sign_in);
        usr_req_to_reset_password = (TextView) findViewById(R.id.request_reset_password);
        usr_req_to_register = (TextView) findViewById(R.id.request_registration);
        sign_in_loader = (ProgressBar) findViewById(R.id.sign_in_progbar);
        sign_in_attempts = 5;
        logFbAuth = FirebaseAuth.getInstance();
        registered_usr = logFbAuth.getCurrentUser();

        // Hide progress bar
        sign_in_loader.setVisibility(View.INVISIBLE);

        // Hide reset password section
        usr_req_to_reset_password.setVisibility(View.INVISIBLE);

        // If user doesn't have any account
        usr_req_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registration_page = new Intent(SignInActivity.this, RegistrationActivity.class);
                startActivity(registration_page);
            }
        });

        sign_in_regd_usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide email authentication button
                sign_in_regd_usr.setVisibility(View.INVISIBLE);

                // Show progress bar
                sign_in_loader.setVisibility(View.VISIBLE);

                // Cast all the fields
                final String regd_email = regd_usr_email.getText().toString();
                final String regd_password = regd_usr_password.getText().toString();

                // Validate all the fields
                if( regd_email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(regd_email).matches() ){
                    Toast.makeText(getApplicationContext(), "Enter a valid Email address", Toast.LENGTH_SHORT).show();
                    sign_in_regd_usr.setVisibility(View.VISIBLE);
                    sign_in_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if( regd_password.isEmpty() || !Usr_Allowed_Password.matcher(regd_password).matches() ){
                    Toast.makeText(getApplicationContext(), "Your password contains at least 6 character, one uppercase letter and one digit", Toast.LENGTH_SHORT).show();
                    sign_in_regd_usr.setVisibility(View.VISIBLE);
                    sign_in_loader.setVisibility(View.INVISIBLE);
                    return;
                }

                // All requirements have been filled by the user
                else {
                    logFbAuth.signInWithEmailAndPassword(regd_email, regd_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // User's email and password are matched to the database
                            if(task.isSuccessful()){
                                // User has verified email
                                if( registered_usr.isEmailVerified()){
                                    Toast.makeText(getApplicationContext(), "You have logged in successfully!", Toast.LENGTH_LONG).show();
                                    Intent home_page = new Intent(SignInActivity.this, HomeActivity.class);
                                }
                                // User has not verified email
                                else {
                                    Toast.makeText(getApplicationContext(), "Please verify your email.", Toast.LENGTH_LONG).show();
                                    logFbAuth.signOut();
                                }
                            }

                            // User's email and password don't match the database
                            else {
                                Toast.makeText(getApplicationContext(), "Please check your email and password again.", Toast.LENGTH_SHORT).show();
                                // Let user attempt to sign in 5 times max
                                sign_in_attempts--;

                            }
                        }
                    });
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
