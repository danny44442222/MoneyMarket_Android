package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.buzzware.monymarket.Models.CountryCodeWithCurrencyModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.classes.Constants;

public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        setView();
        addDataInList();
        addDataInSpinnerList();
        startHandler();

    }
    private void setView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

    }

    private void addDataInList() {

        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("NGN", "NG"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("GHS", "GH"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("KES", "KE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("UGX", "UG"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("ZAR", "ZA"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("TZS", "TZ"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("AUD", "AU"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("CHF", "LI"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "AT"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "LT"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "BE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "LU"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("BRL", "BR"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("MYR", "MY"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("BGN", "BG"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "MT"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("CAD", "CA"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("MXN", "MX"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("HRK", "HR"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "NL"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "CY"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("NZD", "NZ"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("NOK", "NO"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("DKK", "DK"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("PLN", "PL"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "EE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "PT"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "FI"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("RON", "RO"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "FR"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("SGD", "SG"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "DE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "SK"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "SI"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "GR"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "ES"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("HKD", "HK"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "SE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("HUF", "HU"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("CHF", "CH"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("THB", "TH"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "IE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("AED", "AE"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "IT"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "GB"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("USD", "US"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("JPY", "JP"));
        Constants.countryCodeWithCurrency.add(new CountryCodeWithCurrencyModel("EUR", "LV"));

    }

    private void addDataInSpinnerList() {

        Constants.spinnerCurrency.add("NGN");
        Constants.spinnerCurrency.add("GHS");
        Constants.spinnerCurrency.add("KES");
        Constants.spinnerCurrency.add("UGX");
        Constants.spinnerCurrency.add("ZAR");
        Constants.spinnerCurrency.add("TZS");
        Constants.spinnerCurrency.add("AUD");
        Constants.spinnerCurrency.add("CHF");
        Constants.spinnerCurrency.add("EUR");
        Constants.spinnerCurrency.add("BRL");
        Constants.spinnerCurrency.add("MYR");
        Constants.spinnerCurrency.add("BGN");
        Constants.spinnerCurrency.add("CAD");
        Constants.spinnerCurrency.add("MXN");
        Constants.spinnerCurrency.add("HRK");
        Constants.spinnerCurrency.add("NZD");
        Constants.spinnerCurrency.add("NOK");
        Constants.spinnerCurrency.add("DKK");
        Constants.spinnerCurrency.add("PLN");
        Constants.spinnerCurrency.add("RON");
        Constants.spinnerCurrency.add("SGD");
        Constants.spinnerCurrency.add("HKD");
        Constants.spinnerCurrency.add("HUF");
        Constants.spinnerCurrency.add("CHF");
        Constants.spinnerCurrency.add("THB");
        Constants.spinnerCurrency.add("AED");
        Constants.spinnerCurrency.add("USD");
        Constants.spinnerCurrency.add("JPY");

    }


    private void startHandler() {

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, StartUpActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }

        }, 3000);

    }

}