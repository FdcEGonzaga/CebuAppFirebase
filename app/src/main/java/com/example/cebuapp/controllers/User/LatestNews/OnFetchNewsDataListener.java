package com.example.cebuapp.controllers.User.LatestNews;

import com.example.cebuapp.model.NewsArticles;

import java.util.List;

public interface OnFetchNewsDataListener<NewsApiResponse> {

    void onfetchData(List<NewsArticles> list, String message);
    void onError(String message);
}
