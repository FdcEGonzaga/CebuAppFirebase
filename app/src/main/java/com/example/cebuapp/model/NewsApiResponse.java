package com.example.cebuapp.model;

import java.io.Serializable;
import java.util.List;

public class NewsApiResponse implements Serializable {
    private String status;
    private int totalResults;
    List<NewsArticles> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<NewsArticles> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticles> articles) {
        this.articles = articles;
    }
}
