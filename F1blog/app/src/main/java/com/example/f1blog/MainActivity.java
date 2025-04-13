package com.example.f1blog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY=0;

    EditText userName;
    EditText password;

    private SharedPreferences preferences;


    private FirebaseAuth mAuth;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = findViewById(R.id.userEmail);
        password = findViewById(R.id.editTextPassword);
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        findViewById(R.id.loginAsGuestButton).setOnTouchListener((v, event) -> {
            v.startAnimation(anim);
            return false;
        });

        findViewById(R.id.loginButton).setOnTouchListener((v, event) -> {
            v.startAnimation(anim);
            return false;
        });

        findViewById(R.id.registerButton).setOnTouchListener((v, event) -> {
            v.startAnimation(anim);
            return false;
        });

        Log.i(LOG_TAG, "onCreate");
    }


    public void login(View view) {
        String userNameStr = userName.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (userNameStr.isEmpty() || passwordStr.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userNameStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Logged in successfully");
                    startBlog();
                } else {
                    Log.d(LOG_TAG, "Login failed");
                    Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Anonym user loged in successfully");
                    startBlog();
                }else{
                    Log.d(LOG_TAG, "Anonym user login failed");
                    Toast.makeText(MainActivity.this, "Login failed: "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startBlog(){
        Intent intent=new Intent(this, BlogStart.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("username", userName.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }


}