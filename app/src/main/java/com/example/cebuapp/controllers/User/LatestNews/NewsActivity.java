package com.example.cebuapp.controllers.User.LatestNews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.model.NewsApiResponse;
import com.example.cebuapp.model.NewsArticles;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements NewsSelectListener, View.OnClickListener{
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ProgressDialog dialog;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private SearchView searchView;
    private NewsRequestManager manager;
    private ImageButton backBtn;
    private Intent intent;

    // main spinner
    private Spinner mainSpinner;
    private DatabaseReference mainSpinnerRef;
    private ArrayList<String> mainSpinnerList;
    private ArrayAdapter<String> mainSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // set manager
        manager = new NewsRequestManager(this);
        String defaultCategory = "GENERAL";
        manager.getNewsHeadlines(listener, defaultCategory, null);

        // set dialogs
        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching news articles");
        dialog.show();

        castComponents();
        searchWordListener();
        setBtnListeners();

        // main spinner
        setMainSpinner();

        // back btn
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v-> {
            intent = new Intent(NewsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void setMainSpinner() {
//        mainSpinnerRef = FirebaseDatabase.getInstance().getReference();
        mainSpinner = findViewById(R.id.main_spinner);
        mainSpinnerList = new ArrayList<>();
        mainSpinnerList.add("ALL");
        mainSpinnerList.add("ENTERTAINMENT");
        mainSpinnerList.add("BUSINESS");
        mainSpinnerList.add("HEALTH");
        mainSpinnerList.add("SCIENCE");
        mainSpinnerList.add("TECHNOLOGY");
        mainSpinnerList.add("SPORTS");
        mainSpinnerAdapter = new ArrayAdapter<String>(NewsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, mainSpinnerList);
        mainSpinner.setAdapter(mainSpinnerAdapter);

        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = mainSpinner.getSelectedItem().toString();
                dialog.setTitle("Fetching " + selectedCategory + " news articles.");
                dialog.show();

                // set all to general before calling api
                selectedCategory = selectedCategory.equals("ALL") ? "GENERAL" : selectedCategory;

                // call api
                manager.getNewsHeadlines(listener, selectedCategory, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void castComponents() {
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        searchView = findViewById(R.id.search_view);
    }

    private void setBtnListeners() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
    }

    private final OnFetchNewsDataListener<NewsApiResponse> listener = new OnFetchNewsDataListener<NewsApiResponse>() {
        @Override
        public void onfetchData(List<NewsArticles> list, String message) {
            if (list.isEmpty()) {
                Toast.makeText(NewsActivity.this, "Error: No data found." , Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                showNews(list);
                dialog.dismiss();
            }
        }

        @Override
        public void onError(String message) {
            dialog.dismiss();
            Toast.makeText(NewsActivity.this, "Error: Unable to connect to the internet.", Toast.LENGTH_LONG).show();
        }
    };

    private void showNews(List<NewsArticles> list) {
        recyclerView = findViewById(R.id.newslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new NewsAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsArticles articles) {
        startActivity(new Intent(NewsActivity.this, NewsDetailsActivity.class)
                .putExtra("data", articles));
    }

    private void searchWordListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.setTitle("Searching for " + query + "...");
                dialog.show();

                NewsRequestManager manager = new NewsRequestManager(NewsActivity.this);
                manager.getNewsHeadlines(listener, "general", query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String category = button.getText().toString();
        dialog.setTitle("Fetching " + category + " news articles.");
        dialog.show();

        // call api
        NewsRequestManager manager = new NewsRequestManager(this);
        manager.getNewsHeadlines(listener, category, null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), HomeActivity.class)));
        finish();
    }
}