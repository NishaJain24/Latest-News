package com.nishajain.tidings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nishajain.tidings.Models.ArticlesModel;
import com.nishajain.tidings.Models.HeadlinesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    final String API_KEY = "5e2fe8d6a8e740bbb6b66bd6eca7f5ee";
    Adapter adapter;
    List<ArticlesModel> articlesModels = new ArrayList<>();
    SwipeRefreshLayout swipe;
    Button search;
    EditText etquery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.recycler_view);
        swipe = findViewById(R.id.swipe);
        search = findViewById(R.id.search);
        etquery = findViewById(R.id.etQuery);
        etquery.clearFocus();
//        getSupportActionBar().hide();
        rv.setLayoutManager(new LinearLayoutManager(this));
        String country = getCountry();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("", country, API_KEY);
            }
        });
        retrieveJson("", country, API_KEY);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etquery.clearFocus();
                if (!etquery.getText().toString().equals("")) {
                    swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson(etquery.getText().toString(), country, API_KEY);
                        }
                    });
                    retrieveJson(etquery.getText().toString(), country, API_KEY);
                } else {
                    swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson("", country, API_KEY);
                        }
                    });
                    retrieveJson("", country, API_KEY);

                }
            }
        });
    }

    public void retrieveJson(String query, String country, String apiKey) {
        swipe.setRefreshing(true);
        Call<HeadlinesModel> call;
        if (!etquery.getText().toString().equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);

        } else {
            call = ApiClient.getInstance().getApi().getHeadlines(country, apiKey);

        }
//        Call<HeadlinesModel> call = ApiClient.getInstance().getApi().get(country,apiKey);
        call.enqueue(new Callback<HeadlinesModel>() {
            @Override
            public void onResponse(Call<HeadlinesModel> call, Response<HeadlinesModel> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    swipe.setRefreshing(false);
                    articlesModels.clear();
                    articlesModels = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this, articlesModels);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<HeadlinesModel> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();

    }
}