package com.example.ubc_bookstore;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewCardListAdapter extends RecyclerView.Adapter<ReviewCardListAdapter.ViewHolder>{
    private ArrayList <ReviewCardData> reviewdata;
    public ReviewCardListAdapter(ArrayList <ReviewCardData> reviewdata) {
        this.reviewdata = reviewdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.reviewcardslist_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ReviewCardData reviewCardData = reviewdata.get(position);
        holder.headerView.setText(reviewdata.get(position).getUserinfo());
        holder.reviewView.setText(reviewdata.get(position).getReviewtxt());
        holder.ratingBar.setRating(reviewdata.get(position).getUserrating());

    }

    @Override
    public int getItemCount() {
        return reviewdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView headerView;
        public TextView reviewView;
        public RatingBar ratingBar;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.headerView = (TextView) itemView.findViewById(R.id.headingtextView);
            this.reviewView = itemView.findViewById(R.id.reviewtextView);
            this.ratingBar = itemView.findViewById(R.id.ratingBar3);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
