package com.example.stocksapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tickerClose;
    private Spinner currencySpinner;

    private Button viewResults;

    private EditText tickerSymbolView;
    private final double USDtoCADConversion = 1.35;
    private final String tiingoApiKey = BuildConfig.TIINGO_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupClickListeners();
    }
    private void initializeViews(){
        currencySpinner = findViewById(R.id.currencySpinner);
        viewResults = (Button) findViewById(R.id.resultsBtn);
        tickerSymbolView = (EditText) findViewById(R.id.ticker);
        tickerClose = findViewById(R.id.priceLabel);
        currencySpinner.setSelection(0);
    }
    private void setupClickListeners(){
        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tickerSymbol;
                tickerSymbol = (String) tickerSymbolView.getText().toString();
                retrieveCloseInfo(tickerSymbol);
            }
        });
    }

    protected void retrieveCloseInfo(String tickerSymbol) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "https://api.tiingo.com/tiingo/daily/" + tickerSymbol + "/prices?token=" + tiingoApiKey;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                tickerClose.setText("Ticker Unavailable");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Gson gson = new Gson();
                    //Store End-Of-Day stock data in array
                    EODStockData[] stockDataArray = gson.fromJson(myResponse, EODStockData[].class);
                    String selectedCurrency = currencySpinner.getSelectedItem().toString();
                    String closeValue;
                    if (selectedCurrency.equals("USD")) {
                        closeValue = String.valueOf(stockDataArray[0].getClose());
                    } else if (selectedCurrency.equals("CAD")) {
                        double closeCAD = USDtoCADConversion * stockDataArray[0].getClose();
                        closeValue = String.format("%.2f", closeCAD);
                    } else {
                        closeValue = "Error";
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tickerClose.setText(closeValue);
                        }
                    });
                } else if (response.code() == 404) {
                    // Handle a 404 Not Found error
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tickerClose.setText("###.##");
                            Toast.makeText(getApplicationContext(), "Sorry, Ticker Unavailable at This Time",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}