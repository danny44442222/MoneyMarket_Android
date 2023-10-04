package com.buzzware.monymarket.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OfferModel implements Parcelable {

    public String id;
    public Long exchangeAmount;
    public String exchangeCurrency = "";
    public String imageUrl = "";
    public Long proAmount;
    public String proCurrency = "";
    public String proId = "";
    public String proName = "";
    public String senderID = "";
    public String senderName="";
    public String userID="";
    public String ConversationID="";


    public OfferModel() {
    }

    protected OfferModel(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            exchangeAmount = null;
        } else {
            exchangeAmount = in.readLong();
        }
        exchangeCurrency = in.readString();
        imageUrl = in.readString();
        if (in.readByte() == 0) {
            proAmount = null;
        } else {
            proAmount = in.readLong();
        }
        proCurrency = in.readString();
        proId = in.readString();
        proName = in.readString();
        senderID = in.readString();
        senderName = in.readString();
        userID = in.readString();
        ConversationID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (exchangeAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(exchangeAmount);
        }
        dest.writeString(exchangeCurrency);
        dest.writeString(imageUrl);
        if (proAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(proAmount);
        }
        dest.writeString(proCurrency);
        dest.writeString(proId);
        dest.writeString(proName);
        dest.writeString(senderID);
        dest.writeString(senderName);
        dest.writeString(userID);
        dest.writeString(ConversationID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OfferModel> CREATOR = new Creator<OfferModel>() {
        @Override
        public OfferModel createFromParcel(Parcel in) {
            return new OfferModel(in);
        }

        @Override
        public OfferModel[] newArray(int size) {
            return new OfferModel[size];
        }
    };
}