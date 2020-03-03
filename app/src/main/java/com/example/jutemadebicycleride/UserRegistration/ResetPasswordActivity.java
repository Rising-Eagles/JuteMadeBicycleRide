package com.example.jutemadebicycleride.UserRegistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jutemadebicycleride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    // Define all the fields
    private EditText rp_regd_usr_email;
    private Button reset_password_regd_usr;
    private ProgressBar reset_password_loader;
    private FirebaseAuth rpFbAuth;
    private FirebaseUser rp_registered_usr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        FirebaseApp.initializeApp(ResetPasswordActivity.this);

        // Initialize all the fields
        rp_regd_usr_email = (EditText) findViewById(R.id.regd_user_reset_password_email);
        reset_password_regd_usr = (Button) findViewById(R.id.regd_user_reset_password);
        reset_password_loader = (ProgressBar) findViewById(R.id.reset_password_progbar);
        rpFbAuth = FirebaseAuth.getInstance();
        rp_registered_usr = rpFbAuth.getCurrentUser();

        // Hide progress bar
        reset_password_loader.setVisibility(View.INVISIBLE);

        // Redirect user to a link to reset the password
        reset_password_regd_usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide reset password button
                reset_password_regd_usr.setVisibility(View.INVISIBLE);

                // Show progress bar
                reset_password_loader.setVisibility(View.VISIBLE);

                // Cast email field
                String rp_regd_email = rp_regd_usr_email.getText().toString();

                // Validate email field
                if( rp_regd_email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(rp_regd_email).matches() ){
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your valid email address.", Toast.LENGTH_SHORT).show();
                    reset_password_regd_usr.setVisibility(View.VISIBLE);
                    reset_password_loader.setVisibility(View.INVISIBLE);
                    return;
                }

                // User has filled his email
                else{
                    rpFbAuth.sendPasswordResetEmail(rp_regd_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // User has entered a correct email
                                    if(task.isSuccessful()){
                                        Toast.makeText(ResetPasswordActivity.this, "A link has been sent to your email. Click to reset your password", Toast.LENGTH_LONG).show();
                                        Intent sign_in_page_bk = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                                        startActivity(sign_in_page_bk);
                                    }

                                    // User has entered a wrong email
                                    else{
                                        Toast.makeText(ResetPasswordActivity.this, "We could not send any email since it either doesn't exist or you have registered with another email", Toast.LENGTH_LONG).show();
                                        reset_password_regd_usr.setVisibility(View.VISIBLE);
                                        reset_password_loader.setVisibility(View.INVISIBLE);
                                        return;
                                    }
                                }
                            });
                }
            }
        });
    }
}
