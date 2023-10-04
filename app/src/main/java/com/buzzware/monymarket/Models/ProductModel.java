package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductModel implements Parcelable {

    public String proId;
    public String proCurrency;
    public String proDescription = "";
    public Long proExchnagePrice;
    public String proImageUrls = "";
    public Long proQuantity;
    public String proTitle = "";
    public String proexchangeCurr;
    public Long timeStamp;
    public String userId = "";

    public ProductModel() {

    }

    protected ProductModel(Parcel in) {
        proId = in.readString();
        proCurrency = in.readString();
        proDescription = in.readString();
        if (in.readByte() == 0) {
            proExchnagePrice = null;
        } else {
            proExchnagePrice = in.readLong();
        }
        proImageUrls = in.readString();
        if (in.readByte() == 0) {
            proQuantity = null;
        } else {
            proQuantity = in.readLong();
        }
        proTitle = in.readString();
        proexchangeCurr = in.readString();
        if (in.readByte() == 0) {
            timeStamp = null;
        } else {
            timeStamp = in.readLong();
        }
        userId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(proId);
        dest.writeString(proCurrency);
        dest.writeString(proDescription);
        if (proExchnagePrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(proExchnagePrice);
        }
        dest.writeString(proImageUrls);
        if (proQuantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(proQuantity);
        }
        dest.writeString(proTitle);
        dest.writeString(proexchangeCurr);
        if (timeStamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timeStamp);
        }
        dest.writeString(userId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };
}