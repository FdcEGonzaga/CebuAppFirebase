package com.example.cebuapp.controllers.User.LatestNews;

import android.content.Context;
import android.widget.Toast;

import com.example.cebuapp.R;
import com.example.cebuapp.model.NewsApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NewsRequestManager {
    Context context;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadlines(OnFetchNewsDataListener listener, String category, String query) {
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsApiResponse> call = callNewsApi.callHeadlines("ph", category, query, context.getString(R.string.news_api_key));

        try {
            call.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "Error news api response", Toast.LENGTH_LONG).show();
                    }

                    listener.onfetchData(response.body().getArticles(), response.message());
                }

                @Override
                public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                    listener.onError("Error requesting news.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NewsRequestManager(Context context) {
        this.context = context;
    }

    public interface CallNewsApi {
        @GET("top-headlines")
        Call<NewsApiResponse> callHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("q") String query,
            @Query("apiKey") String apiKey

        );
    }
}
