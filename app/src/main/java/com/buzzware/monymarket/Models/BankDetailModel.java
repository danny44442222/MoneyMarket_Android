package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BankDetailModel implements Parcelable {


    public String userID;
    public String ibanNumber;
    public String bankName;
    public String accountName;

    public BankDetailModel(){

    }

    protected BankDetailModel(Parcel in) {

        userID = in.readString();
        ibanNumber = in.readString();
        bankName = in.readString();
        accountName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(userID);
        dest.writeString(ibanNumber);
        dest.writeString(bankName);
        dest.writeString(accountName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BankDetailModel> CREATOR = new Creator<BankDetailModel>() {
        @Override
        public BankDetailModel createFromParcel(Parcel in) {
            return new BankDetailModel(in);
        }

        @Override
        public BankDetailModel[] newArray(int size) {
            return new BankDetailModel[size];
        }
    };
}