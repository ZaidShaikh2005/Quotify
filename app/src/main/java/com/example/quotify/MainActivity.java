package com.example.quotify;

import android.annotation.SuppressLint;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import com.google.android.material.color.DynamicColors;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView quoteText;
    private ImageButton favBtn;
    private QuoteManager quoteManager;
    private GestureDetector gestureDetector;
    private String currentQuote = "";
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        setTheme(R.style.Theme_Quotify);
        super.onCreate(savedInstanceState);
        splashScreen.setKeepOnScreenCondition(() -> isLoading);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> isLoading = false, 2000); // 3 seconds

        TextView greetingText = findViewById(R.id.greetingText);
        quoteText = findViewById(R.id.quoteText);
        favBtn = findViewById(R.id.favBtn);
        gestureDetector = new GestureDetector(this, new GestureListener());
        View cardArea = findViewById(R.id.centerWrapper);
        quoteManager = new QuoteManager(this);

        greetingText.setText(getGreeting());

        favBtn.setOnClickListener(v -> {
            if (quoteManager.isFavorite(currentQuote)) {
                quoteManager.removeFromFavorites(currentQuote);
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                quoteManager.addToFavorites(currentQuote);
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteIcon();
        });

        cardArea.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }

            return true;
        });

        findViewById(R.id.shareBtn).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, currentQuote);
            startActivity(Intent.createChooser(intent, "Share Quote"));
        });

        findViewById(R.id.fabFavorites).setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    private void updateFavoriteIcon() {
        if (quoteManager.isFavorite(currentQuote)) {
            favBtn.setImageResource(R.drawable.ic_favourite_2);
        } else {
            favBtn.setImageResource(R.drawable.ic_favourite);
        }
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) return "Good Morning";
        else if (hour >= 12 && hour < 17) return "Good Afternoon";
        else if (hour >= 17 && hour < 21) return "Good Evening";
        else return "Good Night";
    }

    @Override
    protected void onResume() {
        super.onResume();

        quoteManager.fetchQuote(new QuoteManager.QuoteCallback() {
            @Override
            public void onQuoteReceived(String quote) {
                currentQuote = quote;
                quoteText.setText(quote);
                updateFavoriteIcon();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(String error) {
                quoteText.setText("Unable to load quote.");
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @Nullable MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void onSwipeRight() {
        fetchNewQuote();
    }

    private void onSwipeLeft() {
        fetchNewQuote();
    }

    @SuppressLint("SetTextI18n")
    private void fetchNewQuote() {
        quoteText.setText("Loading new quote...");
        quoteManager.fetchQuote(new QuoteManager.QuoteCallback() {
            @Override
            public void onQuoteReceived(String quote) {
                currentQuote = quote;
                quoteText.setText(quote);
                updateFavoriteIcon();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(String error) {
                quoteText.setText("Couldn't load new quote.");
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
