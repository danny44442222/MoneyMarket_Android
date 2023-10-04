package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TranferDataModel implements Parcelable {

    public String id = "";
    public String accountName = "";
    public String bankName = "";
    public String ibanNumber = "";
    public String proId = "";
    public String timeStamp = "";
    public String tranferAmount = "";
    public String transferCurrency = "";
    public String transferStatus = "";
    public String userID = "";

    public TranferDataModel(){

    }

    protected TranferDataModel(Parcel in) {
        id = in.readString();
        accountName = in.readString();
        bankName = in.readString();
        ibanNumber = in.readString();
        proId = in.readString();
        timeStamp = in.readString();
        tranferAmount = in.readString();
        transferCurrency = in.readString();
        transferStatus = in.readString();
        userID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accountName);
        dest.writeString(bankName);
        dest.writeString(ibanNumber);
        dest.writeString(proId);
        dest.writeString(timeStamp);
        dest.writeString(tranferAmount);
        dest.writeString(transferCurrency);
        dest.writeString(transferStatus);
        dest.writeString(userID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TranferDataModel> CREATOR = new Creator<TranferDataModel>() {
        @Override
        public TranferDataModel createFromParcel(Parcel in) {
            return new TranferDataModel(in);
        }

        @Override
        public TranferDataModel[] newArray(int size) {
            return new TranferDataModel[size];
        }
    };
}
