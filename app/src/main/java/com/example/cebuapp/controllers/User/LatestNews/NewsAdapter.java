package com.example.cebuapp.controllers.User.LatestNews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.model.NewsArticles;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private Context context;
    private List<NewsArticles> articles;
    private NewsSelectListener listener;

    public NewsAdapter(Context context, List<NewsArticles> articles, NewsSelectListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_list_item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.text_title.setText(articles.get(position).getTitle());
        holder.text_source.setText(articles.get(position).getSource().getName());

        if (articles.get(position).getUrlToImage() != null) {
            Picasso.get().load(articles.get(position).getUrlToImage()).into(holder.img_headline);
        }

        // card view click listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnNewsClicked(articles.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
