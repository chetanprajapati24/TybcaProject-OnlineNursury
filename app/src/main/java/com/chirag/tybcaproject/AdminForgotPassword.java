package com.chirag.tybcaproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chirag.tybcaproject.Activity.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminForgotPassword extends BaseActivity {

    private EditText mEmailEditText;
    private Button mResetPasswordButton, backButton;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mResetPasswordButton = findViewById(R.id.reset_password_button);
        backButton = findViewById(R.id.back_button);
        mProgressBar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();

        mResetPasswordButton.setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                return;
            }

            mResetPasswordButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);

            resetPassword(email);
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
               .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                        mResetPasswordButton.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Error sending reset password email", Toast.LENGTH_SHORT).show();
                        mResetPasswordButton.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}