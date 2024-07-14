package com.example.ubc_bookstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    List<Book> bookList;
    Context context;
    public RecycleViewAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_view_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i("MyMsg", "Holder Title = " + bookList.get(position).getTitle());
        holder.tvTitle.setText(bookList.get(position).getTitle());
        Glide.with(this.context).load(bookList.get(position).getImageURL()).into(holder.ivBookCover);

        holder.ivBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use holder.getAdapterPosition() to get the latest position
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ProductView.class);
                    intent.putExtra("isbn", bookList.get(adapterPosition).getISBN());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView ivBookCover;
        ConstraintLayout parentLayout;
        TextView tvTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            parentLayout = itemView.findViewById(R.id.oneLineBookLayout);

        }
    }
}
