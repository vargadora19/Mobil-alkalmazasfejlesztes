package com.example.f1blog;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class BlogListActivity extends AppCompatActivity {
    private static final String LOG_TAG = BlogListActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<BlogItem> mItemList;
    private BlogItemAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int gridNumber = 1;

    private DocumentSnapshot lastVisibleDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> insets);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
            return;
        }
        Log.d(LOG_TAG, "Authenticated user");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();
        mAdapter = new BlogItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        scheduleNotificationJob();

        queryPopularBlogs();
        queryBlogsWithPagination();
        queryBlogsByCategory("Tech");
    }

    private void queryData() {
        mItemList.clear();
        mItems.orderBy("name")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        queryDocumentSnapshots.forEach(document -> {
                            BlogItem item = document.toObject(BlogItem.class);
                            item.setId(document.getId());
                            mItemList.add(item);
                        });
                    }

                    if (mItemList.isEmpty()) {
                        initializeData();
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "QUERY FAIL", e);
                    initializeData();
                });
    }

    private void initializeData() {
        Log.d(LOG_TAG, "initializeData() called");
        String[] itemsList = getResources().getStringArray(R.array.blog_items_name);
        String[] itemsInfo = getResources().getStringArray(R.array.blog_items_info);
        var itemsImageResource = getResources().obtainTypedArray(R.array.item_images);

        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new BlogItem(
                            itemsImageResource.getResourceId(i, 0),
                            itemsList[i],
                            itemsInfo[i]))
                    .addOnSuccessListener(r -> Log.d(LOG_TAG, "ADDED: " + r.getId()))
                    .addOnFailureListener(e -> Log.e(LOG_TAG, "ADD FAIL", e));
        }
        itemsImageResource.recycle();
    }

    //lekerdezes1:
    private void queryPopularBlogs() {
        mItems.whereGreaterThanOrEqualTo("views", 100)
                .orderBy("views", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(LOG_TAG, "Popular Blogs query succeeded, count: " + querySnapshot.size());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        querySnapshot.forEach(document -> {
                            BlogItem item = document.toObject(BlogItem.class);
                            Object viewsValue = document.get("views");
                            Log.d(LOG_TAG, "Found blog: " + item.getName() + ", views: " +
                                    (viewsValue != null ? viewsValue.toString() : "N/A"));
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Popular Blogs query failed", e));
    }


    //lekerdezes2:
    private void queryBlogsWithPagination() {
        Query query = mItems.orderBy("timestamp", Query.Direction.ASCENDING).limit(5);
        if (lastVisibleDocument != null) {
            query = mItems.orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(lastVisibleDocument)
                    .limit(5);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    if (!documents.isEmpty()) {
                        lastVisibleDocument = documents.get(documents.size() - 1);
                        Log.d(LOG_TAG, "Pagination query succeeded, count: " + documents.size());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            documents.forEach(document -> {
                                BlogItem item = document.toObject(BlogItem.class);
                                Log.d(LOG_TAG, "Page Blog: " + item.getName());
                            });
                        }
                    } else {
                        Log.d(LOG_TAG, "No more documents in pagination query.");
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Pagination query failed", e));
    }

    //lekerdezes3:
    private void queryBlogsByCategory(String category) {
        mItems.whereEqualTo("category", category)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(LOG_TAG, "Query by category succeeded, count: " + querySnapshot.size());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        querySnapshot.forEach(document -> {
                            BlogItem item = document.toObject(BlogItem.class);
                            Log.d(LOG_TAG, "Category Blog: " + item.getName() + " in category: " + category);
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Query by category failed", e));
    }

    //Job scheduler
    private void scheduleNotificationJob() {
        ComponentName componentName = new ComponentName(this, NotificationJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setMinimumLatency(60000)
                .setOverrideDeadline(120000)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            int result = jobScheduler.schedule(jobInfo);
            Log.d(LOG_TAG, "Job scheduled with result: " + result);
        } else {
            Log.e(LOG_TAG, "JobScheduler is null!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.new_blog) {
            Log.d(LOG_TAG, "New blog clicked!");
            startActivity(new Intent(this, UploadBlogActivity.class));
            return true;
        } else if (id == R.id.view_selector) {
            Log.d(LOG_TAG, "View clicked!");
            startActivity(new Intent(this, BlogListActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
