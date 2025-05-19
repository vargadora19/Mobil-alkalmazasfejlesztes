package com.example.f1blog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlogItemAdapter extends RecyclerView.Adapter<BlogItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<BlogItem> mBlogItemsData;
    private ArrayList<BlogItem> mBlogItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public BlogItemAdapter(Context context, ArrayList<BlogItem> itemsData) {
        this.mBlogItemsData = itemsData;
        // Győződj meg róla, hogy mBlogItemsDataAll független példány legyen (ha szükséges a szűréshez)
        this.mBlogItemsDataAll = new ArrayList<>(itemsData);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.blog_list, parent, false));
    }

    @Override
    public void onBindViewHolder(BlogItemAdapter.ViewHolder holder, int position) {
        BlogItem currentItem = mBlogItemsData.get(position);
        holder.bindTo(currentItem);

        if (holder.getBindingAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mBlogItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return blogFilter;
    }

    private Filter blogFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<BlogItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mBlogItemsDataAll.size();
                results.values = mBlogItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BlogItem item : mBlogItemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mBlogItemsData = (ArrayList<BlogItem>) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private ImageView mItemImage;
        private Button btnUpdate; // Update gomb
        private Button btnDelete; // Delete gomb (ID: btnReadMore a layoutban)

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.postTitle);
            mInfoText = itemView.findViewById(R.id.postExcerpt);
            mItemImage = itemView.findViewById(R.id.postImage);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete); // layoutban a Delete gomb ID-je

            // Update gomb eseménykezelése
            btnUpdate.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    BlogItem currentItem = mBlogItemsData.get(pos);
                    showUpdateDialog(currentItem, pos);
                }
            });

            // Delete gomb eseménykezelése
            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    BlogItem currentItem = mBlogItemsData.get(pos);
                    // Töröljük a dokumentumot a Firestore-ból
                    FirebaseFirestore.getInstance().collection("Items")
                            .document(currentItem._getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                mBlogItemsData.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(mContext, "Blog deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(mContext, "Deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }

        public void bindTo(BlogItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }

        private void showUpdateDialog(BlogItem blogItem, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Update Blog");

            // Inflate a custom dialog layout
            View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog_update, (ViewGroup) itemView, false);
            EditText inputTitle = viewInflated.findViewById(R.id.editTextUpdateTitle);
            EditText inputInfo = viewInflated.findViewById(R.id.editTextUpdateInfo);

            inputTitle.setText(blogItem.getName());
            inputInfo.setText(blogItem.getInfo());
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String newTitle = inputTitle.getText().toString().trim();
                String newInfo = inputInfo.getText().toString().trim();

                if (newTitle.isEmpty() || newInfo.isEmpty()) {
                    Toast.makeText(mContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("name", newTitle);
                    updateMap.put("info", newInfo);

                    FirebaseFirestore.getInstance().collection("Items")
                            .document(blogItem._getId())
                            .update(updateMap)
                            .addOnSuccessListener(aVoid -> {
                                blogItem.setName(newTitle);
                                blogItem.setInfo(newInfo);
                                notifyItemChanged(position);
                                Toast.makeText(mContext, "Update successful", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext, "Update failed", Toast.LENGTH_SHORT).show());
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}
