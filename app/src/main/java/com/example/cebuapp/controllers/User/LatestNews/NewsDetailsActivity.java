package com.example.cebuapp.controllers.User.LatestNews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.example.cebuapp.model.NewsArticles;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivity extends AppCompatActivity {
    private NewsArticles articles;
    private TextView news_title, news_author, news_time, news_detail, news_content;
    private ImageView news_img;
    private ImageButton backBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        castComponents();

        articles = (NewsArticles) getIntent().getSerializableExtra("data");
        news_title.setText(articles.getTitle());
        news_author.setText(articles.getAuthor());
        news_time.setText(articles.getPublishedAt());
        news_detail.setText(articles.getDescription());
        Picasso.get().load(articles.getUrlToImage()).into(news_img);

        // back btn
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v-> {
            intent = new Intent(NewsDetailsActivity.this, NewsActivity.class);
            startActivity(intent);
        });
    }

    private void castComponents() {
        news_title = findViewById(R.id.show_news_title);
        news_author = findViewById(R.id.show_news_author);
        news_time = findViewById(R.id.show_news_time);
        news_detail = findViewById(R.id.show_news_detail);
        news_content = findViewById(R.id.show_news_content);
        news_img = findViewById(R.id.show_news_img);
    }
}