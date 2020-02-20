package com.example.jutemadebicycleride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.jutemadebicycleride.UserRegistration.RegistrationActivity;
import com.example.jutemadebicycleride.UserRegistration.SignInActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ds = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(ds);
            }
        });

    }
}
