package com.example.view;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.view.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private Context context;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        context = binding.getRoot().getContext();
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        mAuth = FirebaseAuth.getInstance();
        if(isLogin){
            binding.edtLoginEmail.setText(sharedPreferences.getString("email", ""));
            binding.edtLoginPassword.setText(sharedPreferences.getString("password", ""));
            login();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()){
                    login();
                }
            }
        });
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login(){
        String email = binding.edtLoginEmail.getText().toString();
        String password = binding.edtLoginPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.loginFailedText.setVisibility(View.GONE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLogin", true);
                            editor.putString("email", email);
                            editor.putString("password", password);
                            editor.commit();
                            Intent intent = new Intent(context,MainActivity.class);
                            intent.putExtra("userEmail", email);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            binding.loginFailedText.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private boolean validateFields() {
        int yourDesiredLength = 5;
        if (binding.edtLoginEmail.getText().length() < yourDesiredLength) {
            binding.edtLoginEmail.setError("This field cannot be empty");
            return false;
        } else if (binding.edtLoginPassword.getText().length() < yourDesiredLength) {
            binding.edtLoginPassword.setError("This field cannot be empty");
            return false;
        } else {
            return true;
        }
    }
}