package com.example.quotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class QuoteManager {

    private static final String API_URL = "https://api.api-ninjas.com/v1/quotes";
    private static final String API_KEY = "DvwQqBElHJbY87XqIVTw8Q==9ZFn8yFMkTHA9Di0\n";
    private static final String PREFS = "quote_prefs";
    private static final String FAVORITES = "favorites";
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    public QuoteManager(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public interface QuoteCallback {
        void onQuoteReceived(String quote);
        void onError(String error);
    }

    public void fetchQuote(QuoteCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Api-Key", API_KEY);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(builder.toString());
                    JSONObject quoteObj = jsonArray.getJSONObject(0);
                    String quote = quoteObj.getString("quote");
                    String author = quoteObj.getString("author");
                    String fullQuote = "\"" + quote + "\"\n– " + author;

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onQuoteReceived(fullQuote));

                } else {
                    throw new IOException("HTTP " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Failed to fetch quote."));
            }
        }).start();
    }
    public void addToFavorites(String quote) {
        Set<String> favs = getFavorites();
        favs.add(quote);
        saveFavorites(favs);
    }

    public void removeFromFavorites(String quote) {
        Set<String> favs = getFavorites();
        favs.remove(quote);
        saveFavorites(favs);
    }

    public boolean isFavorite(String quote) {
        return getFavorites().contains(quote);
    }

    public Set<String> getFavorites() {
        String json = prefs.getString(FAVORITES, null);
        if (json != null) {
            Type type = new TypeToken<Set<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new HashSet<>();
    }

    private void saveFavorites(Set<String> favs) {
        String json = gson.toJson(favs);
        prefs.edit().putString(FAVORITES, json).apply();
    }
}
