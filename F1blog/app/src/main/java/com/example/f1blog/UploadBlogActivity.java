package com.example.f1blog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UploadBlogActivity extends AppCompatActivity {

    private static final String LOG_TAG = UploadBlogActivity.class.getName();
    private EditText editTextTitle;
    private EditText editTextInfo;
    private Button uploadButton;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private static final String PREFS_NAME = "UploadBlogPrefs";
    private static final String KEY_TITLE = "savedTitle";
    private static final String KEY_INFO = "savedInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_blog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ConstraintLayout mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextTitle = findViewById(R.id.editTextBlogTitle);
        editTextInfo = findViewById(R.id.editTextBlogInfo);
        uploadButton = findViewById(R.id.uploadButton);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        uploadButton.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String info = editTextInfo.getText().toString().trim();

            if (title.isEmpty() || info.isEmpty()) {
                Toast.makeText(UploadBlogActivity.this, "Please fill out both title and information", Toast.LENGTH_SHORT).show();
                return;
            }

            BlogItem newItem = new BlogItem(R.drawable.f1, info, title);

            mItems.add(newItem)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(UploadBlogActivity.this, "Blog uploaded successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove(KEY_TITLE);
                        editor.remove(KEY_INFO);
                        editor.apply();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UploadBlogActivity.this, "Failed to upload blog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(LOG_TAG, "Error uploading blog", e);
                    });
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TITLE, editTextTitle.getText().toString());
        editor.putString(KEY_INFO, editTextInfo.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedTitle = prefs.getString(KEY_TITLE, "");
        String savedInfo = prefs.getString(KEY_INFO, "");
        editTextTitle.setText(savedTitle);
        editTextInfo.setText(savedInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.view_selector) {
            startActivity(new Intent(this, BlogListActivity.class));
            finish();
            return true;
        } else if (id == R.id.logout_button) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
