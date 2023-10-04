package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class WalletModel implements Parcelable {

    public String walletId;
    public String clientSecret="";
    public String imageUrl="";
    public Long proAmount;
    public String proCurrency="";
    public String proId="";
    public String proName="";
    public Long timeStamp;
    public String userID="";

    public WalletModel() {
    }

    protected WalletModel(Parcel in) {
        walletId = in.readString();
        clientSecret = in.readString();
        imageUrl = in.readString();
        if (in.readByte() == 0) {
            proAmount = null;
        } else {
            proAmount = in.readLong();
        }
        proCurrency = in.readString();
        proId = in.readString();
        proName = in.readString();
        if (in.readByte() == 0) {
            timeStamp = null;
        } else {
            timeStamp = in.readLong();
        }
        userID = in.readString();
    }

    public static final Creator<WalletModel> CREATOR = new Creator<WalletModel>() {
        @Override
        public WalletModel createFromParcel(Parcel in) {
            return new WalletModel(in);
        }

        @Override
        public WalletModel[] newArray(int size) {
            return new WalletModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(walletId);
        parcel.writeString(clientSecret);
        parcel.writeString(imageUrl);
        if (proAmount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(proAmount);
        }
        parcel.writeString(proCurrency);
        parcel.writeString(proId);
        parcel.writeString(proName);
        if (timeStamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(timeStamp);
        }
        parcel.writeString(userID);
    }
}
