package com.example.f1blog;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;



    EditText editTextUsername;
    EditText userEmail;
    EditText birthDate;
    EditText editTextPassword;
    EditText passwordAgain;

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

        editTextUsername.setText(username);
        editTextPassword.setText(password);


        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String userNameStr=editTextUsername.getText().toString();
        String emailStr=userEmail.getText().toString();
        String birthdateInput=birthDate.getText().toString();
        String passwordStr=editTextPassword.getText().toString();
        String passwordAgainStr=passwordAgain.getText().toString();

        if(!passwordStr.equals(passwordAgainStr)){
            Log.e(LOG_TAG, "Passwords do not match");
            return;
        }


        Log.i(LOG_TAG,"Registration: "+userNameStr+", password: "+passwordStr);
    }

    public void login(View view) {
        Intent intent=new Intent(this, MainActivity.class);
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