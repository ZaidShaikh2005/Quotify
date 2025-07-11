package com.example.quotify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.QuoteViewHolder> {

    private final List<String> allQuotes;
    private final List<String> filteredQuotes;
    private final Context context;
    private final QuoteManager quoteManager;

    public FavoriteAdapter(List<String> quotes, Context context, QuoteManager manager) {
        this.allQuotes = quotes;
        this.filteredQuotes = new ArrayList<>(quotes);
        this.context = context;
        this.quoteManager = manager;
    }

    /** @noinspection ClassEscapesDefinedScope*/
    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new QuoteViewHolder(view);
    }

    /** @noinspection ClassEscapesDefinedScope*/
    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        String quote = filteredQuotes.get(position);
        holder.quoteText.setText(quote);

        holder.unfavoriteButton.setOnClickListener(v -> {
            quoteManager.removeFromFavorites(quote);
            allQuotes.remove(quote);
            filterQuotes(""); // refresh list
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();

            if (getItemCount() == 0 && context instanceof FavoritesActivity) {
                ((FavoritesActivity) context).showEmptyMessage();
            }
        });

        holder.shareButton.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, quote);
            context.startActivity(Intent.createChooser(share, "Share Quote via"));
        });
    }

    @Override
    public int getItemCount() {
        return filteredQuotes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterQuotes(String query) {
        filteredQuotes.clear();
        if (query.isEmpty()) {
            filteredQuotes.addAll(allQuotes);
        } else {
            String lower = query.toLowerCase();
            for (String q : allQuotes) {
                if (q.toLowerCase().contains(lower)) {
                    filteredQuotes.add(q);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView quoteText;
        ImageButton unfavoriteButton, shareButton;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quoteText);
            unfavoriteButton = itemView.findViewById(R.id.unfavoriteButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}