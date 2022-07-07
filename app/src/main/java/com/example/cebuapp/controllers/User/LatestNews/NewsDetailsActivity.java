package com.example.cebuapp.controllers.User.LatestNews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.model.NewsArticles;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivity extends AppCompatActivity {
    private NewsArticles articles;
    private TextView news_title, news_author, news_time, news_detail;
    private ImageView news_img;
    private ImageButton backBtn;
    private Intent intent;
    private String mainSpinVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        castComponents();

        articles = (NewsArticles) getIntent().getSerializableExtra("data");
        mainSpinVal = (String) getIntent().getSerializableExtra("spinVal");
        Picasso.get().load(articles.getUrlToImage()).into(news_img);
        news_title.setText(articles.getTitle());
        news_author.setText(articles.getAuthor());
        news_time.setText(articles.getPublishedAt());
        news_detail.setText(articles.getDescription());

        // back btn
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v-> {
            intent = new Intent(new Intent(getApplicationContext(), NewsActivity.class));
            intent.putExtra("spinVal" , mainSpinVal);
            startActivity(intent);
        });
    }

    private void castComponents() {
        news_title = findViewById(R.id.show_news_title);
        news_author = findViewById(R.id.show_news_author);
        news_time = findViewById(R.id.show_news_time);
        news_detail = findViewById(R.id.show_news_detail);
        news_img = findViewById(R.id.show_news_img);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(new Intent(getApplicationContext(), NewsActivity.class));
        intent.putExtra("spinVal" , mainSpinVal);
        finish();
    }
}