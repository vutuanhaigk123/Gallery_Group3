package com.example.view;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.view.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        mAuth = FirebaseAuth.getInstance();
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    createAccount();
                }
            }
        });
    }

    private void createAccount() {
        String email = binding.edtSignUpEmail.getText().toString();
        String password = binding.edtSignUpPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Sign up unsuccessfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean validateFields() {
        int yourDesiredLength = 6;
        if (binding.edtSignUpEmail.getText().length() < yourDesiredLength) {
            binding.edtSignUpEmail.setError("This field need at least 6 characters");
            return false;
        } else if (binding.edtSignUpPassword.getText().length() < yourDesiredLength) {
            binding.edtSignUpPassword.setError("This field need at least 6 characters");
            return false;
        }
        else if(!binding.edtSignUpConfirmPassword.getText().toString().equals(binding.edtSignUpPassword.getText().toString()) ){
            binding.edtSignUpConfirmPassword.setError("Password and confirm password does not match");
            return false;
        }
        else {
            return true;
        }
    }
}