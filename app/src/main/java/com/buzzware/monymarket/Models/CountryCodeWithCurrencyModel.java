package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class CountryCodeWithCurrencyModel {

    public String code;
    public String currency;

    public CountryCodeWithCurrencyModel( String currency,String code) {
        this.code = code;
        this.currency = currency;
    }
}