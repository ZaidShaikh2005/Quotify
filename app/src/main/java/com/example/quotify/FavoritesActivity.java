package com.example.quotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesActivity extends Activity {

    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        QuoteManager quoteManager = new QuoteManager(this);
        Set<String> favoritesSet = quoteManager.getFavorites();
        List<String> favoriteQuotes = new ArrayList<>(favoritesSet);

        RecyclerView favRecycler = findViewById(R.id.favRecycler);
        favRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter(favoriteQuotes, this, quoteManager);
        favRecycler.setAdapter(adapter);

        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterQuotes(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView emptyTitle = findViewById(R.id.emptyTitle);
        emptyTitle.setVisibility(favoriteQuotes.isEmpty() ? View.VISIBLE : View.GONE);

        TextView emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setVisibility(favoriteQuotes.isEmpty() ? View.VISIBLE : View.GONE);

        Button viewQuotesButton = findViewById(R.id.viewQuotes);
        viewQuotesButton.setVisibility(favoriteQuotes.isEmpty() ? View.VISIBLE : View.GONE);

        viewQuotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void showEmptyMessage() {
            findViewById(R.id.emptyTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);
            findViewById(R.id.viewQuotes).setVisibility(View.VISIBLE);
    }
}