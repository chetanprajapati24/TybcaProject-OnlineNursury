package com.chirag.tybcaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chirag.tybcaproject.Activity.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Userloginphone extends BaseActivity {

    EditText num;
    Button sendotp,signinemail;
    TextView signup;
    ImageButton back;
    CountryCodePicker cpp;
    FirebaseAuth Fauth;
    String number;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userloginphone);

        num = (EditText)findViewById(R.id.number);
        sendotp = (Button)findViewById(R.id.otp);
        cpp=(CountryCodePicker)findViewById(R.id.CountryCode);
        signinemail=(Button)findViewById(R.id.btnEmail);
        signup = (TextView)findViewById(R.id.acsignup);
        back=(ImageButton)findViewById(R.id.backBtn);

        Fauth = FirebaseAuth.getInstance();

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number=num.getText().toString().trim();
                String Phonenumber = cpp.getSelectedCountryCodeWithPlus()+number;
                Intent b = new Intent(Userloginphone.this, Usersendotp.class);

                b.putExtra("Phonenumber",Phonenumber);
                startActivity(b);
                finish();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Userloginphone.this, UserRegistration.class));
                finish();
            }
        });
        signinemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Userloginphone.this, Userlogin.class));
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Userloginphone.this, MainMenu.class));
                finish();
            }
        });

    }
}