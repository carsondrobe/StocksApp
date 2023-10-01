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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currencySpinner = findViewById(R.id.currencySpinner);
        viewResults = (Button) findViewById(R.id.resultsBtn);
        tickerSymbolView = (EditText) findViewById(R.id.ticker);
        currencySpinner.setSelection(0);
        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ticker;
                ticker = (String) tickerSymbolView.getText().toString();
                retrieveCloseInfo(ticker);
            }
        });
    }

    protected void retrieveCloseInfo(String ticker) {
        tickerClose = findViewById(R.id.priceLabel);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?token=b8eeb0525ea6b650cb6915f3361fda61e7628ba0";

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
                    Stock[] stockDataArray = gson.fromJson(myResponse, Stock[].class);
                    String selectedCurrency = currencySpinner.getSelectedItem().toString();
                    String closeValue;
                    if (selectedCurrency.equals("USD")) {
                        closeValue = String.valueOf(stockDataArray[0].getClose());
                    } else if (selectedCurrency.equals("CAD")) {
                        double closeCAD = 1.35 * stockDataArray[0].getClose();
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