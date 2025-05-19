package com.example.f1blog;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BlogListActivity extends AppCompatActivity {
    private static final String LOG_TAG= BlogListActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<BlogItem> mItemList;
    private BlogItemAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int gridNumber=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blog_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.d(LOG_TAG, "Authenticated user");
        }else{
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList=new ArrayList<>();
        mAdapter=new BlogItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore=FirebaseFirestore.getInstance();
        mItems=mFirestore.collection("Items");

        queryData();
    }

    private void queryData() {
        mItemList.clear();
        mItems.orderBy("name").limit(10).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BlogItem item = document.toObject(BlogItem.class);
                        mItemList.add(item);
                    }
                    if (mItemList.size() == 0) {
                        initializeData(); // első indításkor feltölt
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "QUERY FAIL", e);
                    initializeData(); // ha hiba, akkor is próbálj feltölteni
                });
    }

    private void initializeData() {
        Log.d(LOG_TAG, "initializeData() called");

        String[] itemsList = getResources().getStringArray(R.array.blog_items_name);
        String[] itemsInfo = getResources().getStringArray(R.array.blog_items_info);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.item_images);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.blog_menu, menu);

        MenuItem menuItem=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        } else if (id == R.id.settings_button) {
            Log.d(LOG_TAG, "Settings clicked!");
            return true;
        } else if (id == R.id.new_blog) {
            Log.d(LOG_TAG, "New blog clicked!");
            return true;
        } else if (id == R.id.search_bar) {
            Log.d(LOG_TAG, "Search clicked!");
            return true;
        } else if (id == R.id.view_selector) {
            Intent homeIntent = new Intent(this, BlogListActivity.class);
            startActivity(homeIntent);
            Log.d(LOG_TAG, "View clicked!");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
