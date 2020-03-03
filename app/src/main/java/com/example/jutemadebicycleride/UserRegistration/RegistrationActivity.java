package com.example.jutemadebicycleride.UserRegistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jutemadebicycleride.R;
import com.example.jutemadebicycleride.UserActivities.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    // Define all the fields
    private ImageView usr_prof_pic;
    private static int permission_req_code = 1, image_req_code = 1;
    private Uri image_uri;
    private Button create_usr;
    private EditText usr_name, usr_birthday, usr_address, usr_height, usr_gender, usr_profession, usr_cycling, usr_mobile,
            usr_email, usr_password, usr_confirm_password;
    private RadioGroup usr_choice_gender, usr_choice_profession, usr_choice_cycling;
    private RadioButton usr_selected_gender, usr_selected_profession, usr_selected_cycling;
    private String usr_chosen_gender, usr_chosen_profession, usr_chosen_cycling, usr_verification_code_id,
            country_code_bangladesh = "+880";
    private static Pattern Allowed_Password = Pattern.compile("^(?=.*?[A-Z])(?=.*?[0-9]).{6,}$");
    private ProgressBar registration_loader;
    private Calendar usr_calendar;
    private FirebaseAuth fbAuth;
    private FirebaseUser registered_new_usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(RegistrationActivity.this);
        getSupportActionBar().setTitle("Create Account");

        // Initialize all the fields
        usr_prof_pic = (ImageView) findViewById(R.id.user_profile_picture);
        usr_name = (EditText) findViewById(R.id.user_name);
        usr_birthday = (EditText) findViewById(R.id.user_birthday);
        usr_address = (EditText) findViewById(R.id.user_address);
        usr_height = (EditText) findViewById(R.id.user_height);
        usr_gender = (EditText) findViewById(R.id.user_gender);
        usr_profession = (EditText) findViewById(R.id.user_profession);
        usr_cycling = (EditText) findViewById(R.id.user_cycling);
        usr_mobile = (EditText) findViewById(R.id.user_mobile);
        usr_email = (EditText) findViewById(R.id.user_email);
        usr_password = (EditText) findViewById(R.id.user_password);
        usr_confirm_password = (EditText) findViewById(R.id.user_confirm_password);
        usr_choice_gender = (RadioGroup) findViewById(R.id.user_gender_choose);
        usr_choice_profession = (RadioGroup) findViewById(R.id.user_profession_choose);
        usr_choice_cycling = (RadioGroup) findViewById(R.id.user_cycling_choose);
        create_usr = (Button) findViewById(R.id.complete_reg);
        registration_loader = (ProgressBar) findViewById(R.id.reg_progbar);
        fbAuth = FirebaseAuth.getInstance();
        registered_new_usr = fbAuth.getCurrentUser();

        // Hide progress bar
        registration_loader.setVisibility(View.INVISIBLE);

        // Select user profile picture from Gallery
        usr_prof_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ask to access user's gallery
                if(Build.VERSION.SDK_INT >=16){
                    if(ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                            Toast.makeText(RegistrationActivity.this, "Please allow us to access your gallery", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ActivityCompat.requestPermissions(RegistrationActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permission_req_code);
                        }
                    }
                    else{
                        Intent open_gallery = new Intent(Intent.ACTION_GET_CONTENT);
                        open_gallery.setType("image/*");
                        startActivityForResult(open_gallery, image_req_code);
                    }
                }
                // Open users's gallery
                else{
                    Intent open_gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    open_gallery.setType("image/*");
                    startActivityForResult(open_gallery, image_req_code);
                }
            }
        });

        // Choose user birthday
        usr_calendar = Calendar.getInstance();
        final int usr_birth_year = usr_calendar.get(Calendar.YEAR);
        final int usr_birth_month = usr_calendar.get((Calendar.MONTH));
        final int usr_birth_day = usr_calendar.get(Calendar.DAY_OF_MONTH);
        usr_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog show_usr_calendar = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int usr_birth_year, int usr_birth_month, int usr_birth_day) {
                        usr_birth_month = usr_birth_month + 1;
                        String usr_birth_date = usr_birth_day + "/" + usr_birth_month + "/" + usr_birth_year;
                        usr_birthday.setText(usr_birth_date);
                    }
                }, usr_birth_year, usr_birth_month, usr_birth_day);
                show_usr_calendar.show();
            }
        });

        // Choose user gender
        usr_choice_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedIdGender) {
                usr_selected_gender = usr_gender.findViewById(checkedIdGender);
                if( checkedIdGender == R.id.user_gender_male){
                    usr_chosen_gender = "Male";
                }
                else if( checkedIdGender == R.id.user_gender_female){
                    usr_chosen_gender = "Female";
                }
                else if( checkedIdGender == R.id.user_gender_others){
                    usr_chosen_gender = "Others";
                }
                usr_gender.setText(usr_chosen_gender);
            }
        });

        // Choose user profession
        usr_choice_profession.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedIdProfession) {
                usr_selected_profession = usr_profession.findViewById(checkedIdProfession);
                if( checkedIdProfession == R.id.user_profession_student){
                    usr_chosen_profession = "Student";
                }
                else if( checkedIdProfession == R.id.user_profession_teacher){
                    usr_chosen_profession = "Teacher";
                }
                else if( checkedIdProfession == R.id.user_profession_government_job){
                    usr_chosen_profession = "Government job";
                }
                else if( checkedIdProfession == R.id.user_profession_private_job){
                    usr_chosen_profession = "Private job";
                }
                else if( checkedIdProfession == R.id.user_profession_businessman){
                    usr_chosen_profession = "Businessman";
                }
                usr_profession.setText(usr_chosen_profession);
            }
        });

        // Choose user_cycling_experience
        usr_choice_cycling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedIdCycling) {
                usr_selected_cycling = usr_cycling.findViewById(checkedIdCycling);
                if( checkedIdCycling == R.id.user_cycling_yes){
                    usr_chosen_cycling = "Yes";
                }
                else if( checkedIdCycling == R.id.user_cycling_no){
                    usr_chosen_cycling = "No";
                }
                usr_cycling.setText(usr_chosen_cycling);
            }
        });

        // Create new user
        create_usr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide registration button
                create_usr.setVisibility(View.INVISIBLE);

                // Show progress bar
                registration_loader.setVisibility(View.VISIBLE);

                // Cast all the fields
                final String name = usr_name.getText().toString();
                final String birthday = usr_birthday.getText().toString();
                final String address = usr_address.getText().toString();
                final String height = usr_height.getText().toString();
                final String gender = usr_gender.getText().toString();
                final String profession = usr_profession.getText().toString();
                final String cycling = usr_cycling.getText().toString();
                final String mobile_no = usr_mobile.getText().toString();
                final String email = usr_email.getText().toString();
                final String password = usr_password.getText().toString();
                final String confirm_password = usr_confirm_password.getText().toString();

                // Validate all the fields
                if(name.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please enter your name.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(birthday.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please tell your birthday.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(address.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please enter your address.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(height.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please enter your height.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(gender.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please select your gender.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(profession.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please select your profession.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(cycling.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Please tell whether you can do cycling.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(mobile_no.isEmpty() || !Patterns.PHONE.matcher(mobile_no).matches()){
                    Toast.makeText(RegistrationActivity.this, "Please write your valid mobile number.", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegistrationActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(password.isEmpty() || password.length()<6){
                    Toast.makeText(RegistrationActivity.this, "Please enter a password of at least 6 chaaracters", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(!Allowed_Password.matcher(password).matches()){
                    Toast.makeText(RegistrationActivity.this, "Your password is took weak!!! It should contain uppercase letters and digits.",Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(!confirm_password.equals(password)){
                    Toast.makeText(RegistrationActivity.this, "Password didn't match! Try it ne more time", Toast.LENGTH_SHORT).show();
                    create_usr.setVisibility(View.VISIBLE);
                    registration_loader.setVisibility(View.INVISIBLE);
                    return;
                }

                // All requirements have been filled
                else{
                    fbAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        registered_new_usr.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Intent sign_in_page = new Intent(RegistrationActivity.this, SignInActivity.class);
                                                    startActivity(sign_in_page);
                                                    Toast.makeText(RegistrationActivity.this, "You have registered successfully! Please check your email to verify your registration.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Since user can't be permitted to sign in without verification
                                                    fbAuth.signOut();
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(RegistrationActivity.this, "We couldn't send any verrification email since the email is not correct. Check your email please.",
                                                            Toast.LENGTH_SHORT).show();
                                                    create_usr.setVisibility(View.VISIBLE);
                                                    registration_loader.setVisibility(View.INVISIBLE);
                                                    return;
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(RegistrationActivity.this, "Failed to create your account! Check your information please",
                                                Toast.LENGTH_SHORT).show();
                                        create_usr.setVisibility(View.VISIBLE);
                                        registration_loader.setVisibility(View.INVISIBLE);
                                        return;
                                    }
                                }
                            });
                }
            }
        });
    }


    // Pushing an image reference to the firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == image_req_code && data != null){
            image_uri = data.getData();
            usr_prof_pic.setImageURI(image_uri);
        }
    }
}
