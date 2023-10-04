package com.buzzware.monymarket.classes;


import com.buzzware.monymarket.Models.CountryCodeWithCurrencyModel;
import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.retrofit.currencyRes.CubeItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Constants {

    public static List<CountryCodeWithCurrencyModel> countryCodeWithCurrency = new ArrayList<>();
    public static List<String> spinnerCurrency = new ArrayList<>();
    public static List<CubeItem> currencyList = new ArrayList<>();
    public static String currencyApiResponseData = "";

    public static String selectedUserId = "";
    public static String selectedProId = "";
    public static String selectedConId = "";
    public static ProductModel editProductModel = new ProductModel();

    public static String getRate(String currencyName) {

        for (int i = 0; i < currencyList.size(); i++) {

            if (currencyList.get(i).getCurrency().equals(currencyName)) {
                return currencyList.get(i).getRate();
            }

        }

        return "";

    }

    public static String convert(int value, String currentCurrency, String outputCurrency) throws JSONException {

        Double currentCurrencyRate = 0.0;
        Double outputCurrencyRate = 0.0;

        JSONObject jObj = new JSONObject(currencyApiResponseData);
        JSONObject rateObg = jObj.getJSONObject("rates");

        String current = rateObg.getString(currentCurrency.toUpperCase());
        String output = rateObg.getString(outputCurrency.toUpperCase());


        currentCurrencyRate = Double.parseDouble(current);
        outputCurrencyRate = Double.parseDouble(output);


        Double multiplier = outputCurrencyRate / currentCurrencyRate;

        Double convertedValue = value * multiplier;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(convertedValue);


    }


}
