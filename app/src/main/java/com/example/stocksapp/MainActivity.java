package com.example.stocksapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

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

    private TextView SnP500Close;
    private Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currencySpinner = findViewById(R.id.currencySpinner);
        retrieveCloseInfo();
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                retrieveCloseInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                retrieveCloseInfo();
            }
        });
    }

    protected void retrieveCloseInfo() {
        SnP500Close = findViewById(R.id.priceLabel);

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "https://api.tiingo.com/tiingo/daily/SPY/prices?token=b8eeb0525ea6b650cb6915f3361fda61e7628ba0";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
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
                        closeValue = String.valueOf(1.35 * stockDataArray[0].getClose());
                    } else {
                        closeValue = "Error";
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SnP500Close.setText(closeValue);
                        }
                    });
                }
            }
        });
    }
}