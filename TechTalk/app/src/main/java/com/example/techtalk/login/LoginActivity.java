package com.example.techtalk.login;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.techtalk.MainActivity;
import com.example.techtalk.R;
import com.example.techtalk.password.ResetPasswordActivity;
import com.example.techtalk.signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
    }
    public void tvSignupClick(View v){
        startActivity(new Intent(this, SignupActivity.class));
    }
    public void btnLoginClick(View v){
        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();
        if (email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }
        else if(password.equals("")){
            etPassword.setError(getString(R.string.enter_password));
        }
        else
            {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Login Failed : "+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        }
    }
    public void tvResetPasswordClick(View view){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}