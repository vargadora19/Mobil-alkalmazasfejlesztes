package com.example.f1blog;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;

    private static final int SECRET_KEY=0;



    EditText editTextUsername;
    EditText userEmail;
    EditText birthDate;
    EditText editTextPassword;
    EditText passwordAgain;

    private FirebaseAuth mAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int secret_key=getIntent().getIntExtra("SECRET_KEY", 1);

        if(secret_key!=0){
            finish();
        }

        editTextUsername= findViewById(R.id.editTextUsername);
        userEmail= findViewById(R.id.userEmail);
        birthDate= findViewById(R.id.birthDate);
        editTextPassword= findViewById(R.id.editTextPassword);
        passwordAgain= findViewById(R.id.passwordAgain);


        preferences=getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username=preferences.getString("username", "");
        String password=preferences.getString("password","");

        userEmail.setText(username);
        editTextPassword.setText(password);

        mAuth=FirebaseAuth.getInstance();


        Log.i(LOG_TAG, "onCreate");

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        findViewById(R.id.loginButton).setOnTouchListener((v, event) -> {
            v.startAnimation(anim);
            return false;
        });

        findViewById(R.id.registerButton).setOnTouchListener((v, event) -> {
            v.startAnimation(anim);
            return false;
        });
    }

    private boolean isValidDate(String dateStr) {
        String[] formats = {"yyyy.MM.dd", "yyyy-MM-dd", "yyyy/MM/dd"};
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date inputDate = sdf.parse(dateStr);

                Date today = new Date();
                if (inputDate.after(today)) {
                    return false;
                }

                return true;
            } catch (ParseException ignored) {
            }
        }
        return false;
    }




    public void register(View view) {
        String userNameStr = editTextUsername.getText().toString().trim();
        String emailStr = userEmail.getText().toString().trim();
        String birthdateInput = birthDate.getText().toString().trim();
        String passwordStr = editTextPassword.getText().toString();
        String passwordAgainStr = passwordAgain.getText().toString();

        if (userNameStr.isEmpty() || emailStr.isEmpty() || birthdateInput.isEmpty() || passwordStr.isEmpty() || passwordAgainStr.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(birthdateInput)) {
            Toast.makeText(this, "Please enter a valid birthdate (format: yyyy.MM.dd, yyyy-MM-dd or yyyy/MM/dd)", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!passwordStr.equals(passwordAgainStr)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(LOG_TAG, "Registration: " + userNameStr + ", email: " + emailStr);

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Account created successfully");
                    startBlog();
                } else {
                    Log.d(LOG_TAG, "Account creation failed");
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void login(View view) {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startBlog(){
        Intent intent=new Intent(this, BlogStart.class);
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