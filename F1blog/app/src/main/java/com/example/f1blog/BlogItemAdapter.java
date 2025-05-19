package com.example.f1blog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BlogItemAdapter extends RecyclerView.Adapter<BlogItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<BlogItem> mBlogItemsData;
    private ArrayList<BlogItem> mBlogItemsDataAll;
    private Context mContext;
    private int lastPosition=-1;

    public BlogItemAdapter(Context context, ArrayList<BlogItem> itemsData) {
        this.mBlogItemsData=itemsData;
        this.mBlogItemsDataAll=itemsData;
        this.mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_blog, parent, false));
    }

    @Override
    public void onBindViewHolder(BlogItemAdapter.ViewHolder holder, int position) {
        BlogItem currentItem=mBlogItemsData.get(position);
        holder.bindTo(currentItem);

        if(holder.getBindingAdapterPosition()>lastPosition){
            Animation animation= AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition=holder.getBindingAdapterPosition();
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

    private Filter blogFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<BlogItem> filteredList=new ArrayList<>();
            FilterResults results=new FilterResults();

            if(constraint==null || constraint.length()==0){
                results.count=mBlogItemsDataAll.size();
                results.values=mBlogItemsDataAll;
            }else{
                String filterPattern=constraint.toString().toLowerCase().trim();
                for (BlogItem item: mBlogItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count=filteredList.size();
                results.values=filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mBlogItemsData=(ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private ImageView mItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText=itemView.findViewById(R.id.postTitle);
            mInfoText =itemView.findViewById(R.id.postExcerpt);
            mItemImage=itemView.findViewById(R.id.postImage);

            itemView.findViewById(R.id.btnReadMore).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity", "Show details");
                }
            });
        }

        public void bindTo(BlogItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }
    }
}
